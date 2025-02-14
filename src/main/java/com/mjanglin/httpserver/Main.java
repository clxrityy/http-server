package com.mjanglin.httpserver;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mjanglin.httpserver.config.Config;
import com.mjanglin.httpserver.config.ConfigManager;
import com.mjanglin.httpserver.core.ServerListenerThread;

public class Main {

    private final static Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("Server starting...");

        ConfigManager.getInstance().loadConfig("src/main/resources/http.json");
        Config config = ConfigManager.getInstance().getCurrentConfig();

        LOGGER.info("Using port: " + config.getPort());
        LOGGER.info("Using webroot: " + config.getWebroot());

        try {
            ServerListenerThread serverListenerThread = new ServerListenerThread(config.getPort(), config.getWebroot());
            serverListenerThread.start();
        } catch (IOException e) {
            LOGGER.error("Problem with starting server", e);
        }
    }
}