package com.jwt.services;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jwt.models.RefreshToken;
import com.jwt.models.User;
import com.jwt.repositories.RefreshTokenRepository;
import com.jwt.repositories.UserRepository;

@Service
public class RefreshTokenService {
	
	public Long refreshTokenValidity = (long) (10*60*60*1000);

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	public RefreshToken createRefreshToken(String username) {
		
		User user = userRepository.findByEmail(username).get();
		
		RefreshToken refreshToken1 = user.getRefreshToken();
		
		if(refreshToken1==null) {
			refreshToken1 = RefreshToken
					.builder()
					.refreshToken(UUID.randomUUID().toString())
					.expiry(Instant.now().plusMillis(refreshTokenValidity))
					.user(userRepository.findByEmail(username).get())
					.build();
		}else {
			refreshToken1.setExpiry(Instant.now().plusMillis(refreshTokenValidity));
		}
		
		user.setRefreshToken(refreshToken1);
		
		
		refreshTokenRepository.save(refreshToken1);
		
		
		return refreshToken1;
	}
	
	public RefreshToken VerifyRefreshToken(String refreshToken) {
		
		RefreshToken refreshToken2 = refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(()-> new RuntimeException("Invalid Token"));
		
		if(refreshToken2.getExpiry().compareTo(Instant.now())<0) {
			refreshTokenRepository.delete(refreshToken2);
			throw new RuntimeException("Refresh Token Expired");
		}else {
			
		}
		
		return refreshToken2;
	}
	
}
