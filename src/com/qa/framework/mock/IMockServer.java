package com.qa.framework.mock;

/**
 * Created by kcgw001 on 2016/7/26.
 */
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
