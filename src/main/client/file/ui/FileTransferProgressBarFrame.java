package main.client.file.ui;

import javax.swing.*;
import java.awt.*;

public class FileTransferProgressBarFrame extends JFrame {
    private JProgressBar progressBar;
    private Timer timer;
    private int progressValue;

    public FileTransferProgressBarFrame(int fileSize) {
        setTitle("File Transfer Progress");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 100);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true); // 진행 퍼센트를 표시하기 위해 설정

        // 타이머를 사용하여 진행 바 업데이트
        timer = new Timer(100, e -> {
            // 진행 바를 업데이트하는 작업 수행 (예: 파일 전송 상황에 따라)
            // 이 예제에서는 무작위로 진행 상황을 증가시킵니다.
            progressBar.setValue((int)(progressValue/fileSize));

            if (progressValue >= fileSize) {
                ((Timer) e.getSource()).stop(); // 파일 전송이 완료되면 타이머 중지
            }
        });

        add(progressBar, BorderLayout.CENTER);

        progressValue = 0;
        progressBar.setValue((int)(progressValue/fileSize));
        timer.start();
    }

    public void add(long size) {
        progressValue += size;
    }
}
