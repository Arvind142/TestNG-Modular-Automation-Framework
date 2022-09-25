FROM maven:3.8.5-openjdk-11-slim

LABEL maintainer=arvindchoudhary142@yahoo.in

LABEL image-name=TestNG-Modular-Automation-Framework

COPY ./ /app

WORKDIR /app

RUN mvn clean compile

CMD mvn test