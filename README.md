#### struts2 app from render
Below is a complete Maven pom.xml for your Struts 2 Ticket REST API, configured for:

Java 8

Apache Struts 2

Apache Tomcat 9

REST endpoints returning JSON

Jackson JSON serialization

PostgreSQL JDBC

HikariCP connection pooling

Maven WAR packaging

I'm using Struts 2.5.33 as an example baseline because it supports Java 8 and the javax.servlet API. It is an older, security-sensitive release, so this is a compatibility starting point, not a recommendation to deploy it publicly without reviewing and upgrading to a supported, patched Struts release.

#### docker container running struts2 for render

Step: Test the build locally (optional but recommended)
If Docker is installed on your computer, execute:

docker build -t struts2-app .

docker run --name struts2-app -p 10000:10000 -e DB_URL="jdbc:postgresql://external url from render" ` -e DB_USERNAME="tickets_ay9l_user" `-e DB_PASSWORD="pw" `struts2-app

1. List all tickets
   GET

http://localhost:10000/api/tickets

Copy

2. Retrieve a specific ticket

http://localhost:10000/api/tickets/1001

3. Create a ticket

POST http://localhost:10000/api/tickets

docker exec struts2-app sh -c "ls -lah /usr/local/tomcat/logs/"

docker exec struts2-app sh -c "grep -n -A 30 -B 10 -E 'SEVERE|Exception|Caused by' /usr/local/tomcat/logs/*"

docker exec struts2-app sh -c "env | grep -E 'DB_|DATABASE|JDBC'"
