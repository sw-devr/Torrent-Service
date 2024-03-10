package main.server.payment;

import main.server.file.metadata.FileMetadata;
import main.server.file.metadata.FileMetadataRepository;
import main.server.file.metadata.FileMetadataService;
import main.server.file.transfer.FileDownloadAuthorityRepository;
import main.server.user.User;
import main.server.user.UserRepository;
import main.server.user.UserRole;

public class PaymentService {

    private final FileMetadataService fileMetadataService;
    private final UserRepository userRepository;
    private final FileMetadataRepository fileMetadataRepository;
    private final FileDownloadAuthorityRepository fileDownloadAuthorityRepository;

    public PaymentService(FileMetadataService fileMetadataService,
                          UserRepository userRepository,
                          FileMetadataRepository fileMetadataRepository,
                          FileDownloadAuthorityRepository fileDownloadAuthorityRepository) {
        this.fileMetadataService = fileMetadataService;
        this.userRepository = userRepository;
        this.fileMetadataRepository = fileMetadataRepository;
        this.fileDownloadAuthorityRepository = fileDownloadAuthorityRepository;
    }

    public ResponsePurchaseFileDto purchaseFile(long userId, long fileId) {

        User consumer = userRepository.findById(userId);
        FileMetadata fileMetadata = fileMetadataRepository.findById(fileId);
        if(fileMetadata == null) {
            throw new IllegalArgumentException("존재하지 않는 파일데이터입니다.");
        }
        User producer = userRepository.findById(fileMetadata.getUserId());
        if(producer == null) { //삭제된 유저의 파일 데이터들을 삭제해야함
            fileMetadataService.deleteAllFromUser(fileMetadata.getUserId());
            throw new IllegalStateException("존재하지 않는 유저의 파일 데이터를 구매하려 합니다.");
        }
        int filePrice = fileMetadata.getPrice();

        // 결제 처리
        consumer.payPoints(filePrice);
        producer.receivePoints(filePrice);
        userRepository.update(consumer);
        userRepository.update(producer);

        // 다운로드 권한 토큰 생성 후 리턴
        String fileDownloadAuthorityToken = fileDownloadAuthorityRepository.createAuthority(fileMetadata.getPath());

        ResponsePurchaseFileDto responseBody = new ResponsePurchaseFileDto();
        responseBody.setDownloadFileAuthorityToken(fileDownloadAuthorityToken);
        responseBody.setDownloadFilePath(fileMetadata.getPath());

        return responseBody;
    }

    public void purchaseAuthority(long userId, UserRole role) {

        User user = userRepository.findById(userId);
        user.purchaseAuthority(role);
        userRepository.update(user);
    }

    public void chargePoints(long userId, int addingPoints) {

        User user = userRepository.findById(userId);
        user.receivePoints(addingPoints);
        userRepository.update(user);
    }

    public void refund(long userId, String downloadFilePath) {

        FileMetadata fileMetadata = fileMetadataRepository.findByPath(downloadFilePath);

        User consumer = userRepository.findById(userId);
        User producer = userRepository.findById(fileMetadata.getUserId());

        consumer.receivePoints(fileMetadata.getPrice());
        producer.payPoints(fileMetadata.getPrice());

        userRepository.save(consumer);
        userRepository.save(producer);
    }
}
