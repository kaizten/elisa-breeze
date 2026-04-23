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

public class InstanceCreatorTFS {

    public PersonsReducedMobilityProblem createInstance(
            List<String> selectedFlights,
            String airport,
            int numberOfDays,
            int maxOverlaps,
            double percentage,
            String INSTANCEDIRECTORY,
            int agents, 
            int timePerDay) throws Exception {

            final int numberOfServices = selectedFlights.size() + 61 + 6 * numberOfDays;
            int numberOfManagers = 3;
            int numberOfDrivers = 3;
            // calculo de numero de empleados extra: el dia con más demanda es lunes con 32
            // adjudicamos eso para que cubra eso y ya de paso cubre los demas (regla general de momento)
            int numberOfExtraEmployees = 32;
            int numberOfEmployees = agents + numberOfManagers + numberOfDrivers + numberOfExtraEmployees;
            final PersonsReducedMobilityProblem optimizationProblem = new PersonsReducedMobilityProblem(
                    numberOfServices,
                    numberOfEmployees);
            optimizationProblem.setAirport(airport);
            final List<Service> listOfServices = new ArrayList<>();
            // Añado servicios base (1 conductor, 1 coordinador) las 24h los 7 dias, con turnos de 8h:
            final Role[] roles = { Role.DRIVER, Role.MANAGER }; 
            int code = 0;
            
            // iterar a lo largo de los días
            for (int i = 0; i < numberOfDays; i++) {
                LocalDate date = LocalDate.of(2024, 2, 5).plusDays(i);
                
                for (int shift = 0; shift < 3; shift++) { // turnos por día (3 porq son de 8h)
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
            // Servicios añadidos manuales dependiendo del número de vuelos:
            // LUNES
            LocalDate monday = LocalDate.of(2024, 2, 5);
            final Service newService1 = new Service();
            newService1.setCode("f-" + String.format("%04d", code));
            newService1.setStartingTime(monday.atTime(6, 0).atOffset(ZoneOffset.UTC));
            newService1.setFinishingTime(monday.atTime(7, 0).atOffset(ZoneOffset.UTC));
            newService1.setRequiredEmployees(2);
            newService1.setRole(Role.DRIVER);
            listOfServices.add(newService1);

            final Service newService2 = new Service();
            newService2.setCode("f-" + String.format("%04d", ++code));
            newService2.setStartingTime(monday.atTime(10, 0).atOffset(ZoneOffset.UTC));
            newService2.setFinishingTime(monday.atTime(13, 0).atOffset(ZoneOffset.UTC));
            newService2.setRequiredEmployees(4);
            newService2.setRole(Role.DRIVER);
            listOfServices.add(newService2);

            final Service newService3 = new Service();
            newService3.setCode("f-" + String.format("%04d", ++code));
            newService3.setStartingTime(monday.atTime(10, 0).atOffset(ZoneOffset.UTC));
            newService3.setFinishingTime(monday.atTime(13, 0).atOffset(ZoneOffset.UTC));
            newService3.setRequiredEmployees(2);
            newService3.setRole(Role.MANAGER);
            listOfServices.add(newService3);

            final Service newService4 = new Service();
            newService4.setCode("f-" + String.format("%04d", ++code));
            newService4.setStartingTime(monday.atTime(13, 0).atOffset(ZoneOffset.UTC));
            newService4.setFinishingTime(monday.atTime(15, 0).atOffset(ZoneOffset.UTC));
            newService4.setRequiredEmployees(8);
            newService4.setRole(Role.DRIVER);
            listOfServices.add(newService4);

            final Service newService5 = new Service();
            newService5.setCode("f-" + String.format("%04d", ++code));
            newService5.setStartingTime(monday.atTime(13, 0).atOffset(ZoneOffset.UTC));
            newService5.setFinishingTime(monday.atTime(15, 0).atOffset(ZoneOffset.UTC));
            newService5.setRequiredEmployees(3);
            newService5.setRole(Role.MANAGER);
            listOfServices.add(newService5);

            final Service newService6 = new Service();
            newService6.setCode("f-" + String.format("%04d", ++code));
            newService6.setStartingTime(monday.atTime(16, 0).atOffset(ZoneOffset.UTC));
            newService6.setFinishingTime(monday.atTime(17, 0).atOffset(ZoneOffset.UTC));
            newService6.setRequiredEmployees(4);
            newService6.setRole(Role.DRIVER);
            listOfServices.add(newService6);

            final Service newService7 = new Service();
            newService7.setCode("f-" + String.format("%04d", ++code));
            newService7.setStartingTime(monday.atTime(16, 0).atOffset(ZoneOffset.UTC));
            newService7.setFinishingTime(monday.atTime(17, 0).atOffset(ZoneOffset.UTC));
            newService7.setRequiredEmployees(2);
            newService7.setRole(Role.MANAGER);
            listOfServices.add(newService7);

            final Service newService8 = new Service();
            newService8.setCode("f-" + String.format("%04d", ++code));
            newService8.setStartingTime(monday.atTime(17, 0).atOffset(ZoneOffset.UTC));
            newService8.setFinishingTime(monday.atTime(19, 0).atOffset(ZoneOffset.UTC));
            newService8.setRequiredEmployees(8);
            newService8.setRole(Role.DRIVER);
            listOfServices.add(newService8);

            final Service newService9 = new Service();
            newService9.setCode("f-" + String.format("%04d", ++code));
            newService9.setStartingTime(monday.atTime(17, 0).atOffset(ZoneOffset.UTC));
            newService9.setFinishingTime(monday.atTime(19, 0).atOffset(ZoneOffset.UTC));
            newService9.setRequiredEmployees(3);
            newService9.setRole(Role.MANAGER);
            listOfServices.add(newService9);

            final Service newService10 = new Service();
            newService10.setCode("f-" + String.format("%04d", ++code));
            newService10.setStartingTime(monday.atTime(19, 0).atOffset(ZoneOffset.UTC));
            newService10.setFinishingTime(monday.atTime(20, 0).atOffset(ZoneOffset.UTC));
            newService10.setRequiredEmployees(4);
            newService10.setRole(Role.DRIVER);
            listOfServices.add(newService10);

            final Service newService11 = new Service();
            newService11.setCode("f-" + String.format("%04d", ++code));
            newService11.setStartingTime(monday.atTime(19, 0).atOffset(ZoneOffset.UTC));
            newService11.setFinishingTime(monday.atTime(20, 0).atOffset(ZoneOffset.UTC));
            newService11.setRequiredEmployees(2);
            newService11.setRole(Role.MANAGER);
            listOfServices.add(newService11);

            final Service newService12 = new Service();
            newService12.setCode("f-" + String.format("%04d", ++code));
            newService12.setStartingTime(monday.atTime(20, 0).atOffset(ZoneOffset.UTC));
            newService12.setFinishingTime(monday.atTime(22, 0).atOffset(ZoneOffset.UTC));
            newService12.setRequiredEmployees(2);
            newService12.setRole(Role.DRIVER);
            listOfServices.add(newService12);

            // MARTES
            LocalDate tuesday = LocalDate.of(2024, 2, 6);

            final Service newService13 = new Service();
            newService13.setCode("f-" + String.format("%04d", ++code));
            newService13.setStartingTime(tuesday.atTime(9, 0).atOffset(ZoneOffset.UTC));
            newService13.setFinishingTime(tuesday.atTime(10, 0).atOffset(ZoneOffset.UTC));
            newService13.setRequiredEmployees(2);
            newService13.setRole(Role.DRIVER);
            listOfServices.add(newService13);

            final Service newService14 = new Service();
            newService14.setCode("f-" + String.format("%04d", ++code));
            newService14.setStartingTime(tuesday.atTime(10, 0).atOffset(ZoneOffset.UTC));
            newService14.setFinishingTime(tuesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService14.setRequiredEmployees(8);
            newService14.setRole(Role.DRIVER);
            listOfServices.add(newService14);

            final Service newService15 = new Service();
            newService15.setCode("f-" + String.format("%04d", ++code));
            newService15.setStartingTime(tuesday.atTime(10, 0).atOffset(ZoneOffset.UTC));
            newService15.setFinishingTime(tuesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService15.setRequiredEmployees(3);
            newService15.setRole(Role.MANAGER);
            listOfServices.add(newService15);

            final Service newService16 = new Service();
            newService16.setCode("f-" + String.format("%04d", ++code));
            newService16.setStartingTime(tuesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService16.setFinishingTime(tuesday.atTime(19, 0).atOffset(ZoneOffset.UTC));
            newService16.setRequiredEmployees(8);
            newService16.setRole(Role.DRIVER);
            listOfServices.add(newService16);

            final Service newService17 = new Service();
            newService17.setCode("f-" + String.format("%04d", ++code));
            newService17.setStartingTime(tuesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            newService17.setFinishingTime(tuesday.atTime(19, 0).atOffset(ZoneOffset.UTC));
            newService17.setRequiredEmployees(3);
            newService17.setRole(Role.MANAGER);
            listOfServices.add(newService17);

            final Service newService18 = new Service();
            newService18.setCode("f-" + String.format("%04d", ++code));
            newService18.setStartingTime(tuesday.atTime(19, 0).atOffset(ZoneOffset.UTC));
            newService18.setFinishingTime(tuesday.atTime(20, 0).atOffset(ZoneOffset.UTC));
            newService18.setRequiredEmployees(4);
            newService18.setRole(Role.DRIVER);
            listOfServices.add(newService18);

            final Service newService19 = new Service();
            newService19.setCode("f-" + String.format("%04d", ++code));
            newService19.setStartingTime(tuesday.atTime(19, 0).atOffset(ZoneOffset.UTC));
            newService19.setFinishingTime(tuesday.atTime(20, 0).atOffset(ZoneOffset.UTC));
            newService19.setRequiredEmployees(2);
            newService19.setRole(Role.MANAGER);
            listOfServices.add(newService19);

            // MIÉRCOLES
            LocalDate wednesday = LocalDate.of(2024, 2, 7);

            final Service service15 = new Service();
            service15.setCode("f-" + String.format("%04d", ++code));
            service15.setStartingTime(wednesday.atTime(8, 0).atOffset(ZoneOffset.UTC));
            service15.setFinishingTime(wednesday.atTime(10, 0).atOffset(ZoneOffset.UTC));
            service15.setRequiredEmployees(2);
            service15.setRole(Role.DRIVER);
            listOfServices.add(service15);

            final Service service16 = new Service();
            service16.setCode("f-" + String.format("%04d", ++code));
            service16.setStartingTime(wednesday.atTime(10, 0).atOffset(ZoneOffset.UTC));
            service16.setFinishingTime(wednesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            service16.setRequiredEmployees(8);
            service16.setRole(Role.DRIVER);
            listOfServices.add(service16);

            final Service service17 = new Service();
            service17.setCode("f-" + String.format("%04d", ++code));
            service17.setStartingTime(wednesday.atTime(10, 0).atOffset(ZoneOffset.UTC));
            service17.setFinishingTime(wednesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            service17.setRequiredEmployees(3);
            service17.setRole(Role.MANAGER);
            listOfServices.add(service17);

            final Service service18 = new Service();
            service18.setCode("f-" + String.format("%04d", ++code));
            service18.setStartingTime(wednesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            service18.setFinishingTime(wednesday.atTime(19, 0).atOffset(ZoneOffset.UTC));
            service18.setRequiredEmployees(8);
            service18.setRole(Role.DRIVER);
            listOfServices.add(service18);

            final Service service19 = new Service();
            service19.setCode("f-" + String.format("%04d", ++code));
            service19.setStartingTime(wednesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
            service19.setFinishingTime(wednesday.atTime(19, 0).atOffset(ZoneOffset.UTC));
            service19.setRequiredEmployees(3);
            service19.setRole(Role.MANAGER);
            listOfServices.add(service19);

            final Service service20 = new Service();
            service20.setCode("f-" + String.format("%04d", ++code));
            service20.setStartingTime(wednesday.atTime(19, 0).atOffset(ZoneOffset.UTC));
            service20.setFinishingTime(wednesday.atTime(20, 0).atOffset(ZoneOffset.UTC));
            service20.setRequiredEmployees(4);
            service20.setRole(Role.DRIVER);
            listOfServices.add(service20);

            final Service service21 = new Service();
            service21.setCode("f-" + String.format("%04d", ++code));
            service21.setStartingTime(wednesday.atTime(19, 0).atOffset(ZoneOffset.UTC));
            service21.setFinishingTime(wednesday.atTime(20, 0).atOffset(ZoneOffset.UTC));
            service21.setRequiredEmployees(2);
            service21.setRole(Role.MANAGER);
            listOfServices.add(service21);

            // JUEVES
            LocalDate thursday = LocalDate.of(2024, 2, 8);

            final Service service22 = new Service();
            service22.setCode("f-" + String.format("%04d", ++code));
            service22.setStartingTime(thursday.atTime(9, 0).atOffset(ZoneOffset.UTC));
            service22.setFinishingTime(thursday.atTime(10, 0).atOffset(ZoneOffset.UTC));
            service22.setRequiredEmployees(4);
            service22.setRole(Role.DRIVER);
            listOfServices.add(service22);

            final Service service23 = new Service();
            service23.setCode("f-" + String.format("%04d", ++code));
            service23.setStartingTime(thursday.atTime(9, 0).atOffset(ZoneOffset.UTC));
            service23.setFinishingTime(thursday.atTime(10, 0).atOffset(ZoneOffset.UTC));
            service23.setRequiredEmployees(2);
            service23.setRole(Role.MANAGER);
            listOfServices.add(service23);

            final Service service24 = new Service();
            service24.setCode("f-" + String.format("%04d", ++code));
            service24.setStartingTime(thursday.atTime(10, 0).atOffset(ZoneOffset.UTC));
            service24.setFinishingTime(thursday.atTime(17, 0).atOffset(ZoneOffset.UTC));
            service24.setRequiredEmployees(8);
            service24.setRole(Role.DRIVER);
            listOfServices.add(service24);

            final Service service25 = new Service();
            service25.setCode("f-" + String.format("%04d", ++code));
            service25.setStartingTime(thursday.atTime(10, 0).atOffset(ZoneOffset.UTC));
            service25.setFinishingTime(thursday.atTime(17, 0).atOffset(ZoneOffset.UTC));
            service25.setRequiredEmployees(3);
            service25.setRole(Role.MANAGER);
            listOfServices.add(service25);

            final Service service26 = new Service();
            service26.setCode("f-" + String.format("%04d", ++code));
            service26.setStartingTime(thursday.atTime(17, 0).atOffset(ZoneOffset.UTC));
            service26.setFinishingTime(thursday.atTime(18, 0).atOffset(ZoneOffset.UTC));
            service26.setRequiredEmployees(4);
            service26.setRole(Role.DRIVER);
            listOfServices.add(service26);

            final Service service27 = new Service();
            service27.setCode("f-" + String.format("%04d", ++code));
            service27.setStartingTime(thursday.atTime(17, 0).atOffset(ZoneOffset.UTC));
            service27.setFinishingTime(thursday.atTime(18, 0).atOffset(ZoneOffset.UTC));
            service27.setRequiredEmployees(2);
            service27.setRole(Role.MANAGER);
            listOfServices.add(service27);

            final Service service28 = new Service();
            service28.setCode("f-" + String.format("%04d", ++code));
            service28.setStartingTime(thursday.atTime(18, 0).atOffset(ZoneOffset.UTC));
            service28.setFinishingTime(thursday.atTime(19, 0).atOffset(ZoneOffset.UTC));
            service28.setRequiredEmployees(8);
            service28.setRole(Role.DRIVER);
            listOfServices.add(service28);

            final Service service29 = new Service();
            service29.setCode("f-" + String.format("%04d", ++code));
            service29.setStartingTime(thursday.atTime(18, 0).atOffset(ZoneOffset.UTC));
            service29.setFinishingTime(thursday.atTime(19, 0).atOffset(ZoneOffset.UTC));
            service29.setRequiredEmployees(3);
            service29.setRole(Role.MANAGER);
            listOfServices.add(service29);

            final Service service30 = new Service();
            service30.setCode("f-" + String.format("%04d", ++code));
            service30.setStartingTime(thursday.atTime(19, 0).atOffset(ZoneOffset.UTC));
            service30.setFinishingTime(thursday.atTime(20, 0).atOffset(ZoneOffset.UTC));
            service30.setRequiredEmployees(4);
            service30.setRole(Role.DRIVER);
            listOfServices.add(service30);

            final Service service31 = new Service();
            service31.setCode("f-" + String.format("%04d", ++code));
            service31.setStartingTime(thursday.atTime(19, 0).atOffset(ZoneOffset.UTC));
            service31.setFinishingTime(thursday.atTime(20, 0).atOffset(ZoneOffset.UTC));
            service31.setRequiredEmployees(2);
            service31.setRole(Role.MANAGER);
            listOfServices.add(service31);

            // VIERNES
            LocalDate friday = LocalDate.of(2024, 2, 9);

            final Service service32 = new Service();
            service32.setCode("f-" + String.format("%04d", ++code));
            service32.setStartingTime(friday.atTime(1, 0).atOffset(ZoneOffset.UTC));
            service32.setFinishingTime(friday.atTime(2, 0).atOffset(ZoneOffset.UTC));
            service32.setRequiredEmployees(2);
            service32.setRole(Role.DRIVER);
            listOfServices.add(service32);

            final Service service33 = new Service();
            service33.setCode("f-" + String.format("%04d", ++code));
            service33.setStartingTime(friday.atTime(8, 0).atOffset(ZoneOffset.UTC));
            service33.setFinishingTime(friday.atTime(10, 0).atOffset(ZoneOffset.UTC));
            service33.setRequiredEmployees(2);
            service33.setRole(Role.DRIVER);
            listOfServices.add(service33);

            final Service service34 = new Service();
            service34.setCode("f-" + String.format("%04d", ++code));
            service34.setStartingTime(friday.atTime(10, 0).atOffset(ZoneOffset.UTC));
            service34.setFinishingTime(friday.atTime(11, 0).atOffset(ZoneOffset.UTC));
            service34.setRequiredEmployees(4);
            service34.setRole(Role.DRIVER);
            listOfServices.add(service34);

            final Service service35 = new Service();
            service35.setCode("f-" + String.format("%04d", ++code));
            service35.setStartingTime(friday.atTime(10, 0).atOffset(ZoneOffset.UTC));
            service35.setFinishingTime(friday.atTime(11, 0).atOffset(ZoneOffset.UTC));
            service35.setRequiredEmployees(2);
            service35.setRole(Role.MANAGER);
            listOfServices.add(service35);

            final Service service36 = new Service();
            service36.setCode("f-" + String.format("%04d", ++code));
            service36.setStartingTime(friday.atTime(11, 0).atOffset(ZoneOffset.UTC));
            service36.setFinishingTime(friday.atTime(15, 0).atOffset(ZoneOffset.UTC));
            service36.setRequiredEmployees(8);
            service36.setRole(Role.DRIVER);
            listOfServices.add(service36);

            final Service service37 = new Service();
            service37.setCode("f-" + String.format("%04d", ++code));
            service37.setStartingTime(friday.atTime(11, 0).atOffset(ZoneOffset.UTC));
            service37.setFinishingTime(friday.atTime(15, 0).atOffset(ZoneOffset.UTC));
            service37.setRequiredEmployees(3);
            service37.setRole(Role.MANAGER);
            listOfServices.add(service37);

            final Service service38 = new Service();
            service38.setCode("f-" + String.format("%04d", ++code));
            service38.setStartingTime(friday.atTime(15, 0).atOffset(ZoneOffset.UTC));
            service38.setFinishingTime(friday.atTime(20, 0).atOffset(ZoneOffset.UTC));
            service38.setRequiredEmployees(8);
            service38.setRole(Role.DRIVER);
            listOfServices.add(service38);

            final Service service39 = new Service();
            service39.setCode("f-" + String.format("%04d", ++code));
            service39.setStartingTime(friday.atTime(15, 0).atOffset(ZoneOffset.UTC));
            service39.setFinishingTime(friday.atTime(20, 0).atOffset(ZoneOffset.UTC));
            service39.setRequiredEmployees(3);
            service39.setRole(Role.MANAGER);
            listOfServices.add(service39);

            final Service service40 = new Service();
            service40.setCode("f-" + String.format("%04d", ++code));
            service40.setStartingTime(friday.atTime(20, 0).atOffset(ZoneOffset.UTC));
            service40.setFinishingTime(friday.atTime(21, 0).atOffset(ZoneOffset.UTC));
            service40.setRequiredEmployees(2);
            service40.setRole(Role.DRIVER);
            listOfServices.add(service40);

            // SÁBADO
            LocalDate saturday = LocalDate.of(2024, 2, 10);

            final Service service41 = new Service();
            service41.setCode("f-" + String.format("%04d", ++code));
            service41.setStartingTime(saturday.atTime(8, 0).atOffset(ZoneOffset.UTC));
            service41.setFinishingTime(saturday.atTime(9, 0).atOffset(ZoneOffset.UTC));
            service41.setRequiredEmployees(2);
            service41.setRole(Role.DRIVER);
            listOfServices.add(service41);

            final Service service42 = new Service();
            service42.setCode("f-" + String.format("%04d", ++code));
            service42.setStartingTime(saturday.atTime(9, 0).atOffset(ZoneOffset.UTC));
            service42.setFinishingTime(saturday.atTime(10, 0).atOffset(ZoneOffset.UTC));
            service42.setRequiredEmployees(4);
            service42.setRole(Role.DRIVER);
            listOfServices.add(service42);

            final Service service43 = new Service();
            service43.setCode("f-" + String.format("%04d", ++code));
            service43.setStartingTime(saturday.atTime(9, 0).atOffset(ZoneOffset.UTC));
            service43.setFinishingTime(saturday.atTime(10, 0).atOffset(ZoneOffset.UTC));
            service43.setRequiredEmployees(2);
            service43.setRole(Role.MANAGER);
            listOfServices.add(service43);

            final Service service44 = new Service();
            service44.setCode("f-" + String.format("%04d", ++code));
            service44.setStartingTime(saturday.atTime(10, 0).atOffset(ZoneOffset.UTC));
            service44.setFinishingTime(saturday.atTime(15, 0).atOffset(ZoneOffset.UTC));
            service44.setRequiredEmployees(8);
            service44.setRole(Role.DRIVER);
            listOfServices.add(service44);

            final Service service45 = new Service();
            service45.setCode("f-" + String.format("%04d", ++code));
            service45.setStartingTime(saturday.atTime(10, 0).atOffset(ZoneOffset.UTC));
            service45.setFinishingTime(saturday.atTime(15, 0).atOffset(ZoneOffset.UTC));
            service45.setRequiredEmployees(3);
            service45.setRole(Role.MANAGER);
            listOfServices.add(service45);

            final Service service46 = new Service();
            service46.setCode("f-" + String.format("%04d", ++code));
            service46.setStartingTime(saturday.atTime(15, 0).atOffset(ZoneOffset.UTC));
            service46.setFinishingTime(saturday.atTime(20, 0).atOffset(ZoneOffset.UTC));
            service46.setRequiredEmployees(8);
            service46.setRole(Role.DRIVER);
            listOfServices.add(service46);

            final Service service47 = new Service();
            service47.setCode("f-" + String.format("%04d", ++code));
            service47.setStartingTime(saturday.atTime(15, 0).atOffset(ZoneOffset.UTC));
            service47.setFinishingTime(saturday.atTime(20, 0).atOffset(ZoneOffset.UTC));
            service47.setRequiredEmployees(3);
            service47.setRole(Role.MANAGER);
            listOfServices.add(service47);

            final Service service48 = new Service();
            service48.setCode("f-" + String.format("%04d", ++code));
            service48.setStartingTime(saturday.atTime(20, 0).atOffset(ZoneOffset.UTC));
            service48.setFinishingTime(saturday.atTime(21, 0).atOffset(ZoneOffset.UTC));
            service48.setRequiredEmployees(4);
            service48.setRole(Role.DRIVER);
            listOfServices.add(service48);

            // DOMINGO
            LocalDate sunday = LocalDate.of(2024, 2, 11);

            final Service service50 = new Service();
            service50.setCode("f-" + String.format("%04d", ++code));
            service50.setStartingTime(sunday.atTime(8, 0).atOffset(ZoneOffset.UTC));
            service50.setFinishingTime(sunday.atTime(9, 0).atOffset(ZoneOffset.UTC));
            service50.setRequiredEmployees(2);
            service50.setRole(Role.DRIVER);
            listOfServices.add(service50);

            final Service service51 = new Service();
            service51.setCode("f-" + String.format("%04d", ++code));
            service51.setStartingTime(sunday.atTime(9, 0).atOffset(ZoneOffset.UTC));
            service51.setFinishingTime(sunday.atTime(10, 0).atOffset(ZoneOffset.UTC));
            service51.setRequiredEmployees(4);
            service51.setRole(Role.DRIVER);
            listOfServices.add(service51);

            final Service service52 = new Service();
            service52.setCode("f-" + String.format("%04d", ++code));
            service52.setStartingTime(sunday.atTime(9, 0).atOffset(ZoneOffset.UTC));
            service52.setFinishingTime(sunday.atTime(10, 0).atOffset(ZoneOffset.UTC));
            service52.setRequiredEmployees(2);
            service52.setRole(Role.MANAGER);
            listOfServices.add(service52);

            final Service service53 = new Service();
            service53.setCode("f-" + String.format("%04d", ++code));
            service53.setStartingTime(sunday.atTime(10, 0).atOffset(ZoneOffset.UTC));
            service53.setFinishingTime(sunday.atTime(16, 0).atOffset(ZoneOffset.UTC));
            service53.setRequiredEmployees(8);
            service53.setRole(Role.DRIVER);
            listOfServices.add(service53);

            final Service service54 = new Service();
            service54.setCode("f-" + String.format("%04d", ++code));
            service54.setStartingTime(sunday.atTime(10, 0).atOffset(ZoneOffset.UTC));
            service54.setFinishingTime(sunday.atTime(16, 0).atOffset(ZoneOffset.UTC));
            service54.setRequiredEmployees(3);
            service54.setRole(Role.MANAGER);
            listOfServices.add(service54);

            final Service service55 = new Service();
            service55.setCode("f-" + String.format("%04d", ++code));
            service55.setStartingTime(sunday.atTime(16, 0).atOffset(ZoneOffset.UTC));
            service55.setFinishingTime(sunday.atTime(20, 0).atOffset(ZoneOffset.UTC));
            service55.setRequiredEmployees(4);
            service55.setRole(Role.DRIVER);
            listOfServices.add(service55);

            final Service service56 = new Service();
            service56.setCode("f-" + String.format("%04d", ++code));
            service56.setStartingTime(sunday.atTime(16, 0).atOffset(ZoneOffset.UTC));
            service56.setFinishingTime(sunday.atTime(20, 0).atOffset(ZoneOffset.UTC));
            service56.setRequiredEmployees(2);
            service56.setRole(Role.MANAGER);
            listOfServices.add(service56);

            final Service service57 = new Service();
            service57.setCode("f-" + String.format("%04d", ++code));
            service57.setStartingTime(sunday.atTime(20, 0).atOffset(ZoneOffset.UTC));
            service57.setFinishingTime(sunday.atTime(21, 0).atOffset(ZoneOffset.UTC));
            service57.setRequiredEmployees(2);
            service57.setRole(Role.DRIVER);
            listOfServices.add(service57);

            // ========================================================================================================

            // SERVICIOS REALES
            // crear las fechas y hora de inicio y fin de los servicios
            for (int i = 0; i < selectedFlights.size(); i++) {
                code++;
                String[] flightInfo = selectedFlights.get(i).split("/"); 
                String time = flightInfo[2].trim(); 
                LocalDate localDate = LocalDate.parse(flightInfo[0].trim()); //fecha
                LocalTime localTime = LocalTime.parse(time); //hora
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
            for (int i = 0; i < numberOfEmployees - 38; i++) {
                optimizationProblem.addEmployeeRoles(i, Role.AGENT);
                optimizationProblem.setEmployeeCode(i, String.format("%04d", i));
                optimizationProblem.setEmployeeTimePerDay(i, Duration.ofHours(timePerDay));
            }

            // creo los empleados necesitados para los servicios añadidos (driver y manager)
            // 70% conductores(27) y 30% coordinadores(11)
            for (int i = numberOfEmployees - 38; i < numberOfEmployees - 11; i++) {
                optimizationProblem.addEmployeeRoles(i, Role.DRIVER);
                optimizationProblem.setEmployeeCode(i, String.format("%04d", i));
            }

            for (int i = numberOfEmployees - 11; i < numberOfEmployees; i++) {
                optimizationProblem.addEmployeeRoles(i, Role.MANAGER);
                optimizationProblem.setEmployeeCode(i, String.format("%04d", i));
            }
            optimizationProblem.computeEmployeesAvailability(List.of());
            return optimizationProblem;
    }
}