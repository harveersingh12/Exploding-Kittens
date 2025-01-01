package Model;

import Controller.ClientServer.Server;

import java.util.ArrayList;
import java.util.Random;


/**
 * Class represents a player's hand in the game,
 * containing a collection of cards and providing various
 * methods to interact with the hand.
 *
 * @author Andrei Cristian Dorneanu - s2327478
 * @author Harveer Singh - s3231046
 */
public class Hand {
    private ArrayList<Card> cards;
    private ArrayList<String> names;
    private boolean cursed;
    private static final int CARDS_IN_HAND = 7;


    /**
     * Constructs a Hand object by initializing cards in the hand based on the
     * provided deck. The hand of player is only cursed when a 'curse of the cat
     * butt' card is played on them.
     * @param deck The Deck object from which the initial cards are drawn.
     */
    public Hand(Deck deck){
        this.cards = initializeCardsInHand(deck);
        cursed = false;
    }


    /**
     * Checks if the hand is cursed.
     * @return true if the hand is cursed
     */
    public boolean isCursed() {
        return cursed;
    }


    /**
     * Sets the hand to the opposite
     * of its current state
     */
    public void setCurse(){
        cursed = !cursed;
    }


    /**
     * @return the list of cards in the hand
     */
    public ArrayList<Card> getCards() {
        return cards;
    }


    /**
     * @return list of all card names in the hand
     */
    public ArrayList<String> getNames(){
        setNames();
        return names;
    }


    /**
     * Sets the list of names by populating it with the names of the cards in the hand.
     * @ensures The list is updated to reflect the current cards in the hand.
     */
    public void setNames(){
        names = new ArrayList<>();
        for(Card card : cards)
            names.add(card.getName());
    }


    /**
     * Initializes the cards in the hand by drawing from the deck, ensuring that
     * exploding kittens and defuse cards are not drawn excessively. Also adds
     * a defuse card to a random position in the hand.
     * @param deck The deck instance from which cards are drawn.
     * @ensures card added to hand is removed from the draw pile.
     * @return a list of Card objects representing the initialized hand.
     */
    private ArrayList<Card> initializeCardsInHand(Deck deck){
        ArrayList<Card> result = new ArrayList<>();
        for(int i = 0; i<CARDS_IN_HAND; i++) {
            int index;
            Card card;
            int attempts = 0;
            do {
                index = generateIndex(deck.getDrawPile());
                card = deck.getDrawPile().get(index);
                attempts++;
                //System.out.println("loooping here");
            }while((card.getType() == CardType.EXPLODING_KITTEN || card.getType() == CardType.DEFUSE)
                    && attempts < deck.getDrawPile().size() - deck.getNumberOfEk() - deck.getNumberOfDefuse());
            deck.getDrawPile().remove(card);
            result.add(card);
        }
        Random random = new Random();
        Card toRemove = null;
        int randomIndex = random.nextInt(result.size() + 1);
        for(Card card : deck.getDrawPile()) {
            if (card.getType() == CardType.DEFUSE){
                result.add(randomIndex,card);
                toRemove = card;
                break;
            }
        }
        deck.getDrawPile().remove(toRemove);
        return result;
    }


    /**
     * Generates a random index within the bounds of the provided list.
     * @param list, hand containing the cards of the player
     * @return An integer representing the randomly generated index.
     * @requires The provided list must not be empty.
     * @throws IllegalStateException if the deck is equal to null
     */
    private int generateIndex(ArrayList<Card> list){
        if(list.isEmpty()){
            throw new IllegalStateException("List in generate index is empty");
        }
        return (int) (Math.random() * list.size());
    }


    /**
     * Checks if the hand contains a card of the specified card type.
     * @param cardType The card type to check for in the hand.
     * @return true if the hand contains a card of the specified type; otherwise, false.
     */
    public boolean containsCardType(CardType cardType) {
        for (Card card : cards) {
            if (card.getType() == cardType) {
                return true;
            }
        }
        return false;
    }


    /**
     * Adds a new card to the hand
     * @param card The Card object to be added to the hand.
     */
    public void addCard(Card card){
        cards.add(card);
    }


    /**
     * Clears all cards from the hand
     * @ensures The hand is empty after this operation.
     */
    public void clearHand(){
        cards.clear();
    }


   /**
    * Removes a specified card from the hand.
     * @param card, the card to be removed from the hand.
    * @ensures The specified card is removed from the hand if it exists.
    */
    public void removeCard(Card card){
        if(!cards.isEmpty()){
            cards.remove(card);
        }
    }


    /**
     * Finds and returns the first card of the specified card type in the hand.
     * @param type The card type to search for in the hand.
     * @return The card of the specified type if found; otherwise, null.
     */
    public Card findCard(CardType type) {
        for (Card card : cards) {
            if (card.getType() == type) {
                return card;
            }
        }
        return null;
    }


    /**
     * Prints a string representation of the hand, if cursed,
     * all cards will simply state "BLIND", otherwise each
     * card is visible.
     * @return result, the list of cards in the hand
     */
    @Override
    public String toString(){
        String result = "";
        if (cards.isEmpty()) result += "empty (not good)";
        if(isCursed()){
            result += "You have these cards in your hand: ";
            for (int i=0; i<cards.size(); i++) {
                if(i == cards.size() - 1) result +="BLIND"+".\n";
                else result += "BLIND"+", ";
            }
        }
        else{
            result +=Server.BasicCommands.SHOW_HAND+Protocol.ARGUMENT_SEPARATOR;
            for (int i=0; i<cards.size(); i++) {
                if(i == cards.size() - 1) result +=cards.get(i).getName();
                else result += cards.get(i).getName()+Protocol.ELEMENT_SEPARATOR;
            }
        }
        return result;
    }
}