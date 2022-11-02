package com.core.test_package;


import com.core.framework.annotation.TestDescription;
import com.core.framework.base.TestNG_Base;
import com.core.framework.listener.Listener;
import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

@Listeners(Listener.class)
public class BDDRunner extends TestNG_Base {

    @Test(groups = {"feature based execution"})
    @TestDescription(author = "Arvind Choudhary", isBDD = "yes")
    @Parameters({"features","tags"})
    public void test(@Optional("All") String feature,@Optional("All") String tags){
        System.out.println(feature+" : "+tags);
        Results results = Runner.path("src/test/resources/Features/").parallel(1);
        bddReporter.initializeReportingVars();
        bddReporter.generateBDDReport(results);
    }
}
