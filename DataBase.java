/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vlpap
 */
import java.sql.*;

public class DataBase {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/";
    static final String USER = "username";
    static final String PASS = "password";
    
    
    public static void main(String[] args) throws ClassNotFoundException{
        Statement stmt = null;
        Connection conn = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            
            String sql = "CREATE DATABASE STOREDB";
            stmt.executeUpdate(sql);
            System.out.println("Database created");
            
            // Creating a table for stores
            sql = "CREATE TABLE Store ("
                    + "StoreID VARCHAR(255),"// This will be the primary key, need to decide on rules for the StoreID 
                    + "Name VARCHAR(255),"
                    + "Location VARCHAR(255),"
                    + "ManagerMobile VARCHAR(255),"
                    + "AuthInfo VARCHAR(255),"
                    + "FloorArea INTEGER not NULL,"
                    + "StartTime VARCHAR(255),"// Timings was split into start time and end time
                    + "EndTime VARCHAR(255),"
                    + "StoreStatus BIT(1),"//if value 1 - store is open
                    + "PeopleCount INTEGER not NULL,"// Number of people currently in the store
                    + "QRCode1 VARCHAR(255),"// Since QRCode encode string values, the string can be stored in database and converted later locally
                    + "QRCode2 VARCHAR(255),"
                    + "MaxPeople INTEGER not NULL,"
                    + "MaxInRate INTEGER not NULL,"
                    + "InQueue JSON,"// A json object consisting of all customer IDs currently inside the store,
                    // Not sure InQueue will be useful, but included just in case
                    + "PRIMARY KEY (StoreID))";
            stmt.executeUpdate(sql);
            
            // creating the db for staff
            sql = "CREATE TABLE StoreStaff("
                    + "StoreID VARCHAR(255),"
                    + "Name VARCHAR(255),"
                    + "Mobile VARCHAR(255))";
            stmt.executeUpdate(sql);
            
            
            
            
            
            //Creating a table for store_staff
        }catch(SQLException se){
            se.printStackTrace();
        }
    }
}
