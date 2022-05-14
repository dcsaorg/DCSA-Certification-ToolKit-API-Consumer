package org.dcsa.ctk.consumer.reporter.report;

import lombok.extern.java.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

@Log
public class PropertyLoader {

    private static PropertyLoader instance;
    private Properties properties;


    private PropertyLoader() {
        String RESOURCE_FILENAME = "application.yml";
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(RESOURCE_FILENAME)) {
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public static synchronized PropertyLoader getInstance() {
        if (instance == null) {
            instance = new PropertyLoader();
        }
        return instance;
    }

    public String getProperty(String key) {
        return getInstance().properties.getProperty(key);
    }
}
