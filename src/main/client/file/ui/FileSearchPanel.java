package main.client.file.ui;

import main.client.file.search.FileSearcher;
import main.server.file.FileMetadata;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static main.server.file.FileConstants.DEFAULT_PAGING_SIZE;

public class FileSearchPanel extends JPanel {

    private final JFrame beforeFrame;

    public FileSearchPanel(String sessionId, FileSearcher fileSearcher, int offset, JFrame beforeFrame) {

        this.beforeFrame = beforeFrame;

        setBounds(50, 160, 1139, 500);
        List<FileMetadata> fileMetadataList = fileSearcher.getFileMetadataList(sessionId, offset);

        setBackground(new Color(255, 255, 255));
        setForeground(new Color(255, 255, 255));
        setLayout(null);

        JLabel daisoImage = new JLabel("New label");
        daisoImage.setIcon(new ImageIcon("C:\\Users\\교육생06\\Desktop\\image.png"));
        daisoImage.setBounds(53, 26, 200, 52);
        add(daisoImage);

        JTable searchTable = new SearchTable(sessionId, fileMetadataList, beforeFrame);
        JScrollPane scrollPane = new JScrollPane(searchTable);
        scrollPane.setBounds(53, 113, 1139, 300);

        add(scrollPane, BorderLayout.CENTER);

        JLabel page1Label = new JLabel(Integer.toString((offset % DEFAULT_PAGING_SIZE) + 1));
        page1Label.setForeground(new Color(0, 0, 255));
        page1Label.setFont(new Font("굴림", Font.BOLD, 15));
        page1Label.setBounds(506, 600, 9, 18);
        add(page1Label);

        JLabel page2Label = new JLabel(Integer.toString((offset % (DEFAULT_PAGING_SIZE/2)) + 2));
        page2Label.setForeground(new Color(0, 0, 255));
        page2Label.setFont(new Font("굴림", Font.BOLD, 15));
        page2Label.setBounds(536, 600, 9, 18);
        add(page2Label);

        JLabel page3Label = new JLabel(Integer.toString((offset % DEFAULT_PAGING_SIZE) + 3));
        page3Label.setForeground(new Color(0, 0, 255));
        page3Label.setFont(new Font("굴림", Font.BOLD, 15));
        page3Label.setBounds(566, 600, 9, 18);
        add(page3Label);

        JLabel page4Label = new JLabel(Integer.toString((offset % DEFAULT_PAGING_SIZE) + 4));
        page4Label.setForeground(new Color(0, 0, 255));
        page4Label.setFont(new Font("굴림", Font.BOLD, 15));
        page4Label.setBounds(596, 600, 9, 18);
        add(page4Label);

        JLabel page5Label = new JLabel(Integer.toString((offset % DEFAULT_PAGING_SIZE) + 5));
        page5Label.setForeground(new Color(0, 0, 255));
        page5Label.setFont(new Font("굴림", Font.BOLD, 15));
        page5Label.setBounds(626, 600, 9, 18);
        add(page5Label);

        JLabel pageNextLabel = new JLabel("다음 >");
        pageNextLabel.setForeground(new Color(0, 0, 255));
        pageNextLabel.setFont(new Font("굴림", Font.BOLD, 15));
        pageNextLabel.setBounds(653, 595, 50, 30);
        add(pageNextLabel);
    }
}
