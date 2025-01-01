package Model;

import Controller.Game;

import java.util.ArrayList;
import java.util.HashSet;


/**
 * @invariant all card names are unique and assigned once to a card
 * Creates cards of a given type and name, each of which are added into
 * the drawPile of the Deck class and used in the game by players.
 */
public class Card {
    private String name;
    private CardType type;


    /**
     * @param type is the card type
     * already been set in the current game deck
     * When a card is created it requires a type and the existing
     * list of used card names. Its name is then set and added to
     * the list of used names to ensure its uniqueness.
     *
     * @author Andrei Cristian Dorneanu - s2327478
     * @author Harveer Singh - s3231046
     */
    public Card(CardType type){
        this.type = type;
        this.name = setName();
    }


    /**
     * Type is the input for createName() which will provide a list of card names of this type
     * @return the randomly chosen card name from the list of available cards of the chosen type
     * @ensures the name that gets returned is not of a card that is already in use
     * @ensures the chosen name is added to the list of used card names so that no two cards are the same
     */
    private String setName(){
        int index;
        String localName;
        do {
            index = (int) (Math.random() * Deck.names.size());
            localName = Deck.names.get(index);
            System.out.println(Deck.names);
            System.out.println(localName);
            System.out.println(type);
        } while (!localName.startsWith(type.name().toLowerCase()));
        Deck.names.remove(localName);
        return localName;
    }


    /**
     *@return the randomly generated name of the card
     */
    public String getName(){
        return name;
    }


    /**
     *@return the type of the card
     */
    public CardType getType(){
        return type;
    }
}