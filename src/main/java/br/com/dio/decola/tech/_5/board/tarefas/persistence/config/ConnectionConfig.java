package br.com.dio.decola.tech._5.board.tarefas.persistence.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import static lombok.AccessLevel.PRIVATE;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@NoArgsConstructor(access = PRIVATE)
public class ConnectionConfig {

    public static Connection getConnection() throws SQLException{
        var url = "jdbc:mysql://localhost:3306/mydatabase?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        var user = "admin";
        var password = "adminPW";
        var connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(false);
        return connection;
    }
}
