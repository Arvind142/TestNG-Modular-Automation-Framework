
# Modular-Automation-Framework
___  
<!--  
  
![](https://img.shields.io/github/license/arvind142/Modular-Automation-Testing.svg)  
  
-->  
![GitHub repo size](https://img.shields.io/github/repo-size/arvind142/Modular-Automation-Testing)  
![Lines of code](https://img.shields.io/tokei/lines/github/arvind142/Modular-Automation-Testing)

> Framework is under development and will always be! 😊

#### Feature List:
1. Extent Test Report on test completion.
2. TestNG_Base class to takecare of framework initialization.(**Make sure to extent TestNG_Base on your Test Classes 😁**).
3. @TestDescription annotation to add below details on TestCases:
    1. *Author*: Testcase Author
    2. *Test Description*: Test case description (It reflects under testcase name in Report).
4.  use @Slf4j to add logs to your test scripts.
5. Works with below mentioned TestNG features:
    1. XML and Class Base test execution.
    2. dataProvider (run same test method multiple times).
    3. invocationCount (no. of time tests to be invoked).
    4. timeout (time in which test method should get executed).
    5. dependsOnMethods & dependsOnGroups.

### Feature Status
|Feature |Working(status) |
|--|--|
|Logging|Y|
|HTML Report (Extent Report)|Y|
|Parallel Test Execution|Y |
|@TestDescription|Y|
|RemoteExecution |Y|
|Containerized Execution|Y|


___  

**Is Framework Development completed?** No!, but I've few sample scripts which you can execute to see how logging and reporting works.

___  

## containerized test execution
[docker-image-of-your-testing-project](https://arvind-choudhary.medium.com/docker-image-of-your-testing-project-92338f996f6f)

### How to Execute your scripts in container?

> By defualt testng.xml will be executed.  to learn more on how to control which xml has to be executed read above mentioned medium article.

#### Start execution
	 docker-compose up -d
#### Cleanup after execution stopped
	 docker-compose down  

***Test Report location***
> test-output/(Highest_Number)/

> result.html - test result report  
> summary.txt - where you can find list of pass/fail/skip cases  
> assets/ - where you can store screenshots/payloads

*Test Logs*
> logs/

<!--  
#### Tested on!  
![](https://img.shields.io/badge/Windows-0078D6?style=for-the-badge&logo=windows&logoColor=white) ![](https://img.shields.io/badge/Ubuntu-E95420?style=for-the-badge&logo=ubuntu&logoColor=white)  
-->