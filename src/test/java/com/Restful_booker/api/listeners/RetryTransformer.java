package com.Restful_booker.api.listeners;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Attaches {@link RetryAnalyzer} to every test method without touching the
 * Cucumber runner classes — the open/closed way to add retries framework-wide.
 */
public class RetryTransformer implements IAnnotationTransformer {

    @Override
    @SuppressWarnings("rawtypes") // TestNG declares this method with raw types
    public void transform(ITestAnnotation annotation, Class testClass,
                          Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}
