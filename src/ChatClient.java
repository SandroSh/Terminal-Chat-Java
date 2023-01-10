import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private  String username;

    public void closing(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader){
        try{
            if( socket != null){
                socket.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(bufferedReader != null){
                bufferedReader.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner  = new Scanner(System.in);
        System.out.println("Enter Your Username For The Group Chat: ");
        String username = scanner.nextLine();
        System.out.println("""
                Hello! Welcome to the chatroom.
                Instructions:
                1. Simply type the message to send broadcast to all active clients
                2. Type '@username<space>yourMessage' without quotes to send message to desired client
                3. Type 'WHOIS' without quotes to see list of active clients
                4. Type 'LOGOUT' without quotes to logoff from server
                5. Type 'PENGU' without quotes to request a random penguin fact""");
        System.out.println();
        Socket socket = new Socket("localhost",3000);
        ChatClient client = new ChatClient(socket, username);
        client.listenForMessage();
        client.sendMessage();


    }
    public ChatClient(Socket socket, String username){
        try{

            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;


        }catch (IOException e){
            closing(socket, bufferedWriter,bufferedReader);

        }
    }
    public void sendMessage(){
        try{
            this.bufferedWriter.write(username);
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();

            Scanner scanner  = new Scanner(System.in);
            while(socket.isConnected()){
                String messageToSend = scanner.nextLine();
                if(messageToSend.equals(("LOGOUT"))){
                    closing(socket,bufferedWriter,bufferedReader);
                    System.exit(0);
                }
                this.bufferedWriter.write(this.username + ": " + messageToSend);
                this.bufferedWriter.newLine();
                this.bufferedWriter.flush();

            }
        }catch (IOException e){
            closing(this.socket, this.bufferedWriter,this.bufferedReader);
        }
    }

    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromGroupChat;
                while(socket.isConnected()){
                    try{
                        messageFromGroupChat = bufferedReader.readLine();
                        System.out.println(messageFromGroupChat);
                    }catch (IOException e){
                        closing(socket, bufferedWriter, bufferedReader);
                    }

                }
            }
        }).start();
    }
}

