rem Launch the Designer application (args: JDBC connection string, user name, password and optional collector name)
java -Xdebug -Xrunjdwp:transport=dt_socket,address=8998,server=y -jar oee-apps-3.3.0.jar DESIGNER jdbc:hsqldb:hsql://localhost/OEE SA