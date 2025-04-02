package réseau;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

import réseau.ConnectéCallback;

public class Communication {

    //cspell:ignore serversocket
    public static boolean estServeur = true;
    public static boolean estConnecté = false;
    public static boolean communicationsCoupés = false;

    private static ServerSocket serversocket;
    private static Socket socket;
    private static String ip;
    private static final long ID = System.currentTimeMillis();

    private static class AttenteCommunication extends Thread{
        private ArrayList<String> headersReçus = new ArrayList<>();
        private ArrayList<Object> contenuReçus = new ArrayList<>();
        private Socket socket;
        private boolean continuerCommunications = true;
        private boolean estCanalUtilisé = true;
        private boolean estContrôleDemandé = false;
        private boolean communicationsCoupés = false;

        public AttenteCommunication(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run(){
            DataInputStream input = null;
            DataOutputStream output = null;
            try{
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
            }catch(Exception e){
                e.printStackTrace();
            }
            System.out.println("Écoute démarée à : "+System.currentTimeMillis());
            while(continuerCommunications){
                //System.out.println("Socket status : "+this.socket+", "+this.socket.isOutputShutdown()+", "+this.socket.isOutputShutdown());
                if(estContrôleDemandé){
                    relâcherContrôleCanal();
                    try{Thread.sleep(100);}catch(Exception e){e.printStackTrace();}
                    demanderContrôleCanal();
                }
                try {
                    socket.setSoTimeout(300);
                    if(input.readUTF().compareTo("Communication demandee") != 0){
                        System.err.println("[ERREUR] AttenteCommunication : Reçus message inatendu.");
                    }
                    System.out.println("Reçus demande de communication à "+System.currentTimeMillis());
                    try {Thread.sleep(20);} catch (Exception e) {e.printStackTrace();}
                    output.writeUTF("Communication acceptee");
                    output.flush();
                    System.out.println("Demande de communication acceptée à "+System.currentTimeMillis());
                    lireMessage();
                } catch(IOException e){
                    try{Thread.sleep(100);}catch(Exception f){f.printStackTrace();}
                } 
                catch (Exception e) {
                    System.err.println("[ERREUR] AttenteCommunication.");
                    e.printStackTrace();
                }
            }
        }

        private synchronized void lireMessage(){
            DataInputStream input = null;
            DataOutputStream output = null;
            try{
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                this.socket.setSoTimeout(300);
                String header = input.readUTF();
                System.out.println("Header reçus à "+System.currentTimeMillis()+" : "+header);
                String type = header.split(";")[1].split(":")[1];
                try {Thread.sleep(20);} catch (Exception e) {e.printStackTrace();}
                output.writeUTF("Header recus");
                output.flush();
                System.out.println("Envoyé réponse à : "+System.currentTimeMillis());
                this.socket.setSoTimeout(1000);

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
                        this.socket.setSoTimeout(1000);
                        System.out.println("Reçus byte[]");
                        int longueur = input.readInt();
                        byte[] contenu = new byte[longueur];
                        input.readFully(contenu, 0, longueur);
                        ArrayList<Byte> c = new ArrayList<>(contenu.length);
                        for (int i = 0; i < longueur; i++){
                            c.add(contenu[i]);
                        }
                        contenuReçus.add(c);
                        break;
                    }
                }
                headersReçus.add(header);
                System.out.println("Fini de recevoir le contenu à "+System.currentTimeMillis());
                if(header.split(";")[0].split(":")[1].compareTo("Coupure des communications") == 0 && type.compareTo("bool") == 0){
                    System.out.println("Signal reçus : coupure des communications");
                    continuerCommunications = false;
                    communicationsCoupés = true;
                    socket.close();
                }
            }catch(Exception e){
                System.err.println("[ERREUR] AttenteCommunication.");
                e.printStackTrace();
            }
        }

        private void demanderContrôleCanal(){
            System.out.println("Contrôle du canal demandé à "+System.currentTimeMillis());
            synchronized(this){
                this.estContrôleDemandé = true;
            }
            while(true){
                synchronized(this){
                    if(!this.estCanalUtilisé){
                        this.estCanalUtilisé = true;
                        break;
                    }
                }
                try{
                    Thread.sleep(100);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            synchronized(this){
                this.estContrôleDemandé = false;
            }
            System.out.println("Contrôle du canal obtenu à "+System.currentTimeMillis());
        }

        private void relâcherContrôleCanal(){
            synchronized(this){
                this.estCanalUtilisé = false;
            }
            System.out.println("Contrôle du canal relâché à "+System.currentTimeMillis());
        }

        private synchronized void demanderCommunication(){
            DataInputStream input = null;
            DataOutputStream output = null;
            try{
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
            }catch(Exception e){
                e.printStackTrace();
            }
            while(true){
                try{
                    output.writeUTF("Communication demandee");
                    System.out.println("Demande de communication envoyée à "+System.currentTimeMillis());
                    socket.setSoTimeout(1000);
                    String message = input.readUTF();
                    if (message.compareTo("Communication acceptee") == 0){
                        break;
                    }else if(message.compareTo("Communication demandee") == 0 && Communication.estServeur){
                        System.out.println("Contre-demande de communication reçue. Le serveur accepte à "+System.currentTimeMillis());
                        try {Thread.sleep(300);} catch (Exception e) {e.printStackTrace();}
                        input.readUTF();    // Effacer la deuxième demande du client
                        output.writeUTF("Communication acceptee");
                        System.out.println("Demande communication acceptée à "+System.currentTimeMillis());
                        lireMessage();
                        relâcherContrôleCanal();
                        try {Thread.sleep(100);} catch (Exception e) {e.printStackTrace();}
                        demanderContrôleCanal();
                        continue;
                    }else if(message.compareTo("Communication demandee") == 0 && !Communication.estServeur){
                        System.err.println("[ERREUR] Réponse inatendue à "+System.currentTimeMillis());
                        continue;
                    }else{
                        System.err.println("[ERREUR] Réponse inatendue à "+System.currentTimeMillis());
                        continue;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        public void envoyerString(String nom, String contenu){
            DataInputStream input = null;
            DataOutputStream output = null;
            try{
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
            }catch(Exception e){
                e.printStackTrace();
            }
            demanderContrôleCanal();
            demanderCommunication();
            try{
                while(true){
                    //System.out.println("Socket status : "+this.socket+", "+this.socket.isOutputShutdown()+", "+this.socket.isOutputShutdown()+", "+this.socket.isConnected());
                    try{
                        System.out.println("Envoie de string");
                        output.writeUTF("NOM:"+nom+";TYPE:string;ID:"+Communication.ID);
                        System.out.println("Envoyé header à : "+System.currentTimeMillis());
                        this.socket.setSoTimeout(300);
                        String message = input.readUTF();
                        if( message.compareTo("Header recus") != 0 ){
                            System.err.println("[ERREUR] AttenteCommunication.envoyerString : Reçus réponse inattendue : "+message);
                            continue;
                        }
                        System.out.println("Reçus confirmation à "+System.currentTimeMillis());
                        try{Thread.sleep(20);}catch(Exception e){e.printStackTrace();}
                        output.writeUTF(contenu);
                        output.flush();
                        System.out.println("Envoyé contenu à "+System.currentTimeMillis());
                        relâcherContrôleCanal();
                        return;
                    }catch(IOException e){
                        System.err.println("[ATTENTION] AttenteCommunication.envoyerString : timeout à "+System.currentTimeMillis());
                    }
                }
            }catch(Exception e){
                relâcherContrôleCanal();
                System.err.println("[ERREUR] AttenteCommunication.envoyerString");
                e.printStackTrace();
            }
        }

        public void envoyerChar(String nom, char contenu){
            DataInputStream input = null;
            DataOutputStream output = null;
            try{
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
            }catch(Exception e){
                e.printStackTrace();
            }
            demanderContrôleCanal();
            demanderCommunication();
            try{
                while(true){
                    //System.out.println("Socket status : "+this.socket+", "+this.socket.isOutputShutdown()+", "+this.socket.isOutputShutdown()+", "+this.socket.isConnected());
                    try{
                        System.out.println("Envoie de char");
                        output.writeUTF("NOM:"+nom+";TYPE:char;ID:"+Communication.ID);
                        System.out.println("Envoyé header à : "+System.currentTimeMillis());
                        this.socket.setSoTimeout(300);
                        String message = input.readUTF();
                        if( message.compareTo("Header recus") != 0 ){
                            System.err.println("[ERREUR] AttenteCommunication.envoyerChar : Reçus réponse inattendue : "+message);
                            continue;
                        }
                        System.out.println("Reçus confirmation à "+System.currentTimeMillis());
                        try{Thread.sleep(20);}catch(Exception e){e.printStackTrace();}
                        output.writeChar(contenu);
                        output.flush();
                        System.out.println("Envoyé contenu à "+System.currentTimeMillis());
                        relâcherContrôleCanal();
                        return;
                    }catch(IOException e){
                        System.err.println("[ATTENTION] AttenteCommunication.envoyerChar : timeout à "+System.currentTimeMillis());
                    }
                }
            }catch(Exception e){
                relâcherContrôleCanal();
                System.err.println("[ERREUR] AttenteCommunication.envoyerChar");
                e.printStackTrace();
            }
        }

        public void envoyerByte(String nom, byte contenu){
            DataInputStream input = null;
            DataOutputStream output = null;
            try{
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
            }catch(Exception e){
                e.printStackTrace();
            }
            demanderContrôleCanal();
            demanderCommunication();
            try{
                while(true){
                    //System.out.println("Socket status : "+this.socket+", "+this.socket.isOutputShutdown()+", "+this.socket.isOutputShutdown()+", "+this.socket.isConnected());
                    try{
                        System.out.println("Envoie de byte");
                        output.writeUTF("NOM:"+nom+";TYPE:byte;ID:"+Communication.ID);
                        System.out.println("Envoyé header à : "+System.currentTimeMillis());
                        this.socket.setSoTimeout(300);
                        String message = input.readUTF();
                        if( message.compareTo("Header recus") != 0 ){
                            System.err.println("[ERREUR] AttenteCommunication.envoyerByte : Reçus réponse inattendue : "+message);
                            continue;
                        }
                        System.out.println("Reçus confirmation à "+System.currentTimeMillis());
                        try{Thread.sleep(20);}catch(Exception e){e.printStackTrace();}
                        output.writeByte(contenu);
                        output.flush();
                        System.out.println("Envoyé contenu à "+System.currentTimeMillis());
                        relâcherContrôleCanal();
                        return;
                    }catch(IOException e){
                        System.err.println("[ATTENTION] AttenteCommunication.envoyerByte : timeout à "+System.currentTimeMillis());
                    }
                }
            }catch(Exception e){
                relâcherContrôleCanal();
                System.err.println("[ERREUR] AttenteCommunication.envoyerByte");
                e.printStackTrace();
            }
        }

        public void envoyerShort(String nom, short contenu){
            DataInputStream input = null;
            DataOutputStream output = null;
            try{
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
            }catch(Exception e){
                e.printStackTrace();
            }
            demanderContrôleCanal();
            demanderCommunication();
            try{
                while(true){
                    //System.out.println("Socket status : "+this.socket+", "+this.socket.isOutputShutdown()+", "+this.socket.isOutputShutdown()+", "+this.socket.isConnected());
                    try{
                        System.out.println("Envoie de short");
                        output.writeUTF("NOM:"+nom+";TYPE:short;ID:"+Communication.ID);
                        System.out.println("Envoyé header à : "+System.currentTimeMillis());
                        this.socket.setSoTimeout(300);
                        String message = input.readUTF();
                        if( message.compareTo("Header recus") != 0 ){
                            System.err.println("[ERREUR] AttenteCommunication.envoyerShort : Reçus réponse inattendue : "+message);
                            continue;
                        }
                        System.out.println("Reçus confirmation à "+System.currentTimeMillis());
                        try{Thread.sleep(20);}catch(Exception e){e.printStackTrace();}
                        output.writeShort(contenu);
                        output.flush();
                        System.out.println("Envoyé contenu à "+System.currentTimeMillis());
                        relâcherContrôleCanal();
                        return;
                    }catch(IOException e){
                        System.err.println("[ATTENTION] AttenteCommunication.envoyerShort : timeout à "+System.currentTimeMillis());
                    }
                }
            }catch(Exception e){
                relâcherContrôleCanal();
                System.err.println("[ERREUR] AttenteCommunication.envoyerShort");
                e.printStackTrace();
            }
        }

        public void envoyerInt(String nom, int contenu){
            DataInputStream input = null;
            DataOutputStream output = null;
            try{
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
            }catch(Exception e){
                e.printStackTrace();
            }
            demanderContrôleCanal();
            demanderCommunication();
            try{
                while(true){
                    //System.out.println("Socket status : "+this.socket+", "+this.socket.isOutputShutdown()+", "+this.socket.isOutputShutdown()+", "+this.socket.isConnected());
                    try{
                        System.out.println("Envoie de int");
                        output.writeUTF("NOM:"+nom+";TYPE:int;ID:"+Communication.ID);
                        System.out.println("Envoyé header à : "+System.currentTimeMillis());
                        this.socket.setSoTimeout(300);
                        String message = input.readUTF();
                        if( message.compareTo("Header recus") != 0 ){
                            System.err.println("[ERREUR] AttenteCommunication.envoyerInt : Reçus réponse inattendue : "+message);
                            continue;
                        }
                        System.out.println("Reçus confirmation à "+System.currentTimeMillis());
                        try{Thread.sleep(20);}catch(Exception e){e.printStackTrace();}
                        output.writeInt(contenu);
                        output.flush();
                        System.out.println("Envoyé contenu à "+System.currentTimeMillis());
                        relâcherContrôleCanal();
                        return;
                    }catch(IOException e){
                        System.err.println("[ATTENTION] AttenteCommunication.envoyerInt : timeout à "+System.currentTimeMillis());
                    }
                }
            }catch(Exception e){
                relâcherContrôleCanal();
                System.err.println("[ERREUR] AttenteCommunication.envoyerInt");
                e.printStackTrace();
            }
        }

        public void envoyerLong(String nom, long contenu){
            DataInputStream input = null;
            DataOutputStream output = null;
            try{
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
            }catch(Exception e){
                e.printStackTrace();
            }
            demanderContrôleCanal();
            demanderCommunication();
            try{
                while(true){
                    //System.out.println("Socket status : "+this.socket+", "+this.socket.isOutputShutdown()+", "+this.socket.isOutputShutdown()+", "+this.socket.isConnected());
                    try{
                        System.out.println("Envoie de long");
                        output.writeUTF("NOM:"+nom+";TYPE:long;ID:"+Communication.ID);
                        System.out.println("Envoyé header à : "+System.currentTimeMillis());
                        this.socket.setSoTimeout(300);
                        String message = input.readUTF();
                        if( message.compareTo("Header recus") != 0 ){
                            System.err.println("[ERREUR] AttenteCommunication.envoyerLong : Reçus réponse inattendue : "+message);
                            continue;
                        }
                        System.out.println("Reçus confirmation à "+System.currentTimeMillis());
                        try{Thread.sleep(20);}catch(Exception e){e.printStackTrace();}
                        output.writeLong(contenu);
                        output.flush();
                        System.out.println("Envoyé contenu à "+System.currentTimeMillis());
                        relâcherContrôleCanal();
                        return;
                    }catch(IOException e){
                        System.err.println("[ATTENTION] AttenteCommunication.envoyerLong : timeout à "+System.currentTimeMillis());
                    }
                }
            }catch(Exception e){
                relâcherContrôleCanal();
                System.err.println("[ERREUR] AttenteCommunication.envoyerLong");
                e.printStackTrace();
            }
        }

        public void envoyerFloat(String nom, float contenu){
            DataInputStream input = null;
            DataOutputStream output = null;
            try{
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
            }catch(Exception e){
                e.printStackTrace();
            }
            demanderContrôleCanal();
            demanderCommunication();
            try{
                while(true){
                    //System.out.println("Socket status : "+this.socket+", "+this.socket.isOutputShutdown()+", "+this.socket.isOutputShutdown()+", "+this.socket.isConnected());
                    try{
                        System.out.println("Envoie de float");
                        output.writeUTF("NOM:"+nom+";TYPE:float;ID:"+Communication.ID);
                        System.out.println("Envoyé header à : "+System.currentTimeMillis());
                        this.socket.setSoTimeout(300);
                        String message = input.readUTF();
                        if( message.compareTo("Header recus") != 0 ){
                            System.err.println("[ERREUR] AttenteCommunication.envoyerFloat : Reçus réponse inattendue : "+message);
                            continue;
                        }
                        System.out.println("Reçus confirmation à "+System.currentTimeMillis());
                        try{Thread.sleep(20);}catch(Exception e){e.printStackTrace();}
                        output.writeFloat(contenu);
                        output.flush();
                        System.out.println("Envoyé contenu à "+System.currentTimeMillis());
                        relâcherContrôleCanal();
                        return;
                    }catch(IOException e){
                        System.err.println("[ATTENTION] AttenteCommunication.envoyerFloat : timeout à "+System.currentTimeMillis());
                    }
                }
            }catch(Exception e){
                relâcherContrôleCanal();
                System.err.println("[ERREUR] AttenteCommunication.envoyerFloat");
                e.printStackTrace();
            }
        }

        public void envoyerDouble(String nom, double contenu){
            DataInputStream input = null;
            DataOutputStream output = null;
            try{
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
            }catch(Exception e){
                e.printStackTrace();
            }
            demanderContrôleCanal();
            demanderCommunication();
            try{
                while(true){
                    //System.out.println("Socket status : "+this.socket+", "+this.socket.isOutputShutdown()+", "+this.socket.isOutputShutdown()+", "+this.socket.isConnected());
                    try{
                        System.out.println("Envoie de double");
                        output.writeUTF("NOM:"+nom+";TYPE:double;ID:"+Communication.ID);
                        System.out.println("Envoyé header à : "+System.currentTimeMillis());
                        this.socket.setSoTimeout(300);
                        String message = input.readUTF();
                        if( message.compareTo("Header recus") != 0 ){
                            System.err.println("[ERREUR] AttenteCommunication.envoyerDouble : Reçus réponse inattendue : "+message);
                            continue;
                        }
                        System.out.println("Reçus confirmation à "+System.currentTimeMillis());
                        try{Thread.sleep(20);}catch(Exception e){e.printStackTrace();}
                        output.writeDouble(contenu);
                        output.flush();
                        System.out.println("Envoyé contenu à "+System.currentTimeMillis());
                        relâcherContrôleCanal();
                        return;
                    }catch(IOException e){
                        System.err.println("[ATTENTION] AttenteCommunication.envoyerDouble : timeout à "+System.currentTimeMillis());
                    }
                }
            }catch(Exception e){
                relâcherContrôleCanal();
                System.err.println("[ERREUR] AttenteCommunication.envoyerDouble");
                e.printStackTrace();
            }
        }

        public void envoyerBool(String nom, boolean contenu){
            DataInputStream input = null;
            DataOutputStream output = null;
            try{
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
            }catch(Exception e){
                e.printStackTrace();
            }
            demanderContrôleCanal();
            demanderCommunication();
            try{
                while(true){
                    //System.out.println("Socket status : "+this.socket+", "+this.socket.isOutputShutdown()+", "+this.socket.isOutputShutdown()+", "+this.socket.isConnected());
                    try{
                        System.out.println("Envoie de bool");
                        output.writeUTF("NOM:"+nom+";TYPE:bool;ID:"+Communication.ID);
                        System.out.println("Envoyé header à : "+System.currentTimeMillis());
                        this.socket.setSoTimeout(300);
                        String message = input.readUTF();
                        if( message.compareTo("Header recus") != 0 ){
                            System.err.println("[ERREUR] AttenteCommunication.envoyerBool : Reçus réponse inattendue : "+message);
                            continue;
                        }
                        System.out.println("Reçus confirmation à "+System.currentTimeMillis());
                        try{Thread.sleep(20);}catch(Exception e){e.printStackTrace();}
                        output.writeBoolean(contenu);
                        output.flush();
                        System.out.println("Envoyé contenu à "+System.currentTimeMillis());
                        relâcherContrôleCanal();
                        return;
                    }catch(IOException e){
                        System.err.println("[ATTENTION] AttenteCommunication.envoyerBool : timeout à "+System.currentTimeMillis());
                    }
                }
            }catch(Exception e){
                relâcherContrôleCanal();
                System.err.println("[ERREUR] AttenteCommunication.envoyerBool");
                e.printStackTrace();
            }
        }

        public void envoyerByteListe(String nom, byte[] contenu){
            DataInputStream input = null;
            DataOutputStream output = null;
            try{
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
            }catch(Exception e){
                e.printStackTrace();
            }
            demanderContrôleCanal();
            demanderCommunication();
            try{
                while(true){
                    System.out.println("Envoie de byte[]");
                    output.writeUTF("NOM:"+nom+";TYPE:byte[];ID:"+Communication.ID);
                    System.out.println("Envoyé header à : "+System.currentTimeMillis());
                    this.socket.setSoTimeout(1000);
                    String message = input.readUTF();
                    if( message.compareTo("Header recus") != 0 ){
                        System.err.println("[ERREUR] AttenteCommunication.envoyerByteListe : Reçus réponse inattendue : "+message);
                        continue;
                    }
                    System.out.println("Reçus confirmation à "+System.currentTimeMillis());
                    try{Thread.sleep(20);}catch(Exception e){e.printStackTrace();}
                    output.writeInt(contenu.length);
                    output.write(contenu);
                    output.flush();
                    System.out.println("Envoyé contenu à "+System.currentTimeMillis());
                    relâcherContrôleCanal();
                    return;
                }
            }catch(Exception e){
                relâcherContrôleCanal();
                System.err.println("[ERREUR] AttenteCommunication.envoyerByteListe");
                e.printStackTrace();
            }
        }

        public Object[] obtenirMessage(String nom){
            synchronized(this){
                for (int i = 0; i < this.headersReçus.size(); i++){
                    if (this.headersReçus.get(i).split(";")[0].split(":")[1].compareTo(nom) == 0){
                        return new Object[]{this.headersReçus.remove(i),this.contenuReçus.remove(i)};
                    }
                }
                return null;
            }
        }

        public boolean communicationsCoupés(){
            return communicationsCoupés;
        }

        public void couperCommunication(){
            envoyerBool("Coupure des communications", true);
            synchronized(this){
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                communicationsCoupés = true;
                continuerCommunications = false;
            }
        }
    }

    private static class ClientConnecterThread extends Thread {
        public boolean continuerÀChercher = true;
        public Socket socket;
        private ConnectéCallback connectéCallback;
        private boolean estÀTuer = false;

        public ClientConnecterThread(ConnectéCallback connectéCallback){
            this.connectéCallback = connectéCallback;
        }

        @Override
        public void run(){
            try{
                Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
                ip = "127.0.0.1"; //InetAddress.getLocalHost().toString().split("/")[1];
                while (e.hasMoreElements()){
                    NetworkInterface n = (NetworkInterface)e.nextElement();
                    Enumeration ee = n.getInetAddresses();
                    while(ee.hasMoreElements()){
                        InetAddress i = (InetAddress) ee.nextElement();
                        if(i.isSiteLocalAddress() && i.toString().split("\\.").length == 4){
                            ip = i.toString().split("/")[1];
                            break;
                        }
                    }
                    if(ip.compareTo("127.0.0.1") != 0){
                        break;
                    }
                }
            }catch(Exception e){
                System.err.println("[ERREUR] Le programme n'a pas pus lire l'adresse locale.");
                e.printStackTrace();
                ip = "127.0.0.1";
            }
            System.out.println("Ip du client : "+ip);
            String[] ipPart = ip.split("\\.");
            for (int i = 0; i < 256; i++){
                synchronized(Communication.class){
                    if (!continuerÀChercher || Communication.communicationsCoupés){
                        break;
                    }
                }

                try{
                    this.socket = new Socket();
                    this.socket.connect(new InetSocketAddress(ipPart[0]+"."+ipPart[1]+"."+ipPart[2]+"."+i, 5000),100);
                    System.out.println("L'adresse "+ipPart[0]+"."+ipPart[1]+"."+ipPart[2]+"."+i+":5000 répond. Test d'authentification.");
                    if (authentifierServeur(this.socket)){
                        System.out.println("L'adresse correspond à un hôte.");
                        synchronized(this.connectéCallback){
                            connectéCallback.connecté();
                        }
                        this.continuerÀChercher = false;
                    }else if(estÀTuer){
                        break;
                    }else{
                        System.out.println("L'adresse ne correspond pas à un hôte.");
                        //System.out.println("Socket fermé : "+this.socket);
                        try{this.socket.close();}catch(Exception e){}
                        this.socket = null;
                    }
                }catch(Exception e){}
            }

            if(this.socket == null){
                System.out.println("Le jeu n'a pas pus trouver d'hôte.");
            }
        }

        public void tuer(){
            estÀTuer = true;
            continuerÀChercher = false;
        }
    }

    private static class ServeurConnecterThread extends Thread {
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
                this.serverSocket = new ServerSocket(5000);
                boolean communicationsCoupés = false;
                synchronized(Communication.class){
                    communicationsCoupés = Communication.communicationsCoupés;
                }
                while(this.continuerÀAttendre && !communicationsCoupés){
                    serverSocket.setSoTimeout(12000);
                    this.socket = serverSocket.accept();
                    System.out.println("Client potentiel connecté.");
                    if(authentifierClient(this.socket)){
                        System.out.println("La connection correspond à un client.");
                        synchronized(this.connectéCallback){
                            this.connectéCallback.connecté();
                        }
                        this.continuerÀAttendre = false;
                        break;
                    }else{
                        System.out.println("La connection ne correspond pas à un client.");
                        this.socket.close();
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private static class ConnecterThread extends Thread {
        private ConnectéCallback connectéCallback;

        public ConnecterThread(ConnectéCallback connectéCallback){
            this.connectéCallback = connectéCallback;
        }

        @Override
        public void run(){
            synchronized(Communication.class){
                Communication.clientConnecterThread = new ClientConnecterThread(new ConnectéCallback(){
                    @Override
                    public void connecté(){
                        synchronized(Communication.class){
                            if(Communication.communicationsCoupés){
                                return;
                            }
                        }
                        System.out.println("Je suis devenus un client");
                        Socket socket;
                        synchronized (Communication.clientConnecterThread) {
                            socket = Communication.clientConnecterThread.socket;
                        }
                        synchronized (Communication.serveurConnecterThread){
                            Communication.serveurConnecterThread.continuerÀAttendre = false;
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

                Communication.serveurConnecterThread = new ServeurConnecterThread(new ConnectéCallback(){
                    @Override
                    public void connecté(){
                        synchronized(Communication.class){
                            if(Communication.communicationsCoupés){
                                return;
                            }
                        }
                        System.out.println("Je suis devenus un serveur");
                        Socket socket = null;
                        ServerSocket serverSocket = null;
                        synchronized (Communication.serveurConnecterThread) {
                            socket = Communication.serveurConnecterThread.socket;
                            serversocket = Communication.serveurConnecterThread.serverSocket;
                        }
                        synchronized (Communication.clientConnecterThread){
                            Communication.clientConnecterThread.tuer();
                        }
                        synchronized (Communication.class){
                            Communication.estConnecté = true;
                            Communication.estServeur = true;
                            Communication.socket = socket;
                            Communication.serversocket = serverSocket;
                            Communication.attenteCommunication = new AttenteCommunication(socket);
                            Communication.attenteCommunication.start();
                        }
                        try{Thread.sleep(100);}catch(Exception e){e.printStackTrace();}
                        synchronized (connectéCallback){
                            connectéCallback.connecté();
                        }
                    }
                });

                Communication.clientConnecterThread.start();
                Communication.serveurConnecterThread.start();
            }
        }
    }

    public static AttenteCommunication attenteCommunication = null;
    public static ClientConnecterThread clientConnecterThread = null;
    public static ServeurConnecterThread serveurConnecterThread = null;
    public static ConnecterThread connecterThread = null;

    public static void Connecter(ConnectéCallback connectéCallback){

        connecterThread = new ConnecterThread(connectéCallback);
        connecterThread.start();
    }

    private static boolean authentifierServeur(Socket connection){
        try{
            DataInputStream input = new DataInputStream(connection.getInputStream());
            DataOutputStream output = new DataOutputStream(connection.getOutputStream());
            System.out.println("Envoie de la demande d'authentification.");
            Thread.sleep(100);
            output.writeUTF("Êtes-vous un hôte Battleship?");
            output.flush();
            System.out.println("Attente de la réponse");
            connection.setSoTimeout(200);
            String réponse = input.readUTF();
            if (!réponse.equals("Oui je suis un hôte Battleship.")){
                System.out.println("Réponse invalide.");
                return false;
            }
            System.out.println("Réponse valide. Envoie de l'identifiant.");
            output.writeLong(ID);
            output.flush();
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
            output.flush();
            System.out.println("Attente de l'identifiant.");
            connection.setSoTimeout(200);
            if(input.readLong()==ID){
                System.out.println("Identifiant invalide : le client est soi-même.");
                output.writeInt(1);
                output.flush();
                return false;
            } else {
                System.out.println("Identifiant valide.");
                output.writeInt(0);
                output.flush();
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static void couperCommunication(){
        System.out.println("Coupure des communications");
        if(!communicationsCoupés() && attenteCommunication != null){
            attenteCommunication.couperCommunication();
        }
        try {socket.close();} catch (Exception e) {e.printStackTrace();}
        if(estServeur){
            try {serversocket.close();} catch (Exception e) {e.printStackTrace();}
        }
        try {clientConnecterThread.interrupt();} catch (Exception e) {e.printStackTrace();}
        try {synchronized(serveurConnecterThread.serverSocket){serveurConnecterThread.serverSocket.close();}} catch (Exception e) {e.printStackTrace();}
        try {serveurConnecterThread.interrupt();} catch (Exception e) {e.printStackTrace();}
        try {connecterThread.interrupt();} catch (Exception e) {e.printStackTrace();}
        communicationsCoupés = true;
        estConnecté = false;
    }

    public static boolean communicationsCoupés(){
        if (communicationsCoupés){
            return true;
        }
        if(attenteCommunication != null && attenteCommunication.communicationsCoupés()){
            communicationsCoupés = true;
            estConnecté = false;
            try {socket.close();} catch (Exception e) {e.printStackTrace();}
            if(estServeur){
                try {serversocket.close();} catch (Exception e) {e.printStackTrace();}
            }

            try {clientConnecterThread.interrupt();} catch (Exception e) {e.printStackTrace();}
            try {synchronized(serveurConnecterThread.serverSocket){serveurConnecterThread.serverSocket.close();}} catch (Exception e) {e.printStackTrace();}
            try {serveurConnecterThread.interrupt();} catch (Exception e) {e.printStackTrace();}
            try {connecterThread.interrupt();} catch (Exception e) {e.printStackTrace();}
            return true;
        }else{
            return false;
        }
    }

    public static void envoyerString(String nom, String contenu){
        class Fil extends Thread{
            @Override
            public void run(){
                attenteCommunication.envoyerString(nom, contenu);
            }
        }
        Fil fil = new Fil();
        fil.start();
    }

    public static void envoyerChar(String nom, char contenu){
        class Fil extends Thread{
            @Override
            public void run(){
                attenteCommunication.envoyerChar(nom, contenu);
            }
        }
        Fil fil = new Fil();
        fil.start();
    }

    public static void envoyerByte(String nom, byte contenu){
        class Fil extends Thread{
            @Override
            public void run(){
                attenteCommunication.envoyerByte(nom, contenu);
            }
        }
        Fil fil = new Fil();
        fil.start();
    }

    public static void envoyerShort(String nom, short contenu){
        class Fil extends Thread{
            @Override
            public void run(){
                attenteCommunication.envoyerShort(nom, contenu);
            }
        }
        Fil fil = new Fil();
        fil.start();
    }

    public static void envoyerInt(String nom, int contenu){
        class Fil extends Thread{
            @Override
            public void run(){
                attenteCommunication.envoyerInt(nom, contenu);
            }
        }
        Fil fil = new Fil();
        fil.start();
    }

    public static void envoyerLong(String nom, long contenu){
        class Fil extends Thread{
            @Override
            public void run(){
                attenteCommunication.envoyerLong(nom, contenu);
            }
        }
        Fil fil = new Fil();
        fil.start();
    }

    public static void envoyerFloat(String nom, float contenu){
        class Fil extends Thread{
            @Override
            public void run(){
                attenteCommunication.envoyerFloat(nom, contenu);
            }
        }
        Fil fil = new Fil();
        fil.start();
    }

    public static void envoyerDouble(String nom, double contenu){
        class Fil extends Thread{
            @Override
            public void run(){
                attenteCommunication.envoyerDouble(nom, contenu);
            }
        }
        Fil fil = new Fil();
        fil.start();
    }

    public static void envoyerBool(String nom, boolean contenu){
        class Fil extends Thread{
            @Override
            public void run(){
                attenteCommunication.envoyerBool(nom, contenu);
            }
        }
        Fil fil = new Fil();
        fil.start();
    }

    public static void envoyerByteListe(String nom, byte[] contenu){
        class Fil extends Thread{
            @Override
            public void run(){
                attenteCommunication.envoyerByteListe(nom, contenu);
            }
        }
        Fil fil = new Fil();
        fil.start();
    }

    public static Object[] obtenirMessage(String nom){
        return attenteCommunication.obtenirMessage(nom);
    }
}
