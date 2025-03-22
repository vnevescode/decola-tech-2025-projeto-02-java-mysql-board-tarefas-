package br.com.dio.decola.tech._5.board.tarefas;

import br.com.dio.decola.tech._5.board.tarefas.persistence.migration.MigrationStrategy;
import br.com.dio.decola.tech._5.board.tarefas.ui.MainMenu;


import java.sql.SQLException;

import static br.com.dio.decola.tech._5.board.tarefas.persistence.config.ConnectionConfig.getConnection;


public class Application {

	public static void main(String[] args) throws SQLException {
		try(var connection = getConnection()){
			new MigrationStrategy(connection).executeMigration();
		}
		//SpringApplication.run(Application.class, args);
		new MainMenu().execute();
	}

}
