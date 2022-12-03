package com.autobots.automanager.controles;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.modelos.Login;
import com.autobots.automanager.security.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;


@RestController
public class LoginControle {
	
	@Autowired
    private AuthenticationManager auth;
	
	public void setAuth(AuthenticationManager auth) {
        this.auth = auth;
    }
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Login dados, HttpServletResponse resposta) throws JsonProcessingException{
		Authentication credentials = new UsernamePasswordAuthenticationToken(dados.getUsername(), dados.getPassword());
		Usuario usuario = (Usuario) auth.authenticate(credentials).getPrincipal();
		resposta.setHeader("token",JwtUtils.generateToken(usuario));
		resposta.setHeader("Access-Control-Expose-Headers", "token");
		return new ResponseEntity<>(dados,HttpStatus.ACCEPTED);
	}

}
