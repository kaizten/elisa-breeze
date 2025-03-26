package com.kaizten.prmp.conversor;

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
            int numberOfDays) {
        final int numberOfServices = selectedFlights.size();
        final int numberOfEmployees = numberOfServices + 3 * numberOfDays;
        final PersonsReducedMobilityProblem optimizationProblem = new PersonsReducedMobilityProblem(
                numberOfServices,
                numberOfEmployees);
        optimizationProblem.setAirport(airport);
        // crear las fechas y hora de inicio y fin de los servicios
        final List<Service> listOfServices = new ArrayList<>();
        for (int i = 0; i < numberOfServices; i++) {
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
            newService.setCode(String.format("%04d", i));
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
        // solo le asigno rol y codigo, lo demas vacío o defualt como ya está puesto
        for (int i = 0; i < numberOfEmployees; i++) {
            optimizationProblem.addEmployeeRoles(i, Role.AGENT);
            optimizationProblem.setEmployeeCode(i, String.format("%04d", i));
        }
        optimizationProblem.computeEmployeesAvailability(List.of());
        return optimizationProblem;
    }
}