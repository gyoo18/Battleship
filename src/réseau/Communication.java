package réseau;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import réseau.ConnectéCallback;

public class Communication {

    //cspell:ignore serversocket
    public static boolean estServeur = true;
    public static boolean estConnecté = false;

    private static Socket socket;
    private static ServerSocket serversocket;
    private static DataInputStream input;
    private static DataOutputStream output;
    private static String ip;
    private static final long ID = System.currentTimeMillis();

    private static class AttenteCommunication extends Thread{
        private ArrayList<String> headersReçus = new ArrayList<>();
        private ArrayList<Object> contenuReçus = new ArrayList<>();
        private Socket socket;
        private DataInputStream input;
        private DataOutputStream output;
        private boolean continuerCommunications = true;

        public AttenteCommunication(Socket socket){
            this.socket = socket;
            try{
                this.input = new DataInputStream(socket.getInputStream());
                this.output = new DataOutputStream(socket.getOutputStream());
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void run(){
            while(continuerCommunications){
                try{
                    socket.setSoTimeout(100000);
                    String header = input.readUTF().replace("[", "").replace("]","");
                    headersReçus.add(header);
                    System.out.println("Header reçus : "+headersReçus);
                    String type = header.split(";")[1].split(":")[1];
                    output.writeUTF("Header reçus");
                    socket.setSoTimeout(100000);

                    switch (type){
                        case "string":{
                            String contenu = input.readUTF();
                            contenuReçus.add(contenu);
                            break;
                        }
                        case "char":{
                            Character contenu = input.readChar();
                            contenuReçus.add(contenu);
                            break;
                        }
                        case "byte":{
                            Byte contenu = input.readByte();
                            contenuReçus.add(contenu);
                            break;
                        }
                        case "short":{
                            Short contenu = input.readShort();
                            contenuReçus.add(contenu);
                            break;
                        }
                        case "int":{
                            Integer contenu = input.readInt();
                            contenuReçus.add(contenu);
                            break;
                        }
                        case "long":{
                            Long contenu = input.readLong();
                            contenuReçus.add(contenu);
                            break;
                        }
                        case "float":{
                            Float contenu = input.readFloat();
                            contenuReçus.add(contenu);
                            break;
                        }
                        case "double":{
                            Double contenu = input.readDouble();
                            contenuReçus.add(contenu);
                            break;
                        }
                        case "bool":{
                            Boolean contenu = input.readBoolean();
                            contenuReçus.add(contenu);
                            break;
                        }
                        case "byte[]":{
                            byte[] contenu = input.readAllBytes();
                            ArrayList<Byte> c = new ArrayList<>(contenu.length);
                            for (int i = 0; i < c.size(); i++){
                                c.add(contenu[i]);
                            }
                            contenuReçus.add(c);
                            break;
                        }
                        
                    }
                }catch(Exception e){
                    System.err.println("[ERREUR] AttenteCommunication.");
                    e.printStackTrace();
                }
            }
        }

        public void envoyerString(String nom, String contenu){
            synchronized(this){
                try{
                    this.output.writeUTF("NOM:"+nom+";TYPE:string");
                    this.socket.setSoTimeout(1000);
                    if( this.input.readUTF().compareTo("Header Reçus") != 0 ){
                        System.err.println("[ERREUR] AttenteCommunication.envoyerString : Reçus réponse inattendue");
                        return;
                    }
                    output.writeUTF(contenu);
                }catch(Exception e){
                    System.err.println("[ERREUR] AttenteCommunication.envoyerString");
                    e.printStackTrace();
                }
            }
        }

        public void envoyerChar(String nom, char contenu){
            synchronized(this){
                try{
                    this.output.writeUTF("NOM:"+nom+";TYPE:char");
                    this.socket.setSoTimeout(1000);
                    if( this.input.readUTF().compareTo("Header Reçus") != 0 ){
                        System.err.println("[ERREUR] AttenteCommunication.envoyerString : Reçus réponse inattendue");
                        return;
                    }
                    output.writeChar(contenu);
                }catch(Exception e){
                    System.err.println("[ERREUR] AttenteCommunication.envoyerString");
                    e.printStackTrace();
                }
            }
        }

        public void envoyerByte(String nom, byte contenu){
            synchronized(this){
                try{
                    this.output.writeUTF("NOM:"+nom+";TYPE:byte");
                    this.socket.setSoTimeout(1000);
                    if( this.input.readUTF().compareTo("Header Reçus") != 0 ){
                        System.err.println("[ERREUR] AttenteCommunication.envoyerString : Reçus réponse inattendue");
                        return;
                    }
                    output.writeByte(contenu);
                }catch(Exception e){
                    System.err.println("[ERREUR] AttenteCommunication.envoyerString");
                    e.printStackTrace();
                }
            }
        }

        public void envoyerShort(String nom, short contenu){
            synchronized(this){
                try{
                    this.output.writeUTF("NOM:"+nom+";TYPE:short");
                    this.socket.setSoTimeout(1000);
                    if( this.input.readUTF().compareTo("Header Reçus") != 0 ){
                        System.err.println("[ERREUR] AttenteCommunication.envoyerString : Reçus réponse inattendue");
                        return;
                    }
                    output.writeChar(contenu);
                }catch(Exception e){
                    System.err.println("[ERREUR] AttenteCommunication.envoyerString");
                    e.printStackTrace();
                }
            }
        }

        public void envoyerInt(String nom, int contenu){
            synchronized(this){
                try{
                    this.output.writeUTF("NOM:"+nom+";TYPE:int");
                    this.socket.setSoTimeout(1000);
                    if( this.input.readUTF().compareTo("Header Reçus") != 0 ){
                        System.err.println("[ERREUR] AttenteCommunication.envoyerString : Reçus réponse inattendue");
                        return;
                    }
                    output.writeInt(contenu);
                }catch(Exception e){
                    System.err.println("[ERREUR] AttenteCommunication.envoyerString");
                    e.printStackTrace();
                }
            }
        }

        public void envoyerLong(String nom, long contenu){
            synchronized(this){
                try{
                    this.output.writeUTF("NOM:"+nom+";TYPE:long");
                    this.socket.setSoTimeout(1000);
                    if( this.input.readUTF().compareTo("Header Reçus") != 0 ){
                        System.err.println("[ERREUR] AttenteCommunication.envoyerString : Reçus réponse inattendue");
                        return;
                    }
                    output.writeLong(contenu);
                }catch(Exception e){
                    System.err.println("[ERREUR] AttenteCommunication.envoyerString");
                    e.printStackTrace();
                }
            }
        }

        public void envoyerFloat(String nom, float contenu){
            synchronized(this){
                try{
                    this.output.writeUTF("NOM:"+nom+";TYPE:float");
                    this.socket.setSoTimeout(1000);
                    if( this.input.readUTF().compareTo("Header Reçus") != 0 ){
                        System.err.println("[ERREUR] AttenteCommunication.envoyerString : Reçus réponse inattendue");
                        return;
                    }
                    output.writeFloat(contenu);
                }catch(Exception e){
                    System.err.println("[ERREUR] AttenteCommunication.envoyerString");
                    e.printStackTrace();
                }
            }
        }

        public void envoyerDouble(String nom, double contenu){
            synchronized(this){
                try{
                    this.output.writeUTF("NOM:"+nom+";TYPE:double");
                    this.socket.setSoTimeout(1000);
                    if( this.input.readUTF().compareTo("Header Reçus") != 0 ){
                        System.err.println("[ERREUR] AttenteCommunication.envoyerString : Reçus réponse inattendue");
                        return;
                    }
                    output.writeDouble(contenu);
                }catch(Exception e){
                    System.err.println("[ERREUR] AttenteCommunication.envoyerString");
                    e.printStackTrace();
                }
            }
        }

        public void envoyerBool(String nom, boolean contenu){
            synchronized(this){
                try{
                    this.output.writeUTF("NOM:"+nom+";TYPE:bool");
                    this.socket.setSoTimeout(1000);
                    if( this.input.readUTF().compareTo("Header Reçus") != 0 ){
                        System.err.println("[ERREUR] AttenteCommunication.envoyerString : Reçus réponse inattendue");
                        return;
                    }
                    output.writeBoolean(contenu);
                }catch(Exception e){
                    System.err.println("[ERREUR] AttenteCommunication.envoyerString");
                    e.printStackTrace();
                }
            }
        }

        public void envoyerByteListe(String nom, byte[] contenu){
            synchronized(this){
                try{
                    this.output.writeUTF("NOM:"+nom+";TYPE:byte[]");
                    this.socket.setSoTimeout(1000);
                    if( this.input.readUTF().compareTo("Header Reçus") != 0 ){
                        System.err.println("[ERREUR] AttenteCommunication.envoyerString : Reçus réponse inattendue");
                        return;
                    }
                    output.write(contenu);
                }catch(Exception e){
                    System.err.println("[ERREUR] AttenteCommunication.envoyerString");
                    e.printStackTrace();
                }
            }
        }

        public Object[] obtenirMessage(String nom){
            for (int i = 0; i < headersReçus.size(); i++){
                if (headersReçus.get(i).split(";")[0].split(":")[1].compareTo(nom) == 0){
                    return new Object[]{headersReçus.remove(i),contenuReçus.remove(i)};
                }
            }
            return null;
        }
    }

    public static AttenteCommunication attenteCommunication;

    public static void Connecter(ConnectéCallback connectéCallback){

        class ClientConnecterThread extends Thread {
            public boolean continuerÀChercher = true;
            public Socket socket;
            private ConnectéCallback connectéCallback;

            public ClientConnecterThread(ConnectéCallback connectéCallback){
                this.connectéCallback = connectéCallback;
            }

            @Override
            public void run(){
                try{
                    ip = InetAddress.getLocalHost().toString().split("/")[1];
                }catch(Exception e){
                    System.err.println("[ERREUR] Le programme n'a pas pus lire l'adresse locale.");
                    e.printStackTrace();
                    ip = "127.0.0.1";
                }
                System.out.println("Ip du client : "+ip);
                String[] ipPart = ip.split("\\.");
                for (int i = 0; i < 256; i++){
                    if (!continuerÀChercher){
                        break;
                    }

                    try{
                        socket = new Socket();
                        socket.connect(new InetSocketAddress(ipPart[0]+"."+ipPart[1]+"."+ipPart[2]+"."+i, 5000),100);
                        System.out.println("L'adresse "+ipPart[0]+"."+ipPart[1]+"."+ipPart[2]+"."+i+":5000 répond. Test d'authentification.");
                        if (authentifierServeur(socket)){
                            System.out.println("L'adresse correspond à un hôte.");
                            synchronized(connectéCallback){
                                connectéCallback.connecté();
                            }
                            continuerÀChercher = false;
                        }else{
                            System.out.println("L'adresse ne correspond pas à un hôte.");
                            try{socket.close();}catch(Exception e){}
                            socket = null;
                        }
                    }catch(Exception e){}
                }

                if(socket == null){
                    System.out.println("Le jeu n'a pas pus trouver d'hôte.");
                }
            }
        }

        class ServeurConnecterThread extends Thread {
            public ServerSocket serverSocket;
            public Socket socket;
            public boolean continuerÀAttendre = true;

            private ConnectéCallback connectéCallback;
            public ServeurConnecterThread(ConnectéCallback connectéCallback){
                this.connectéCallback = connectéCallback;
            }

            @Override
            public void run(){
                try{
                    serverSocket = new ServerSocket(5000);
                    while(continuerÀAttendre){
                        socket = serverSocket.accept();
                        System.out.println("Client potentiel connecté.");
                        if(authentifierClient(socket)){
                            System.out.println("La connection correspond à un client.");
                            synchronized(connectéCallback){
                                connectéCallback.connecté();
                            }
                            continuerÀAttendre = false;
                            break;
                        }else{
                            System.out.println("La connection ne correspond pas à un client.");
                            socket.close();
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        class ConnecterThread extends Thread {
            private ConnectéCallback connectéCallback;
            private ClientConnecterThread client;
            private ServeurConnecterThread serveur;

            public ConnecterThread(ConnectéCallback connectéCallback){
                this.connectéCallback = connectéCallback;
            }

            @Override
            public void run(){
                client = new ClientConnecterThread(new ConnectéCallback(){
                    @Override
                    public void connecté(){
                        System.out.println("Je suis devenus un client");
                        Socket socket;
                        synchronized (client) {
                            socket = client.socket;
                        }
                        synchronized (serveur){
                            serveur.continuerÀAttendre = false;
                        }
                        synchronized (Communication.class){
                            Communication.estConnecté = true;
                            Communication.estServeur = false;
                            Communication.socket = socket;
                            Communication.attenteCommunication = new AttenteCommunication(socket);
                            Communication.attenteCommunication.start();
                        }
                        synchronized (connectéCallback) {
                            connectéCallback.connecté();
                        }
                    }
                });

                serveur = new ServeurConnecterThread(new ConnectéCallback(){
                    @Override
                    public void connecté(){
                        System.out.println("Je suis devenus un serveur");
                        Socket socket = null;
                        ServerSocket serverSocket = null;
                        synchronized (serveur) {
                            socket = serveur.socket;
                            serversocket = serveur.serverSocket;
                        }
                        synchronized (serveur){
                            client.continuerÀChercher = false;
                        }
                        synchronized (Communication.class){
                            Communication.estConnecté = true;
                            Communication.estServeur = false;
                            Communication.socket = socket;
                            Communication.serversocket = serverSocket;
                            Communication.attenteCommunication = new AttenteCommunication(socket);
                            Communication.attenteCommunication.start();
                        }
                        synchronized (connectéCallback){
                            connectéCallback.connecté();
                        }
                    }
                });

                client.start();
                serveur.start();
            }
        }

        ConnecterThread connecter = new ConnecterThread(connectéCallback);
        connecter.start();
    }

    private static boolean authentifierServeur(Socket connection){
        try{
            DataInputStream input = new DataInputStream(connection.getInputStream());
            DataOutputStream output = new DataOutputStream(connection.getOutputStream());
            System.out.println("Envoie de la demande d'authentification.");
            Thread.sleep(100);
            output.writeUTF("Êtes-vous un hôte Battleship?");
            System.out.println("Attente de la réponse");
            connection.setSoTimeout(200);
            String réponse = input.readUTF();
            if (!réponse.equals("Oui je suis un hôte Battleship.")){
                System.out.println("Réponse invalide.");
                return false;
            }
            System.out.println("Réponse valide. Envoie de l'identifiant.");
            output.writeLong(ID);
            System.out.println("Attente de la réponse");
            connection.setSoTimeout(200);
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
            connection.setSoTimeout(200);
            String demande = input.readUTF();
            if (!demande.equals("Êtes-vous un hôte Battleship?")){
                System.out.println("Demande invalide, le client n'est pas un client Battleship");
                return false;
            } 
            System.out.println("Demande valide, envoie de la réponse");
            output.writeUTF("Oui je suis un hôte Battleship.");
            System.out.println("Attente de l'identifiant.");
            connection.setSoTimeout(200);
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

    public static void couperCommunication(){
        // synchronized(attenteRéponse){
        //     attenteRéponse.continuer = false;
        //     attenteRéponse.interrupt();
        // }
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
        // if (attenteRéponse.communicationsCoupés){
        //     try {
        //         System.out.println("Les communications ont été coupées");
        //         input.close();
        //         output.close();
        //         socket.close();
        //         return true;
        //     } catch (IOException e) {
        //         e.printStackTrace();
        //         return true;
        //     }
        // }
        return false;
    }

    public static void envoyerString(String nom, String contenu){
        attenteCommunication.envoyerString(nom, contenu);
    }

    public static void envoyerChar(String nom, char contenu){
        attenteCommunication.envoyerChar(nom, contenu);
    }

    public static void envoyerByte(String nom, byte contenu){
        attenteCommunication.envoyerByte(nom, contenu);
    }

    public static void envoyerShort(String nom, short contenu){
        attenteCommunication.envoyerShort(nom, contenu);
    }

    public static void envoyerInt(String nom, int contenu){
        attenteCommunication.envoyerInt(nom, contenu);
    }

    public static void envoyerLong(String nom, long contenu){
        attenteCommunication.envoyerLong(nom, contenu);
    }

    public static void envoyerFloat(String nom, float contenu){
        attenteCommunication.envoyerFloat(nom, contenu);
    }

    public static void envoyerDouble(String nom, double contenu){
        attenteCommunication.envoyerDouble(nom, contenu);
    }

    public static void envoyerBool(String nom, boolean contenu){
        attenteCommunication.envoyerBool(nom, contenu);
    }

    public static void envoyerByteListe(String nom, byte[] contenu){
        attenteCommunication.envoyerByteListe(nom, contenu);
    }

    public static Object[] obtenirMessage(String nom){
        return attenteCommunication.obtenirMessage(nom);
    }
}
