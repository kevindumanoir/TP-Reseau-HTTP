package http.server.Methods;

import http.server.Client;
import http.server.Config;
import http.server.ResponseType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Kévin on 05/12/2016.
 * Permet de créer un fichier selon le chemin web demandé.
 * Renvoie 200 OK ou 500 en cas d'erreur.
 */
public class Put extends Method {

    public Put(Client c) {
        super(c);
    }

    @Override
    public void execute() throws IOException {

        // On récupère le corps de la requête (cad le contenu du fichier à insérer)
        String data = client.getRequestBody();

        // On récupère le chemin web...
        String path = client.getRequestHeader().get("path");

        try {
            // En on écrit le fichier.
            boolean fileIsNew = writeFile(path, data);

            client.setResponseHeader((fileIsNew) ? ResponseType.CREATED : ResponseType.OK);
        } catch (Exception e) {
            client.setResponseHeader(ResponseType.ERROR);
        }

    }

    /**
     * Permet d'écrire un fichier dans le répertoire web du serveur.
     * En cas de problème d'écriture, soulève une exception.
     * @param path Chemin web vers le fichier.
     * @param data Données à insérer.
     * @return Le fichier existait-il déjà ?
     * @throws FileCreationException
     * @throws IOException
     */
    private boolean writeFile(String path, String data) throws FileCreationException, IOException {
        path = getSysPath(path);
        File file = new File(path);
        boolean newFile = true;
        if (file.exists()) {
            file.delete();
            newFile = false;
        }

        boolean fileCreated = file.createNewFile();

        if (fileCreated) {
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            writer.println(data);
            writer.flush();
            writer.close();
        } else {
            throw new FileCreationException();
        }

        return newFile;
    }

    private class FileCreationException extends Exception {}

}
