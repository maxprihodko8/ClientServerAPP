import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Server {
    ArrayList clientOutputStreams;
    ArrayList <String>servSocket = new ArrayList<>(); //info about clients
    Map<String, String> h1 = new HashMap<String, String>();
    String msgFromClient;
    BufferedReader inFromClient = null;
    Socket connectedSocket;
    String allNewsHere = "NEWS! : ";

    public class SocketDispatcher implements Runnable {
        Socket connectedSocket;

        public SocketDispatcher(Socket connectedSocket) {
            this.connectedSocket = connectedSocket;
        }

        @Override
        public void run(){
            try {
                inFromClient = new BufferedReader(new InputStreamReader(connectedSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                PrintWriter outToClient1 = new PrintWriter(connectedSocket.getOutputStream(), true);
                while ((msgFromClient = inFromClient.readLine()) != null) {

                    System.out.println("Client send message: " + msgFromClient);

                    if(msgFromClient.matches("echo")) {
                        outToClient1.println("Write any sentence to echo!");

                        if((msgFromClient = inFromClient.readLine())!=null)
                            tellEveryone(msgFromClient);

                        else
                            outToClient1.println("no symbols");
                    }

                    else if(msgFromClient.matches("info")){
                        outToClient1.println(getInfoAboutClients());
                    }

                    else if(msgFromClient.matches("getNews")){
                        if(allNewsHere.equals(""))
                            outToClient1.println("no news");
                        else
                            outToClient1.println(allNewsHere);
                    }

                    else if(msgFromClient.matches("setNews")){
                        outToClient1.println("Write your news please!");
                        if((msgFromClient = inFromClient.readLine())!=null)
                        {
                            tellEveryone("News changed");
                            allNewsHere += "  " + msgFromClient;
                        }
                    }

                    else if(msgFromClient.matches("loginMe")){
                        outToClient1.println("Write login!");
                        String userLogin, userPassword;
                        if ((msgFromClient = inFromClient.readLine())!=null){
                            userLogin = msgFromClient;
                            outToClient1.println("Write password!");
                            if ((msgFromClient = inFromClient.readLine())!=null){
                                userPassword = msgFromClient;
                                if( h1.containsKey(userLogin) && h1.containsValue(userPassword)){
                                    outToClient1.println("You are welcome to enter the system as user!");
                                }
                                else{
                                    outToClient1.println("You don't have login or password, want to add? (y,n)");
                                    if((msgFromClient = inFromClient.readLine())!=null){
                                        if(msgFromClient.equals("y")){
                                            outToClient1.println("Write login!");
                                            if((msgFromClient = inFromClient.readLine())!=null){
                                                userLogin = msgFromClient;
                                                outToClient1.println("Write password!");
                                                if((msgFromClient = inFromClient.readLine())!=null){
                                                    userPassword = msgFromClient;
                                                    h1.put(userLogin,userPassword);
                                                    outToClient1.println("added succesfully");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    else{
                        outToClient1.println("No usages, your sentence is " + msgFromClient + ", Please use echo, info, getNews, setNews, loginMe instead");
                    }
                    System.out.println("Writed " + msgFromClient + " to client");


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inFromClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                connectedSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() throws IOException {
        clientOutputStreams = new ArrayList();
        ServerSocket serverSocket = new ServerSocket(5555);
        while (true) {
            connectedSocket = serverSocket.accept();
            PrintWriter outToClient = new PrintWriter(connectedSocket.getOutputStream(),true);
            System.out.println("Connected " + connectedSocket.getInetAddress() + " " + connectedSocket.getPort() + " " + connectedSocket.getTrafficClass());
            clientOutputStreams.add(outToClient);
            String s1 = "ip = " + connectedSocket.getInetAddress().toString() + " port = " + connectedSocket.getPort() + " traffic class =  " + connectedSocket.getTrafficClass() + "  ";
            if(!s1.equals(""))
                servSocket.add(s1);
            else
                servSocket.add("none");
            new Thread(new SocketDispatcher(connectedSocket)).start();
        }
    }

    public String getInfoAboutClients() {
        Iterator it = servSocket.iterator();
        String s =  "";
        while (it.hasNext()){
            s+= it.next();
        }
        return s;
    }

    public void tellEveryone(String message) {
        Iterator it = clientOutputStreams.iterator();
        while (it.hasNext()) {
            try {
                PrintWriter outToClient = (PrintWriter)it.next();
                outToClient.println(message);
                outToClient.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String []argv){
        Server s = new Server();
        System.out.print("Server started " + LocalTime.now() + "\n");

        try {
            s.start();
        } catch (IOException e) {
            System.out.print("Connection reset");
        }

    }
}

