/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.communication;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlo
 */
public class ServerTCP {   
    
    private static ServerSocket serverSocket;
    private Socket connectionSocket;
  
  
    public ServerTCP(int port) throws IOException{
        System.out.println("INICIANDO SERVER TCP");
        serverSocket = new ServerSocket(port);
        connectionSocket = serverSocket.accept();
        System.out.println("aceptado");
    }
  
    

    public String receive() throws IOException{
        //DataInputStream in = new DataInputStream(connectionSocket.getInputStream());
        //String a = in.readUTF();
        //return a;
        BufferedReader reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        String a = reader.readLine();
        System.out.println("Servidor recibe: "+a);
        return a;
    }


    public void send(String msg) throws IOException{
        //DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
        //outToClient.writeUTF(msg);
        PrintStream pstream = new PrintStream(connectionSocket.getOutputStream() );
        pstream.println(msg);
        pstream.flush();
        System.out.println("Servidor envia: "+msg);
    }

    public void end() throws IOException{
        serverSocket.close();
    }
}
