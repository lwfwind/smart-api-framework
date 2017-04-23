package com.qa.framework.mock;

public interface IMockServer {
    /**
     * Start server.
     */
    void startServer();

    /**
     * Stop server.
     */
    void stopServer();

    /**
     * Sets rules.
     */
    void settingRules();
}
