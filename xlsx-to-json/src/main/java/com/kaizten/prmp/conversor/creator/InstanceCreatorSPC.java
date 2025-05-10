package com.kaizten.prmp.conversor.creator;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import com.kaizten.prmp.domain.Service;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.problem.Role;
import com.kaizten.prmp.io.PersonsReducedMobilityProblemToJson;
import com.kaizten.utils.io.KaiztenFile;

public class InstanceCreatorSPC {

    public void createInstance( //TODO bucle y en cada uno sumarle un empleado hasta 30, y devolver lista de problemas con numero de empleados variados. guardar con numero de agentes en el nombre 
            List<String> selectedFlights,
            String airport,
            int numberOfDays, int maxOverlaps, double percentage, String INSTANCEDIRECTORY) throws Exception {
                
        for (int agents = 1; agents <= 30; agents++) { //30 intancias => agentes de 1 a 30
            final int numberOfServices = selectedFlights.size()+ 4*numberOfDays; //añado 4*dias de servicio por los servicios adicionales
            //regla general => un empleado tiene 8h y puede hacer unos 5 servicios al dia => (vuelos/7)/5 => y cojo el maximo (ceil) pa no tener menos
            //int serviciosPorEmpleadoPorSemana = 7 * 5; // 5 servicios por día, 7 días a la semana
            //int neededAgents = Math.max(maxOverlaps, (int) Math.ceil((double) selectedFlights.size() / serviciosPorEmpleadoPorSemana)); // número maximo de solapamientos
            // Managers y drivers para servicios adicionales (4 por día) + 1 de respuesto de cada
            int numberOfManagers = 2; 
            int numberOfDrivers = 2;
            int numberOfEmployees = agents + numberOfManagers + numberOfDrivers; 

            final PersonsReducedMobilityProblem optimizationProblem = new PersonsReducedMobilityProblem(
                    numberOfServices,
                    numberOfEmployees);
            optimizationProblem.setAirport(airport);
            // crear las fechas y hora de inicio y fin de los servicios
            final List<Service> listOfServices = new ArrayList<>();

            /*Añado los 4 servicios adicionales
            7:30 a 14:30 --> 1 Coordinador y 1 Conductor
            14:30 a 21:30 --> 1 Coordinador y 1 Conductor
            */

            Role[] roles = {Role.MANAGER, Role.DRIVER};
            int code = 0; 
            for (int i = 0; i < numberOfDays; i++) {
                LocalDate date = LocalDate.of(2024, 2, 5).plusDays(i); //pongo la fecha de inicio a mano ya que no lo puedo coger de los datos porque no lo especifica
                for (int k = 0; k<2; k++) {
                    for (Role role: roles) {
                        final Service service = new Service();
                        service.setCode("f-"+String.format("%04d", code));
                        code++;
                        if (k == 0) {
                            service.setStartingTime(OffsetDateTime.of(date, LocalTime.of(07, 30), ZoneOffset.UTC));
                            service.setFinishingTime(OffsetDateTime.of(date, LocalTime.of(14, 30), ZoneOffset.UTC));
                        } else {
                            service.setStartingTime(OffsetDateTime.of(date, LocalTime.of(14, 30), ZoneOffset.UTC));
                            service.setFinishingTime(OffsetDateTime.of(date, LocalTime.of(21, 30), ZoneOffset.UTC));
                        }
                        service.setRequiredEmployees(1);
                        service.setRole(role);
                        listOfServices.add(service);
                    }
                }
            }

            //Añado los vuelos seleccionados
            for (int i = 0; i < selectedFlights.size(); i++) { //coge el tamaño del selectedFlights
                String[] flightInfo = selectedFlights.get(i).split("/"); // [0] => fecha, [1] => tipo de vuelo, [2] => hora
                // System.out.println("Flight info: " + flightInfo[0] + " " + flightInfo[1] + "
                // " + flightInfo[2]);
                String time = flightInfo[2].trim();
                if (time.length() == 4) {
                    time = "0" + time; // Agregar un cero al principio si la hora tiene solo un dígito
                }
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
                newService.setCode(String.format("%04d", i+4*numberOfDays));
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

            // Creo los 4 empleados adicionales
            int employeecode = 0;
            for (int i = 0; i<2; i++){
                for (Role role: roles) {
                    optimizationProblem.addEmployeeRoles(employeecode, role);
                    optimizationProblem.setEmployeeCode(employeecode, String.format("%04d", employeecode));
                    employeecode++;
                }
            }

            // creo x empleados nuevos, rol AGENTE
            // solo le asigno rol y codigo, lo demas vacío o defualt como ya está puesto
            for (int i = 4; i < numberOfEmployees; i++) {
                optimizationProblem.addEmployeeRoles(i, Role.AGENT);
                optimizationProblem.setEmployeeCode(i, String.format("%04d", i)); 
            }

            optimizationProblem.computeEmployeesAvailability(List.of());

            //guardar el problema en un archivo json
            PersonsReducedMobilityProblemToJson toJson = new PersonsReducedMobilityProblemToJson();
            JSONObject json = toJson.apply(optimizationProblem);
            KaiztenFile.writeToFile(new File(INSTANCEDIRECTORY + airport + "-" + percentage + "-agents" + agents + ".json"), json);
        }
    }
        
}