
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

public class chat {

    private static final int PORT = 9001;

    private static HashSet<String> names = new HashSet<String>();
    //private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();  
    private static HashMap<String, PrintWriter> nameOut = new HashMap<String, PrintWriter>();
    
    private static HashSet<PrintWriter> room1 = new HashSet<PrintWriter>();   
    private static HashSet<PrintWriter> room2 = new HashSet<PrintWriter>();  
    private static HashSet<PrintWriter> room3 = new HashSet<PrintWriter>();   
    private static HashSet<PrintWriter> room4 = new HashSet<PrintWriter>();    
    private static HashSet<PrintWriter> room5 = new HashSet<PrintWriter>();
    private static HashSet<PrintWriter> room6 = new HashSet<PrintWriter>();
   
    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running.");
        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }
  
    private static class Handler extends Thread {
        private String line;
    	private String name;
        private int roomNum;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String receiver;
        private String picture;
              
        public Handler(Socket socket) {
            this.socket = socket;
        }

        /**
         * Services this thread's client by repeatedly requesting a
         * screen name until a unique one has been submitted, then
         * acknowledges the name and registers the output stream for
         * the client in a global set, then repeatedly gets inputs and
         * broadcasts them.
         */
        public void run() {
            try {

                // Create character streams for the socket.
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                            
                while (true) {      
                    line = in.readLine();
                    System.out.println(line);
                    name = line.substring(0, line.indexOf("/"));
                    roomNum = Integer.parseInt(line.substring(line.indexOf("/")+1));
                    System.out.println(name + "_" + roomNum);
                    if (name == null) {
                    	System.out.println("null");
                        return;
                    }
                    synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                            nameOut.put(name,out);
                            System.out.println("done");
                            break;
                        }
                    }

                }
                   
                //writers.add(out);
                picture="pic";               
                                    
                switch(roomNum) {
                case -1:
                	receiver = in.readLine();
                	break;
                case 1:
                	room1.add(out);
                	break;
                case 2:
                	room2.add(out);
                	break;
                case 3:
                	room3.add(out);
                	break;
                case 4:
                	room4.add(out);
                	break;
                case 5:
                	room5.add(out);
                	break;
                case 6:
                	room6.add(out);
                	break;
                default:
                	break;
                }             
                              
                while (true) {
                	//1:1 chat
                	if(roomNum==-1) {
                		String input = in.readLine();
                        if (input == null) {
                            return;
                        }                                             
                        //Find the target's name in the HashMap to sent the message
                      	for (String userName : nameOut.keySet()) {
                      		if(receiver.equals(userName)){
                      			PrintWriter outWriter = nameOut.get(userName);
                      			outWriter.println(name + "/" + receiver + "/"+ input + "/" + picture);
                      			break;
                      		}
                         } 
                      	
                	} 
                	//1:n chat
                	else {
                		String input = in.readLine();
                        if (input == null) {
                            return;
                        }
                                                
                        switch(roomNum) {
                        case 1:
                        	for (PrintWriter writer : room1) {
                                writer.println(name + "/" + roomNum + "/" + input + "/" + picture);
                            }
                        	break;
                        case 2:                     	
                        	for (PrintWriter writer : room2) {
                                writer.println(name + "/" + roomNum + "/" + input + "/" + picture);
                            }
                        	break;
                        case 3:                        	
                        	for (PrintWriter writer : room3) {
                                writer.println(name + "/" + roomNum + "/" + input + "/" + picture);
                            }
                        	break;
                        case 4:                        	
                        	for (PrintWriter writer : room4) {
                                writer.println(name + "/" + roomNum + "/" + input + "/" + picture);
                            }
                        	break;
                        case 5:                        	
                        	for (PrintWriter writer : room5) {
                                writer.println(name + "/" + roomNum + "/" + input + "/" + picture);
                            }
                        	break;
                        case 6:                        	
                        	for (PrintWriter writer : room6) {
                                writer.println(name + "/" + roomNum + "/" + input + "/" + picture);
                            }
                        	break;
                        default: 
                        	break;
                        }
                	}
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                // This client is going down!  Remove its name and its print
                // writer from the sets, and close its socket.
            	
            	//Store the exiting user's name 
            	String userExit=null;
            	
                if (name != null) {
                	userExit=name;
                    names.remove(name);
                    nameOut.remove(name,out);
                    System.out.println(name + "_out");
                }
                if (out != null) {
                    //writers.remove(out);
                    switch(roomNum) {                   
                    case 1:
                    	room1.remove(out);                 	
                    	break;
                    case 2:
                    	room2.remove(out);               	
                    	break;
                    case 3:
                    	room3.remove(out);                  	
                    	break;
                    case 4:
                    	room4.remove(out);                 	
                    	break;
                    case 5:
                    	room5.remove(out); 
                    	break;
                    case 6:
                    	room6.remove(out);
                    	break;
                    default:
                    	break;
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}