package View;

import java.net.InetAddress;
import java.util.Scanner;


/**
 * The ServerTUI class provides a text-based user interface for the server.
 * It allows the server to display messages, read string and integer input from the console,
 * and obtain an IP address.
 *
 * @author Andrei Cristian Dorneanu - s2327478
 * @author Harveer Singh - s3231046
 */
public class ServerTUI {


    /**
     * Displays a message to the console.
     *
     * @param message The message to be displayed.
     */
    public void update(String message){
        System.out.println(message);
    }


    /**
     * Reads a string input from the console.
     *
     * @return The string entered by the user.
     */
    public String keyboardStringInput(){
        Scanner stringScanner = new Scanner(System.in);
        return stringScanner.nextLine();
    }


    /**
     * Reads an integer input from the console.
     *
     * @return The integer entered by the user.
     */
    public int keyboardIntInput(){
        Scanner intScanner = new Scanner(System.in);
        int number = intScanner.nextInt();
        intScanner.nextLine();
        return number;
    }


    /**
     * Obtains an IP address from the user.
     *
     * @return The InetAddress representing the IP address entered by the user.
     */
    public String getIp(){
        while(true){
            try {
                update("Enter a server IP address: ");
                String userInput=keyboardStringInput();
                InetAddress test =  InetAddress.getByName(userInput);
                return userInput;
            } catch (Exception e) {
                update("Invalid IP address. Please enter a valid IP address.");
            }
        }
    }


    /**
     * Obtains a boolean value from the user (yes or no).
     *
     * @param message The message prompting the user for input.
     * @return True if the user enters "yes," false if the user enters "no."
     */
    public boolean getBoolean(String message){
        while(true){
            update(message);
            String userInput=keyboardStringInput();
            if(userInput.equals("yes")){
                return true;
            } else if(userInput.equals("no")){
                return false;
            } else {
                update("Please enter a value that is yes or no");
            }
        }
    }
}
