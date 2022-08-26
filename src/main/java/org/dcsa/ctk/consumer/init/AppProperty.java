package org.dcsa.ctk.consumer.init;

import lombok.Data;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Data
@Log
@EnableConfigurationProperties
@Configuration
@ConfigurationProperties( prefix = "spring")
public class AppProperty {
    public static Connection connection = null;

    private static final String UPLOAD_CONFIG_PATH_NAME_KEY = "app.upload_config_path";
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

  /*  @Bean
    public WebClient localApiClient() {
        return WebClient.create("http://localhost:9090/v2/");
    }*/
}
