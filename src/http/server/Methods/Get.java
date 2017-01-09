package http.server.Methods;

import http.server.Client;

import java.io.IOException;

/**
 * Created by Kévin on 05/12/2016.
 * Envoie le contenu du fichier demandé s'il existe et, si la queryString n'est pas vide, affiche son contenu.
 */
public class Get extends Method {

    public Get(Client c) {
        super(c);
    }

    @Override
    public void execute() throws IOException {

        sendFile();

        printQueryString();
    }
}
