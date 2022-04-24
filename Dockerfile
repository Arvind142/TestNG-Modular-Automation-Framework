FROM maven:3.8.5-openjdk-11-slim

COPY ./ /Modular-Automation-Framework

WORKDIR /Modular-Automation-Framework

CMD ["mvn","clean","compile","test"]