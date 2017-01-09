package http.server.Methods;

import http.server.Client;
import http.server.ResponseType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Kévin on 05/12/2016.
 * Permet de supprimer un fichier, s'il existe.
 * Renvoie 200 OK ou 500 en cas d'erreur.
 */
public class Delete extends Method {

    public Delete(Client c) {
        super(c);
    }

    @Override
    public void execute() throws IOException {

        String path = client.getRequestHeader().get("path");

        try {
            deleteFile(path);

            client.setResponseHeader(ResponseType.OK);
        } catch (Exception e) {
            e.printStackTrace();
            client.setResponseHeader(ResponseType.ERROR);
        }

    }

    /**
     * Supprime un fichier dans le dossier web du serveur.
     * Retourne une exception en cas de problème de permission ou d'existance de fichier.
     * @param path Chemin web du fichier à supprimer
     * @throws FileNotExistException
     * @throws PermissionException
     */
    private void deleteFile(String path) throws FileNotExistException, PermissionException {
        path = getSysPath(path);
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotExistException();
        }

        if (!file.delete()) {
            throw new PermissionException();
        }
    }

    private class FileNotExistException extends Exception {}
    private class PermissionException extends Exception {}

}
