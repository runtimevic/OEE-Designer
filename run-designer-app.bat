rem Launch the Designer application (args: JDBC connection string, user name, password and optional collector name)
start %JAVA_HOME%/bin/javaw.exe -p %JAVAFX_HOME%\lib --add-modules javafx.controls,javafx.fxml,javafx.web -jar oee-apps-3.6.0.jar DESIGNER jdbc:hsqldb:hsql://localhost/OEE SA 
 