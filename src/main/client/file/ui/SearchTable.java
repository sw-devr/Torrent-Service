package main.client.file.ui;

import main.server.file.FileMetadata;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchTable extends JTable {

    private final String sessionId;
    private final JComponent beforeFrame;
    private final String searchOption;

    public SearchTable(String sessionId, List<FileMetadata> fileMetadataList, String searchOption, JComponent beforeFrame) {

        this.sessionId = sessionId;
        this.beforeFrame = beforeFrame;
        this.searchOption = searchOption;

        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public int getColumnCount() {
                return 6; // FileMetadata 클래스의 속성 개수에 맞춰 열 개수 설정
            }

            @Override
            public String getColumnName(int columnIndex) {
                // 각 열의 이름 설정
                switch (columnIndex) {
                    case 0:
                        return "Subject";
                    case 1:
                        return "Description";
                    case 2:
                        return "Price";
                    case 3:
                        return "Size";
                    case 4:
                        return "CreatedTimestamp";
                    case 5:
                        return "DownloadCnt";
                    default:
                        return "";
                }
            }

            @Override
            public int getRowCount() {
                return fileMetadataList.size();
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {

                FileMetadata metadata = fileMetadataList.get(rowIndex);
                // 각 열에 해당하는 FileMetadata 속성 반환
                switch (columnIndex) {
                    case 0:
                        return metadata.getSubject();
                    case 1:
                        return metadata.getDescription();
                    case 2:
                        return metadata.getPrice();
                    case 3: {
                        int size = metadata.getSize();
                        double KBytes = ((double) size) / 1024;
                        return String.format("%.1f KB", KBytes);
                    }
                    case 4:{
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
                        return format.format(new Date(metadata.getCreatedTimestamp()));
                    }

                    case 5:
                        return metadata.getDownloadCnt();
                    default:
                        return "";
                }
            }
        };

        setModel(tableModel);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { // 단일 클릭 이벤트 처리
                    int selectedRow = getSelectedRow();
                    if (selectedRow != -1) {
                        // 선택된 파일 메타데이터 가져오기
                        FileMetadata selectedMetadata = fileMetadataList.get(selectedRow);

                        // 선택된 파일 메타데이터를 기반으로 상세 페이지로 이동
                        showDetailPage(selectedMetadata);
                    }
                }
            }
        });
    }

    private void showDetailPage(FileMetadata fileMetadata) {

        if(searchOption.equals("user")) {
            ModifierFrame downloadFrame = new ModifierFrame(sessionId, fileMetadata, beforeFrame);
        }else {
            DownloadFrame downloadFrame = new DownloadFrame(sessionId, fileMetadata, beforeFrame);
        }

    }
}
