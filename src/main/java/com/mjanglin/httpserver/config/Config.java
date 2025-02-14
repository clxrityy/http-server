package com.mjanglin.httpserver.config;

public class Config {
    private int port;
    private String root;

    public int getPort() {
        return port;
    }

    public String getRoot() {
        return root;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setRoot(String webroot) {
        this.root = webroot;
    }
}
