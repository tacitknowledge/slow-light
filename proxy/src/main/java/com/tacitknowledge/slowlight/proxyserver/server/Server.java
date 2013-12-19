package com.tacitknowledge.slowlight.proxyserver.server;

/**
 * This interface defines a slowlight server.
 *
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 * */
public interface Server
{
    /**
     * Starts the server.
     */
    void start();

    /**
     * Stops the server.
     */
    void shutdown();
}
