package main.client.file.ui;

import main.client.file.listener.FileMetadataCreateListener;
import main.client.file.search.UserFileSearcher;
import main.client.payment.PaymentChargingPointListener;
import main.client.user.handler.UserHandler;
import main.client.user.listener.UserLogoutListener;
import main.client.user.listener.UserWithdrawListener;
import main.server.file.FileMetadata;
import main.server.user.ResponseUserDto;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UserManagementPanel extends JPanel {

    private final String sessionId;
    private final JFrame beforeFrame;

    public UserManagementPanel(String sessionId, JFrame beforeFrame) {

        this.sessionId = sessionId;
        this.beforeFrame = beforeFrame;

        UserHandler userHandler = new UserHandler();
        ResponseUserDto user = userHandler.getUser(sessionId);
        System.out.println("USER : " + user);

        setBackground(new Color(255, 255, 255));
        setLayout(null);

        JTabbedPane userManageTabbedPane = new JTabbedPane(JTabbedPane.TOP);
        userManageTabbedPane.setFont(new Font("SansSerif", Font.BOLD, 12));
        userManageTabbedPane.setBounds(53, 105, 1141, 474);
        add(userManageTabbedPane);


        //유저의 파일 목록 Panel
        UserFileSearcher userFileSearcher = new UserFileSearcher(user.getUserId());
        List<FileMetadata> fileMetadataList = userFileSearcher.getFileMetadataList(sessionId, 0);

        System.out.println(fileMetadataList);
        JTable fileSearchTable = new SearchTable(sessionId, fileMetadataList, beforeFrame);
        JScrollPane fileSearchScroll = new JScrollPane(fileSearchTable);
        fileSearchScroll.setBackground(new Color(255, 255, 255));
        add(fileSearchScroll);

        userManageTabbedPane.addTab("유저 파일 목록", null, fileSearchScroll, null);
        userManageTabbedPane.addTab("파일 업로드", null, getUploadPanel(sessionId), null);
        userManageTabbedPane.addTab("포인트 충전", null, getChargingPanel(user), null);

        JLabel daisoImage_1 = new JLabel("New label");
        daisoImage_1.setIcon(new ImageIcon("C:\\Users\\교육생06\\Desktop\\image.png"));
        daisoImage_1.setBounds(53, 26, 200, 52);
        add(daisoImage_1);

        JLabel emailLabel = new JLabel("E-maill");
        emailLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        emailLabel.setBounds(349, 33, 139, 33);
        add(emailLabel);

        JTextField emailTextField = new JTextField(user.getEmail());
        emailTextField.setFont(new Font("SansSerif", Font.BOLD, 18));
        emailTextField.setEditable(false);
        emailTextField.setBounds(481, 33, 373, 33);
        add(emailTextField);
        emailTextField.setColumns(10);


        JLabel gradeLabel = new JLabel("Grade");
        gradeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gradeLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        gradeLabel.setBounds(349, 74, 139, 33);
        add(gradeLabel);

        JTextField userGradeText = new JTextField(user.getRole().toString());
        userGradeText.setEditable(false);
        userGradeText.setFont(new Font("SansSerif", Font.BOLD, 18));
        userGradeText.setBounds(481, 74, 139, 37);
        add(userGradeText);

        JButton logoutButton = new JButton("로그아웃");
        logoutButton.addActionListener(new UserLogoutListener(sessionId, beforeFrame));
        logoutButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        logoutButton.setBounds(1058, 33, 122, 33);
        add(logoutButton);

        JButton withdrawButton = new JButton("회원탈퇴");
        withdrawButton.addActionListener(new UserWithdrawListener(sessionId, beforeFrame));
        withdrawButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        withdrawButton.setBounds(1058, 83, 122, 33);
        add(withdrawButton);


        /*JTable table = new JTable();
        table.setFont(new Font("SansSerif", Font.BOLD, 15));
        table.setBounds(58, 103, 1125, 494);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(92, 179, 255));
        header.setForeground(new Color(255, 255, 255));*/

        setVisible(true);
    }

    private JPanel getUploadPanel(String sessionId) {
        JPanel userManageUploadPanel = new JPanel();

        userManageUploadPanel.setBackground(new Color(255, 255, 255));
        userManageUploadPanel.setLayout(null);

        JLabel subjectLabel = new JLabel("Subject");
        subjectLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        subjectLabel.setBounds(120, 105, 82, 44);
        userManageUploadPanel.add(subjectLabel);

        JLabel descriptionLabel = new JLabel("Description");
        descriptionLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        descriptionLabel.setBounds(120, 159, 110, 44);
        userManageUploadPanel.add(descriptionLabel);

        JLabel priceLabel = new JLabel("Price");
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        priceLabel.setBounds(120, 265, 82, 44);
        userManageUploadPanel.add(priceLabel);

        JLabel filepathLabel = new JLabel("FilePath");
        filepathLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        filepathLabel.setBounds(120, 319, 82, 44);
        userManageUploadPanel.add(filepathLabel);

        JTextField subjectTextField = new JTextField();
        subjectTextField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subjectTextField.setBounds(277, 113, 540, 30);
        userManageUploadPanel.add(subjectTextField);
        subjectTextField.setColumns(10);

        JTextField descriptionTextField = new JTextField();
        descriptionTextField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        descriptionTextField.setColumns(10);
        descriptionTextField.setBounds(277, 168, 540, 83);
        userManageUploadPanel.add(descriptionTextField);

        JTextField priceTextField = new JTextField();
        priceTextField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        priceTextField.setColumns(10);
        priceTextField.setBounds(277, 275, 540, 30);
        userManageUploadPanel.add(priceTextField);

        JTextField filepathTextField = new JTextField();
        filepathTextField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        filepathTextField.setColumns(10);
        filepathTextField.setBounds(277, 330, 540, 30);
        userManageUploadPanel.add(filepathTextField);

        JButton uploadButton = new JButton("Upload");
        uploadButton.addActionListener(
                new FileMetadataCreateListener(sessionId, subjectTextField,
                        descriptionTextField, priceTextField, filepathTextField)
        );

        uploadButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        uploadButton.setBounds(904, 183, 151, 103);
        userManageUploadPanel.add(uploadButton);

        return userManageUploadPanel;
    }

    private JPanel getChargingPanel(ResponseUserDto user) {

        JPanel userManageChargePanel = new JPanel();
        userManageChargePanel.setBackground(new Color(255, 255, 255));
        userManageChargePanel.setLayout(null);

        JTextField ownPointTextField = new JTextField(Long.toString(user.getPoints()));
        ownPointTextField.setHorizontalAlignment(SwingConstants.TRAILING);
        ownPointTextField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        ownPointTextField.setEditable(false);
        ownPointTextField.setColumns(10);
        ownPointTextField.setBounds(314, 115, 449, 32);
        userManageChargePanel.add(ownPointTextField);

        JLabel ownPointLabel = new JLabel("현재 보유 포인트");
        ownPointLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ownPointLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        ownPointLabel.setBounds(135, 114, 156, 32);
        userManageChargePanel.add(ownPointLabel);

        JLabel P = new JLabel("P");
        P.setFont(new Font("SansSerif", Font.BOLD, 20));
        P.setBounds(777, 115, 22, 32);
        userManageChargePanel.add(P);

        JLabel toChargeLabel = new JLabel("충전하실 금액");
        toChargeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        toChargeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        toChargeLabel.setBounds(138, 242, 138, 32);
        userManageChargePanel.add(toChargeLabel);

        JTextField toChargeTextField = new JTextField();
        toChargeTextField.setHorizontalAlignment(SwingConstants.TRAILING);
        toChargeTextField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        toChargeTextField.setColumns(10);
        toChargeTextField.setBounds(288, 245, 522, 32);
        userManageChargePanel.add(toChargeTextField);

        JLabel KRW = new JLabel("KRW");
        KRW.setFont(new Font("SansSerif", Font.BOLD, 18));
        KRW.setBounds(822, 242, 44, 32);
        userManageChargePanel.add(KRW);

        JButton chargeButton = new JButton("Charge");
        chargeButton.addActionListener(new PaymentChargingPointListener(sessionId, user.getUserId(), toChargeTextField, beforeFrame));
        chargeButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        chargeButton.setBounds(922, 232, 103, 57);
        userManageChargePanel.add(chargeButton);

        return userManageChargePanel;
    }
}
