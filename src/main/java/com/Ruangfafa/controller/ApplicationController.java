package com.Ruangfafa.controller;

import com.Ruangfafa.common.ConfigLoader;
import com.Ruangfafa.common.Constants.LogMessageCons;
import com.Ruangfafa.common.Constants.LogSourceCons;
import com.Ruangfafa.common.Constants.DatabaseServiceJava;
import com.Ruangfafa.common.Enums.TaskType;
import com.Ruangfafa.service.DatabaseService;

import java.sql.Connection;
import java.util.List;

import static com.Ruangfafa.service.Log.log;

public class ApplicationController {
    public static void assignTask(Connection conn, TaskType taskType) {
        List<Long> clients = DatabaseService.getFreeClient(conn);
        if (clients.isEmpty()) {
            log(LogMessageCons.DB_ASSIGNTASK_WARN, LogSourceCons.APPLICATION_CONTROLLER, ConfigLoader.LOG_PRINT);
            return;
        }

        // 1. 锁住所有 client
        for (long id : clients) {
            DatabaseService.setState(conn, id, DatabaseServiceJava.USERTABLE_STATE_LOCK, 1);
        }

        // 2. 读取 Server 中任务表
        List<String> urls = DatabaseService.getTask(conn, taskType);


        // 3. 平均分配任务
        int clientCount = clients.size();
        int index = 0;
        for (String url : urls) {
            long clientId = clients.get(index % clientCount);

            // 获取 clientName
            String clientName = DatabaseService.getClientNameById(conn, clientId);
            if (clientName == null) continue;

            // 向该 Client 的 Task 表插入任务
            DatabaseService.insertTask(conn, clientName, url);
            index++;
        }

        // 4. 设置所有 client 的 state = 对应的 taskType name
        for (long id : clients) {
            DatabaseService.setState(conn, id, DatabaseServiceJava.USERTABLE_STATE_STATE, taskType.getTaskInt());
        }

        // 5. 解锁所有 client
        for (long id : clients) {
            DatabaseService.setState(conn, id, DatabaseServiceJava.USERTABLE_STATE_LOCK, 0);
        }

        log(String.format(LogMessageCons.DB_ASSIGNTASK_SUCCESS, urls.size(), clients.size()), LogSourceCons.APPLICATION_CONTROLLER, ConfigLoader.LOG_PRINT);
    }

}
