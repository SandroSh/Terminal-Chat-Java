import java.io.*;
import java.net.Socket;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

public class HandlerOfClient  implements  Runnable{

    public static ArrayList<HandlerOfClient> clientHandlers = new ArrayList<>();
    public static ArrayList<String> clientHandlersUserNames = new ArrayList<>();
    private Socket socket;

    // We Will use BufferedReader to read data, specifically messages that have been sent from the other client
    private BufferedReader bufferedReader;

    // We Will use BufferWriter for Send data, specifically  messages that current client will send to other Clients
    private BufferedWriter bufferedWriter;
    private LocalTime time;
    private  String clientUserName;



    ArrayList<String> pinguFacts = new ArrayList<>(
            Arrays.asList(
                    "In general, a penguin’s lifespan ranges from 15 to 20 years.",
                    "About 75% of a penguin’s life is spent in water, where they do all their hunting.",
                    "Penguins open their feather to feel the cold.",
                    "The first penguin fossil to be discovered was found in rocks that were around 25 million years old.",
                    "The four penguins in the film Madagascar are named Skipper, Kowalski, Rico, and Private.",
                    "Most penguins can swim about 15 miles per hour.",
                    "Penguins can jump 6 feet out of water.",
                    "The Linux mascot ‘Tux’ is a penguin.",
                    "When the chick hatches, it immediately starts calling so that its parents will learn to recognize its voice.",
                    "A penguin is an unofficial symbol of the United States Libertarian Party."
            ));

    public HandlerOfClient (Socket socket, LocalTime time){
        try{
            this.time = time;
            this.socket = socket;
            //we are going to use this to send data
            this.bufferedWriter = new BufferedWriter( new OutputStreamWriter(socket.getOutputStream()));
            // we are going to use this to read data
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUserName = bufferedReader.readLine();
            clientHandlers.add(this);
            clientHandlersUserNames.add(this.clientUserName);
            broadcastMessage("SERVER: " + clientUserName + " Has Joined the Chat");

        }catch (IOException e){
            closingClient(socket, bufferedWriter, bufferedReader);

        }
    }

    @Override
    public void run() {
        String messagefromOtherClient;
        while (socket.isConnected()){

            try{
                messagefromOtherClient = bufferedReader.readLine();
                broadcastMessage(messagefromOtherClient);
            }catch(IOException e) {
                closingClient(socket, bufferedWriter, bufferedReader);
                break;
            }
        }


    }
    public void removeClient(){
        clientHandlers.remove(this);
        clientHandlersUserNames.remove(this.clientUserName);
        broadcastMessage("SERVER: " + this.clientUserName + " Has left the Chat");
    }
    public void closingClient(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader){
        removeClient();
        try{

            if( bufferedReader != null){
                bufferedReader.close();
            }
            if( bufferedWriter != null){
                bufferedWriter.close();
            }
            if( socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public void broadcastMessage(String message){
        int randomIndexForFacts =  (int)(Math.random() * pinguFacts.size());
        if(message == null ||  message.split(" ")[1].equals("LOGOUT")){
            clientHandlers.remove(this);
            clientHandlersUserNames.remove(this.clientUserName);


        }else if( message.trim().equals("")){

        }
        clientHandlers.forEach(clientHandler -> {
            try{
                if( message != null){
                    char  charAtFirstPos = message.split(" ")[1].charAt(0);
                    String firstWord = message.split(" ")[1];
                    if(charAtFirstPos == '@'){
                        String splitedName = message.split(" ")[1].substring(1);
                        String[] correctedMessageArray = message.split(" ");
                        String correctedMessage = "";

                        if(clientHandlersUserNames.contains(splitedName) &&
                                clientHandler.clientUserName.equals(splitedName) &&
                                !clientHandler.clientUserName.equals(this.clientUserName)){
                            for (int i = 2; i < correctedMessageArray.length; i++) {
                                correctedMessage += correctedMessageArray[i] + " ";

                            }
                            clientHandler.bufferedWriter.write(LocalTime.now().getHour() +":"+ LocalTime.now().getMinute() +":"+ LocalTime.now().getMinute()+"------"+ this.clientUserName + "(Private Message): " +  correctedMessage.trim());
                            clientHandler.bufferedWriter.newLine();
                            clientHandler.bufferedWriter.flush();

                        }

                    }else if(firstWord.equals("PENGU")){

                        if(clientHandler.clientUserName.equals(this.clientUserName)){
                            clientHandler.bufferedWriter.write("\n" + "You've  sent interesting fact about penguins:\n" +  pinguFacts.get(randomIndexForFacts));
                        }else{
                            clientHandler.bufferedWriter.write("\n"+ this.clientUserName + " has sent interesting fact about penguins:\n"+ pinguFacts.get(randomIndexForFacts));
                        }
                        clientHandler.bufferedWriter.newLine();
                        clientHandler.bufferedWriter.flush();

                    }else if(firstWord.equals("WHOIS")){
                        if(clientHandler.clientUserName.equals(this.clientUserName)){
                            for (HandlerOfClient person : clientHandlers) {
                                clientHandler.bufferedWriter.write(person.clientUserName + " - - - Joined at " +  person.time);
                                clientHandler.bufferedWriter.newLine();
                                clientHandler.bufferedWriter.flush();
                            }
                        }


                    }else if(!clientHandler.clientUserName.equals(this.clientUserName)){


                        clientHandler.bufferedWriter.write(LocalTime.now().getHour() +":"+ LocalTime.now().getMinute() +":"+ LocalTime.now().getMinute()+"------"+ message);
                        clientHandler.bufferedWriter.newLine();
                        clientHandler.bufferedWriter.flush();
                    }
                }else{
                    tryCatchFunction();

                }
            }catch (IOException e){

                tryCatchFunction();
            }
        });
    }


    private  void tryCatchFunction(){
        try{

            if( bufferedReader != null){
                bufferedReader.close();
            }
            if( bufferedWriter != null){
                bufferedWriter.close();
            }
            if( socket != null){
                socket.close();
            }
        }catch (IOException k){
            k.printStackTrace();
        }
    }

}

