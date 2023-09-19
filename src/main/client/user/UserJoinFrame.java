package main.client.user;

import main.client.user.listener.UserJoinListener;

import javax.swing.*;

public class UserJoinFrame extends JFrame {

    public UserJoinFrame() {

        setTitle("File Daiso (회원가입)");
        JPanel panel = new JPanel();
        JLabel label = new JLabel("e-mail :");
        JLabel paswd = new JLabel("Password :");
        JTextField txtID = new JTextField(13);
        JPasswordField txtpass = new JPasswordField(10);//비번 *표(암호화)
        JButton joinButton = new JButton("회원가입");
        JButton homeButton = new JButton("홈으로 돌아가기");
        panel.add(label);
        panel.add(txtID);
        panel.add(paswd);
        panel.add(txtpass);
        panel.add(joinButton);
        panel.add(homeButton);

        //라벨정렬
        label.setHorizontalAlignment(NORMAL);
        paswd.setHorizontalAlignment(NORMAL);

        add(panel);

        //버튼이벤트
        joinButton.addActionListener(new UserJoinListener(txtID, txtpass, this));
        homeButton.addActionListener(event -> {
            new StartPageFrame();
            setVisible(false);
        });

        setVisible(true);
        setSize(600,400);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
