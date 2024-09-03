FROM openjdk:17
EXPOSE 1989
ADD target/auth-details.jar auth-details.jar
ENTRYPOINT ["java","-jar","auth-details.jar"]