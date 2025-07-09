package com.Ruangfafa.common;

public class Enums {
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

}
