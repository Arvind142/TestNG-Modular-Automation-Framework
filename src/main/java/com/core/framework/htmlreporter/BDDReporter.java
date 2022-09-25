package com.core.framework.htmlreporter;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.gherkin.model.And;
import com.aventstack.extentreports.gherkin.model.Given;
import com.aventstack.extentreports.gherkin.model.Then;
import com.aventstack.extentreports.gherkin.model.When;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.core.framework.listener.Listener;
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
import java.util.*;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


@Slf4j
public class BDDReporter {
    private static BDDReporter reporter=null;

    private ExtentReports extentReport;

    private ExtentTest extentTest;

    private String featureName = "";

    private String scenarioName = "";

    private String reportingFolder;

    private String assetFolder;

    private String summaryFolder;

    private String deviceDetails;




    public boolean stopReporting() {
        this.extentReport.flush();
        log.debug("report flush");
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

    public static synchronized BDDReporter initializeReporting() {
        if(reporter==null) {
            reporter = new BDDReporter();
        }
        return reporter;
    }
    private BDDReporter(){
        log.trace("constructor initialized");
        this.reportingFolder = Listener.reporter.getReportingFolder();
    }

    public void initializeReportingVars(){
        log.debug("BDD HTML Reporter called");
        log.debug("Output folder created @ "+reportingFolder);
        // reporting initialized
        ExtentSparkReporter htmlReporter = new ExtentSparkReporter(reportingFolder + "/cucumber-result.html");
        this.extentReport = new ExtentReports();
        if (new File("src/test/resources/extent-config.xml").exists()) {
            try {
                htmlReporter.loadXMLConfig("src/test/resources/extent-config.xml");
            } catch (Exception e) {
                //do Nothing go with default config
            }
        }
        extentReport.attachReporter(htmlReporter);
        log.debug("html reporting initialized");
    }

    public void generateBDDReport(Results results){
        try{
            List<ScenarioResult> scenarioResults = getScenarioResults(results);
            scenarioResults.forEach(scenarioResult->{
                Feature feature = getFeature(scenarioResult);
                ExtentTest featureNode = createFeatureNode(feature);
                Scenario scenario = scenarioResult.getScenario();
                ExtentTest scenarioNode = createScenarioNode(featureNode,scenario);
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
        extentTest = extentReport.createTest(com.aventstack.extentreports.gherkin.model.Feature.class,feature.getName(),feature.getDescription());
        if(feature.getTags()!=null && feature.getTags().size()!=0){
            extentTest.assignCategory(getTags(feature.getTags()));
        }
        return extentTest;
    }

    public ExtentTest createScenarioNode(ExtentTest featureNode,Scenario scenario){
        if(this.scenarioName.equalsIgnoreCase(scenario.getName())){
            return extentTest;
        }
        this.scenarioName=scenario.getName();
        extentTest = featureNode.createNode(com.aventstack.extentreports.gherkin.model.Scenario.class,scenarioName);
        if(scenario.getTags()!=null && scenario.getTags().size()!=0){
            extentTest.assignCategory(getTags(scenario.getTags()));
        }
        return extentTest;
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
    public String[] getTags(List<Tag> tagList){
        String[] arr = new String[tagList.size()];
        for(int i=0;i<arr.length;i++){
            arr[i]=tagList.get(i).getName();
        }
        return arr;
    }
}
