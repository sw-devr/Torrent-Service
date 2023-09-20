package main.client.user.ui;

import main.client.user.listener.UserLoginListener;

import javax.swing.*;
import java.awt.*;

public class UserLoginFrame extends JFrame {

    public UserLoginFrame() {

        setTitle("File Daiso");

        JPanel panel = new JPanel();
        JLabel label = new JLabel("e-mail :");
        JLabel paswd = new JLabel("Password :");
        JTextField txtID = new JTextField(13);
        JPasswordField txtpass = new JPasswordField(10);//비번 *표(암호화)
        JButton loginButton = new JButton("로그인");
        JButton backButton = new JButton("뒤로가기");

        backButton.setBounds(300, 170, 122, 30);
        panel.setLayout(new GridLayout(8, 1));

        panel.add(label);
        panel.add(txtID);
        panel.add(paswd);
        panel.add(txtpass);
        panel.add(loginButton);
        panel.add(backButton);

        label.setHorizontalAlignment(NORMAL);
        paswd.setHorizontalAlignment(NORMAL);

        backButton.addActionListener((event) -> {
            new StartPageFrame();
            setVisible(false);
        });

        loginButton.addActionListener(new UserLoginListener(txtID, txtpass, this));
        add(panel);

        setVisible(true);
        setSize(600,400);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
