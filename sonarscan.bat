mvn clean verify sonar:sonar^
 -Dsonar.projectKey=crud^
 -Dsonar.host.url=http://localhost:9000^
 -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/**/*.xml^
 -Dsonar.login=
