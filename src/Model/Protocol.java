package Model;

/** Class for defining client-server protocol for
 * the game Exploding Kittens.
 * Project Module 2
 * @author Harveer Singh, Andrei Dorneanu
 */
public class Protocol {
    public static final String ARGUMENT_SEPARATOR = "~";
    public static final String ELEMENT_SEPARATOR =",";
    /**Errors
     * The following exceptions are a list of responses meant to be displayed
     * by the server when there is unexpected behavior from the client.
     */
    public enum Error{
        E01("Unknown Command."),
        E02("Name already in use."),
        E03("LOBBY FULL - Too many players in the lobby."),
        E04("SERVER FULL - Too many players connected on the server."),
        E05("Not enough people to start a game."),
        E06("There are not any computer players connected."),
        E07("Card not in hand."),
        E09("Flags don’t match.");
        private String description;

        /**
         * @return the text error that comes as a result of unexpected player
         * behavior.
         */
        public String getDescription(){
            return this.description;
        }

        /**
         * @param description is used to create each error with a meaningful
         * message, so that the player knows what is expected of him/her
         */
        Error(String description){
            this.description = description;
        }
    }
    /**
     * The classes Controller.ClientServerTest.Server and Controller.ClientServerTest.Client contain implementations of
     * this interface Command in the form of enums. The enums are subdivided into
     * the two types of commands, basic and advanced.
     */
    public interface Command{}
}
