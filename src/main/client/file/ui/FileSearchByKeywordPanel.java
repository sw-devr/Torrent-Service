package main.client.file.ui;

import main.client.file.listener.FileMetadataFindFromSubjectListener;

import javax.swing.*;
import java.awt.*;

public class FileSearchByKeywordPanel extends JPanel {


    public FileSearchByKeywordPanel(String sessionId, int offset, String keyword, Component mainFrame) {
        setBackground(new Color(255, 255, 255));

        setBounds(0, 0, 1260, 130);
        setLayout(null);

        JLabel daisoImage = new JLabel("New label");
        daisoImage.setIcon(new ImageIcon("C:\\Users\\교육생06\\Desktop\\image.png"));
        daisoImage.setBounds(53, 26, 200, 52);
        add(daisoImage);

        JLabel searchLabel = new JLabel("SEARCH :");
        searchLabel.setBounds(553, 47, 106, 27);
        searchLabel.setFont(new Font("Lucida Sans", Font.BOLD, 22));
        add(searchLabel);


        JTextField searchInput = new JTextField(keyword);
        searchInput.setHorizontalAlignment(SwingConstants.TRAILING);
        searchInput.setBounds(664, 46, 266, 28);
        searchInput.setFont(new Font("SansSerif", Font.PLAIN, 15));
        add(searchInput);
        searchInput.setColumns(20);

        JButton searchButton = new JButton("🔎");
        searchButton.setBounds(935, 43, 57, 35);
        searchButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searchButton.setForeground(Color.BLACK);
        searchButton.setBackground(UIManager.getColor("Table.selectionBackground"));
        searchButton.addActionListener(new FileMetadataFindFromSubjectListener(sessionId, searchInput, 0, mainFrame));
        add(searchButton);
    }
}
