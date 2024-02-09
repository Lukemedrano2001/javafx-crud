package br.com.lukemedrano;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class Database {

    private static Connection conexao;

    public static Connection getConexao() {
        try {
            if (conexao == null || conexao.isClosed()) {
                Properties propriedades = carregarPropriedades();
                String url = propriedades.getProperty("banco.url");
                String usuario = propriedades.getProperty("banco.usuario");
                String senha = propriedades.getProperty("banco.senha");

                conexao = DriverManager.getConnection(url, usuario, senha);
            }

            return conexao;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static void fecharConexao() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static Properties carregarPropriedades() {
        try {
            InputStream input = Database.class.getResourceAsStream("application.properties");

            if (input == null) {
                System.err.println("O arquivo application.properties não foi encontrado.");
                return null;
            }

            Properties propriedades = new Properties();
            propriedades.load(input);

            if (propriedades.isEmpty()) {
                System.err.println("O arquivo application.properties está vazio ou não contém propriedades válidas.");
                return null;
            }

            return propriedades;
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}