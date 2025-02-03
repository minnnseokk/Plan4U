package org.project.pack.configuration;

import org.project.pack.entity.User;
import org.project.pack.repository.CalculatorRepository;
import org.project.pack.repository.GuestsRepository;
import org.project.pack.repository.MemoRepository;
import org.project.pack.repository.RoomRepository;
import org.project.pack.repository.UserRepository;
import org.project.pack.services.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {
	
	@Value("${websocket.handler.path}")
	private String socketPath;
	@Value("${websocket.handler.allowed.pattern}")
	private String socketAllowedPattern;
	
	WebSocketService webSocketService;
	
	public WebSocketConfiguration(WebSocketService webSocketService) {
		this.webSocketService = webSocketService;
	}
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry
			.addHandler(webSocketService, "/home")
			.setAllowedOriginPatterns("*")
			.addInterceptors(new HttpSessionHandshakeInterceptor());
			// 특정한 출처만 허용하도록 제한을 거는 것...
	}
}
