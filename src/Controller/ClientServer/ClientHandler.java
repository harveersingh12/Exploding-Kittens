package Controller.ClientServer;

import Controller.Game;
import Model.Protocol;
import Controller.Players.ComputerPlayer;
import Controller.Players.Player;
import Model.Exceptions.E02;
import Model.Exceptions.E05;
import Model.Exceptions.E09;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


/**
 * The `ClientHandler` class represents a handler for client communication on the server side.
 * It manages the input and output streams for communication with a connected client.
 *
 * The class implements the `Runnable` interface for multithreaded communication.
 *
 * @requires The `sock` parameter in the constructor must be a valid and open socket.
 * @requires The `srv` parameter in the constructor must be a valid `Server` instance.
 * @requires The `name` parameter in the constructor must be a non-null string representing the client's name.
 *
 * @author Andrei Cristian Dorneanu - s2327478
 * @author Harveer Singh - s3231046
 */
public class ClientHandler implements Runnable{
    private BufferedReader in;
    private BufferedWriter out;
    private Socket sock;
    private Server srv;
    private boolean blocked;
    private String name;
    private String response;


    /**
     * Constructs a new ClientHandler. Opens the In- and OutputStreams.
     *
     * @param sock The client socket
     * @param srv  The connected server
     * @param name The name of this ClientHandler
     */
    public ClientHandler(Socket sock, Server srv, String name) {
        try {
            in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
            out = new BufferedWriter(
                    new OutputStreamWriter(sock.getOutputStream()));
            this.sock = sock;
            this.srv = srv;
            this.name = name;
        } catch (IOException e) {
            shutdown();
        }
    }


    /**
     * Checks whether the client is blocked for input processing.
     *
     * @return `true` if the client is blocked, `false` otherwise.
     * @ensures The method returns the current blocked status of the client.
     */
    public boolean isBlocked(){
        return blocked;
    }


    /**
     * Sets the blocked status of the client.
     *
     * @param blocked The new blocked status.
     * @ensures The blocked status of the client is set to the specified value.
     */
    public void setBlocked(boolean blocked){
        this.blocked=blocked;
    }


    /**
     * Gets the name of this client handler.
     *
     * @return The name of this client handler.
     * @ensures The method returns the current name of the client handler.
     */
    public String getName(){
        return name;
    }


    /**
     * Continuously listens to client input and forwards the input to the
     * {@link #handleCommand(String)} method.
     *
     * @invariants The method continuously listens to client input, forwarding it to the command handler.
     * @ensures If an IOException occurs, the `shutdown` method is called to gracefully terminate the client handler.
     */
    public void run() {
        String msg;
        try {
            //msg = in.readLine();
            while ((msg = in.readLine()) != null) {
                System.out.println("> [" + name + "] Incoming: " + msg);
                response = null;
                handleCommand(msg);
                out.newLine();
                out.flush();
                //msg = in.readLine();
            }
            shutdown();
        } catch (IOException e) {
            shutdown();
        }
    }


    /**
     * Handles commands received from the client by calling the according
     * methods at the `Server`.
     *
     * If the received input is not valid, send an "Unknown Command"
     * message to the server.
     *
     * @param input command from client
     * @throws IOException if an IO error occurs during command handling.
     *
     * @requires The input parameter must be a non-null string representing a valid client command.
     * @ensures The method appropriately processes the client command, and the server responds accordingly.
     */
    private void handleCommand(String input) throws IOException{
        String command, parm = null, parm2;
        String[] split;
        split=input.split(Protocol.ARGUMENT_SEPARATOR);
        command=split[0];
        if(split.length >= 2)
            parm=split[1];
        switch(command){
        case "CONNECT":
                try {
                    ArrayList<String> names=srv.getClientNames();
                    for (String name : names)
                        if(name.equals(parm)) throw new E02(Protocol.Error.E02.getDescription());
                } catch (E02 e) {
                    send(e.getMessage());
                    break;
                }
                if(split.length==2){
                    out.write((srv.getHello(parm,"-1".split(" "))));
                    if(parm!=null) this.name=parm;
                } else if(split.length==3){
                    try {
                        String[] flagInput=split[2].split(",");
                        ArrayList<Integer> inputedFlags=new ArrayList<>();
                        for (String s : flagInput) {
                            int value;
                            try {
                                value=Integer.valueOf(s);
                            } catch (Exception e) {
                                throw new E09("Please only enter Integer values separated by (,) for your flags");
                            }
                            if(value<0||value>6){
                                throw new E09("Please only enter Integer values separated by (,) for your flags. Values < 6 and >= 0");
                            }
                            inputedFlags.add(value);
                        }

//                        if(!(inputedFlags.containsAll(Game.flags)&&Game.flags.containsAll(inputedFlags)))
//                            throw new E09(Protocol.Error.E09.getDescription() +
//                                    " You need to connect with same flags as the first person that connected to the server. The fags are: " +
//                                    Game.flags);
                    } catch (E09 e09) {
                        send(e09.getMessage());
                        break;
                    }
                    out.write(srv.getHello(parm,split[2].split(",")));
                }
                if(parm!=null) this.name=parm;
            break;
        case "ADD_COMPUTER":
            if(!isBlocked() && !Server.isGameActive()) send(srv.addComputer());
            else send("You cannot send input to the server right now.");
            break;
        case "REMOVE_COMPUTER":
            if(!isBlocked() && !Server.isGameActive()) send(srv.removeComputer());
            else send("You cannot send input to the server right now.");
            break;
        case "REQUEST_GAME":
            if(!isBlocked() && !Server.isGameActive()){
                int value=Integer.parseInt(parm);
                try {
                    if(!Game.flags.contains(Game.PARTY_FLAG)){
                        if(value>5)
                            throw new E05("In the base version of the game the maximum amount of players is 5!");
                    } else {
                        if(value>10) throw new E05("The maximum number of players in the PARTY_PACK is 10");
                    }
                    if(value>Server.clients.size()){
                        throw new E05("You entered a number grater than the clients connect on the server. Max number: "+Server.clients.size());
                    }
                    srv.requestGame(Integer.parseInt(parm));
                } catch (E05 e) {
                    send(e.getMessage());
                }
            }else send("You cannot send input to the server right now.");
            break;
        case "PLAY_CARD":
            if(!isBlocked()){
                response=parm;
                break;
            }else send("You cannot send input to the server right now.");
            break;
        case "DRAW_CARD":
            if(!isBlocked()){
                response="DRAW_CARD";
                break;
            }else send("You cannot send input to the server right now.");
            break;
        case "RESPOND_PLAYERNAME":
            if(!isBlocked()){
                response=parm;
                break;
            }else send("You cannot send input to the server right now.");
            break;
        case "RESPOND_CARDNAME":
            if(!isBlocked()){
                response=parm;
                break;
            }else send("You cannot send input to the server right now.");
            break;
        case "RESPOND_INDEX":
            if(!isBlocked()){
                response=parm;
                break;
            }else send("You cannot send input to the server right now.");
            break;
        case "RESPOND_YESORNO":
            if(!isBlocked()){
                for (Player player : Server.players)
                    if(player instanceof ComputerPlayer) Server.askPlayer(player,command+parm);
                response=parm;
                break;
            }else send("You cannot send input to the server right now.");
            break;
        case "SEND":
            if(Game.flags.contains(Game.CHAT_FLAG)){
                if(parm != null && !parm.equals("null")){
                    for (ClientHandler client : Server.clients) {
                        if(!client.getName().equals(name))
                            client.send(Server.AdvancedCommand.MESSAGE+Protocol.ARGUMENT_SEPARATOR+name+Protocol.ARGUMENT_SEPARATOR+parm);
                        else send("Message sent.");
                    }
                } else send("This message is not allowed on this server!");
            }else send("Chat is not enabled on this server.");
            break;
            default:
                out.write("Unknown command");
        }
    }


    /**
     * Gets the response from the client, blocking other clients input
     * until a response is received.
     *
     * @return The response from the client.
     * @ensures The method returns the client's response, blocking until a response is received.
     */
    public String getResponse(){
        response=null;
        while(response==null){
            srv.getView().update("Waiting for player input.");
        }
        return response;
    }


    /**
     * Sends the message received as param to the client.
     *
     * @param message The message to be sent.
     * @requires The `message` parameter must be a non-null string.
     * @ensures The message is sent to the client via the output stream.
     */
    public void send(String message){
        try {
            out.write(message);
            out.newLine();
            out.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    /**
     * Shuts down the client handler, closing input and output streams and removing the client from the server.
     *
     * @ensures The client handler is gracefully terminated, and associated resources are closed.
     */
    public void shutdown() {
        System.out.println("> [" + name + "] Shutting down.");
        try {
            for(Player player : Server.players){
                if(player.getName().equals(name)){
                    Server.players.remove(player);
                    break;
                }
            }

            if(Server.isGameActive()){
                for (Player player : Game.players) {
                    if(player.getName().equals(name)){
                        Game.players.remove(player);
                        break;
                    }
                }
                Game.setCurrent(0);
            }

            in.close();
            out.close();
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        srv.removeClient(this);
    }
}
