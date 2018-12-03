package mo.communication.notes;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import mo.communication.ClientConnection;
import mo.communication.Command;
import mo.communication.PetitionResponse;
import mo.core.ui.dockables.DockableElement;
import mo.core.ui.dockables.DockablesRegistry;

public class RemoteNotesPanel extends JPanel{
    
    private long begin, end;
    private JTextArea textArea;
    private JButton buttonNewNote, buttonAccept;
    
    public RemoteNotesPanel(){
        GridBagLayout gbl = new GridBagLayout();
        this.setLayout(gbl);
        
        GridBagConstraints gbc = new GridBagConstraints();

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
//        textArea.setRows(10);
//        textArea.setColumns(25);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        textArea.setSize(30, 30);
        this.add(scrollPane,gbc);
        scrollPane.setVisible(false);
        
        buttonNewNote = new JButton("New Note");
        buttonNewNote.setFont(new Font("Arial",Font.PLAIN,20));
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.add(buttonNewNote,gbc);
        
        buttonAccept = new JButton("Add");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.25;
        gbc.weighty = 0.25;
        this.add(buttonAccept,gbc);
        
        
        buttonNewNote.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonNewNote.setVisible(false);
                scrollPane.setVisible(true);
                buttonAccept.setVisible(true);
                getBegin();
            }
        });
        
        buttonAccept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scrollPane.setVisible(false);
                buttonNewNote.setVisible(true);
                buttonAccept.setVisible(false);
                getEnd();
                sendNote();
            }
        });
        
        buttonAccept.setVisible(false);
        
        
        
        SwingUtilities.invokeLater(() -> {
                
            try {
                DockableElement e = new DockableElement();
                e.add(this);
                DockablesRegistry.getInstance().addAppWideDockable(e);
            } catch (Exception ex) {}
        });      
    }
    
    public long getBegin(){
        return begin = System.currentTimeMillis();
    }
    
    public long getEnd(){
        return end = System.currentTimeMillis();
    }
    
    public long getMilliseconds() {
        return end-begin;
    }
    
    public void sendNote() {
        try {
            HashMap<String,Object> map = new HashMap<>();
            map.put("note", textArea.getText());
            map.put("time",String.valueOf(getMilliseconds()));
            PetitionResponse petition = new PetitionResponse(Command.TAKE_NOTE,map);
            ClientConnection.getInstance().getServer().send(petition);
            textArea.setText(null);
        } catch (Exception ex) {
            Logger.getLogger(RemoteNotesPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
