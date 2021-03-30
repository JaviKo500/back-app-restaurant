package com.appetit.Auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import com.appetit.models.Usuario;
import com.appetit.service.UsuarioService;

@Component
public class InfoAdicionalToken implements TokenEnhancer {

	@Autowired
	UsuarioService usuarioService;

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

		Usuario usuario = usuarioService.findByUsername(authentication.getName());

		Map<String, Object> info = new HashMap<>();
		info.put("id_usuario", usuario.getId());

		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
		return accessToken;
	}

}
