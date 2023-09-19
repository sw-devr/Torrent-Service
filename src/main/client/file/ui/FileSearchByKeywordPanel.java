package main.client.file.ui;

import main.client.file.listener.FileMetadataFindFromSubjectListener;

import javax.swing.*;
import java.awt.*;

public class FileSearchByKeywordPanel extends JPanel {

    public FileSearchByKeywordPanel(String sessionId, int offset, String keyword, Component mainFrame) {

        setBounds(15, 15, 1500, 130);

        JLabel searchLabel = new JLabel("SEARCH :");
        searchLabel.setBounds(316, 21, 120, 36);
        searchLabel.setFont(new Font("Lucida Sans", Font.BOLD, 22));
        add(searchLabel);


        JTextField searchInput = new JTextField(keyword);
        searchInput.setHorizontalAlignment(SwingConstants.TRAILING);
        searchInput.setBounds(446, 21, 650, 36);
        searchInput.setFont(new Font("SansSerif", Font.PLAIN, 15));
        add(searchInput);
        searchInput.setColumns(20);

        JButton searchButton = new JButton("🔎");
        searchButton.setBounds(1123, 21, 61, 36);
        searchButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searchButton.setForeground(Color.BLACK);
        searchButton.setBackground(UIManager.getColor("Table.selectionBackground"));
        searchButton.addActionListener(new FileMetadataFindFromSubjectListener(sessionId, searchInput, offset, mainFrame));
        add(searchButton);
    }
}
