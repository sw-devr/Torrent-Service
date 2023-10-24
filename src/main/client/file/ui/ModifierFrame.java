package main.client.file.ui;

import main.client.file.listener.FileMetadataDeleteListener;
import main.client.file.listener.FileMetadataUpdateListener;
import main.client.user.handler.UserHandler;
import main.server.file.metadata.FileMetadata;
import main.server.user.ResponseUserDto;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ModifierFrame extends JFrame {

    private JTextField subjectTextField;
    private JTextField sizeTextField;
    private JTextField priceTextField;
    private JTextField createdTimestampTextField;
    private JTextField downloadCntTextField;
    private JTextArea descriptionTextArea;
    private final FileMetadata fileMetadata;

    public ModifierFrame(String sessionId, FileMetadata fileMetadata, JComponent beforeFrame) {

        this.fileMetadata = fileMetadata;

        UserHandler userHandler = new UserHandler();
        ResponseUserDto consumer = userHandler.getUser(sessionId);

        setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\교육생06\\Desktop\\logo.jpg"));
        getContentPane().setBackground(new Color(255, 255, 255));
        getContentPane().setLayout(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(186, 219, 241));
        panel.setBounds(0, 0, 986, 663);
        getContentPane().add(panel);
        panel.setLayout(null);


        // Subject
        JLabel subjectLabel = new JLabel("Subject");
        subjectLabel.setBounds(147, 84, 68, 33);
        panel.add(subjectLabel);
        subjectLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));

        subjectTextField = new JTextField(fileMetadata.getSubject());
        subjectTextField.setHorizontalAlignment(SwingConstants.CENTER);
        subjectTextField.setBounds(326, 80, 563, 37);
        panel.add(subjectTextField);
        subjectTextField.setForeground(Color.DARK_GRAY);
        subjectTextField.setEditable(true);
        subjectTextField.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        subjectTextField.setColumns(10);
        subjectTextField.setBackground(new Color(186, 219, 241));
        subjectTextField.setBorder(new LineBorder(Color.BLACK, 2));


        // Size
        JLabel sizeLabel = new JLabel("Size");
        sizeLabel.setBounds(147, 155, 68, 33);
        panel.add(sizeLabel);
        sizeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));

        int size = fileMetadata.getSize();
        double KBytes = ((double) size) / 1024;

        sizeTextField = new JTextField(String.format("%.1f KB", KBytes));
        sizeTextField.setHorizontalAlignment(SwingConstants.CENTER);
        sizeTextField.setBounds(326, 151, 158, 41);
        panel.add(sizeTextField);
        sizeTextField.setEditable(false);
        sizeTextField.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        sizeTextField.setColumns(10);
        sizeTextField.setBackground(new Color(186, 219, 241));
        sizeTextField.setBorder(new LineBorder(Color.BLACK, 2));


        // Price
        JLabel priceLabel = new JLabel("Price");
        priceLabel.setBounds(147, 211, 68, 33);
        panel.add(priceLabel);
        priceLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));

        priceTextField = new JTextField(Integer.toString(fileMetadata.getPrice()));
        priceTextField.setHorizontalAlignment(SwingConstants.CENTER);
        priceTextField.setBounds(326, 207, 158, 41);
        panel.add(priceTextField);
        priceTextField.setEditable(true);
        priceTextField.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        priceTextField.setColumns(10);
        priceTextField.setBackground(new Color(186, 219, 241));
        priceTextField.setBorder(new LineBorder(Color.BLACK, 2));


        // CreatedTinestamp
        JLabel createdTimestampLabel = new JLabel("CreatedTimestamp");
        createdTimestampLabel.setBounds(532, 211, 166, 33);
        panel.add(createdTimestampLabel);
        createdTimestampLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        String date = sf.format(new Date(fileMetadata.getCreatedTimestamp()));

        createdTimestampTextField = new JTextField(date);
        createdTimestampTextField.setHorizontalAlignment(SwingConstants.CENTER);
        createdTimestampTextField.setBounds(725, 207, 158, 41);
        panel.add(createdTimestampTextField);
        createdTimestampTextField.setEditable(false);
        createdTimestampTextField.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        createdTimestampTextField.setColumns(10);
        createdTimestampTextField.setBackground(new Color(186, 219, 241));
        createdTimestampTextField.setBorder(new LineBorder(Color.BLACK, 2));

        // Download
        JLabel downloadCntLabel = new JLabel("DownloadCnt");
        downloadCntLabel.setBounds(532, 151, 128, 33);
        panel.add(downloadCntLabel);
        downloadCntLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));

        downloadCntTextField = new JTextField(Integer.toString(fileMetadata.getDownloadCnt()));
        downloadCntTextField.setHorizontalAlignment(SwingConstants.CENTER);
        downloadCntTextField.setBounds(725, 151, 57, 41);
        panel.add(downloadCntTextField);
        downloadCntTextField.setEditable(false);
        downloadCntTextField.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        downloadCntTextField.setColumns(10);
        downloadCntTextField.setBackground(new Color(186, 219, 241));
        downloadCntTextField.setBorder(new LineBorder(Color.BLACK, 2));

        // Description
        JLabel descriptionLabel = new JLabel("Description");
        descriptionLabel.setBounds(147, 335, 104, 33);
        panel.add(descriptionLabel);
        descriptionLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));

        descriptionTextArea = new JTextArea(fileMetadata.getDescription());
        descriptionTextArea.setBounds(326, 299, 488, 215);
        panel.add(descriptionTextArea);
        descriptionTextArea.setEditable(true);
        descriptionTextArea.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        descriptionTextArea.setBackground(new Color(186, 219, 241));
        descriptionTextArea.setBorder(new LineBorder(Color.BLACK, 2));

        JButton updateButton = new JButton("수 정");
        updateButton.setBounds(238, 555, 182, 50);
        panel.add(updateButton);
        updateButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        updateButton.addActionListener(new FileMetadataUpdateListener(sessionId, fileMetadata.getId(), consumer.getUserId(),
                priceTextField, subjectTextField, descriptionTextArea, beforeFrame, this));


        JButton deleteButton = new JButton("삭 제");
        deleteButton.setBounds(538, 555, 182, 50);
        panel.add(deleteButton);
        deleteButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        deleteButton.addActionListener(new FileMetadataDeleteListener(sessionId, fileMetadata.getId(), consumer.getUserId(), beforeFrame, this));





        setBackground(new Color(255, 255, 255));
        setSize(1000,700);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("유저 업로딩 상세 파일 페이지");
    }

    public FileMetadata getFileMetadata() {
        return this.fileMetadata;
    }
}
