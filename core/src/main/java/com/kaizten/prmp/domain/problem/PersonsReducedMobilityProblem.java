package com.kaizten.prmp.domain.problem;

import com.kaizten.opt.problem.OptimizationProblem;
import com.kaizten.utils.collection.Tuple2;
import com.kaizten.utils.datastructure.KaiztenArray;
import com.kaizten.utils.string.KaiztenFormatterTable;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class PersonsReducedMobilityProblem extends OptimizationProblem {

    public static String OPTIMIZATION_PROBLEM_NAME = "Persons with reduced mobility problem";
    public static final int DEFAULT_TIME_PER_DAY = 8 * 60;
    public static final int DEFAULT_TIME_PER_WEEK = 40 * 60;
    public static final int DEFAULT_TIME_BETWEEN_WORKING_DAYS = 12 * 60;
    private String airport;
    private final int services;
    private final String[] serviceCode;
    private final OffsetDateTime[] serviceStartTime;
    private final OffsetDateTime[] serviceFinishTime;
    private final Set<LocalDate> dates;
    private final int[] serviceRequiredEmployees;
    private final int[] serviceTime;
    private final Role[] serviceRole;
    private Map<LocalDate, Tuple2<Integer, Integer>> servicesByDate;
    private int employees;
    private String[] employeeCode;
    private int[] employeeTimePerDay;
    private int[] employeeTimePerWeek;
    private int[] employeeTimeBetweenWorkingDays;
    private boolean[][] employeeAvailability;
    private LocalTime[] employeeStartTime;
    private LocalTime[] employeeFinishTime;
    private Set<Role>[] employeeRoles;
    private LocalDate firstDate;
    private LocalDate lastDate;

    public PersonsReducedMobilityProblem(int services, int employees) {
        super();
        super.setName(OPTIMIZATION_PROBLEM_NAME);
        this.airport = null;
        this.services = services;
        this.employees = employees;
        this.serviceCode = new String[this.services];
        this.serviceStartTime = new OffsetDateTime[this.services];
        this.serviceFinishTime = new OffsetDateTime[this.services];
        this.serviceRequiredEmployees = new int[this.services];
        this.serviceTime = new int[this.services];
        this.serviceRole = new Role[this.services];
        this.servicesByDate = new HashMap<>();
        this.employeeCode = new String[this.employees];
        this.employeeTimePerDay = new int[this.employees];
        Arrays.fill(this.employeeTimePerDay, DEFAULT_TIME_PER_DAY);
        this.employeeTimePerWeek = new int[this.employees];
        Arrays.fill(this.employeeTimePerWeek, DEFAULT_TIME_PER_WEEK);
        this.employeeTimeBetweenWorkingDays = new int[this.employees];
        Arrays.fill(this.employeeTimeBetweenWorkingDays, DEFAULT_TIME_BETWEEN_WORKING_DAYS);
        this.employeeStartTime = new LocalTime[this.employees];
        Arrays.fill(this.employeeStartTime, null);
        this.employeeFinishTime = new LocalTime[this.employees];
        Arrays.fill(this.employeeFinishTime, null);
        this.employeeRoles = new HashSet[this.employees];
        for (int i = 0; i < this.employees; i++) {
            this.employeeRoles[i] = new HashSet<>();
        }
        this.dates = new HashSet<>();
    }

    public void addEmployee() {
        this.employees++;
        this.employeeCode = KaiztenArray.add(this.employeeCode, "");
        this.employeeTimePerDay = KaiztenArray.add(this.employeeTimePerDay, DEFAULT_TIME_PER_DAY);
        this.employeeTimePerWeek = KaiztenArray.add(this.employeeTimePerWeek, DEFAULT_TIME_PER_WEEK);
        this.employeeTimeBetweenWorkingDays = KaiztenArray.add(this.employeeTimeBetweenWorkingDays, DEFAULT_TIME_BETWEEN_WORKING_DAYS);
        LocalTime[] newEmployeeStartTime = new LocalTime[this.employees];
        LocalTime[] newEmployeeFinishTime = new LocalTime[this.employees];
        for (int i = 0; i < this.employees - 1; i++) {
            newEmployeeStartTime[i] = this.employeeStartTime[i];
            newEmployeeFinishTime[i] = this.employeeFinishTime[i];
        }
        newEmployeeStartTime[this.employees - 1] = null;
        newEmployeeFinishTime[this.employees - 1] = null;
        this.employeeStartTime = newEmployeeStartTime;
        this.employeeFinishTime = newEmployeeFinishTime;
        this.employeeRoles = KaiztenArray.add(this.employeeRoles, new HashSet<>());
    }

    public void addEmployee(Collection<Role> roles) {
        this.addEmployee();
        int index = this.employees - 1;
        for (Role role : roles) {
            this.employeeRoles[index].add(role);
        }
    }

    public PersonsReducedMobilityProblem(String airport, int services, int employees) {
        this(services, employees);
        this.airport = airport;
    }

    public boolean hasEmployeeStartTime(int index) {
        return this.employeeStartTime[index] != null;
    }

    public boolean hasEmployeeFinishTime(int index) {
        return this.employeeFinishTime[index] != null;
    }

    public Optional<LocalTime> getEmployeeStartTime(int index) {
        return Optional.ofNullable(this.employeeStartTime[index]);
    }

    public int getEmployeeTimePerDay(int index) {
        return this.employeeTimePerDay[index];
    }

    public int getEmployeeAvailableTimePerDay(int index) {
        return this.getEmployeeTimePerDay(index);
    }

    public int getIndexOfFirstServiceInDate(LocalDate date) {
        return this.servicesByDate.get(date).get0();
    }

    public int getIndexOfLastServiceInDate(LocalDate date) {
        return this.servicesByDate.get(date).get1();
    }

    public LocalDate getDate(int index) {
        return this.serviceStartTime[index].toLocalDate();
    }

    public int getIndexOfDate(LocalDate date) {
        List<LocalDate> datesOfServices = this.getDatesOfServices();
        for (int i = 0; i < datesOfServices.size(); i++) {
            if (datesOfServices.get(i).equals(date)) {
                return i;
            }
        }
        return -1;
    }

    public List<Integer> getServicesByDate(LocalDate date) {
        List<Integer> services = new ArrayList<Integer>();
        for (int i = 0; i < this.serviceStartTime.length; i++) {
            LocalDate serviceStarting = this.serviceStartTime[i].toLocalDate();
            if (serviceStarting.isEqual(date)) {
                services.add(i);
            }
        }
        return services;
    }

    public int getNumberOfDatesWithServices() {
        return this.servicesByDate.size();
    }

    public boolean hasAirport() {
        return this.airport != null;
    }

    public Optional<String> getAirport() {
        return Optional.of(this.airport);
    }

    public int getNumberOfEmployees() {
        return this.employees;
    }

    public int getNumberOfServices(LocalDate date) {
        if (!this.servicesByDate.containsKey(date)) {
            return 0;
        }
        Tuple2<Integer, Integer> tuple = this.servicesByDate.get(date);
        int firstService = tuple.get0();
        int lastService = tuple.get1();
        return lastService - firstService + 1;
    }

    public int getNumberOfServices() {
        return this.services;
    }

    public int getNumberOfDates() {
        return this.dates.size();
    }

    public OffsetDateTime getStartingTimeOfPlanningHorizon() {
        return this.serviceStartTime[0];
    }

    public OffsetDateTime getFinishingTimeOfPlanningHorizon() {
        return this.serviceFinishTime[this.services - 1];
    }

    public int getServiceTime(int serviceIndex) {
        return this.serviceTime[serviceIndex];
    }

    public String getServiceCode(int serviceIndex) {
        return this.serviceCode[serviceIndex];
    }

    public int getServiceRequiredEmployees(int serviceIndex) {
        return this.serviceRequiredEmployees[serviceIndex];
    }

    public OffsetDateTime getServiceStartingTime(int serviceIndex) {
        return this.serviceStartTime[serviceIndex];
    }

    public OffsetDateTime getServiceFinishingTime(int serviceIndex) {
        return this.serviceFinishTime[serviceIndex];
    }

    public Role getServiceRole(int serviceIndex) {
        return this.serviceRole[serviceIndex];
    }

    public String getEmployeeCode(int employeeIndex) {
        return this.employeeCode[employeeIndex];
    }

    public int getEmployeeMinutesPerDay(int index) {
        return this.employeeTimePerDay[index];
    }

    public boolean hasEmployeeStart(int index) {
        return this.employeeStartTime[index] != null;
    }

    public Set<Role> getEmployeeRoles(int index) {
        return this.employeeRoles[index];
    }

    public int getNumberOfEmployeeRoles(int index) {
        return this.employeeRoles[index].size();
    }

    public void addEmployeeRoles(int index, Role role) {
        this.employeeRoles[index].add(role);
    }

    public boolean hasEmployeeRole(int index, Role role) {
        return this.employeeRoles[index].contains(role);
    }

    public Optional<LocalTime> getEmployeeStart(int index) {
        return Optional.ofNullable(this.employeeStartTime[index]);
    }

    public boolean hasEmployeeFinish(int index) {
        return this.employeeFinishTime[index] != null;
    }

    public Optional<LocalTime> getEmployeeFinish(int index) {
        return Optional.ofNullable(this.employeeFinishTime[index]);
    }

    public int getEmployeeHoursPerDay(int index) {
        return this.getEmployeeMinutesPerDay(index) / 60;
    }

    public int getEmployeeTimePerWeek(int employeeIndex) {
        return this.employeeTimePerWeek[employeeIndex];
    }

    public int getEmployeeTimeBetweenWorkingDays(int employeeIndex) {
        return this.employeeTimeBetweenWorkingDays[employeeIndex];
    }

    public LocalDate getFirstDate() {
        return this.firstDate;
    }

    public LocalDate getLastDate() {
        return this.lastDate;
    }

    public void setAirport(String airport) {
        this.airport = airport;
    }

    public void setServiceCode(int serviceIndex, String name) {
        this.serviceCode[serviceIndex] = name;
    }

    public void setServiceRequiredEmployees(int serviceIndex, int requiredEmployees) {
        this.serviceRequiredEmployees[serviceIndex] = requiredEmployees;
    }

    public void setServiceTimes(int service, OffsetDateTime startingTime, OffsetDateTime finishingTime) {
        this.serviceStartTime[service] = startingTime;
        LocalDate date = startingTime.toLocalDate();
        this.dates.add(date);
        if ((this.firstDate == null) || (date.isBefore(this.firstDate))) {
            this.firstDate = date;
        }
        if ((this.lastDate == null) || (date.isAfter(this.lastDate))) {
            this.lastDate = date;
        }
        this.serviceStartTime[service] = startingTime;
        this.serviceFinishTime[service] = finishingTime;
        this.serviceTime[service] = (int) Duration.between(startingTime, finishingTime).toMinutes();
        Tuple2<Integer, Integer> values = null;
        if (!this.servicesByDate.containsKey(date)) {
            values = Tuple2.of(service, service);
            this.servicesByDate.put(date, values);
        } else {
            values = this.servicesByDate.get(date);
        }
        values.set0(Math.min(values.get0(), service));
        values.set1(Math.max(values.get1(), service));
    }

    public void setServiceRole(int serviceIndex, Role role) {
        this.serviceRole[serviceIndex] = role;
    }

    public List<LocalDate> getDatesOfServices() {
        List<LocalDate> sortedDates = new ArrayList<>(this.dates);
        Collections.sort(sortedDates);
        return sortedDates;
    }

    public void setEmployeeCode(int employeeIndex, String code) {
        this.employeeCode[employeeIndex] = code;
    }

    public void setEmployeeTimePerDay(int index, Duration minutes) {
        this.employeeTimePerDay[index] = (int) minutes.toMinutes();
    }

    public void setEmployeeStartTime(int index, LocalTime time) {
        this.employeeStartTime[index] = time;
    }

    public void setEmployeeFinishTime(int index, LocalTime time) {
        this.employeeFinishTime[index] = time;
    }

    public void setEmployeeHoursPerWeek(int employeeIndex, Duration timePerWeek) {
        this.employeeTimePerWeek[employeeIndex] = (int) timePerWeek.toMinutes();
    }

    public void setEmployeeTimeBetweenWorkingDays(int employeeIndex, Duration timeBetweenWorkingDays) {
        this.employeeTimeBetweenWorkingDays[employeeIndex] = (int) timeBetweenWorkingDays.toMinutes();
    }

    public void computeEmployeesAvailability(List<Tuple2<Integer, LocalDate>> notAvailableDates) {
        this.employeeAvailability = new boolean[this.getNumberOfEmployees()][this.getNumberOfDates()];
        for (int employee = 0; employee < this.getNumberOfEmployees(); employee++) {
            for (int date = 0; date < this.getNumberOfDates(); date++) {
                this.employeeAvailability[employee][date] = true;
            }
        }
        List<LocalDate> sortedDates = this.getDatesOfServices();
        for (Tuple2<Integer, LocalDate> pair : notAvailableDates) {
            LocalDate date = pair.get1();
            int indexOfDate = sortedDates.indexOf(date);
            if (indexOfDate != -1) {
                this.employeeAvailability[pair.get0()][indexOfDate] = false;
            }
        }
    }

    public boolean isEmployeeAvailable(LocalDate date, int employeeIndex) {
        List<LocalDate> sortedDates = this.getDatesOfServices();
        int indexOfDate = sortedDates.indexOf(date);
        return this.employeeAvailability[employeeIndex][indexOfDate];
    }

    public String toString() {
        List<LocalDate> sortedDates = this.getDatesOfServices();
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString());
        builder.append("\n");
        builder.append("dates with services: " + this.getNumberOfDates() + "\n");
        builder.append("dates:               " + this.getDatesOfServices() + "\n");
        builder.append("first date:          " + this.getFirstDate() + "\n");
        builder.append("last date:           " + this.getLastDate() + "\n");
        KaiztenFormatterTable tableServices = new KaiztenFormatterTable();
        tableServices.addRow(new String[] {
                "index",
                "code",
                "startTime",
                "finishTime",
                "time",
                "role",
                "employees"
        });
        for (int service = 0; service < this.getNumberOfServices(); service++) {
            int row = service + 1;
            int column = 0;
            tableServices.setValue(service, row, column++);
            tableServices.setValue(this.getServiceCode(service), row, column++);
            tableServices.setValue(this.getServiceStartingTime(service), row, column++);
            tableServices.setValue(this.getServiceFinishingTime(service), row, column++);
            tableServices.setValue(this.getServiceTime(service), row, column++);
            tableServices.setValue(this.getServiceRole(service), row, column++);
            tableServices.setValue(this.getServiceRequiredEmployees(service), row, column++);
        }
        builder.append(tableServices.toString());
        builder.append("\n");
        builder.append("services by date:\n");
        for (int i = 0; i < sortedDates.size(); i++) {
            LocalDate date = sortedDates.get(i);
            builder.append(i + ") " + date + ": ");
            builder.append("(" + this.getNumberOfServices(date) + ") ");
            int firstFlight = this.getIndexOfFirstServiceInDate(date);
            int lastFlight = this.getIndexOfLastServiceInDate(date);
            for (int index = firstFlight; index <= lastFlight; index++) {
                builder.append(index + " ");
            }
            builder.append("\n");
        }
        KaiztenFormatterTable tableEmployees = new KaiztenFormatterTable();
        String[] header = new String[] {
                "index",
                "code",
                "timePerDay",
                "start",
                "finish",
                "timePerWeek",
                "timeBetweenWorkingDays",
                "roles",
        };
        for (int indexDate = 0; indexDate < this.getNumberOfDates(); indexDate++) {
            LocalDate date = this.getDatesOfServices().get(indexDate);
            header = KaiztenArray.add(header, date.toString());
        }
        tableEmployees.addRow(header);
        for (int employee = 0; employee < this.getNumberOfEmployees(); employee++) {
            int row = employee + 1;
            int column = 0;
            tableEmployees.setValue(employee, row, column++);
            tableEmployees.setValue(this.getEmployeeCode(employee), row, column++);
            tableEmployees.setValue(this.getEmployeeMinutesPerDay(employee), row, column++);
            Optional<LocalTime> start = this.getEmployeeStart(employee);
            Optional<LocalTime> finish = this.getEmployeeFinish(employee);
            if (!start.isEmpty()) {
                tableEmployees.setValue(start.get(), row, column++);
            } else {
                tableEmployees.setValue("", row, column++);
            }
            if (!finish.isEmpty()) {
                tableEmployees.setValue(finish.get(), row, column++);
            } else {
                tableEmployees.setValue("", row, column++);
            }
            tableEmployees.setValue(this.getEmployeeTimePerWeek(employee), row, column++);
            tableEmployees.setValue(this.getEmployeeTimeBetweenWorkingDays(employee), row, column++);
            tableEmployees.setValue(this.getEmployeeRoles(employee), row, column++);
            for (int indexDate = 0; indexDate < this.getNumberOfDates(); indexDate++) {
                LocalDate date = this.getDatesOfServices().get(indexDate);
                tableEmployees.setValue(this.isEmployeeAvailable(date, employee), row, column++);
            }
        }
        builder.append(tableEmployees.toString());
        return builder.toString();
    }
}