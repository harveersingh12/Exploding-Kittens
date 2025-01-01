package Controller.ClientServer;

import Model.Protocol;
import Model.Exceptions.ExitProgram;
import Model.Exceptions.ProtocolException;
import Model.Exceptions.ServerUnavailableException;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


/**
 * The ComputerClient class represents a computer player in the game.
 * It communicates with the server, responds to game requests, and makes moves.
 * Everything is automated, and the Computer Player can understand and respond the
 * server's questions using the M2 Team Protocol (you can find a PDF of the protocol
 * in the files of this project.
 *
 * @author Andrei Cristian Dorneanu - s2327478
 * @author Harveer Singh - s3231046
 */
public class ComputerClient implements Runnable{
    private Socket serverSock;
    private BufferedReader in;
    private BufferedWriter out;
    private String[] hand = null;
    private int counter;


    /**
     * Constructs a ComputerClient with a counter.
     *
     * @param counter The counter to distinguish between multiple computer clients.
     */
    public ComputerClient(int counter){
        this.counter = counter;
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
     * Establishes a connection with the server.
     *
     * @throws ExitProgram If the user chooses to exit the program.
     */
    public void createConnection() throws ExitProgram{
        clearConnection();
        while(serverSock==null){
            String host="127.0.0.1";
            int port=8811;

            try {
                InetAddress addr=InetAddress.getByName(host);
                serverSock=new Socket(addr,port);
                in=new BufferedReader(new InputStreamReader(serverSock.getInputStream()));
                out=new BufferedWriter(new OutputStreamWriter(serverSock.getOutputStream()));
            } catch (IOException e) {
                if(false){
                    throw new ExitProgram("User indicated to exit.");
                }
            }
        }
    }


    /**
     * Closes the connection with the server.
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
     * @throws ServerUnavailableException If there is an issue with the server connection.
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
     * Handles the initial connection protocol with the server.
     *
     * @throws ServerUnavailableException If there is an issue with the server connection.
     * @throws ProtocolException If there is an issue with the communication protocol.
     */
    public void handleHello() throws ServerUnavailableException, ProtocolException{
        String computerName = "Computer_ÆÑŤØÑ";
        if(counter == 0)
            sendMessage(Client.BasicCommands.CONNECT+Protocol.ARGUMENT_SEPARATOR+ computerName);
        else sendMessage(Client.BasicCommands.CONNECT+Protocol.ARGUMENT_SEPARATOR+ computerName+counter);
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
                handleServerInput(message);
                message = in.readLine();
            }
        }catch (Exception e){
            System.out.println("Exception " + e);
        }
    }


    /**
     * Handles input received from the server.
     *
     * @param input The input received from the server.
     * @throws ExitProgram If the user chooses to exit the program.
     * @throws ServerUnavailableException If there is an issue with the server connection.
     */
    void handleServerInput(String input) throws ExitProgram, ServerUnavailableException{
        String lastMove;
        String command, parm=null, parm2=null;
        String[] split;
        split=input.split(Protocol.ARGUMENT_SEPARATOR);
        command=split[0];
        if(split.length>=2) parm=split[1];
        switch(command){
        case "BROADCAST_MOVE":
            if(split.length>=3) parm2=split[2];
            lastMove=parm2;
            break;
        case "ASK_FOR_YESORNO":
            sendMessage(Client.BasicCommands.RESPOND_YESORNO+Protocol.ARGUMENT_SEPARATOR+"no");
            break;
        case "ASK_FOR_INDEX":
            sendMessage(Client.BasicCommands.RESPOND_INDEX+Protocol.ARGUMENT_SEPARATOR+parm);
            break;
        case "ASK_FOR_PLAYERNAME":
            if(split.length>=3) parm2=split[2];
            String[] splitParm=split[2].split(Protocol.ELEMENT_SEPARATOR);
            if(splitParm[0]!=null)
                sendMessage(Client.BasicCommands.RESPOND_PLAYERNAME+Protocol.ARGUMENT_SEPARATOR+splitParm[0]);
            else {
                for (String element : splitParm)
                    if(element!=null){
                        sendMessage(Client.BasicCommands.RESPOND_PLAYERNAME+Protocol.ARGUMENT_SEPARATOR+element);
                        break;
                    }
            }
            break;
        case "ASK_FOR_CARDNAME":
//            boolean ok = false;
//            for (String element : hand) {
//                if(element!=null&&(element.contains("taco_cat")||element.contains("rainbow_ralphing")||element.contains("beard_cat")||element.contains("cattermelon")||element.contains("hairy_potato"))){
//                    sendMessage(Client.BasicCommands.RESPOND_CARDNAME+Protocol.ARGUMENT_SEPARATOR+element);
//                    ok=true;
//                    break;
//                } else if(element!=null&&!(element.contains("defuse"))){
//                    sendMessage(Client.BasicCommands.RESPOND_CARDNAME+Protocol.ARGUMENT_SEPARATOR+element);
//                    ok=true;
//                    break;
//                }
//            }
            //if(!ok)
            sendMessage(Client.BasicCommands.RESPOND_CARDNAME+Protocol.ARGUMENT_SEPARATOR+hand[0]);
            break;
        case "SHOW_HAND":
            if(split.length>=2){
                parm2=split[1];
                hand=parm2.split(Protocol.ELEMENT_SEPARATOR);
            }
            break;
        }
    }
}
