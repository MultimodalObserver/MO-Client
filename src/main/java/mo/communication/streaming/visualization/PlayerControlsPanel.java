package mo.communication.streaming.visualization;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import mo.communication.ClientConnection;
import mo.communication.Command;
import mo.communication.ConnectionListener;
import mo.communication.PetitionResponse;
import mo.communication.RemoteServer;
import mo.core.I18n;
import mo.core.ui.GridBConstraints;
import org.apache.commons.lang3.time.FastDateFormat;

public class PlayerControlsPanel implements ConnectionListener{

    private JPanel panel;
    //private JSlider slider;
    private JButton playRecording, stopRecording, cancelRecording, playStreaming, stopStreaming;
    //private JCheckBox CheckSync;
    //private JLabel LabelSync;
    //private JLabel currentTime;
    //private JLabel ellapsedTLabel;
    private JLabel labelStreaming;
    private JLabel labelRecording;
    private GridBConstraints gbc;
    private I18n inter;

    private final static String ELLAPSED_FORMAT = "%02d:%02d:%02d:%1d";
    private final FastDateFormat timeF = FastDateFormat.getInstance("yyyy-MM-dd  HH:mm:ss:SSS");

    private final VisualizationStreamingPlayer player;

    //private static final String PLAY_SYMBOL = "Play";//"\u25BA"; //25BA
    private String RECORD_SYMBOL;// = inter.s("PlayerControlsPanel.RECORD_SYMBOL");//"Record";//"\u25CF";
    private String PAUSE_SYMBOL;//= inter.s("PlayerControlsPanel.PAUSE_SYMBOL");//"Pause";//"||";
    private String STOP_SYMBOL;// = inter.s("PlayerControlsPanel.STOP_SYMBOL");//"Stop";// "\u25A0";
    private String CANCEL_SYMBOL;// = inter.s("PlayerControlsPanel.CANCEL_SYMBOL");//"Cancel";//"\u2716";

    public PlayerControlsPanel(VisualizationStreamingPlayer player) {
        inter = new I18n(PlayerControlsPanel.class);
        this.player = player;
        
        RECORD_SYMBOL = inter.s("PlayerControlsPanel.RECORD_SYMBOL");//"Record";//"\u25CF";
        PAUSE_SYMBOL = inter.s("PlayerControlsPanel.PAUSE_SYMBOL");//"Pause";//"||";
        STOP_SYMBOL = inter.s("PlayerControlsPanel.STOP_SYMBOL");//"Stop";// "\u25A0";
        CANCEL_SYMBOL = inter.s("PlayerControlsPanel.CANCEL_SYMBOL");//"Cancel";//"\u2716";
        

        panel = new JPanel(new GridBagLayout());

        SwingUtilities.invokeLater(() -> {
            gbc = new GridBConstraints();

            gbc.f(GridBagConstraints.HORIZONTAL);
            gbc.i(new Insets(5, 5, 5, 5)).wx(1);
            
            /*
            RECORDING CONTROLS
            */
            labelRecording = new JLabel(inter.s("PlayerControlsPanel.RecordingControls"));
            panel.add(labelRecording,gbc.gy(1).gw(1).wx(0));
            
            playRecording = new JButton(PAUSE_SYMBOL);
            playRecording.addActionListener((ActionEvent e) -> {
                playRecordingPressed();
            });
             
            panel.add(playRecording, gbc.gy(1).gw(1).wx(0));
            
            
            stopRecording = new JButton(STOP_SYMBOL);
            stopRecording.addActionListener((ActionEvent e) -> {
                stopRecordingPressed();
            });
             
            panel.add(stopRecording, gbc.gy(1).gw(1).wx(0));
            
            
            cancelRecording = new JButton(CANCEL_SYMBOL);
            cancelRecording.addActionListener((ActionEvent e) -> {
                cancelRecordingPressed();
            });
             
            panel.add(cancelRecording, gbc.gy(1).gw(1).wx(0));
            
            
            
//            panel.add(new JLabel("              "),gbc.gy(1).gw(1).wx(0));
            /*
            STREAMING CONTROLS
            */
//            labelStreaming = new JLabel("Streaming controls");
//            panel.add(labelStreaming,gbc.gy(1).gw(1).wx(0));
//            
//            playStreaming = new JButton(PLAY_SYMBOL);
//            playStreaming.addActionListener((ActionEvent e) -> {
//                playStreamingPressed();
//            });
//             
//            panel.add(playStreaming, gbc.gy(1).gw(1).wx(0));
//            
//            
//            stopStreaming = new JButton(STOP_SYMBOL);
//            stopStreaming.addActionListener((ActionEvent e) -> {
//                //stopStreamingPressed();
//            });
//             
//            panel.add(stopStreaming, gbc.gy(1).gw(1).wx(0));
        });
        
        try {
            ClientConnection.getInstance().subscribeListener(this);
        } catch (UnknownHostException ex) {}
    }


    /*
    RECORDING CONTROLS
    */
    private void playRecordingPressed() {
        try {
//            if (playRecording.getText().equals(RECORD_SYMBOL)) {
//                playRecording.setText(PAUSE_SYMBOL);
//            } else {
//                playRecording.setText(RECORD_SYMBOL);
//            }
//            playRecording.setEnabled(true);
//            cancelRecording.setEnabled(true);
            PetitionResponse petition = new PetitionResponse(Command.PAUSE_RESUME_RECORDING,null);
            ClientConnection.getInstance().getServer().send(petition);
        } catch (UnknownHostException ex) {
            Logger.getLogger(PlayerControlsPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(PlayerControlsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    private void stopRecordingPressed(){
        try {
            PetitionResponse petition = new PetitionResponse(Command.STOP_RECORDING,null);
            ClientConnection.getInstance().getServer().send(petition);
//            playRecording.setEnabled(false);
//            cancelRecording.setEnabled(false);
        } catch (UnknownHostException ex) {
            Logger.getLogger(PlayerControlsPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(PlayerControlsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void cancelRecordingPressed() {
        try {
            PetitionResponse petition = new PetitionResponse(Command.CANCEL_RECORDING,null);
            ClientConnection.getInstance().getServer().send(petition);
//            playRecording.setEnabled(false);
//            cancelRecording.setEnabled(false);
        } catch (UnknownHostException ex) {
            Logger.getLogger(PlayerControlsPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(PlayerControlsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /*
    STREAMING CONTROLS
    */
//    private void playStreamingPressed() {
//        if (player.isPlaying()) {
//            playStreaming.setText(PLAY_SYMBOL);
//            player.pause();
//        } else {
//            playStreaming.setText(PAUSE_SYMBOL);
//            player.play();
//        }
//    }
    
    private void stopStreamingPressed() {
        
    }

    public JPanel getPanel() {
        return panel;
    }

//    public void stop() {
//        playRecording.setText(PLAY_SYMBOL);
//    }
    
    private void getRecordState() {
        try{
            PetitionResponse petition = new PetitionResponse(Command.GET_RECORDING_STATE,null);
            ClientConnection.getInstance().getServer().send(petition);
        } catch (UnknownHostException ex) {
            Logger.getLogger(PlayerControlsPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(PlayerControlsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onMessageReceived(Object obj, PetitionResponse r) {
        if(r.getType().equals(Command.UPDATE_STATE_RECORDING)){
            String var = r.getHashMap().get("recording_state").toString();
            switch (var) {
                case "paused":
                    playRecording.setText(RECORD_SYMBOL);
                    cancelRecording.setEnabled(true);
                    stopRecording.setEnabled(true);
                    break;
                case "recording":
                    playRecording.setText(PAUSE_SYMBOL);
                    cancelRecording.setEnabled(true);
                    stopRecording.setEnabled(true);
                    break;
                case "stopped":
                    playRecording.setText(RECORD_SYMBOL);
                    playRecording.setEnabled(true);
                    cancelRecording.setEnabled(false);
                    stopRecording.setEnabled(false);
                    break;
                case "cancelled":
                    playRecording.setText(RECORD_SYMBOL);
                    playRecording.setEnabled(true);
                    cancelRecording.setEnabled(false);
                    stopRecording.setEnabled(false);
                    break;
                default:
                    // DO NOTHING
            }
            
        }
    }
}
