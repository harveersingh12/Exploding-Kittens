package View;

import Controller.ClientServer.Client;
import Model.Exceptions.E05;
import Model.Protocol;
import Controller.ClientServer.Server;
import Model.Exceptions.ExitProgram;
import Model.Exceptions.ServerUnavailableException;

import java.net.InetAddress;
import java.util.Scanner;


/**
 * The ClientTUI class represents the Textual User Interface for the client-side of the game.
 * It handles interactions between the player and the game client, providing a console-based interface.
 * The class includes methods for displaying messages, and receiving user input.
 *
 * @author Andrei Cristian Dorneanu - s2327478
 * @author Harveer Singh - s3231046
 */
public class ClientTUI {
    Client client;


    /**
     * Constructs a new ClientTUI with a specified client.
     *
     * @param client The associated client.
     */
    public ClientTUI(Client client){
        this.client=client;
    }


    /**
     * Prints a message to the console.
     *
     * @param message The message to be printed.
     */
    public void update(String message){
        System.out.println(message);
    }


    /**
     * Displays a welcome message to the player (ASCII art).
     */
    public void welcomeThePlayer(){
        update("\n"+" _______ ___   ___ .______    __        ______    _______   __  .__   __.   _______     __  ___  __  .___________..___________. _______ .__   __.      _______.\n"
                +"|   ____|\\  \\ /  / |   _  \\  |  |      /  __  \\  |       \\ |  | |  \\ |  |  /  _____|   |  |/  / |  | |           ||           ||   ____||  \\ |  |     /       |\n"
                +"|  |__    \\  V  /  |  |_)  | |  |     |  |  |  | |  .--.  ||  | |   \\|  | |  |  __     |  '  /  |  | `---|  |----``---|  |----`|  |__   |   \\|  |    |   (----`\n"
                +"|   __|    >   <   |   ___/  |  |     |  |  |  | |  |  |  ||  | |  . `  | |  | |_ |    |    <   |  |     |  |         |  |     |   __|  |  . `  |     \\   \\    \n"
                +"|  |____  /  .  \\  |  |      |  `----.|  `--'  | |  '--'  ||  | |  |\\   | |  |__| |    |  .  \\  |  |     |  |         |  |     |  |____ |  |\\   | .----)   |   \n"
                +"|_______|/__/ \\__\\ | _|      |_______| \\______/  |_______/ |__| |__| \\__|  \\______|    |__|\\__\\ |__|     |__|         |__|     |_______||__| \\__| |_______/    \n"+
                "                                                                                                                                                               ");
        update(Server.ANSI_PURPLE + "----FLAG LIST----\n"+
                "0 - Chat\n"+
                "1 - Teams (Party-Pack)\n"+
                "3 - Lobby -> always active\n"+
                "4 - Combos\n"+
                "5 - Extension (Streaking Kittens)\n"+
                "no 3rd argument - no flags\n"+
                "-----------------\n" + Server.ANSI_RESET );
    }


    /**
     * Starts the TUI by continuously reading user input and handling it.
     *
     * @throws ExitProgram If the user chooses to exit the program.
     * @throws ServerUnavailableException If there is an issue with the server connection.
     */
    public void start(){
        try {
            while(true){
                String userInput=keyboardStringInput();
                handleUserInput(userInput);
            }
        } catch (ExitProgram | ServerUnavailableException e) {
            update("Exiting the program...");
        }
    }


    /**
     * Handles user input by calling the appropriate client methods.
     *
     * @param input The user input command.
     * @throws ExitProgram If the user chooses to exit the program.
     * @throws ServerUnavailableException If there is an issue with the server connection.
     */
    public void handleUserInput(String input) throws ExitProgram, ServerUnavailableException{
            String command, parm=null;
            String[] split;
            split=input.split(Protocol.ARGUMENT_SEPARATOR);
            command=split[0];
            if(split.length>=2) parm=split[1];
            switch(command){
            case "CONNECT":
                try {
                    client.handleHello();
                }catch (Exception e){
                    System.out.println("Error: " + e);
                }
            case "ADD_COMPUTER":
                client.sendMessage(String.valueOf(Client.BasicCommands.ADD_COMPUTER));
                break;
            case "REMOVE_COMPUTER":
                client.sendMessage(String.valueOf(Client.BasicCommands.REMOVE_COMPUTER));
                break;
            case "REQUEST_GAME":
                try {
                    int value = Integer.parseInt(parm);
                    if(value < 2)
                        throw new E05(Protocol.Error.E05.getDescription());
                    client.sendMessage(Client.BasicCommands.REQUEST_GAME+Protocol.ARGUMENT_SEPARATOR+value);
                }catch (NumberFormatException e){
                    update("Second argument should be a Integer value.");
                }catch (E05 e1){
                    update(e1.getMessage());
                }
                break;
            case "PLAY_CARD":
                client.sendMessage(Client.BasicCommands.PLAY_CARD+Protocol.ARGUMENT_SEPARATOR+parm);
                break;
            case "DRAW_CARD":
                client.sendMessage(String.valueOf(Client.BasicCommands.DRAW_CARD));
                break;
            case "RESPOND_PLAYERNAME":
                client.sendMessage(Client.BasicCommands.RESPOND_PLAYERNAME+Protocol.ARGUMENT_SEPARATOR+parm);
                break;
            case "RESPOND_CARDNAME":
                client.sendMessage(Client.BasicCommands.RESPOND_CARDNAME+Protocol.ARGUMENT_SEPARATOR+parm);
                break;
            case "RESPOND_INDEX":
                client.sendMessage(Client.BasicCommands.RESPOND_INDEX+Protocol.ARGUMENT_SEPARATOR+parm);
                break;
            case "RESPOND_YESORNO":
                if(parm!=null)
                    client.sendMessage(Client.BasicCommands.RESPOND_YESORNO+Protocol.ARGUMENT_SEPARATOR+parm.toLowerCase());
                else
                    client.sendMessage(Client.BasicCommands.RESPOND_YESORNO+Protocol.ARGUMENT_SEPARATOR+parm);
                break;
            case "SEND":
                client.sendMessage(Client.AdvancedCommand.SEND + Protocol.ARGUMENT_SEPARATOR + parm);
                break;
            case "HELP":
                printHelpMenu();
                break;
            default:
                update(Server.ANSI_PURPLE+"Unknown Command. You can use the command 'HELP' for a Help Menu"+Server.ANSI_RESET);
            }
    }


    /**
     * Reads a line of text from the console.
     *
     * @return The user input as a String.
     */
    public String keyboardStringInput(){
        Scanner stringScanner=new Scanner(System.in);
        return stringScanner.nextLine();
    }


    /**
     * Reads an integer from the console.
     *
     * @return The user input as an integer.
     * @throws NumberFormatException If the input is not a valid integer.
     */
    public int keyboardIntInput() throws NumberFormatException{
        try {
            Scanner intScanner=new Scanner(System.in);
            int number=intScanner.nextInt();
            intScanner.nextLine();
            return number;
        }catch (Exception e) {
            update("Invalid number. Please enter a valid Integer number.");
        }
        throw new NumberFormatException("Invalid number. Please enter a valid Integer number.");
    }


    /**
     * Gets the IP address from the user.
     *
     * @return The user-entered IP address.
     */
    public InetAddress getIp(){
        while(true){
            try {
                update("Enter a server IP address: ");
                String userInput=keyboardStringInput();
                return InetAddress.getByName(userInput);
            } catch (Exception e) {
                update("Invalid IP address. Please enter a valid IP address.");
            }
        }
    }


    /**
     * Prints a list with all the commands that can be sent to the server.
     */
    public void printHelpMenu(){
        update(Server.ANSI_PURPLE + "---------------------------------------------------------------------------" +
                "HELP MENU---------------------------------------------------------------------------\n"+Server.ANSI_RESET +
                Server.ANSI_YELLOW + "Lobby Commands\n" + Server.ANSI_RESET +
                Server.ANSI_PURPLE + "ADD_COMPUTER" + Server.ANSI_RESET + " -> connects a computer player to the server;\n" +
                Server.ANSI_PURPLE + "REMOVE_COMPUTER" + Server.ANSI_RESET + " -> removes a computer player from the server;\n" +
                Server.ANSI_PURPLE + "REQUEST_GAME~number_of_players" + Server.ANSI_RESET + " -> " +
                "starts a game with the number of players used as second argument.\n\n" +
                Server.ANSI_YELLOW + "In-Game Commands (if you use this while a game is not running, " +
                "they will do nothing and you won't receive any message)\n" + Server.ANSI_RESET +
                Server.ANSI_PURPLE + "PLAY_CARD~one card/multiple cards separated by (,)" + Server.ANSI_RESET +
                " -> used when it is your turn, to play one or more cards from your had;\n" +
                Server.ANSI_PURPLE + "DRAW_CARD" + Server.ANSI_RESET + " -> used when it is your turn to draw a card from the draw pile;\n" +
                Server.ANSI_PURPLE + "RESPOND_PLAYERNAME~name of a player" + Server.ANSI_RESET +
                " -> used when the server asks for a player name via classic text or via the 'ASK_FOR_PLAYERNAME' command;\n"+
                Server.ANSI_PURPLE + "RESPOND_CARDNAME~name of a card" + Server.ANSI_RESET +
                " -> used when the server asks for a card name via classic text or via the 'ASK_FOR_CARDNAME' command;\n" +
                Server.ANSI_PURPLE + "RESPOND_INDEX~index (multiple indexes can be separated by (,))" + Server.ANSI_RESET +
                " -> used when the server asks for a index via classic text or via the 'ASK_FOR_INDEX' command;\n"+
                Server.ANSI_PURPLE + "RESPOND_YESORNO~yes/no" + Server.ANSI_RESET +
                " -> used when the server asks for a yes/no via classic text or via the 'ASK_FORYESORNO' command;\n"+
                Server.ANSI_PURPLE + "SEND~message" + Server.ANSI_RESET +" -> sends the message to all the other clients connected;\n"+
                Server.ANSI_PURPLE + "HELP" + Server.ANSI_RESET+" -> prints this menu.\n"+
                Server.ANSI_PURPLE + "-------------------------------------------------------------------------------------" +
                "--------------------------------------------------------------------------\n" + Server.ANSI_RESET);
    }
}

