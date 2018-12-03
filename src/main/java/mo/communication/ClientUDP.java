/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.communication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.*;

/**
 *
 * @author carlo
 */
public class ClientUDP {
    
    DatagramSocket socketUDP;
    DatagramPacket packetToSend;
    InetAddress host;
    byte[] buffer;
    int port;
    
    public ClientUDP(String hostIP, int port, int tamBuffer) throws SocketException, UnknownHostException{
        this.port = port;
        socketUDP = new DatagramSocket();
        buffer = new byte[tamBuffer];
        host = InetAddress.getByName(hostIP);        
    }
    
    
    public PetitionResponse receive() throws IOException, ClassNotFoundException{
        DatagramPacket incomingPacket = new DatagramPacket(buffer, buffer.length);
        socketUDP.receive(incomingPacket);
        byte[] data = incomingPacket.getData();
        ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(data));
        PetitionResponse response = (PetitionResponse) iStream.readObject();
        return response;
    }
    
    public void send(PetitionResponse petition) throws IOException{
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        ObjectOutput oo = new ObjectOutputStream(bStream); 
        oo.writeObject(petition);
        oo.close();
        byte[] serializedMessage = bStream.toByteArray();
        
        DatagramPacket packetToSend = new DatagramPacket(serializedMessage,serializedMessage.length,host,port);
        socketUDP.send(packetToSend);
    }
    
    public void end(){
        socketUDP.close();
    }
}
