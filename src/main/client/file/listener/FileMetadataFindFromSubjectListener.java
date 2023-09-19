package main.client.file.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.client.file.ui.FileMainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FileMetadataFindFromSubjectListener implements ActionListener {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String sessionId;
    private final JTextField subjectText;
    private final int offset;
    private final Component beforeFrame;

    public FileMetadataFindFromSubjectListener(String sessionId, JTextField subjectText, int offset, Component beforeFrame) {
        this.subjectText = subjectText;
        this.sessionId = sessionId;
        this.offset = offset;
        this.beforeFrame = beforeFrame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String keyword = subjectText.getText();
        if(keyword.isEmpty()) {
            FileMainFrame mainFrame = new FileMainFrame(sessionId, "all", null, offset);
        }else {
            FileMainFrame mainFrame = new FileMainFrame(sessionId, "subject", keyword, offset);
        }
        beforeFrame.setVisible(false);
    }
}
