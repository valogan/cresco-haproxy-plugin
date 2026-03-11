package io.cresco.haproxy;

import com.google.gson.Gson;
import io.cresco.library.messaging.MsgEvent;
import io.cresco.library.plugin.Executor;
import io.cresco.library.plugin.PluginBuilder;
import io.cresco.library.utilities.CLogger;

import java.util.Map;

public class PluginExecutor implements Executor {

    private final PluginBuilder plugin;
    private final CLogger logger;
    private final HAProxyManager haproxyManager;

    public PluginExecutor(PluginBuilder pluginBuilder, HAProxyManager haproxyManager) {
        this.plugin = pluginBuilder;
        this.logger = plugin.getLogger(PluginExecutor.class.getName(), CLogger.Level.Info);
        this.haproxyManager = haproxyManager;
    }

    @Override
    public MsgEvent executeCONFIG(MsgEvent incoming) {
        logger.debug("Processing CONFIG message: Action = " + incoming.getParam("action"));
        if (incoming.getParams().containsKey("action")) {
            String action = incoming.getParam("action");
            try {
                switch (action) {
                    case "build_config":
                        return buildConfig(incoming);
                    case "start_haproxy":
                        return startHAProxy(incoming);
                    case "stop_haproxy":
                        return stopHAProxy(incoming);
                    case "reload_haproxy":
                        return reloadHAProxy(incoming);
                    default:
                        logger.error("Unknown/Unsupported CONFIG action: {}", action);
                        incoming.setParam("status", "99");
                        incoming.setParam("status_desc", "Unknown/Unsupported config action: " + action);
                        break;
                }
            } catch (Exception e) {
                logger.error("Error processing CONFIG action '" + action + "': " + e.getMessage(), e);
                incoming.setParam("status", "500");
                incoming.setParam("status_desc", "Internal error processing action '" + action + "': " + e.getMessage());
            }
        } else {
            logger.error("CONFIG message received without 'action' parameter.");
            incoming.setParam("status", "400");
            incoming.setParam("status_desc", "Missing 'action' parameter in CONFIG message.");
        }
        return incoming;
    }

    private MsgEvent buildConfig(MsgEvent incoming) {
        String configData = incoming.getParam("haproxy_config_data");
        if (configData != null) {
            boolean success = haproxyManager.buildConfig(configData);
            if (success) {
                incoming.setParam("status", "10");
                incoming.setParam("status_desc", "Successfully built HA-Proxy configuration.");
            } else {
                incoming.setParam("status", "9");
                incoming.setParam("status_desc", "Failed to build HA-Proxy configuration. Check logs.");
            }
        } else {
            incoming.setParam("status", "400");
            incoming.setParam("status_desc", "Missing parameter: haproxy_config_data");
        }
        return incoming;
    }
    
    private MsgEvent startHAProxy(MsgEvent incoming) {
        boolean success = haproxyManager.startHAProxy();
        if (success) {
            incoming.setParam("status", "10");
            incoming.setParam("status_desc", "Successfully started HA-Proxy.");
        } else {
            incoming.setParam("status", "9");
            incoming.setParam("status_desc", "Failed to start HA-Proxy. Check logs.");
        }
        return incoming;
    }
    
    private MsgEvent stopHAProxy(MsgEvent incoming) {
        boolean success = haproxyManager.stopHAProxy();
        if (success) {
            incoming.setParam("status", "10");
            incoming.setParam("status_desc", "Successfully stopped HA-Proxy.");
        } else {
            incoming.setParam("status", "9");
            incoming.setParam("status_desc", "Failed to stop HA-Proxy. Check logs.");
        }
        return incoming;
    }
    
    private MsgEvent reloadHAProxy(MsgEvent incoming) {
        boolean success = haproxyManager.reloadHAProxy();
        if (success) {
            incoming.setParam("status", "10");
            incoming.setParam("status_desc", "Successfully reloaded HA-Proxy.");
        } else {
            incoming.setParam("status", "9");
            incoming.setParam("status_desc", "Failed to reload HA-Proxy. Check logs.");
        }
        return incoming;
    }

    @Override
    public MsgEvent executeEXEC(MsgEvent incoming) {
        logger.warn("Received unimplemented EXEC message.");
        return null;
    }

    @Override
    public MsgEvent executeDISCOVER(MsgEvent incoming) {
        return null;
    }
    @Override
    public MsgEvent executeERROR(MsgEvent incoming) {
        return null;
    }
    @Override
    public MsgEvent executeINFO(MsgEvent incoming) {
        return null;
    }
    @Override
    public MsgEvent executeWATCHDOG(MsgEvent incoming) {
        return null;
    }
    @Override
    public MsgEvent executeKPI(MsgEvent incoming) {
        return null;
    }
}
