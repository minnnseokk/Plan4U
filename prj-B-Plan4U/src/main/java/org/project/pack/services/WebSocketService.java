package org.project.pack.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.project.pack.annotations.AuthUser;
import org.project.pack.classes.UD;
import org.project.pack.classes.WebSocketRoom;
import org.project.pack.entity.Guests;
import org.project.pack.entity.Room;
import org.project.pack.entity.User;
import org.project.pack.repository.CalculatorRepository;
import org.project.pack.repository.GuestsRepository;
import org.project.pack.repository.MemoRepository;
import org.project.pack.repository.RoomRepository;
import org.project.pack.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;


@Service
public class WebSocketService extends TextWebSocketHandler {
	
	private final RoomRepository roomRep;
	private final UserRepository userRep;
	private final GuestsRepository guestsRep;
	private final MemoRepository memoRep;
	private final CalculatorRepository calculatorRep;
	
	private WebSocketRoom publicRoom; // 만약 전체룸 채팅을 사용하면 필요한 채팅방
	private Map<Long, WebSocketRoom> rooms;
	
	
	
	public WebSocketService(
			@Autowired RoomRepository roomRep,
			@Autowired UserRepository userRep,
			@Autowired GuestsRepository guestsRep,
			@Autowired MemoRepository memoRep,
			@Autowired CalculatorRepository calculatorRep
			) {
		this.roomRep = roomRep;
		this.userRep = userRep;
		this.guestsRep =guestsRep;
		this.memoRep =memoRep;
		this.calculatorRep = calculatorRep;
		 
		rooms = new ConcurrentHashMap<Long, WebSocketRoom>();
	}
	
	private void broadcastUserStatus(WebSocketRoom roomSet) { // 유저 상태 보여주는거
		String statusMessage = roomSet.onlineUsers.entrySet()
			.stream()
			.map(entry -> entry.getKey() + ":" + (entry.getValue() ? "online" : "offline"))
			.collect(Collectors.joining(","));
		roomSet.BroadCast("/status " + statusMessage);
	}

	
	@Override
	@Transactional
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		try {
			Long roomId = (Long)session.getAttributes().get("roomId");
			
		    // roomId로 user 를 불러오기위한 Room 객체 가져오기
		    
		    SecurityContextImpl securityContext = (SecurityContextImpl) session.getAttributes().get("SPRING_SECURITY_CONTEXT");
		    if (securityContext == null) {
	            throw new IllegalStateException("Security context is null. Cannot authenticate user.");
	        }
		    OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) securityContext.getAuthentication();
		    if (auth == null) {
	            throw new IllegalStateException("Authentication token is null. User is not authenticated.");
	        }
		    
		    UD pr = (UD)auth.getPrincipal();
		    User currUser = pr.getUser();
		    
	    	if(roomRep.existsByIdAndHost_Id(roomId, currUser.getId())||guestsRep.existsByRoom_IdAndUser_Id(roomId, currUser.getId())) {
	    		session.getAttributes().put("user", currUser.getName());
	    		// 핸들러에 전달하기전 roomId에 맞는 chattingRoom 을 부여한다
	    		WebSocketRoom chattingRoom = rooms.computeIfAbsent(roomId, (key)->new WebSocketRoom(key));
		    	session.getAttributes().put("chattingRoom", chattingRoom);
		    	chattingRoom.AddUser(session);
		    	
		    	chattingRoom.onlineUsers.put((String) session.getAttributes().get("user"), true); // 유저 온라인상태
		    	broadcastUserStatus(chattingRoom);
		    	
		    	chattingRoom.sendPreviousMessages(session);
		    	//BroadCastRoom(chattingRoom,"[방 번호 : " + chattingRoom.getKey() +"] "+ currUser.getName() + "님이 입장하셨습니다");
	    	}else {
	    		// 권한이 없는 유저에게 메시지 전송
	    	    session.sendMessage(new TextMessage("noAuthorUser-허가되지 않은 유저 채팅권한 없음"));
	    	}
		}catch(Exception e) {} 
		}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		WebSocketRoom room = (WebSocketRoom) session.getAttributes().get("chattingRoom");
		// 세션에 해당하는 웹소켓룸을 불러오고
		// 세션에서 가져온 WebSocketRoom에서 해당 세션을 제거
		if(room != null) {
			room.RemoveUser(session);
			room.onlineUsers.put((String) session.getAttributes().get("user"), false); // 유저 오프라인상태 넣어주기
	    	broadcastUserStatus(room);
		}
		// 서버 세션 종료 처리
		//DisConnectServer(session, (User)session.getAttributes().get("user"), (HttpSession)session.getAttributes().get("session"), room);
	}
	

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		try {	
			SecurityContext securityContext = (SecurityContext) session.getAttributes().get("SPRING_SECURITY_CONTEXT");
			if (securityContext != null) {
	            Authentication auth = securityContext.getAuthentication();
	            // auth 객체 사용 가능
	        }
			 if (securityContext != null) {
		            Authentication auth = securityContext.getAuthentication();
		            if (auth != null) {
		                // auth 객체 사용 가능
		            } else {
		            }
		        } else {
		        }
			// 사용자가 접속중에 메시지를 전송했을때
			String name = (String)session.getAttributes().get("user");
			// 방과 관련된 정보 가져오기
			WebSocketRoom room = (WebSocketRoom)session.getAttributes().get("chattingRoom");
			String msg = message.getPayload();
			
			if(msg.startsWith("/")) {
				try {
					String[] tokens = msg.split("=");
					if(tokens[0].equals("/id")) {
						session.getAttributes().put("user", tokens[1]);
					}
					else if (tokens[0].equals("/horror")) {
			            // 공포 메시지 브로드캐스트
			            String horrorMessage = "😱 so scary!";
			            room.BroadCast("/horror " + horrorMessage);
			        }
					else if (tokens[0].equals("/rickroll")) {
						room.BroadCast("/rickroll ");
					}
					else if (tokens[0].equals("/themewinter")) {
						room.BroadCast("/snow ");
					}
					else if (tokens[0].equals("/themesummer")) {
						room.BroadCast("/summer ");
					}
					else if (tokens[0].equals("/deletetheme")) {
						room.BroadCast("/deletetheme ");
					}
					else if (tokens[0].equals("/birthday")) {
						// 공포 메시지 브로드캐스트
						String birthMessage = "생일축하한다잉~!";
						room.BroadCast("/birthday " + birthMessage);
					}
					else if (tokens[0].equals("/pop") && tokens.length > 1) {
			            String announceMessage = tokens[1];
			            // 공지사항을 모든 유저에게 브로드캐스트
			            room.BroadCast("/pop "+ name + " :: " + announceMessage);
			        }
	//				else if(tokens[0].equals("/kick")) {
	//					room.KickUser(tokens[1]);
	//				}
				} catch(Exception e) {}
			}
			else {
		         String forBroadCast = name + ": ⓐ" + msg;
		         room.BroadCast(forBroadCast);
		      }
		}catch(Exception e) {
			
		}
		
	}
	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {}
	
//	public void BroadCastAll(String message) { // 모든 방에 공통적으로 전달할 경우 사용 예를 들어 공지사항
//		totalRoom.BroadCast(message); 
//		}
	public void BroadCastRoom(WebSocketRoom room,WebSocketSession session, String message) throws IOException { 
		session.sendMessage(new TextMessage("/announce " + message)); 
	}
	
/*	
	public void BroadCastUser(WebSocketSession session, String message) {
		try { session.sendMessage(new TextMessage(message)); } catch (IOException e) {}
	}
	
	public void ConnectServer(WebSocketSession socket, User user, HttpSession session) {
		// 룸서버 연결된 후
		System.out.println(user.getName() + "이 연결되었습니다");
	}
	
	public void DisConnectServer(WebSocketSession socket, User user, HttpSession session, WebSocketRoom room) {
		// 접속 종료 후
	}
*/
	
}	








	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

