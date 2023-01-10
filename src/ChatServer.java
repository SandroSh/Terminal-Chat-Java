import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;

public class ChatServer {

    private ServerSocket socketOfServer;
    public ChatServer(ServerSocket serverSocket) {
        this.socketOfServer = serverSocket;
    }

    public void ServerStarting(){
        try {
            System.out.println("----------SERVER STARTED----------");
            while(!socketOfServer.isClosed()){
                System.out.println("Server is waiting for Clients");
                Socket clientsSocket = socketOfServer.accept();
                System.out.println("New Client Has Joined");
                HandlerOfClient clientHandler = new HandlerOfClient(clientsSocket, LocalTime.now());

                Thread thread = new Thread(clientHandler);
                thread.start();

            }
        }catch (IOException e){
            try {
                if(socketOfServer != null){
                    socketOfServer.close();
                }
            }catch (IOException k){
                k.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(3000);
        ChatServer server = new ChatServer(serverSocket);
        server.ServerStarting();

    }
}
