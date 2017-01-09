///A Simple Web Server (WebServer.java)

package http.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

/**
 * Serveur Web supportant (ou en tout cas essaye) les méthodes GET, POST, PUT et DELETE.
 * Basé sur le serveur WebServer conçu par Jeff Heaton.
 */
public class WebServer {

    /**
     * WebServer constructor.
     */
    protected void start() {

        Scanner sc = new Scanner(System.in);

        System.out.println("Le dossier de travail de l'applcation est : "+System.getProperty("user.dir"));
        System.out.println("Le dossier de fichers HTML est : "+System.getProperty("user.dir")+Config.HTML_DIRECTORY);
        System.out.print("Entrez le port d'écoute du serveur HTTP: ");

        int port = sc.nextInt();

        ServerSocket s;

        System.out.println("Serveur HTTP démarré sur le port" + port);
        System.out.println("(press ctrl-c to exit)");
        try {
            // create the main server socket
            s = new ServerSocket(port);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return;
        }

        System.out.println("Waiting for connection");
        for (; ; ) {
            try {
                // wait for a connection
                Socket remote = s.accept();
                // remote is now the connected socket
                System.out.println("Connection, sending data.");

                // On crée un objet représentant l'utilisateur et sa demande
                Client client = new Client(remote);

                // On traite sa demande dans un thread séparé afin de pouvoir
                // continuer à accueillir les requêtes des autres clients.
                new TransactionThread(client).start();

            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }

    /**
     * Start the application.
     *
     * @param args Command line parameters are not used.
     */
    public static void main(String args[]) {
        WebServer ws = new WebServer();
        ws.start();
    }
}
