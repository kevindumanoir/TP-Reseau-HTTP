package http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by Kévin on 10/12/2016.
 * Représente un client, son socket, avec ses flux d'entrée et de sortie.
 * On y stocke les en-têtes et le corps de la requête.
 * Dispose d'un petit système permettant d'éviter d'envoyer deux fois un en-tête de réponse.
 */
public class Client {

    /**
     * Socket du client
     */
    private Socket socket;

    /**
     * Flux d'entrée
     */
    private BufferedReader in;

    /**
     * Flux de sortie
     */
    private PrintWriter out;

    /**
     * Dictionnaire des en-têtes de requête
     */
    private HashMap<String, String> requestHeader;

    /**
     * Corps de la requête
     */
    private String requestBody;

    /**
     * Drapeau d'envoi de l'en-tête de réponse
     */
    private boolean setResponseHeader = false;

    public Client(Socket s) throws IOException {

        System.out.println("[Client] Handling new user - "+s.getInetAddress().getHostAddress()+":"+s.getPort());

        socket = s;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream());
    }

    /**
     * À partir du flux d'entrée, récupère le dictionnaire clé/valeur de l'en-tête de la requête.
     * Pour le cas particulier des paramètres envoyés en queryString, ces derniers sont stockés
     * dans le format brut dans une entrée du dictionnaire. Cette entrée a pour clé "querystring".
     * Le chemin "web" vers le fichier demandé a pour clé associée "path".
     * Enfin, le type de méthode demandée par le client est stockée dans une entrée ayant pour clé "method".
     * @return Dictionnaire de l'en-tête de la requête.
     * @throws IOException
     */
    public HashMap<String, String> getRequestHeader() throws IOException {
        if (requestHeader == null) {

            requestHeader = new HashMap<String, String>();
            String str = ".";

            while (str != null && !str.equals("")) { // Tant que l'on est pas sur une ligne vide (cad la fin de l'en-tête)
                str = in.readLine();

                System.out.println(str);
                if (str != null && !str.equals("")) {
                    if (str.contains("HTTP/1.1")) { // Si la ligne actuelle renseigne la méthode demandée et le chemin (ex: GET / HTTP/1.1)

                        String[] requestType = str.split("\\s+"); // On scinde la chaîne pour récupérer les informations importantes

                        if (requestType.length == 3) {

                            requestHeader.put("method", requestType[0]); // On stocke la méthode demandée

                            if (requestType[1].contains("?")) { // Si une querystring est présente dans le chemin

                                // En cas d'envoi de variables dans la reqûete... (Ex : index.html?var1=true)
                                String[] getPath = requestType[1].split("\\?");

                                // On traite ce cas et on stocke la querystring brute dans le dictionnaire.

                                requestType[1] = getPath[0];
                                requestHeader.put("querystring", getPath[1]);
                            }

                            requestHeader.put("path", requestType[1]);
                        }

                    } else if (str.contains(":")) {
                        // S'il s'agit d'un composant standard de l'en-tête, on le stocke directement.

                        String[] request = str.split(":");

                        if (request.length == 2) {
                            requestHeader.put(request[0].trim().toLowerCase(), request[1].trim());
                        }
                    }
                }
            }
        }

        return requestHeader;
    }

    /**
     * Récupère, en fonction de la valeur de l'en-tête "Content-Length", le corps de la requête.
     * Une fois le corps retrouvé, il est mis en cache si cette méthode est appelée plusieurs fois.
     * @return Corps de la requête
     * @throws IOException
     */
    public String getRequestBody() throws IOException {
        if (requestBody == null) {
            requestBody = "";

            HashMap<String, String> header = getRequestHeader();

            if (header.containsKey("content-length")) {
                int length = Integer.parseInt(header.get("content-length"));

                int a;
                for (int i = 0; i < length; i++) {
                    a = in.read();
                    if (a >= 0) requestBody += (char)a;
                }
            }
        }

        return requestBody;
    }

    /**
     * Insère dans le flux de sortie l'en-tête de réponse.
     * @param response
     */
    public void setResponseHeader(ResponseType response) {
        if (!setResponseHeader) {
            out.println(response.toString());
            out.println();

            setResponseHeader = true;
        }
    }
    /**
     * Insère dans le flux de sortie l'en-tête de réponse avec des paramètres supplémentaires.
     * @param response
     * @param details
     */
    public void setResponseHeader(ResponseType response, String details) {
        if (!setResponseHeader) {
            out.println(response.toString());
            out.println(details);
            out.println();

            setResponseHeader = true;
        }
    }

    /**
     * Insère une ligne dans le flux de sortie de la connexion courante.
     * @param str
     */
    public void addResponseLine(String str) {
        out.println(str);
    }

    /**
     * Vide les mémoires tempons et envoie les données, puis clos la connexion.
     */
    public void endTransaction() {

        System.out.println("[Client] Ending transaction for user "+socket.getInetAddress().getHostAddress()+":"+socket.getPort());

        out.println();
        out.flush();
        out.close();

        try {
            in.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("ERREUR DE FIN DE TRANSACTION");
            e.printStackTrace();
        }
    }
}
