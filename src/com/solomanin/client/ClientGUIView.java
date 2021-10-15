package com.solomanin.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientGUIView {
    private final ClientGUIController controller;

    private JFrame frame = new JFrame("CHAT");
    private JTextField textField = new JTextField(50);
    private JTextArea message = new JTextArea(10, 50);
    private JTextArea users = new JTextArea(10, 10);

    public ClientGUIView(ClientGUIController controller) {
        this.controller = controller;
        initView();
    }

    private void initView() {
        textField.setEditable(false);
        message.setEditable(false);
        users.setEditable(false);

        frame.getContentPane().add(textField, BorderLayout.NORTH);
        frame.getContentPane().add(new JScrollPane(message), BorderLayout.WEST);
        frame.getContentPane().add(new JScrollPane(users), BorderLayout.EAST);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.sendTextMessage(textField.getText());
                textField.setText("");
            }
        });
    }

    public String getServerAddress(){
        return JOptionPane.showInputDialog(
                frame,
                "Input server address:",
                "Client config",
                JOptionPane.QUESTION_MESSAGE
        );
    }

    public int getServerPort(){
        while (true){
            String port = JOptionPane.showInputDialog(
                    frame,
                    "Input server port:",
                    "Client config",
                    JOptionPane.QUESTION_MESSAGE);
            try {
                return Integer.parseInt(port.trim());
            } catch (Exception e){
                JOptionPane.showMessageDialog(
                        frame,
                        "Uncorrect server port",
                        "Client config",
                        JOptionPane.QUESTION_MESSAGE
                );
            }
        }
    }

    public String getUserName(){
        return JOptionPane.showInputDialog(
                frame,
                "Input your name:",
                "Client config",
                JOptionPane.QUESTION_MESSAGE);
    }

    public void notifyConnectionStatusChanged(boolean clientConnected){
        textField.setEditable(clientConnected);
        if(clientConnected){
            JOptionPane.showMessageDialog(
                    frame,
                    "Succesful connection with chat:",
                    "Chat",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(
                    frame,
                    "Client don't connect with chat:",
                    "Chat",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshMessage(){
        message.append(controller.getModel().getNewMessage() + "\n");
    }

    public void refreshUsers(){
        ClientGuiModel model = controller.getModel();
        StringBuilder sb = new StringBuilder();
        for(String userName : model.getAllUserNames()){
            sb.append(userName).append("\n");
        }
        users.setText(sb.toString());
    }
}
