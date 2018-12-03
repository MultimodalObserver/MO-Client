package mo.communication;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import mo.communication.chat.ChatEntry;
import mo.communication.streaming.capture.CaptureConfig;
import mo.communication.streaming.capture.PluginCaptureListener;
import mo.communication.streaming.capture.PluginCaptureSender;
import mo.communication.streaming.visualization.VisualizationStreamingStage;
import mo.communication.streaming.visualization.VisualizeStreamingAction;

public class RemoteServer {
    private String ip;
    private String multicastIP = "230.0.0.0";
    private int portTCP;
    private int portUDP;
    private int portRTP;
    private HashMap<String,CaptureConfig> capturePlugins;
    private Socket clientSocket;
    private ObjectOutputStream outStream;
    private final int TIME_OUT = 50; // in ms (5 sec)
    protected MulticastSocket udpSocket;
    private InetAddress host;
    
    public RemoteServer(String ip, int portTCP) throws IOException{
//        System.setProperty("java.net.preferIPv4Stack" , "true");
        this.ip = ip;
        this.portTCP = portTCP;
        this.capturePlugins = new HashMap<>();
    }
    
    public boolean connect() throws InterruptedException, ClassNotFoundException{
        boolean connecting = true;
        int trying = 0;
        clientSocket = new Socket();
        while(connecting){
            try {System.out.println("INTENTO "+trying);
                clientSocket.connect(new InetSocketAddress(ip, portTCP), TIME_OUT);
                System.out.println("PASO ACA");
                connecting = false;
            } catch (IOException e) {
                if(trying++ > 1) {System.out.println("NO PUDO"); break;}
            }
        }
        System.out.println("SALIO con "+!connecting);
        return !connecting; // false = no pudo conectarse ; true = pudo establecer conexión
    }
    
    public void getCapturePlugins() throws IOException, ClassNotFoundException{
        send(new PetitionResponse(Command.GET_ACTIVE_PLUGINS,null));
    }
    
    public void getPorts() throws IOException, ClassNotFoundException{
        send(new PetitionResponse(Command.GET_PORTS,null));
    }

    public String getIp() {
        return ip;
    }
    
    private void addCapturePlugins(PetitionResponse r) throws UnknownHostException{
        //System.out.println("PLUGINS QUE LLEGARON");
        //System.out.println(r.getHashMap());
        r.getHashMap().forEach((k,v) -> {
            //System.out.println("Key: " + k + ": Value: " +(CaptureConfig) v);
            capturePlugins.put(k,(CaptureConfig) v);
        });
        System.out.println("PLUGINS QUE LLEGARON"+capturePlugins);
        
        //CommunicationViewer.getInstance();//.establishedConnection();
  //      VisualizationStreamingStage vss = new VisualizationStreamingStage();
        //VisualizeStreamingAction vsa = new VisualizeStreamingAction();
        //vsa.init(capturePlugins);
        VisualizeStreamingAction vsa = new VisualizeStreamingAction();
        vsa.initCapturePluginsStreaming(capturePlugins);
        //vss.getActions().get(0).init(capturePlugins);
    }
    
    private void configDirectDevs(PetitionResponse r){
        try{
            VisualizeStreamingAction vsa = new VisualizeStreamingAction();
            vsa.initDirectStreaming(r.getHashMap(),multicastIP);
        }catch(NullPointerException ex){
            
        }
        
    }
    
    public String removeEnd(String in) {
        String firstWords = in.substring(0, in.lastIndexOf(" "));
        return firstWords;
    }
    
    public String getEnd(String in) {
        String firstWords = in.substring(in.lastIndexOf(" ")+1);
        return firstWords;
    }
    
    public void listenTCP(){
        new Thread(() -> {
            listeningTCP();
        }).start();
        
//        new Thread(() -> {
//            listeningRTP();
//        }).start();
//        
//        new Thread(() -> {
//            listeningUDP();
//        }).start(); 
    }
    
    private void listeningTCP(){
        //BufferedReader reader = null;
        ObjectInputStream inputStream;
       // Response response;
        while(true){
            try {
                if(clientSocket.getInputStream().available() > 0) {
                    inputStream = new ObjectInputStream(clientSocket.getInputStream());
                    PetitionResponse response = (PetitionResponse)inputStream.readObject();
                    if(response != null){
                        System.out.println(response);
                        //Response auxResponse = response;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {handlerTCP(response);}
                                catch (UnknownHostException ex) {} catch (IOException ex) {
                                    Logger.getLogger(RemoteServer.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (ClassNotFoundException ex) {
                                    Logger.getLogger(RemoteServer.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }).start();
                    }
                }
                
            } catch (IOException | ClassNotFoundException ex) {}
        }
    }
    
    public void handlerTCP(PetitionResponse r) throws UnknownHostException, IOException, ClassNotFoundException{
        /*
        1. TODO: comprobar el tipo de repsuesta
        2. Realizar acciones
        */
        switch (r.getType()) {
            case Command.GET_ACTIVE_PLUGINS_RESPONSE:
                addCapturePlugins(r);               
                break;
            case Command.DIRECT_CONFIGS:
                configDirectDevs(r);
                break;
            case Command.END_CONNECTION:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnect();
                        try {
                            ClientConnection.getInstance().downClient();
                        } catch (UnknownHostException ex) {
                            Logger.getLogger(RemoteServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                }).start();
//                disconnect();
//                ClientConnection.getInstance().disconnect();
                break;
            case Command.MSG_CLIENT_TO_SERVER:
                send(r);
                break;
            case Command.TAKE_NOTE:
                send(r);
                break;
            case Command.CHANGE_QUALITY_STREAMING:
                System.out.println("Enviando "+r);
                send(r);
                break;
            case Command.STOP_STREAMING:
                System.out.println("Enviando "+r);
                send(r);
                break;
            case Command.GET_PORTS_RESPONSE:
                System.out.println("RECIBIENDO PUERTOS"+r);
                portRTP = 0;// Integer.parseInt((String)r.getHashMap().get("portRTP"));
//                new Thread(() -> {
//                    listeningRTP();
//                }).start();

                portUDP = Integer.parseInt((String)r.getHashMap().get("portUDP"));
                try {
//                    System.setProperty("java.net.preferIPv4Stack" , "true");
                    System.out.println("Socket udp antes: "+udpSocket);
                    System.out.println("multicast ip: "+multicastIP);
                    udpSocket = new MulticastSocket(portUDP);
                    System.out.println("Socket udp despues: "+udpSocket);
                    host = InetAddress.getByName(multicastIP);
                    udpSocket.joinGroup(host);
                    System.out.println("se unió");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                new Thread(() -> {
                    listeningUDP();
                }).start();
                break;
//            case Command.MSG_SERVER_TO_CLIENT:
//                ChatEntry entry = new ChatEntry(
//                        (String)r.getHashMap().get("name"),
//                        (String)r.getHashMap().get("msg"),
//                        1);
//                ClientConnection.getInstance().getChat().insertMsg(entry);
//                break;
            default:
                System.out.println("Notificando "+r);
                if(ClientConnection.getInstance().getListeners() != null){
                    for(ConnectionListener cl :ClientConnection.getInstance().getListeners()){
                        if(cl!=null)
                            cl.onMessageReceived(ClientConnection.getInstance(), r);
                    }
                }
        }
    }
    
    private void listeningRTP(){
        //while(true){}
    }
    
    private void listeningUDP(){
        try {
            /*
            1. TODO: comprobar fuente
            2. Reproducir
            */
            byte[] buffer = new byte[2048];
            DatagramPacket incomingPacket = new DatagramPacket(buffer, buffer.length);
            Thread.sleep(2000);
            while(true){
                try {
                    udpSocket.receive(incomingPacket);
                    byte[] data = incomingPacket.getData();
                    ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(data));
                    PetitionResponse response = (PetitionResponse) iStream.readObject();
                    if(response != null){
                        System.out.println(response);
                        //Response auxResponse = response;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("ESTA RESPUESTA DA NULO; "+response);
                                handlerUDP(response);
                            }
                        }).start();
                    }
                } catch (IOException | ClassNotFoundException ex) {}
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(RemoteServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void handlerUDP(PetitionResponse r){
        /*
        1. TODO: comprobar el tipo de repsuesta
        2. Realizar acciones
        */
        switch (r.getType()) {
            case Command.GET_ACTIVE_PLUGINS_RESPONSE:
                r.getHashMap();
            default:
                System.out.println("Cliente recibe ->\n"+r);
            try {
                for(ConnectionListener c: ClientConnection.getInstance().getListeners())
                    c.onMessageReceived(ClientConnection.getInstance(), r);
                //throw new AssertionError();
            } catch (UnknownHostException ex) {}
        }
    }
    
    public void send(PetitionResponse petition) throws UnknownHostException, SocketException{
        try{
            if(!clientSocket.isClosed()){
                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                outputStream.writeObject(petition);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(RemoteServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public String getMulticastIP(){
        return multicastIP;
    }
    
    public void disconnect(){
        try {
            if(!clientSocket.isClosed())
                clientSocket.close();
            if(!udpSocket.isClosed())
                udpSocket.close();
        } catch (IOException ex) {}
        
    }
    
    
    
//    public void sendIPAddress() throws UnknownHostException, IOException {
//        socketUDP = new DatagramSocket();
//        host = InetAddress.getByName(this.ip);
//        String localIP = InetAddress.getLocalHost().getHostAddress();
//        byte[] msgBytes = localIP.getBytes();
//        DatagramPacket packetToSend = new DatagramPacket(msgBytes,localIP.length(),host,this.portUDP);
//        socketUDP.send(packetToSend);
//    }
}
