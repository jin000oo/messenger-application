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

import com.nhnacademy.messenger.common.dto.response.info.RoomInfo;
import com.nhnacademy.messenger.common.dto.response.info.UserInfo;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import lombok.Getter;

@Getter
public class MessageClientForm {

    private final JFrame frame;
    private final CardLayout cardLayout;
    private final JPanel mainContainer;

    // 로그인 화면
    private JTextField loginIdField;
    private JPasswordField loginPwField;
    private JButton loginButton;

    // 메인 채팅 화면
    private JList<RoomInfo> roomList; // 채팅방 목록
    private DefaultListModel<RoomInfo> roomListModel;

    // 상단 버튼
    private JButton createRoomButton;
    private JButton refreshRoomButton;

    // 하단 버튼
    private JButton userListButton;
    private JButton helpButton;
    private JButton logoutButton;

    // 채팅 영역
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JLabel currentRoomLabel;
    private JButton leaveRoomButton;

    private final Consumer<String> commandProcessor;

    // 유저 목록 팝업창 관리를 위한 변수
    private JDialog userListDialog;
    private JList<UserInfo> userListComp;
    private DefaultListModel<UserInfo> userListModel;

    public MessageClientForm(Consumer<String> commandProcessor) {
        this.commandProcessor = commandProcessor;

        frame = new JFrame("Messenger");
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // 두 개의 메인 패널 초기화
        JPanel loginPanel = createLoginPanel();
        JPanel chatPanel = createChatPanel();

        mainContainer.add(loginPanel, "LOGIN");
        mainContainer.add(chatPanel, "CHAT");

        frame.add(mainContainer);
        frame.setSize(900, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
    }

    // 로그인 패널 생성
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 242, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Messenger");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

        loginIdField = new JTextField(15);
        loginPwField = new JPasswordField(15);
        loginButton = new JButton("로그인");

        loginButton.setPreferredSize(new Dimension(200, 40));
        loginButton.setBackground(new Color(58, 175, 169));
        loginButton.setForeground(Color.WHITE);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);

        // 배치
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(loginIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("PW:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(loginPwField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 0, 0);
        panel.add(loginButton, gbc);

        // 이벤트: 로그인 버튼 클릭
        loginButton.addActionListener(e -> attemptLogin());
        loginPwField.addActionListener(e -> attemptLogin()); // 엔터 치면 로그인

        return panel;
    }

    // --- 메인 채팅 패널 생성 ---
    private JPanel createChatPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // [왼쪽] 사이드바 (채팅방 목록)
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
        sidebar.setPreferredSize(new Dimension(200, 0));

        // 상단: 방 관리 버튼
        JPanel topButtonPanel = new JPanel(new GridLayout(1, 2, 5, 0)); // 1행 2열
        topButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        createRoomButton = new JButton("방 생성");
        refreshRoomButton = new JButton("새로고침");
        topButtonPanel.add(createRoomButton);
        topButtonPanel.add(refreshRoomButton);

        // 중앙: 채팅방 리스트
        roomListModel = new DefaultListModel<>();
        roomList = new JList<>(roomListModel);
        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            String text = String.format("🏠 %s (%d명)", value.roomName(), value.userCount());
            JLabel label = new JLabel(text);
            label.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            label.setFont(new Font("SansSerif", Font.PLAIN, 13));
            if (isSelected) {
                label.setBackground(new Color(200, 220, 240));
                label.setOpaque(true);
            }
            return label;
        });

        // 하단: 유저관리/도움말/로그아웃
        JPanel bottomButtonPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        bottomButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        userListButton = new JButton("👥 접속자 목록");
        helpButton = new JButton("❓ 도움말");
        logoutButton = new JButton("🚪 로그아웃");

        bottomButtonPanel.add(userListButton);
        bottomButtonPanel.add(helpButton);
        bottomButtonPanel.add(logoutButton);

        sidebar.add(topButtonPanel, BorderLayout.NORTH);
        sidebar.add(new JScrollPane(roomList), BorderLayout.CENTER);
        sidebar.add(bottomButtonPanel, BorderLayout.SOUTH);

        // [오른쪽] 채팅 영역
        JPanel chatContentPanel = new JPanel(new BorderLayout());

        // 채팅방 헤더
        JPanel chatHeader = new JPanel(new BorderLayout());
        chatHeader.setBackground(Color.WHITE);
        chatHeader.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        currentRoomLabel = new JLabel("대기실 (채팅방을 선택하세요)");
        currentRoomLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        leaveRoomButton = new JButton("나가기");

        chatHeader.add(currentRoomLabel, BorderLayout.WEST);
        chatHeader.add(leaveRoomButton, BorderLayout.EAST);

        // 채팅 내용
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        chatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 입력 영역
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        inputField.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        sendButton = new JButton("전송");
        sendButton.setBackground(new Color(58, 175, 169));
        sendButton.setForeground(Color.WHITE);
        sendButton.setOpaque(true);
        sendButton.setBorderPainted(false);

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        chatContentPanel.add(chatHeader, BorderLayout.NORTH);
        chatContentPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        chatContentPanel.add(inputPanel, BorderLayout.SOUTH);

        // 스플릿 페인으로 합치기
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, chatContentPanel);
        splitPane.setDividerLocation(220);
        panel.add(splitPane, BorderLayout.CENTER);

        // --- 이벤트 연결 ---
        configureChatEvents();

        return panel;
    }

    private void configureChatEvents() {
        // 방 생성 버튼
        createRoomButton.addActionListener(e -> {
            String roomName = JOptionPane.showInputDialog(frame, "생성할 채팅방 이름을 입력하세요:");
            if (roomName != null && !roomName.trim().isEmpty()) {
                commandProcessor.accept("/create " + roomName.trim());
            }
        });

        // 목록 갱신 버튼
        refreshRoomButton.addActionListener(e -> commandProcessor.accept("/list"));

        // 채팅방 더블 클릭 -> 입장
        roomList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    RoomInfo selected = roomList.getSelectedValue();
                    if (selected != null) {
                        commandProcessor.accept("/enter " + selected.roomId());
                        currentRoomLabel.setText("채팅방: " + selected.roomName());
                        chatArea.setText(""); // 기존 내용 지우기
                    }
                }
            }
        });

        // 나가기 버튼
        leaveRoomButton.addActionListener(e -> {
            commandProcessor.accept("/leave");
            currentRoomLabel.setText("대기실");
            chatArea.setText("");
            commandProcessor.accept("/list"); // 나가고 나서 목록 갱신
        });

        // 유저 목록 버튼
        userListButton.addActionListener(e -> commandProcessor.accept("/users"));

        // 로그아웃 버튼
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(frame, "정말 로그아웃 하시겠습니까?", "로그아웃", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                commandProcessor.accept("/logout");
            }
        });

        // 도움말 버튼
        helpButton.addActionListener(e -> {
            String helpText = """
                    [사용법]
                    
                    1. 방 생성: [방 생성] 버튼 클릭
                    2. 방 입장: 목록에서 방 더블 클릭
                    3. 귓속말: [접속자 목록] -> 유저 더블 클릭
                    4. 채팅: 아래 입력창에 메시지 입력 후 엔터
                    """;
            JOptionPane.showMessageDialog(frame, helpText, "도움말", JOptionPane.INFORMATION_MESSAGE);
        });

        // 메시지 전송
        Runnable sendAction = () -> {
            String msg = inputField.getText();
            if (!msg.trim().isEmpty()) {
                commandProcessor.accept("/chat " + msg);
                inputField.setText("");
            }
        };
        sendButton.addActionListener(e -> sendAction.run());
        inputField.addActionListener(e -> sendAction.run());
    }

    private void attemptLogin() {
        String id = loginIdField.getText().trim();
        String pw = new String(loginPwField.getPassword()).trim();
        if (id.isEmpty() || pw.isEmpty()) {
            return;
        }

        // 명령어를 생성해서 Processor에게 전달
        commandProcessor.accept("/login " + id + " " + pw);
    }

    // --- 유저 목록 팝업창 띄우기 ---
    public void showUserListPopup(List<UserInfo> users) {
        SwingUtilities.invokeLater(() -> {
            if (userListDialog == null) {
                // 팝업창 최초 생성
                userListDialog = new JDialog(frame, "접속자 목록", false); // non-modal (채팅하면서 볼 수 있게)
                userListDialog.setSize(300, 400);
                userListDialog.setLocationRelativeTo(frame);

                userListModel = new DefaultListModel<>();
                userListComp = new JList<>(userListModel);
                userListComp.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                // 유저 더블 클릭 -> 귓속말
                userListComp.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            UserInfo target = userListComp.getSelectedValue();
                            if (target != null) {
                                String msg = JOptionPane.showInputDialog(userListDialog,
                                        target.name() + "님에게 보낼 귓속말:", "귓속말 보내기", JOptionPane.QUESTION_MESSAGE);
                                if (msg != null && !msg.trim().isEmpty()) {
                                    commandProcessor.accept("/whisper " + target.id() + " " + msg.trim());
                                }
                            }
                        }
                    }
                });

                // 렌더러
                userListComp.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
                    String status = value.online() ? "🟢" : "⚪"; // 온라인/오프라인 표시 (서버가 지원한다면)
                    String text = String.format("%s %s (%s)", status, value.name(), value.id());
                    JLabel label = new JLabel(text);
                    label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    if (isSelected) {
                        label.setBackground(new Color(200, 220, 240));
                        label.setOpaque(true);
                    }
                    return label;
                });

                userListDialog.add(new JScrollPane(userListComp));
            }

            // 데이터 업데이트 및 보여주기
            userListModel.clear();
            for (UserInfo user : users) {
                userListModel.addElement(user);
            }
            userListDialog.setVisible(true);
        });
    }

    // --- 외부 제어 메서드 ---
    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }

    // 화면 전환 (LOGIN <-> CHAT)
    public void showScreen(String screenName) {
        SwingUtilities.invokeLater(() -> {
            cardLayout.show(mainContainer, screenName);
            if ("LOGIN".equals(screenName)) {
                frame.setTitle("Messenger - Login");
                // 로그아웃 후 로그인 필드 초기화 및 포커스
                loginIdField.setText("");
                loginPwField.setText("");
                loginIdField.requestFocus();
            } else {
                frame.setTitle("Messenger - Lobby");
            }
        });
    }

    public void appendMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(msg + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    // 방 목록 UI 업데이트
    public void updateRoomList(List<RoomInfo> rooms) {
        SwingUtilities.invokeLater(() -> {
            roomListModel.clear();
            for (RoomInfo room : rooms) {
                roomListModel.addElement(room);
            }
        });
    }

    public void showErrorMessage(String msg) {
        JOptionPane.showMessageDialog(frame, msg, "오류", JOptionPane.ERROR_MESSAGE);
    }

}
