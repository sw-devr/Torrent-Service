package main.client.file.ui;

import main.client.file.search.AllFileSearcher;
import main.client.file.search.SubjectFileSearcher;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FileMainFrame extends JFrame {

    private JPanel contentPane;
    private final String sessionId;

    public FileMainFrame(String sessionId, String searchOperator, String keyword, int offset) {
            this.sessionId = sessionId;

            setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\교육생06\\Desktop\\logo.jpg"));
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setSize(1350, 800);
            setTitle("파일다이소");
            contentPane = new JPanel();
            contentPane.setBackground(new Color(186, 219, 241));
            contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
            setLocationRelativeTo(null);

            setContentPane(contentPane);
            contentPane.setLayout(null);

            JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
            tabbedPane.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            tabbedPane.setBackground(new Color(255, 255, 255));
            tabbedPane.setBounds(45, 45, 1260, 680);
            tabbedPane.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
            contentPane.add(tabbedPane);

            JPanel searchPanel = new JPanel();
            searchPanel.setLayout(null);
            searchPanel.add(new FileSearchByKeywordPanel(sessionId, offset, keyword, this));

            // 파일리스트
            if(searchOperator.equals("all")) {
                searchPanel.add(new FileSearchPanel(sessionId, new AllFileSearcher(), searchOperator, offset, searchPanel));
            }
            else if(searchOperator.equals("subject")) {
                searchPanel.add( new FileSearchPanel(sessionId, new SubjectFileSearcher(keyword),searchOperator, offset, searchPanel));
            }

            tabbedPane.addTab("파일 목록", null, searchPanel, null);

            // 파일관리
            tabbedPane.addTab("유저 관리", null, new UserManagementPanel(sessionId, this), null);

            setVisible(true);
    }
}
