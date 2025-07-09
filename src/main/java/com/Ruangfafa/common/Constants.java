package com.Ruangfafa.common;

public class Constants {
    public static class LogMessageCons{
        public static final String
                DB_LOGIN_SUCCESS = "✔️数据库连接成功",
                DB_LOGIN_FAIL = "❌数据库连接失败",

                DB_CREATECLIENT_ERROR = "获取新用户ID时失败",

                DB_CREATECLIENTLOGIN_SUCCESS = "✔️创建用户成功！\n新用户登录账户: \n%s\n新用户登录密码: \n%s\n",

                DB_NEWCLIENTPERMITION_SUCCESS = "✔️权限授予成功 [%s]：%s",
                DB_NEWCLIENTPERMITION_FAIL = "❌权限授权失败 [%s]：%s",

                DB_CREATECLIENTSCHEMA_SUCCESS = "✔️创建数据库成功 [%s]：`%s`",
                DB_CREATECLIENTSCHEMA_FAIL = "❌创建数据库失败 [%s]",

                DB_CREATECLIENTTABLE_SUCCESS = "✔️创建表成功 [%s]：%s",
                DB_CREATECLIENTTABLE_FAIL = "❌创建表失败 [%s]：%s",

                DB_CLIENTTABLEINIT_SUCCESS = "✔️初始化 State 表数据成功 for [%s]",
                DB_CLIENTTABLEINIT_FAIL = "❌初始化 State 表数据失败 for [%s]",

                DB_CREATECLIENT_FAIL = "❌创建用户失败",

                DB_SELECTUSER_FAIL = "❌未找到 ID [%d] 对应的 clientName",
                DB_DROPSCHEMA_SUCCESS = "✔️删除数据库成功 `%s`",
                DB_DELETEUSER_SUCCESS = "✔️删除数据库用户成功 `%s`@'%%'",
                DB_REMOVEUSERLIST_SUCCESS = "✔️已从 Server.Client 中删除 ID = %d 的记录",
                DB_DELETEUSER_FAIL = "❌删除用户 ID [%d] 时发生错误",

                DB_SELECTSTATUS_FAIL = "❌查询 %s.State 出错: %s",

                DB_UPDATECLIENTSTATUS_SUCCESS = "✔️更新 %s.State: %s = %s",
                DB_UPDATECLIENTSTATUS_WARN = "⚠️未找到 %s.State 中 key = '%s'",
                DB_UPDATECLIENTSTATUS_FAIL = "❌未找到 id = %d 的 client",
                DB_UPDATECLIENTSTATUS_FAIL2 = "❌setState 失败：id=%d, key=%s, value=%s",

                DB_SELETCTASKURL_FAIL = "❌读取任务失败：%s",
                DB_SELETCTASKURL_WARN = "⚠️没有待分配的任务",

                DB_INSTERTTASKURL_SUCCESS = "✔️插入任务 [%s] 到 %s.Task",
                DB_INSTERTTASKURL_FAIL = "❌插入任务失败 [%s] 到 %s.Task",

                DB_ASSIGNTASK_SUCCESS = "✔️成功将 %d 条任务平均分配给 %d 个客户端",
                DB_ASSIGNTASK_WARN = "⚠️ 没有可用的空闲客户端";
    }

    public static class LogSourceCons{
        public static final String
                DATABASE_SERVICE = "com/Ruangfafa/service/DatabaseService.java",
                APPLICATION_CONTROLLER = "com/Ruangfafa/controller/ApplicationController.java";
    }

    public static class LogJava{
        public static final String
                TIME_FORMAT = "yyyy-MM-dd HH:mm:ss",
                LOG_FORMAT = "[%s] [%s] %s",
                LOG_FORMAT_WITH_ERROR = "[%s] [%s] %s | ERROR: %s";
    }

    public static class DatabaseServiceJava{
        public static final String
                PASSWORDCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+",

                SQL_SERVER_CLIENT_NEWID = "INSERT INTO Server.Client () VALUES ()",
                SQL_SERVER_CLIENT_UPDATE = "UPDATE Server.Client SET client = ? WHERE id = ?",
                SQL_USER_CREATE = "CREATE USER `%s`@'%%' IDENTIFIED BY '%s'",
                SQL_USERSCHEMA_CREATE = "CREATE DATABASE IF NOT EXISTS `%s`",
                SQL_USERTABLE_INIT = "INSERT IGNORE INTO `%s`.State (`key`, `value`) VALUES (?, ?)",
                SQL_CLIENT_NAME_BY_ID = "SELECT client FROM Server.Client WHERE id = ?",
                SQL_SERVER_CLIENT_DELETE = "DELETE FROM Server.Client WHERE id = ?",
                SQL_USERSCHEMA_DROP = "DROP DATABASE IF EXISTS `%s`",
                SQL_USERS_DROP = "DROP USER IF EXISTS `%s`@'%%'",
                SQL_SERVER_CLIENT_SELECT = "SELECT id, client FROM Server.Client",
                SQL_CLIENTSTATUS_STATUS_SELECT = "SELECT value FROM %s.State WHERE `key` = 'state' LIMIT 1",
                SQL_SERVER_CLIENT_SELECT_BYID = "SELECT client FROM Server.Client WHERE id = ?",
                SQL_CLIENTSTATUS_STATUS_UPDATE = "UPDATE %s.State SET value = ? WHERE `key` = ?",
                SQL_TASKURL_SELECT = "SELECT url FROM %s",
                SQL_TASKURL_INSERT = "INSERT INTO %s.Task (url) VALUES (?)",

                CLIENT_NAME_FORMAT = "Client_%07d",
                USERTABLE_STATE_STATE = "state",
                USERTABLE_STATE_LOCK = "lock",
                SERVER_CLIENT_ID = "id",
                SERVER_CLIENT_CLIENT = "client",
                CLIENT_STATUS_VALUE = "value",
                TASK_URL = "url",
                SERVER_TABLE = "Server.%s";


        public static final String[]
                DEFAULT_USER_PERMISSIONS = {
                    "GRANT SELECT, DELETE ON %1$s.Task TO `%1$s`@'%%'",
                    "GRANT SELECT, DELETE ON %1$s.State TO `%1$s`@'%%'",

                    "GRANT INSERT ON Server.Seller TO `%1$s`@'%%'",
                    "GRANT INSERT ON Server.SellerTag TO `%1$s`@'%%'",
                    "GRANT INSERT ON Server.ProductTag TO `%1$s`@'%%'",
                    "GRANT INSERT ON Server.ProductRank TO `%1$s`@'%%'",
                    "GRANT INSERT ON Server.Product TO `%1$s`@'%%'",
                    "GRANT INSERT ON Server.Comment TO `%1$s`@'%%'"
                },
                DEFAULT_USER_TABLES = {
                    "CREATE TABLE IF NOT EXISTS `%s`.Task (id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, url VARCHAR(2048) NOT NULL)",
                    "CREATE TABLE IF NOT EXISTS `%s`.State (`key` CHAR(8) PRIMARY KEY, `value` TINYINT)"
                };
    }
}
