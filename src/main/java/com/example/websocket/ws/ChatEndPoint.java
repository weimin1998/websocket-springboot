package com.example.websocket.ws;

import com.example.websocket.interceptor.UserInterceptor;
import com.example.websocket.pojo.Message;
import com.example.websocket.utils.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ServerEndpoint(value = "/chat", configurator = GetHttpSessionConfigurator.class)
@Component
public class ChatEndPoint {
    //用线程安全的map来保存当前用户
    private static Map<String, ChatEndPoint> onLineUsers = new ConcurrentHashMap<>();
    //声明一个session对象，通过该对象可以发送消息给指定用户，不能设置为静态，每个ChatEndPoint有一个session才能区分.(websocket的session)
    private Session session;
    //保存当前登录浏览器的用户
    private HttpSession httpSession;

    //建立连接时发送系统广播
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        this.httpSession = httpSession;
        String username = (String) httpSession.getAttribute("user");
        log.info("上线用户名称：" + username);
        onLineUsers.put(username, this);
        String message = MessageUtils.getMessage(true, null, getNames());
        broadcastAllUsers(message);
    }

    //获取当前登录的用户
    private Set<String> getNames() {
        return onLineUsers.keySet();
    }

    //发送系统消息
    private void broadcastAllUsers(String message) {
        try {
            Set<String> names = onLineUsers.keySet();
            for (String name : names) {
                ChatEndPoint chatEndPoint = onLineUsers.get(name);
                chatEndPoint.session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //用户之间的信息发送
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Message mess = mapper.readValue(message, Message.class);
            String toName = mess.getToName();
            String data = mess.getMessage();
            String username = (String) httpSession.getAttribute("user");
            log.info(username + "向" + toName + "发送的消息：" + data);
            String resultMessage = MessageUtils.getMessage(false, username, data);
            if (StringUtils.hasLength(toName)) {
                onLineUsers.get(toName).session.getBasicRemote().sendText(resultMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //用户断开连接的断后操作
    @OnClose
    public void onClose(Session session) {
        String username = (String) httpSession.getAttribute("user");
        log.info("离线用户：" + username);
        if (username != null) {
            onLineUsers.remove(username);
            UserInterceptor.onLineUsers.remove(username);
        }
        httpSession.removeAttribute("user");
        String message = MessageUtils.getMessage(true, null, getNames());
        broadcastAllUsers(message);
    }
}
