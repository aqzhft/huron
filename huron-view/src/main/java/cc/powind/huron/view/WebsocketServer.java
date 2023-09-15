package cc.powind.huron.view;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/websocket")
public class WebsocketServer {

    private final static Map<String, Session> sessionMap = new ConcurrentHashMap<>(1024);

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("open: " + session);

        sessionMap.put(session.getId(), session);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("close: " + session);

        sessionMap.remove(session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("message: " + message + ", session: " + session.getId());

    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("session: " + session.getId() + ", throwable: " + throwable);

        sessionMap.remove(session.getId());
    }

    public void send(String message) {

        for (String sessionId : sessionMap.keySet()) {
            Session session = sessionMap.get(sessionId);
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
