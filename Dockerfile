FROM maven:3.8.5-openjdk-17
EXPOSE 8090
ADD target/fhir-subscription-1.0-SNAPSHOT.jar fhir-subscription-1.0-SNAPSHOT.jar
CMD ["java", "-jar", "/fhir-subscription-1.0-SNAPSHOT.jar"]