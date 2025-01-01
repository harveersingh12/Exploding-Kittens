package Controller.ClientServer;

import Controller.Game;
import Controller.Players.ComputerPlayer;
import Controller.Players.HumanPlayer;
import Controller.Players.Player;
import Model.Exceptions.*;
import Model.Protocol;
import View.ServerTUI;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;



/**
 * The `Server` class represents the server-side of the Exploding Kittens game.
 * It handles client connections, manages players, and communicates with clients
 * using the M2 Team Exploding Kittens protocol (a PDF file can be found in the
 * project's files.)
 *
 * The server supports basic commands for starting and playing the game, as well
 * as advanced commands for features like chat and multi-game support.
 *
 * The server uses multithreading to handle multiple clients simultaneously.
 *
 * @author Andrei Cristian Dorneanu - s2327478
 * @author Harveer Singh - s3231046
 */
public class Server implements Runnable {
    /**
     * List of all server commands in the game that will be present regardless of whether
     * bonus features are implemented. These commands are used in starting a game, playing
     * it and displaying information to the client.
     */
    public enum BasicCommands implements Protocol.Command {
        HELLO,PLAYER_LIST,QUEUE,NEW_GAME,CURRENT,SHOW_HAND,GAME_OVER,ERROR,BROADCAST_MOVE,PLAYER_OUT,
        ASK_FOR_PLAYERNAME, ASK_FOR_CARDNAME, ASK_FOR_INDEX, ASK_FOR_YESORNO;
    }


    /**
     * List of commands that are required upon implementing the chat feature and/or
     * the multi-game feature. LOBBY_LIST is a display of all open games and their player
     * limit. MESSAGE relays a message from one player to all others.
     * This list can be extended if needed.
     */
    public enum AdvancedCommand implements Protocol.Command {
        LOBBY_LIST,MESSAGE
    }


    private ServerSocket ssock;
    private static boolean gameActive;
    private boolean serverActive;
    public static final String ANSI_RED = "\u001b[31m";
    public static final String ANSI_YELLOW ="\u001b[33m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static List<ClientHandler> clients;
    private int next_client_no;
    private int computerNo;
    private ServerTUI view;
    public static ArrayList<Player> players;


    /**
     * Constructs a new Exploding Kittens server.
     * Initializes necessary data structures.
     *
     * @ensures A new server instance is created with empty client and player lists.
     */
    public Server(){
        clients=new ArrayList<>();
        view=new ServerTUI();
        next_client_no=1;
        computerNo = 0;
        players=new ArrayList<>();
        serverActive = true;
    }


    /**
     * Retrieves the server's view.
     *
     * @return The ServerTUI (Text User Interface) associated with this server.
     * @ensures The view is returned.
     */
    public ServerTUI getView(){
        return view;
    }


    /**
     * Checks if a game is currently active.
     *
     * @return true if a game is active, false otherwise.
     * @ensures The active status of the game is returned.
     */
    public static boolean isGameActive(){
        return gameActive;
    }


    /**
     * Checks if the server is currently active.
     *
     * @return true if a game is active, false otherwise.
     * @ensures The active status of the game is returned.
     */
    public boolean isServerActive(){return serverActive;}


    /**
     * Sets the game's active status.
     *
     * @param gameActive true to set the game as active, false otherwise.
     * @ensures The active status of the game is set.
     */
    public static void setGameActive(boolean gameActive){
        Server.gameActive=gameActive;
    }


    /**
     * Returns the server socket.
     *
     * @return the socket of the server
     */
    public ServerSocket getSsock(){
        return ssock;
    }


    /**
     * Retrieves the names of connected clients.
     *
     * @return A list of client names.
     * @ensures A list of client names is returned.
     */
    public ArrayList<String> getClientNames(){
        ArrayList<String> clientNames = new ArrayList<>();
        for(ClientHandler client : clients)
            clientNames.add(client.getName());
        return clientNames;
    }


    /**
     * Main execution method for the server. Handles client connections,
     * starts new threads for each client, and manages the server socket.
     *
     * @ensures The server is running and handling client connections.
     */
    public void run(){
        boolean openNewSocket=true;
        while(openNewSocket){
            try {
                setup();
                while(true){
                    Socket sock=ssock.accept();
                    String name="Client "+String.format("%02d",next_client_no++);
                    view.update("New client ["+name+"] connected!");
                    ClientHandler handler = new ClientHandler(sock,this,name);
                    view.update(sock.toString());
                    new Thread(handler).start();
                    clients.add(handler);
                }

            } catch (ExitProgram e1) {
                openNewSocket=false;
            } catch (IOException e) {
                System.out.println("A server IO error occurred: "+e.getMessage());

                if(!view.getBoolean("Do you want to open a new socket?")){
                    openNewSocket=false;
                }
            }
        }
        view.update("See you later!");
    }


    /**
     * Sets up a new server socket for the Exploding Kittens game.
     * Allows the user to input the port number for the server.
     *
     * @throws ExitProgram If the user decides to exit the program.
     * @throws IOException If an IO error occurs during socket setup.
     * @requires The system allows socket creation.
     * @ensures A serverSocket is opened for client connections.
     */
    public void setup() throws ExitProgram{

        ssock=null;
        while(ssock==null){
            String address = "127.0.0.1";
            int port = 8811;

            //Uncomment the lines below and the comment the lines above to test user input values
            //view.update("Please enter the server port.");
            //int port=view.keyboardIntInput();
            //String address = view.getIp();

            try {
                view.update("Attempting to open a socket at 127.0.0.1 "+"on port "+port+"...");
                ssock=new ServerSocket(port,0,InetAddress.getByName(address));
                view.update("Server started at port "+port);
            } catch (IOException e) {
                view.update("ERROR: could not create a socket on "+"127.0.0.1"+" and port "+port+".");
                if(!view.getBoolean("Do you want to try again?")){
                    throw new ExitProgram("User indicated to exit the "+"program.");
                }
            }
        }
    }


    /**
     * Removes a client handler from the client list.
     *
     * @param client The client handler to be removed.
     * @requires client != null
     * @ensures The specified client handler is removed from the client list.
     */
    public void removeClient(ClientHandler client){
        this.clients.remove(client);
    }


    /**
     * Retrieves the hello message for a client along with game flags.
     *
     * @param name  The name of the client.
     * @param flags An array of game flags.
     * @return The hello message for the client.
     * @ensures The hello message for the client is returned.
     */
    public String getHello(String name,String[] flags){
        if(!name.contains("Computer")){
            if(Game.flags.isEmpty()){
                for (String element : flags) {
                    int value=Integer.parseInt(element);
                    if(value>=0&&value<=6) Game.flags.add(value);
                }
            }
            return BasicCommands.HELLO+Protocol.ARGUMENT_SEPARATOR+name+Protocol.ARGUMENT_SEPARATOR+Game.flags;
        }else{
            return "WELCOME COMPUTER PLAYER";
        }
    }


    /**
     * Adds a computer player to the game.
     *
     * @return A message indicating success or an error message.
     * @ensures A computer player is added to the game.
     */
    public String addComputer(){
        System.out.println(Game.flags);
        if(!Game.flags.contains(Game.PARTY_FLAG) && !Game.flags.contains(Game.COMBO_FLAG) &&
                !Game.flags.contains(Game.EXTENSION_FLAG)){
            ComputerClient computerClient=new ComputerClient(computerNo);
            try {
                computerClient.createConnection();
                computerClient.handleHello();
            } catch (Exception e) {
                view.update("Error: "+e);
            }
            Thread thread=new Thread(computerClient);
            thread.start();

            //This for loop ensures that all the other processes that are needed finish executing
            // before the return statement is reached. (for an accurate result)
            for (int i=0; i<100000; i++) {
            }
            computerNo++;
            return BasicCommands.PLAYER_LIST+Protocol.ARGUMENT_SEPARATOR+getClientNames()+" "+BasicCommands.QUEUE+Protocol.ARGUMENT_SEPARATOR+clients.size();
        }else return "You cannot play with a computer player if flags 1, 4 or 5 are active.";
    }


    /**
     * Removes a computer player from the game.
     *
     * @return A message indicating success or an error message.
     * @ensures A computer player is removed from the game.
     */
    public String removeComputer(){
        for(ClientHandler client : clients)
            if(client.getName().contains("Computer")){
                removeClient(client);
                return BasicCommands.PLAYER_LIST+Protocol.ARGUMENT_SEPARATOR+getClientNames()+" "+BasicCommands.QUEUE+Protocol.ARGUMENT_SEPARATOR+clients.size();
            }
        return BasicCommands.ERROR+Protocol.ARGUMENT_SEPARATOR+Protocol.Error.E06.getDescription();
    }


    /**
     * Initiates the game by creating players and starting a game thread.
     *
     * @param numberOfPlayers The number of players in the game.
     * @requires numberOfPlayers > 0
     * @ensures A game is initiated with the specified number of players.
     */
    public void requestGame(int numberOfPlayers){
            for (int i=0; i<numberOfPlayers; i++) {
                if(!clients.get(i).getName().contains("Computer"))
                    players.add(new HumanPlayer(clients.get(i).getName()));
                else players.add(new ComputerPlayer(clients.get(i).getName()));
            }

            int totalComputer=0;
            for (Player player : Server.players)
                if(player instanceof ComputerPlayer) totalComputer++;

            if(totalComputer==2){
                for (Player player : new ArrayList<>(Server.players))
                    if(player instanceof HumanPlayer){
                        players.remove(player);
                    }
            }
            for (ClientHandler client : Server.clients)
                client.setBlocked(true);

            for (Player player : players)
                askPlayer(player,BasicCommands.NEW_GAME + Protocol.ARGUMENT_SEPARATOR + players.toString());

            gameActive=true;
            Game game=new Game(players);
            Thread thread=new Thread(game);
            thread.start();
    }


    /**
     * Asks a player to perform an action by sending a message to the client.
     *
     * @param player  The player to whom the message is sent.
     * @param message The message to be sent to the player.
     * @requires player != null && message != null
     * @ensures The player is asked to perform the specified action.
     */
    public static void askPlayer(Player player, String message){
        for (ClientHandler client : Server.clients) {
            if(client.getName().equals(player.getName())){
                client.send(message);
                break;
            }
        }
    }


    /**
     * Receives input from a player using the client handler.
     *
     * @param player The player from whom input is expected.
     * @return The received input from the player.
     * @requires player != null
     * @ensures The input from the player is received.
     */
    public static String receiveInput(Player player){
        String result =null;
        for (ClientHandler client : clients) {
            if(client.getName().equals(player.getName())){
                client.setBlocked(false);
                System.out.println("Trying to get result");
                result = client.getResponse();
                System.out.println("got response. it was " + result);
                client.setBlocked(true);
                break;
            }
        }
        return result;
    }


    /**
     * Main method to start the Exploding Kittens server.
     *
     * @ensures The server is started and ready to handle client connections.
     */
    public static void main(String[] args) {
        Server server = new Server();
        System.out.println("Welcome! EXPLODING BRAINZ starting...");
        new Thread(server).start();
    }
}
