package com.Ruangfafa;

import com.Ruangfafa.common.ConfigLoader;
import com.Ruangfafa.service.DatabaseService;

import java.sql.Connection;

public class CreateClientTest {
    public static void main(String[] args) {
        Connection conn = DatabaseService.getConnection(ConfigLoader.DB_URL,ConfigLoader.DB_USER,ConfigLoader.DB_PASSWORD);
        DatabaseService.createClient(conn);
        //DatabaseService.deleteClient(conn,1);

    }
}
