package com.mjanglin.httpserver.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.mjanglin.httpserver.util.Json;

public class ConfigManager {
    
    private static ConfigManager myConfigManager;
    private static Config myConfig;

    private ConfigManager() {

    }

    public static ConfigManager getInstance() {
        if (myConfigManager == null) {
            myConfigManager = new ConfigManager();
        }
        return myConfigManager;
    }


    /**
     * Load the configuration from the given file path
     * @param filePath
     */
    @SuppressWarnings("resource")
    public void loadConfig(String filePath) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(filePath);
        } catch (FileNotFoundException e) {
            throw new HttpConfigExeception(e);
        }

        StringBuilder sb = new StringBuilder();

        int i;

        try {
            while ((i = fileReader.read()) != -1) {
                sb.append((char) i);
    
            }
        } catch (IOException e) {
            throw new HttpConfigExeception(e);
        }

        JsonNode conf;
        try {
            conf = Json.parse(sb.toString());
        } catch (IOException e) {
            throw new HttpConfigExeception("Error parsing configuration file", e);
        }
        try {
            myConfig = Json.fromJson(conf, Config.class);
        } catch (JsonProcessingException e) {
            throw new HttpConfigExeception("Error parsing configuration file, internal", e);
        }
    }

    /**
     * Returns the current loaded configuration
     */

    public Config getCurrentConfig() {
        if (myConfig == null) {
            throw new HttpConfigExeception("No current configuration loaded");
        }

        return myConfig;
    }
}
