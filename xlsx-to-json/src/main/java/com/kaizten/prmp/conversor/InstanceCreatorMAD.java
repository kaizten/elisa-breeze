package com.kaizten.prmp.conversor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.kaizten.prmp.domain.Service;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.problem.Role;

public class InstanceCreatorMAD {

    public PersonsReducedMobilityProblem createInstance(
            List<String> selectedFlights,
            String airport,
            int numberOfDays) {
        final int numberOfServices = selectedFlights.size()+107+12*numberOfDays;
        final int numberOfEmployees = selectedFlights.size() + 12 * numberOfDays + 510;
        final PersonsReducedMobilityProblem optimizationProblem = new PersonsReducedMobilityProblem(
                numberOfServices,
                numberOfEmployees);
        optimizationProblem.setAirport(airport);
        // crear las fechas y hora de inicio y fin de los servicios
        final List<Service> listOfServices = new ArrayList<>();


        //Añado servicios base las 24h los 7 dias: 
        Role[] roles = {Role.DRIVER, Role.DRIVER, Role.RAMP_MANAGER, Role.MANAGER}; //BASE: 2 driver, 1 manager, 1 ramp manager
        //iterar a lo largo de los días 
        int code = 0; 
        for (int i = 0; i < numberOfDays; i++) {
            LocalDate date = LocalDate.of(2024, 2, 5).plusDays(i);
            for (int shift = 0; shift < 3; shift++) { // turnos por día (3 porq son de 8h)
                int start = (shift * 8) % 24;
                int finish = (start + 8) % 24;

                OffsetDateTime startingTime = date.atTime(start, 0).atOffset(ZoneOffset.UTC);
                OffsetDateTime finishingTime = date.atTime(finish, 0).atOffset(ZoneOffset.UTC);

                if (finishingTime.getHour() < startingTime.getHour()){
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

        //SERVICIOS ADICIONALES
        //LUNES
        LocalDate monday = LocalDate.of(2024, 2, 5);

        final Service newService1 = new Service();
        newService1.setCode("f-" + String.format("%04d", code));
        newService1.setStartingTime(monday.atTime(0, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(monday.atTime(1, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(5);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService1);

        final Service newService2 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(monday.atTime(0, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(monday.atTime(1, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService2);

        final Service newService3 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(monday.atTime(0, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(monday.atTime(1, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService3);

        final Service newService4 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(monday.atTime(1, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(monday.atTime(2, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService4);

        final Service newService5 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(monday.atTime(5, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(monday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(5);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService5);

        final Service newService6 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(monday.atTime(5, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(monday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService6);

        final Service newService7 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(monday.atTime(5, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(monday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService7);

        final Service newService8 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(monday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(monday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(8);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService8);

        final Service newService9 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(monday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(monday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService9);

        final Service newService10 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(monday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(monday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService10);

        final Service newService11 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(monday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(monday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(8);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService11);

        final Service newService12 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(monday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(monday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService12);

        final Service newService13 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(monday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(monday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService13);

        final Service newService14 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(monday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(monday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
        newService1.setRequiredEmployees(8);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService14);

        final Service newService15 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(monday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(monday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService15);

        final Service newService16 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(monday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(monday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService16);

        //Martes
        LocalDate tuesday = LocalDate.of(2024, 2, 6);

        final Service newService17 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(tuesday.atTime(0, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(tuesday.atTime(1, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(5);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService17);

        final Service newService18 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(tuesday.atTime(0, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(tuesday.atTime(1, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService18);

        final Service newService19 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(tuesday.atTime(0, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(tuesday.atTime(1, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService19);

        final Service newService20 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(tuesday.atTime(1, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(tuesday.atTime(2, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService20);

        final Service newService21 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(tuesday.atTime(3, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(tuesday.atTime(5, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService21);

        final Service newService22 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(tuesday.atTime(5, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(tuesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(5);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService22);

        final Service newService23 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(tuesday.atTime(5, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(tuesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService23);

        final Service newService24 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(tuesday.atTime(5, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(tuesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService24);

        final Service newService25 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(tuesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(tuesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(8);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService25);

        final Service newService26 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(tuesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(tuesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService26);

        final Service newService27 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(tuesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(tuesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService27);

        final Service newService28 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(tuesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(tuesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(8);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService28);

        final Service newService29 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(tuesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(tuesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService29);

        final Service newService30 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(tuesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(tuesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService30);

        final Service newService31 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(tuesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(tuesday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
        newService1.setRequiredEmployees(8);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService31);

        final Service newService32 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(tuesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(tuesday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService32);

        final Service newService33 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(tuesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(tuesday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService33);

        //WEDNESDAY
        LocalDate wednesday = LocalDate.of(2024, 2, 7);

        final Service newService34 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(wednesday.atTime(0, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(wednesday.atTime(2, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService34);

        final Service newService35 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(wednesday.atTime(5, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(wednesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(5);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService35);

        final Service newService36 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(wednesday.atTime(5, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(wednesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService36);

        final Service newService37 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(wednesday.atTime(5, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(wednesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService37);

        final Service newService38 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(wednesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(wednesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(8);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService38);

        final Service newService39 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(wednesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(wednesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService39);

        final Service newService40 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(wednesday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(wednesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService40);

        final Service newService41 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(wednesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(wednesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(8);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService41);

        final Service newService42 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(wednesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(wednesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService42);

        final Service newService43 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(wednesday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(wednesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService43);

        final Service newService44 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(wednesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(wednesday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
        newService1.setRequiredEmployees(8);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService44);

        final Service newService45 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(wednesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(wednesday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService45);

        final Service newService46 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(wednesday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(wednesday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService46);

        //THURSDAY
        LocalDate thursday = LocalDate.of(2024, 2, 8);

        final Service newService47 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(thursday.atTime(0, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(thursday.atTime(2, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService47);

        final Service newService48 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(thursday.atTime(3, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(thursday.atTime(4, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService48);

        final Service newService49 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(thursday.atTime(5, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(thursday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(5);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService49);

        final Service newService50 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(thursday.atTime(5, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(thursday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService50);

        final Service newService51 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(thursday.atTime(5, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(thursday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService51);

        final Service newService52 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(thursday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(thursday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(8);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService52);

        final Service newService53 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(thursday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(thursday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService53);

        final Service newService54 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(thursday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(thursday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService54);

        final Service newService55 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(thursday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(thursday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(8);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService55);

        final Service newService56 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(thursday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(thursday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService56);

        final Service newService57 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(thursday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(thursday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService57);

        final Service newService58 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(thursday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(thursday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
        newService1.setRequiredEmployees(8);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService58);

        final Service newService59 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(thursday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(thursday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService59);

        final Service newService60 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(thursday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(thursday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService60);

        //FRIDAY
        LocalDate friday = LocalDate.of(2024, 2, 9);

        final Service newService61 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(friday.atTime(0, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(friday.atTime(1, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(5);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService61);

        final Service newService62 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(friday.atTime(0, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(friday.atTime(1, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService62);

        final Service newService63 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(friday.atTime(0, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(friday.atTime(1, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService63);

        final Service newService64 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(friday.atTime(4, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(friday.atTime(5, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService64);

        final Service newService65 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(friday.atTime(5, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(friday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(5);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService65);

        final Service newService66 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(friday.atTime(5, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(friday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService66);

        final Service newService67 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(friday.atTime(5, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(friday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService67);

        final Service newService68 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(friday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(friday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(8);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService68);

        final Service newService69 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(friday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(friday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService69);

        final Service newService70 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(friday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(friday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService70);

        final Service newService71 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(friday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(friday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(8);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService71);

        final Service newService72 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(friday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(friday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService72);

        final Service newService73 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(friday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(friday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService73);

        final Service newService74 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(friday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(friday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
        newService1.setRequiredEmployees(8);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService74);

        final Service newService75 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(friday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(friday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService75);

        final Service newService76 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(friday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(friday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService76);

        //SATURDAY
        LocalDate saturday = LocalDate.of(2024, 2, 10);

        final Service newService77 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(saturday.atTime(0, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(saturday.atTime(1, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(5);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService77);

        final Service newService78 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(saturday.atTime(0, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(saturday.atTime(1, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService78);

        final Service newService79 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(saturday.atTime(0, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(saturday.atTime(1, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService79);

        final Service newService80 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(saturday.atTime(1, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(saturday.atTime(2, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService80);

        final Service newService81 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(saturday.atTime(5, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(saturday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(5);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService81);

        final Service newService82 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(saturday.atTime(5, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(saturday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService82);

        final Service newService83 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(saturday.atTime(5, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(saturday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService83);

        final Service newService84 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(saturday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(saturday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(8);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService84);

        final Service newService85 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(saturday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(saturday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService85);

        final Service newService86 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(saturday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(saturday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService86);

        final Service newService87 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(saturday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(saturday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(8);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService87);

        final Service newService88 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(saturday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(saturday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService88);

        final Service newService89 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(saturday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(saturday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService89);

        final Service newService90 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(saturday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(saturday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
        newService1.setRequiredEmployees(8);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService90);

        final Service newService91 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(saturday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(saturday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService91);

        final Service newService92 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(saturday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(saturday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService92);

        //SUNDAY
        LocalDate sunday = LocalDate.of(2024, 2, 11);

        final Service newService93 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(sunday.atTime(0, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(sunday.atTime(1, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(5);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService93);

        final Service newService94 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(sunday.atTime(0, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(sunday.atTime(1, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService94);

        final Service newService95 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(sunday.atTime(0, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(sunday.atTime(1, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService95);

        final Service newService96 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(sunday.atTime(5, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(sunday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(5);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService96);

        final Service newService97 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(sunday.atTime(5, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(sunday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService97);

        final Service newService98 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(sunday.atTime(5, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(sunday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(2);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService98);

        final Service newService99 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(sunday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(sunday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(8);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService99);

        final Service newService100 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(sunday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(sunday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService100);

        final Service newService101 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(sunday.atTime(6, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(sunday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService101);

        final Service newService102 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(sunday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(sunday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(8);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService102);

        final Service newService103 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(sunday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(sunday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService103);

        final Service newService104 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(sunday.atTime(14, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(sunday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService104);

        final Service newService105 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(sunday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(sunday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
        newService1.setRequiredEmployees(8);
        newService1.setRole(Role.DRIVER);
        listOfServices.add(newService105);

        final Service newService106 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(sunday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(sunday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.MANAGER);
        listOfServices.add(newService106);

        final Service newService107 = new Service();
        newService1.setCode("f-" + String.format("%04d", ++code));
        newService1.setStartingTime(sunday.atTime(22, 0).atOffset(ZoneOffset.UTC));
        newService1.setFinishingTime(sunday.atTime(00, 0).atOffset(ZoneOffset.UTC).plusDays(1));
        newService1.setRequiredEmployees(3);
        newService1.setRole(Role.RAMP_MANAGER);
        listOfServices.add(newService107);


        // ========================================================================================================

        //SERVICIOS REALES

        for (int i = 0; i < selectedFlights.size(); i++) {
            code++;
            String[] flightInfo = selectedFlights.get(i).split("/"); // [0] => fecha, [1] => tipo de vuelo, [2] => hora
            // System.out.println("Flight info: " + flightInfo[0] + " " + flightInfo[1] + "
            // " + flightInfo[2]);
            String time = flightInfo[2].trim();
            LocalDate localDate = LocalDate.parse(flightInfo[0].trim()); // fecha
            LocalTime localTime = LocalTime.parse(time); // hora
            // Combinar la fecha y la hora en un LocalDateTime
            LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
            // Crear el OffsetDateTime usando UTC
            OffsetDateTime flightTime = localDateTime.atOffset(ZoneOffset.UTC);
            // Ahora, offsetDateTime contiene la fecha y hora con el Offset UTC
            // System.out.println("OffsetDateTime: " + flightTime);
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
        
        
        System.out.println(listOfServices.size());
        for (int i = 0; i < listOfServices.size(); i++) {
            Service service = listOfServices.get(i);
            System.out.println("Service #" + i + ": " + service.getCode() + " " + service.getStartingTime());
        }

        Collections.sort(listOfServices); //da error aqui??
        for (int i = 0; i < listOfServices.size(); i++) {
            Service service = listOfServices.get(i);
            System.out.println(service.getCode()+ " " + service.getStartingTime());
            optimizationProblem.setServiceCode(i, service.getCode());
            optimizationProblem.setServiceTimes(i, service.getStartingTime(), service.getFinishingTime());
            optimizationProblem.setServiceRole(i, service.getRole());
            optimizationProblem.setServiceRequiredEmployees(i, service.getRequiredEmployees());
        }


        // creo x empleados nuevos, rol AGENTE
        // solo le asigno rol y codigo, lo demas vacío o defualt como ya está puesto
        for (int i = 0; i < numberOfEmployees-510; i++) {
            optimizationProblem.addEmployeeRoles(i, Role.AGENT);
            optimizationProblem.setEmployeeCode(i, String.format("%04d", i));
        }

        //Empleados añadidos
        //294 DRIVERS
        //108 MANAGER
        //108 RAMP_MANAGER
        //TOTAl: 510 empleados extra
        for (int i = numberOfEmployees-510; i < numberOfEmployees-216; i++) {
            optimizationProblem.addEmployeeRoles(i, Role.DRIVER);
            optimizationProblem.setEmployeeCode(i, String.format("%04d", i));
        }
        for (int i = numberOfEmployees-216; i < numberOfEmployees-108; i++) {
            optimizationProblem.addEmployeeRoles(i, Role.MANAGER);
            optimizationProblem.setEmployeeCode(i, String.format("%04d", i));
        }
        for (int i = numberOfEmployees-108; i < numberOfEmployees; i++) {
            optimizationProblem.addEmployeeRoles(i, Role.RAMP_MANAGER);
            optimizationProblem.setEmployeeCode(i, String.format("%04d", i));
        }


        optimizationProblem.computeEmployeesAvailability(List.of());
        return optimizationProblem;
    }
}