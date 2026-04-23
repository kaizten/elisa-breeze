package com.kaizten.prmp.conversor.creator;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.kaizten.prmp.domain.Service;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.problem.Role;

public class InstanceCreatorMAD {

    public PersonsReducedMobilityProblem createInstance(
            List<String> selectedFlights,
            String airport,
            int numberOfDays,
            int maxOverlaps,
            double percentage,
            String INSTANCEDIRECTORY,
            int agents,
            int timePerDay) throws Exception {

            final int numberOfServices = selectedFlights.size() + 107 + 12 * numberOfDays;
            final int numberOfManagers = 3;
            final int numberOfDrivers = 6;
            final int numberOfRampManagers = 3;
            final int numberOfExtraEmployees = 80; 

            final int numberOfEmployees = agents +
                    numberOfManagers +
                    numberOfDrivers +
                    numberOfRampManagers +
                    numberOfExtraEmployees;
            final PersonsReducedMobilityProblem optimizationProblem = new PersonsReducedMobilityProblem(
                    numberOfServices,
                    numberOfEmployees);

            optimizationProblem.setAirport(airport);
            
            // crear las fechas y hora de inicio y fin de los servicios
            final List<Service> listOfServices = new ArrayList<>();
            // Añado servicios base las 24h los 7 dias:
            final Role[] roles = { Role.DRIVER, Role.DRIVER, Role.RAMP_MANAGER, Role.MANAGER }; // BASE: 2 driver, 1 manager, 1 ramp manager
            // iterar a lo largo de los días, 3 turnos de 8h
            int code = 0;
            
            for (int i = 0; i < numberOfDays; i++) {
                LocalDate date = LocalDate.of(2024, 2, 5).plusDays(i);
                
                for (int shift = 0; shift < 3; shift++) {          
                    int start = (shift * 8) % 24;
                    int finish = (start + 8) % 24;
                    OffsetDateTime startingTime = date.atTime(start, 0).atOffset(ZoneOffset.UTC);
                    OffsetDateTime finishingTime = date.atTime(finish, 0).atOffset(ZoneOffset.UTC);
                    
                    if (finishingTime.getHour() < startingTime.getHour()) {
                        finishingTime = finishingTime.plusDays(1);
                    }
                    
                    for (Role role : roles) {
                        final Service newService = new Service();
                        newService.setCode("f-" + String.format("%04d", code));
                        newService.setStartingTime(startingTime);
                        newService.setFinishingTime(finishingTime);
                        newService.setRequiredEmployees(1);
                        newService.setRole(role);
                        listOfServices.add(newService);
                        code++;
                    }
                }
            }
            // SERVICIOS ADICIONALES
            // LUNES
            LocalDate monday = LocalDate.of(2024, 2, 5);
            final Service newService1 = new Service();
            newService1.setCode("f-" + String.format("%04d", code));
            newService1.setStartingTime(monday.atTime(0, 0).atOffset(ZoneOffset.UTC));
            newService1.setFinishingTime(monday.atTime(1, 0).atOffset(ZoneOffset.UTC));
            newService1.setRequiredEmployees(5);
            newService1.setRole(Role.DRIVER);
            listOfServices.add(newService1);

            final Service newService2 = new Service();
            newService2.setCode("f-" + String.format("%04d", ++code));
            newService2.setStartingTime(monday.atTime(0, 0).atOffset(ZoneOffset.UTC));
            newService2.setFinishingTime(monday.atTime(1, 0).atOffset(ZoneOffset.UTC));
            newService2.setRequiredEmployees(2);
            newService2.setRole(Role.MANAGER);
            listOfServices.add(newService2);

            final Service newService3 = new Service();
            newService3.setCode("f-" + String.format("%04d", ++code));
            newService3.setStartingTime(monday.atTime(0, 0).atOffset(ZoneOffset.UTC));
            newService3.setFinishingTime(monday.atTime(1, 0).atOffset(ZoneOffset.UTC));
            newService3.setRequiredEmployees(2);
            newService3.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService3);

            final Service newService4 = new Service();
            newService4.setCode("f-" + String.format("%04d", ++code));
            newService4.setStartingTime(monday.atTime(1, 0).atOffset(ZoneOffset.UTC));
            newService4.setFinishingTime(monday.atTime(2, 0).atOffset(ZoneOffset.UTC));
            newService4.setRequiredEmployees(3);
            newService4.setRole(Role.DRIVER);
            listOfServices.add(newService4);

            final Service newService5 = new Service();
            newService5.setCode("f-" + String.format("%04d", ++code));
            newService5.setStartingTime(monday.atTime(5, 0).atOffset(ZoneOffset.UTC));
            newService5.setFinishingTime(monday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService5.setRequiredEmployees(5);
            newService5.setRole(Role.DRIVER);
            listOfServices.add(newService5);

            final Service newService6 = new Service();
            newService6.setCode("f-" + String.format("%04d", ++code));
            newService6.setStartingTime(monday.atTime(5, 0).atOffset(ZoneOffset.UTC));
            newService6.setFinishingTime(monday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService6.setRequiredEmployees(2);
            newService6.setRole(Role.MANAGER);
            listOfServices.add(newService6);

            final Service newService7 = new Service();
            newService7.setCode("f-" + String.format("%04d", ++code));
            newService7.setStartingTime(monday.atTime(5, 0).atOffset(ZoneOffset.UTC));
            newService7.setFinishingTime(monday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService7.setRequiredEmployees(2);
            newService7.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService7);

            final Service newService8 = new Service();
            newService8.setCode("f-" + String.format("%04d", ++code));
            newService8.setStartingTime(monday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService8.setFinishingTime(monday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService8.setRequiredEmployees(8);
            newService8.setRole(Role.DRIVER);
            listOfServices.add(newService8);

            final Service newService9 = new Service();
            newService9.setCode("f-" + String.format("%04d", ++code));
            newService9.setStartingTime(monday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService9.setFinishingTime(monday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService9.setRequiredEmployees(3);
            newService9.setRole(Role.MANAGER);
            listOfServices.add(newService9);

            final Service newService10 = new Service();
            newService10.setCode("f-" + String.format("%04d", ++code));
            newService10.setStartingTime(monday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService10.setFinishingTime(monday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService10.setRequiredEmployees(3);
            newService10.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService10);

            final Service newService11 = new Service();
            newService11.setCode("f-" + String.format("%04d", ++code));
            newService11.setStartingTime(monday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService11.setFinishingTime(monday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService11.setRequiredEmployees(8);
            newService11.setRole(Role.DRIVER);
            listOfServices.add(newService11);

            final Service newService12 = new Service();
            newService12.setCode("f-" + String.format("%04d", ++code));
            newService12.setStartingTime(monday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService12.setFinishingTime(monday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService12.setRequiredEmployees(3);
            newService12.setRole(Role.MANAGER);
            listOfServices.add(newService12);

            final Service newService13 = new Service();
            newService13.setCode("f-" + String.format("%04d", ++code));
            newService13.setStartingTime(monday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService13.setFinishingTime(monday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService13.setRequiredEmployees(3);
            newService13.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService13);

            final Service newService14 = new Service();
            newService14.setCode("f-" + String.format("%04d", ++code));
            newService14.setStartingTime(monday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService14.setFinishingTime(monday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
            newService14.setRequiredEmployees(8);
            newService14.setRole(Role.DRIVER);
            listOfServices.add(newService14);

            final Service newService15 = new Service();
            newService15.setCode("f-" + String.format("%04d", ++code));
            newService15.setStartingTime(monday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService15.setFinishingTime(monday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
            newService15.setRequiredEmployees(3);
            newService15.setRole(Role.MANAGER);
            listOfServices.add(newService15);

            final Service newService16 = new Service();
            newService16.setCode("f-" + String.format("%04d", ++code));
            newService16.setStartingTime(monday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService16.setFinishingTime(monday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
            newService16.setRequiredEmployees(3);
            newService16.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService16);

            // Martes
            LocalDate tuesday = LocalDate.of(2024, 2, 6);

            final Service newService17 = new Service();
            newService17.setCode("f-" + String.format("%04d", ++code));
            newService17.setStartingTime(tuesday.atTime(0, 0).atOffset(ZoneOffset.UTC));
            newService17.setFinishingTime(tuesday.atTime(1, 0).atOffset(ZoneOffset.UTC));
            newService17.setRequiredEmployees(5);
            newService17.setRole(Role.DRIVER);
            listOfServices.add(newService17);

            final Service newService18 = new Service();
            newService18.setCode("f-" + String.format("%04d", ++code));
            newService18.setStartingTime(tuesday.atTime(0, 0).atOffset(ZoneOffset.UTC));
            newService18.setFinishingTime(tuesday.atTime(1, 0).atOffset(ZoneOffset.UTC));
            newService18.setRequiredEmployees(2);
            newService18.setRole(Role.MANAGER);
            listOfServices.add(newService18);

            final Service newService19 = new Service();
            newService19.setCode("f-" + String.format("%04d", ++code));
            newService19.setStartingTime(tuesday.atTime(0, 0).atOffset(ZoneOffset.UTC));
            newService19.setFinishingTime(tuesday.atTime(1, 0).atOffset(ZoneOffset.UTC));
            newService19.setRequiredEmployees(2);
            newService19.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService19);

            final Service newService20 = new Service();
            newService20.setCode("f-" + String.format("%04d", ++code));
            newService20.setStartingTime(tuesday.atTime(1, 0).atOffset(ZoneOffset.UTC));
            newService20.setFinishingTime(tuesday.atTime(2, 0).atOffset(ZoneOffset.UTC));
            newService20.setRequiredEmployees(3);
            newService20.setRole(Role.DRIVER);
            listOfServices.add(newService20);

            final Service newService21 = new Service();
            newService21.setCode("f-" + String.format("%04d", ++code));
            newService21.setStartingTime(tuesday.atTime(3, 0).atOffset(ZoneOffset.UTC));
            newService21.setFinishingTime(tuesday.atTime(5, 0).atOffset(ZoneOffset.UTC));
            newService21.setRequiredEmployees(3);
            newService21.setRole(Role.DRIVER);
            listOfServices.add(newService21);

            final Service newService22 = new Service();
            newService22.setCode("f-" + String.format("%04d", ++code));
            newService22.setStartingTime(tuesday.atTime(5, 0).atOffset(ZoneOffset.UTC));
            newService22.setFinishingTime(tuesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService22.setRequiredEmployees(5);
            newService22.setRole(Role.DRIVER);
            listOfServices.add(newService22);

            final Service newService23 = new Service();
            newService23.setCode("f-" + String.format("%04d", ++code));
            newService23.setStartingTime(tuesday.atTime(5, 0).atOffset(ZoneOffset.UTC));
            newService23.setFinishingTime(tuesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService23.setRequiredEmployees(2);
            newService23.setRole(Role.MANAGER);
            listOfServices.add(newService23);

            final Service newService24 = new Service();
            newService24.setCode("f-" + String.format("%04d", ++code));
            newService24.setStartingTime(tuesday.atTime(5, 0).atOffset(ZoneOffset.UTC));
            newService24.setFinishingTime(tuesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService24.setRequiredEmployees(2);
            newService24.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService24);

            final Service newService25 = new Service();
            newService25.setCode("f-" + String.format("%04d", ++code));
            newService25.setStartingTime(tuesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService25.setFinishingTime(tuesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService25.setRequiredEmployees(8);
            newService25.setRole(Role.DRIVER);
            listOfServices.add(newService25);

            final Service newService26 = new Service();
            newService26.setCode("f-" + String.format("%04d", ++code));
            newService26.setStartingTime(tuesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService26.setFinishingTime(tuesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService26.setRequiredEmployees(3);
            newService26.setRole(Role.MANAGER);
            listOfServices.add(newService26);

            final Service newService27 = new Service();
            newService27.setCode("f-" + String.format("%04d", ++code));
            newService27.setStartingTime(tuesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService27.setFinishingTime(tuesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService27.setRequiredEmployees(3);
            newService27.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService27);

            final Service newService28 = new Service();
            newService28.setCode("f-" + String.format("%04d", ++code));
            newService28.setStartingTime(tuesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService28.setFinishingTime(tuesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService28.setRequiredEmployees(8);
            newService28.setRole(Role.DRIVER);
            listOfServices.add(newService28);

            final Service newService29 = new Service();
            newService29.setCode("f-" + String.format("%04d", ++code));
            newService29.setStartingTime(tuesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService29.setFinishingTime(tuesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService29.setRequiredEmployees(3);
            newService29.setRole(Role.MANAGER);
            listOfServices.add(newService29);

            final Service newService30 = new Service();
            newService30.setCode("f-" + String.format("%04d", ++code));
            newService30.setStartingTime(tuesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService30.setFinishingTime(tuesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService30.setRequiredEmployees(3);
            newService30.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService30);

            final Service newService31 = new Service();
            newService31.setCode("f-" + String.format("%04d", ++code));
            newService31.setStartingTime(tuesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService31.setFinishingTime(tuesday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
            newService31.setRequiredEmployees(8);
            newService31.setRole(Role.DRIVER);
            listOfServices.add(newService31);

            final Service newService32 = new Service();
            newService32.setCode("f-" + String.format("%04d", ++code));
            newService32.setStartingTime(tuesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService32.setFinishingTime(tuesday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
            newService32.setRequiredEmployees(3);
            newService32.setRole(Role.MANAGER);
            listOfServices.add(newService32);

            final Service newService33 = new Service();
            newService33.setCode("f-" + String.format("%04d", ++code));
            newService33.setStartingTime(tuesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService33.setFinishingTime(tuesday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
            newService33.setRequiredEmployees(3);
            newService33.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService33);

            // WEDNESDAY
            LocalDate wednesday = LocalDate.of(2024, 2, 7);

            final Service newService34 = new Service();
            newService34.setCode("f-" + String.format("%04d", ++code));
            newService34.setStartingTime(wednesday.atTime(0, 0).atOffset(ZoneOffset.UTC));
            newService34.setFinishingTime(wednesday.atTime(2, 0).atOffset(ZoneOffset.UTC));
            newService34.setRequiredEmployees(3);
            newService34.setRole(Role.DRIVER);
            listOfServices.add(newService34);

            final Service newService35 = new Service();
            newService35.setCode("f-" + String.format("%04d", ++code));
            newService35.setStartingTime(wednesday.atTime(5, 0).atOffset(ZoneOffset.UTC));
            newService35.setFinishingTime(wednesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService35.setRequiredEmployees(5);
            newService35.setRole(Role.DRIVER);
            listOfServices.add(newService35);

            final Service newService36 = new Service();
            newService36.setCode("f-" + String.format("%04d", ++code));
            newService36.setStartingTime(wednesday.atTime(5, 0).atOffset(ZoneOffset.UTC));
            newService36.setFinishingTime(wednesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService36.setRequiredEmployees(2);
            newService36.setRole(Role.MANAGER);
            listOfServices.add(newService36);

            final Service newService37 = new Service();
            newService37.setCode("f-" + String.format("%04d", ++code));
            newService37.setStartingTime(wednesday.atTime(5, 0).atOffset(ZoneOffset.UTC));
            newService37.setFinishingTime(wednesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService37.setRequiredEmployees(2);
            newService37.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService37);

            final Service newService38 = new Service();
            newService38.setCode("f-" + String.format("%04d", ++code));
            newService38.setStartingTime(wednesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService38.setFinishingTime(wednesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService38.setRequiredEmployees(8);
            newService38.setRole(Role.DRIVER);
            listOfServices.add(newService38);

            final Service newService39 = new Service();
            newService39.setCode("f-" + String.format("%04d", ++code));
            newService39.setStartingTime(wednesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService39.setFinishingTime(wednesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService39.setRequiredEmployees(3);
            newService39.setRole(Role.MANAGER);
            listOfServices.add(newService39);

            final Service newService40 = new Service();
            newService40.setCode("f-" + String.format("%04d", ++code));
            newService40.setStartingTime(wednesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService40.setFinishingTime(wednesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService40.setRequiredEmployees(3);
            newService40.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService40);

            final Service newService41 = new Service();
            newService41.setCode("f-" + String.format("%04d", ++code));
            newService41.setStartingTime(wednesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService41.setFinishingTime(wednesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService41.setRequiredEmployees(8);
            newService41.setRole(Role.DRIVER);
            listOfServices.add(newService41);

            final Service newService42 = new Service();
            newService42.setCode("f-" + String.format("%04d", ++code));
            newService42.setStartingTime(wednesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService42.setFinishingTime(wednesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService42.setRequiredEmployees(3);
            newService42.setRole(Role.MANAGER);
            listOfServices.add(newService42);

            final Service newService43 = new Service();
            newService43.setCode("f-" + String.format("%04d", ++code));
            newService43.setStartingTime(wednesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService43.setFinishingTime(wednesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService43.setRequiredEmployees(3);
            newService43.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService43);

            final Service newService44 = new Service();
            newService44.setCode("f-" + String.format("%04d", ++code));
            newService44.setStartingTime(wednesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService44.setFinishingTime(wednesday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
            newService44.setRequiredEmployees(8);
            newService44.setRole(Role.DRIVER);
            listOfServices.add(newService44);

            final Service newService45 = new Service();
            newService45.setCode("f-" + String.format("%04d", ++code));
            newService45.setStartingTime(wednesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService45.setFinishingTime(wednesday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
            newService45.setRequiredEmployees(3);
            newService45.setRole(Role.MANAGER);
            listOfServices.add(newService45);

            final Service newService46 = new Service();
            newService46.setCode("f-" + String.format("%04d", ++code));
            newService46.setStartingTime(wednesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService46.setFinishingTime(wednesday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
            newService46.setRequiredEmployees(3);
            newService46.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService46);

            // THURSDAY
            LocalDate thursday = LocalDate.of(2024, 2, 8);

            final Service newService47 = new Service();
            newService47.setCode("f-" + String.format("%04d", ++code));
            newService47.setStartingTime(thursday.atTime(0, 0).atOffset(ZoneOffset.UTC));
            newService47.setFinishingTime(thursday.atTime(2, 0).atOffset(ZoneOffset.UTC));
            newService47.setRequiredEmployees(3);
            newService47.setRole(Role.DRIVER);
            listOfServices.add(newService47);

            final Service newService48 = new Service();
            newService48.setCode("f-" + String.format("%04d", ++code));
            newService48.setStartingTime(thursday.atTime(3, 0).atOffset(ZoneOffset.UTC));
            newService48.setFinishingTime(thursday.atTime(4, 0).atOffset(ZoneOffset.UTC));
            newService48.setRequiredEmployees(3);
            newService48.setRole(Role.DRIVER);
            listOfServices.add(newService48);

            final Service newService49 = new Service();
            newService49.setCode("f-" + String.format("%04d", ++code));
            newService49.setStartingTime(thursday.atTime(5, 0).atOffset(ZoneOffset.UTC));
            newService49.setFinishingTime(thursday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService49.setRequiredEmployees(5);
            newService49.setRole(Role.DRIVER);
            listOfServices.add(newService49);

            final Service newService50 = new Service();
            newService50.setCode("f-" + String.format("%04d", ++code));
            newService50.setStartingTime(thursday.atTime(5, 0).atOffset(ZoneOffset.UTC));
            newService50.setFinishingTime(thursday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService50.setRequiredEmployees(2);
            newService50.setRole(Role.MANAGER);
            listOfServices.add(newService50);

            final Service newService51 = new Service();
            newService51.setCode("f-" + String.format("%04d", ++code));
            newService51.setStartingTime(thursday.atTime(5, 0).atOffset(ZoneOffset.UTC));
            newService51.setFinishingTime(thursday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService51.setRequiredEmployees(2);
            newService51.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService51);

            final Service newService52 = new Service();
            newService52.setCode("f-" + String.format("%04d", ++code));
            newService52.setStartingTime(thursday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService52.setFinishingTime(thursday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService52.setRequiredEmployees(8);
            newService52.setRole(Role.DRIVER);
            listOfServices.add(newService52);

            final Service newService53 = new Service();
            newService53.setCode("f-" + String.format("%04d", ++code));
            newService53.setStartingTime(thursday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService53.setFinishingTime(thursday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService53.setRequiredEmployees(3);
            newService53.setRole(Role.MANAGER);
            listOfServices.add(newService53);

            final Service newService54 = new Service();
            newService54.setCode("f-" + String.format("%04d", ++code));
            newService54.setStartingTime(thursday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService54.setFinishingTime(thursday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService54.setRequiredEmployees(3);
            newService54.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService54);

            final Service newService55 = new Service();
            newService55.setCode("f-" + String.format("%04d", ++code));
            newService55.setStartingTime(thursday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService55.setFinishingTime(thursday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService55.setRequiredEmployees(8);
            newService55.setRole(Role.DRIVER);
            listOfServices.add(newService55);

            final Service newService56 = new Service();
            newService56.setCode("f-" + String.format("%04d", ++code));
            newService56.setStartingTime(thursday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService56.setFinishingTime(thursday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService56.setRequiredEmployees(3);
            newService56.setRole(Role.MANAGER);
            listOfServices.add(newService56);

            final Service newService57 = new Service();
            newService57.setCode("f-" + String.format("%04d", ++code));
            newService57.setStartingTime(thursday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService57.setFinishingTime(thursday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService57.setRequiredEmployees(3);
            newService57.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService57);

            final Service newService58 = new Service();
            newService58.setCode("f-" + String.format("%04d", ++code));
            newService58.setStartingTime(thursday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService58.setFinishingTime(thursday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
            newService58.setRequiredEmployees(8);
            newService58.setRole(Role.DRIVER);
            listOfServices.add(newService58);

            final Service newService59 = new Service();
            newService59.setCode("f-" + String.format("%04d", ++code));
            newService59.setStartingTime(thursday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService59.setFinishingTime(thursday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
            newService59.setRequiredEmployees(3);
            newService59.setRole(Role.MANAGER);
            listOfServices.add(newService59);

            final Service newService60 = new Service();
            newService60.setCode("f-" + String.format("%04d", ++code));
            newService60.setStartingTime(thursday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService60.setFinishingTime(thursday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
            newService60.setRequiredEmployees(3);
            newService60.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService60);

            // FRIDAY
            LocalDate friday = LocalDate.of(2024, 2, 9);

            final Service newService61 = new Service();
            newService61.setCode("f-" + String.format("%04d", ++code));
            newService61.setStartingTime(friday.atTime(0, 0).atOffset(ZoneOffset.UTC));
            newService61.setFinishingTime(friday.atTime(1, 0).atOffset(ZoneOffset.UTC));
            newService61.setRequiredEmployees(5);
            newService61.setRole(Role.DRIVER);
            listOfServices.add(newService61);

            final Service newService62 = new Service();
            newService62.setCode("f-" + String.format("%04d", ++code));
            newService62.setStartingTime(friday.atTime(0, 0).atOffset(ZoneOffset.UTC));
            newService62.setFinishingTime(friday.atTime(1, 0).atOffset(ZoneOffset.UTC));
            newService62.setRequiredEmployees(2);
            newService62.setRole(Role.MANAGER);
            listOfServices.add(newService62);

            final Service newService63 = new Service();
            newService63.setCode("f-" + String.format("%04d", ++code));
            newService63.setStartingTime(friday.atTime(0, 0).atOffset(ZoneOffset.UTC));
            newService63.setFinishingTime(friday.atTime(1, 0).atOffset(ZoneOffset.UTC));
            newService63.setRequiredEmployees(2);
            newService63.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService63);

            final Service newService64 = new Service();
            newService64.setCode("f-" + String.format("%04d", ++code));
            newService64.setStartingTime(friday.atTime(4, 0).atOffset(ZoneOffset.UTC));
            newService64.setFinishingTime(friday.atTime(5, 0).atOffset(ZoneOffset.UTC));
            newService64.setRequiredEmployees(3);
            newService64.setRole(Role.DRIVER);
            listOfServices.add(newService64);

            final Service newService65 = new Service();
            newService65.setCode("f-" + String.format("%04d", ++code));
            newService65.setStartingTime(friday.atTime(5, 0).atOffset(ZoneOffset.UTC));
            newService65.setFinishingTime(friday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService65.setRequiredEmployees(5);
            newService65.setRole(Role.DRIVER);
            listOfServices.add(newService65);

            final Service newService66 = new Service();
            newService66.setCode("f-" + String.format("%04d", ++code));
            newService66.setStartingTime(friday.atTime(5, 0).atOffset(ZoneOffset.UTC));
            newService66.setFinishingTime(friday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService66.setRequiredEmployees(2);
            newService66.setRole(Role.MANAGER);
            listOfServices.add(newService66);

            final Service newService67 = new Service();
            newService67.setCode("f-" + String.format("%04d", ++code));
            newService67.setStartingTime(friday.atTime(5, 0).atOffset(ZoneOffset.UTC));
            newService67.setFinishingTime(friday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService67.setRequiredEmployees(2);
            newService67.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService67);

            final Service newService68 = new Service();
            newService68.setCode("f-" + String.format("%04d", ++code));
            newService68.setStartingTime(friday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService68.setFinishingTime(friday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService68.setRequiredEmployees(8);
            newService68.setRole(Role.DRIVER);
            listOfServices.add(newService68);

            final Service newService69 = new Service();
            newService69.setCode("f-" + String.format("%04d", ++code));
            newService69.setStartingTime(friday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService69.setFinishingTime(friday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService69.setRequiredEmployees(3);
            newService69.setRole(Role.MANAGER);
            listOfServices.add(newService69);

            final Service newService70 = new Service();
            newService70.setCode("f-" + String.format("%04d", ++code));
            newService70.setStartingTime(friday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService70.setFinishingTime(friday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService70.setRequiredEmployees(3);
            newService70.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService70);

            final Service newService71 = new Service();
            newService71.setCode("f-" + String.format("%04d", ++code));
            newService71.setStartingTime(friday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService71.setFinishingTime(friday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService71.setRequiredEmployees(8);
            newService71.setRole(Role.DRIVER);
            listOfServices.add(newService71);

            final Service newService72 = new Service();
            newService72.setCode("f-" + String.format("%04d", ++code));
            newService72.setStartingTime(friday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService72.setFinishingTime(friday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService72.setRequiredEmployees(3);
            newService72.setRole(Role.MANAGER);
            listOfServices.add(newService72);

            final Service newService73 = new Service();
            newService73.setCode("f-" + String.format("%04d", ++code));
            newService73.setStartingTime(friday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService73.setFinishingTime(friday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService73.setRequiredEmployees(3);
            newService73.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService73);

            final Service newService74 = new Service();
            newService74.setCode("f-" + String.format("%04d", ++code));
            newService74.setStartingTime(friday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService74.setFinishingTime(friday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
            newService74.setRequiredEmployees(8);
            newService74.setRole(Role.DRIVER);
            listOfServices.add(newService74);

            final Service newService75 = new Service();
            newService75.setCode("f-" + String.format("%04d", ++code));
            newService75.setStartingTime(friday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService75.setFinishingTime(friday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
            newService75.setRequiredEmployees(3);
            newService75.setRole(Role.MANAGER);
            listOfServices.add(newService75);

            final Service newService76 = new Service();
            newService76.setCode("f-" + String.format("%04d", ++code));
            newService76.setStartingTime(friday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService76.setFinishingTime(friday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
            newService76.setRequiredEmployees(3);
            newService76.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService76);

            // SATURDAY
            LocalDate saturday = LocalDate.of(2024, 2, 10);

            final Service newService77 = new Service();
            newService77.setCode("f-" + String.format("%04d", ++code));
            newService77.setStartingTime(saturday.atTime(0, 0).atOffset(ZoneOffset.UTC));
            newService77.setFinishingTime(saturday.atTime(1, 0).atOffset(ZoneOffset.UTC));
            newService77.setRequiredEmployees(5);
            newService77.setRole(Role.DRIVER);
            listOfServices.add(newService77);

            final Service newService78 = new Service();
            newService78.setCode("f-" + String.format("%04d", ++code));
            newService78.setStartingTime(saturday.atTime(0, 0).atOffset(ZoneOffset.UTC));
            newService78.setFinishingTime(saturday.atTime(1, 0).atOffset(ZoneOffset.UTC));
            newService78.setRequiredEmployees(2);
            newService78.setRole(Role.MANAGER);
            listOfServices.add(newService78);

            final Service newService79 = new Service();
            newService79.setCode("f-" + String.format("%04d", ++code));
            newService79.setStartingTime(saturday.atTime(0, 0).atOffset(ZoneOffset.UTC));
            newService79.setFinishingTime(saturday.atTime(1, 0).atOffset(ZoneOffset.UTC));
            newService79.setRequiredEmployees(2);
            newService79.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService79);

            final Service newService80 = new Service();
            newService80.setCode("f-" + String.format("%04d", ++code));
            newService80.setStartingTime(saturday.atTime(1, 0).atOffset(ZoneOffset.UTC));
            newService80.setFinishingTime(saturday.atTime(2, 0).atOffset(ZoneOffset.UTC));
            newService80.setRequiredEmployees(3);
            newService80.setRole(Role.DRIVER);
            listOfServices.add(newService80);

            final Service newService81 = new Service();
            newService81.setCode("f-" + String.format("%04d", ++code));
            newService81.setStartingTime(saturday.atTime(5, 0).atOffset(ZoneOffset.UTC));
            newService81.setFinishingTime(saturday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService81.setRequiredEmployees(5);
            newService81.setRole(Role.DRIVER);
            listOfServices.add(newService81);

            final Service newService82 = new Service();
            newService82.setCode("f-" + String.format("%04d", ++code));
            newService82.setStartingTime(saturday.atTime(5, 0).atOffset(ZoneOffset.UTC));
            newService82.setFinishingTime(saturday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService82.setRequiredEmployees(2);
            newService82.setRole(Role.MANAGER);
            listOfServices.add(newService82);

            final Service newService83 = new Service();
            newService83.setCode("f-" + String.format("%04d", ++code));
            newService83.setStartingTime(saturday.atTime(5, 0).atOffset(ZoneOffset.UTC));
            newService83.setFinishingTime(saturday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService83.setRequiredEmployees(2);
            newService83.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService83);

            final Service newService84 = new Service();
            newService84.setCode("f-" + String.format("%04d", ++code));
            newService84.setStartingTime(saturday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService84.setFinishingTime(saturday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService84.setRequiredEmployees(8);
            newService84.setRole(Role.DRIVER);
            listOfServices.add(newService84);

            final Service newService85 = new Service();
            newService85.setCode("f-" + String.format("%04d", ++code));
            newService85.setStartingTime(saturday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService85.setFinishingTime(saturday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService85.setRequiredEmployees(3);
            newService85.setRole(Role.MANAGER);
            listOfServices.add(newService85);

            final Service newService86 = new Service();
            newService86.setCode("f-" + String.format("%04d", ++code));
            newService86.setStartingTime(saturday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService86.setFinishingTime(saturday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService86.setRequiredEmployees(3);
            newService86.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService86);

            final Service newService87 = new Service();
            newService87.setCode("f-" + String.format("%04d", ++code));
            newService87.setStartingTime(saturday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService87.setFinishingTime(saturday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService87.setRequiredEmployees(8);
            newService87.setRole(Role.DRIVER);
            listOfServices.add(newService87);

            final Service newService88 = new Service();
            newService88.setCode("f-" + String.format("%04d", ++code));
            newService88.setStartingTime(saturday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService88.setFinishingTime(saturday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService88.setRequiredEmployees(3);
            newService88.setRole(Role.MANAGER);
            listOfServices.add(newService88);

            final Service newService89 = new Service();
            newService89.setCode("f-" + String.format("%04d", ++code));
            newService89.setStartingTime(saturday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService89.setFinishingTime(saturday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService89.setRequiredEmployees(3);
            newService89.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService89);

            final Service newService90 = new Service();
            newService90.setCode("f-" + String.format("%04d", ++code));
            newService90.setStartingTime(saturday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService90.setFinishingTime(saturday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
            newService90.setRequiredEmployees(8);
            newService90.setRole(Role.DRIVER);
            listOfServices.add(newService90);

            final Service newService91 = new Service();
            newService91.setCode("f-" + String.format("%04d", ++code));
            newService91.setStartingTime(saturday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService91.setFinishingTime(saturday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
            newService91.setRequiredEmployees(3);
            newService91.setRole(Role.MANAGER);
            listOfServices.add(newService91);

            final Service newService92 = new Service();
            newService92.setCode("f-" + String.format("%04d", ++code));
            newService92.setStartingTime(saturday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService92.setFinishingTime(saturday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
            newService92.setRequiredEmployees(3);
            newService92.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService92);

            // SUNDAY
            LocalDate sunday = LocalDate.of(2024, 2, 11);

            final Service newService93 = new Service();
            newService93.setCode("f-" + String.format("%04d", ++code));
            newService93.setStartingTime(sunday.atTime(0, 0).atOffset(ZoneOffset.UTC));
            newService93.setFinishingTime(sunday.atTime(1, 0).atOffset(ZoneOffset.UTC));
            newService93.setRequiredEmployees(5);
            newService93.setRole(Role.DRIVER);
            listOfServices.add(newService93);

            final Service newService94 = new Service();
            newService94.setCode("f-" + String.format("%04d", ++code));
            newService94.setStartingTime(sunday.atTime(0, 0).atOffset(ZoneOffset.UTC));
            newService94.setFinishingTime(sunday.atTime(1, 0).atOffset(ZoneOffset.UTC));
            newService94.setRequiredEmployees(2);
            newService94.setRole(Role.MANAGER);
            listOfServices.add(newService94);

            final Service newService95 = new Service();
            newService95.setCode("f-" + String.format("%04d", ++code));
            newService95.setStartingTime(sunday.atTime(0, 0).atOffset(ZoneOffset.UTC));
            newService95.setFinishingTime(sunday.atTime(1, 0).atOffset(ZoneOffset.UTC));
            newService95.setRequiredEmployees(2);
            newService95.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService95);

            final Service newService96 = new Service();
            newService96.setCode("f-" + String.format("%04d", ++code));
            newService96.setStartingTime(sunday.atTime(5, 0).atOffset(ZoneOffset.UTC));
            newService96.setFinishingTime(sunday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService96.setRequiredEmployees(5);
            newService96.setRole(Role.DRIVER);
            listOfServices.add(newService96);

            final Service newService97 = new Service();
            newService97.setCode("f-" + String.format("%04d", ++code));
            newService97.setStartingTime(sunday.atTime(5, 0).atOffset(ZoneOffset.UTC));
            newService97.setFinishingTime(sunday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService97.setRequiredEmployees(2);
            newService97.setRole(Role.MANAGER);
            listOfServices.add(newService97);

            final Service newService98 = new Service();
            newService98.setCode("f-" + String.format("%04d", ++code));
            newService98.setStartingTime(sunday.atTime(5, 0).atOffset(ZoneOffset.UTC));
            newService98.setFinishingTime(sunday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService98.setRequiredEmployees(2);
            newService98.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService98);

            final Service newService99 = new Service();
            newService99.setCode("f-" + String.format("%04d", ++code));
            newService99.setStartingTime(sunday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService99.setFinishingTime(sunday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService99.setRequiredEmployees(8);
            newService99.setRole(Role.DRIVER);
            listOfServices.add(newService99);

            final Service newService100 = new Service();
            newService100.setCode("f-" + String.format("%04d", ++code));
            newService100.setStartingTime(sunday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService100.setFinishingTime(sunday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService100.setRequiredEmployees(3);
            newService100.setRole(Role.MANAGER);
            listOfServices.add(newService100);

            final Service newService101 = new Service();
            newService101.setCode("f-" + String.format("%04d", ++code));
            newService101.setStartingTime(sunday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService101.setFinishingTime(sunday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService101.setRequiredEmployees(3);
            newService101.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService101);

            final Service newService102 = new Service();
            newService102.setCode("f-" + String.format("%04d", ++code));
            newService102.setStartingTime(sunday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService102.setFinishingTime(sunday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService102.setRequiredEmployees(8);
            newService102.setRole(Role.DRIVER);
            listOfServices.add(newService102);

            final Service newService103 = new Service();
            newService103.setCode("f-" + String.format("%04d", ++code));
            newService103.setStartingTime(sunday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService103.setFinishingTime(sunday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService103.setRequiredEmployees(3);
            newService103.setRole(Role.MANAGER);
            listOfServices.add(newService103);

            final Service newService104 = new Service();
            newService104.setCode("f-" + String.format("%04d", ++code));
            newService104.setStartingTime(sunday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService104.setFinishingTime(sunday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService104.setRequiredEmployees(3);
            newService104.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService104);

            final Service newService105 = new Service();
            newService105.setCode("f-" + String.format("%04d", ++code));
            newService105.setStartingTime(sunday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService105.setFinishingTime(sunday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
            newService105.setRequiredEmployees(8);
            newService105.setRole(Role.DRIVER);
            listOfServices.add(newService105);

            final Service newService106 = new Service();
            newService106.setCode("f-" + String.format("%04d", ++code));
            newService106.setStartingTime(sunday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService106.setFinishingTime(sunday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
            newService106.setRequiredEmployees(3);
            newService106.setRole(Role.MANAGER);
            listOfServices.add(newService106);

            final Service newService107 = new Service();
            newService107.setCode("f-" + String.format("%04d", ++code));
            newService107.setStartingTime(sunday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService107.setFinishingTime(sunday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
            newService107.setRequiredEmployees(3);
            newService107.setRole(Role.RAMP_MANAGER);
            listOfServices.add(newService107);

            // ========================================================================================================

            // SERVICIOS REALES

            for (int i = 0; i < selectedFlights.size(); i++) {
                code++;
                String[] flightInfo = selectedFlights.get(i).split("/"); 
                String time = flightInfo[2].trim();
                LocalDate localDate = LocalDate.parse(flightInfo[0].trim()); // fecha
                LocalTime localTime = LocalTime.parse(time); // hora
                // Combinar la fecha y la hora en un LocalDateTime
                LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
                // Crear el OffsetDateTime usando UTC
                OffsetDateTime flightTime = localDateTime.atOffset(ZoneOffset.UTC);
                OffsetDateTime serviceStartingTime;
                OffsetDateTime serviceFinishingTime;
                if ("Salidas".equals(flightInfo[1].trim())) { // mira si es salida
                    serviceStartingTime = flightTime.minusHours(1); // 1 hora antes de la salida
                    serviceFinishingTime = flightTime.plusMinutes(10); // 10 minutos después de la salida
                } else { // Llegadas
                    serviceStartingTime = flightTime.minusMinutes(10); // 10 minutos antes de la llegada
                    serviceFinishingTime = flightTime.plusHours(1); // 1 hora después de la llegada
                }
                final Service newService = new Service();
                newService.setCode(String.format("%04d", code));
                newService.setStartingTime(serviceStartingTime);
                newService.setFinishingTime(serviceFinishingTime);
                newService.setRequiredEmployees(1);
                newService.setRole(Role.AGENT);
                listOfServices.add(newService);
            }
            Collections.sort(listOfServices);
            for (int i = 0; i < listOfServices.size(); i++) {
                Service service = listOfServices.get(i);
                optimizationProblem.setServiceCode(i, service.getCode());
                optimizationProblem.setServiceTimes(i, service.getStartingTime(), service.getFinishingTime());
                optimizationProblem.setServiceRole(i, service.getRole());
                optimizationProblem.setServiceRequiredEmployees(i, service.getRequiredEmployees());
            }
            // creo x empleados nuevos, rol AGENTE
            for (int i = 0; i < numberOfEmployees - 92; i++) {
                optimizationProblem.addEmployeeRoles(i, Role.AGENT);
                optimizationProblem.setEmployeeCode(i, String.format("%04d", i));
                optimizationProblem.setEmployeeTimePerDay(i, Duration.ofHours(timePerDay));
            }
            // Empleados añadidos
            // TOTAl: 80+6+3+3 = 92
            // TOTAL TOTAL: 510
            // 294 DRIVERS => 60% => 54
            // 108 MANAGER => 20% => 19
            // 108 RAMP_MANAGER => 20% => 19
            for (int i = numberOfEmployees - (92); i < numberOfEmployees - (38); i++) {
                optimizationProblem.addEmployeeRoles(i, Role.DRIVER);
                optimizationProblem.setEmployeeCode(i, String.format("%04d", i));
            }
            for (int i = numberOfEmployees - (38); i < numberOfEmployees - (19); i++) {
                optimizationProblem.addEmployeeRoles(i, Role.MANAGER);
                optimizationProblem.setEmployeeCode(i, String.format("%04d", i));
            }
            for (int i = numberOfEmployees - (19); i < numberOfEmployees; i++) {
                optimizationProblem.addEmployeeRoles(i, Role.RAMP_MANAGER);
                optimizationProblem.setEmployeeCode(i, String.format("%04d", i));
            }
            optimizationProblem.computeEmployeesAvailability(List.of());
            return optimizationProblem; 
    }
}