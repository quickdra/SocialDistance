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

public class Customer implements GlobalConstants{
    
    // Covers algorithm 5
    public static void Entry(String QRCode, String DB_URL, String USER, String PASS) throws ClassNotFoundException{
        Statement stmt = null;
        Connection conn = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            String sql = "SELECT * FROM Store"
                    + "WHERE QRCode1 == "+ QRCode;
            //QRCode1 is the entry code
            ResultSet value = stmt.executeQuery(sql);
            if(!value.next()){
                System.out.println("The QRCode given is not an entry code");
                return;
            }
            else{
                String status = value.getString("StoreStatus");
                String StoreID = value.getString("StoreID");
                int PeopleCount = Integer.valueOf(value.getString("PeopleCount"));
                int MaxPeople = Integer.valueOf(value.getString("MaxPeople"));
                if(PeopleCount < MaxPeople){
                    //Show green symbol in the app
                    //Update peoplecount in database
                    PeopleCount = PeopleCount + 1;
                    sql = "UPDATE Store SET PeopleCount = "+ Integer.toString(PeopleCount)
                            +" WHERE StoreID == "+ StoreID;
                    stmt.executeUpdate(sql);
                    //Start Timer by using MaxTimeInStore from GlobalConstants
                }
                else{
                    //Show Red signal
                    return;
                
            }
            }
            
        }catch(SQLException se){
            se.printStackTrace();
        }
    }
    
    
    // Covers Algorithm 6
    public static void Exit(String QRCode, String DB_URL, String USER, String PASS) throws ClassNotFoundException{
        Statement stmt = null;
        Connection conn = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            String sql = "SELECT * FROM Store "
                    + "WHERE QRCode2 == "+QRCode;
            //QRCode2 is the exit code
            ResultSet value = stmt.executeQuery(sql);
            if(!value.next()){
                System.out.println("The QRCode is not an exit code/ the qrcode does not exist in db");
                return;
            }
            else{
                String StoreID = value.getString("StoreID");
                String StoreStatus = value.getString("StoreStatus");
                int PeopleCount = Integer.valueOf(value.getString("PeopleCount"));
                if(StoreStatus == "1"){
                    PeopleCount = PeopleCount - 1;
                    sql = "UPDATE Store SET PeopleCount = "+Integer.toString(PeopleCount)
                            +" WHERE StoreID == "+ StoreID;
                    stmt.executeUpdate(sql);
                    //Reset timer in device       
                }
            }
            
        }catch(SQLException se){
            se.printStackTrace();
        }
    }
    
    
    //Covers Algorithm 7
    public static void Recommendation(String CustomerLocation){
        
    }
    
}
