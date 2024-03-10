package main.client.file.ui;

import main.client.file.search.FileSearcher;
import main.server.file.metadata.FileMetadata;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static main.server.file.FileConstants.DEFAULT_PAGING_SIZE;

public class FileSearchPanel extends JPanel {

    private JPanel thisPanel = this;
    private final JComponent beforeFrame;

    public FileSearchPanel(String sessionId, FileSearcher fileSearcher, String operator, int offset, JComponent beforeFrame) {

        this.beforeFrame = beforeFrame;

        setBounds(0, 130, 1260, 550);
        List<FileMetadata> fileMetadataList = fileSearcher.getFileMetadataList(sessionId, offset);

        setBackground(new Color(255, 255, 255));
        setForeground(new Color(255, 255, 255));
        setLayout(null);

        JTable searchTable = new SearchTable(sessionId, fileMetadataList, operator, beforeFrame);
        JScrollPane scrollPane = new JScrollPane(searchTable);
        scrollPane.setBounds(50, 57, 1139, 398);

        add(scrollPane);

        JLabel pageBeforeLabel = new JLabel("< 이전");
        pageBeforeLabel.setForeground(new Color(0, 0, 255));
        pageBeforeLabel.setFont(new Font("굴림", Font.BOLD, 15));
        pageBeforeLabel.setBounds(480, 482, 50, 30);
        pageBeforeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPanel searchPanel = new FileSearchPanel(sessionId, fileSearcher, operator,  Math.max(0, offset - DEFAULT_PAGING_SIZE*5), beforeFrame);
                beforeFrame.add(searchPanel);
                thisPanel.setVisible(false);
                beforeFrame.remove(thisPanel);

            }
        });
        add(pageBeforeLabel);

        JLabel page1Label = new JLabel(Integer.toString((offset / DEFAULT_PAGING_SIZE) + 1));
        page1Label.setForeground(new Color(0, 0, 255));
        page1Label.setFont(new Font("굴림", Font.BOLD, 15));
        page1Label.setBounds(536, 487, 30, 18);
        page1Label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPanel searchPanel = new FileSearchPanel(sessionId, fileSearcher, operator, offset, beforeFrame);
                beforeFrame.add(searchPanel);
                thisPanel.setVisible(false);
                beforeFrame.remove(thisPanel);

            }
        });
        add(page1Label);

        JLabel page2Label = new JLabel(Integer.toString((offset / (DEFAULT_PAGING_SIZE)) + 2));
        page2Label.setForeground(new Color(0, 0, 255));
        page2Label.setFont(new Font("굴림", Font.BOLD, 15));
        page2Label.setBounds(571, 487, 30, 18);
        page2Label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPanel searchPanel = new FileSearchPanel(sessionId, fileSearcher, operator, offset + DEFAULT_PAGING_SIZE, beforeFrame);
                beforeFrame.add(searchPanel);
                thisPanel.setVisible(false);
                beforeFrame.remove(thisPanel);

            }
        });
        add(page2Label);

        JLabel page3Label = new JLabel(Integer.toString((offset / DEFAULT_PAGING_SIZE) + 3));
        page3Label.setForeground(new Color(0, 0, 255));
        page3Label.setFont(new Font("굴림", Font.BOLD, 15));
        page3Label.setBounds(606, 487, 30, 18);
        page3Label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPanel searchPanel = new FileSearchPanel(sessionId, fileSearcher, operator, offset + DEFAULT_PAGING_SIZE*2, beforeFrame);
                beforeFrame.add(searchPanel);
                thisPanel.setVisible(false);
                beforeFrame.remove(thisPanel);

            }
        });
        add(page3Label);

        JLabel page4Label = new JLabel(Integer.toString((offset / DEFAULT_PAGING_SIZE) + 4));
        page4Label.setForeground(new Color(0, 0, 255));
        page4Label.setFont(new Font("굴림", Font.BOLD, 15));
        page4Label.setBounds(641, 487, 30, 18);
        page4Label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPanel searchPanel = new FileSearchPanel(sessionId, fileSearcher, operator,  offset + DEFAULT_PAGING_SIZE*3, beforeFrame);
                beforeFrame.add(searchPanel);
                thisPanel.setVisible(false);
                beforeFrame.remove(thisPanel);

            }
        });
        add(page4Label);

        JLabel page5Label = new JLabel(Integer.toString((offset / DEFAULT_PAGING_SIZE) + 5));
        page5Label.setForeground(new Color(0, 0, 255));
        page5Label.setFont(new Font("굴림", Font.BOLD, 15));
        page5Label.setBounds(676, 487, 30, 18);
        page5Label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPanel searchPanel = new FileSearchPanel(sessionId, fileSearcher, operator,  offset + DEFAULT_PAGING_SIZE*4, beforeFrame);
                beforeFrame.add(searchPanel);
                thisPanel.setVisible(false);
                beforeFrame.remove(thisPanel);

            }
        });
        add(page5Label);

        JLabel pageNextLabel = new JLabel("다음 >");
        pageNextLabel.setForeground(new Color(0, 0, 255));
        pageNextLabel.setFont(new Font("굴림", Font.BOLD, 15));
        pageNextLabel.setBounds(703, 482, 50, 30);
        pageNextLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPanel searchPanel = new FileSearchPanel(sessionId, fileSearcher, operator,  offset + DEFAULT_PAGING_SIZE*5, beforeFrame);
                beforeFrame.add(searchPanel);
                thisPanel.setVisible(false);
                beforeFrame.remove(thisPanel);

            }
        });
        add(pageNextLabel);
    }
}
