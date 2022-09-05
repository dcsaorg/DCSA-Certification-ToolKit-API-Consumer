package org.dcsa.ctk.consumer.init;

import lombok.Data;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import javax.servlet.MultipartConfigElement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Data
@Log
@EnableConfigurationProperties
@Configuration
@ConfigurationProperties( prefix = "spring")
public class AppProperty {
    public static Connection connection = null;

    private static final String UPLOAD_CONFIG_PATH_NAME_KEY = "app.upload_config_path";

    public static final String CTK_SUBSCRIPTION_TABLE = "ctk_event_subscription";
    public static String DATABASE_URL;
    public static String DATABASE_USER_NAME;
    public static String DATABASE_PASSWORD;
    public static String UPLOAD_CONFIG_PATH;
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

     public void init(){
        String evnApiRootUri = System.getenv("DB_HOST_IP");
        if(evnApiRootUri != null){
            AppProperty.DATABASE_URL = url.replace("localhost", evnApiRootUri) ;
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

    public static Connection getConnection() {
        try {
            if (connection == null) {
                connection = DriverManager.getConnection(AppProperty.DATABASE_URL, AppProperty.DATABASE_USER_NAME, AppProperty.DATABASE_PASSWORD);
                System.out.println("Connected to the database!");
            } else {
                System.out.println("Connection is initialized: "+connection);
            }
        } catch (SQLException e) {
            System.out.println("Connection init error: "+e.getMessage());
        }
        return connection;
    }
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement("");
    }

}
