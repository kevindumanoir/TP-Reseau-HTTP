package http.server;

/**
 * Created by Kévin on 11/12/2016.
 * Représente la liste des Réponses possibles à une transaction par le serveur.
 * On y lie un code. Le message qui lui est associé est déduit du nom.
 */
public enum ResponseType {
    OK(200),
    CREATED(201),
    NOT_FOUND(404),
    FORBIDDEN(403),
    ERROR(500),
    GATEWAY_TIMEOUT(504),
    NO_CONTENT(204),
    BAD_REQUEST(400);

    int code;
    String message;

    ResponseType(int code) {
        this.code = code;
        this.message = this.name().replace('_', ' ');
    }

    public String toString() {
        System.out.println("[ResponseType] Sending "+name());
        return "HTTP/1.1 "+code+" "+message;
    }
}