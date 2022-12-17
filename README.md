# Modular-Automation-Framework
___
<!--

![](https://img.shields.io/github/license/arvind142/Modular-Automation-Testing.svg)

-->
![GitHub repo size](https://img.shields.io/github/repo-size/arvind142/Modular-Automation-Testing)

___

**Framework** to cater basic needs of automation tester, where tester can mainly focus on scripting!!!
you get generic methods for your testing.

Few example of what you get:
1. wrapper with **Selenium Library** to help you perform Browser testing effortlessly.

___

**Is Framework Development completed**? No!, but I've few sample scripts which you can execute to see how logging and reporting works(refer to execution section to see how you can execute test script).

___

|Feature| Library or tool  |
|:--|:--|
| HTML Report	|  _Extent Reporting_ |
| Logging		|	 _Log-back logger_ |
|Automatic Driver version management | _WDM(WebDriverManager)_ |
|Containerization | _Docker_|

___

### How to Execute your scripts in container?

> By defualt testng.xml will be executed.
#### Start execution
	docker-compose up -d
#### Cleanup after execution stopped
	docker-compose down

**Test Report location**

> test-output/(Highest_Number)/

> result.html - test result report
> summary/ - where you can find list of pass/fail/skip cases
> assets/ - where you can store screenshots/payloads

**Test Logs**
> logs/

<!--
#### Tested on!
![](https://img.shields.io/badge/Windows-0078D6?style=for-the-badge&logo=windows&logoColor=white) ![](https://img.shields.io/badge/Ubuntu-E95420?style=for-the-badge&logo=ubuntu&logoColor=white)
-->
