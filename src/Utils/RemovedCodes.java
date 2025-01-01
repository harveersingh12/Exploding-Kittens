package Utils;

class RemovedCodes {

    //FROM CLASS CARD

//    private String setName(CardType type){
//        ArrayList<String> names = createName(type);
//        int index;
//        do {
//            index = (int) (Math.random() * names.size());
//            name = names.get(index);
//        } while (usedNames.contains(name) && !name.contains(type.name().toLowerCase()));
//
//        String localName = names.get(index);
//        usedNames.add(localName);
//        return localName;
//    }
//
//    public HashSet<String> getUsedNames(){
//        return usedNames;
//    }
//
//    /**
//     * @param type is the type of each card in the list of card names
//     * @return the list of all cards of a single type
//     **/
//    public ArrayList<String> createName (CardType type){
//        ArrayList<String> names = new ArrayList<>();
////        switch(type){
//            case NOPE:
//                Collections.addAll(names, "nope1", "nope2", "nope3", "nope4", "nope5");
//                break;
//            case SKIP:
//                Collections.addAll(names, "skip1", "skip2", "skip3", "skip4");
//                break;
//            case FAVOR:
//                Collections.addAll(names, "favor1", "favor2", "favor3", "favor4");
//                break;
//            case SHUFFLE:
//                Collections.addAll(names, "shuffle1", "shuffle2", "shuffle3", "shuffle4");
//                break;
//            case SEE_THE_FUTURE:
//                Collections.addAll(names, "stf1", "stf2", "stf3", "stf4", "stf5");
//                break;
//            case ATTACK:
//                Collections.addAll(names, "attack1", "attack2", "attack3", "attack4");
//                break;
//            case HAIRY_POTATO:
//                Collections.addAll(names, "potato1", "potato2", "potato3", "potato4");
//                break;
//            case TACO_CAT:
//                Collections.addAll(names, "taco1", "taco2", "taco3", "taco4");
//                break;
//            case RAINBOW_RALPHING:
//                Collections.addAll(names, "rr1", "rr2", "rr3", "rr4", "rr5");
//                break;
//            case BEARD_CAT:
//                Collections.addAll(names, "beard1", "beard2", "beard3", "beard4");
//                break;
//            case CATTERMELON:
//                Collections.addAll(names, "melon1", "melon2", "melon3", "melon4");
//                break;
//            case EXPLODING_KITTEN:
//                Collections.addAll(names, "ek1", "ek2", "ek3", "ek4");
//                break;
//            case DEFUSE:
//                Collections.addAll(names, "defuse1", "defuse2", "defuse3", "defuse4", "defuse5", "defuse6");
//                break;
//        }
//        return names;
//}


//    private void listGenerator(int position_amount){
//        for(int i = 0; i< CardType.values().length; i++){
//            for(int j = 0; j<CardType.values()[i].getAmount().get(position_amount); j++){
//                String name = CardType.values()[i].name().toLowerCase() + String.valueOf(j);
//                System.out.println(name);
//                if(usedNames == null || !usedNames.contains(name))
//                    names.add(name);
//            }
//        }
    }


 //   ClientHandler
//        System.out.println(Server.ClientTUIs);
//        for(ClientTUI tui : Server.ClientTUIs) {
//            System.out.println(sock.getPort() +" " + tui.getClient().getServerSock().getLocalPort());
//            if(sock.getPort()==tui.getClient().getServerSock().getLocalPort()) tui.update(argument);
//        }




//SERVER

//whereToShow(players.get(0).getName() + ", you won!");

//    public void initializeHands(Deck deck){
//        for(int i = 0; i<players.size(); i++)
//            players.set(i, new HumanPlayer(players.get(i).getName(), deck));
//    }
//
//    private void updateCurrent(){
//        current=(current+1) % Game.numberOfPlayers;
//    }
//
//    private void setCurrent(int index){
//        current = index;
//    }
//
//    public void playTurn(Player player){
//        nopePlayed=false;
//        dontDraw=false;
//        do {
//            ArrayList<Card> playedCardList=player.playCard();
//            if(playedCardList!=null&&playedCardList.size()==1){
//                singleCard(player,playedCardList);
//                if(!dontDraw){
//                    doDraw(player);
//                    break;
//                }
//            }
//        }while(nopePlayed);
//    }
//
//    private void singleCard(Player player, ArrayList<Card> playedCardList){
//        Card playedCard=playedCardList.get(0);
//        endTurn=false;
//        while(playedCard!=null){
//            if(playedCard.getType()!=CardType.DEFUSE){
//                if(anyPlayerWantsToNope()){
//                    nopePlayed = true;
//                    dontDraw = true;
//                    break;
//                }
//            }
//            switch(playedCard.getType()){
//            case NOPE:
//                askPlayer(player,"You cannot play a NOPE right now");
//                player.getHand().addCard(playedCard);
//                break;
//            case DEFUSE: // can only be played if exploding kitten was drawn, otherwise an error message is displayed. If valid, the player
//                // puts the exploding kitten back in the pile with the position of the card being at their discretion.
//                askPlayer(player,"You cannot play a defuse card at the moment");
//                player.getHand().getCards().add(playedCard);
//                break;
//            case ATTACK: // next player is made to play two turns, current player does not draw
//                //compound for > 2 players (max 4 cards)
//                attackCase(player);
//                break;
//            case SKIP:
//                // current player does not do anything, turn is simply moved on to the next player
//                break;
//            case SHUFFLE: // deck is shuffled
//                deck.shuffle();
//                askPlayer(player,"The draw pile has been shuffled");
//                break;
//            case FAVOR: // current player chooses another player to steal a card from, that player chooses a card to give away
//                favorCase(player);
//                break;
//            case SEE_THE_FUTURE:
//                for (int i=0; i<3; i++) {
//                    askPlayer(player,deck.drawPile.get(i).getName());
//                }
//                break;
//            default:
//                if(playedCard.getType() == CardType.TACO_CAT || playedCard.getType() == CardType.RAINBOW_RALPHING
//                        || playedCard.getType() == CardType.BEARD_CAT || playedCard.getType() == CardType.CATTERMELON
//                        || playedCard.getType() == CardType.HAIRY_POTATO){
//                    askPlayer(player,"This card is powerless on its own.");
//                    player.getHand().addCard(playedCard);
//                }
//                else // message when no card is played
//                    askPlayer(player,"Please play a card in your hand");
//            }
//
//            if(playedCard.getType()==CardType.SKIP){
//                dontDraw=true;
//                break;
//            }
//
//            if(!endTurn){
//                playedCardList=player.playCard();
//                if(playedCardList != null &&  playedCardList.size()==1)
//                    playedCard=playedCardList.get(0);
//                else playedCard= null;
//            } else playedCard=null;
//        }
//    }
//
//    private int askForIndex(String currentPlayerName){
//        for (int i=0; i<playerNames.size(); i++)
//            if(!playerNames.get(i).equals(currentPlayerName)) view.update((i+1)+". " + playerNames.get(i));
//
//        int index=view.keyboardIntInput();
//
//        while(index < 0 || index > playerNames.size()-1||playerNames.get(index).equals(currentPlayerName)){
//            askPlayer(currentPlayer,"Please enter a valid index which is not yours. The players of the game are: ");
//            for (int i=0; i<playerNames.size(); i++)
//                if(!playerNames.get(i).equals(currentPlayerName)) view.update((i+1)+". "+playerNames.get(i));
//            index=view.keyboardIntInput()-1;
//        }
//        return index;
//    }
//
//    public void attackCase(Player player){
//
//        askPlayer(player,"You played an attack card, select the index of the player (who is not you) you want to attack");
//
//        setCurrent(askForIndex(player.getName()));
//        remainingTurns+=2;
//        playTurn(players.get(current));
//        remainingTurns--;
//        playTurn(players.get(current));
//        remainingTurns--;
//        for(int i = 0; i < remainingTurns; i++)
//            playTurn(players.get(current));
//
//        //move to next player (3-turn situation in 2 players game)
//        setCurrent(players.indexOf(player));
//        dontDraw=true;
//        endTurn=true;
//    }
//
//
//    public void favorCase(Player player){
//
//        String currentPlayerName=player.getName();
//
//        askPlayer(player,"You played a favor card, select a player index which is not yours to take a card from");
//
//        int favorPlayerIndex=askForIndex(currentPlayerName);
//
//        Player favorPlayer=null;
//        for (Player checkThisPlayer : players) {
//            if(checkThisPlayer.getName().equals(playerNames.get(favorPlayerIndex))) {
//                favorPlayer=checkThisPlayer;
//                break;
//            }
//        }
//
//        askPlayer(favorPlayer,favorPlayer.getName()+", please select a card to give away. You can choose between the following cards: \n"+favorPlayer.getHand().getNames());
//        String givenCardName=view.keyboardStringInput();
//
//        while(!favorPlayer.getHand().getNames().contains(givenCardName)){
//            view.update("Please insert a valid name.");
//            givenCardName=view.keyboardStringInput();
//        }
//
//        Card givenCard=null;
//        for (Card card : favorPlayer.getHand().getCards())
//            if(card.getName().equals(givenCardName)) {
//                givenCard=card;
//                break;
//            }
//
//        favorPlayer.getHand().removeCard(givenCard);
//        players.get(current).getHand().addCard(givenCard);
//        askPlayer(player,givenCardName+" removed from "+favorPlayer.getName()+"'s hand and given to "+currentPlayerName);
//    }
//
//
//    public boolean anyPlayerWantsToNope() {
//        for (Player player : players) {
//            if(player != players.get(current)){
//                if (player.hasNopeCard()) {
//                    askPlayer(player, player.getName() + ", do you want to play a Nope card to cancel the action? (yes/no)");
//                    String nopeChoice =view.keyboardStringInput().toLowerCase();
//                    boolean moveToFalse = false;
//
//                    if (nopeChoice.equals("yes")) {
//                        player.playNopeCard();
//                        for(int i = 2; true; i++){
//                            if(i % 2 == 0){
//                                if(players.get(current).hasNopeCard()){
//                                    view.update(players.get(current).getName() + ", do you want to play a Nope card to cancel the previous NOPE? (yes/no)");
//                                    nopeChoice =view.keyboardStringInput().toLowerCase();
//                                    if(nopeChoice.equals("yes")){
//                                        players.get(current).playNopeCard();
//                                        view.update(players.get(current).getName() + " NOPED the NOPE.");
//                                    }
//                                    else{
//                                        moveToFalse = true;
//                                        break;
//                                    }
//                                }else {
//                                    moveToFalse = true;
//                                    break;
//                                }
//                            }
//                            else {
//                                if (player.hasNopeCard()) {
//                                    view.update(player.getName() + ", do you want to play ANOTHER Nope card to cancel the action? (yes/no)");
//                                    nopeChoice = view.keyboardStringInput().toLowerCase();
//                                    if(nopeChoice.equals("yes"))
//                                        player.playNopeCard();
//                                    else {
//                                        moveToFalse = true;
//                                        break;
//                                    }
//                                }
//                                else {
//                                    moveToFalse = true;
//                                    break;
//                                }
//                            }
//                        }
//                        if(moveToFalse == false){
//                            askPlayer(player,"Action canceled by playing a Nope card. ("+player.getName()+")");
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//        return false;
//    }
//
//    public void doDraw(Player player){
//        Card drawnCard=player.drawCard(deck);
//        checkEK(drawnCard,player);
//    }
//
//
//    private void checkEK(Card card, Player player){
//        if(card.getType() == CardType.EXPLODING_KITTEN){
//            view.update("You drew an exploding kitten. Play a defuse card if you have. (type 'no' to get exploded)");
//            defuseEK(card, player);
//        }
//    }
//
//    private void defuseEK(Card card, Player player){
//        Card isDefuse = player.playSingleCard();
//        while(isDefuse != null){
//            if(isDefuse.getType()==CardType.DEFUSE){
//                view.update("You defused an exploding kitten");
//                view.update("Please select a location in the draw pile to re-insert the exploding kitten");
//                view.update("The size of the draw pile is: " + deck.drawPile.size());
//                int ekIndex =view.keyboardIntInput()-1;
//                while(ekIndex < 0 || ekIndex > deck.drawPile.size() - 1){
//                    view.update("Please insert a valid location. It should be between " + 1  + " and " + deck.drawPile.size());
//                    ekIndex =view.keyboardIntInput();
//                    //sometimes the last location doesn't work
//                }
//                deck.drawPile.add(ekIndex, card);
//                player.getHand().getCards().remove(card);
//                return;
//            } else {
//                view.update("You can only play a defuse card here");
//                isDefuse=player.playSingleCard();
//            }
//        }
//        players.remove(player);
//    }



//HUMAN PLAYER

//@Override
//public ArrayList<Card> playTestCard(Scanner scanner) {
//    ArrayList<Card> result = new ArrayList<>();
//    String input = scanner.nextLine();
//    String[] splitInput = input.split(",");
//    boolean validCombination = true;
//
//    if (input.equals("no")) {
//        return null;
//    }
//    for (String param : splitInput) {
//        Card card = findCardByName(param);
//
//        if (card != null) {
//            if (result.isEmpty() || card.getType() == result.get(0).getType()) {
//                result.add(card);
//            } else {
//                validCombination = false;
//                break;
//            }
//        } else {
//            validCombination = false;
//            break;
//        }
//    }
//
//    if (validCombination && result.size() == splitInput.length) {
//        for (Card card : result) {
//            hand.getCards().remove(card);
//        }
//    }
//    return result;
//}



//GAME -> STREAKING_KITTENS EK

//Card isDefuse = player.playSingleCard();
//                        while(isDefuse != null){
//        if(isDefuse.getType()==CardType.DEFUSE){
//whereToShow("You defused an exploding kitten");
//whereToShow("Please select a location in the draw pile to re-insert the exploding kitten");
//whereToShow("The size of the draw pile is: " + deck.drawPile.size());
//int ekIndex =view.keyboardIntInput()-1;
//                                while(ekIndex < 0 || ekIndex > deck.drawPile.size() - 1){
//whereToShow("Please insert a valid location. It should be between " + 1  + " and " + deck.drawPile.size());
//ekIndex =view.keyboardIntInput();
//                                }
//                                        deck.drawPile.add(ekIndex, playedCard);
//                                player.getHand().getCards().remove(playedCard);
//                                return;
//                                        } else {
//whereToShow("You can only play a defuse card here");
//isDefuse=player.playSingleCard();
//                            }
//                                    }
//                                    players.remove(player);
//                        break;





//REMAKE CLIENT


//package ClientServerTest;
//
//import Model.Exceptions.ExitProgram;
//import Model.Exceptions.ProtocolException;
//import Model.Exceptions.ServerUnavailableException;
//
//import java.io.*;
//import java.net.InetAddress;
//import java.net.Socket;
//
//
//    public class remake_client {
//        /**
//         * List of all fundamental (non-bonus) commands on the client side. These commands
//         * are required for playing the game and starting it.
//         */
//
//        public enum BasicCommands implements Protocol.Command {
//            CONNECT,ADD_COMPUTER,REMOVE_COMPUTER,REQUEST_GAME,PLAY_CARD,DRAW_CARD
//        }
//
//        /**
//         * List of commands for bonus features. Currently, there is only the SEND command,
//         * meant for one player to correspond with others (chat feature). This list, as with
//         * the server commands can be extended.
//         */
//        public enum AdvancedCommand implements Protocol.Command {
//            SEND
//        }
//
//        private Socket serverSock;
//        private BufferedReader in;
//        private BufferedWriter out;
//        private ClientTUI view;
//        private boolean readingFromServer;
//
//        public remake_client(){
//            view=new ClientTUI(this);
//        }
//
//        public Socket getServerSock(){
//            return serverSock;
//        }
//
//        public void start(){
//            try {
//                createConnection();
//                handleHello();
//                readingFromServerThread();
//                view.start();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        /**
//         * Creates a connection to the server. Requests the IP and port to
//         * connect to at the view (TUI).
//         * <p>
//         * The method continues to ask for an IP and port and attempts to connect
//         * until a connection is established or until the user indicates to exit
//         * the program.
//         *
//         * @throws ExitProgram if a connection is not established and the user
//         *                     indicates to want to exit the program.
//         * @ensures serverSock contains a valid socket connection to a server
//         */
//        public void createConnection() throws ExitProgram{
//            clearConnection();
//            while(serverSock==null){
//                String host="127.0.0.1";
//                //String host = String.valueOf(view.getIp());
//                view.update("Enter a port on which you would like to connect.");
//                int port=view.keyboardIntInput();
//
//                // try to open a Socket to the server
//                try {
//                    InetAddress addr=InetAddress.getByName(host);
//                    view.update("Attempting to connect to "+addr+":"+port+"...");
//                    serverSock=new Socket(addr,port);
//                    in=new BufferedReader(new InputStreamReader(serverSock.getInputStream()));
//                    out=new BufferedWriter(new OutputStreamWriter(serverSock.getOutputStream()));
//                } catch (IOException e) {
//                    view.update("ERROR: could not create a socket on "+host+" and port "+port+".");
//
//                    //Do you want to try again? (ask user, to be implemented)
//                    if(false){
//                        throw new ExitProgram("User indicated to exit.");
//                    }
//                }
//            }
//        }
//
//        /**
//         * Resets the serverSocket and In- and OutputStreams to null.
//         * <p>
//         * Always make sure to close current connections via shutdown()
//         * before calling this method!
//         */
//        public void clearConnection(){
//            serverSock=null;
//            in=null;
//            out=null;
//        }
//
//        /**
//         * Sends a message to the connected server, followed by a new line.
//         * The stream is then flushed.
//         *
//         * @param msg the message to write to the OutputStream.
//         * @throws ServerUnavailableException if IO errors occur.
//         */
//        public void sendMessage(String msg) throws ServerUnavailableException{
//            if(out!=null){
//                try {
//                    out.write(msg);
//                    out.newLine();
//                    out.flush();
//                } catch (IOException e) {
//                    System.out.println(e.getMessage());
//                    throw new ServerUnavailableException("Could not write to server.");
//                }
//            } else {
//                throw new ServerUnavailableException("Could not write to server.");
//            }
//        }
//
//        private void readingFromServerThread(){
//            if(!readingFromServer){
//                readingFromServer = true;
//                Thread serverReaderThread = new Thread(this::readFromServerLoop);
//                serverReaderThread.start();
//            }
//        }
//
//        private void readFromServerLoop(){
//            try {
//                while(readingFromServer){
//                    String line=readLineFromServer();
//                    if(line != null){
//                        //System.out.println("got in if " + i + "'th time");
//                        view.update(line);
//                    }
//                }
//            }catch (ServerUnavailableException  e){
//                e.printStackTrace();
//            }
//        }
//
//        private void handleServerMessage(String message){
//            view.update(message);
//        }
//
//        /**
//         * Reads and returns one line from the server.
//         *
//         * @return the line sent by the server.
//         * @throws ServerUnavailableException if IO errors occur.
//         */
//        public String readLineFromServer() throws ServerUnavailableException{
//            if(in!=null){
//                try {
//                    // Read and return answer from Server
//                    String answer=in.readLine();
//                    if(answer==null){
//                        throw new ServerUnavailableException("Could not read from server.");
//                    }
//                    return answer;
//                } catch (IOException e) {
//                    throw new ServerUnavailableException("Could not read from server.");
//                }
//            } else {
//                throw new ServerUnavailableException("Could not read from server.");
//            }
//        }
//
//        public static void receiveMessage(String argument){
//            // view.update(argument);
//        }
//
//        /**
//         * Reads and returns multiple lines from the server until the end of
//         * the text is indicated using a line containing ProtocolMessages.EOT.
//         *
//         * @return the concatenated lines sent by the server.
//         * @throws ServerUnavailableException if IO errors occur.
//         */
//        public String readMultipleLinesFromServer() throws ServerUnavailableException{
//            if(in!=null){
//                try {
//                    // Read and return answer from Server
//                    StringBuilder sb = new StringBuilder();
//                    for (String line=in.readLine(); line!=null; line=in.readLine()) {
//                        sb.append(line+System.lineSeparator());
//                    }
//                    return sb.toString();
//                } catch (IOException e) {
//                    throw new ServerUnavailableException("Could not read from server.");
//                }
//            } else {
//                throw new ServerUnavailableException("Could not read from server.");
//            }
//        }
//
//        /**
//         * Closes the connection by closing the In- and OutputStreams, as
//         * well as the serverSocket.
//         */
//        public void closeConnection(){
//            System.out.println("Closing the connection...");
//            try {
//                in.close();
//                out.close();
//                serverSock.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        public void handleHello() throws ServerUnavailableException, ProtocolException{
//            try {
//                view.update("Connect using the protocol message.");
//                String input = view.keyboardStringInput();
//                String[] splitByDelimiter = input.split("~");
//                if(splitByDelimiter.length == 2)
//                    sendMessage(Controller.ClientServerTest.Client.BasicCommands.CONNECT+Protocol.ARGUMENT_SEPARATOR+splitByDelimiter[1]);
//                else if(splitByDelimiter.length == 3) {
//                    if(view.checkFlags(splitByDelimiter[2].split(","))){
//                        sendMessage(Controller.ClientServerTest.Client.BasicCommands.CONNECT+Protocol.ARGUMENT_SEPARATOR+splitByDelimiter[1]+Protocol.ARGUMENT_SEPARATOR+splitByDelimiter[2]);
//                    } else sendMessage(Controller.ClientServerTest.Client.BasicCommands.CONNECT+Protocol.ARGUMENT_SEPARATOR);
//                }else sendMessage(Controller.ClientServerTest.Client.BasicCommands.CONNECT+Protocol.ARGUMENT_SEPARATOR);
////            if(!message.contains(String.valueOf(Server.BasicCommands.HELLO))&&!message.contains("U-ParkHotel")){
////                throw new ProtocolException("Hello did not go good");
////            } else {
//                //view.update(readLineFromServer());
//                //}
//            } catch (Exception e) {
//                System.out.println("Error: "+e);
//            }
//        }
//
//        public void addComputer() throws ServerUnavailableException {
//            sendMessage(String.valueOf(Controller.ClientServerTest.Client.BasicCommands.ADD_COMPUTER));
//            if(readLineFromServer()!=null){
//                //view.update(readLineFromServer());
//            }else throw new ServerUnavailableException("Could not add computer");
//        }
//
//        public void removeComputer() throws ServerUnavailableException{
//            sendMessage(String.valueOf(Controller.ClientServerTest.Client.BasicCommands.REMOVE_COMPUTER));
//            if(readLineFromServer()!=null){
//                //view.update(readLineFromServer());
//            }else throw new ServerUnavailableException("Could not remove computer");
//        }
//
//        public void requestGame(int numberOfPlayers) throws ServerUnavailableException {
//            sendMessage(Controller.ClientServerTest.Client.BasicCommands.REQUEST_GAME + Protocol.ARGUMENT_SEPARATOR + numberOfPlayers);
//            if(readLineFromServer()!=null){
//                //view.update(readLineFromServer());
//            }else throw new ServerUnavailableException("Could not request game");
//        }
//
//        public void playCard(String card) throws ServerUnavailableException{
//            sendMessage(Controller.ClientServerTest.Client.BasicCommands.PLAY_CARD + Protocol.ARGUMENT_SEPARATOR + card);
//            if(readLineFromServer()!=null){
//                //view.update(readLineFromServer());
//            }else throw new ServerUnavailableException("Could not play card");
//        }
//
//        public void drawCard() throws ServerUnavailableException{
//            sendMessage(String.valueOf(Controller.ClientServerTest.Client.BasicCommands.DRAW_CARD));
//            if(readLineFromServer()!=null){
//                view.update(readLineFromServer());
//            }else throw new ServerUnavailableException("Could not draw card");
//        }
//
//        public static void main(String[] args) {
//            (new Controller.ClientServerTest.Client()).start();
//        }
//    }
//
//}




//EXPLODING KITTENS

//package Utils;
//
//import Controller.Game;
//import Controller.Players.*;
//        import View.GameTUI;
//import View.UserInterface;
//
//import java.util.*;
//
///**
// * Class for playing the game
// * @invariant input for number of players must be an int
// * @invariant player names are unique
// * @invariant number of players in the game is at least 2
// */
//public class ExplodingKittensClass {
//    public static final String ANSI_PURPLE = "\u001B[35m";
//    public static final String ANSI_RESET = "\u001B[0m";
////    public static final int PARTY_FLAG = 1;
////    public static final int COMBO_FLAG = 4;
////    public static final int EXTENSION_FLAG = 5;
////    public static ArrayList<Integer> flags = new ArrayList<>();
//
//
//    public static void main(String[] args) {
//        ArrayList<Player> players = new ArrayList<>();
//
//        Set<String> playerNames = new HashSet<>();
//        int playerNumber;
//        UserInterface view = new GameTUI();
//
//
//        view.update("\n"+" _______ ___   ___ .______    __        ______    _______   __  .__   __.   _______     __  ___  __  .___________..___________. _______ .__   __.      _______.\n"
//                +"|   ____|\\  \\ /  / |   _  \\  |  |      /  __  \\  |       \\ |  | |  \\ |  |  /  _____|   |  |/  / |  | |           ||           ||   ____||  \\ |  |     /       |\n"
//                +"|  |__    \\  V  /  |  |_)  | |  |     |  |  |  | |  .--.  ||  | |   \\|  | |  |  __     |  '  /  |  | `---|  |----``---|  |----`|  |__   |   \\|  |    |   (----`\n"
//                +"|   __|    >   <   |   ___/  |  |     |  |  |  | |  |  |  ||  | |  . `  | |  | |_ |    |    <   |  |     |  |         |  |     |   __|  |  . `  |     \\   \\    \n"
//                +"|  |____  /  .  \\  |  |      |  `----.|  `--'  | |  '--'  ||  | |  |\\   | |  |__| |    |  .  \\  |  |     |  |         |  |     |  |____ |  |\\   | .----)   |   \n"
//                +"|_______|/__/ \\__\\ | _|      |_______| \\______/  |_______/ |__| |__| \\__|  \\______|    |__|\\__\\ |__|     |__|         |__|     |_______||__| \\__| |_______/    \n"+
//                "                                                                                                                                                               \n");
//        view.update(ANSI_PURPLE + "🎮 Starting game configuration... 🕹" + ANSI_RESET);
//        view.update(ANSI_PURPLE + "\nSTEP 1: " + ANSI_RESET + "What flags (optional features) would you like the game to have? Separate multiple flags with (,).");
//        view.update("----FLAG LIST----\n"+
//                "0 - Chat\n"+
//                "1 - Teams\n"+
//                "2 - Multi-Games\n"+
//                "3 - Lobby\n"+
//                "4 - Combos\n"+
//                "5 - Extension\n"+
//                "6 - GUI\n"+
//                "no - no flags\n"+
//                "-----------------");
//        int ok = 1;
//        do {
//            view.updateSameLine(ANSI_PURPLE + "INPUT HERE: " + ANSI_RESET);
//            String[] flagInput = view.keyboardStringInput().split(",");
//            if(flagInput[0].equals("no") && flagInput.length==1)
//                break;
//            for (int i=0; i<flagInput.length; i++) {
//                int value = -1;
//                try{
//                    value=Integer.valueOf(flagInput[i]);
//                }catch (NumberFormatException e){
//                    view.showError("Please only enter numbers separated by (,) or (no)");
//                }
//                if(value<0||value>6){
//                    view.showError("Invalid flag input. Please try again.");
//                    ok = 0;
//                } else {
//                    Game.flags.add(value);
//                    ok = 1;
//                }
//            }
//        }while(ok == 0);
//
//        view.update(ANSI_PURPLE + "\nSelected flags: " + Game.flags + ANSI_RESET);
//
//        view.update(ANSI_PURPLE + "\nSTEP 2: " + ANSI_RESET +"How many players?");
//        do {
//            try {
//                view.updateSameLine(ANSI_PURPLE + "INPUT HERE: " + ANSI_RESET);
//                playerNumber = view.keyboardIntInput();
//
//                if (playerNumber < 2) {
//                    view.update("The game can only be played with 2 or more players");
//                } else{
//                    for (int i = 0; i < playerNumber; i++) {
//                        view.update( ANSI_PURPLE+"2."+(i+1)+ANSI_RESET+ " Write the name of player " + (i + 1)+".");
//                        do {
//                            view.updateSameLine(ANSI_PURPLE + "INPUT HERE: " + ANSI_RESET);
//                            String name = view.keyboardStringInput();
//                            if (playerNames.contains(name)) {
//                                view.showError("Multiple players cannot have the same name. Please enter a different name.");
//                            } else {
//                                playerNames.add(name);
//                                Player player = new HumanPlayer(name);
//                                players.add(player);
//                                break;
//                            }
//                        } while (true);
//                    }
//
//                    view.update(ANSI_PURPLE + "\nSelected players: " + players + ANSI_RESET);
//
//                    Game game = new Game(players, view);
//                    view.update(ANSI_PURPLE + "\n🕹 Game configured! Have fun! 🎮\n" + ANSI_RESET);
//                    //game.start();
//                    break;
//                }
//            } catch (InputMismatchException e) {
//                view.showError("Please enter a number.");
//            }
//        } while (true);
//    }
//}

//Uncomment the lines below and the comment the lines above to switch
//            String host = String.valueOf(view.getIp());
//            view.update("Enter a port on which you would like to connect.");
//            int port = -1;
//            try {
//                port=view.keyboardIntInput();
//            }catch (NumberFormatException e){
//                e.getMessage();
//            }
