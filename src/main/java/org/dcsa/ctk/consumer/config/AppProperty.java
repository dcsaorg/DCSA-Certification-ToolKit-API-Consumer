package org.dcsa.ctk.consumer.config;

import lombok.Data;
import lombok.extern.java.Log;
import org.dcsa.ctk.consumer.util.SqlUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Data
@Log
@EnableConfigurationProperties
@Configuration
@ConfigurationProperties( prefix = "spring")
public class AppProperty {
    public static String RESOURCE_FILENAME = "application.yml";
    private static String API_VERSION =  "2.2.0";
    public static String DATABASE_URL;
    public static String DATABASE_USER_NAME;
    public static String DATABASE_PASSWORD;
    public static String UPLOAD_CONFIG_PATH;

    public static String EVENT_PATH;
    public static String CALLBACK_PATH;

    public static String DATABASE_NAME;

    public static String DATABASE_SCHEMA;

    private static final String DATABASE_URL_KEY = "url";
    private static final String DATABASE_SCHEMA_KEY = "schema";
    private static final String DATABASE_NAME_KEY = "dbname";
    private static final String DATABASE_USERNAME_KEY = "username";
    private static final String DATABASE_PASSWORD_KEY = "password";
    public String EVENTS_PATH_KEY="events_path";

    public String EVENT_PATH_KEY="event_path";
    private String upload_config_path;

    private String event_path;
    public static Path uploadPath;
    @Value("${spring.r2dbc.url}")
    public String url;
    @Value("${spring.r2dbc.password}")
    public String password;
    @Value("${spring.r2dbc.username}")
    public String username;
    @Value("${spring.r2dbc.dbname}")
    public String dbName;
    @Value("${spring.r2dbc.properties.schema}")
    public String schema;
    private String api_root_uri;
    private String callback_uri;
    private int callback_port;
    private int callback_wait;
    private String test_suite_name;
    private String event_subscription_simulation;
    private long notificationTriggerTime;
    public static boolean isAppDataUploaded = false;
    public static String API_ROOT_URI;
    public static String CALLBACK_URI;
    public static int CALLBACK_PORT;
    public static int CALLBACK_WAIT;
    public static long NOTIFICATION_TRIGGER_TIME;
    public static String EVENTS_PATH;
    private String events_path;
    public void init(){
        isAppDataUploaded = true;
        makeUploadPath();
        AppProperty.API_ROOT_URI = api_root_uri;
        AppProperty.CALLBACK_URI = callback_uri;
        AppProperty.CALLBACK_PORT = callback_port;
        AppProperty.CALLBACK_WAIT = callback_wait;
        AppProperty.UPLOAD_CONFIG_PATH = upload_config_path;
        AppProperty.EVENTS_PATH = event_path;
        AppProperty.NOTIFICATION_TRIGGER_TIME = notificationTriggerTime;
        AppProperty.DATABASE_URL = url;
        AppProperty.DATABASE_NAME = dbName;
        AppProperty.DATABASE_USER_NAME = username;
        AppProperty.DATABASE_PASSWORD = password;
        AppProperty.DATABASE_SCHEMA = schema;
        AppProperty.CALLBACK_PATH = "/v"+ AppProperty.API_VERSION.split("\\.")[0]+"/notification-endpoints/receive/";
        String evnEventsPath = System.getenv("EVENTS_PATH");
        if(evnEventsPath != null){
            AppProperty.EVENTS_PATH = evnEventsPath;
        } else if(!PropertyLoader.getProperty(EVENTS_PATH_KEY).isBlank()){
            AppProperty.EVENTS_PATH = PropertyLoader.getProperty(EVENTS_PATH_KEY);
        }else{
            AppProperty.EVENTS_PATH = events_path;
        }
        String evnEventPath = System.getenv("EVENT_PATH");
        if(evnEventPath != null){
            AppProperty.EVENT_PATH = evnEventPath;
        } else if(!PropertyLoader.getProperty(EVENT_PATH_KEY).isBlank()){
            AppProperty.EVENT_PATH = PropertyLoader.getProperty(EVENT_PATH_KEY);
        }else{
            AppProperty.EVENT_PATH = event_path;
        }
        initDatabaseProperties();
    }

    private static void initDatabaseProperties(){
        String evnDbUrl = System.getenv("DB_URL");
        String evnDbPassword = System.getenv("DB_PASSWORD");
        String evnDbUsername = System.getenv("DB_USERNAME");
        AppProperty.DATABASE_USER_NAME = PropertyLoader.getProperty(DATABASE_USERNAME_KEY);
        AppProperty.DATABASE_PASSWORD = PropertyLoader.getProperty(DATABASE_PASSWORD_KEY);
        AppProperty.DATABASE_NAME = PropertyLoader.getProperty(DATABASE_NAME_KEY);
        AppProperty.DATABASE_SCHEMA = PropertyLoader.getProperty(DATABASE_SCHEMA_KEY);

        if(evnDbUrl != null){
            System.out.println("evnDbUrl: "+evnDbUrl);
            AppProperty.DATABASE_URL = evnDbUrl;
            System.out.println("Replaced evnDbUrl in the localhost with "+evnDbUrl);
        }else{
            System.out.println("evnDbUrl was null");
            AppProperty.DATABASE_URL = PropertyLoader.getProperty(DATABASE_URL_KEY);
        }
        if(evnDbPassword != null){
            System.out.println("evnDbPassword: "+evnDbPassword);
            AppProperty.DATABASE_PASSWORD = evnDbPassword;
        }else{
            System.out.println("evnDbPassword was null");
            AppProperty.DATABASE_PASSWORD = PropertyLoader.getProperty(DATABASE_PASSWORD_KEY);
        }
        if(evnDbUsername != null){
            System.out.println("evnDbUsername: "+evnDbUsername);
            AppProperty.DATABASE_USER_NAME = evnDbUsername;
        }else{
            System.out.println("evnDbUsername was null");
            AppProperty.DATABASE_USER_NAME = PropertyLoader.getProperty(DATABASE_USERNAME_KEY);
        }
        AppProperty.DATABASE_URL = AppProperty.DATABASE_URL.replace("r2dbc", "jdbc");
        SqlUtility.getConnection();
    }

    private void makeUploadPath(){
        AppProperty.UPLOAD_CONFIG_PATH = upload_config_path;
        uploadPath = Paths.get(AppProperty.UPLOAD_CONFIG_PATH);
        try {
            Files.createDirectories(uploadPath);
        }
        catch (IOException e) {
            throw new RuntimeException("Could not initialize storage ", e);
        }
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement("");
    }

}
