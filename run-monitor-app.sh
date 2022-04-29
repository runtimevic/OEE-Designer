# Launch the Monitor application (args: JDBC connection string, user name, password and optional collector name)
java -p $JAVAFX_HOME/lib --add-modules javafx.controls,javafx.fxml,javafx.web -Dlog4j2.configurationFile=config/logging/log4j2.xml -jar oee-apps-3.7.0.jar MONITOR jdbc:hsqldb:hsql://localhost/OEE SA  