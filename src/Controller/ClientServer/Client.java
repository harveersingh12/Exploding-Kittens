package Controller.ClientServer;

import Model.Protocol;
import Model.Exceptions.*;
import View.ClientTUI;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


/**
 * The {@code Client} class represents a client for the EXPLODING KITTENS game.
 * It connects to the server, handles communication, and interacts with the user interface.
 * The client supports basic and advanced commands defined in the {@link BasicCommands}
 * and {@link AdvancedCommand} enums.
 *
 * @author Andrei Cristian Dorneanu - s2327478
 * @author Harveer Singh - s3231046
 */
public class Client implements Runnable{


    /**
     * List of all fundamental (non-bonus) commands on the client side. These commands
     * are required for playing the game and starting it.
     */
    public enum BasicCommands implements Protocol.Command {
        CONNECT,ADD_COMPUTER,REMOVE_COMPUTER,REQUEST_GAME,PLAY_CARD,DRAW_CARD,RESPOND_PLAYERNAME, RESPOND_CARDNAME, RESPOND_INDEX, RESPOND_YESORNO;
    }


    /**
     * List of commands for bonus features. Currently, there is only the SEND command,
     * meant for one player to correspond with others (chat feature). This list, as with
     * the server commands can be extended.
     */
    public enum AdvancedCommand implements Protocol.Command {
        SEND
    }


    private Socket serverSock;
    private BufferedReader in;
    private BufferedWriter out;
    private ClientTUI view;


    /**
     * Constructs a new Client and initializes the associated user interface.
     * @ensures the client feels welcomed on the server with an ASCII art.
     */
    public Client(){
        view=new ClientTUI(this);
        view.welcomeThePlayer();
    }


    /**
     * Gets the Socket instance for the client's connection to the server.
     *
     * @return The Socket instance or null if not connected.
     */
    public Socket getServerSock(){
        return serverSock;
    }


    /**
     * Listens to server input continuously and processes the received messages.
     *
     * @ensures The method continuously reads messages from the server input stream,
     * prints them to the console, and terminates when the server input stream is closed.
     */
    @Override
    public void run(){
        String message;
        try{
            message = in.readLine();
            while(message != null){
                view.update(message);
                message = in.readLine();
            }
        }catch (IOException e){
            System.out.println("Exception " + e);
        }
    }


    /**
     * Starts the user interface for the client.
     */
    public void startView(){
            view.start();
    }


    /**
     * Creates a connection to the server.
     *
     * @throws ExitProgram if the user decides to exit.
     * @requires view instance must not be null.
     * @ensures The server socket, input, and output streams are initialized.
     */
    public void createConnection() throws ExitProgram{
        clearConnection();
        while(serverSock==null){
            String host="127.0.0.1";
            int port = 8811;

            //Uncomment the lines below and the comment the lines above to test user input values
            // to non hard-coded ip and port values.
//            String host = String.valueOf(view.getIp());
//            view.update("Enter a port on which you would like to connect.");
//            int port = -1;
//            try {
//                port=view.keyboardIntInput();
//            }catch (NumberFormatException e){
//                e.getMessage();
//            }
            try {
                InetAddress addr=InetAddress.getByName(host);
                view.update("Attempting to connect to "+addr+":"+port+"...");
                serverSock=new Socket(addr,port);
                in=new BufferedReader(new InputStreamReader(serverSock.getInputStream()));
                out=new BufferedWriter(new OutputStreamWriter(serverSock.getOutputStream()));
            } catch (IOException e) {
                view.update("ERROR: could not create a socket on "+host+" and port "+port+".");
                if(false){
                    throw new ExitProgram("User indicated to exit.");
                }
            }
        }
    }


    /**
     * Clears the connection by setting server socket, input, and output streams to null.
     */
    public void clearConnection(){
        serverSock=null;
        in=null;
        out=null;
    }


    /**
     * Sends a message to the server.
     *
     * @param msg The message to be sent.
     * @throws ServerUnavailableException if the server is not available.
     * @requires The msg parameter must be a non-null string.
     * @ensures The message is sent to the server via the output stream.
     */
    public void sendMessage(String msg) throws ServerUnavailableException{
        if(out!=null){
            try {
                out.write(msg);
                out.newLine();
                out.flush();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new ServerUnavailableException("Could not write to server.");
            }
        } else {
            throw new ServerUnavailableException("Could not write to server.");
        }
    }


    /**
     * Handles the initial connection and hello message exchange with the server.
     *
     * @throws ServerUnavailableException if the server is not available.
     * @throws ProtocolException if a protocol error occurs.
     *
     * @requires The view instance must not be null.
     * @ensures The hello message is sent to the server, and the server responds appropriately.
     */
    public void handleHello() throws ServerUnavailableException, ProtocolException{
        try {
            boolean messageSent = false;
            view.update("Connect using the protocol message.");
            while(!messageSent){
                String input=view.keyboardStringInput();
                String[] splitByDelimiter=input.split("~");
                if(splitByDelimiter[0].equals(String.valueOf(BasicCommands.CONNECT))){
                    if(splitByDelimiter.length==2){
                        sendMessage(BasicCommands.CONNECT+Protocol.ARGUMENT_SEPARATOR+splitByDelimiter[1]);
                        messageSent=true;
                        view.printHelpMenu();
                    } else if(splitByDelimiter.length==3){
                            sendMessage(BasicCommands.CONNECT+Protocol.ARGUMENT_SEPARATOR+splitByDelimiter[1]+Protocol.ARGUMENT_SEPARATOR+splitByDelimiter[2]);
                            messageSent=true;
                            view.printHelpMenu();
                    }
                }
                if(!messageSent){
                    view.update("Use a message of the form (CONNECT~your_name~flag1,flag2,...). Make sure the cards are valid.");
                    view.update("If you are not the first who connects -> use the same flags that the first client who connected used.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: "+e);
        }
    }


    /**
     * Main method to create and start a client instance.
     */
    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.createConnection();
            client.handleHello();
        }catch (Exception e){
            System.out.println("Error: " + e);
        }
        Thread thread = new Thread(client);
        thread.start();
        client.startView();
    }
}
