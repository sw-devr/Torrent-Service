package main.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.protocol.Mapping;
import main.protocol.SocketRequest;
import main.protocol.SocketResponse;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static main.protocol.Status.SUCCESS;

public class URLMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Method> methodMapper;
    private final Map<String, String> classMethodMapper;
    private final Map<String, Object> classMapper;

    public URLMapper() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        methodMapper = new HashMap<>();
        classMapper = new HashMap<>();
        classMethodMapper = new HashMap<>();
        init();

        System.out.println(methodMapper);
    }

    public SocketResponse handle(SocketRequest request) throws JsonProcessingException, InvocationTargetException, IllegalAccessException, MalformedURLException, ClassNotFoundException {

        System.out.println(request);
        Method mappedMethod = methodMapper.get(request.getUrl());
        if(mappedMethod == null) {
            throw new MalformedURLException("요청한 URL에 매핑되는 핸들러가 없습니다.");
        }

        Object clazz = classMapper.get(classMethodMapper.get(mappedMethod.getName()));

        //실제 메소드 실행
        Object response = mappedMethod.invoke(clazz, request);

        if(response instanceof SocketResponse) {
            return (SocketResponse)response;
        }
        SocketResponse socketResponse = new SocketResponse();
        socketResponse.setStatusCode(SUCCESS.getCode());
        socketResponse.setHeader(request.getHeader());
        socketResponse.setBody(objectMapper.writeValueAsString(response));

        return socketResponse;
    }

    private void init() throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        String path = "C:\\Users\\교육생11\\IdeaProjects\\torrent\\out\\production\\torrent/main/server";
        findMapper(path);
    }

    private void findMapper(String currentPath) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        File currentFile = Paths.get(currentPath).toFile();
        if(currentFile.isFile()){
            if(!currentFile.getName().endsWith(".class")){
                return;
            }

            String path = currentPath.replaceAll("/",".")
                    .substring(currentPath.indexOf("/main") + 1, currentPath.indexOf(".class"));

            Class clazz = Class.forName(path);

            Arrays.stream(clazz.getMethods()).forEach(
                    method -> {
                        if(method.isAnnotationPresent(Mapping.class)) {
                            methodMapper.put(method.getAnnotation(Mapping.class).value(), method);
                            classMethodMapper.put(method.getName(), clazz.getName());
                            try {
                                classMapper.put(clazz.getName(), clazz.newInstance());
                            } catch (InstantiationException | IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
            );
            return;
        }

        Arrays.stream(currentFile.list())
                .forEach(subFile -> {
                    try {
                        findMapper(currentPath + "/" + subFile);
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
