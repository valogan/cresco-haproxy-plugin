package io.cresco.haproxy;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
    public void start(BundleContext context) {
        System.out.println("Starting HA-Proxy Bundle.");
    }
    public void stop(BundleContext context) {
        System.out.println("Stopped HA-Proxy Bundle.");
    }
}
