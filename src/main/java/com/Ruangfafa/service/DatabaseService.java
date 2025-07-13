package com.Ruangfafa.service;

import com.Ruangfafa.common.Constants.LogMessageCons;
import com.Ruangfafa.common.Constants.LogSourceCons;
import com.Ruangfafa.common.Constants.DatabaseServiceJava;
import com.Ruangfafa.common.ConfigLoader;
import com.Ruangfafa.common.Enums.TaskType;
import com.Ruangfafa.common.Enums.TaskTable;
import com.Ruangfafa.model.TaskTag;

import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

            log( String.format(LogMessageCons.DB_CREATECLIENTLOGIN_SUCCESS, clientName, clientPassword),LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
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

    public static List<Long> getFreeClient(Connection conn) {
        List<Long> freeClients = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(DatabaseServiceJava.SQL_SERVER_CLIENT_SELECT);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                long clientId = rs.getLong(DatabaseServiceJava.SERVER_CLIENT_ID);             // Server.Client.id
                String clientName = rs.getString(DatabaseServiceJava.SERVER_CLIENT_CLIENT);   // e.g., Client_0000001

                // 构造 State 表查询语句
                try (PreparedStatement stateStmt = conn.prepareStatement(String.format(DatabaseServiceJava.SQL_CLIENTSTATUS_STATUS_SELECT, clientName));
                     ResultSet stateRs = stateStmt.executeQuery()) {
                    if (stateRs.next()) {
                        if (stateRs.getInt(DatabaseServiceJava.CLIENT_STATUS_VALUE) == 0) {
                            freeClients.add(clientId); //使用 Server.Client 表中的真实 ID
                        }
                    }
                } catch (SQLException e) {
                    log(String.format(LogMessageCons.DB_SELECTSTATUS_FAIL, clientName, e.getMessage()), e, LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return freeClients;
    }

    public static List<String> getTask(Connection conn, TaskType taskType) {
        String taskTableName = String.format(DatabaseServiceJava.SERVER_TABLE, taskType.getTaskTable());  // 例如 Server.TaskProduct
        String fetchTaskSQL = String.format(DatabaseServiceJava.SQL_TASKURL_SELECT, taskTableName);

        List<String> urls = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(fetchTaskSQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                urls.add(rs.getString(DatabaseServiceJava.TASK_URL));
            }
        } catch (Exception e) {
            log(String.format(LogMessageCons.DB_SELETCTASKURL_FAIL, taskTableName), e, LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
            return null;
        }

        if (urls.isEmpty()) {
            log(LogMessageCons.DB_SELETCTASKURL_WARN, LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
            return new ArrayList<>();
        }
        return urls;
    }

    public static void setState(Connection conn, long id, String key, int value) {
        try (PreparedStatement stmt = conn.prepareStatement(DatabaseServiceJava.SQL_SERVER_CLIENT_SELECT_BYID)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String clientName = rs.getString(DatabaseServiceJava.SERVER_CLIENT_CLIENT);
                try (PreparedStatement updateStmt = conn.prepareStatement(String.format(DatabaseServiceJava.SQL_CLIENTSTATUS_STATUS_UPDATE,clientName))) {
                    updateStmt.setInt(1, value);
                    updateStmt.setString(2, key);
                    int affected = updateStmt.executeUpdate();
                    if (affected > 0) {
                        log(String.format(LogMessageCons.DB_UPDATECLIENTSTATUS_SUCCESS, clientName, key, value), LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
                    } else {
                        log(String.format(LogMessageCons.DB_UPDATECLIENTSTATUS_WARN, clientName, key), LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
                    }
                }
            } else {
                log(String.format(LogMessageCons.DB_UPDATECLIENTSTATUS_FAIL, id), LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
            }
        } catch (Exception e) {
            log(String.format(LogMessageCons.DB_UPDATECLIENTSTATUS_FAIL2, id, key, value), e, LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
        }
    }

    public static String getClientNameById(Connection conn, long id){
        try (PreparedStatement stmt = conn.prepareStatement(DatabaseServiceJava.SQL_SERVER_CLIENT_SELECT_BYID)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString(DatabaseServiceJava.SERVER_CLIENT_CLIENT);
            }
        } catch (SQLException e) {
            Log.log(String.format(LogMessageCons.DB_SELECTUSER_FAIL, id), e, LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
        }
        return null;
    }

    public static void loadTask(Connection conn, TaskTable taskTable, String url){
        String tableName = taskTable.getTaskTableStr();
        String sql = String.format(DatabaseServiceJava.SQL_LOADTASK_INSERT, tableName);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, url);
            stmt.executeUpdate();
            Log.log(LogMessageCons.DB_LOADTASK_SUCCESS, LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
        } catch (SQLException e) {
            Log.log(LogMessageCons.DB_LOADTASK_FAIL, e, LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
        }
    }

    public static void insertTask(Connection conn, String clientName, String url) {
        String insertSQL = String.format(DatabaseServiceJava.SQL_TASKURL_INSERT, clientName);
        try (PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
            stmt.setString(1, url);
            stmt.executeUpdate();
            Log.log(String.format(LogMessageCons.DB_INSTERTTASKURL_SUCCESS, url, clientName), LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
        } catch (SQLException e) {
            Log.log(String.format(LogMessageCons.DB_INSTERTTASKURL_FAIL, url, clientName), e, LogSourceCons.DATABASE_SERVICE, ConfigLoader.LOG_PRINT);
        }
    }

    public static List<TaskTag> getSellerTag(Connection conn) {
        List<TaskTag> SellerTagList = new ArrayList<>();
        String sql = DatabaseServiceJava.SQL_GETSELLERTAG_SELECT;
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String pageType = rs.getString("pageType");
                String sellerId = rs.getString("sellerId");
                String cpId = rs.getString("cpId");

                TaskTag tag = new TaskTag(pageType, sellerId, cpId);
                SellerTagList.add(tag);
            }
        } catch (SQLException e) {
            e.printStackTrace();  // 生产环境建议记录日志而不是直接打印
        }
        return SellerTagList;
    }

}
