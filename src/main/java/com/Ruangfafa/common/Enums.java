package com.Ruangfafa.common;

public class Enums {
    public static enum TaskTable{
        SELLER,
        TAG,
        RANKING,
        PRODUCT;
        public String getTaskTableStr(){
            return switch (this) {
                case  SELLER -> "Server.task_seller";
                case  TAG -> "Server.task_tag";
                case  RANKING -> "Server.task_ranking";
                case  PRODUCT -> "Server.task_product";
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
                case SELLER,SELLERTAG -> "task_seller";
                case PRODUCTTAG -> "task_tag";
                case PRODUCTRANK -> "task_ranking";
                case PRODUCT,COMMENT -> "task_product";
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

    public static enum TaskProductPageType{
        TM;
        public String getPageTypeStr() {
            return  switch (this) {
                case TM -> "TM";
            };
        }
    }

}
