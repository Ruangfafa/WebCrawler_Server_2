package com.Ruangfafa;

import com.Ruangfafa.common.ConfigLoader;
import com.Ruangfafa.common.Enums.TaskType;
import com.Ruangfafa.controller.ApplicationController;
import com.Ruangfafa.service.DatabaseService;

import java.sql.Connection;


public class TM_VerticleTest {
    public static void main(String[] args) {
        Connection conn = DatabaseService.getConnection(ConfigLoader.DB_URL,ConfigLoader.DB_USER,ConfigLoader.DB_PASSWORD);
        //ApplicationController.assignTask(conn, TaskType.SELLER);
        //ApplicationController.assignTask(conn, TaskType.SELLERTAG);
        ApplicationController.loadTaskTag(conn);
        //ApplicationController.assignTask(conn, TaskType.PRODUCTTAG);
        //ApplicationController.loadTaskProduct(conn);
        //ApplicationController.assignTask(conn, TaskType.PRODUCT);
        //ApplicationController.assignTask(conn, TaskType.COMMENT);

    }

}
