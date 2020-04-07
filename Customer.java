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
    
    //calculates distance between two give locations
    public static float Distance(String Location, String CustomerLocation){
        return (float) 5;
        //Based on location format this function needs to be implemented
    }
    
    //Covers Algorithm 7
    public static void Recommendation(String CustomerLocation,String CustomerLocal, String Distance, String DB_URL, String USER, String PASS) throws ClassNotFoundException{
        Statement stmt = null;
        Connection conn = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            String sql = "SELECT * FROM Store WHERE Locality == "+ CustomerLocal;
            ResultSet value = stmt.executeQuery(sql);
            if(!value.isBeforeFirst()){
                System.out.println("Error no stores fetched with the same locality");
                return;
            }
            else{
                int rowCount = 0;
                if(value.last()){
                    rowCount = value.getRow();
                    value.beforeFirst();
                }
                String[] IDarray = new String[rowCount];
                float[] distance = new float[rowCount];
                int count = 0;
                while(value.next()){
                    IDarray[count] = value.getString("StoreID");
                    distance[count] = Distance(value.getString("Location"), CustomerLocation);
                    count = count + 1; 
                    // Both the arrays can be sorted based on the distance array for recommendation
                    int PeopleCount = Integer.valueOf(value.getString("PeopleCount"));
                    int MaxPeople = Integer.valueOf(value.getString("MaxPeople"));
                    if(PeopleCount >= MaxPeople - 2){
                        //display red
                    }
                    else{
                        //display green
                    }
                    
                    
                }
                
            }
        }catch(SQLException se){
            se.printStackTrace();
        }
             
    }
        //For algorithm 8 it gets the customer selection
        public static int getSliderSelection(){
            return 0;
        }
    
        //Covers Algorithm 8
        //The InQueue array is string and not float as everytime an update or fetch from db has to be done
        // the inqueue array would have to be parsed element wise and build into a float array, and to update
        //db will have to be stored again as a string
        public static void OnStatus(String StoreID, String DB_URL, String PASS, String USER) throws ClassNotFoundException{
            Statement stmt = null;
            Connection conn = null;
            try{
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                stmt = conn.createStatement();
                String sql = "SELECT * FROM Store WHERE StoreID == "+ StoreID;
                ResultSet value = stmt.executeQuery(sql);
                if(!value.next()){
                    System.out.println("Error");
                }
                else{
                    String InQueue = value.getString("InQueue");
                    int index = InQueue.indexOf(InQueue);
                    int counter = 0;
                    int N = InQueue.length();
                    while(index > 0 && index < N){
                        index = counter + index;
                        float element = Float.parseFloat(InQueue.substring(counter,index));
                        if(element > 0.6){
                            //Display red in slider
                        }
                        else{
                            //Display green in slider
                        }
                        counter = index;
                        
                        index = InQueue.substring(counter,N).indexOf(",");                    
                    }
                    //Based on user slection change InQueue element
                    //and then update
                    int SliderIndex = getSliderSelection();//Fetches the slider index of the customer's choice
                    index = InQueue.indexOf(InQueue);
                    counter = 0;
                    int count = 0;
                    while(index > 0 && index < N){
                        index = counter + index;
                        if(count == SliderIndex){
                            InQueue = InQueue.substring(0,index - counter)+ ",0.65" +InQueue.substring(index, N);//",0.65" is based on customer choice
                        }
                        count += 1;
                        counter = index;
                        index = InQueue.substring(counter,N).indexOf(",");
                                
                    }
                    sql = "UPDATE Store SET InQueue == "+ InQueue+" WHERE StoreID == "+StoreID;
                    stmt.executeUpdate(sql);
                
                }
            }catch(SQLException se){
                se.printStackTrace();
            }
            
        }
    
}
