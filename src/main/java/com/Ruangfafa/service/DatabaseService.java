package com.Ruangfafa.service;

import com.Ruangfafa.common.Constants.LogMessageCons;
import com.Ruangfafa.common.Constants.LogSourceCons;
import com.Ruangfafa.common.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class DatabaseService {
    public static Connection getConnection(String url, String user, String password) {
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            Log.log(LogMessageCons.DB_LOGIN_SUCCESS , null, LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
            return conn;
        } catch (SQLException e) {
            Log.log(LogMessageCons.DB_LOGIN_FAIL, e, LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
            return null;
        }
    }
}
