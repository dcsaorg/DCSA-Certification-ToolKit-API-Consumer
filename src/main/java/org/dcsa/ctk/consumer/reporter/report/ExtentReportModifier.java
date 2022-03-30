package org.dcsa.ctk.consumer.reporter.report;

import lombok.extern.java.Log;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

@Log
public class ExtentReportModifier {
    // replace pass with covered
    private static final String capitalPassedKey = ">Pass</span>";
    private static final String capitalPassedVal = ">Covered</span>";
    private static final String passedKey = "passed</span>";
    private static final String passedVal = "covered</span>";
    private static final String passKey = "<th>Passed</th>";
    private static final String passVal = "<th>Covered</th>";
    private static final String passPercentageKey = "<th>Passed %</th>";
    private static final String passPercentageVal = "<th>Covered %</th>";
    private static final String testPassedKey = "Tests Passed";
    private static final String testPassedVal = "Tests Covered";
   // replace fail with not covered
    private static final String failKey = "Fail<";
    private static final String failVal = "Not Covered<";
    private static final String failedKey = "failed<";
    private static final String failedVal = "not covered<";
    private static final String FailedKey = "Failed<";
    private static final String FailedVal = "Not covered<";
    private static final String testFailedKey = "Test Failed";
    private static final String testFailedVal = "Tests not covered";
    // to comment the pai chart
    private static final String htmlStrStartKey = "<div class=\"row\">";
    private static final String htmlStrStartVal = "<!--";
    private static final String htmlStrEndKey = "<div class=\"row\">";
    private static final String htmlStrEndVal = "--> <div class=\"row\">";

    private static final Map<String, String> htmlTagMap;

    static {
        htmlTagMap = new HashMap<>();
        // all pass as key and covered as value
        htmlTagMap.put(capitalPassedKey, capitalPassedVal);
        htmlTagMap.put(passedKey, passedVal);
        htmlTagMap.put(passKey, passVal);
        htmlTagMap.put(passPercentageKey, passPercentageVal);
        htmlTagMap.put(testPassedKey, testPassedVal);
        // all fail as key and not covered as value
        htmlTagMap.put(failKey, failVal);
        htmlTagMap.put(failedKey, failedVal);
        htmlTagMap.put(FailedKey, FailedVal);
        htmlTagMap.put(testFailedKey, testFailedVal);
        // html tag for pai chart commented
        htmlTagMap.put(htmlStrStartKey, htmlStrStartVal);
        htmlTagMap.put(htmlStrEndKey, htmlStrEndVal);
    }

    public static void modifyFile(String reportPath) {
        Path path = Paths.get(reportPath);
        Charset charset = StandardCharsets.UTF_8;

        final String[] content = {null};
        try {
            content[0] = Files.readString(path, charset);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        // replace key with value
        htmlTagMap.forEach((key, val) -> {
            if(key.equals(htmlStrStartKey)){
                int indexOfHtmlStart = indexOfSubstringAt(content[0], htmlStrStartKey,1);
                content[0] = replaceStringAtIndex(indexOfHtmlStart, htmlStrStartKey.length()+indexOfHtmlStart, content[0], htmlStrStartVal);
                int indexOfHtmlEnd = indexOfSubstringAt(content[0], htmlStrEndKey, 1);
                content[0] = replaceStringAtIndex(indexOfHtmlEnd, htmlStrEndKey.length()+indexOfHtmlEnd, content[0], htmlStrEndVal);
            }else {
                content[0] =  Objects.requireNonNull(content[0]).replaceAll(key, val);
            }
        });

        try {
            Files.write(path, content[0].getBytes(charset));
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
