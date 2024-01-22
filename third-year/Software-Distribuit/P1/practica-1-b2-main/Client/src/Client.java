import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;
import Util.Util;

public class Client {

    public Client() {
    }

    public static final String INIT_ERROR = "Client should be initialized with -h <host> -p <port>";

    public static void main(String[] args) throws IOException {

        if (args.length != 4) {
            throw new IllegalArgumentException("Wrong amount of arguments.\n" + INIT_ERROR);
        }

        if (!args[0].equals("-h") || !args[2].equals("-p")) {
            throw new IllegalArgumentException("Wrong argument keywords.\n" + INIT_ERROR);
        }

        int port;

        try {
            port = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("<port> should be an Integer.");
        }

        String host = args[1];
        Socket socket;

        try {
            socket = new Socket(host, port);
            System.out.println("Client connected to server");
            Util util = new Util(socket);
            GameClient gameClient = new GameClient(util);
            gameClient.play();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Proxy has invalid type or null:\n" + e.getMessage());
        } catch (SecurityException e) {
            throw new SecurityException("Connection to the proxy denied for security reasons:\n" + e.getMessage());
        } catch (UnknownHostException e) {
            throw new RuntimeException("Host is Unknown:\n" + e.getMessage());
        } catch (SocketTimeoutException e) {
            throw new SocketTimeoutException("Timeout:\n" + e.getMessage());

        } catch (IOException e) {
            throw new RuntimeException("I/O Error when creating the socket:\n" + e.getMessage() + ". Is the host listening?");
        }
    }
}
