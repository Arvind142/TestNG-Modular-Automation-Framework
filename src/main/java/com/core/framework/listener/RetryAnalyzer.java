package com.core.framework.listener;

import com.core.framework.constant.FrameworkConstants;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    int count = 0;
    int retryCount = FrameworkConstants.retryFailed;
    @Override
    public boolean retry(ITestResult iTestResult) {
        if(iTestResult.getStatus()==ITestResult.FAILURE){
            if(retryCount>count){
                count++;
                iTestResult.setStatus(ITestResult.FAILURE);
                return true;
            }
            else{
                return false;
            }
        }
        return false;
    }
}
