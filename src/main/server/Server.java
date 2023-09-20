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
}
