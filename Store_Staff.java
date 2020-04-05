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
import java.util.Random;
public class Store_Staff {
    //String StoreID;
    String Name;
    String Mobile;
    
    public Store_Staff(String Name, String Mobile){
        this.Name = Name;
        this.Mobile = Mobile;
    }
    
    // Registration of a staff member, covers algorithm 2
    public void registration(String QRCode, String DB_URL, String USER, String PASS) throws ClassNotFoundException{
        Statement stmt = null;
        Connection conn = null;
        try{
            
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            String sql = "SELECT *"
                    + "FROM Store"
                    + "WHERE QRCode1 == "+QRCode
                    +"OR QRCode2 == "+QRCode;
            ResultSet value = stmt.executeQuery(sql);
            if(!value.next()) {
                System.out.println("QRCode not found");
                return;        
            }
            else{
                String StoreID = value.getString("StoreID");
                String ManagerMobile = value.getString("ManagerMobile");
                // Creating OTP - 5 digit random number for example
                int max = 99999;
                int min = 10000;
                int OTP = new Random().nextInt(max - min + 1)+ min;
                //Send OTP and implement Check
                // if OTP is authenticated, insert data in StoreStaff table
                
                sql = "INSERT INTO StoreStaff "
                        + "VALUES ("+ StoreID +"," +this.Name+
                        ","+ this.Mobile + ")";
                stmt.executeUpdate(sql);               
            }
        }catch(SQLException se){
            se.printStackTrace();        
        }
        
    }
}
