import csv
import json
import re
from collections import defaultdict
from pathlib import Path
from statistics import mean
from datetime import datetime

SINTETICO_DIR = Path("/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/solutions")
REAL_DIR = Path("/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/realCaseTest/solutions")
OUTPUT_DIR = Path("/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/analisis_resultados_py/worked_days_output")


def get_algorithm_from_filename(path: Path) -> str:
    name = path.stem.lower()

    if "compacting" in name:
        return "CompactingSolver"
    if "random" in name:
        return "RandomSolver"
    if "reference" in name:
        return "ReferenceSolver"


def get_agents_from_filename(path: Path):
    name = path.stem.lower()

    match = re.search(r"agents(\d+)", name)
    if match:
        return int(match.group(1))

    match = re.search(r"_(\d+)_agents_", name)
    if match:
        return int(match.group(1))

    return ""

def parse_datetime(value: str):
    if not value:
        return None

    value = value.replace("Z", "+00:00")

    try:
        return datetime.fromisoformat(value)
    except ValueError:
        return None

def get_time_per_day_from_filename(path: Path):
    name = path.stem.lower()

    match = re.search(r"timeperday[-_]?(\d+)", name)
    if match:
        return int(match.group(1))

    return ""


def get_demand_level_from_filename(path: Path):
    name = path.stem.lower()

    match = re.search(r"-(0(?:\.\d+)?|1(?:\.0)?)-", name)
    if match:
        return match.group(1)

    return ""


def get_instance_from_filename(path: Path):
    name = path.stem.lower()

    match = re.search(r"instance[-_]?(\d+)", name)
    if match:
        return int(match.group(1))

    return ""


def parse_filename(path: Path, experiment_type: str) -> dict:
    if experiment_type == "real":
        airport = "MAD"
    else:
        airport = path.stem.split("-")[0]

    return {
        "file": path.name,
        "airport": airport,
        "algorithm": get_algorithm_from_filename(path),
        "agents_available": get_agents_from_filename(path),
        "time_per_day": get_time_per_day_from_filename(path),
        "demand_level": get_demand_level_from_filename(path),
        "instance": get_instance_from_filename(path),
    }


def get_employee_identifier(employee: dict) -> str:
    preferred_keys = [
        "code",
        "name",
        "employeeName",
        "employee_name",
        "employee",
        "workerName",
        "worker_name",
        "agentName",
        "agent_name",
        "id",
        "employeeId",
        "employee_id",
        "workerId",
        "worker_id",
        "agentId",
        "agent_id",
    ]

    for key in preferred_keys:
        value = employee.get(key)
        if value not in (None, ""):
            return str(value)

    ignored_keys = {
        "services",
        "indicators",
        "assignments",
        "tasks",
    }

    for key, value in employee.items():
        if key in ignored_keys:
            continue

        if isinstance(value, (str, int, float)):
            return str(value)

    return ""


def parse_service_time_to_minutes(service_time: str) -> float:
    if not service_time:
        return 0

    match = re.match(r"PT(?:(\d+)H)?(?:(\d+)M)?(?:(\d+)S)?", service_time)

    if not match:
        return 0

    hours = int(match.group(1)) if match.group(1) else 0
    minutes = int(match.group(2)) if match.group(2) else 0
    seconds = int(match.group(3)) if match.group(3) else 0

    return hours * 60 + minutes + seconds / 60


def calculate_worked_days(solution: dict):
    dates = solution.get("dates")

    if not isinstance(dates, list):
        raise ValueError("No es un JSON de solución porque no tiene la clave dates.")

    employee_days = defaultdict(set)
    employee_minutes = defaultdict(float)
    employee_used_minutes = defaultdict(float)
    horizon_dates = set()

    for day_block in dates:
        date = str(day_block.get("date", ""))[:10]

        if not date:
            continue

        horizon_dates.add(date)

        for employee in day_block.get("employees", []):
            employee_id = get_employee_identifier(employee)
            services = employee.get("services", [])

            real_services = [
                service for service in services
                if not str(service.get("code", "")).startswith("f-")
            ]

            if employee_id and real_services:
                employee_days[employee_id].add(date)

                # Tiempo efectivo de servicios reales, sin huecos.
                for service in real_services:
                    employee_minutes[employee_id] += parse_service_time_to_minutes(
                        service.get("serviceTime", "")
                    )

                # Tiempo usado: desde el primer servicio real hasta el último servicio real del día.
                real_starts = [
                    parse_datetime(service.get("startTime", ""))
                    for service in real_services
                ]

                real_finishes = [
                    parse_datetime(service.get("finishTime", ""))
                    for service in real_services
                ]

                real_starts = [value for value in real_starts if value is not None]
                real_finishes = [value for value in real_finishes if value is not None]

                if real_starts and real_finishes:
                    used_minutes = (max(real_finishes) - min(real_starts)).total_seconds() / 60
                    employee_used_minutes[employee_id] += used_minutes

    horizon_days = len(horizon_dates)
    days_values = [len(days) for days in employee_days.values()]

    employee_rows = []

    for employee_id, worked_dates in sorted(employee_days.items()):
        days_worked = len(worked_dates)
        total_used_hours = employee_used_minutes[employee_id] / 60

        employee_rows.append({
            "employee_id": employee_id,
            "days_worked": days_worked,
            "total_used_hours": round(total_used_hours, 4),
            "avg_hours_per_worked_day": round(total_used_hours / days_worked, 4) if days_worked else 0,
        })

    if not days_values:
        summary = {
            "horizon_days": horizon_days,
            "active_employees": 0,
            "employee_days": 0,
            "avg_days_per_active": 0,
            "avg_hours_per_active_employee": 0,
            "avg_hours_per_worked_day": 0,
        }

        return summary, employee_rows

    active_employees = len(days_values)
    employee_days_total = sum(days_values)

    total_work_hours = sum(employee_minutes.values()) / 60
    total_used_hours = sum(employee_used_minutes.values()) / 60

    summary = {
        "horizon_days": horizon_days,
        "active_employees": active_employees,
        "employee_days": employee_days_total,
        "avg_days_per_active": round(mean(days_values), 4),
        "avg_hours_per_active_employee": round(total_work_hours / active_employees, 4),
        "avg_hours_per_worked_day": round(total_used_hours / employee_days_total, 4) if employee_days_total else 0,
    }

    return summary, employee_rows

def process_folder(folder: Path, experiment_type: str):
    summary_rows = []
    employee_rows = []

    for path in sorted(folder.rglob("*.json")):
        try:
            with path.open("r", encoding="utf-8") as f:
                solution = json.load(f)

            summary, employees = calculate_worked_days(solution)
            metadata = parse_filename(path, experiment_type)

            summary_rows.append({
                "experiment_type": experiment_type,
                **metadata,
                **summary,
            })

            for employee in employees:
                employee_rows.append({
                    "experiment_type": experiment_type,
                    **metadata,
                    **employee,
                })

        except ValueError:
            continue

        except Exception as e:
            print(f"Error procesando {path.name}: {e}")

    print(f"{experiment_type}: {len(summary_rows)} soluciones procesadas")
    return summary_rows, employee_rows


def write_tsv(path: Path, rows: list[dict]):
    if not rows:
        print(f"No se generó {path}, porque no hay datos.")
        return

    path.parent.mkdir(parents=True, exist_ok=True)

    fieldnames = list(rows[0].keys())

    with path.open("w", encoding="utf-8", newline="") as f:
        writer = csv.DictWriter(f, fieldnames=fieldnames, delimiter="\t")
        writer.writeheader()
        writer.writerows(rows)

    print(f"Tabla generada: {path}")


def main():
    sintetico_summary, sintetico_employees = process_folder(
        folder=SINTETICO_DIR,
        experiment_type="sintetico",
    )

    real_summary, real_employees = process_folder(
        folder=REAL_DIR,
        experiment_type="real",
    )

    write_tsv(OUTPUT_DIR / "synthetic_worked_days_summary.tsv", sintetico_summary)
    write_tsv(OUTPUT_DIR / "synthetic_worked_days_by_employee.tsv", sintetico_employees)

    write_tsv(OUTPUT_DIR / "real_worked_days_summary.tsv", real_summary)
    write_tsv(OUTPUT_DIR / "real_worked_days_by_employee.tsv", real_employees)


if __name__ == "__main__":
    main()