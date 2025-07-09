package com.Ruangfafa.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    public static final String DB_URL;
    public static final String DB_USER;
    public static final String DB_PASSWORD;

    public static final Boolean LOG_PRINT;

    static {
        Properties props = new Properties();
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(".properties")) {
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("❌ 读取配置文件失败: " + e.getMessage());
        }
        DB_URL = props.getProperty("db.url");
        DB_USER = props.getProperty("db.user");
        DB_PASSWORD = props.getProperty("db.password");

        LOG_PRINT = props.getProperty("log.print").equals("true");
    }
}
