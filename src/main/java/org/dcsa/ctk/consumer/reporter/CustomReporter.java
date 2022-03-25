package org.dcsa.ctk.consumer.reporter;

public interface CustomReporter {
     String generateExcelTestReport(String outputDirectory);
     String generateHtmlTestReport();
}
