package org.dcsa.ctk.consumer.reporter;

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
    // replace pass with conformant
    private static final String capitalPassedKey = ">Pass</span>";
    private static final String capitalPassedVal = ">Conformant</span>";
    private static final String passedKey = "passed</span>";
    private static final String passedVal = "conformant</span>";
    private static final String passKey = "<th>Passed</th>";
    private static final String passVal = "<th>Conformant</th>";
    private static final String passPercentageKey = "<th>Passed %</th>";
    private static final String passPercentageVal = "<th>Conformant %</th>";
    private static final String testPassedKey = "Tests Passed";
    private static final String testPassedVal = "Tests Conformant";
   // replace fail with not conformant
    private static final String failKey = "Fail<";
    private static final String failVal = "not conformant<";
    private static final String failedKey = "failed<";
    private static final String failedVal = "not conformant<";
    private static final String FailedKey = "Failed<";
    private static final String FailedVal = "not conformant<";
    private static final String testFailedKey = "Test Failed";
    private static final String testFailedVal = "Tests Not Conformant";
    // to comment the pai chart
    private static final String htmlStrStartKey = "<div class=\"row\">";
    private static final String htmlStrStartVal = "<!--";
    private static final String htmlStrEndKey = "<div class=\"row\">";
    private static final String htmlStrEndVal = "--> <div class=\"row\">";
    // replace default log
    private static final String htmlDefaultLogoStartKey = "<div class=\"nav-logo\">";
    private static final String htmlDefaultLogoStartVal = "<!-- 2";
    private  static final String htmlDefaultLogoEndKey = "</a>\n" + "</div>";
    private static final String htmlDefaultLogoEndVal = " -->";
    // Add DCSA logo
    private static final String htmlLeftNavKey = "<ul class=\"nav-left\">";
    private static final String htmlLeftNavVal = "<ul class=\"nav-left\">\n" +
            "<li class=\"m-r-10\">\n" +
            "  <img src=\"https://dcsa.org/wp-content/uploads/2021/05/logo-files.jpg\" alt=\"dcsa-logo\" width=\"48\" height=\"48\" border=\"0\">\n" +
            "</li>";

    private static final Map<String, String> htmlTagMap;
    private static String htmlContent;

    static {
        htmlTagMap = new HashMap<>();
        // all pass as key and conformant as value
        htmlTagMap.put(capitalPassedKey, capitalPassedVal);
        htmlTagMap.put(passedKey, passedVal);
        htmlTagMap.put(passKey, passVal);
        htmlTagMap.put(passPercentageKey, passPercentageVal);
        htmlTagMap.put(testPassedKey, testPassedVal);
        // all fail as key and not conformant as value
        htmlTagMap.put(failKey, failVal);
        htmlTagMap.put(failedKey, failedVal);
        htmlTagMap.put(FailedKey, FailedVal);
        htmlTagMap.put(testFailedKey, testFailedVal);
        // html tag for pai chart commented
        htmlTagMap.put(htmlStrStartKey, htmlStrStartVal);
        // add dcsa logo
        htmlTagMap.put(htmlLeftNavKey, htmlLeftNavVal);
        // remove default logo
        htmlTagMap.put(htmlDefaultLogoStartKey, htmlDefaultLogoStartVal);
        htmlTagMap.put(htmlDefaultLogoEndKey, htmlDefaultLogoEndVal);
    }

    public static void modifyFile(String reportPath) {
        Path path = Paths.get(reportPath);
        Charset charset = StandardCharsets.UTF_8;

        try {
            htmlContent = Files.readString(path, charset);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        // replace key with value
        htmlTagMap.forEach((key, val) -> {
            if(key.equals(htmlStrStartKey)){
                htmlContent = replaceSubStringAtIndex(1, htmlContent,key, val);
                // comment the pai chart
                htmlContent = replaceSubStringAtIndex(1, htmlContent,key, htmlStrEndVal);
            }else {
                htmlContent=  Objects.requireNonNull(htmlContent).replaceAll(key, val);
            }
        });

        try {
            Files.write(path, htmlContent.getBytes(charset));
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public static String replaceSubStringAtIndex(int index, String inputStr, String subStr, String replaceStr){
        int posAt =  indexOfSubstringAt(inputStr, subStr, index);
        StringBuilder sb = new StringBuilder(inputStr);
        sb.replace(posAt, subStr.length()+posAt, replaceStr);
        return  sb.toString();
    }

    public static int indexOfSubstringAt(String inputStr, String subStr, int n) {
        int pos = -1;
        do {
            pos = inputStr.indexOf(subStr, pos + 1);
        } while (n-- > 0 && pos != -1);
        return pos;
    }
}
