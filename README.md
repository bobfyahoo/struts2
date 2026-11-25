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

docker run -p 10000:10000 struts2-app

Open: http://localhost:10000

