package mo.communication.notes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.border.EtchedBorder;
import mo.communication.ClientConnection;
import mo.communication.Command;
import mo.communication.CommunicationConfiguration;
import mo.communication.ConnectionListener;
import mo.communication.ConnectionSender;
import mo.communication.PetitionResponse;
import mo.communication.chat.ChatEntry;
import mo.core.ui.dockables.DockableElement;
import mo.core.ui.dockables.DockablesRegistry;

public class NotesWindow implements CommunicationConfiguration, ConnectionSender{

    private JPanel notePanel, jp, jp2, container;
    private JButton sendButton;
    private JButton newNoteButton;
    private JTextArea noteTextArea;
    private String name;
    
    public NotesWindow(String name) {
        if(name != null){
            this.name = name;
        }
        else{
            this.name = "Name";
        }
        
        notePanel = new JPanel();
        notePanel.setLayout(new GridBagLayout());
        notePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        
        
        //PANEL PRINCIPAL
        container = new JPanel();
        container.setLayout(new GridBagLayout());
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        
        // PANEL EN EL QUE SE MUESTRAN LOS MENSAJES
        JPanel msgScrollPanel = new JPanel();
        msgScrollPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0.1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        msgScrollPanel.add(notePanel, gbc);
        VerticalScrollPane vsp = new VerticalScrollPane(msgScrollPanel);
        gbc.weightx = 2.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        //gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        container.add(new JScrollPane(vsp),gbc);
        
        
        //PANEL EN EL CUAL SE ESCRIBEN LOS MENSAJES Y TIENE EL BOTON DE ENVIAR
        jp = new JPanel(new BorderLayout());
        jp.setPreferredSize(new Dimension(175,100));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 2.0;
        gbc.weighty=0.0;
        jp.setBorder(new EtchedBorder());
        
        
        // PARA ESCRIBIR MENSAJE
        noteTextArea = new JTextArea();
        //JScrollPane scrollMsgArea = new JScrollPane(msgArea);
        //jp.add(scrollMsgArea,BorderLayout.CENTER);
        noteTextArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(noteTextArea);
        jp.add(scrollPane,BorderLayout.CENTER);
        
        noteTextArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    noteTextArea.setText(null);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    sendButton.doClick();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    noteTextArea.setText(null);
            }
        });
        
        
        //PARA ENVIAR
        sendButton = new JButton("Send");
        jp.add(sendButton,BorderLayout.EAST);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendNote();
            }
        });
        
        
        
        container.add(jp,gbc);
        jp.setVisible(false);
        
        
        //PANEL DEL BOTON DE NEUVA NOT
        jp2 = new JPanel();
        jp2.setPreferredSize(new Dimension(175,100));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 2.0;
        gbc.weighty=0.0;
        jp2.setBorder(new EtchedBorder());
        
        newNoteButton = new JButton("New Note");
        jp2.add(newNoteButton,BorderLayout.PAGE_START);
        newNoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newNote();
            }
        });
        newNoteButton.setPreferredSize(new Dimension(175, 30));
        
        container.add(jp2,gbc);
        
        // AGREGA EL CONTENEDOR PRINCIPAL A LA VENTANA
        SwingUtilities.invokeLater(() -> {
                
            try {
                DockableElement e = new DockableElement();
                e.add(container);
                if(ClientConnection.getInstance().getServer() != null){
                    DockablesRegistry.getInstance().addDockableInProjectGroup(
                        ClientConnection.getInstance().getServer().getIp(), e);
                }
                
            } catch (Exception ex) {}
        });      
    }
    
    public void newNote(){
        setBegin();
        jp2.setVisible(false);
        jp.setVisible(true);
        noteTextArea.setText(null);
    }
    
    public void sendNote(){
        if(noteTextArea.getText() != null && !noteTextArea.getText().equals("")){
            setEnd();
            String noteContent = noteTextArea.getText();
            noteTextArea.setText(null);
            int type = 1;
            HashMap<String,Object> map = new HashMap<>();
            map.put("name", name);
            map.put("noteContent", noteContent);
            map.put("timeBegin", String.valueOf(begin));
            map.put("timeEnd", String.valueOf(end));
            PetitionResponse p = new PetitionResponse(Command.TAKE_NOTE,map);
            //ClientConnection.getInstance().getServer().send(petition);
            if(listener != null){
                listener.onMessageReceived(this, p);
                System.out.println("Listener de ChatWindow: "+listener.toString());
                insertNote(new Note("You", noteContent, type));
            }
            else{
                insertNote(new Note("You", "No connection", 2));
            }
        }
        
        jp2.setVisible(true);
        jp.setVisible(false);
    }

    public void insertNote(Note entry) {
        GridBagConstraints gbc = new GridBagConstraints();

            JLabel nameLabel = new JLabel(entry.autor);

            BubblePane bubble = new BubblePane(notePanel, entry.content);

            // Arrange each chat entry based on the user.
            if (entry.type == 1) {
                bubble.setBackground(Color.YELLOW);
                gbc.anchor = GridBagConstraints.WEST;
            }
            else {
                bubble.setBackground(Color.CYAN);
                gbc.anchor = GridBagConstraints.EAST;
            }

            gbc.insets.set(0, 0, 0, 0);
            gbc.weightx = 1.0;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.NONE;
            notePanel.add(nameLabel, gbc);

            if (gbc.anchor == GridBagConstraints.WEST) {
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets.set(0, 0, 10, 40);
                notePanel.add(bubble, gbc);
            }
            else {
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets.set(0, 40, 10, 0);
                notePanel.add(bubble, gbc);
            }
        notePanel.revalidate();
        notePanel.repaint();
    }
    
    private long begin, end;
    
    public void setBegin(){
        begin = System.currentTimeMillis();
    }
    
    public void setEnd(){
        end = System.currentTimeMillis();
    }

    public static void main(String[] args) throws InterruptedException {

        NotesWindow cw = new NotesWindow(null);
        cw.insertNote(new Note("David", "Hey Lori, how are you?", 1));
        Thread.sleep(1000);
        cw.insertNote(new Note("Lori", "Hi David, I'm good. What have you been up to?", 1));
        Thread.sleep(1000);
        cw.insertNote(new Note("David", "I've been super busy with work.", 1));
        Thread.sleep(1000);
        cw.insertNote(new Note("David", "Haven't had much free time to even go out to eat.", 1));

    }

    @Override
    public void showPlayer() {
        container.setVisible(true);
    }

    @Override
    public void closePlayer() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private ConnectionListener listener;
    @Override
    public void subscribeListener(ConnectionListener c) {
        listener = c;
    }

    @Override
    public void unsubscribeListener(ConnectionListener c) {
        listener = null;
    }

    @Override
    public void setInfo(File parentDir, String name) {
        // DO NOTHING
    }
}