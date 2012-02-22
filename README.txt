JSieve is built using Maven 2 (http://maven.apache.org).

To build all modules type:
% mvn install

To release: 
%mvn clean javadoc:aggregate site install
%mvn release:clean
%mvn release:perform
Read the rest on http://www.apache.org/dev/publishing-maven-artifacts.html
