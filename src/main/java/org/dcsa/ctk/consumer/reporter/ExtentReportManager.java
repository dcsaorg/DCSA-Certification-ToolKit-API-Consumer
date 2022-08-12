package org.dcsa.ctk.consumer.reporter;

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
    private static final String COMPANY_KEY = "company";
    private static final String AUTHOR_KEY = "author";
    private static final String NAME_KEY = "name";
    private static final String LOCATION_KEY = "location";
    private static final String TITLE_KEY = "title";
    private static final String THEME_KEY = "theme";
    private static final String ENABLED_KEY = "enabled";
    private static final String FORMAT_KEY = "format";
    private static final String OS_NAME = "os.name";
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
        extentReports.setSystemInfo(COMPANY_KEY.toUpperCase(), PropertyLoader.getInstance().getProperty(COMPANY_KEY));
        extentReports.setSystemInfo(AUTHOR_KEY.toUpperCase(), PropertyLoader.getInstance().getProperty(AUTHOR_KEY));
        extentReports.setSystemInfo(OS_NAME.toUpperCase().replaceAll("\\."," "),
                                                (String)properties.get(OS_NAME));
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
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy__hh-mm-ss");
        reportName = PropertyLoader.getInstance().getProperty(NAME_KEY) +
                    "_" +dateFormat.format(Calendar.getInstance().getTime()) + ".html";
        reportPath = System.getProperty("user.dir")+"/"+
                    PropertyLoader.getInstance().getProperty(LOCATION_KEY)+"/" + reportName;

        ExtentSparkReporter reporter = new ExtentSparkReporter( reportPath);

        reporter.config().setReportName(PropertyLoader.getInstance().getProperty(NAME_KEY));
        reporter.config().setDocumentTitle(PropertyLoader.getInstance().getProperty(TITLE_KEY));
        reporter.config().setTheme(
                Objects.equals(PropertyLoader.getInstance().getProperty(THEME_KEY), Theme.DARK.getName()) ?
                        Theme.DARK : Theme.STANDARD);
        reporter.config().setTimelineEnabled(Boolean.parseBoolean(
                PropertyLoader.getInstance().getProperty(ENABLED_KEY)));
        reporter.config().setTimeStampFormat(PropertyLoader.getInstance().getProperty(FORMAT_KEY));
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
