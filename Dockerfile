# we will use openjdk 8 with alpine as it is a very small linux distro
FROM openjdk:8-jre-alpine3.9
 
# copy the packaged jar file into our docker image
COPY target/ProPetsAccounting-0.0.1-SNAPSHOT.jar /ProPetsAccounting-0.0.1-SNAPSHOT.jar

ENV DB_URL=mongodb+srv://user:kC01Jaj6QjDhnUbx@cluster0.orivv.mongodb.net/proPetsAccountingDB?retryWrites=true&w=majority
ENV ORIGIN_URL=*
ENV VALIDATION_SERVICE_URL=https://propetsvalidation.herokuapp.com/account/en/v1
 
# set the startup command to execute the jar
CMD ["java", "-jar", "/ProPetsAccounting-0.0.1-SNAPSHOT.jar"]