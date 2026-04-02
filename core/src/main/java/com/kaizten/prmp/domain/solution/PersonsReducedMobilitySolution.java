package com.kaizten.prmp.domain.solution;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kaizten.opt.solution.Solution;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.utils.string.KaiztenFormatterTable;

public class PersonsReducedMobilitySolution extends Solution<PersonsReducedMobilityProblem> implements Cloneable {

    private final int NO_SERVICE_ASSIGNED = -1;
    private Set<Integer>[][] serviceAssignment;
    private int firstService[][];
    private int lastService[][];
    private Set<Integer>[] assignedEmployees;

    public PersonsReducedMobilitySolution(PersonsReducedMobilityProblem optimizationProblem) {
        super(optimizationProblem);
        this.serviceAssignment = new HashSet[optimizationProblem.getNumberOfDatesWithServices()][optimizationProblem
                .getNumberOfEmployees()];
        for (int day = 0; day < optimizationProblem.getNumberOfDatesWithServices(); day++) {
            for (int employee = 0; employee < optimizationProblem.getNumberOfEmployees(); employee++) {
                this.serviceAssignment[day][employee] = new HashSet<>();
            }
        }
        this.firstService = new int[optimizationProblem.getNumberOfDatesWithServices()][optimizationProblem
                .getNumberOfEmployees()];
        for (int day = 0; day < optimizationProblem.getNumberOfDatesWithServices(); day++) {
            Arrays.fill(this.firstService[day], NO_SERVICE_ASSIGNED);
        }
        this.lastService = new int[optimizationProblem.getNumberOfDatesWithServices()][optimizationProblem
                .getNumberOfEmployees()];
        for (int day = 0; day < optimizationProblem.getNumberOfDatesWithServices(); day++) {
            Arrays.fill(this.lastService[day], NO_SERVICE_ASSIGNED);
        }
        this.assignedEmployees = new HashSet[optimizationProblem.getNumberOfServices()];
        for (int service = 0; service < optimizationProblem.getNumberOfServices(); service++) {
            this.assignedEmployees[service] = new HashSet<>();
        }
    }

    @Override
    public Solution clone() {
        PersonsReducedMobilitySolution copy = (PersonsReducedMobilitySolution) super.clone();
        copy.serviceAssignment = new HashSet[optimizationProblem.getNumberOfDatesWithServices()][optimizationProblem
                .getNumberOfEmployees()];
        for (int day = 0; day < optimizationProblem.getNumberOfDatesWithServices(); day++) {
            for (int employee = 0; employee < optimizationProblem.getNumberOfEmployees(); employee++) {
                copy.serviceAssignment[day][employee] = new HashSet<>(this.serviceAssignment[day][employee]);
            }
        }
        copy.firstService = new int[optimizationProblem.getNumberOfDatesWithServices()][optimizationProblem
                .getNumberOfEmployees()];
        for (int day = 0; day < optimizationProblem.getNumberOfDatesWithServices(); day++) {
            for (int employee = 0; employee < optimizationProblem.getNumberOfEmployees(); employee++) {
                copy.firstService[day][employee] = this.firstService[day][employee];
            }
        }
        copy.lastService = new int[optimizationProblem.getNumberOfDatesWithServices()][optimizationProblem
                .getNumberOfEmployees()];
        for (int day = 0; day < optimizationProblem.getNumberOfDatesWithServices(); day++) {
            for (int employee = 0; employee < optimizationProblem.getNumberOfEmployees(); employee++) {
                copy.lastService[day][employee] = this.lastService[day][employee];
            }
        }
        copy.assignedEmployees = new HashSet[optimizationProblem.getNumberOfServices()];
        for (int service = 0; service < optimizationProblem.getNumberOfServices(); service++) {
            copy.assignedEmployees[service] = new HashSet<>(this.assignedEmployees[service]);
        }
        return copy;
    }

    public boolean areServicesCovered() {
        return this.getNumberOfCoveredServices() == this.optimizationProblem.getNumberOfServices();
    }

    public List<Integer> getEmployeesByDate(LocalDate date) {
        final List<Integer> employees = new ArrayList<>();
        final List<Integer> servicesByDate = this.optimizationProblem.getServicesByDate(date);
        for (int s = 0; s < servicesByDate.size(); s++) {
            int service = servicesByDate.get(s);
            Integer[] employeesByService = this.assignedEmployees[service].stream().toArray(Integer[]::new);
            for (int e = 0; e < employeesByService.length; e++) {
                int employee = employeesByService[e];
                if (!employees.contains(employee))
                    employees.add(employee);
            }
        }
        return employees;
    }

    public boolean isEmployeeAssignedToService(int service, int employee) {
        final Set<Integer> employees = this.getAssignedEmployees(service);
        return employees.contains(employee);
    }

    public Set<Integer> getAssignedEmployees(int employee) {
        return this.assignedEmployees[employee];
    }

    public void assignServiceToEmployee(int employee, int service) {
        LocalDate date = this.optimizationProblem.getDate(service);
        int indexOfDate = this.optimizationProblem.getIndexOfDate(date);
        if (this.hasAssignedServices(indexOfDate, employee)) {
            this.firstService[indexOfDate][employee] = Math.min(this.firstService[indexOfDate][employee], service);
            this.lastService[indexOfDate][employee] = Math.max(this.lastService[indexOfDate][employee], service);
        } else {
            this.firstService[indexOfDate][employee] = service;
            this.lastService[indexOfDate][employee] = service;
        }
        this.serviceAssignment[indexOfDate][employee].add(service);
        this.assignedEmployees[service].add(employee);
    }

    public boolean isServiceCovered(int service) {
        return (this.getNumberOfAssignedEmployees(service) == this.optimizationProblem
                .getServiceRequiredEmployees(service));
    }

    public boolean hasAllServicesCovered() {
        for (int service = 0; service < this.optimizationProblem.getNumberOfServices(); service++) {
            if (!this.isServiceCovered(service)) {
                return false;
            }
        }
        return true;
    }

    public boolean isServiceOverlapping(int employee, int service) {
        LocalDate date = this.optimizationProblem.getDate(service);
        int indexOfDate = this.optimizationProblem.getIndexOfDate(date);
        final Set<Integer> services = this.serviceAssignment[indexOfDate][employee];
        OffsetDateTime newStartingTime = this.optimizationProblem.getServiceStartingTime(service);
        OffsetDateTime newEndingTime = this.optimizationProblem.getServiceFinishingTime(service);
        Integer[] arrayServices = services.stream().toArray(Integer[]::new);
        for (int i = 0; i < arrayServices.length; i++) {
            final int fl = arrayServices[i];
            OffsetDateTime flStartingTime = this.optimizationProblem.getServiceStartingTime(fl);
            OffsetDateTime flEndingTime = this.optimizationProblem.getServiceFinishingTime(fl);
            if (newStartingTime.isBefore(flEndingTime) && newEndingTime.isAfter(flStartingTime)) {
                return true;
            }
        }
        return false;
    }

    /*
     * public boolean doesServiceFitEmployeeWorkingTime(int employee, int service) {
     * LocalDate date = this.optimizationProblem.getDate(service);
     * int indexOfDate = this.optimizationProblem.getIndexOfDate(date);
     * long assignedWorkingTime = 0;
     * if (this.hasAssignedServices(indexOfDate, employee)) {
     * assignedWorkingTime = this.getUsedTime(indexOfDate, employee).toMinutes();
     * OffsetDateTime employeeFinishingTime = this.getFinishingTime(indexOfDate,
     * employee);
     * OffsetDateTime lastServiceFinishingTime =
     * super.getOptimizationProblem().getServiceFinishingTime(service);
     * if (lastServiceFinishingTime.isAfter(employeeFinishingTime)) {
     * assignedWorkingTime += Duration.between(employeeFinishingTime,
     * lastServiceFinishingTime).toMinutes();
     * } else {
     * OffsetDateTime employeeStartingTime = this.getStartingTime(indexOfDate,
     * employee);
     * OffsetDateTime firstServiceStartingTime =
     * super.getOptimizationProblem().getServiceStartingTime(service);
     * if (firstServiceStartingTime.isBefore(employeeStartingTime)) {
     * assignedWorkingTime += Duration.between(firstServiceStartingTime,
     * employeeStartingTime).toMinutes();
     * }
     * }
     * 
     * }
     * return (assignedWorkingTime <=
     * super.getOptimizationProblem().getEmployeeAvailableTimePerDay(employee));
     * }
     */
    // verificar que no se acumule a otro dia sino que se considere como nuevo dia
    public boolean doesServiceFitEmployeeWorkingTime(int employee, int service) {
        LocalDate date = this.optimizationProblem.getDate(service);
        int indexOfDate = this.optimizationProblem.getIndexOfDate(date);
        long assignedWorkingTime = 0;
        // Resetea las horas de trabajo a cero al inicio de un nuevo día
        if (this.hasAssignedServices(indexOfDate, employee)) {
            assignedWorkingTime = this.getUsedTime(indexOfDate, employee).toMinutes();
            OffsetDateTime employeeFinishingTime = this.getFinishingTime(indexOfDate, employee);
            OffsetDateTime lastServiceFinishingTime = super.getOptimizationProblem().getServiceFinishingTime(service);
            // Si el servicio termina después de la hora de finalización del empleado, se
            // acumula el tiempo adicional
            if (lastServiceFinishingTime.isAfter(employeeFinishingTime)) {
                assignedWorkingTime += Duration.between(employeeFinishingTime, lastServiceFinishingTime).toMinutes();
            } else {
                // Si el servicio comienza antes que la hora de inicio del empleado, se acumula
                // el tiempo faltante
                OffsetDateTime employeeStartingTime = this.getStartingTime(indexOfDate, employee);
                OffsetDateTime firstServiceStartingTime = super.getOptimizationProblem()
                        .getServiceStartingTime(service);
                if (firstServiceStartingTime.isBefore(employeeStartingTime)) {
                    assignedWorkingTime += Duration.between(firstServiceStartingTime, employeeStartingTime).toMinutes();
                }
            }
        }
        // Verifica que las horas trabajadas no excedan las disponibles para el empleado
        return (assignedWorkingTime <= super.getOptimizationProblem().getEmployeeAvailableTimePerDay(employee));
    }

    public int getFirstUncoveredService() {
        for (int i = 0; i < this.optimizationProblem.getNumberOfServices(); i++) {
            if (!this.isServiceCovered(i)) {
                return i;
            }
        }
        return -1;
    }

    public int getNextAvailableService(int employee, int service) {
        LocalDate date = this.optimizationProblem.getDate(service);
        int indexOfDate = this.optimizationProblem.getIndexOfDate(date);
        if (this.hasAssignedServices(indexOfDate, employee)) {
            for (int i = service + 1; i < this.optimizationProblem.getNumberOfServices(); i++) {
                if (this.doesServiceFitEmployeeWorkingTime(employee, i)) {
                    return i;
                }
            }
        } else {
            if (service < (this.optimizationProblem.getNumberOfServices() - 1))
                return (service + 1);
        }
        return -1;
    }

    public Set<Integer> getAssignedServices(LocalDate date, int employee) {
        int indexOfDate = this.optimizationProblem.getIndexOfDate(date);
        return this.getAssignedServices(indexOfDate, employee);
    }

    public Set<Integer> getAssignedServices(int date, int employee) {
        return this.serviceAssignment[date][employee];
    }

    public boolean hasAssignedServices(LocalDate date, int employee) {
        final int indexOfDate = this.optimizationProblem.getIndexOfDate(date);
        return !this.serviceAssignment[indexOfDate][employee].isEmpty();
    }

    public boolean hasAssignedServices(int date, int employee) {
        return !this.serviceAssignment[date][employee].isEmpty();
    }

    public int getNumberOfAssignedEmployees(int service) {
        return this.assignedEmployees[service].size();
    }

    public int getNumberOfAssignedServices(LocalDate date, int employee) {
        final int indexOfDate = this.optimizationProblem.getIndexOfDate(date);
        return this.getNumberOfAssignedServices(indexOfDate, employee);
    }

    public int getNumberOfAssignedServices(int date, int employee) {
        return this.serviceAssignment[date][employee].size();
    }

    public int getFirstAssignedService(LocalDate date, int employee) {
        int indexOfDate = this.optimizationProblem.getIndexOfDate(date);
        return this.firstService[indexOfDate][employee];
    }

    public int getFirstAssignedService(int date, int employee) {
        return this.firstService[date][employee];
    }

    public int getLastAssignedService(LocalDate date, int employee) {
        int indexOfDate = this.optimizationProblem.getIndexOfDate(date);
        return this.lastService[indexOfDate][employee];
    }

    public int getLastAssignedService(int date, int employee) {
        return this.lastService[date][employee];
    }

    public OffsetDateTime getStartingTime(LocalDate date, int employee) {
        int indexOfDate = this.optimizationProblem.getIndexOfDate(date);
        return this.getStartingTime(indexOfDate, employee);
    }

    public OffsetDateTime getStartingTime(int date, int employee) {
        if (!this.hasAssignedServices(date, employee)) {
            return null;
        }
        return this.optimizationProblem
                .getServiceStartingTime(this.getFirstAssignedService(date, employee));
    }

    public OffsetDateTime getFinishingTime(LocalDate date, int employee) {
        int indexOfDate = this.optimizationProblem.getIndexOfDate(date);
        return this.getFinishingTime(indexOfDate, employee);
    }

    public OffsetDateTime getFinishingTime(int date, int employee) {
        if (!this.hasAssignedServices(date, employee)) {
            return null;
        }
        return super.getOptimizationProblem()
                .getServiceFinishingTime(this.getLastAssignedService(date, employee));
    }

    public Duration getUsedTime(LocalDate date, int employee) {
        final int indexOfDate = this.optimizationProblem.getIndexOfDate(date);
        return this.getUsedTime(indexOfDate, employee);
    }

    public Duration getUsedTime(int date, int employee) {
        if (!this.hasAssignedServices(date, employee)) {
            return null;
        }
        return Duration.between(
                this.getStartingTime(date, employee),
                this.getFinishingTime(date, employee));
    }

    public double getProductivityWorkingTime(LocalDate date, int employee) {
        return ((double) this.getWorkingTime(date, employee) /
                (double) this.getUsedTime(date, employee).toMinutes())
                * 100.0;
    }

    public double getProductivityUsedTime(LocalDate date, int employee) {
        return ((double) this.getWorkingTime(date, employee) /
                (double) this.getUsedTime(date, employee).toMinutes())
                * 100.0;
    }

    public double getWorkProductivity(LocalDate date, int employee) {
        // Calcular tiempo de jornada total => en vez de getWorkingTime
        return ((double) this.getWorkingTime(date, employee) /
                (double) optimizationProblem.getEmployeeAvailableTimePerDay(employee))
                * 100.0;
    }

    // cálculo de las distintas productividades medias:
    // _____________________________

    // media de work productivity TOTAL
    public double getAverageWorkProductivity() {
        double productivity = 0.0;
        int numberOfProductivities = 0;
        for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
            for (LocalDate date : this.optimizationProblem.getDatesOfServices()) {
                if (this.hasAssignedServices(this.optimizationProblem.getIndexOfDate(date), employee)) {
                    productivity += this.getWorkProductivity(date, employee);
                    numberOfProductivities++;

                }
            }
        }
        return numberOfProductivities == 0 ? 0.0 : productivity / numberOfProductivities;
    }

    // media de productivity used time TOTAL
    public double getAverageProductivityUsedTime() {
        double productivity = 0.0;
        int numberOfProductivities = 0;
        for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
            for (LocalDate date : this.optimizationProblem.getDatesOfServices()) {
                if (this.hasAssignedServices(this.optimizationProblem.getIndexOfDate(date), employee)) {
                    productivity += this.getProductivityUsedTime(date, employee);
                    numberOfProductivities++;
                }
            }
        }
        return numberOfProductivities == 0 ? 0.0 : productivity / numberOfProductivities;
    }

    // funcion que calcule la productividad used time media de los empleados solo de
    // servicios reales (que no empiecen por f-)
    public double getAverageProductivityUsedTimeRealServices() {
        double productivity = 0.0;
        int numberOfProductivities = 0;
        for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
            for (LocalDate date : this.optimizationProblem.getDatesOfServices()) {
                if (this.hasAssignedServices(this.optimizationProblem.getIndexOfDate(date), employee)) {
                    Set<Integer> assignedServices = this
                            .getAssignedServices(this.optimizationProblem.getIndexOfDate(date), employee);
                    boolean hasBeenCounted = false;
                    for (int service : assignedServices) {
                    if (!hasBeenCounted && !this.getOptimizationProblem().getServiceCode(service).startsWith("f-")) {
                        productivity += this.getProductivityUsedTime(date, employee);
                        numberOfProductivities++;
                        hasBeenCounted = true;
                    }
                    }
                }
            }
        }
        return numberOfProductivities == 0 ? 0.0 : productivity / numberOfProductivities;
    }

    // funcion que calcule la work productividad media de los empleados solo de
    // servicios reales (que no empiecen por f-)
    public double getAverageWorkProductivityRealServices() {
        double productivity = 0.0;
        int numberOfProductivities = 0;
        for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
            for (LocalDate date : this.optimizationProblem.getDatesOfServices()) {
                Set<Integer> assignedServices = this.getAssignedServices(this.optimizationProblem.getIndexOfDate(date),
                        employee);
                boolean hasBeenCounted = false;
                for (int service : assignedServices) {
                    if (!hasBeenCounted && !this.getOptimizationProblem().getServiceCode(service).startsWith("f-")) {
                        productivity += this.getWorkProductivity(date, employee);
                        numberOfProductivities++;
                        hasBeenCounted = true;
                    }
                }
            }
        }
        return numberOfProductivities == 0 ? 0.0 : productivity / numberOfProductivities;
    }

    // media de productivity used time por rol total
    public Map<String, Double> getAverageProductivityUsedTimePerRole() {
        Map<String, Double> productivityPerRole = new HashMap<>();
        for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
            String role = this.optimizationProblem.getEmployeeRoles(employee).toString();
            double productivity = 0.0;
            int numberOfProductivities = 0;
            for (LocalDate date : this.optimizationProblem.getDatesOfServices()) {
                if (this.hasAssignedServices(this.optimizationProblem.getIndexOfDate(date), employee)) {
                    productivity += this.getProductivityUsedTime(date, employee);
                    numberOfProductivities++;
                } 
            }
            productivityPerRole.put(role, numberOfProductivities == 0 ? 0.0 : productivity / numberOfProductivities);
        }
        return productivityPerRole;
    }

    // media de work productivity por rol total
    public Map<String, Double> getAverageWorkProductivityPerRole() {
        Map<String, Double> productivityPerRole = new HashMap<>();
        for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
            String role = this.optimizationProblem.getEmployeeRoles(employee).toString();
            double productivity = 0.0;
            int numberOfProductivities = 0;
            for (LocalDate date : this.optimizationProblem.getDatesOfServices()) {
                if (this.hasAssignedServices(this.optimizationProblem.getIndexOfDate(date), employee)) {
                    productivity += this.getWorkProductivity(date, employee);
                    numberOfProductivities++;
                } 
            }
            productivityPerRole.put(role, numberOfProductivities == 0 ? 0.0 : productivity / numberOfProductivities);
        }
        return productivityPerRole;
    }

    // número de servicios reales
    public int getNumberOfRealServices() {
        int numberOfRealServices = 0;
        for (int service = 0; service < this.optimizationProblem.getNumberOfServices(); service++) {
            if (!this.getOptimizationProblem().getServiceCode(service).startsWith("f-")) {
                numberOfRealServices++;
            }
        }
        return numberOfRealServices;
    }

    // número de servicios falsos
    public int getNumberOfFakeServices() {
        int numberOfFakeServices = 0;
        for (int service = 0; service < this.optimizationProblem.getNumberOfServices(); service++) {
            if (this.getOptimizationProblem().getServiceCode(service).startsWith("f-")) {
                numberOfFakeServices++;
            }
        }
        return numberOfFakeServices;
    }

    // numero de empleados por rol
    public Map<String, Integer> getNumberOfEmployeesPerRole() {
        Map<String, Integer> numberOfEmployeesPerRole = new HashMap<>();
        for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
            String role = this.optimizationProblem.getEmployeeRoles(employee).toString();
            numberOfEmployeesPerRole.put(role, numberOfEmployeesPerRole.getOrDefault(role, 0) + 1);
        }
        return numberOfEmployeesPerRole;
    }

    // numero de AGENTES con trabajo
    public int getNumberOfAgentsWithWork() {
        int numberOfAgentsWithWork = 0;
        for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
            if (this.optimizationProblem.getEmployeeRoles(employee).toString().equals("[AGENT]")) {
                for (LocalDate date : this.optimizationProblem.getDatesOfServices()) {
                    if (this.hasAssignedServices(this.optimizationProblem.getIndexOfDate(date), employee)) {
                        numberOfAgentsWithWork++;
                        break;
                    }
                }
            }
        }
        return numberOfAgentsWithWork;
    }

    public int getNumberOfAgents() {
        int numberOfAgents = 0;
        for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
            if (this.optimizationProblem.getEmployeeRoles(employee).toString().equals("[AGENT]")) {
                numberOfAgents++;
            }
        }
        return numberOfAgents;
    }

    // % AGENTES con trabajo
    public double getPercentageOfAgentsWithWork() {
        int totalAgents = this.getNumberOfAgents();
        int agentsWithWork = this.getNumberOfAgentsWithWork();
        return (((double) agentsWithWork / totalAgents) * 100.0);
    }

    // _____________________________

    public int getWorkingTime(LocalDate date, int employee) {
        final int indexOfDate = this.optimizationProblem.getIndexOfDate(date);
        return this.getWorkingTime(indexOfDate, employee);
    }

    public int getWorkingTime(int date, int employee) {
        if (!this.hasAssignedServices(date, employee)) {
            return 0;
        }
        int workingTime = 0;
        Set<Integer> services = this.getAssignedServices(date, employee);
        Iterator<Integer> iterator = services.iterator();
        while (iterator.hasNext()) {
            int service = iterator.next();
            workingTime += this.optimizationProblem.getServiceTime(service);
        }
        return workingTime;
    }

    public int getBreakTime(LocalDate date, int employee) {
        final int indexOfDate = this.optimizationProblem.getIndexOfDate(date);
        return this.getBreakTime(indexOfDate, employee);
    }

    public int getBreakTime(int date, int employee) {
        int breakTime = this.optimizationProblem.getEmployeeTimePerDay(employee);
        if (!this.hasAssignedServices(date, employee)) {
            return breakTime;
        }
        Set<Integer> services = this.getAssignedServices(date, employee);
        Iterator<Integer> iterator = services.iterator();
        while (iterator.hasNext()) {
            int service = iterator.next();
            breakTime -= this.optimizationProblem.getServiceTime(service);
        }
        return breakTime;
    }

    public int getNumberOfCoveredServices() {
        int coveredServices = 0;
        for (int i = 0; i < this.optimizationProblem.getNumberOfServices(); i++) {
            if (this.isServiceCovered(i)) {
                coveredServices++;
            }
        }
        return coveredServices;
    }

    public int getNumberOfCoveredServices(LocalDate date) {
        int coveredServices = 0;
        for (int i = this.optimizationProblem.getIndexOfFirstServiceInDate(date); i <= this.optimizationProblem
                .getIndexOfLastServiceInDate(date); i++) {
            if (this.isServiceCovered(i)) {
                coveredServices++;
            }
        }
        return coveredServices;
    }

    public List<String> getUncoveredServices(LocalDate date) {
        List<String> uncoveredServices = new ArrayList<>();
        for (int i = this.optimizationProblem.getIndexOfFirstServiceInDate(date); i <= this.optimizationProblem
                .getIndexOfLastServiceInDate(date); i++) {
            if (!this.isServiceCovered(i)) {
                uncoveredServices.add(this.optimizationProblem.getServiceCode(i));
            }
        }
        return uncoveredServices;
    }

    public int getNumberOfUncoveredServices() {
        return this.optimizationProblem.getNumberOfServices() - this.getNumberOfCoveredServices();
    }

    public int getNumberOfUncoveredServices(LocalDate date) {
        return this.optimizationProblem.getNumberOfServices(date) - this.getNumberOfCoveredServices(date);
    }

    // que se considere por día
    // Método para calcular el tiempo disponible de un empleado en un día
    public int getAvailableTime(LocalDate date, int employee) {
        // Reinicia el tiempo disponible al principio de cada día
        int availableTime = super.getOptimizationProblem().getEmployeeAvailableTimePerDay(employee);
        // Aquí restas el tiempo ya trabajado en ese día
        if (this.hasAssignedServices(date, employee)) {
            availableTime -= this.getUsedTime(date, employee).toMinutes();
        }
        return availableTime;
    }

    public int getAvailableTime(int date, int employee) {
        return this.optimizationProblem.getEmployeeAvailableTimePerDay(employee) -
                this.getWorkingTime(date, employee);
    }

    public int getUsedEmployees() {
        int usedEmployees = 0;
        for (LocalDate date : this.getOptimizationProblem().getDatesOfServices()) {
            usedEmployees += this.getUsedEmployees(date);
        }
        return usedEmployees;
    }

    public int getUsedEmployees(LocalDate date) {
        int usedEmployees = 0;
        for (int e = 0; e < this.getOptimizationProblem().getNumberOfEmployees(); e++) {
            if (this.hasAssignedServices(date, e)) {
                usedEmployees++;
            }
        }
        return usedEmployees;
    }

    public long getElapsedTimeFromPreviousDays(int employee, int serviceToCheck) {
        long elapseOfTimeBetweenDays = Long.MAX_VALUE;
        LocalDate date = this.getOptimizationProblem().getServiceStartingTime(serviceToCheck).toLocalDate();
        for (int service = serviceToCheck - 1; service >= 0; service--) {
            if (this.isEmployeeAssignedToService(service, employee)) {
                LocalDate date2 = this.getOptimizationProblem().getServiceStartingTime(service).toLocalDate();
                if (!date.isEqual(date2)) {
                    return Duration.between(
                            this.getOptimizationProblem().getServiceFinishingTime(service),
                            this.getOptimizationProblem().getServiceStartingTime(serviceToCheck))
                            .toMinutes();
                }
            }
        }
        return elapseOfTimeBetweenDays;
    }

    public long getElapsedTimeFromPreviousDays(LocalDate date, int employee) {
        int indexOfDate = this.optimizationProblem.getIndexOfDate(date);
        long elapsedTime = Long.MAX_VALUE;
        if (indexOfDate > 0 && this.hasAssignedServices(indexOfDate, employee)) {
            final int theFirstService = this.getFirstAssignedService(indexOfDate, employee);
            elapsedTime = this.getElapsedTimeFromPreviousDays(employee, theFirstService);
        }
        return elapsedTime;
    }

    public boolean doesServiceSatisfiesTimeBetweenDays(int employee, int service) {
        long elapseOfTimeBetweenDays = this.getElapsedTimeFromPreviousDays(employee, service);
        return (elapseOfTimeBetweenDays >= this.optimizationProblem.getEmployeeTimeBetweenWorkingDays(employee));
    }

    public boolean doesServiceFitsWorkingJourney(int employee, int service) {
        if (!this.optimizationProblem.hasEmployeeStartTime(employee)
                && !this.optimizationProblem.hasEmployeeFinishTime(employee)) {
            return true;
        }
        OffsetDateTime serviceStartingTime = this.optimizationProblem.getServiceStartingTime(service);
        OffsetDateTime serviceFinishingTime = this.optimizationProblem.getServiceFinishingTime(service);
        final OffsetDateTime defaultStart = OffsetDateTime.of(
                serviceStartingTime.getYear(),
                serviceStartingTime.getMonthValue(),
                serviceStartingTime.getDayOfMonth(),
                0, 0, 0, 0,
                ZoneOffset.UTC);
        OffsetDateTime start = defaultStart;
        if (this.optimizationProblem.hasEmployeeStartTime(employee)) {
            LocalTime s = this.optimizationProblem.getEmployeeStartTime(employee).get();
            start = start
                    .plusHours(s.getHour())
                    .plusMinutes(s.getMinute());
        }
        OffsetDateTime finish = start
                .plusMinutes(this.optimizationProblem.getEmployeeTimePerDay(employee));
        if (start.isAfter(serviceStartingTime)) {
            return false;
        }
        return !serviceFinishingTime.isAfter(finish);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        KaiztenFormatterTable tableServices = new KaiztenFormatterTable();
        tableServices.addRow(new String[] {
                "",
                "code",
                "startTime",
                "finishTime",
                "serviceTime",
                "role",
                "requiredEmployees",
                "assignedEmployees",
                "covered",
                "",
                "employees"
        });
        for (int service = 0; service < optimizationProblem.getNumberOfServices(); service++) {
            final int row = service + 1;
            int column = 0;
            tableServices.setValue(service + ":", row, column++);
            tableServices.setValue(this.getOptimizationProblem().getServiceCode(service), row, column++);
            tableServices.setValue(this.getOptimizationProblem().getServiceStartingTime(service), row, column++);
            tableServices.setValue(this.getOptimizationProblem().getServiceFinishingTime(service), row, column++);
            tableServices.setValue(this.getOptimizationProblem().getServiceTime(service), row, column++);
            tableServices.setValue(this.getOptimizationProblem().getServiceRole(service), row, column++);
            tableServices.setValue(this.getOptimizationProblem().getServiceRequiredEmployees(service), row, column++);
            tableServices.setValue(this.getNumberOfAssignedEmployees(service), row, column++);
            tableServices.setValue(this.isServiceCovered(service) ? "true" : "false", row, column++);
            tableServices.setValue("|", row, column++);
            ArrayList<Integer> sortedEmployees = new ArrayList<>(this.getAssignedEmployees(service));
            Collections.sort(sortedEmployees);
            for (int employee = 0; employee < sortedEmployees.size(); employee++) {
                tableServices.setValue(sortedEmployees.get(employee), row, employee + column++);
            }
        }
        tableServices.setClearNullValues(true);
        builder.append("Services:\n");
        builder.append(tableServices.toString());
        builder.append("\n");
        //
        for (LocalDate date : this.optimizationProblem.getDatesOfServices()) {
            int indexOfDate = this.optimizationProblem.getIndexOfDate(date);
            builder.append("Date " + indexOfDate + ": " + date + "\n");
            KaiztenFormatterTable tableEmployees = new KaiztenFormatterTable();
            tableEmployees.addRow(new String[] {
                    "employee",
                    "code",
                    "timePerDay",
                    "startTime",
                    "finishTime",
                    "usedTime",
                    "availableTime",
                    "workingTime",
                    "breakTime",
                    "mininumTimeFromPreviousDays",
                    "elapsedTimeFromPreviousDays",
                    "assignedServices",
                    "firstService",
                    "lastService",
                    "services"
            });
            int row = 1;
            for (int employee = 0; employee < optimizationProblem.getNumberOfEmployees(); employee++) {
                int column = 0;
                if (this.hasAssignedServices(indexOfDate, employee)) {
                    tableEmployees.setValue(employee + ":", row, column++);
                    tableEmployees.setValue(this.optimizationProblem.getEmployeeCode(employee), row, column++);
                    tableEmployees.setValue(this.optimizationProblem.getEmployeeTimePerDay(employee), row, column++);
                    tableEmployees.setValue(this.getStartingTime(indexOfDate, employee), row, column++);
                    tableEmployees.setValue(this.getFinishingTime(indexOfDate, employee), row, column++);
                    tableEmployees.setValue(this.getUsedTime(indexOfDate, employee), row, column++);
                    tableEmployees.setValue(this.getAvailableTime(indexOfDate, employee), row, column++);
                    tableEmployees.setValue(this.getWorkingTime(indexOfDate, employee), row, column++);
                    tableEmployees.setValue(this.getBreakTime(indexOfDate, employee), row, column++);
                    tableEmployees.setValue(this.getOptimizationProblem().getEmployeeTimeBetweenWorkingDays(employee),
                            row, column++);
                    String elapsedTime = "INFINITE";
                    long time = this.getElapsedTimeFromPreviousDays(date, employee);
                    if (time != Long.MAX_VALUE) {
                        elapsedTime = "" + time;
                    }
                    tableEmployees.setValue(elapsedTime, row, column++);
                    tableEmployees.setValue(this.getNumberOfAssignedServices(indexOfDate, employee), row, column++);
                    tableEmployees.setValue(this.getFirstAssignedService(indexOfDate, employee), row, column++);
                    tableEmployees.setValue(this.getLastAssignedService(indexOfDate, employee), row, column++);
                    ArrayList<Integer> sortedServices = new ArrayList<>(
                            this.getAssignedServices(indexOfDate, employee));
                    Collections.sort(sortedServices);
                    for (int j = 0; j < sortedServices.size(); j++) {
                        tableEmployees.setValue(sortedServices.get(j), row, j + column++);
                    }
                    row++;
                }
            }
            tableEmployees.setClearNullValues(true);
            builder.append("Employees:\n");
            builder.append(tableEmployees.toString());
            builder.append("\n");
        }
        return builder.toString();
    }
}