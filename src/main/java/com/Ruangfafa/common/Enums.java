package com.Ruangfafa.common;

public class Enums {
    public static enum TaskTable{
        SELLER,
        TAG,
        RANKING,
        PRODUCT;
        public String getTaskTableStr(){
            return switch (this) {
                case  SELLER -> "Server.TaskSeller";
                case  TAG -> "Server.TaskTag";
                case  RANKING -> "Server.TaskRanking";
                case  PRODUCT -> "Server.TaskProduct";
            };
        }
    }

    public static enum TaskType{
        SELLER,
        SELLERTAG,
        PRODUCTTAG,
        PRODUCTRANK,
        PRODUCT,
        COMMENT;
        public String getTaskTable() {
            return switch (this) {
                case SELLER,SELLERTAG -> "TaskSeller";
                case PRODUCTTAG -> "TaskTag";
                case PRODUCTRANK -> "TaskRanking";
                case PRODUCT,COMMENT -> "TaskProduct";
            };
        }
        public int getTaskInt() {
            return switch (this) {
                case SELLER -> 1;
                case SELLERTAG -> 2;
                case PRODUCTTAG -> 3;
                case PRODUCTRANK -> 4;
                case PRODUCT -> 5;
                case COMMENT -> 6;
            };
        }
    }

    public static enum TaskTagPageType{
        TM;
        public String getPageTypeStr() {
            return  switch (this) {
                case TM -> "TM";
            };
        }
    }

}
