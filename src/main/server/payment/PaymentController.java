package main.server.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.protocol.Mapping;
import main.protocol.SocketRequest;
import main.protocol.SocketResponse;
import main.protocol.Status;
import main.server.user.UserService;

import static main.protocol.ResponseFactory.createResponse;
import static main.protocol.SocketHeaderType.SESSION_ID;
import static main.server.common.CommonConstants.PAYMENT_SERVICE;
import static main.server.common.CommonConstants.USER_SERVICE;

public class PaymentController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserService userService = USER_SERVICE;
    private final PaymentService paymentService = PAYMENT_SERVICE;

    @Mapping("/payment/file/purchase")
    public SocketResponse purchaseFile(SocketRequest request) {
        try {
            String sessionId = request.getHeader().get(SESSION_ID.getValue());
            validateSession(sessionId);

            RequestPurchaseFileDto requestParam = objectMapper.readValue((String) request.getBody(), RequestPurchaseFileDto.class);
            long userId = userService.currentUserId(sessionId);

            if(requestParam.getUserId() != userId) {
                throw new IllegalAccessException("접근 권한이 없는 유저입니다.");
            }

            ResponsePurchaseFileDto responseBody = paymentService.purchaseFile(requestParam.getUserId(), requestParam.getFileId());

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), objectMapper.writeValueAsString(responseBody));
        }
        catch (IllegalAccessException e) {
            return createResponse(Status.FORBIDDEN.getCode(), request.getHeader(), e.getMessage());
        }
        catch (IllegalArgumentException e) {
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        }
        catch (JsonProcessingException e) {
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), request.getHeader(), e.getMessage());
        }
    }

    @Mapping("/payment/authority/purchase")
    public SocketResponse purchaseAuthority(SocketRequest request) {
        try {
            String sessionId = request.getHeader().get(SESSION_ID.getValue());
            validateSession(sessionId);

            RequestPurchaseAuthorityDto requestParam = objectMapper.readValue((String) request.getBody(), RequestPurchaseAuthorityDto.class);
            long userId = userService.currentUserId(sessionId);

            if(requestParam.getUserId() != userId) {
                throw new IllegalAccessException("접근 권한이 없는 유저입니다.");
            }

            paymentService.purchaseAuthority(requestParam.getUserId(), requestParam.getRole());

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), requestParam.getRole() + " 권한 구매 성공");
        }
        catch (IllegalAccessException e) {
            return createResponse(Status.FORBIDDEN.getCode(), request.getHeader(), e.getMessage());
        }
        catch (IllegalArgumentException e) {
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        }
        catch (JsonProcessingException e) {
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), request.getHeader(), e.getMessage());
        }
    }

    @Mapping("/payment/point/charging")
    public SocketResponse chargePoints(SocketRequest request) {
        try {
            String sessionId = request.getHeader().get(SESSION_ID.getValue());
            validateSession(sessionId);

            RequestChargePointDto requestParam = objectMapper.readValue((String) request.getBody(), RequestChargePointDto.class);
            long userId = userService.currentUserId(sessionId);

            if(requestParam.getUserId() != userId) {
                throw new IllegalAccessException("접근 권한이 없는 유저입니다.");
            }

            paymentService.chargePoints(requestParam.getUserId(), requestParam.getAddingPoints());

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), requestParam.getAddingPoints() + " 포인트 충전 성공");
        }
        catch (IllegalAccessException e) {
            return createResponse(Status.FORBIDDEN.getCode(), request.getHeader(), e.getMessage());
        }
        catch (IllegalArgumentException e) {
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        }
        catch (JsonProcessingException e) {
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), request.getHeader(), e.getMessage());
        }
    }

    @Mapping("/payment/point/refund")
    public SocketResponse refundFile(SocketRequest request) {
        try {
            String sessionId = request.getHeader().get(SESSION_ID.getValue());
            validateSession(sessionId);

            RequestRefundDto requestParam = objectMapper.readValue((String) request.getBody(), RequestRefundDto.class);
            long userId = userService.currentUserId(sessionId);

            paymentService.refund(userId, requestParam.getDownloadFilePath());

            return createResponse(Status.SUCCESS.getCode(), request.getHeader(), "환불 완료");
        }
        catch (IllegalArgumentException e) {
            return createResponse(Status.BAD_REQUEST.getCode(), request.getHeader(), e.getMessage());
        }
        catch (JsonProcessingException e) {
            return createResponse(Status.INTERNAL_SERVER_ERROR.getCode(), request.getHeader(), e.getMessage());
        }
    }

    private void validateSession(String sessionId) {

        if(!userService.isLogin(sessionId)) {
            throw new IllegalArgumentException("이미 로그인 되어 있지 않습니다.");
        }
    }
}
