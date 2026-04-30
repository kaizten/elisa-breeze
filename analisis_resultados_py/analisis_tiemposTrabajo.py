import csv
import json
import re
from collections import defaultdict
from pathlib import Path
from statistics import mean, median

import matplotlib.pyplot as plt

SINTETICO_DIR = Path("/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/solutions")
REAL_DIR = Path("/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/realCaseTest/solutions")
OUTPUT_DIR = Path("/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/analisis_resultados_py/worked_days_output")


def get_algorithm_from_filename(path: Path) -> str:
    last_part = path.stem.split("-")[-1].lower()

    if "compacting" in last_part:
        return "CompactingSolver"
    if "random" in last_part:
        return "RandomSolver"
    if "reference" in last_part:
        return "ReferenceSolver"


def get_agents_from_filename(path: Path):
    name = path.stem.lower()

    # Sintéticos: 
    match = re.search(r"agents(\d+)", name)
    if match:
        return int(match.group(1))

    # Reales:
    match = re.search(r"_(\d+)_(?:reference|random|compacting)", name)
    if match:
        return int(match.group(1))

    return ""


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


def calculate_worked_days(solution: dict):

    dates = solution.get("dates")

    employee_days = defaultdict(set)
    employee_services = defaultdict(int)
    horizon_dates = set()

    for day_block in dates:
        date = day_block.get("date")

        if not date:
            continue

        horizon_dates.add(date)

        for employee in day_block.get("employees", []):
            employee_id = get_employee_identifier(employee)
            services = employee.get("services", [])

            if employee_id and services:
                employee_days[employee_id].add(date)
                employee_services[employee_id] += len(services)

    horizon_days = len(horizon_dates)
    days_values = [len(days) for days in employee_days.values()]

    detail_rows = []

    for employee_id, worked_dates in sorted(employee_days.items()):
        days_worked = len(worked_dates)

        detail_rows.append({
            "employee_id": employee_id,
            "days_worked": days_worked,
            "active_days_ratio": round(days_worked / horizon_days, 4) if horizon_days else 0,
            "assigned_services": employee_services[employee_id],
            "worked_dates": ";".join(sorted(worked_dates)),
        })

    if not days_values:
        summary = {
            "horizon_days": horizon_days,
            "active_employees": 0,
            "employee_days": 0,
            "avg_days_per_active": 0,
            "median_days_per_active": 0,
            "min_days_per_active": 0,
            "max_days_per_active": 0,
            "avg_active_days_ratio": 0,
            "employee_days_per_calendar_day": 0,
            "one_day_employees": 0,
            "full_period_employees": 0,
            "total_assigned_services": 0,
            "avg_services_per_active": 0,
        }

        return summary, detail_rows

    total_employee_days = sum(days_values)
    total_assigned_services = sum(employee_services.values())
    avg_days = mean(days_values)

    summary = {
        "horizon_days": horizon_days,
        "active_employees": len(days_values),
        "employee_days": total_employee_days,
        "avg_days_per_active": round(avg_days, 4),
        "median_days_per_active": round(median(days_values), 4),
        "min_days_per_active": min(days_values),
        "max_days_per_active": max(days_values),
        "avg_active_days_ratio": round(avg_days / horizon_days, 4) if horizon_days else 0,
        "employee_days_per_calendar_day": round(total_employee_days / horizon_days, 4) if horizon_days else 0,
        "one_day_employees": sum(1 for value in days_values if value == 1),
        "full_period_employees": sum(1 for value in days_values if value == horizon_days),
        "total_assigned_services": total_assigned_services,
        "avg_services_per_active": round(total_assigned_services / len(days_values), 4),
    }

    return summary, detail_rows

def process_folder(folder: Path, experiment_type: str):
    summary_rows = []
    employee_rows = []

    for path in sorted(folder.rglob("*.json")):
        try:
            with path.open("r", encoding="utf-8") as f:
                solution = json.load(f)

            summary, details = calculate_worked_days(solution)
            metadata = parse_filename(path, experiment_type)

            summary_rows.append({
                "experiment_type": experiment_type,
                **metadata,
                **summary,
            })

            for detail in details:
                employee_rows.append({
                    "experiment_type": experiment_type,
                    **metadata,
                    **detail,
                })

        except Exception as e:
            print(f"Error procesando {path.name}: {e}")

    return summary_rows, employee_rows


def write_tsv(path: Path, rows: list[dict]):

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