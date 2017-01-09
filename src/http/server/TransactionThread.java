package http.server;

import http.server.Methods.Method;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Kévin on 11/12/2016.
 * Permet de traiter la demande d'un utilisateur dans un Thread séparé.
 */
public class TransactionThread extends Thread {

    /**
     * Client en cours de traitement
     */
    private Client client;

    public TransactionThread(Client c) {
        client = c;
    }

    public void run() {

        try {
            // On récupère sous forme d'un dictionnaire l'en-tête de la demande
            HashMap<String, String> header = client.getRequestHeader();

            try {
                // On récupère la méthode demandée par le client
                Method method = Method.factory(header.get("method"), client);

                // Et on l'exécute en fonction des paramètres à notre disposition: Path, QueryString, Paramètres POST, Body...
                method.execute();

            } catch (Method.UnknownMethodException e) {
                // Mais si aucune méthode ne correspond au mot fourni...
                // Bad Request.
                client.addResponseLine(ResponseType.BAD_REQUEST.toString());
            }

            // On clos la transaction avec le client.
            client.endTransaction();

            interrupt();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
