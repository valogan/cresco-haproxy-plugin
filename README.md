# Cresco HA-Proxy Plugin

This repository contains an OSGi-based Cresco plugin for managing an HA-Proxy instance. The plugin runs on a machine hosting a Cresco agent and allows remote configuration and life-cycle management of an `haproxy` process through the Cresco messaging plane.

## How It Works

The plugin consists of the following core components:

1. **`Activator` and `Plugin`**: These manage the OSGi lifecycle and initialization within the Cresco framework. They handle starting/stopping the plugin and instantiating the core logic.
2. **`HAProxyManager`**: This component interacts with the local file system and OS to manage the HA-Proxy application. It dynamically writes the provided HA-Proxy configuration to disk (`haproxy.cfg`) and uses `ProcessBuilder` (shell execution) to start, stop, or reload the background HA-Proxy shell process. 
3. **`PluginExecutor`**: This interface listens for incoming remote Procedure Call (RPC) messages (specifically `CONFIG` messages) from the agent or other plugins in the Cresco network, translating them into actions performed by the `HAProxyManager`.

## Accepted Messages

The plugin listens for `MsgEvent.Type.CONFIG` messages. To command the plugin, you must send a `CONFIG` level message containing a specific `action` parameter. 

### 1. Build HA-Proxy Configuration
Builds and saves a new `haproxy.cfg` file on the remote machine.
* **`action`**: `build_config`
* **`haproxy_config_data`**: The full raw string content of your desired HA-Proxy configuration file.

### 2. Start HA-Proxy
Starts the `haproxy` process using the compiled `haproxy.cfg`. 
* **`action`**: `start_haproxy`
* *Note: Does nothing if the process is already running.*

### 3. Stop HA-Proxy
Stops the currently running `haproxy` process managed by the plugin.
* **`action`**: `stop_haproxy`

### 4. Reload HA-Proxy
Gracefully reloads the running `haproxy` process. It stops the current process and starts a new one so that it can pick up any changes if `build_config` was recently called.
* **`action`**: `reload_haproxy`


## Building and Deploying

To build the plugin into an OSGi jar, run:
```bash
mvn clean install
```
This will produce a jar file inside the `target/` directory (e.g., `haproxy-1.2-SNAPSHOT.jar`). This jar can be directly deployed into a Cresco framework agent environment.
