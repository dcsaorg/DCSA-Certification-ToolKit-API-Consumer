package org.dcsa.ctk.consumer.config;

import lombok.extern.java.Log;
import org.dcsa.ctk.consumer.util.FileUtility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;


@Log
public class PropertyLoader {
    private static PropertyLoader instance;
    private Properties properties;
    private PropertyLoader(String configName) {
        if(AppProperty.isAppDataUploaded) {
            try (InputStream inputStream = FileUtility.getInputStream(AppProperty.uploadPath.toAbsolutePath() + File.separator + AppProperty.RESOURCE_FILENAME)) {
                properties = new Properties();
                properties.load(inputStream);
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage());
            }
        } else if(!configName.isBlank()) {
            try (InputStream inputStream = FileUtility.getInputStream( System.getProperty("user.dir")+"/config/"+configName)) {
                properties = new Properties();
                properties.load(inputStream);
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage());
            }
        }else {
            try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(AppProperty.RESOURCE_FILENAME)) {
                properties = new Properties();
                properties.load(inputStream);
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage());
            }
        }
    }
    public static synchronized PropertyLoader getInstance(String configName) {
        if (instance == null) {
            instance = new PropertyLoader(configName);
        }
        return instance;
    }

    public static String getProperty(String key) {
        return getInstance(AppProperty.RESOURCE_FILENAME).properties.getProperty(key);
    }
    public static void setResourceFilename(String resourceFilename){
        AppProperty.RESOURCE_FILENAME = resourceFilename;
    }
}
