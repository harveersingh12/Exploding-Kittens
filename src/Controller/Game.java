package Controller;

import Controller.ClientServer.ClientHandler;
import Controller.ClientServer.Server;
import Model.Protocol;
import Controller.Players.ComputerPlayer;
import Model.Deck;
import Model.Card;
import Model.CardType;
import Controller.Players.HumanPlayer;
import Controller.Players.Player;


import java.util.*;


/**
 * Class controlling how the game is played. Combines methods
 * and variables from all other classes to allow all the actions
 * occurring in the actual card game. Implements the runnable interface
 * so that it can be run in a separate thread from the network.
 * @invariant players, player names, deck, hands and cards are not null
 * @invariant two or more players must be playing
 * @ensures full game is played and a winner is decided
 * @ensures all the rules of exploding kittens are followed
 *
 * @author Andrei Cristian Dorneanu - s2327478
 * @author Harveer Singh - s3231046
 */
public class Game implements Runnable{

    public static int numberOfPlayers;
    public static final int CHAT_FLAG = 0;
    public static final int PARTY_FLAG = 1;
    public static final int COMBO_FLAG = 4;
    public static final int EXTENSION_FLAG = 5;
    public static ArrayList<Integer> flags = new ArrayList<>();
    public static ArrayList<Player> players;

    private Deck deck;
    private static int current;
    private ArrayList<String> playerNames;
    private ArrayList<String> markedList;
    private boolean dontDraw, endTurn, nopePlayed;
    private int remainingTurns;
    Player currentPlayer;


    /**
     * @param players, all participants of the game
     * Creates the game with a list of players, initializes
     * their hands and a new deck of cards. Adds all player names
     * to another list meant for display and creates a list of
     * marked cards for the streaking kittens extension.
     */
    public Game(ArrayList<Player> players) {
        this.players = players;
        playerNames = new ArrayList<>();
        numberOfPlayers = players.size();
        markedList = new ArrayList<>();
        current = 0;
        remainingTurns = 0;
        deck = new Deck();
        initializeHands(deck);
        for(Player player : players){
            playerNames.add(player.getName());
        }
    }


    /**
     * @param message text to display to the current player
     * Sends a message from the server to the current player,
     * usually prior to asking for input or to provide confirmation
     * for an action in the game.
     * @requires the recipient is in the client list
     * @ensures the only recipient is the current player
     */
    private void whereToShow(String message){
        for (ClientHandler client : Server.clients) {
            if(currentPlayer.getName().equals(client.getName())){
                client.send(message);
                break;
            }
        }
    }


    /**
     * @param message text to display to all players
     * Meant for announcements such as a player having
     * drawn a card (indicating end of turn), the type of card
     * played, confirming the completion of an action involving
     * all players (garbage collection).
     */
    private void broadcastAll(String message){
        for (ClientHandler client : Server.clients) {
            client.send(message);
        }
    }


    /**
     * Iterates through each player in the player list and sets
     * all the cards in their hand from the deck
     * @param deck the deck of card used in current game
     * @requires player in the player list
     * @ensures all cards in a player's hand are unique
     */
    private void initializeHands(Deck deck){
        for(int i = 0; i<players.size(); i++) {
            if(players.get(i) instanceof HumanPlayer)
                players.set(i,new HumanPlayer(players.get(i).getName(),deck));
            else  players.set(i,new ComputerPlayer(players.get(i).getName(),deck));
        }
    }


    /**
     * @return the deck being used in the current game
     */
    public Deck getDeck(){
        return deck;
    }


    /**
     * @return the deck being used in the current game
     */
    public static int getNumberOfPlayers(){
        return numberOfPlayers;
    }


    /**
     * @return the deck being used in the current game
     */
    public ArrayList<Player> getPlayers(){
        return players;
    }


    /**
     * Overridden method from the Runnable interface.
     * @ensures the game is played on an independent thread
     */
    @Override
    public void run(){
            play();
    }


    /**
     * Sets the current player to the next one in the player
     * list in chronological order.
     * @ensures player index never exceeds the player list size
     */
    private void updateCurrent(){
        current=(current+1) % numberOfPlayers;
    }


    /**
     * Allows setting the current player to any player.
     * @param index the index of a player in the player list
     * @requires player index is smaller than list size and
     * larger than or equal to zero
     */
    public static void setCurrent(int index){
        current = index;
    }


    /**
     * Handles the logic of playing the game in turns until it ends
     * and displaying the relevant information during each turn. On any given
     * turn the variable "current" is first set, the general information required to
     * play a turn are displayed to the player, and he is prompted for input through
     * the playTurn() method. After that the current player is updated and the cycle
     * repeats. The winner is displayed once the player list size is down to one.
     * @requires current to be a valid index
     * @requires gameOver() to be false at the start of the game
     * @ensures current player is given all information needed to play a turn
     * @ensures winner of the game is displayed to all players
     */
    private void play() {
        while (!gameOver()) {
            currentPlayer = players.get(current);
            broadcastAll("size of discard pile: " + deck.getDiscardPile().size());
            broadcastAll(Server.BasicCommands.CURRENT + Protocol.ARGUMENT_SEPARATOR + currentPlayer.getName());
            whereToShow("Chance of drawing an exploding kitten: " + deck.chanceOfEk()+"%");
            whereToShow(String.valueOf(deck.getDrawPile().size()));

            playTurn(currentPlayer);
            updateCurrent();
        }
        Server.askPlayer(players.get(0),players.get(0).getName() + ", you won!");
        broadcastAll(Server.BasicCommands.GAME_OVER + Protocol.ARGUMENT_SEPARATOR + players.get(0).getName());
        Server.players.clear();
        Server.setGameActive(false);
        for (ClientHandler client : Server.clients)
            client.setBlocked(false);
    }


    /**
     * Prompts player to play a card through playCard() in the HumanPlayer
     * class and calls the method corresponding to the number of cards played
     * to execute the action related to the card(s). All play is encapsulated
     * in a do-while loop with the Nope card being played as the condition.
     * This ensures that if a Nope is played, the card is lost and
     * the same player is forced to play again.
     * @param player, the player who is going to play their turn
     * @requires list of played cards is not null
     * @requires streaking kittens flag to display marked card list
     * @requires special combo flag to play multiple cards
     * @ensures the current player keeps playing if a Nope was played
     * @ensures a card is drawn unless not drawing is explicitly set
     * @ensures a cursed player draws cards until they draw a non-cat card
     */
    private void playTurn(Player player){
        nopePlayed = false;
        dontDraw = false;
        if(Game.flags.contains(Game.EXTENSION_FLAG)) {
            broadcastAll("Marked cards: " + markedList);
        }
        do{
            ArrayList<Card> playedCardList = player.playCard();
            if(playedCardList!= null) {
                markedCards(playedCardList.get(0));
                for(Card card: playedCardList){
                    deck.getDiscardPile().add(card);
                }
                if(playedCardList.size() == 1){
                    broadcastAll(Server.BasicCommands.BROADCAST_MOVE + Protocol.ARGUMENT_SEPARATOR +
                            player.getName() + Protocol.ARGUMENT_SEPARATOR + playedCardList.get(0).getType());
                    singleCard(player, playedCardList);
                    if(!dontDraw){
                        doDraw(player);
                        break;
                    }

                }else if(playedCardList.size() == 2) {
                    if(Game.flags.contains(Game.COMBO_FLAG)){
                        broadcastAll(Server.BasicCommands.BROADCAST_MOVE + Protocol.ARGUMENT_SEPARATOR +
                                player.getName() + Protocol.ARGUMENT_SEPARATOR + "TWO-CARD COMBO");
                        doubleCombo(player, playedCardList);
                        if(!dontDraw){
                            doDraw(player);
                            break;
                        }
                    } else {
                        whereToShow("Combos are not enabled in this game.");
                        for(Card c: playedCardList){
                            player.getHand().addCard(c);
                        }
                        playTurn(player);
                    }

                } else if(playedCardList.size() == 3){
                    if(Game.flags.contains(Game.COMBO_FLAG)){
                        broadcastAll(Server.BasicCommands.BROADCAST_MOVE + Protocol.ARGUMENT_SEPARATOR +
                                player.getName() + Protocol.ARGUMENT_SEPARATOR + "THREE-CARD COMBO");
                        tripleCombo(player,playedCardList);
                        if(!dontDraw){
                            doDraw(player);
                            break;
                        }
                    } else {
                        whereToShow("Combos are not enabled in this game.");
                        for(Card c: playedCardList){
                            player.getHand().addCard(c);
                        }
                        playTurn(player);
                    }
                }else if(playedCardList.size() == 5) {
                    if (Game.flags.contains(Game.PARTY_FLAG)) {
                        broadcastAll(Server.BasicCommands.BROADCAST_MOVE + Protocol.ARGUMENT_SEPARATOR +
                                player.getName() + Protocol.ARGUMENT_SEPARATOR + "FIVE-CARD COMBO");
                        quintupleCombo(player, playedCardList);
                        if (!dontDraw) {
                            doDraw(player);
                            break;
                        }
                    } else whereToShow("Team-play is not enabled in this game.");
                }
            } else{
                Card drawnCard;
                if(player.getHand().isCursed()){
                    do {
                        doDraw(player);
                        drawnCard = player.getHand().getCards().get(player.getHand().getCards().size() - 1);
                        if(drawnCard.getType() == CardType.EXPLODING_KITTEN) {
                            break;
                        }
                    } while (drawnCard.getType() == CardType.FERAL_CAT || drawnCard.getType() == CardType.RAINBOW_RALPHING || drawnCard.getType() == CardType.TACO_CAT ||
                            drawnCard.getType() == CardType.BEARD_CAT || drawnCard.getType() == CardType.CATTERMELON);
                    if(drawnCard.getType() == CardType.EXPLODING_KITTEN){
                        defuseEK(drawnCard, player);
                    }
                    player.getHand().setCurse();
                }else {
                    doDraw(player);
                    break;
                }
            }
        }while(nopePlayed);
    }


    /**
     * Switch case containing all card types from the party pack, streaking kittens
     * extension and the base version of the game. The turn of the player does not end
     * until endTurn is set to true.
     * @param player, player who is going to play a single card
     * @param playedCardList, the played card in a list consisting of one element
     * @requires played card type is valid
     * @requires player is in the player list
     * @requires the number of cards played is equal to one
     * @requires bonus flags for bonus card types
     * @ensures only one card is played
     * @ensures turn ends after execution of card logic and endTurn being set to true
     * @ensures bonus card types are only played if their respective flag is active
     */
    private void singleCard(Player player, ArrayList<Card> playedCardList){
        Card playedCard=playedCardList.get(0);
        endTurn=false;

        while(playedCard!=null){
            if(playedCard.getType()!=CardType.DEFUSE && playedCard.getType()!=CardType.NOPE
                    && playedCard.getType()!=CardType.TACO_CAT && playedCard.getType()!=CardType.RAINBOW_RALPHING
                    && playedCard.getType()!=CardType.BEARD_CAT && playedCard.getType()!=CardType.HAIRY_POTATO
                    && playedCard.getType()!=CardType.CATTERMELON){
                if(anyPlayerWantsToNope()){
                    nopePlayed = true;
                    dontDraw = true;
                    break;
                }
            }
            switch(playedCard.getType()){
            case NOPE:
                whereToShow("You cannot play a NOPE right now");
                player.getHand().addCard(playedCard);
                break;
            case DEFUSE:
                whereToShow("You cannot play a defuse card at the moment");
                player.getHand().getCards().add(playedCard);
                break;
                case ATTACK:
                    attackCase(player);
                    break;
            case TARGETED_ATTACK:
                targetedAttackCase(player);
                break;
            case SKIP:
                break;
            case SHUFFLE:
                deck.shuffle();
                whereToShow("The draw pile has been shuffled");
                break;
            case FAVOR:
                favorCase(player);
                break;
            case SEE_THE_FUTURE:
                for (int i=0; i<3; i++) {
                    whereToShow(deck.getDrawPile().get(i).getName());
                }
                break;
            case DRAW_FROM_THE_BOTTOM:
                dontDraw = true;
                endTurn = true;
                if(deck.getDrawPile() != null){
                    Card drawnCard=deck.getDrawPile().get(deck.getDrawPile().size()-1);
                    if(drawnCard!=null) {
                        player.getHand().addCard(drawnCard);
                        deck.addDiscardRemoveDrawPile(drawnCard);
                       // whereToShow("Player " + player.getName() + ", you drew the card " + drawnCard.getName() + "." + "\nHand updated:\n" + player.getHand());
                        whereToShow(player.getHand().toString());
                        checkEK(drawnCard, player);
                    }
                }
                break;
            case ALTER_THE_FUTURE:
                alterTheFuture3Case();
                break;
            case FERAL_CAT:
                whereToShow("This card is powerless on its own.");
                player.getHand().addCard(playedCard);
                break;
            default:
                if(Game.flags.contains(Game.EXTENSION_FLAG)){
                    switch(playedCard.getType()){
                    case STREAKING_KITTEN:
                        whereToShow("You cannot play this card, it only gives you the ability to hold an exploding kitten");
                        player.getHand().addCard(playedCard);
                        break;
                    case EXPLODING_KITTEN:
                    boolean wasDefused = false;
                    for(Card isDefuse : player.getHand().getCards()){
                        if(isDefuse.getType() == CardType.DEFUSE){
                            wasDefused = true;
                            broadcastAll(player.getName() + " defused an exploding kitten");
                            Server.askPlayer(player,Server.BasicCommands.ASK_FOR_INDEX + Protocol.ARGUMENT_SEPARATOR + deck.getDrawPile().size());
                            try{
                            int ekIndex = Integer.parseInt(Server.receiveInput(player))-1;
                            while(ekIndex < 0 || ekIndex > deck.getDrawPile().size() - 1){
                                Server.askPlayer(player,Server.BasicCommands.ASK_FOR_INDEX + Protocol.ARGUMENT_SEPARATOR + deck.getDrawPile().size());
                                Server.askPlayer(player,"Please insert a valid location. It should be between " + 1  + " and " + deck.getDrawPile().size());
                                ekIndex =Integer.parseInt(Server.receiveInput(player))-1;
                                deck.getDrawPile().add(ekIndex, playedCard);
                                }
                            }catch(NumberFormatException e){
                                Server.askPlayer(player,"Please insert a number");
                            }
                            player.getHand().getCards().remove(playedCard);
                            player.getHand().getCards().remove(isDefuse);
                            break;
                        }
                    }
                    if(!wasDefused){
                        broadcastAll(player.getName() + " did not defuse an EK");
                        Server.askPlayer(player,Server.BasicCommands.PLAYER_OUT + Protocol.ARGUMENT_SEPARATOR + player.getName());
                        players.remove(player);
                    }
                    case SUPER_SKIP:
                        dontDraw = true;
                        remainingTurns = 0;
                        Server.askPlayer(player, "You ended your turn");
                        moveToNextPlayer();
                        endTurn = true;
                        break;
                    case x5_SEE_THE_FUTURE:
                        for (int i=0; i<5; i++) {
                            whereToShow(deck.getDrawPile().get(i).getName());
                        }
                        break;
                    case x5_ALTER_THE_FUTURE:
                        alterTheFuture5Case();
                        break;
                    case SWAP_TOP_AND_BOTTOM:
                        deck.getDrawPile().set(0,deck.getDrawPile().get(deck.getDrawPile().size()-1));
                        deck.getDrawPile().set(deck.getDrawPile().size()-1, deck.getDrawPile().get(0));
                        whereToShow("Top card and bottom card have been swapped");
                        break;
                    case GARBAGE_COLLECTION:
                        for(Player p: players){
                                Card cardToInsert = null;
                                if(!p.getHand().getCards().isEmpty()){
                                    if(!p.getHand().isCursed()){
                                        broadcastAll("Asking " + p.getName() + " to insert a card into the draw pile");
                                        Server.askPlayer(p, Server.BasicCommands.ASK_FOR_CARDNAME + Protocol.ARGUMENT_SEPARATOR + "please select a card to insert randomly into the draw pile");
                                        Server.askPlayer(p, p.getHand().toString());
                                        String input = Server.receiveInput(p);
                                        cardToInsert = p.findCardByName(input);
                                        while(!p.getHand().getCards().contains(cardToInsert)){
                                            input = Server.receiveInput(p);
                                            cardToInsert = p.findCardByName(input);
                                        }
                                    }else {
                                        Server.askPlayer(p, Server.BasicCommands.ASK_FOR_CARDNAME + Protocol.ARGUMENT_SEPARATOR + "please select a card to insert randomly into the draw pile");
                                        Server.askPlayer(p, p.getHand().toString());
                                        try {
                                            int intInput = Integer.parseInt(Server.receiveInput(p)) - 1;
                                            while (intInput < 0 || intInput >= p.getHand().getCards().size()) {
                                                intInput = Integer.parseInt(Server.receiveInput(p)) - 1;
                                            }
                                            cardToInsert = p.getHand().getCards().get(intInput);
                                        }catch (NumberFormatException e){
                                            Server.askPlayer(player,"Please insert a number");
                                        }
                                    }
                                    p.getHand().getCards().remove(cardToInsert);
                                    Random random = new Random();
                                    int randomIndex = random.nextInt(deck.getDrawPile().size()-1);
                                    deck.getDrawPile().add(randomIndex, cardToInsert);
                            }
                        }
                        deck.shuffle();
                        broadcastAll("Garbage has been collected and the deck has been shuffled");
                        break;
                    case CATOMIC_BOMB:
                        ArrayList<Card> listOfEk = new ArrayList<>();
                        broadcastAll("Exploding kittens in the deck:");
                        for(Card card: new ArrayList<>(deck.getDrawPile())){
                            if(card.getType() == CardType.EXPLODING_KITTEN){
                                broadcastAll(card.getName());
                                deck.getDrawPile().remove(card);
                                listOfEk.add(card);
                            }
                        }
                        deck.shuffle();
                        for(Card ek : listOfEk){
                            deck.getDrawPile().add(0,ek);
                        }
                        broadcastAll("All exploding kittens have been reinserted at the top of the draw pile.");
                        endTurn = true;
                        dontDraw = true;
                        break;
                    case MARK:
                        whereToShow("Choose a player to mark:");
                        Player markedPlayer = askForName(player.getName());
                        int markedCardIndex = 0;

                        if(markedPlayer!=null){
                            whereToShow("Pick a random number between 1 and " + markedPlayer.getHand().getCards().size());
                            while(true){
                                try {
                                    markedCardIndex = Integer.parseInt(Server.receiveInput(currentPlayer)) - 1;
                                }catch (NumberFormatException e){
                                    Server.askPlayer(player,"Please insert a number");
                                }
                                if(markedCardIndex >= 0 && markedCardIndex < markedPlayer.getHand().getCards().size()){
                                    break;
                                }
                                else {
                                    whereToShow("Pick a valid number");
                                }
                            }

                            Card markedCard = markedPlayer.getHand().getCards().get(markedCardIndex);
                            markedList.add(markedPlayer.getName() + ": " + markedCard.getName());
                        }
                        break;
                    case CURSE_OF_THE_CAT_BUTT:
                        whereToShow("Choose a player to curse: ");
                        Player unluckyPlayer = askForName(player.getName());
                        if(unluckyPlayer != null){
                            unluckyPlayer.getHand().setCurse();
                        }
                        break;
                    }
                }
                if(playedCard.getType() == CardType.TACO_CAT || playedCard.getType() == CardType.RAINBOW_RALPHING
                        || playedCard.getType() == CardType.BEARD_CAT || playedCard.getType() == CardType.CATTERMELON
                        || playedCard.getType() == CardType.HAIRY_POTATO){
                    whereToShow("This card is powerless on its own.");
                    if(!player.getHand().isCursed()){
                        player.getHand().addCard(playedCard);
                    }
                }
                else if(playedCardList.size()==0)
                    whereToShow("Please play a card in your hand");
            }

            if(playedCard.getType()==CardType.SKIP){
                dontDraw=true;
                break;
            }

            if(!endTurn){
                playedCardList=player.playCard();
                if(playedCardList != null &&  playedCardList.size()==1){
                    playedCard=playedCardList.get(0);
                }
                else playedCard = null;
            } else {
                playedCard=null;
            }
        }
    }


    /**
     * Allows updating the status of the current player
     * outside normal circumstances, usually only done
     * at the end of a turn.
     */
    private void moveToNextPlayer() {
        setCurrent((current + 1) % players.size());
    }


    /**
     * Removes a card from marked cards if it is in the list.
     * @param card, card to be removed from the marked card list
     */
    private void markedCards(Card card){
        markedList.removeIf(s -> s.contains(card.getName()));
    }


    /**
     * Steals a random card from the player of choice by the current
     * player.
     * @param player, the current player
     * @param playedCardList, the list containing the cards played
     * @requires number of played cards is two
     * @requires the name of the player to steal from is in the player list
     * @requires the index of the card to steal is within the size of the hand
     * of the player to steal from
     * @ensures the attacked player name and the index of the card to steal are valid
     * @ensures action is canceled if a Nope is played
     * @ensures the stolen card is removed from the list of marked cards (if marked)
     * @ensures the stolen card is added to the hand of the current player and removed
     * from the hand of the player who originally had it
     */
    private void doubleCombo(Player player, ArrayList<Card> playedCardList){
        while (playedCardList != null) {

            String currentPlayerName = player.getName();

            whereToShow("You played a two card combo, select a player to take a random card from: ");
            if (anyPlayerWantsToNope()) {
                nopePlayed = true;
                dontDraw = true;
                break;
            }
            Player stealPlayer = askForName(currentPlayerName);
            int stealCardIndex = 0;
            if(stealPlayer!=null) {
                whereToShow("Choose the index of a card to steal from player " + stealPlayer.getName()
                        + ". Size of hand is: " + stealPlayer.getHand().getCards().size());
                try{
                stealCardIndex = Integer.parseInt(Server.receiveInput(currentPlayer)) - 1;

                while (stealCardIndex < 0 || stealCardIndex > stealPlayer.getHand().getCards().size() - 1) {
                    whereToShow("Please insert a valid index. You can choose a number between 1 and " + stealPlayer.getHand().getCards().size());
                    stealCardIndex = Integer.parseInt(Server.receiveInput(currentPlayer)) - 1;
                }
                }catch (NumberFormatException e){
                    Server.askPlayer(player,"Please insert a number");
                }
                Card stealCard = null;

                for (int i = 0; i < stealPlayer.getHand().getCards().size(); i++)
                    if (i == stealCardIndex) {
                        stealCard = stealPlayer.getHand().getCards().get(i);
                        markedCards(stealCard);
                        break;
                    }

                stealPlayer.getHand().removeCard(stealCard);
                player.getHand().addCard(stealCard);
                whereToShow("You stole from " + stealPlayer.getName() + " the card " + stealCard.getName());
                Server.askPlayer(stealPlayer, currentPlayerName + " stole " + stealCard.getName() + "from you. (double combo)");
            }
            playedCardList = player.playCard();
            if(playedCardList != null){
                if(playedCardList.size()==1){
                    singleCard(player,playedCardList);
                    return;
                } else if(playedCardList.size()==3){
                    tripleCombo(player,playedCardList);
                    return;
                }else if(Game.flags.contains(Game.PARTY_FLAG)){
                    if(playedCardList.size()==5){
                        quintupleCombo(player, playedCardList);
                    }
                }
            }
        }
    }


    /**
     * Steals a card of choice from a player of choice by the current
     * player.
     * @param player, current player
     * @param playedCardList, list of cards played
     * @requires number of played cards to be three
     * Same pre- and post-conditions as the two-card combo
     */
    private void tripleCombo(Player player, ArrayList<Card> playedCardList){
        while(playedCardList != null){

            whereToShow("You played a three card combo, select a player to take a random card from:");
            if(playedCardList.get(0).getType() != CardType.DEFUSE){
                if (anyPlayerWantsToNope()) {
                    nopePlayed = true;
                    dontDraw = true;
                    break;
                }
            }

            Player steal3Player = askForName(player.getName());

            whereToShow("Enter the index of a card type that you would like to steal from."+steal3Player.getName());

            for (int i=0; i<CardType.values().length; i++) {
                if(CardType.values()[i]!=CardType.EXPLODING_KITTEN) {
                    whereToShow((i+1)+". "+CardType.values()[i]);
                }
            }
            int typeIndex = 0;

            try{
                typeIndex=Integer.parseInt(Server.receiveInput(currentPlayer))-1;

                while(typeIndex<0||typeIndex>CardType.values().length-1||CardType.values()[typeIndex]==CardType.EXPLODING_KITTEN){
                whereToShow(typeIndex+" is not a valid card type. Enter again!");
                typeIndex=Integer.parseInt(Server.receiveInput(currentPlayer))-1;
                }
            }catch (NumberFormatException e){
                Server.askPlayer(player,"Please insert a number");
            }
            if(!steal3Player.getHand().containsCardType(CardType.values()[typeIndex])){
                whereToShow("You get nothing! HAHAHAHA!");
            } else {
                Card steal3Card=null;
                for (Card card : steal3Player.getHand().getCards())
                    if(card.getType()==CardType.values()[typeIndex]){
                        steal3Card=card;
                        markedCards(card);
                        break;
                    }
                player.getHand().addCard(steal3Card);
                steal3Player.getHand().removeCard(steal3Card);
                whereToShow("You stole a "+CardType.values()[typeIndex].toString()+" from "+steal3Player.getName());
                broadcastAll(Server.BasicCommands.BROADCAST_MOVE+Protocol.ARGUMENT_SEPARATOR+currentPlayer.getName()+" stole a card from " + steal3Player.getName());
                Server.askPlayer(steal3Player, currentPlayer.getName()+ " stole " + steal3Card.getName() + "from you. (triple combo)");
            }
            playedCardList = player.playCard();

            if(playedCardList != null){
                if(playedCardList.size()==1){
                    singleCard(player,playedCardList);
                    return;
                } else if(playedCardList.size()==2){
                    doubleCombo(player,playedCardList);
                    return;
                }else if(Game.flags.contains(Game.PARTY_FLAG)){
                    if(playedCardList.size()==5){
                        quintupleCombo(player, playedCardList);
                    }
                }
            }
        }
    }



    private void attackCase(Player player){
        Server.askPlayer(player,"You attacked the next player");
        updateCurrent();
        Server.askPlayer(players.get(current),"You have been attacked");
        remainingTurns += 2;

        playTurn(players.get(current));

        remainingTurns--;
        if(remainingTurns != 0) {
            playTurn(players.get(current));

            remainingTurns--;
            int cloneRemainingTurns = remainingTurns;
            for (int i = 0; i < cloneRemainingTurns; i++) {
                playTurn(players.get(current));
                remainingTurns--;
            }
        }

        setCurrent(players.indexOf(player));
        dontDraw = true;
        endTurn = true;
    }


    /**
     * Steals any card from the discard pile except for a
     * streaking or exploding kitten.
     * @param player, current player
     * @param playedCardList, list of played cards
     * @requires card to be stolen is in the discard pile
     * @requires card type is not exploding/streaking kitten
     * @requires five cards of a different types to have been played
     * @ensures combo and the chosen card (from the discard pile) are valid
     * @ensures the card is removed from the discard pile and added to the
     * player's hand
     */
    private void quintupleCombo(Player player, ArrayList<Card> playedCardList){
        while(playedCardList!=null){
            Card chosenCard = null;
            whereToShow("You played a five card combo, select any card from the discard pile (except for a streaking or exploding kitten)");
            for(Card card: deck.getDiscardPile()){
                whereToShow(card.getName());
            }
            while(chosenCard == null || chosenCard.getType() == CardType.EXPLODING_KITTEN || chosenCard.getType() == CardType.STREAKING_KITTEN){
                String input = Server.receiveInput(player);
                for(Card card: deck.getDiscardPile()){
                    if(card.getName().equals(input)){
                        chosenCard = card;
                        break;
                    }
                }if(chosenCard == null || chosenCard.getType() == CardType.EXPLODING_KITTEN || chosenCard.getType() == CardType.STREAKING_KITTEN){
                    whereToShow("The chosen card must be in the discard pile and cannot be a streaking or exploding kitten");
                }
            }
            for (Card card: playedCardList) {
                player.getHand().getCards().remove(card);
                markedCards(card);
            }
            player.getHand().getCards().add(chosenCard);
            deck.getDiscardPile().remove(chosenCard);
            broadcastAll(player.getName() +" played a five card combo.");

            playedCardList = player.playCard();
            if(playedCardList != null){
                if(playedCardList.size()==1){
                    singleCard(player,playedCardList);
                    return;
                } else if(playedCardList.size()==2){
                    doubleCombo(player,playedCardList);
                    return;
                }else if(playedCardList.size()==3){
                    tripleCombo(player, playedCardList);
                }
            }
        }
    }


    /**
     * Displays all player names in the list of player names
     * and prompts the user to enter one of them.
     * If invalid, player is prompted until he provides a name in the list.
     * @param currentPlayerName, name of the current player
     * @requires player name input is in the list of player names
     * @ensures only a valid name returns a player
     * @return the player to be attacked (in case of a favor, attack,
     * double or triple combo) or null
     */
    private Player askForName(String currentPlayerName){
        String show = Server.BasicCommands.ASK_FOR_PLAYERNAME + Protocol.ARGUMENT_SEPARATOR;
            for (int i=0; i<playerNames.size(); i++) {
                if(!playerNames.get(i).equals(currentPlayerName)){
                    if(i==playerNames.size()-1) show+=playerNames.get(i);
                    else show+=playerNames.get(i)+Protocol.ELEMENT_SEPARATOR;
                }
            }
            whereToShow(show);

        String playerName = Server.receiveInput(currentPlayer);
        while(!playerNames.contains(playerName) || playerName.equals(currentPlayerName)){
            whereToShow("Please select a valid player name, you can choose from the following options:");
            show = Server.BasicCommands.ASK_FOR_PLAYERNAME + Protocol.ARGUMENT_SEPARATOR;
            for (int i=0; i<playerNames.size(); i++) {
                if(!playerNames.get(i).equals(currentPlayerName)){
                    if(i==playerNames.size()-1) show+=playerNames.get(i);
                    else show+=playerNames.get(i)+Protocol.ELEMENT_SEPARATOR;
                }
            }
            whereToShow(show);
            playerName = Server.receiveInput(currentPlayer);
        }
        for(Player player: players){
            if(player.getName().equals(playerName)){
                return player;
            }
        }
        return null;
    }


    /**
     * Asks all players except for the current player whether they want
     * to play a Nope card (if they have a Nope in their hand).
     * If so, the current player's action is canceled; the current player
     * is then given the opportunity to play a Nope on the first Nope, which
     * reinstates the original card played. As long as someone has a Nope in their
     * hand, they can play it and either NOPE or YUP their original action.
     * @requires input is either 'yes' or 'no'
     * @requires a preceding action (card played)
     * @ensures only valid input is accepted
     * @ensures every even Nope is a YUP and every odd one is a NOPE
     * @ensures loop of asking whether someone wants to Nope only breaks
     * when no one has a Nope card in their hand
     * @return whether the original card played was Noped or not
     */
    private boolean anyPlayerWantsToNope() {
        boolean actionCanceled = false;
        Player lastNopePlayer = null;

        while (true) {
            boolean nopePlayed = false;
            String nopeChoice;
            String originalNopeChoice;

            for (Player player : players) {

                if (player == lastNopePlayer) {
                    continue;
                }
                if (player != players.get(current) && player.hasNopeCard()) {
                    Server.askPlayer(player, Server.BasicCommands.ASK_FOR_YESORNO + Protocol.ARGUMENT_SEPARATOR + player.getName()
                            + ", do you want to play a Nope card to cancel the action? (yes/no)");

                    do {
                        nopeChoice = Server.receiveInput(player).toLowerCase();
                        if (nopeChoice.equals("yes")) {
                            player.playNopeCard();
                            broadcastAll(Server.BasicCommands.BROADCAST_MOVE + Protocol.ARGUMENT_SEPARATOR + "Action canceled by playing a Nope card.");
                            actionCanceled = true;
                            nopePlayed = true;
                            lastNopePlayer = player;
                            break;
                        } else if (nopeChoice.equals("no")) {
                            return actionCanceled;
                        } else {
                            whereToShow("Invalid input. Please enter 'yes' or 'no'.");
                        }
                    } while (true);
                }
            }
            if (!nopePlayed) break;
            if (players.get(current).hasNopeCard()) {
                whereToShow(Server.BasicCommands.ASK_FOR_YESORNO + Protocol.ARGUMENT_SEPARATOR + players.get(current).getName() + ", do you want to play a Nope card to cancel the previous Nope? (yes/no)");
                do {
                    originalNopeChoice = Server.receiveInput(currentPlayer).toLowerCase();

                    if (originalNopeChoice.equals("yes")) {
                        players.get(current).playNopeCard();
                        deck.getDiscardPile().add(players.get(current).getHand().findCard(CardType.NOPE));
                        broadcastAll(Server.BasicCommands.BROADCAST_MOVE + Protocol.ARGUMENT_SEPARATOR + "Previous Nope action canceled by playing a Nope card.");
                        lastNopePlayer = players.get(current);
                        actionCanceled = false;
                        break;
                    } else if (originalNopeChoice.equals("no")) {
                        broadcastAll(Server.BasicCommands.BROADCAST_MOVE + Protocol.ARGUMENT_SEPARATOR + "Action canceled by playing a Nope card.");
                        break;
                    } else {
                        whereToShow("Invalid input. Please enter 'yes' or 'no'.");
                    }

                } while (true);
                if (actionCanceled) break;
            } else break;
        }
        return actionCanceled;
    }

    /**
     * Displays to all players that the player has drawn a card.
     * Removes the top card from the draw pile, adds it to the player's
     * hand and checks whether it was an exploding kitten.
     * @param player, the player who is going to draw a card
     */
    private void doDraw(Player player){
        broadcastAll(Server.BasicCommands.BROADCAST_MOVE + Protocol.ARGUMENT_SEPARATOR +
                player.getName() + Protocol.ARGUMENT_SEPARATOR + "drew a card");
        Card drawnCard=player.drawCard(deck);
        checkEK(drawnCard,player);
    }


    /**
     * Allows the player to rearrange the top five cards in the draw pile in
     * whatever order they like.
     * @requires five unique indices between one and five
     * @ensures the rearranged cards are added to the draw pile in the chosen order
     */
    private void alterTheFuture5Case() {
            ArrayList<Card> cardsToAlter = new ArrayList<>();
            String[] split = null;
            boolean validList = false;
            int alteredIndex;
            whereToShow("Rearrange the following cards in whatever order of indices you would like (split each index with a ,): ");

            for (int i = 0; i < 5; i++) {
                cardsToAlter.add(deck.getDrawPile().get(i));
                whereToShow((i + 1) + ": " + deck.getDrawPile().get(i).getName());
                deck.getDrawPile().remove(0);
            }

            while (!validList) {
                String input = Server.receiveInput(currentPlayer);
                split = input.split(",");
                HashSet<String> uniqueList = new HashSet<>(Arrays.asList(split));

                if (uniqueList.size() == 5) {
                    boolean indicesWithinRange = true;

                    for (String index : split) {
                        try {
                            alteredIndex = Integer.parseInt(index) - 1;
                        }catch (NumberFormatException e){
                            Server.askPlayer(players.get(current),"Please insert a number");
                            indicesWithinRange = false;
                            break;
                        }
                        if (alteredIndex < 0 || alteredIndex >= cardsToAlter.size()) {
                            indicesWithinRange = false;
                            break;
                        }
                    }
                    validList = indicesWithinRange;
                }
                if (!validList) {
                    whereToShow("Please provide a list of five unique indices in the given range (1 to 5)");
                }
            }

            ArrayList<Card> alteredCards = new ArrayList<>();
            for (String index : split) {
                alteredIndex = Integer.parseInt(index) - 1;
                Card alteredCard = cardsToAlter.get(alteredIndex);
                alteredCards.add(alteredCard);
            }

            for (int i = 4; i >= 0; i--) {
                deck.getDrawPile().add(0, alteredCards.get(i));
            }
            whereToShow("The future has been altered");

        }


    /**
     * The same case as altering the future with five cards but with
     * three cards, and meant for the party-pack extension
     * instead of streaking kittens.
     */
    private void alterTheFuture3Case(){

        ArrayList<Card> cardsToAlter = new ArrayList<>();
        String[] split = null;
        boolean validList = false;
        int alteredIndex;

        whereToShow("Rearrange the following cards in whatever order of indices you would like (split each index with a ,): ");

        for (int i = 0; i < 3; i++) {
            cardsToAlter.add(deck.getDrawPile().get(i));
            whereToShow((i + 1) + ": " + deck.getDrawPile().get(i).getName());
            deck.getDrawPile().remove(0);
        }

        while (!validList) {
            String input = Server.receiveInput(currentPlayer);
            split = input.split(",");
            HashSet<String> uniqueList = new HashSet<>(Arrays.asList(split));

            if (uniqueList.size() == 3) {
                boolean indicesWithinRange = true;

                for (String index : split) {
                    try {
                        alteredIndex = Integer.parseInt(index) - 1;
                    }catch (NumberFormatException e){
                        Server.askPlayer(players.get(current),"Please insert a number");
                        indicesWithinRange = false;
                        break;
                    }
                    if (alteredIndex < 0 || alteredIndex >= cardsToAlter.size()) {
                        indicesWithinRange = false;
                        break;
                    }
                }

                validList = indicesWithinRange;
            }
            if (!validList) {
                whereToShow("Please provide a list of three unique indices in the given range (1 to 3)");
            }
        }

        ArrayList<Card> alteredCards = new ArrayList<>();
        for (String index : split) {
            alteredIndex = Integer.parseInt(index) - 1;
            Card alteredCard = cardsToAlter.get(alteredIndex);
            alteredCards.add(alteredCard);
        }

        for (int i = 2; i >= 0; i--) {
            deck.getDrawPile().add(0, alteredCards.get(i));
        }
        whereToShow("The future has been altered");
    }


    /**
     * Prompts the user to choose a player name from the player name
     * list in the case of a target attack, or changes the turn to the
     * next player in case of a normal attack. That player will then be
     * forced to play two consecutive turns. In a two-player situation,
     * it will be three turns since the attacked player would be next.
     * @param player, current player, the one attacking
     * @requires attacked player name to be in the list of player names
     * @ensures attacked player plays two turns (three in a two-player game)
     * @ensures attacking player skips drawing a card
     */
    private void targetedAttackCase(Player player) {
        Server.askPlayer(player,"You played an attack card, select the player you want to attack");
        Player attackedPlayer = askForName(player.getName());
        int attackedPlayerIndex = players.indexOf(attackedPlayer);
        setCurrent(attackedPlayerIndex);
        Server.askPlayer(players.get(current),"You have been attacked");
        remainingTurns += 2;

        playTurn(players.get(current));

        remainingTurns--;
        if(remainingTurns != 0) {
            playTurn(players.get(current));

            remainingTurns--;
            int cloneRemainingTurns = remainingTurns;
            for (int i = 0; i < cloneRemainingTurns; i++){
                playTurn(players.get(current));
                remainingTurns--;
            }
        }

        setCurrent(players.indexOf(player));
        dontDraw = true;
        endTurn = true;
    }

    /**
     * Steals a card from a player of choice, player getting stolen from
     * gets to choose which card they give.
     * @requires the name of player to steal from is in the list of player names
     * @requires the favor player to give a card that is in their hand
     * @ensures the player name and card given away are both valid
     * @ensures the given card is unmarked if it was marked
     * @ensures the given card is removed from the favor player's hand and added
     * to the hand of the player stealing
     * @param player, the current player
     */
    private void favorCase(Player player){
        String givenCardName;
        Card givenCard = null;
        String currentPlayerName=player.getName();

        whereToShow("You played a favor card, select a player to take a card from");

        Player favorPlayer = askForName(currentPlayerName);
        whereToShow("Asking favor player to pick a card to give to you");
        Server.askPlayer(favorPlayer, favorPlayer.getHand().toString());

        if(favorPlayer.getHand().isCursed()){
            Server.askPlayer(favorPlayer,Server.BasicCommands.ASK_FOR_INDEX + Protocol.ARGUMENT_SEPARATOR + "pick a card index to give away");
            int givenCardIndex = Integer.parseInt(Server.receiveInput(favorPlayer))-1;
            while(givenCardIndex < 0 || givenCardIndex >= player.getHand().getCards().size()){
                Server.askPlayer(favorPlayer, "select an index between 1 and " + favorPlayer.getHand().getCards().size());
                givenCardIndex = Integer.parseInt(Server.receiveInput(favorPlayer))-1;
            }
            givenCard = favorPlayer.getHand().getCards().get(givenCardIndex);
            givenCardName = givenCard.getName();
        }else {
            Server.askPlayer(favorPlayer, Server.BasicCommands.ASK_FOR_CARDNAME + Protocol.ARGUMENT_SEPARATOR
                    + "pick a card to give away");
            givenCardName = Server.receiveInput(favorPlayer);
            while (!favorPlayer.getHand().getNames().contains(givenCardName)) {
                Server.askPlayer(favorPlayer, Server.BasicCommands.ASK_FOR_CARDNAME + Protocol.ARGUMENT_SEPARATOR + "Please insert a valid name.");
                givenCardName = Server.receiveInput(favorPlayer);
                for (Card card : favorPlayer.getHand().getCards())
                    if(card.getName().equals(givenCardName)) {
                        givenCard=card;
                    }
            }
        }

        if(givenCard!=null){
            markedCards(givenCard);
            favorPlayer.getHand().removeCard(givenCard);
            players.get(current).getHand().addCard(givenCard);
            Server.askPlayer(favorPlayer, givenCardName+" removed from "+favorPlayer.getName()+"'s hand and given to "+currentPlayerName);
            Server.askPlayer(currentPlayer, givenCardName+" removed from "+favorPlayer.getName()+"'s hand and given to "+currentPlayerName);
            if(Game.flags.contains(Game.EXTENSION_FLAG)){
                checkEK(givenCard, player);
            }
        }
    }


    /**
     * Checks whether a card is an exploding kitten, if so
     * the player related to it is forced to defuse it in another
     * method.
     * @param card, drawn card
     * @param player, the player who drew the card
     */
    private void checkEK(Card card, Player player){
        if(card.getType() == CardType.EXPLODING_KITTEN){
            whereToShow("You drew an exploding kitten.");
            defuseEK(card, player);
        }
    }


    /**
     * Prompts the user to enter a defuse card if they have one.
     * If they have a streaking kitten, they are given the option
     * to hold the exploding kitten and continue playing, but
     * otherwise they have to defuse it to stay in the game.
     * @param card, the exploding kitten
     * @param player, the player who has it in their hand
     * @requires a card of type DEFUSE to be played
     * @requires the played card to be in the hand of the player
     * @requires the chosen index for reinserting the exploding kitten
     * to be larger than zero and smaller than the size of the draw pile
     * @ensures the player is not forced to defuse the exploding kitten if they
     * have a streaking kitten in their hand
     * @ensures the played card is of type DEFUSE, and the chosen index for
     * reinsertion of the exploding kitten is within the valid range
     * @ensures the exploding kitten is added back to the draw pile and the defuse
     * and exploding kitten cards are removed from the player's hand
     * @ensures the player is removed from the player list (and therefore, the game)
     * if the exploding kitten is not defused
     */
    private void defuseEK(Card card, Player player){
        boolean validInput = false;
        if(Game.flags.contains(Game.EXTENSION_FLAG)){
            for(Card isSK: player.getHand().getCards()){
                if(isSK.getType() == CardType.STREAKING_KITTEN){
                    whereToShow("You have a streaking kitten. You can hold the exploding kitten");
                    while(!validInput){
                        whereToShow("Do you still want to defuse (yes/no)?");
                        String defuseChoice = Server.receiveInput(player);
                        switch (defuseChoice){
                        case "yes":
                            whereToShow("Play a defuse card if you have one. (type 'no' to get exploded)");
                            validInput = true;
                            break;
                        case "no":
                            return;
                        default: whereToShow("Invalid input, please enter yes or no");
                        }
                    }
                }
            }
        }
        int ekIndex = 0;
        for(Card isDefuse : player.getHand().getCards()){
           if(isDefuse.getType() == CardType.DEFUSE){
               broadcastAll(player.getName() + " defused an exploding kitten");
               Server.askPlayer(player,Server.BasicCommands.ASK_FOR_INDEX + Protocol.ARGUMENT_SEPARATOR + deck.getDrawPile().size());
               try{
                    ekIndex = Integer.parseInt(Server.receiveInput(player))-1;
                    while(ekIndex < 0 || ekIndex > deck.getDrawPile().size() - 1){
                        Server.askPlayer(player,Server.BasicCommands.ASK_FOR_INDEX + Protocol.ARGUMENT_SEPARATOR + deck.getDrawPile().size());
                        Server.askPlayer(player,"Please insert a valid location. It should be between " + 1  + " and " + deck.getDrawPile().size());
                        ekIndex =Integer.parseInt(Server.receiveInput(player))-1;
                    }
               }catch (NumberFormatException e){
                   Server.askPlayer(player, "Please enter a number");
               }
               deck.getDrawPile().add(ekIndex, card);
               player.getHand().getCards().remove(card);
               player.getHand().getCards().remove(isDefuse);
               return;
           }
       }
           broadcastAll(player.getName() + " did not defuse an EK");
           Server.askPlayer(player,Server.BasicCommands.PLAYER_OUT + Protocol.ARGUMENT_SEPARATOR + player.getName());
           players.remove(player);

    }


    /**
     * adds a card to the discard pile, cards that
     * have already been played and are not to be used until
     * the next game
     * @param card, the card to be added to the discard pile
     */
    private void discard(Card card){
        deck.getDrawPile().add(card);
    }


    /**
     * Checks the size of the player list at the start
     * of every turn.
     * @return whether the game is over
     */
    private boolean gameOver(){
        return players.size() == 1;
    }
}