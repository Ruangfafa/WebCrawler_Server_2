package com.Ruangfafa.controller;

import com.Ruangfafa.common.ConfigLoader;
import com.Ruangfafa.common.Constants.LogMessageCons;
import com.Ruangfafa.common.Constants.LogSourceCons;
import com.Ruangfafa.common.Constants.ApplicationControllerJava;
import com.Ruangfafa.common.Enums.*;
import com.Ruangfafa.model.TaskProduct;
import com.Ruangfafa.model.TaskTag;
import com.Ruangfafa.service.DatabaseService;

import java.sql.Connection;
import java.util.List;

import static com.Ruangfafa.service.DatabaseService.loadTask;
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
            DatabaseService.setState(conn, id, ApplicationControllerJava.USERTABLE_STATE_LOCK, 1);
        }
        // 2. 设置所有 client 的 state = 对应的 taskType name
        for (long id : clients) {
            DatabaseService.setState(conn, id, ApplicationControllerJava.USERTABLE_STATE_STATE, taskType.getTaskInt());
        }
        // 3. 读取 Server 中任务表
        List<String> urls = DatabaseService.getTask(conn, taskType);
        // 4. 平均分配任务
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
        // 5. 解锁所有 client
        for (long id : clients) {
            DatabaseService.setState(conn, id, ApplicationControllerJava.USERTABLE_STATE_LOCK, 0);
        }
        log(String.format(LogMessageCons.DB_ASSIGNTASK_SUCCESS, urls.size(), clients.size()), LogSourceCons.APPLICATION_CONTROLLER, ConfigLoader.LOG_PRINT);
    }

    public static void loadTaskTag(Connection conn) {
        List<TaskTag> sellerTagList = DatabaseService.getSellerTag(conn);
        for (TaskTag sellerTag : sellerTagList) {
            String[] taskTag = sellerTag.getTaskTag();
            String c = taskTag[2];
            String p = taskTag[3];
            String url;
            if (c != null && p != null &&
                    (c.equals(ApplicationControllerJava.TM_CP_TYPE_A) || p.equals(ApplicationControllerJava.TM_CP_TYPE_A))) {

                url = String.format(ApplicationControllerJava.TM_TASKTAG_URL_A, taskTag[1]);
                loadTask(conn, TaskTable.TAG, url);
            }
            else if (c == null) {
                url = String.format(ApplicationControllerJava.TM_TASKTAG_URL_P, taskTag[1], p);
                loadTask(conn, TaskTable.TAG, url);
            }
            else if (p == null) {
                url = String.format(ApplicationControllerJava.TM_TASKTAG_URL_C, taskTag[1], c);
                loadTask(conn, TaskTable.TAG, url);
            }
        }
    }

    public static void loadTaskProduct(Connection conn) {
        List<TaskProduct> productTagList = DatabaseService.getProduct(conn);
        for (TaskProduct productTag : productTagList) {
            String[] taskProduct = productTag.getTaskProduct();
            String url = ApplicationControllerJava.BLANK;
            if (taskProduct[0].equalsIgnoreCase(TaskProductPageType.TM.getPageTypeStr())) {
                url = String.format(ApplicationControllerJava.TM_TASKPRODUCT_URL, taskProduct[1]);
                loadTask(conn, TaskTable.PRODUCT, url);
            }
        }
    }
}
