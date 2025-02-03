package org.project.pack.classes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.project.pack.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UD implements UserDetails, OAuth2User {
	
	User user;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();
		for(String auth : user.getAuths()) {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + auth));
		}
		return authorities;
	}
	@Override
	public String getPassword() {
		return user.getPwd();
	}
	@Override
	public String getUsername() {
		return user.getName();
	}
	
	public String getEmail() {
		return user.getEmail();
	}
	@Override
	public Map<String, Object> getAttributes() {
		return user.getAttributes();
	}
	@Override
	public String getName() {
		return user.getName();
	}
}












