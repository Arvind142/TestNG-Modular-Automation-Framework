# Base image
FROM maven:3.8.5-openjdk-11-slim

# what all should be copied from local
COPY ./ /Modular-Automation-Framework

# directory where we would be working
WORKDIR /Modular-Automation-Framework

# command to be excuted whenever user run image/starts container
CMD ["mvn","clean","compile","test"]