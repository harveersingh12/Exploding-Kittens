package Model;

import Controller.Game;
import Controller.Players.Player;

import java.security.cert.Extension;
import java.util.*;

import static Model.CardType.EXPLODING_KITTEN;
import static Model.CardType.POSITION_EXTENSION;

/**
 * Class defining the set-up of the deck for the game and
 * all the actions and information pertaining to it.
 * @invariant a game deck contains 56 cards
 * @invariant the number of exploding kittens in the deck is equal
 * to the number of players subtracted by one
 * @invariant the number of defuse cards in the deck is set to
 * the correct ratio with respect to the number of players
 *
 * @author Andrei Cristian Dorneanu - s2327478
 * @author Harveer Singh - s3231046
 */
public class Deck {
    private int numberOfEk;
    private int numberOfDefuse;
    private ArrayList<Card> drawPile;
    private ArrayList<Card> discardPile;
    protected static ArrayList<String> names;


    /**
     * A game deck is built with a pile from which players draw
     * cards, a pile on which cards are played, a list of the card
     * names that are in use.
     *
     * @ensures the deck is in random order every game
     * @ensures the discard pile and used names list is empty at the
     * start of the game
     */
    public Deck(){
        this.discardPile=new ArrayList<>();
        this.numberOfEk=Game.numberOfPlayers-1;
        createNames();
        drawPile=initializeDrawPile();

        shuffle();
        //creates a drawPile of cards that will be used to play and an empty discardPile
    }


    /**
     * @return the current amount of defuse cards in the draw pile
     */
    public int getNumberOfDefuse(){
        return numberOfDefuse;
    }


    /**
     * @return the number of exploding kittens in the deck
     */
    public int getNumberOfEk(){
        return numberOfEk;
    }


    /**
     * @return the draw pile in the current game
     */
    public ArrayList<Card> getDrawPile(){
        return drawPile;
    }


    /**
     * @return the discard pile
     */
    public ArrayList<Card> getDiscardPile(){
        return discardPile;
    }


    /**
     * Creates the names for all the cards in the deck based on which
     * flags are active.
     */
    public void createNames(){
        //STANDARD or PARTY_PACK game
        if(Game.flags.contains(Game.PARTY_FLAG)){
            if(Game.numberOfPlayers<=3) listGenerator(CardType.POSITION_PARTY_PAW);
            else if(Game.numberOfPlayers<=7&&Game.numberOfPlayers>=4) listGenerator(CardType.POSITION_PARTY_NO_PAW);
            else listGenerator(CardType.POSITION_PARTY_TOTAL);
        } else listGenerator(CardType.POSITION_STANDARD);
        if(Game.flags.contains(Game.EXTENSION_FLAG)){
            for (CardType type : CardType.values())
                if(type==EXPLODING_KITTEN){
                    String extraEk=type.name().toLowerCase()+"_extra";
                    names.add(extraEk);
                } else {
                    for (int i=0; i<type.getAmount().get(POSITION_EXTENSION); i++) {
                        String name=type.name().toLowerCase()+(i+1);
                        if(!names.contains(name)) names.add(name);
                    }
                }
        }
    }


    /**
     * Generates a unique list of names for each card type in the amount
     * defined for a given flag
     * @param position_amount, the number of cards to be generated for a specific type
     * with a specific flag
     */
    private void listGenerator(int position_amount){
        names = new ArrayList<>();
        for (int i=0; i<CardType.values().length; i++) {
            CardType type=CardType.values()[i];
            switch(type){
            case EXPLODING_KITTEN:
                for (int j=0; j<numberOfEk; j++) {
                    String name=type.name().toLowerCase()+String.valueOf(j+1);
                    names.add(name);
                }
                break;
            case DEFUSE:
                if(Game.getNumberOfPlayers()<=3) this.numberOfDefuse=2+Game.getNumberOfPlayers();
                else this.numberOfDefuse=CardType.DEFUSE.getAmount().get(position_amount);
                for (int j=0; j<numberOfDefuse; j++) {
                    String name=type.name().toLowerCase()+String.valueOf(j+1);
                    names.add(name);
                }
                break;
            default:
                for (int j=0; j<type.getAmount().get(position_amount); j++) {
                    String name=type.name().toLowerCase()+String.valueOf(j+1);
                    names.add(name);
                }
            }
        }
    }


    /**
     * Set up of the draw pile, initializing and adding all cards.
     * Does so by iterating through all card types in generateCards(),
     * adding the corresponding number of cards and updating the
     * list of used names each time.
     * @return the complete draw pile, with which the game begins
     * @ensures every card is unique
     */
    public ArrayList<Card> initializeDrawPile(){
        drawPile = new ArrayList<>();
        //PARTY PACK
        if(Game.flags.contains(Game.PARTY_FLAG)){
            if(Game.numberOfPlayers<=3) generateCards(CardType.POSITION_PARTY_PAW);
            else if(Game.numberOfPlayers<=7&&Game.numberOfPlayers>=4) generateCards(CardType.POSITION_PARTY_NO_PAW);
            else generateCards(CardType.POSITION_PARTY_TOTAL);
        }
        //STANDARD GAME
        else {
            generateCards(CardType.POSITION_STANDARD);
        }
        //STREAKING KITTEN EXTENTION
        if(Game.flags.contains(Game.EXTENSION_FLAG)){
            for(CardType type : CardType.values()){
                for(int i = 0; i < type.getAmount().get(POSITION_EXTENSION); i++){
                    Card extensionCard = new Card(type);
                    drawPile.add(extensionCard);
                }
            }
        }
        return drawPile;
    }

    private void generateCards(int position_amount){
        for (CardType type : CardType.values()) {
            switch(type){
            case EXPLODING_KITTEN:
                for (int i=0; i<numberOfEk; i++) {
                    Card newCard=new Card(type);
                    drawPile.add(newCard);
                }
                break;
            case DEFUSE:
                if(Game.getNumberOfPlayers()<=3) this.numberOfDefuse=2+Game.getNumberOfPlayers();
                else this.numberOfDefuse=CardType.DEFUSE.getAmount().get(position_amount);
                for (int i=0; i<numberOfDefuse; i++) {
                    Card newCard = new Card(type);
                    drawPile.add(newCard);
                }
                break;
            default:
                for (int i=0; i<type.getAmount().get(position_amount); i++) {
                    Card newCard = new Card(type);
                    drawPile.add(newCard);
                }
            }
        }
    }


    /**
     * Empties the list of used names and resets the draw pile to allow
     * the card names to be reused for every new game
     * @ensures the draw pile and list of used name is reset
     */
    public void resetDeck(){
        drawPile.clear();
        names.clear();
        initializeDrawPile();
    }

    /**
     * @requires card is not null
     * @requires card is contained in the draw pile
     * @param card is the card to be played
     * @ensures card is added to the discard pile and
     * removed from the draw pile
     */
    public void addDiscardRemoveDrawPile(Card card){
        discardPile.add(card);
        drawPile.remove(card);
    }


    /**
     * Shuffles the draw pile
     * @ensures the order of cards in the draw pile
     * is randomized after every invocation.
     */
    public void shuffle(){
        Collections.shuffle(drawPile);
    }


    /**
     * Calculates the probability of drawing an exploding kitten
     * at any moment in the game
     * @return the probability in percentage
     */
    public int chanceOfEk(){
        double eks = 0;
        for(Card card : drawPile)
            if (card.getType() == EXPLODING_KITTEN)
                eks++;
        return (int) eks*100/drawPile.size();
    }


    /**
     * Removal of the top card from the initialized draw pile
     * @requires the draw pile has at least one card in it
     * @ensures that the top card is drawn
     * @return the drawn card
     */
    public Card drawCard(){
        if(!drawPile.isEmpty()){
            return drawPile.remove(0);
        } return null;
    }


    /**
     * Provides a string representation of all the cards in deck
     * @return the list of cards in the deck
     */
    @Override
    public String toString(){
        String result=" ";
        int i =1;
        for (Card card : drawPile) {
            result+=i+". "+card.getName()+"\n";
            i++;
        }
        return result;
    }

}