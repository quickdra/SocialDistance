/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vlpap
 */
import java.time.LocalTime;
import java.sql.*;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;


public class Store implements GlobalConstants {
    String Name;
    String Location;
    String Manager_Mobile;
    String Auth_Info;
    int Floor_Area;
    
    // Timings
    LocalTime StartTime;
    LocalTime EndTime;
    
    boolean Store_Status; // True - Open, False - Closed
    int People_Count; // Amount of people present in the store currently
    
    // Entry QR code
    String QRCode1;
    // Exit QR code
    String QRCode2;
    int Max_People;
    int Max_InRate;
    String InQueue;
    String StoreID; // Need to create a storeID
    String Locality;
    
    // Constructor for the class Store
    public Store(String Name, String Location, String mobile_num, String auth_info,
            int area, LocalTime startTime, LocalTime EndTime, int people_count, String StoreID, String Locality){
        
        // Initializing the class variables
        // This part of the constructor covers Algorithm 1.1
        this.Name = Name;
        this.Location = Location;
        this.Manager_Mobile = mobile_num;
        this.Auth_Info = auth_info;
        this.Floor_Area = area;
        this.StartTime = startTime;
        this.EndTime = EndTime;
        this.People_Count = people_count;
        this.Max_People = (int) Math.round(this.Floor_Area * PplPerM2);
        this.Max_InRate = (int) Math.round(this.Max_People/MaxTimeInStore);
        this.InQueue = "";
        for(int i = 0; i <= (int)MaxWindow/MaxTimeInStore;i++){
            this.InQueue = this.InQueue + "0,";//Each slider selection
        }
        this.StoreID = StoreID;
        this.QRCode1 = this.Auth_Info + this.Name + "Entry";
        this.QRCode2 = this.Auth_Info + this.Name + "Entry";
        this.Locality = Locality;
        //the above statements are just one way to create unique qrcode strings
        //the preferred statements can be edited later on
          
}
    // This method will insert the above details of the store in the database
    public void Insert(String DB_URL, String USER, String PASS) throws ClassNotFoundException{
        Statement stmt = null;
        Connection conn = null;
        try{
            
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            String sql = "INSERT INTO Store VALUES (" + this.Name + "," +
                    this.Location +","+this.Manager_Mobile + 
                    ","+this.Auth_Info+"," + this.StartTime.toString()+
                    ","+this.EndTime.toString()+","+Integer.toString(this.People_Count)+
                    ","+this.StoreID+","+this.QRCode1 +","+this.QRCode2+
                    ","+Integer.toString(this.Max_People)+","+Integer.toString(this.Max_InRate)+
                    ","+this.InQueue+","+this.Locality+ ")";
            stmt.executeUpdate(sql);
        }catch(SQLException se){
            se.printStackTrace();        
        }
        
    }
    
    // This function is to mark the store open in the database
    // Covers Algorithm 3
    // Function is Independent of the class - does not use any class attributes
    // Function can be separated later on
    public static void Open(String QRCode, String mobile, String DB_URL, String USER, String PASS, int Hours) throws ClassNotFoundException{
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
                // Now authenticating the phone number of the staff member
                
                sql = "SELECT *"
                    + "FROM StoreStaff"
                    + "WHERE StoreID == "+ StoreID;
                ResultSet staff_val = stmt.executeQuery(sql);
                if (!staff_val.isBeforeFirst()){
                    System.out.println("Store ID not found");
                    return;
                }
                boolean check = false;
                while(staff_val.next() && check == false){
                    if(staff_val.getString("Mobile") == mobile){
                        check = true; 
                    }
                }
                // Update the store to be open
                if(check == true){
                    sql = "UPDATE Store"
                            + "SET StoreStatus = 1 WHERE StoreID == "+ StoreID;
                    stmt.executeUpdate(sql);
                    // Updating the time the store will be open till
                    LocalTime current = LocalTime.now();
                    LocalTime EndTime = current.plusHours(Hours);
                    sql = "UPDATE Store"
                            + "SET EndTime = "+EndTime.toString()+" WHERE StoreID == "+ StoreID;
                    stmt.executeUpdate(sql);
                            
                }             
            }
        }catch(SQLException se){
            se.printStackTrace();        
        }
        
    }
    
    
    // Covers Algorithm 4
    // Marks all stores as closed whose closing time has just passed
    // Function is independent and can be separated later on
    public static void BackGround(String DB_URL, String USER, String PASS) throws ClassNotFoundException {
        Statement stmt = null;
        Connection conn = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            String sql = "SELECT * FROM Store"
                    + "WHERE StoreStatus == 1";//Obtaining stores that are open
            ResultSet value = stmt.executeQuery(sql);
            if(!value.isBeforeFirst()){
                System.out.println("All stores are closed");
                return;
                
            }
            else{
                while(value.next()){
                    LocalTime EndTime = LocalTime.parse(value.getString("EndTime"));
                    String StoreID = value.getString("StoreID");
                    LocalTime current = LocalTime.now();
                    String InQueue = value.getString("InQueue");
                    int check = current.compareTo(EndTime);
                    // if current time is equal to or past endtime
                    if(check == 0 || check == 1){
                        sql = "UPDATE Store"
                                + "SET StoreStatus = 0, PeopleCount = 0 WHERE StoreID == "+ StoreID;
                        stmt.executeUpdate(sql);
                        int index = InQueue.indexOf(',');
                        //Removing first element of InQueue and appending 0
                        String newQueue = InQueue.substring(index+1,InQueue.length()) + ",0";
                        sql = "UPDATE Store "
                                + "SET InQueue = "+ newQueue +" WHERE StoreID == "+ StoreID;
                        stmt.executeUpdate(sql);
                    
                    }
                    
                }
            }
            
            
        }catch(SQLException se){
            se.printStackTrace();
            
        }
        
    }
    
    
    //Generating two unique QR codes
    // This Function covers Algorithm 1.2
    public void createQRCode(String FilePath1, String FilePath2,
            int qrCodeHeight, int qrCodeWidth) throws WriterException, IOException {
        
       // The first QR code is stored into FilePath1
       String qrCodeData = this.QRCode1;
       QRCodeWriter qrCodeWriter = new QRCodeWriter();
       BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeData, BarcodeFormat.QR_CODE, qrCodeHeight, qrCodeWidth);
       Path path = FileSystems.getDefault().getPath(FilePath1);
       MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
       
       //The second QR code is stored into FilePath2
       qrCodeData = this.QRCode2;
       qrCodeWriter = new QRCodeWriter();
       bitMatrix = qrCodeWriter.encode(qrCodeData, BarcodeFormat.QR_CODE, qrCodeHeight, qrCodeWidth);
       path = FileSystems.getDefault().getPath(FilePath2);
       MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
       
    }
    
    // This Function covers Algorithm 1.3
    // Send the images from FilePath1 and FilePath2 to the appinterface
    public void SendQRCodes(){
        
    }
    
    public static void main(String[] args) throws WriterException, IOException{
        String qrCodeData = "Hello World!";
	String FilePath = "QRCode.png";
	String Charset = "UTF-8"; // or "ISO-8859-1"
	
        
        
        
	System.out.println("QR Code image created successfully!");
    }
    
}
