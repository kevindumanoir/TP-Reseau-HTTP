# TP Réseau - Serveur HTTP Java

Comme ce titre l'indique, ce programme est un serveur HTTP, basé sur le langage de programmation Java et l'utilisation des Sockets.

NB: Ce projet a été réalisé dans le cadre des cours de programmation Réseau 3IF à l'INSA Lyon.

## Utilisation

Afin de compiler ce programme, je vous recommande l'utilisation de l'**IDE IntelliJ IDEA** (Gratuit pour les étudiants !)

Le main se situe dans le fichier `WebServer.java`

Une **JavaDoc** détaillée est disponible dans le dossier doc/, en ouvrant le fichier index.html.

**ATTENTION** - Le code peut s'avérer complexe pour quelqu'un n'ayant pas l'habitude du Java. Néanmoins, j'ai tenté de commenter ce qui me semblait délicat.
**Il n'y a pas qu'une manière de réaliser un serveur HTTP sous java, ni même de limite dans la complexité qu'on peut atteindre.**

## Concepts de réalisation

Ce serveur est réalisé selon plusieurs concepts. Le plus important étant d'attribuer à chaque méthode HTTP implémentée (GET, POST, PUT, DELETE...) une classe qui lui est propre.

Ces classes héritent toutes de la classe abstraite `Method`, qui centralise et généralise des fonctionnalités nécessaires pour la plupart des méthodes HTTP.

Petit rappel, une transaction HTTP (à mon sens) = une connexion au serveur, envoi de la requête, attente de la réponse, réception de la réponse, fermeture de la connexion.

Ainsi, on peut résumer une transaction avec le serveur de la manière suivante :

1. Le client établit une connexion avec le serveur (Protocole TCP, Socket)
2. Le serveur accepte la connexion, crée un objet pour représenter le client et lance un thread dédié pour la transaction du client. Ce thread est chargé d'interpréter la requête du client, et de lui répondre.
3. Le Thread dédié appelle les méthodes permettant de traiter l'entête de la requête. Il utilise alors une _factory_ * pour récupérer l'objet associé à la méthode utilisée par le client.
4. La méthode est exécutée. Le comportement varie en fonction de chaque classe.
5. Le Thread met fin à la transaction, puis s'interrompt.

\* Factory: en gros, une usine à objet, en fonction de la chaîne de caractère, fournit l'objet correspondant: get -> Objet `Get` généré, etc.

Les entêtes de réponses répondent tous à la même logique: d'abord un message de status (HTTP/1.1 200 OK - 404 Not Found ...) puis ensuite des données supplémentaires permettant la bonne gestion des données envoyées.

Ces messages de status, étant scrupuleusement définis par le [W3C](https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html), sont représentés sous la forme d'un enumérable (coucou Thomas) nommé `ResponseType`.

Il n'y a rien de très compliqué dans cette classe, gardez juste en tête qu'elle permet d'avoir qu'une liste particulière d'objets accessibles assez simplement dans le code (Vive les enums !)

J'expliquerai en détail (et si le temps me le permet) le fonctionnement de certaines méthodes de la classe `Client` qui peuvent paraître un peu obscur. Néanmoins, lancez le code, fouinez, vous trouverez :-P

## Sens de lecture recommandé

De manière personnelle, je vous recommande de lire le code en mode "Top-down", en commençant par le fichier `WebServer.java`.
Ensuite, entammez le fichier `TransactionThread.java`, puis enfin `Client.java` et les classes Méthodes.

## Questions ? Suggestions ?

N'hésitez pas à m'envoyer un mail à l'adresse suivante pour n'importe quelle question sur le code: kevin.dumanoir@insa-lyon.fr

Je tenterai de répondre avec détail à vos interrogations ou vos suggestions par rapport au code. Comme je l'ai dis précédemment, il n'y a pas qu'une manière de faire, si quelque chose vous bloque, admettez le (ou demandez-moi) et revenez dessus plus tard ;-)

## Licence et usage

Ce code a été réalisé dans un cadre pédagogique et ne peut être utilisé de façon commerciale.
Il est la propriété des étudiants du département informatique de l'INSA Lyon.
