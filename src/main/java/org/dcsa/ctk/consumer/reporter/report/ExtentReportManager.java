package org.dcsa.ctk.consumer.reporter.report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import lombok.Data;
import org.dcsa.ctk.consumer.constant.CheckListStatus;
import org.dcsa.ctk.consumer.constant.TestRequirement;
import org.dcsa.ctk.consumer.model.CheckListItem;
import org.dcsa.ctk.consumer.util.JsonUtility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.Properties;

@Data
public class ExtentReportManager {

    private static ExtentReports extentReports;
    private static ExtentTest extentTest;
    private static String reportPath;
    private static String reportName;
    private static final String COMPANY = "Company";
    private static final String AUTHOR = "Author";
    private static final String OS_NAME = "os.name";
    private static final String USER_COUNTRY = "user.country";
    private static final String USER_LANGUAGE = "user.language";
    private static final String JAVA_RUNTIME = "java.runtime.name";
    private static final String JAVA_VERSION = "java.version";


    public static synchronized  ExtentReports getExtentReports() {
        if(extentReports == null){
            extentReports = new ExtentReports();
            extentReports.attachReporter(getExtentSparkReporter());
            setReportSystemInfo();
        }
        return extentReports;
    }

    private static void setReportSystemInfo(){
        Properties properties = System.getProperties();
        extentReports.setSystemInfo(COMPANY.toUpperCase(), PropertyLoader.getInstance().getProperty("report.company"));
        extentReports.setSystemInfo(AUTHOR.toUpperCase(), PropertyLoader.getInstance().getProperty("report.author"));
        extentReports.setSystemInfo(OS_NAME.toUpperCase().replaceAll("\\."," "),
                                                (String)properties.get(OS_NAME));
        extentReports.setSystemInfo(USER_COUNTRY.toUpperCase().replaceAll("\\."," "),
                                                (String)properties.get(USER_COUNTRY));
        extentReports.setSystemInfo(USER_LANGUAGE.toUpperCase().replaceAll("\\."," "),
                                                ((String)properties.get(USER_LANGUAGE)).toUpperCase());
        extentReports.setSystemInfo(JAVA_RUNTIME.toUpperCase().replaceAll("\\."," "),
                                                (String)properties.get(JAVA_RUNTIME));
        extentReports.setSystemInfo(JAVA_VERSION.toUpperCase().replaceAll("\\."," "),
                                                (String)properties.get(JAVA_VERSION));
    }

    public static void flush(){
        getExtentReports().flush();
    }

    public static void cleanup(){
        extentReports = null;
        extentTest = null;
    }

    public static ExtentSparkReporter getExtentSparkReporter(){
        DateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy__hh-mm-ss");
        reportName = PropertyLoader.getInstance().getProperty("report.file.name") +
                    "_" +dateFormat.format(Calendar.getInstance().getTime()) + ".html";
        reportPath = System.getProperty("user.dir")+"/"+
                    PropertyLoader.getInstance().getProperty("report.file.location")+"/" + reportName;

        ExtentSparkReporter reporter = new ExtentSparkReporter( reportPath);

        reporter.config().setReportName(PropertyLoader.getInstance().getProperty("report.name"));
        reporter.config().setDocumentTitle(PropertyLoader.getInstance().getProperty("report.title"));
        reporter.config().setTheme(
                Objects.equals(PropertyLoader.getInstance().getProperty("report.theme"), Theme.DARK.getName()) ?
                        Theme.DARK : Theme.STANDARD);
        reporter.config().setTimelineEnabled(Boolean.parseBoolean(
                PropertyLoader.getInstance().getProperty("report.timeline.enabled")));
        reporter.config().setTimeStampFormat(PropertyLoader.getInstance().getProperty("report.time.stamp.format"));
        return reporter;
    }

    public static String getReportPath(){
      return reportPath;
    }

    public static String getReportName(){
        return reportName;
    }


    public static ExtentTest getExtentTest(String name){
        if(name != null){
            extentTest = ExtentReportManager.getExtentReports().createTest(name);
        }
        return extentTest;
    }
    public static int counter = 0;
    public static void writeExtentTestReport(CheckListItem checkListItem){
        if (checkListItem != null){
            ExtentTest extentTest = ExtentReportManager.getExtentTest(checkListItem.getResponseDecoratorWrapper().getRequirementID()+ " " +
                                    checkListItem.getResponseDecoratorWrapper().getDescription());
            extentTest.assignCategory(checkListItem.getResponseDecoratorWrapper().getRequirementID());
            if(checkListItem.getStatus().equals(CheckListStatus.COVERED)){
                extentTest.pass(checkListItem.getResponseDecoratorWrapper().getDescription()+ " ");
                extentTest.info(TestRequirement.getRequirement(checkListItem.getResponseDecoratorWrapper().getRequirementID()));
                Markup markUp = MarkupHelper.createLabel(checkListItem.getStatus().getName(), ExtentColor.GREEN);
                extentTest.log(Status.INFO, markUp);
            }else{
                extentTest.fail(checkListItem.getResponseDecoratorWrapper().getDescription()+ " ");
                extentTest.log(Status.WARNING, TestRequirement.getRequirement(checkListItem.getResponseDecoratorWrapper().getRequirementID()));
                Markup markUp = MarkupHelper.createLabel(checkListItem.getStatus().getName(), ExtentColor.RED);
                extentTest.log(Status.WARNING, markUp);
            }
            // Truncate large log
            String testCaseDetails = JsonUtility.getStringFormat(checkListItem.getLog());
            if (testCaseDetails.length() > 32766){
                testCaseDetails = testCaseDetails.substring(0, 32766);
            }
            // Only when test case covered and testCaseDetails is not blank
            if(!testCaseDetails.isBlank()){
                extentTest.info("DETAILS REQUEST LOG:  " + testCaseDetails);
            }
            ExtentReportManager.flush();
        }
    }
}
