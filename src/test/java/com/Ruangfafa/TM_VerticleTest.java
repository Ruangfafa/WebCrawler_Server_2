package com.Ruangfafa;

import com.Ruangfafa.common.ConfigLoader;
import com.Ruangfafa.common.Enums;
import com.Ruangfafa.controller.ApplicationController;
import com.Ruangfafa.service.DatabaseService;

import java.sql.Connection;


public class TM_VerticleTest {
    public static void main(String[] args) {
        Connection conn = DatabaseService.getConnection(ConfigLoader.DB_URL,ConfigLoader.DB_USER,ConfigLoader.DB_PASSWORD);
        //ApplicationController.assignTask(conn, Enums.TaskType.SELLER);
        //ApplicationController.assignTask(conn, Enums.TaskType.SELLERTAG);
        //ApplicationController.loadTaskTag(conn);
        ApplicationController.assignTask(conn, Enums.TaskType.PRODUCTTAG);
    }
}
