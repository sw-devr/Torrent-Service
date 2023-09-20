package main.client.user.ui;

import javax.swing.*;
import java.awt.*;

public class StartPageFrame extends JFrame {

    public StartPageFrame() {

        JPanel panel = new JPanel();
        JPanel btnpanel = new JPanel();
        JLabel label = new JLabel("어서오세요 파일다이소 입니다");
        JButton loginButton = new JButton("로그인");
        JButton joinButton = new JButton("회원가입");

        btnpanel.add(loginButton);
        btnpanel.add(joinButton);
        panel.add(label);
        panel.add(btnpanel, BorderLayout.SOUTH);
        label.setHorizontalAlignment(NORMAL);
        panel.setLayout(new GridLayout(2, 1));

        add(panel);

        joinButton.addActionListener((event) -> {
            new UserJoinFrame();
            setVisible(false);
        });

        loginButton.addActionListener((event) -> {
            new UserLoginFrame();
            setVisible(false);
        });


        setTitle("Welcome to FileDaiso");
        setVisible(true);
        setSize(600,400);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}
