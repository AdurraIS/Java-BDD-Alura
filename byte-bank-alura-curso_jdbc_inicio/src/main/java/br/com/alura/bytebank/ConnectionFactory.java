package br.com.alura.bytebank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

	public Connection RecuperaConexao() {

		try{

			return DriverManager
					.getConnection("jdbc:mysql://localhost:3306/byte_bank?user=root");

		} catch (SQLException e){
			throw new RuntimeException(e);

		}
	}
}
