package com.saransh.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Properties;

/**
 * Single source of configuration. Values come from config.properties on the classpath and can be
 * overridden by JVM system properties (-Dkey=value), so CI and local runs share one file.
 */
public final class ConfigManager {

    private static final String FILE = "config.properties";
    private static final Properties PROPS = load();

    private ConfigManager() {
    }

    private static Properties load() {
        Properties p = new Properties();
        try (InputStream in = ConfigManager.class.getClassLoader().getResourceAsStream(FILE)) {
            if (in == null) {
                throw new IllegalStateException(FILE + " not found on classpath");
            }
            p.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read " + FILE, e);
        }
        return p;
    }

    /** System property wins over the file; blank values are treated as "not set". */
    public static String get(String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) {
            return sys.trim();
        }
        String file = PROPS.getProperty(key);
        return file == null ? null : file.trim();
    }

    public static String get(String key, String defaultValue) {
        String v = get(key);
        return v == null || v.isBlank() ? defaultValue : v;
    }

    public static String baseUrl() {
        return get("baseUrl", "https://automationexercise.com");
    }

    public static String apiBaseUrl() {
        return get("apiBaseUrl", "https://automationexercise.com/api");
    }

    public static String browser() {
        return get("browser", "chrome").toLowerCase();
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless", "true"));
    }

    /** Empty means run locally; a URL means use RemoteWebDriver against that Grid. */
    public static String gridUrl() {
        return get("gridUrl", "");
    }

    public static Duration explicitWait() {
        return Duration.ofSeconds(Long.parseLong(get("explicitWaitSeconds", "15")));
    }

    public static Duration pageLoadTimeout() {
        return Duration.ofSeconds(Long.parseLong(get("pageLoadTimeoutSeconds", "60")));
    }
}
