package main.server.file;

import main.server.common.CommonConstants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static main.server.common.CommonConstants.CSV_COLUMN_SEPARATOR;

public class CSVFileMetadataRepository implements FileMetadataRepository{

    private final Path csvFilePath;
    private final Path tempCsvFilePath;

    public CSVFileMetadataRepository(String csvFile) {
        this.csvFilePath = Paths.get(csvFile);
        tempCsvFilePath = Paths.get(CommonConstants.createTempFilePath(csvFile)); //변경해야할 부분
    }

    @Override
    public FileMetadata findById(long id) {

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toFile()))) {
            String line = br.readLine();
            while((line = br.readLine()) != null) {
                FileMetadata fileMetadata = parseFileDto(line);
                if(fileMetadata.getId() == id) {
                    return fileMetadata;
                }
            }
            return null;
        } catch (IOException e) {
            System.out.println("파일 메타데이터를 읽어오는 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }
    }


    @Override
    public FileMetadata findByPath(String filePath) {

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toFile()))) {
            String line = br.readLine();
            while((line = br.readLine()) != null) {
                FileMetadata fileMetadata = parseFileDto(line);
                if(fileMetadata.getPath().equals(filePath)) {
                    return fileMetadata;
                }
            }
            return null;
        } catch (IOException e) {
            System.out.println("파일 메타데이터를 읽어오는 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<FileMetadata> findBySubject(String fileSubject, int offset, int defaultPagingSize) {

        List<FileMetadata> answer = new ArrayList<>();
        int currentCnt = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toFile()))) {
            String line = br.readLine();
            while((line = br.readLine()) != null) {
                FileMetadata fileMetadata = parseFileDto(line);
                if(fileMetadata.getSubject().contains(fileSubject)) {
                    if(currentCnt < offset) {
                        currentCnt++;
                        continue;
                    }
                    answer.add(fileMetadata);
                    if(answer.size() == defaultPagingSize) {
                        break;
                    }
                }
            }
            return answer;
        } catch (IOException e) {
            System.out.println("파일 메타데이터를 읽어오는 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<FileMetadata> findByUserId(long userId, int offset, int defaultPagingSize) {

        List<FileMetadata> answer = new ArrayList<>();
        int currentCnt = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toFile()))) {
            String line = br.readLine();
            while((line = br.readLine()) != null) {
                FileMetadata fileMetadata = parseFileDto(line);
                if(fileMetadata.getUserId() == userId) {
                    if(currentCnt < offset) {
                        currentCnt++;
                        continue;
                    }
                    answer.add(fileMetadata);
                    if(answer.size() == defaultPagingSize) {
                        break;
                    }
                }
            }
            return answer;
        } catch (IOException e) {
            System.out.println("파일 메타데이터를 읽어오는 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<FileMetadata> findAll(int offset, int size) {

        int cnt = -1;
        try(BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toFile()))) {
            while(br.readLine() != null) {
                cnt++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<FileMetadata> realFileMetadata = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toFile()))) {

            int start = Math.max(1, cnt-offset-size);
            int end = Math.max(1, cnt-offset);

            for(int i=0;i<start;i++) {
                if(br.readLine() == null) break;
            }
            for(int i = start; i <= end; i++) {
                String line = br.readLine();
                if(line == null) {
                    break;
                }
                realFileMetadata.add(parseFileDto(line));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<FileMetadata> answer = new ArrayList<>();
        for(int i = realFileMetadata.size()-1; i>=0; i--) {
            answer.add(realFileMetadata.get(i));
        }

        return answer;
    }

    @Override
    public void save(FileMetadata fileMetadata) {

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(csvFilePath.toFile(), true))) {

            String line = pasrseString(fileMetadata);
            bw.newLine();
            bw.write(line);
            bw.flush();
        } catch (IOException e) {
            System.out.println("파일 메타데이터 저장하는 과정에서 파일 입출력 예외가 발생함");
            throw new RuntimeException(e);
        }
    }


    @Override
    public void update(FileMetadata fileMetadata) {

        try(BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toFile()));
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempCsvFilePath.toFile()))) {

            String line = br.readLine(); //첫번째 줄은 컬럼명을 나타내기 때문에 읽지 않음
            bw.write(line);

            while((line = br.readLine()) != null) {
                FileMetadata candidateFileMetadata = parseFileDto(line);
                if(candidateFileMetadata.equals(fileMetadata)) {
                    line = pasrseString(fileMetadata);
                }
                bw.newLine();
                bw.write(line);
            }
            bw.flush();
        } catch (IOException e) {
            System.out.println("파일 메타데이터 업데이트 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }

        try {
            if(!Files.deleteIfExists(csvFilePath)) {
                throw new IllegalStateException("기존 존재햐야하는 파일메타데이터 데이터베이스 파일이 없습니다.");
            }
            if(!tempCsvFilePath.toFile().renameTo(csvFilePath.toFile())) {
                throw new IllegalStateException(
                        "새로 만들어진 파일메타데이터 데이터베이스 파일 이름을 기존 유저 데이터베이스 파일 이름으로 변경할 수 없습니다."
                );
            }
        } catch (IOException e) {
            System.out.println("파일메타데이터 업데이트 기능의 파일 삭제 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }

    }

    @Override
    public void delete(long id) {

        try(BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toFile()));
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempCsvFilePath.toFile()))) {

            String line = br.readLine(); //첫번째 줄은 컬럼명을 나타내기 때문에 읽지 않음
            bw.write(line);

            while((line = br.readLine()) != null) {
                FileMetadata candidateFileMetadata = parseFileDto(line);
                if(candidateFileMetadata.getId() == id) {
                    continue;
                }
                bw.newLine();
                bw.write(line);
            }
            bw.flush();
        } catch (IOException e) {
            System.out.println("파일 메타데이터 삭제 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }

        try {
            if(!Files.deleteIfExists(csvFilePath)) {
                throw new IllegalStateException("기존 존재햐야하는 파일메타데이터 데이터베이스 파일이 없습니다.");
            }
            if(!tempCsvFilePath.toFile().renameTo(csvFilePath.toFile())) {
                throw new IllegalStateException(
                        "새로 만들어진 파일메타데이터 데이터베이스 파일 이름을 기존 유저 데이터베이스 파일 이름으로 변경할 수 없습니다."
                );
            }
        } catch (IOException e) {
            System.out.println("파일메타데이터 삭제 기능의 파일 삭제 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAll(List<Long> fileIds) {

        fileIds.sort(Comparator.naturalOrder());

        try(BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toFile()));
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempCsvFilePath.toFile()))) {

            String line = br.readLine(); //첫번째 줄은 컬럼명을 나타내기 때문에 읽지 않음
            bw.write(line);

            int idx = 0;
            while((line = br.readLine()) != null) {
                FileMetadata candidateFileMetadata = parseFileDto(line);
                if(idx < fileIds.size() && candidateFileMetadata.getId() == fileIds.get(idx)) {
                    idx++;
                    continue;
                }
                bw.newLine();
                bw.write(line);
            }
            bw.flush();
        } catch (IOException e) {
            System.out.println("파일 메타데이터 삭제 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }

        try {
            if(!Files.deleteIfExists(csvFilePath)) {
                throw new IllegalStateException("기존 존재햐야하는 파일메타데이터 데이터베이스 파일이 없습니다.");
            }
            if(!tempCsvFilePath.toFile().renameTo(csvFilePath.toFile())) {
                throw new IllegalStateException(
                        "새로 만들어진 파일메타데이터 데이터베이스 파일 이름을 기존 유저 데이터베이스 파일 이름으로 변경할 수 없습니다."
                );
            }
        } catch (IOException e) {
            System.out.println("파일메타데이터 삭제 기능의 파일 삭제 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }
    }

    private String pasrseString(FileMetadata fileMetadata) {

        return fileMetadata.getId() + CSV_COLUMN_SEPARATOR +
                fileMetadata.getUserId() + CSV_COLUMN_SEPARATOR +
                fileMetadata.getPrice() + CSV_COLUMN_SEPARATOR +
                fileMetadata.getPath() + CSV_COLUMN_SEPARATOR +
                fileMetadata.getSubject() + CSV_COLUMN_SEPARATOR +
                fileMetadata.getDescription() + CSV_COLUMN_SEPARATOR +
                fileMetadata.getSize() + CSV_COLUMN_SEPARATOR +
                fileMetadata.getCreatedTimestamp() + CSV_COLUMN_SEPARATOR +
                fileMetadata.getDownloadCnt() + CSV_COLUMN_SEPARATOR +
                fileMetadata.getState().toString();
    }

    private FileMetadata parseFileDto(String line) {

        String[] columns = line.split(CSV_COLUMN_SEPARATOR);
        StringBuilder description = new StringBuilder();
        for(int i=5;i<columns.length-4;i++) {
            description.append(columns[i]);
            description.append(",");
        }
        description.delete(description.length()-1, description.length());

        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setId(Long.parseLong(columns[0]));
        fileMetadata.setUserId(Long.parseLong(columns[1]));
        fileMetadata.setPrice(Integer.parseInt(columns[2]));
        fileMetadata.setPath(columns[3]);
        fileMetadata.setSubject(columns[4]);
        fileMetadata.setDescription(description.toString());
        fileMetadata.setSize(Integer.parseInt(columns[columns.length-4]));
        fileMetadata.setCreatedTimestamp(Long.parseLong(columns[columns.length-3]));
        fileMetadata.setDownloadCnt(Integer.parseInt(columns[columns.length-2]));
        fileMetadata.setState(FileState.valueOf(columns[columns.length-1]));

        return fileMetadata;
    }
}

