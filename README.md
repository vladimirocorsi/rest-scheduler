# rest-scheduler

A simple program scheduling Groovy scripts via a RESTful API. 
Technical stack is Java with Jersey JAX-RS library.

Usage
  - To build form source, go to mavenProject and issue a "mvn clean package". Maven 3.3.3 was used. The jar file and lib folder will be under "target" subfolder.
  - To run the server go to target folder and issue a "java -jar scheduler-0.0.1.jar". Requires Java 8. TCP port 8085 must be free. 
  - To test the REST API you can use the SoapUI project included in soapUI_5_2_project folder. SoapUI 5.2.1 was used.
  
Documentation
  - Java code is documented in source files.
  - REST API is documented in source code of Java class com.vcorsi.rest-scheduler.scheduler.rest.TaskService.
  - A WADL file will be available at http://localhost:8085/application.wadl.
