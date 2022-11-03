# Base image
FROM maven:3.8.5-openjdk-11-slim
# Argument with default value
ARG suiteFile=testng.xml
# Copy code from local to image
COPY ./ /app
# Specify working directory
WORKDIR /app
# Execute command at creation of image
RUN mvn clean compile
# Command to be executed at start of container
CMD mvn test -Dsurefire.suiteXmlFiles=$suiteFile -e