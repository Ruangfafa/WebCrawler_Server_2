package com.Ruangfafa.service;

import com.Ruangfafa.common.Constants.LogMessageCons;
import com.Ruangfafa.common.Constants.LogSourceCons;
import com.Ruangfafa.common.Constants.DatabaseServiceJava;
import com.Ruangfafa.common.ConfigLoader;

import java.security.SecureRandom;
import java.sql.*;

import static com.Ruangfafa.service.Log.log;

public class DatabaseService {
    public static Connection getConnection(String url, String user, String password) {
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            log(LogMessageCons.DB_LOGIN_SUCCESS , null, LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
            return conn;
        } catch (SQLException e) {
            log(LogMessageCons.DB_LOGIN_FAIL, e, LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
            return null;
        }
    }

    public static void createClient(Connection conn){
        long id;
        String clientName = "";
        try {
            // Step 1.1: 于Client表中创建，插入空记录，拿到自增 ID
            try (PreparedStatement insertStmt = conn.prepareStatement(
                    DatabaseServiceJava.SQL_SERVER_CLIENT_NEWID , Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.executeUpdate();
                ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    id = generatedKeys.getLong(1);
                } else {
                    throw new SQLException(LogMessageCons.DB_CREATECLIENT_ERROR);
                }
            }

            // Step 1.2: 格式化 client 名称
            clientName = String.format(DatabaseServiceJava.CLIENT_NAME_FORMAT, id);

            // Step 1.3: 更新刚插入的记录
            try (PreparedStatement updateStmt = conn.prepareStatement(DatabaseServiceJava.SQL_SERVER_CLIENT_UPDATE)) {
                updateStmt.setString(1, clientName);
                updateStmt.setLong(2, id);
                updateStmt.executeUpdate();
            }

            // Step 2.1: 创建用户专属数据库（与用户名同名）
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(String.format(DatabaseServiceJava.SQL_USERSCHEMA_CREATE, clientName));
                log(String.format(LogMessageCons.DB_CREATECLIENTSCHEMA_SUCCESS, clientName, clientName), LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
            } catch (SQLException e) {
                log(String.format(LogMessageCons.DB_CREATECLIENTSCHEMA_FAIL, clientName), e, LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
            }

            // Step 2.2: 在用户数据库中创建表 Task 和 State
            for (String tableSQLTemplate : DatabaseServiceJava.DEFAULT_USER_TABLES) {
                String sql = String.format(tableSQLTemplate, clientName);
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(sql);
                    log(String.format(LogMessageCons.DB_CREATECLIENTTABLE_SUCCESS, clientName, sql), LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
                } catch (SQLException e) {
                    log(String.format(LogMessageCons.DB_CREATECLIENTTABLE_FAIL, clientName, sql), e, LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
                }
            }

            // Step 2.3: 初始化 State 表数据（插入 state=0 和 lock=0）
            try (PreparedStatement stmt = conn.prepareStatement(String.format(DatabaseServiceJava.SQL_USERTABLE_INIT, clientName))) {
                stmt.setString(1, DatabaseServiceJava.USERTABLE_STATE_STATE);
                stmt.setInt(2, 0);
                stmt.executeUpdate();

                stmt.setString(1, DatabaseServiceJava.USERTABLE_STATE_LOCK);
                stmt.setInt(2, 0);
                stmt.executeUpdate();

                log(String.format(LogMessageCons.DB_CLIENTTABLEINIT_SUCCESS, clientName),
                        LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
            } catch (SQLException e) {
                log(String.format(LogMessageCons.DB_CLIENTTABLEINIT_FAIL, clientName),
                        e, LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
            }

            // Step 3.1: 创建用户登录账户
            String clientPassword = generateStrongPassword(14);
            try (PreparedStatement createUserStmt = conn.prepareStatement(String.format(DatabaseServiceJava.SQL_USER_CREATE, clientName, clientPassword))) {
                createUserStmt.executeUpdate();
                log( String.format(LogMessageCons.DB_CREATECLIENTLOGIN_SUCCESS, clientName, clientPassword),LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
            }

            // Step 3.2: 配置新用户权限
            for (String permissionTemplate : DatabaseServiceJava.DEFAULT_USER_PERMISSIONS) {
                String grantSQL = String.format(permissionTemplate, clientName);
                try (PreparedStatement grantStmt = conn.prepareStatement(grantSQL)) {
                    grantStmt.executeUpdate();
                    log(String.format(LogMessageCons.DB_NEWCLIENTPERMITION_SUCCESS, clientName, grantSQL), LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
                } catch (SQLException e) {
                    log(String.format(LogMessageCons.DB_NEWCLIENTPERMITION_FAIL, clientName, grantSQL), e, LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
                }
            }


        } catch (SQLException e) {
            log(LogMessageCons.DB_CREATECLIENT_FAIL, e, LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
        }
    }

    private static String generateStrongPassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {password.append(DatabaseServiceJava.PASSWORDCHARS.charAt(random.nextInt(DatabaseServiceJava.PASSWORDCHARS.length())));}
        return password.toString();
    }

    public static void deleteClient(Connection conn, long id) {
        String clientName = null;

        try {
            // Step 0: 查找 clientName
            try (PreparedStatement stmt = conn.prepareStatement(DatabaseServiceJava.SQL_CLIENT_NAME_BY_ID)) {
                stmt.setLong(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    clientName = rs.getString(1);
                } else {
                    log(String.format(LogMessageCons.DB_SELECTUSER_FAIL, id),
                            LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
                    return;
                }
            }

            // Step 1: 删除数据库（schema）
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(String.format(DatabaseServiceJava.SQL_USERSCHEMA_DROP, clientName));
                log(String.format(LogMessageCons.DB_DROPSCHEMA_SUCCESS, clientName),
                        LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
            }

            // Step 2: 删除 MySQL 用户
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(String.format(DatabaseServiceJava.SQL_USERS_DROP, clientName));
                log(String.format(LogMessageCons.DB_DELETEUSER_SUCCESS, clientName),
                        LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
            }

            // Step 3: 删除 Server.Client 记录
            try (PreparedStatement stmt = conn.prepareStatement(DatabaseServiceJava.SQL_SERVER_CLIENT_DELETE)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
                log(String.format(LogMessageCons.DB_REMOVEUSERLIST_SUCCESS, id),
                        LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
            }

        } catch (SQLException e) {
            log(String.format(LogMessageCons.DB_DELETEUSER_FAIL, id), e,
                    LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
        }
    }
}
