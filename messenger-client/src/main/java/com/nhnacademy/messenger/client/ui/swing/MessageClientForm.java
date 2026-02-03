/*
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 * + Copyright 2026. NHN Academy Corp. All rights reserved.
 * + * While every precaution has been taken in the preparation of this resource,  assumes no
 * + responsibility for errors or omissions, or for damages resulting from the use of the information
 * + contained herein
 * + No part of this resource may be reproduced, stored in a retrieval system, or transmitted, in any
 * + form or by any means, electronic, mechanical, photocopying, recording, or otherwise, without the
 * + prior written permission.
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 */

package com.nhnacademy.messenger.client.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import lombok.Getter;

@Getter
public class MessageClientForm {

    private final JFrame frame;
    private final JTextArea messageArea;
    private final JTextField inputField;
    private final JButton sendButton;
    private final JScrollPane scrollPane;

    private final Consumer<String> onInputReceived;

    public MessageClientForm(Consumer<String> onInputReceived) {
        this.onInputReceived = onInputReceived;

        frame = new JFrame();
        messageArea = new JTextArea();
        inputField = new JTextField();
        sendButton = new JButton();
        scrollPane = new JScrollPane(messageArea);

        JPanel panel = new JPanel();

        initializeUI(panel);
        configureEvent();
    }

    private void initializeUI(JPanel panel) {
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setRows(20);

        inputField.setColumns(30);

        sendButton.setText("보내기");

        panel.setLayout(new BorderLayout());
        panel.add(inputField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        frame.setTitle("Messenger");
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();

        frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height + frameSize.height) / 2);
    }

    private void configureEvent() {
        ActionListener sendAction = event -> {
            String text = inputField.getText();

            if (!text.trim().isEmpty()) {
                onInputReceived.accept(text);
                inputField.setText("");
            }
        };

        sendButton.addActionListener(sendAction);

        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendAction.actionPerformed(null);
                }
            }
        });
    }

    public void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            messageArea.append(String.format("%s\n", message));
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
        });
    }

    public void setVisible(boolean visible) {
        frame.setVisible(visible);
        inputField.requestFocus();
    }

}
