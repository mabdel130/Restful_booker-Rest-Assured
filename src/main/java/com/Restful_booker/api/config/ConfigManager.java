package com.Restful_booker.api.config;

import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;

/**
 * Typed configuration contract for the framework.
 * <p>
 * Resolution order (first hit wins):
 * <ol>
 *   <li>JVM system properties (e.g. {@code -Dbase.uri=...}) — lets CI override anything</li>
 *   <li>{@code config/<env>.properties} — environment file selected via {@code -Denv=qa}</li>
 *   <li>{@code config/config.properties} — shared defaults</li>
 * </ol>
 */
@LoadPolicy(LoadType.MERGE)
@Sources({
        "system:properties",
        "classpath:config/${env}.properties",
        "classpath:config/config.properties"
})
public interface ConfigManager extends Config, Accessible {

    @Key("base.uri")
    String baseUri();

    @Key("auth.username")
    String username();

    @Key("auth.password")
    String password();

    @Key("timeout.connection.ms")
    @DefaultValue("30000")
    int connectionTimeoutMs();

    @Key("timeout.socket.ms")
    @DefaultValue("30000")
    int socketTimeoutMs();

    /** Upper bound asserted on every response time. */
    @Key("assert.max.response.time.ms")
    @DefaultValue("30000")
    long maxResponseTimeMs();
}
