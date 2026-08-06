package com.Restful_booker.api.config;

import org.aeonbits.owner.ConfigCache;

/**
 * Single access point to configuration. {@link ConfigCache} guarantees one
 * thread-safe instance per interface for the whole JVM.
 */
public final class ConfigProvider {

    private ConfigProvider() {
    }

    public static ConfigManager get() {
        return ConfigCache.getOrCreate(ConfigManager.class);
    }
}
