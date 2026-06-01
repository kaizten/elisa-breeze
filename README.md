# Optimización y Análisis de Datos para la Planificación de Servicios PMR

Este repositorio contiene el código fuente y los los recursos desarrollados para el proyecto de un sistema de optimización para la asistencia a Personas con Movilidad Reducida en aeropuertos. 

## Descripción del Proyecto

El objetivo principal es optimizar la asignación de personal para cubrir servicios de asistencia PMR bajo restricciones operativas como ventanas temporales, roles específicos, descansos legales y capacidad de los agentes.


Se implementa una arquitectura que permite: 

1. Transformar registros de vuelos (Excel) en instancias de optimización
2. Comparar tres enfoques de resolución: ReferenceSolver, RandomSolver y CompactingSolver
3. Realizar comparaciones de distintas hipótesis y visualizar la operativa de soluciones

## Estructura del Repositorio

* **xlsx-to-json/**: Pipeline de preparación de datos en Java. Lee archivos Excel y genera instancias JSON, desde datos históricos de vuelos, o desde registros históricos de servicios PMR.
* **pmr-optimization/**: Motor de optimización principal. Incluye los tres algoritmos de resolución, además del registro automático de métricas.
* **analisis_resultados_py/**: Script para el análisis estadísticos de los resultados mediante el test de Wilcoxon.
* **data/:** Contiene los archivos Excel originales, las instancias JSON generadas, las soluciones de las experimentaciones, resultados de análisis de datos y métricas recogidas.
* **dashboards/**: Ejemplo de los dashboards creados, incluyendo código y captura.

## Requisitos Técnicos

* Java 17 o superior gestionado con Maven
* Python 3.12.6 con librerías: pandas, scipy

## Instrucciones de Ejecución

### 1. Generación de Instancias (Módulo xlsx-to-json)

Este módulo convierte los datos de los aeropuertos en problemas matemáticos. 

* **Instancias Sintéticas:** Ejecutar Controller.java. Esto generará automáticamente instancias variando el porcentaje de demanda y el aeropuerto. Si se requiere un análisis de datos de los vuelos, ejecutar FlightAnalyzer.java.
* **Instancia de datos reales del servicio PMR en Madrid**: Ejecutar TestMain.java dentro del paquete realTestMad. Esto procesará los datos operativos reales del aeropuerto. Si se necesita un diagnóstico de los datos, se puede ejecutar DataDiagnostics.java. Si se necesita crear instancias con distintas plantillas, ejecutar AgentAdder.java. Si se quiere crear una solución JSON de las asignaciones históricas ejecutar RealCaseSolutionCreator, pero éste solo funciona si los datos son coherentes y no incumplen restricciones.

### 2. Ejecución de la Optimización (Módulo pmr-optimization)

Una vez que los archivos JSON están en la carpeta data/instances o en la carpeta data/realCaseTest/instances/:

* Ejecutar la clase Main.java, ajustando las rutas al tipo de experimentación a realizar.
* El programa resolverá sistemáticamente cada instancia usando secuencialmente los tres algoritmos.
* Los resultados se guardarán automáticamente en un fichero txt, que luego se podrá exportar a Excel.

### 3. Validación Estadística (Módulo analisis_resultados_py)

Para comprobar la significancia de los resultandos y obtener los p-valores: 

> cd analisis_resultados_py

> python analisis_wilcoxon.py
