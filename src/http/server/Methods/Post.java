package http.server.Methods;

import http.server.Client;
import http.server.ResponseType;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Kévin on 05/12/2016.
 * Envoie le contenu du fichier demandé, et s'ils sont présents les paramètres passés en POST,
 * et enfin les paramètres de la queryString.
 */
public class Post extends Method {

    private HashMap<String, String> parameters;

    public Post(Client c) {
        super(c);
    }

    @Override
    public void execute() throws IOException {

        if (RetrieveParams()) {
            if (sendFile()) {
                printParameters(parameters, "POST - Donnees du formulaire:");
                printQueryString();

            } else {
                System.out.println("[Post] Fichier introuvable.");
            }
        } else {
            client.setResponseHeader(ResponseType.NO_CONTENT);
        }
    }

    /**
     * Décode les paramètres passés en POST. Si jamais le décodage se passe mal, renvoie false.
     * @return Etat final du décodage: concluant ou non.
     */
    private boolean RetrieveParams() {
        try {
            String requestBody = client.getRequestBody();

            parameters = decodeQueryString(requestBody);
            
            return true;

        } catch (IOException e) {
            System.err.println("[Post] Erreur IO - RequestHeader avec RetrieveParams");
            e.printStackTrace();
        }
        return false;
    }
}
