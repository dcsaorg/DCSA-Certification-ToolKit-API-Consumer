package org.dcsa.ctk.consumer.config;

import lombok.Data;
import lombok.extern.java.Log;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Data
@Log
@EnableConfigurationProperties
@Configuration
@ConfigurationProperties( prefix = "spring")
public class AppProperty {
    private static final String API_VERSION =  "2.2.0";
    public static String DATABASE_URL;
    public static String DATABASE_USER_NAME;
    public static String DATABASE_PASSWORD;
    public static String UPLOAD_CONFIG_PATH;

    public static String CALLBACK_PATH;
    private String upload_config_path;
    public static Path uploadPath;
    @Value("${spring.r2dbc.url}")
    public String url;
    @Value("${spring.r2dbc.password}")
    public String password;
    @Value("${spring.r2dbc.username}")
    public String username;
    @Value("${spring.r2dbc.name}")
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

    public static String API_ROOT_URI;
    public static String CALLBACK_URI;
    public static int CALLBACK_PORT;
    public static int CALLBACK_WAIT;
    public static boolean EVENT_SUBSCRIPTION_SIMULATION;
    public static boolean PUB_SUB_FLAG;
    public static long NOTIFICATION_TRIGGER_TIME;


    public void init(){
        AppProperty.API_ROOT_URI = api_root_uri;
        AppProperty.CALLBACK_URI = callback_uri;
        AppProperty.CALLBACK_PORT = callback_port;
        AppProperty.CALLBACK_WAIT = callback_wait;
        AppProperty.UPLOAD_CONFIG_PATH = upload_config_path;
        AppProperty.NOTIFICATION_TRIGGER_TIME = notificationTriggerTime;
        CALLBACK_PATH = "/v"+API_VERSION.split("\\.")[0]+"/notification-endpoints/receive/";

        String evnDbRootUri = System.getenv("DB_HOST_IP");
        if(evnDbRootUri != null){
            AppProperty.DATABASE_URL = url.replace("localhost", evnDbRootUri) ;
        }else{
            AppProperty.DATABASE_URL = url;
        }
        AppProperty.DATABASE_URL = AppProperty.DATABASE_URL.replace("r2dbc", "jdbc");
        AppProperty.DATABASE_URL = AppProperty.DATABASE_URL+"/"+dbName+"?currentSchema="+schema;
        AppProperty.DATABASE_USER_NAME = username;
        AppProperty.DATABASE_PASSWORD = password;
        AppProperty.UPLOAD_CONFIG_PATH = upload_config_path;

        makeUploadPath();
    }

    private static void makeUploadPath(){
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
