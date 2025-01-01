package Test;

import Controller.ClientServer.Client;
import Controller.ClientServer.ComputerClient;
import Controller.ClientServer.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * The {@code ClientServerTest} class contains JUnit tests for the client-server communication
 * in the {@code Client}, {@code ComputerClient}, and {@code Server} classes. It includes tests to
 * check if the server runs successfully and if clients can connect to the server.
 *
 * WARNING: Before running these tests, ensure that the IP and port on
 * {@code Controller.ClientServer.java}, {@code Controller.Client.java}, and {@code Controller.Server.java}
 * are hard-coded and hard-coded correctly.
 *
 * @author Andrei Cristian Dorneanu - s2327478
 * @author Harveer Singh - s3231046
 */
public class ClientServerTest {
    private Server server;


    /**
     * Sets up the server before each test by creating a new instance and starting it in a separate thread.
     */
    @BeforeEach
    public void setUpServer(){
        server = new Server();
        new Thread(server).start();
    }


    /**
     * Tests if the server runs successfully.
     */
    @Test
    public void testIfServerRuns(){
        assertTrue(server.isServerActive());
    }


    /**
     * Tests if clients (Client and ComputerClient) can successfully connect to the server.
     */
    @Test
    public void clientsConnectsToServer(){
        Client client = new Client();
        try {
            client.createConnection();
        }catch (Exception e){
            System.out.println("Error: " + e);
        }
        Thread thread = new Thread(client);
        thread.start();
        assertEquals(client.getServerSock().getPort(), server.getSsock().getLocalPort());

        ComputerClient computerClient = new ComputerClient(0);
        try {
            computerClient.createConnection();
            computerClient.handleHello();
        }catch (Exception e){
            System.out.println("Error: " + e);
        }
        Thread computerThread = new Thread(computerClient);
        computerThread.start();
        assertEquals(computerClient.getServerSock().getPort(), server.getSsock().getLocalPort());
    }
}
