package Réseau;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Communication {

    //cspell:ignore serversocket
    public static boolean estServeur = true;

    private static Socket socket;
    private static ServerSocket serversocket;
    private static DataInputStream input;
    private static DataOutputStream output;
    private static String ip;
    private static final long ID = System.currentTimeMillis();

    private static class ConnexionServeurThread extends Thread{
        public ServerSocket serverSocket;
        public Socket connection;
        public boolean estConnecté;

        @Override
        public void run() {
            try{
                serverSocket = new ServerSocket(5000);
                while(true){
                    connection = serverSocket.accept();
                    System.out.println("Client potentiel connecté.");
                    if(authentifierClient(connection)){
                        System.out.println("La connection correspond à un client Battleship.");
                        estConnecté = true;
                        break;
                    }else{
                        System.out.println("La connection ne correspond pas à un client Battleship.");
                        connection.close();
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        };
    };

    private static class AttenteRéponse extends Thread{
        public boolean estEnAttente = false;
        public boolean aRépondus = false;
        public String réponseString;
        public int réponseInt;
        public byte[] réponseByteListe = new byte[20];
        public boolean communicationsCoupés = false;

        private int typeRéponse = 0;
        private boolean continuer = true;
        private DataInputStream input;
        private Socket socket;
        public AttenteRéponse(Socket socket, DataInputStream input){
            super();
            this.input = input;
            this.socket = socket;
        }

        @Override
        public void run(){
            System.out.println("Début de AttenteRéponse");
            while(continuer){
                if (estEnAttente){
                    try{
                        System.out.println("Attente d'une réponse commencé");
                        socket.setSoTimeout(100000);
                        switch (typeRéponse) {
                            case 0:{
                                réponseString = input.readUTF();
                                System.out.println("AttenteRéponse a reçus une réponse "+réponseString);
                                break;
                            }
                            case 1:{
                                réponseInt = input.readInt();
                                System.out.println("AttenteRéponse a reçus une réponse "+réponseInt);
                                break;
                            }
                            case 2:{
                                input.readFully(réponseByteListe);
                                System.out.println("AttenteRéponse a reçus une réponse "+réponseByteListe);
                                break;
                            }
                        }
                        aRépondus = true;
                        estEnAttente = false;
                    }catch (EOFException e){
                        continuer = false;
                        communicationsCoupés = true;
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }else{
                    try{
                        //System.out.println("AttenteRéponse en veille");
                        Thread.sleep(100);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }

        public void attendreRéponse(int type){
            System.out.println("Commence à attendre une réponse");
            estEnAttente = true;
            aRépondus = false;
            this.typeRéponse = type;
        }

        public void attendreRéponse(int type, int longueur){
            System.out.println("Commence à attendre une réponse");
            réponseByteListe = new byte[longueur];
            estEnAttente = true;
            aRépondus = false;
            this.typeRéponse = type;
        }
    }

    private static AttenteRéponse attenteRéponse;
    
    public static void Connecter(){
        try{
            ip = "192.168.0.1";
            // System.out.println("Mon ip : "+ip);
            String[] ipPart = ip.split("\\.");
            ConnexionServeurThread connexionServeurThread = new ConnexionServeurThread();
            connexionServeurThread.start();
            for (int i = 0; i < 256; i++){
                try{
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(new String(ipPart[0]+"."+ipPart[1]+"."+ipPart[2]+"."+i),5000),100);
                    System.out.println("L'adresse "+ipPart[0]+"."+ipPart[1]+"."+ipPart[2]+"."+i+" répond. Test d'authentification.");
                    if(authentifierServeur(socket)){
                        System.out.println("Hôte trouvé à "+ipPart[0]+"."+ipPart[1]+"."+ipPart[2]+"."+i);
                        input = new DataInputStream(socket.getInputStream());
                        output = new DataOutputStream(socket.getOutputStream());
                        estServeur = false;
                        break;
                    } else {
                        System.out.println("L'adresse ne correspond pas à un hôte Battleship.");
                    }
                }catch(Exception e){
                    //System.out.println("Adresse : "+ipPart[0]+"."+ipPart[1]+"."+ipPart[2]+"."+i+" non disponible");
                }
                synchronized(connexionServeurThread){
                    if (connexionServeurThread.estConnecté){
                        System.out.println("Un client s'est connecté.");
                        serversocket = connexionServeurThread.serverSocket;
                        socket = connexionServeurThread.connection;
                        connexionServeurThread.interrupt();
                        input = new DataInputStream(socket.getInputStream());
                        output = new DataOutputStream(socket.getOutputStream());
                        break;
                    }else{
                        //System.out.println("Toujours aucun client ne s'est connecté.");
                    }
                }
            }
            if(!socket.isConnected()){
                System.out.println("Aucun hôte trouvé. Création du serveur et attente d'un client.");
                synchronized(connexionServeurThread){
                    serversocket = connexionServeurThread.serverSocket;
                    connexionServeurThread.interrupt();
                }
                System.out.println("Serveur créé à l'adresse :"+serversocket.getInetAddress().toString()+", attente du client...");
                while (true){
                    socket = serversocket.accept();
                    System.out.println("Client potentiel connecté.");
                    if(authentifierClient(socket)){
                        System.out.println("La connection correspond à un client Battleship.");
                        input = new DataInputStream(socket.getInputStream());
                        output = new DataOutputStream(socket.getOutputStream());
                        break;
                    }else{
                        System.out.println("La connection ne correspond pas à un client Battleship.");
                        socket.close();
                    }
                }
            }

            attenteRéponse = new AttenteRéponse(socket, input);
            attenteRéponse.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static boolean authentifierServeur(Socket connection){
        try{
            DataInputStream input = new DataInputStream(connection.getInputStream());
            DataOutputStream output = new DataOutputStream(connection.getOutputStream());
            System.out.println("Envoie de la demande d'authentification.");
            Thread.sleep(100);
            output.writeUTF("Êtes-vous un hôte Battleship?");
            System.out.println("Attente de la réponse");
            socket.setSoTimeout(200);
            String réponse = input.readUTF();
            if (!réponse.equals("Oui je suis un hôte Battleship.")){
                System.out.println("Réponse invalide.");
                return false;
            }
            System.out.println("Réponse valide. Envoie de l'identifiant.");
            output.writeLong(ID);
            System.out.println("Attente de la réponse");
            socket.setSoTimeout(200);
            boolean estIdentique = input.readInt()==0;
            System.out.println(estIdentique?"Réponse valide : le serveur ne correspond pas à soi-même.":"Réponse invalide : le serveur correspond à soi-même.");
            return estIdentique;

        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private static boolean authentifierClient(Socket connection){
        try{
            DataInputStream input = new DataInputStream(connection.getInputStream());
            DataOutputStream output = new DataOutputStream(connection.getOutputStream());
            System.out.println("Attente de la demande du client.");
            socket.setSoTimeout(200);
            String demande = input.readUTF();
            if (!demande.equals("Êtes-vous un hôte Battleship?")){
                System.out.println("Demande invalide, le client n'est pas un client Battleship");
                return false;
            } 
            System.out.println("Demande valide, envoie de la réponse");
            output.writeUTF("Oui je suis un hôte Battleship.");
            System.out.println("Attente de l'identifiant.");
            socket.setSoTimeout(200);
            if(input.readLong()==ID){
                System.out.println("Identifiant invalide : le client est soi-même.");
                output.writeInt(1);
                return false;
            } else {
                System.out.println("Identifiant valide.");
                output.writeInt(0);
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static void indiquerPlacementTerminé(){
        try{
            System.out.println("J'informe l'adversaire que j'ai terminé de placer mes pièces.");
            output.writeUTF("Placement Complété");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void attendrePlacementTerminé(){
        System.out.println("J'attend que l'adversaire m'informe qu'il a terminé de placer ses pièces");
        synchronized(attenteRéponse){
            attenteRéponse.attendreRéponse(0);
        }
    }

    public static boolean placementOpposantTerminé(){
        synchronized(attenteRéponse){
            if(attenteRéponse.aRépondus){
                if (!attenteRéponse.réponseString.equals("Placement Complété")){
                    System.out.println("Communication.placementOpposantTerminé [Erreur] : La réponse n'est pas celle attendue : "+attenteRéponse.réponseString);
                    return false;
                }
                System.out.println("L'adversaire a terminé de placer ses pièces");
                return true;
            }else{
                //System.out.println("L'adversaire n'a pas répondus");
                return false;
            }
        }
    }

    public static void envoyerPlateau(byte[] plateau){
        System.out.println("J'envoie mon plateau");
        try {
            output.write(plateau);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void attendrePlateau(){
        System.out.println("J'attend son plateau");
        synchronized(attenteRéponse){
            attenteRéponse.attendreRéponse(2,10);
        }
    }

    public static byte[] plateauReçus(){
        synchronized(attenteRéponse){
            if(attenteRéponse.aRépondus){
                System.out.println("Plateau reçus");
                return attenteRéponse.réponseByteListe;
            }else{
                return null;
            }
        }
    }

    public static void envoyerCoup(int pos){
        try{
            System.out.println("Envoie du coup à l'adversaire");
            output.writeInt(pos);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void attendreCoup(){
        synchronized(attenteRéponse){
            System.out.println("Attente du coup de l'adversaire");
            attenteRéponse.attendreRéponse(1);
        }
    }

    public static int coupReçus(){
        synchronized(attenteRéponse){
            if(attenteRéponse.aRépondus){
                System.out.println("Reçus un coup de l'adversaire.");
                return attenteRéponse.réponseInt;
            }else{
                return -1;
            }
        }
    }

    public static void couperCommunication(){
        synchronized(attenteRéponse){
            attenteRéponse.continuer = false;
            attenteRéponse.interrupt();
        }
        try {
            System.out.println("Coupure des communications");
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean communicationsCoupés(){
        if (attenteRéponse.communicationsCoupés){
            try {
                System.out.println("Les communications ont été coupées");
                input.close();
                output.close();
                socket.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return true;
            }
        }
        return false;
    }
}
