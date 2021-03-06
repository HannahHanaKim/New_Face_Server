

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class login extends Thread {

   private static final int PORT = 9002;
   //values to store
   private static ArrayList<String> student_id = new ArrayList<String>(); //client's student id
   private static ArrayList<String> passwords = new ArrayList<String>();
   private static ArrayList<String> characters = new ArrayList<String>(); 
   private static ArrayList<String> intro = new ArrayList<String>();
   private static ArrayList<String> names = new ArrayList<String>(); //client names
   private static ArrayList<String> mapping = new ArrayList<String>();

   public static PrintWriter outputStream = null;
   public static Scanner inputStream = null;
   public static String fileName = "info.txt";

   public static void main(String[] args) throws Exception {
      System.out.println("The chat server is running.");

      ServerSocket listener = new ServerSocket(PORT);

      try {
         while (true) {
            new Handler(listener.accept()).start(); //server accept! -> ready for receiving client
         }
      } finally {
         listener.close();
      }
   }

   private static class Handler extends Thread {

      private String from_client;
      private String id;
      private String password;
      private String standard ="";
      private String matching_cmp = "";
      private String[] login = new String[2]; //getting values from the client
      private String[] signup = new String[6]; //for getting values to input in the file
      private String login_id;
      private String login_pw;
      private Socket socket;
      private BufferedReader in;
      private PrintWriter out;
      private double probability;
      
      public Handler(Socket socket) {
         this.socket = socket;
      }
      /** Login method
       *  input: id and password of the user
       *  output: the user's basic information and the matched probability
       *  In this method, we have to calculate the probability based on the matched elements.
       *  After calculating probability, send the informations to the client.
       */
      public void login(){
         try {
            String all_friend = ""; //initialize
            /* reading file for the first time: USER IDENTIFICATION */
            try {  //open file 
               inputStream = new Scanner(new File(fileName));
            } catch (FileNotFoundException e) {
               System.out.println("Error opening the file " + fileName);
               System.exit(0);
            }
            
            String line = in.readLine();

            login = line.split(" ");
            login_id = login[0];
            login_pw = login[1];
            
            String fileLine; //a string to get file values
            String[] fileValue = new String[5]; //each value will go into a fileValue array
            
            String[] my_friend = new String[100];
            int myf_cnt = 0; //number of friends
            int flg = 0; //number of times of reading file
            
            while (true) {
            	/* reading file for the second time: for MATCHING */
               try { 
                  fileLine = inputStream.nextLine();
               } catch (Exception e) {
                  if (flg == 1) { //already read once
                     /* need to read the file again */
                     try { //we have to open the file again for the login
                        inputStream = new Scanner(new File(fileName)); //read again
                     } catch (FileNotFoundException e1) {
                        System.out.println("Error opening the file " + fileName);
                        System.exit(0);
                     }
                     my_friend = new String[100];
                     myf_cnt = 0;
                     flg = 2;
                     fileLine = inputStream.nextLine();
                  }
                  else {
                     break;
                  }   
               }
              
               if (fileLine == null) //just in case
                  break;
   
               fileValue = fileLine.split(" ");
               id = fileValue[1];
               password = fileValue[2];
   
               if (id.equals(login_id) && password.equals(login_pw)) { //if login success,
                  if (flg == 2) {
                     flg = 2;
                  } else { 
                     flg = 1; //first rotation
                  }
                  standard = fileValue[5]; //input the user's matching value into another variable.
               }
               else { //for comparing with other people that are stored in the file
                  matching_cmp = fileValue[5]; //comparing variable
                  int count = 0;
                  for (int i = 0; i < standard.length(); i++) { //compare
                     if (standard.charAt(i) == matching_cmp.charAt(i)) //if the bit is the same,
                        count++; //increase the count value
                  }
                  //calculate the probability
                  probability = (double)((double)count / 43.0) * (double)100;
                  //store information in array to send it to the client
                  my_friend[myf_cnt] = "/ " + fileValue[0] + " " + fileValue[1] + " " + fileValue[3] + " " + fileValue[4] + " " + Double.toString(probability);
                  myf_cnt++;
               }
               /* sorting */
               for (int ii = 0; ii < myf_cnt - 1; ii++) {
                  for (int i = 0; i < myf_cnt - 1; i++) {
                     if (my_friend[i + 1] != null) { //until the file end
                        if (Double.parseDouble((my_friend[i].split(" ")[5])) < Double.parseDouble((my_friend[i+1].split(" ")[5]))) { // compare
                        	/* swap */
                           String tmp = my_friend[i + 1]; 
                           my_friend[i + 1] = my_friend[i];
                           my_friend[i] = tmp;
                        }
                     } else {
                        break;
                     }
                  }
               }
            } //end while
               
            for (int i = 0;i<myf_cnt;i++) {
               all_friend = all_friend + my_friend[i];
            }
            if (flg == 1 || flg == 2)
               out.println(all_friend);
            else
               out.println("Error");
            inputStream.close();
         } catch (Exception e) {
            
         }
      }
      
      /** Sign up method
       *  for the new clients
       *  input: get information from the new user
       *  output: none_just store it into the file
       */
      public void signUp() {
         try {
            from_client = in.readLine(); //receive the name from the client
            signup = from_client.split(" ");
            
            /** storing in arraylists! **/
         
            student_id.add(signup[0]); //id
            passwords.add(signup[1]); //password
            names.add(signup[2]); //name
            characters.add(signup[3]); // character picture
            intro.add(signup[4]); //introduction
            mapping.add(signup[5]); //mapping
            out.println("회원가입 성공");
            
            //storing information into the file
            try {
               outputStream = new PrintWriter(new FileOutputStream(fileName, true));
            } catch (FileNotFoundException e) {
               System.out.println("Error opening the file " + fileName);
               System.exit(0);
            }

            outputStream.print(signup[0] + " " + signup[1] + " " + signup[2] + " " + signup[3] + " " + signup[4] + " " + signup[5]);  //stored!
            outputStream.print("\n");
            outputStream.close();
   
         } catch (Exception e) {
            
         }
      }
      public void run() {
         try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8")); //read from the client
            //UTF-8 is for Korean translation
            out = new PrintWriter(socket.getOutputStream(), true);  //print what the client wrote

            while (true) { //sign up_id
               String tmp = in.readLine();
               if (tmp.equals("LOGIN")){ //log in
                  login();
               } else { //sign up
                  signUp();
               }   
            }
         } catch (IOException e) {
            System.out.println(e);
         } finally {
            try {
               socket.close();
            } catch (IOException e) {
            }
         }
      }
   }
}