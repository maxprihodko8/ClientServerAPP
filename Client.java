
import java.io.*;
import java.net.Socket;
import java.time.LocalTime;

public class Client {
    public final String host = "localhost";
    public final int port = 5555;
    public String messageFromUser;
    public String messageFromServer;

    BufferedReader inFromUser;
    BufferedReader inFromServer;
    PrintWriter outServer;
    Socket clientSocket;

    public void start() throws IOException{
        clientSocket = new Socket(host,port); //server connection
        inFromUser = new BufferedReader(new InputStreamReader(System.in));
        inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        outServer = new PrintWriter(clientSocket.getOutputStream(), true);
        Thread readThread = new Thread(new Reader());
        readThread.start();

    }

    public void closeConn() throws IOException{
        inFromServer.close();
        inFromUser.close();
        outServer.close();
        clientSocket.close();

    }

    public class Reader implements Runnable{
        public void run(){
            try{
                System.out.print("Enter what to send to server\n");
                while ((messageFromUser = inFromUser.readLine()) !=null){
                    outServer.println(messageFromUser);
                    messageFromServer = inFromServer.readLine();
                    System.out.println(messageFromServer);
                    System.out.print("Enter what to send to server\n");
                }
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String []argv) {
        Client c1 = new Client();
        System.out.print("Client started " + LocalTime.now() + "\n");
        try {
            c1.start();
        } catch (IOException e) {
            System.out.print("Connection lost");
        }

    }


}
