package http.server.Methods;

import http.server.Client;
import http.server.Config;
import http.server.ResponseType;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Kévin on 05/12/2016.
 */
public abstract class Method {

    /**
     * Client géré par la méthode HTTP en cours d'éxécution.
     */
    protected Client client;

    /**
     * Liste des paramètres envoyés par la queryString
     */
    protected HashMap<String, String> queryString;

    /**
     * Initialise les attributs et génère la liste des paramètres du queryString
     * @param c
     */
    public Method(Client c) {
        client = c;

        try {
            HashMap<String, String> requestHeader = client.getRequestHeader();
            String query = (requestHeader.containsKey("querystring")) ? requestHeader.get("querystring") : "";

            queryString = decodeQueryString(query);
        } catch (IOException e) {
            e.printStackTrace();

            queryString = new HashMap<String, String>();
        }

    }

    /**
     * Comme son nom l'indique, exécute la méthode.
     * @throws IOException
     */
    public abstract void execute() throws IOException;

    /**
     * Permet d'insérer dans le corps de la réponse HTTP la liste des paramètres envoyés dans le queryString
     */
    protected void printQueryString() {
        printParameters(queryString, "Donnees QueryString :");
    }

    /**
     * Permet de formatter en HTML un dictionnaire (HashMap) de paramètres, en fournissant un titre.
     * La méthode insère directement ce dictionnaire dans le corps de la réponse HTTP
     * @param parameters Dictionnaire des paramètres à formatter
     * @param title Titre
     */
    protected void printParameters(HashMap<String, String> parameters, String title) {
        if (parameters.size() > 0) {
            // Si on a un dictionnaire non vide...
            client.addResponseLine("<h1>"+title+"</h1>");
            client.addResponseLine("<ul>");

            // On commence à insérer la liste des paramètres dans le flux
            // et également à l'afficher dans la sortie du serveur (Debug)

            System.out.println("[Method] DATA PARAMS:");
            Iterator it = parameters.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                System.out.println(pair.getKey() + ": "+pair.getValue());
                client.addResponseLine("<li><b>"+pair.getKey()+"</b>: "+pair.getValue()+"</li>");
            }

            client.addResponseLine("</ul>");

            System.out.println("----------------------------");
        } else {
            // Sinon, on affiche sur la sortie serveur.
            System.out.println("[Method] Aucun paramètre n'a été fourni.");
        }
    }

    /**
     * Ouvre un fichier selon le chemin indiqué, et place dans le flux de sortie son contenu dans le
     * corps de réponse HTTP (avec comme en-tête HTTP/1.1 200 OK et Content-Type: text/html).
     * Si le fichier n'est pas trouvé, l'entête "404 Not Found" est défini.
     * @return Le fichier a-t-il pu être envoyé ?
     * @throws IOException
     */
    protected boolean sendFile() throws IOException {
        String path = client.getRequestHeader().get("path");
        if (path.equals("/")) {
            path = "/index.html";
        }

        try {
            BufferedReader fileReader = readFile(path);

            // On définit l'en-tête de réponse comme étant HTTP/1.1 200 OK, avec du contenu supplémentaire.
            client.setResponseHeader(ResponseType.OK, "Content-Type: text/html\r\n");

            while(fileReader.ready()) {
                client.addResponseLine(fileReader.readLine());
            }

            return true;
        } catch (FileNotFoundException e) { // On catch l'exception pour envoyer le 404

            System.out.println("[Method] Fichier "+path+" inrouvable.");
            client.setResponseHeader(ResponseType.NOT_FOUND);

            return false;
        }
    }

    /**
     * Ouvre un fichier situé dans le dossier dédié aux fichiers web du serveur et
     * renvoie son flux de sortie.
     * @param webPath Chemin "web" vers le fichier, qui sera converti en chemin système.
     * @return Flux de sortie du fichier désiré.
     * @throws FileNotFoundException
     */
    protected BufferedReader readFile(String webPath) throws FileNotFoundException {

        FileReader fileReader = new FileReader(getSysPath(webPath)); // Avec IDEA, le dossier courant est la racine du projet
        BufferedReader reader = new BufferedReader(fileReader);

        System.out.println("[Method] Fichier "+webPath+" chargé.");

        return reader;

    }

    /**
     * À partir du chemin "web" (ex: /index.html), génère le chemin système associé (ex: html/index.html)
     * @param webPath Chemin web
     * @return Chemin Système
     */
    public static String getSysPath(String webPath) {
        if (webPath.charAt(0) != '/') {
            webPath = '/'+webPath;
        }

        return Config.HTML_DIRECTORY+webPath;
    }

    /**
     * À partir d'une chaîne de caractère contenant les paramètres, Génère un dictionnaire clé/valeur
     * correspondant à la liste des paramètres.
     * @param query Chaîne formaté au même format qu'une queryString
     * @return Dictionnaire clé/valeur des paramètres.
     */
    public static HashMap<String, String> decodeQueryString(String query) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        if (!query.isEmpty()) {
            String[] rawParams = query.split("&");
            String[] paramData;

            if (rawParams.length > 0) {
                for (String rawParam: rawParams) {
                    paramData = rawParam.split("=");
                    try {
                        parameters.put(URLDecoder.decode(paramData[0], "UTF-8"), URLDecoder.decode(paramData[1], "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace(); //  Vraiment la loose si ça arrive...
                    }
                }
            }
        }

        return parameters;
    }

    /**
     * En fonction du type de méthode demandé, renvoie un objet de la classe correspondante.
     * @param type Type de méthode, en chaîne de caractère.
     * @param client Client en cours de traitement
     * @return Objet de la classe correspondant à la méthode demandée.
     * @throws UnknownMethodException
     */
    public static Method factory(String type, Client client) throws UnknownMethodException
    {
        if (type != null) {
            type = type.toLowerCase();
            if (type.equals("get")) {
                return new Get(client);
            } else if (type.equals("post")) {
                return new Post(client);
            } else if (type.equals("put")) {
                return new Put(client);
            } else if (type.equals("delete")) {
                return new Delete(client);
            }
        }

        throw new UnknownMethodException("Type de requête "+type+" non-implémenté.");
    }

    /**
     * Exception utilisée en cas de méthode demandée inconnue.
     */
    public static class UnknownMethodException extends Exception {
        public UnknownMethodException(String message) {
            super(message);
        }
    }

}
