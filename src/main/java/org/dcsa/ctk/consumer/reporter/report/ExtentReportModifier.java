package org.dcsa.ctk.consumer.reporter.report;

import lombok.extern.java.Log;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Level;

@Log
public class ExtentReportModifier {
    // replace pass with covered
    private static final String Passed = "Passed<";
    private static final String Replace_Passed = "Covered<";
    private static final String passed = "passed<";
    private static final String Replace_passed = "covered<";
    private static final String Pass = "Pass<";
    private static final String Replace_Pass = "Covered<";
    private static final String Pass_Percentage = "Passed %";
    private static final String Replace_Pass_Percentage = "Covered %";
    private static final String test_passed = "tests passed";
    private static final String Replace_test_passed = "tests covered";
   // replace fail with not covered
    private static final String Fail = "Fail<";
    private static final String Replace_Fail = "Not Covered<";
    private static final String failed = "failed<";
    private static final String Replace_failed = "not covered<";
    private static final String Failed = "Failed<";
    private static final String Replace_Failed = "Not covered<";
    private static final String test_failed = "Test Failed";
    private static final String Replace_test_failed = "Tests not covered";
    // to comment the pai chart
    private static final String htmlStrStart = "<div class=\"row\">";
    private static final String htmlStartReplaceStr = "<!--";
    private static final String htmlStrEnd = "<div class=\"row\">";
    private static final String htmlEndReplaceStr = "--> <div class=\"row\">";

    public static void modifyFile(String reportPath) {
        Path path = Paths.get(reportPath);
        Charset charset = StandardCharsets.UTF_8;

        String content = null;
        try {
            content = Files.readString(path, charset);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        // replace all pass with covered
        content = Objects.requireNonNull(content).replaceAll(Passed, Replace_Passed);
        content = content.replaceAll(passed, Replace_passed);
        content = content.replaceAll(Pass, Replace_Pass);
        // replace all fail with no covered
        content = content.replaceAll(Fail, Replace_Fail);
        content = content.replaceAll(failed, Replace_failed);
        content = content.replaceAll(Failed, Replace_Failed);
        content = content.replaceAll(test_failed, Replace_test_failed);
        // comment the pai chart
        content = content.replaceAll(Pass_Percentage, Replace_Pass_Percentage);
        content = content.replaceAll(test_passed, Replace_test_passed);
        int indexOfHtmlStart = indexOfSubstringAt(content, htmlStrStart,1);
        content = replaceStringAtIndex(indexOfHtmlStart, htmlStrStart.length()+indexOfHtmlStart, content, htmlStartReplaceStr);
        int indexOfHtmlEnd = indexOfSubstringAt(content, htmlStrEnd, 1);
        content = replaceStringAtIndex(indexOfHtmlEnd, htmlStrEnd.length()+indexOfHtmlEnd, content, htmlEndReplaceStr);

        try {
            Files.write(path, content.getBytes(charset));
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public static int indexOfSubstringAt(String inputStr, String subStr, int n) {
        int pos = -1;
        do {
            pos = inputStr.indexOf(subStr, pos + 1);
        } while (n-- > 0 && pos != -1);
        return pos;
    }

    public static String replaceStringAtIndex(int start, int end, String inputStr,  String replaceStr){
        StringBuilder sb = new StringBuilder(inputStr);
        sb.replace(start, end, replaceStr);
        return  sb.toString();
    }
}
