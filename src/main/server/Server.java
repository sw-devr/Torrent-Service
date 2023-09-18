package main.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static main.server.common.CommonConstants.SERVER_PORT;

public class Server {

    public static void main(String[] args) throws Exception {

        URLMapper urlMapper = new URLMapper();

        try(ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            while (true) {
                Socket socket = serverSocket.accept();
                executorService.submit(new SocketServerHandler(socket, urlMapper));
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

/*    private void test() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        URLMapper urlMapper = new URLMapper();

        ObjectMapper objectMapper = new ObjectMapper();
        RequestLoginDto requestJoinDto = new RequestLoginDto();
        requestJoinDto.setEmail("hihi2@naver.com");
        requestJoinDto.setPassword("123413");

        SocketRequest socketRequest = new SocketRequest();
        socketRequest.setUrl("/user/login");
        Map<String, String> header = new HashMap<>();
        header.put(CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
        socketRequest.setHeader(header);
        socketRequest.setBody(objectMapper.writeValueAsString(requestJoinDto));

        SocketResponse response = urlMapper.handle(socketRequest);

        System.out.println(response);
    }*/
}
