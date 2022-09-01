package com.core.framework.htmlreporter;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.gherkin.model.And;
import com.aventstack.extentreports.gherkin.model.Given;
import com.aventstack.extentreports.gherkin.model.Then;
import com.aventstack.extentreports.gherkin.model.When;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.core.framework.testLogs.StepLogger;
import com.intuit.karate.Results;
import com.intuit.karate.core.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;


@Slf4j
public class BDDReporter {
    private static BDDReporter reporter=null;

    private ExtentSparkReporter htmlReporter;

    private ExtentReports extentReport;

    private ExtentTest extentTest;

    private String featureName = "";

    private String scenarioName = "";

    private String reportingFolder;

    private String assetFolder;

    private String summaryFolder;

    private String deviceDetails;




    public boolean stopReporting() {
        extentReport.flush();
        log.debug("report flush");
        log.info("Report url: "+htmlReporter.getFile().getAbsolutePath());
        return true;
    }

    public void setSystemVars(Properties props) {

        if (props == null)
            return;
        if (props.isEmpty())
            return;
        try {
            this.deviceDetails = System.getProperty("os.name")+"/"+props.getProperty("browser");
        }
        catch(NullPointerException ex) { this.deviceDetails =null;}
        //adding property file details onto report
        for (Object key : props.keySet()) {
            extentReport.setSystemInfo(key.toString(), props.getProperty(key.toString()));
        }
    }

    public String getReportingFolder() {
        return reportingFolder;
    }

    public void setReportingFolder(String reportingFolder) {
        this.reportingFolder = reportingFolder;
    }

    public String getAssetFolder() {
        return assetFolder;
    }

    public void setAssetFolder(String assetFolder) {
        this.assetFolder = assetFolder;
    }

    public String getSummaryFolder() {
        return summaryFolder;
    }

    public void setSummaryFolder(String summaryFolder) {
        this.summaryFolder = summaryFolder;
    }

    public static synchronized BDDReporter initializeReporting(String reportingFolderPath) {
        if(reporter==null) {
            File folder = new File(reportingFolderPath);
            // folder check - if not present create one
            if (!folder.exists()) {
                folder.mkdirs();
            }
            reporter = new BDDReporter(reportingFolderPath);
        }
        return reporter;
    }
    private BDDReporter(String resultFolder){
        log.trace("constructor initialized");
        this.reportingFolder = resultFolder;
    }

    public void initializeReportingVars(){
        log.debug("BDD HTML Reporter called");
        log.debug("Output folder created @ "+reportingFolder);
        // reporting initialized
        htmlReporter = new ExtentSparkReporter(reportingFolder + "/cucumber-results.html");
        this.reportingFolder = reportingFolder;
        extentReport = new ExtentReports();
        if (new File("src/test/resources/extent-config.xml").exists()) {
            try {
                htmlReporter.loadXMLConfig("src/test/resources/extent-config.xml");
            } catch (Exception e) {
                //do Nothing go with default config
            }
        }
        log.debug("html reporting initialized");
    }

    public void generateBDDReport(Results results){
        try{
            extentReport.attachReporter(htmlReporter);
            List<ScenarioResult> scenarioResults = getScenarioResults(results);
            scenarioResults.forEach(scenarioResult->{
                Feature feature = getFeature(scenarioResult);
                ExtentTest featureNode = createFeatureNode(feature);
                Scenario scenario = scenarioResult.getScenario();
                ExtentTest scenarioNode = createScenarioNode(featureNode,scenario.getName());
                scenarioResult.getStepResults().forEach(stepResult -> {
                    addScenarioStep(scenarioNode,stepResult.getStep(),stepResult.getResult());
                });
            });
            stopReporting();
        }
        catch (Exception e){
            log.error("Exception while generating test report");
            e.printStackTrace();
        }
    }



    public List<ScenarioResult> getScenarioResults(Results results){
        return results.getScenarioResults().collect(Collectors.toList());
    }
    public Feature getFeature(ScenarioResult result){
        return result.getScenario().getFeature();
    }
    public ExtentTest createFeatureNode(Feature feature){
        if(this.featureName.equalsIgnoreCase(feature.getName())){
            return extentTest;
        }
        this.featureName=feature.getName();
        return extentReport.createTest(com.aventstack.extentreports.gherkin.model.Feature.class,feature.getName(),feature.getDescription());
    }

    public ExtentTest createScenarioNode(ExtentTest featureNode,String scenarioName){
        if(this.scenarioName.equalsIgnoreCase(scenarioName)){
            return extentTest;
        }
        this.scenarioName=scenarioName;
        return featureNode.createNode(com.aventstack.extentreports.gherkin.model.Scenario.class,scenarioName);
    }

    public void addScenarioStep(ExtentTest scenarioNode, Step step, Result stepResult){
        String prefix = step.getPrefix();// GIVEN/WHEN/THEN
        String stepTitle = step.getText();
        String status = stepResult.getStatus();
        Throwable error = stepResult.getError();
        ExtentTest stepNode = null;
        switch (prefix){
            case "Given":{
                stepNode = scenarioNode.createNode(Given.class,stepTitle);
                addStatus(stepNode,status,error);
                break;
            }
            case "When":{
                stepNode = scenarioNode.createNode(When.class,stepTitle);
                addStatus(stepNode,status,error);
                break;
            }
            case "Then":{
                stepNode = scenarioNode.createNode(Then.class,stepTitle);
                addStatus(stepNode,status,error);
                break;
            }
            case "And":{
                stepNode = scenarioNode.createNode(And.class,stepTitle);
                addStatus(stepNode,status,error);
                break;
            }
            default:
                stepNode = scenarioNode.createNode(prefix+" "+stepTitle);
                addStatus(stepNode,status,error);
                break;
        }
    }
    private void addStatus(ExtentTest stepNode, String status, Throwable error){
        switch (status){
            case "passed":{
                stepNode.pass("");
                break;
            }
            case "failed":{
                stepNode.fail(error);
                break;
            }
            default:
                stepNode.skip("");
                break;
        }
    }
}
