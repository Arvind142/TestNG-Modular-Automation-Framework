package com.core.framework.base;

import com.core.framework.listener.AnnotationTransformer;
import com.core.framework.listener.ITestReporter;
import com.core.framework.listener.Listener;
import com.core.framework.listener.RetryAnalyzer;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Listeners;

@Slf4j
@Listeners({Listener.class, AnnotationTransformer.class, ITestReporter.class})
public class TestBase {
    /*
     * Write your own implementation :)
     */
}
