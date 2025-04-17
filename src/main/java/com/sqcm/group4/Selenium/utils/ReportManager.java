package com.sqcm.group4.Selenium.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ReportManager {
    private static ExtentReports extent;
    private static Map<String, ExtentTest> testMap = new HashMap<>();
    
    public static void initReport() {
        if (extent == null) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String reportPath = "test-output/ExtentReport_" + timestamp + ".html";
            
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setDocumentTitle("Selenium Test Automation Report");
            sparkReporter.config().setReportName("INFO6255 Assignment Report");
            sparkReporter.config().setTheme(Theme.STANDARD);
            
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);

            extent.setSystemInfo("University", "Northeastern University");
            extent.setSystemInfo("Course", "INFO6255");
            extent.setSystemInfo("Browser", "Chrome");
        }
    }
    
    public static ExtentTest createTest(String testName) {
        if (extent == null) {
            initReport();
        }
        
        ExtentTest test = extent.createTest(testName);
        testMap.put(testName, test);
        return test;
    }
    
    public static ExtentTest getTest(String testName) {
        return testMap.get(testName);
    }
    
    public static void logStep(String testName, Status status, String stepDetails) {
        ExtentTest test = getTest(testName);
        if (test != null) {
            test.log(status, stepDetails);
        }
    }
    
    public static void logStep(String testName, Status status, String stepDetails, String screenshotPath) {
        ExtentTest test = getTest(testName);
        if (test != null) {
            try {
                test.log(status, stepDetails);
                test.addScreenCaptureFromPath(screenshotPath);
            } catch (Exception e) {
                test.log(Status.WARNING, "Could not attach screenshot: " + e.getMessage());
            }
        }
    }
 // Place this below existing logStep methods in ReportManager.java
    public static void logFinalResult(String testName, boolean condition, String expected, String actual) {
        Status status = condition ? Status.PASS : Status.FAIL;
        String message = "<b>Expected:</b> " + expected + "<br><b>Actual:</b> " + actual + "<br><b>Status:</b> " + (condition ? "PASS" : "FAIL");
        logStep(testName, status, message);
    }
    
    public static void flushReport() {
        if (extent != null) {
            extent.flush();
        }
    }
}