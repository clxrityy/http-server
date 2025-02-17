package com.mjanglin.httpserver;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mjanglin.httpserver.config.Config;
import com.mjanglin.httpserver.config.ConfigManager;
import com.mjanglin.httpserver.core.ServerListenerThread;
import com.mjanglin.httpserver.core.io.RootNotFoundException;

public class Main {

    private final static Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, RootNotFoundException {
        LOGGER.info("Server starting...");

        ConfigManager.getInstance().loadConfig("src/main/resources/config.json");
        Config config = ConfigManager.getInstance().getCurrentConfig();

        LOGGER.info("Using port: " + config.getPort());
        LOGGER.info("Using root: " + config.getRoot());
        LOGGER.info("Live at: http://localhost:" + config.getPort());

        ServerListenerThread serverListenerThread = new ServerListenerThread(config.getPort(), config.getRoot());
        serverListenerThread.start();
    }
}