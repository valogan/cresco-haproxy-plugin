package io.cresco.haproxy;

import io.cresco.library.plugin.PluginBuilder;
import io.cresco.library.utilities.CLogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HAProxyManager {

    private final PluginBuilder plugin;
    private final CLogger logger;
    private final String configPath;
    private Process haproxyProcess;

    public HAProxyManager(PluginBuilder pluginBuilder) {
        this.plugin = pluginBuilder;
        this.logger = plugin.getLogger(this.getClass().getName(), CLogger.Level.Info);
        
        String dataDir = plugin.getPluginDataDirectory();
        File dir = new File(dataDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.configPath = dataDir + File.separator + "haproxy.cfg";
    }

    public boolean buildConfig(String configContent) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configPath))) {
            writer.write(configContent);
            logger.info("Successfully wrote HA-Proxy config to " + configPath);
            return true;
        } catch (IOException e) {
            logger.error("Failed to write HA-Proxy config", e);
            return false;
        }
    }

    public boolean startHAProxy() {
        if (haproxyProcess != null && haproxyProcess.isAlive()) {
            logger.warn("HA-Proxy is already running.");
            return true;
        }
        try {
            logger.info("Starting HA-Proxy using config: " + configPath);
            ProcessBuilder pb = new ProcessBuilder("haproxy", "-f", configPath);
            pb.inheritIO();
            haproxyProcess = pb.start();
            return true;
        } catch (IOException e) {
            logger.error("Failed to start HA-Proxy", e);
            return false;
        }
    }

    public boolean stopHAProxy() {
        if (haproxyProcess != null && haproxyProcess.isAlive()) {
            logger.info("Stopping HA-Proxy...");
            haproxyProcess.destroy();
            return true;
        }
        logger.info("HA-Proxy is not currently running.");
        return true;
    }
    
    public boolean reloadHAProxy() {
         if (haproxyProcess != null && haproxyProcess.isAlive()) {
             logger.info("Reloading HA-Proxy...");
             stopHAProxy();
             return startHAProxy();
         } else {
             logger.warn("HA-Proxy was not running. Starting it instead of reloading.");
             return startHAProxy();
         }
    }

    public void shutdown() {
        stopHAProxy();
    }
}
