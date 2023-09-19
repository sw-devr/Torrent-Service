package main.client.file.ui;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class ProgressBarFrame extends JFrame {

    private MyLabelBar bar = null;
    private MyLabelText progressRatesLabel = new MyLabelText("Loading...");
    private MyLabelText progressSubjectLabel = new MyLabelText("Loading...");
    private final  int totalSize;

    public ProgressBarFrame(String sessionId, int totalSize, String uploadPath) {

        super("Progress");
        this.totalSize = totalSize;
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        Container c = getContentPane();
        c.setLayout(null);

        bar = new MyLabelBar(100);
        bar.setBackground(Color.WHITE);
        bar.setOpaque(true);
        bar.setLocation(20, 50);
        bar.setSize(300, 20);

        int calibration = 5;
        int progressLabelPositionY = bar.getSize().height + bar.getLocation().y + 10; //vGap=10
        progressRatesLabel.setLocation(20,  progressLabelPositionY);
        progressRatesLabel.setSize(bar.getSize().width/2 - calibration , 20);
        int progressSubjectLabelPostionX = progressRatesLabel.getX() + progressRatesLabel.getSize().width + 10; //hGap = 10
        progressSubjectLabel.setLocation(progressSubjectLabelPostionX,  progressLabelPositionY);
        progressSubjectLabel.setSize(bar.getSize().width/2 - calibration , 20);

        //화면 레이블의 레이아웃을 보고 싶을 때 true로 입력한다.
        //flag - true 개발자 모드
        backgroundColor(false);

        c.add(bar);
        c.add(progressRatesLabel);
        c.add(progressSubjectLabel);

        setSize(350, 200);

        setVisible(true);
    }

    private class MyLabelText extends JLabel{
        private int progressRates = 0;

        public MyLabelText(String initTxt) {
            super(initTxt);
        }

        public void setTextProgressRates(int progressRates) {
            // 초기화
            if(this.progressRates == progressRates ) {
                progressRatesLabel.setText( progressRates +" %" );
            }else {
                try {
                    for(int rates=this.progressRates ;  rates <= progressRates ;  rates++) {
                        Thread.sleep(10);
                        progressRatesLabel.setText( rates +" %"  );
                    }
                    this.progressRates = progressRates;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void setTextProgressRates(String message) {
            try {
                Thread.sleep(15);
                progressSubjectLabel.setText( message  );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    public void backgroundColor(boolean flag) {
        if(flag) {
            progressRatesLabel.setBackground(Color.BLUE);
            progressRatesLabel.setOpaque(flag);
            progressSubjectLabel.setBackground(Color.GREEN);
            progressSubjectLabel.setOpaque(flag);
        }else {
            progressRatesLabel.setBackground(null);
            progressRatesLabel.setOpaque(flag);
            progressSubjectLabel.setBackground(null);
            progressSubjectLabel.setOpaque(flag);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public void setProgress(int progress) {
        setProgress(progress, "");
    }

    public void setProgress(int progress, String message)  {

        try {
            SwingUtilities.invokeAndWait(() -> new Thread(() -> {
                progressSubjectLabel.setTextProgressRates(message);
                progressRatesLabel.setTextProgressRates(progress);
            }).start());
        } catch (InvocationTargetException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        bar.setProgress(progress);

        if(progress == totalSize) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.terminated();
        }
    }

    private void terminated() {
        this.dispose();
    }


    private class MyLabelBar extends JLabel{
        private int barSize = 0;
        private int maxBarSize;
        private int originBarSize = 0;

        public MyLabelBar(int maxBarSize) {
            this.maxBarSize = maxBarSize;
        }

        @Override
        public void paint(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.GREEN);
            int width = (int) ( ( (double) (this.getWidth() )) / maxBarSize*barSize  );
            if(width == 0) return;
            g.fillRect(0, 0, width, getHeight());
        }

        public void setProgress(int progress) {
            this.originBarSize = this.barSize;
            try {
                for(int rates=this.originBarSize; rates <= progress ;rates++){
                    this.barSize = rates;
                    Thread.sleep(10);
                    repaint();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
