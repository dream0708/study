package com.sohu.bp.elite.data.statistic.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    private static final Logger log = LoggerFactory.getLogger(Configuration.class);

    private Properties properties = new Properties();

    private String configFile = "audit.properties";

    public Configuration(){

    }

    public Configuration(String filename) {
        this.configFile = filename;
        reset(this.configFile);
    }

    public void init() {
        reset(this.configFile);
    }

    protected void reset(String filename) {
        InputStream in = null;
        try {
            in = Configuration.class.getResourceAsStream("/"+filename);
            File f = new File(filename);
            log.debug(f.getAbsolutePath());
            properties.load(in);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            try {
                in.close();
            } catch (IOException ioe) {
                log.error("could not close stream on config file ", ioe);
            }
        }
    }

    public String get(String property) {
        return properties.getProperty(property);
    }

    public String get(String property, String defaultValue) {
        String result = properties.getProperty(property);
        if(result == null){
            result = defaultValue;
        }
        return result;
    }

    public int getInt(String property) {
        try {
            String result = get(property);
            if(result != null)
                return Integer.valueOf(result).intValue();
            else
                return 0;
        } catch (Exception e) {
            log.error("", e);
            return 0;
        }
    }

    public int getInt(String property, int defaultValue) {
        try {
            String result = get(property);
            if(result != null)
                return Integer.valueOf(result).intValue();
            else
                return defaultValue;
        } catch (Exception e) {
            log.error("", e);
            return defaultValue;
        }
    }


    public boolean getBoolean(String property) {
        try {
            return Boolean.valueOf(get(property)).booleanValue();
        } catch (Exception e) {
            log.error("", e);
            return false;
        }
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }
}
