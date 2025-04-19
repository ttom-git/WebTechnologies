package it.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/dbWeb";
    private static final String USER = "root";
    private static final String PASSWORD = "tomsql2025";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found :(");
            e.printStackTrace();
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args){
        try (Connection connection = getConnection()){
            System.out.println("[:D] Connected to SQL");
        } catch(Exception e){
            System.out.println("[X] FAILED TO CONNECT TO SQL" + e.getMessage());
        }
    }
}
