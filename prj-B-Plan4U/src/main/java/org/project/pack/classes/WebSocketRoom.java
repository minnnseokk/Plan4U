package org.project.pack.classes;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

import org.project.pack.annotations.AuthUser;
import org.project.pack.entity.User;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.common.collect.EvictingQueue;

public class WebSocketRoom {
	
	public CopyOnWriteArraySet<WebSocketSession> sessions; // 클라이언트 세션을 저장하는 Set 공간
	
	private Long key; // 추가된 필드
	
	// **** porm.xml에 Guava 의존성 꼭 추가해야함
    private EvictingQueue<String> messageQueue;
    
    public final Map<String, Boolean> onlineUsers = new ConcurrentHashMap<>(); // 온라인 상태 체크

	
	public WebSocketRoom() {sessions = new CopyOnWriteArraySet<>(); } // 채팅 세션을 만들어준 생성자
    // 키값 불러오는 생성자
    public WebSocketRoom(Long key) {
		sessions = new CopyOnWriteArraySet<>();
        this.key = key;
        this.messageQueue = EvictingQueue.create(300); // 채팅 로그 갯수 
    }

    // Getter for key
    public Long getKey() {return key;}
	


	public void AddUser(WebSocketSession session) {
            sessions.add(session);
    }
	
	
	public void RemoveUser(WebSocketSession session) { 
		sessions.remove(session); 
	}
	
	public WebSocketSession FindUser(final String name) {
		try {
			return (WebSocketSession) sessions // 세션에서 이름에 해당하는 유저 찾음
				.stream()
				.filter(ws->ws.getAttributes().get("user").equals(name))
				.toList();
		} catch(Exception e) { return null; }
	}
	
	public void KickUser(String name) {
		WebSocketSession user = FindUser(name); // 한명 찾고	
		try {
			user.sendMessage(new TextMessage("/kick"));
		} catch (IOException e) {} // kick 기능
	
	}
	
	public List<WebSocketSession> FindUserAll(final String key, final Object value) {
		try {
			return sessions
				.stream()
				.filter(ws->ws.getAttributes().get(key).equals(value))
				.toList();
		} catch(Exception e) { return null; }
	}
	public void BroadCast(String message) {
		if (!(message.startsWith("/horror") || message.startsWith("/rickroll") || message.startsWith("/birthday") || message.startsWith("/status"))) {
	        messageQueue.add(message);
	    }
		for(WebSocketSession session : sessions) {
			try {
				session.sendMessage(new TextMessage(message));
			} catch(Exception e) {}
		}
	}
	public void sendPreviousMessages(WebSocketSession session) {
        // 큐에 저장된 이전 메시지를 새로운 유저에게 전송
        for (String message : messageQueue) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}



















