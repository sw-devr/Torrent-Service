package main.server.file;

import main.server.common.CommonConstants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static main.server.common.CommonConstants.CSV_COLUMN_SEPARATOR;

public class CSVFileMetadataRepository implements FileMetadataRepository{

    private final Path csvFilePath;
    private final Path tempCsvFilePath;
    private final Path idFilePath;

    public CSVFileMetadataRepository(String csvFile, String idFilePath) {
        this.csvFilePath = Paths.get(csvFile);
        this.tempCsvFilePath = Paths.get(CommonConstants.createTempFilePath(csvFile)); //변경해야할 부분
        this.idFilePath = Paths.get(idFilePath);
    }

    @Override
    public void save(FileMetadata fileMetadata) {

        long id;
        try(BufferedReader br = new BufferedReader(new FileReader(idFilePath.toFile()))) {
            id = Long.parseLong(br.readLine());
        } catch (IOException e) {
            System.out.println("파일 쓰는 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }
        try(BufferedWriter br = new BufferedWriter(new FileWriter(idFilePath.toFile()))) {
            br.write(Long.toString(id+1));
            br.flush();
        } catch (IOException e) {
            System.out.println("파일 쓰는 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(csvFilePath.toFile(), true))) {

            fileMetadata.setId(id);
            String line = pasrseString(fileMetadata);
            bw.write(line);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            System.out.println("파일 메타데이터 저장하는 과정에서 파일 입출력 예외가 발생함");
            throw new RuntimeException(e);
        }
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
    public List<FileMetadata> findBySubject(String fileSubject, int offset, int size) {

        if(offset < 0 || size < 0) {
            throw new IllegalArgumentException("잘못된 offset 값 또는 size 값입니다.");
        }
        List<FileMetadata> answer = new ArrayList<>();
        int totalCnt = 0;
        try(BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toFile()))) {

            String line = br.readLine();
            while((line = br.readLine()) != null) {
                FileMetadata fileMetadata = parseFileDto(line);
                if (!fileMetadata.getSubject().contains(fileSubject)) {
                    continue;
                }
                if(fileMetadata.getState() != FileState.AVAILABLE) {
                    continue;
                }
                totalCnt++;
            }
        } catch (IOException e) {
            System.out.println("파일 메타데이터를 읽어오는 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }
        int start = Math.max(totalCnt - (offset + size), 0);
        int end = Math.max(totalCnt - offset, 0);
        int currentCnt = 0;

        if(start >= end) {
            return answer;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toFile()))) {
            String line = br.readLine();
            while((line = br.readLine()) != null) {
                FileMetadata fileMetadata = parseFileDto(line);
                if(!fileMetadata.getSubject().contains(fileSubject)) {
                    continue;
                }
                if(fileMetadata.getState() != FileState.AVAILABLE) {
                    continue;
                }
                if(currentCnt < start) {
                    currentCnt++;
                    continue;
                }
                currentCnt++;
                answer.add(fileMetadata);

                if(currentCnt >= end) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("파일 메타데이터를 읽어오는 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }
        if(answer.size() > size) {
            throw new IllegalStateException("요청한 페이징 사이즈보다 많은 데이터를 가져왔습니다.");
        }
        Collections.reverse(answer);

        return answer;
    }


    @Override
    public List<FileMetadata> findByUserId(long userId, int offset, int size) {

        if(offset < 0 || size < 0) {
            throw new IllegalArgumentException("잘못된 offset 값 또는 size 값입니다.");
        }
        List<FileMetadata> answer = new ArrayList<>();
        int totalCnt = 0;
        try(BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toFile()))) {
            String line = br.readLine();
            while((line = br.readLine()) != null) {
                FileMetadata fileMetadata = parseFileDto(line);
                if (fileMetadata.getUserId() != userId) {
                    continue;
                }
                if(fileMetadata.getState() != FileState.AVAILABLE) {
                    continue;
                }
                totalCnt++;
            }
        } catch (IOException e) {
            System.out.println("파일 메타데이터를 읽어오는 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }
        int start = Math.max(totalCnt - (offset + size), 0);
        int end = Math.max(totalCnt - offset, 0);
        int currentCnt = 0;

        if(start >= end) {
            return answer;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toFile()))) {
            String line = br.readLine();
            while((line = br.readLine()) != null) {
                FileMetadata fileMetadata = parseFileDto(line);
                if(fileMetadata.getUserId() != userId) {
                    continue;
                }
                if(fileMetadata.getState() != FileState.AVAILABLE) {
                    continue;
                }
                if(currentCnt < start) {
                    currentCnt++;
                    continue;
                }
                currentCnt++;
                answer.add(fileMetadata);

                if(currentCnt >= end) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("파일 메타데이터를 읽어오는 과정에서 예외가 발생함");
            throw new RuntimeException(e);
        }
        if(answer.size() > size) {
            throw new IllegalStateException("요청한 페이징 사이즈보다 많은 데이터를 가져왔습니다.");
        }
        Collections.reverse(answer);

        return answer;
    }

    @Override
    public List<FileMetadata> findAll(int offset, int size) {

        if(offset < 0 || size < 0) {
            throw new IllegalArgumentException("잘못된 offset 값 또는 size 값입니다.");
        }
        List<FileMetadata> answer = new ArrayList<>();
        int cnt = 0;
        try(BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toFile()))) {

            String line = br.readLine();
            while((line = br.readLine()) != null) {
                FileMetadata fileMetadata = parseFileDto(line);
                if(fileMetadata.getState() != FileState.AVAILABLE) {
                    continue;
                }
                cnt++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int start = Math.max(0, cnt-offset-size);
        int end = Math.max(0, cnt-offset);
        int current = 0;

        if(start >= end) {
            return answer;
        }
        try(BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toFile()))) {

            String line = br.readLine();
            while((line = br.readLine()) != null) {
                FileMetadata fileMetadata = parseFileDto(line);
                if(fileMetadata.getState() != FileState.AVAILABLE) {
                    continue;
                }
                if(current < start) {
                    current++;
                    continue;
                }
                current++;
                answer.add(fileMetadata);

                if(current >= end) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Collections.reverse(answer);

        return answer;
    }


    @Override
    public boolean update(FileMetadata fileMetadata) {

        boolean isUpdated = false;
        String updatedData = null;

        try(BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toFile()));
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempCsvFilePath.toFile()))) {

            String line = br.readLine(); //첫번째 줄은 컬럼명을 나타내기 때문에 읽지 않음
            bw.write(line);
            bw.newLine();

            while((line = br.readLine()) != null) {
                FileMetadata candidateFileMetadata = parseFileDto(line);
                if(candidateFileMetadata.getId() == fileMetadata.getId()) {
                    isUpdated = true;
                    updatedData = pasrseString(fileMetadata);
                    continue;
                }
                bw.write(line);
                bw.newLine();
            }
            if(updatedData != null) {
                bw.write(updatedData);
                bw.newLine();
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
        return isUpdated;
    }

    @Override
    public boolean delete(long id) {

        boolean isDeleted = false;

        try(BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toFile()));
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempCsvFilePath.toFile()))) {

            String line = br.readLine(); //첫번째 줄은 컬럼명을 나타내기 때문에 읽지 않음
            bw.write(line);
            bw.newLine();

            while((line = br.readLine()) != null) {
                FileMetadata candidateFileMetadata = parseFileDto(line);
                if(candidateFileMetadata.getId() == id) {
                    isDeleted = true;
                    continue;
                }
                bw.write(line);
                bw.newLine();
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
        return isDeleted;
    }

    @Override
    public boolean deleteAll(List<Long> fileIds) {

        boolean isDeleted = false;
        fileIds.sort(Comparator.naturalOrder());

        try(BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toFile()));
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempCsvFilePath.toFile()))) {

            String line = br.readLine(); //첫번째 줄은 컬럼명을 나타내기 때문에 읽지 않음
            bw.write(line);
            bw.newLine();

            int idx = 0;
            while((line = br.readLine()) != null) {
                FileMetadata candidateFileMetadata = parseFileDto(line);
                if(idx < fileIds.size() && candidateFileMetadata.getId() == fileIds.get(idx)) {
                    idx++;
                    isDeleted = true;
                    continue;
                }
                bw.write(line);
                bw.newLine();
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
        return isDeleted;
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
                fileMetadata.getLastUpdatedTimestamp() + CSV_COLUMN_SEPARATOR +
                fileMetadata.getDownloadCnt() + CSV_COLUMN_SEPARATOR +
                fileMetadata.getState().toString();
    }

    private FileMetadata parseFileDto(String line) {

        String[] columns = line.split(CSV_COLUMN_SEPARATOR);
        StringBuilder description = new StringBuilder();
        for(int i=5;i<columns.length-5;i++) {
            description.append(columns[i]);
            description.append(CSV_COLUMN_SEPARATOR);
        }
        description.delete(description.length()-1, description.length());

        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setId(Long.parseLong(columns[0]));
        fileMetadata.setUserId(Long.parseLong(columns[1]));
        fileMetadata.setPrice(Integer.parseInt(columns[2]));
        fileMetadata.setPath(columns[3]);
        fileMetadata.setSubject(columns[4]);
        fileMetadata.setDescription(description.toString());
        fileMetadata.setSize(Integer.parseInt(columns[columns.length-5]));
        fileMetadata.setCreatedTimestamp(Long.parseLong(columns[columns.length-4]));
        fileMetadata.setLastUpdatedTimestamp(Long.parseLong(columns[columns.length-3]));
        fileMetadata.setDownloadCnt(Integer.parseInt(columns[columns.length-2]));
        fileMetadata.setState(FileState.valueOf(columns[columns.length-1]));

        return fileMetadata;
    }
}

