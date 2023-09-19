package main.client.file.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UsersFileListPanel extends JPanel {

    public UsersFileListPanel() {

        setBackground(new Color(255, 255, 255));

        setLayout(null);

        JPanel editPanel = new JPanel();
        editPanel.setBounds(0, 0, 1136, 392);
        add(editPanel);
        editPanel.setLayout(null);

        JTable table_1 = new JTable();
        table_1.setBounds(12, 10, 1112, 372);
        editPanel.add(table_1);

        JButton btnNewButton = new JButton("수정하기");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showInputDialog("Subject");
                JOptionPane.showInputDialog("Description");
                JOptionPane.showInputDialog("Price");
            }
        });
        btnNewButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnNewButton.setBounds(358, 402, 167, 30);
        add(btnNewButton);

        JButton btnNewButton_1 = new JButton("삭제하기");
        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showInputDialog("Subject");
            }
        });
        btnNewButton_1.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnNewButton_1.setBounds(569, 402, 167, 30);
        add(btnNewButton_1);
    }
}
