package com.Ruangfafa.common;

public class Constants {
    public static class LogMessageCons{
        public static final String DB_LOGIN_SUCCESS = "✔️数据库连接成功";
        public static final String DB_LOGIN_FAIL = "❌数据库连接失败";
    }

    public static class LogSourceCons{
        public static final String DATABASE_SERVICE = "com/Ruangfafa/service/DatabaseService";
    }

    public static class LogJava{
        public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
        public static final String LOG_FORMAT = "[%s] [%s] %s";
        public static final String LOG_FORMAT_WITH_ERROR = "[%s] [%s] %s | ERROR: %s";
    }
}
