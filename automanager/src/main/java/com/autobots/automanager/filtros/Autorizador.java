package com.autobots.automanager.filtros;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.autobots.automanager.jwt.ProvedorJwt;

public class Autorizador extends BasicAuthenticationFilter {

	private ProvedorJwt provedorJwt;
	private UserDetailsService servico;
	private ValidadorCabecalho validador;
	private AnalisadorCabecalho analisador;
	private AutenticadorJwt autenticadorJwt;

	public Autorizador(AuthenticationManager gerenciadorAutenticacao, ProvedorJwt provedorJwt,
			UserDetailsService servico) {
		super(gerenciadorAutenticacao);
		this.servico = servico;
		this.provedorJwt = provedorJwt;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String cabecalho = request.getHeader("Authorization");
		validador = new ValidadorCabecalho(cabecalho);
		if (validador.validar()) {
			analisador = new AnalisadorCabecalho(cabecalho);
			String jwt = analisador.obterJwt();
			autenticadorJwt = new AutenticadorJwt(jwt, provedorJwt, servico);
			UsernamePasswordAuthenticationToken autenticacao = autenticadorJwt.obterAutenticacao();
			if (autenticacao != null) {
				SecurityContextHolder.getContext().setAuthentication(autenticacao);
			}
		}
		chain.doFilter(request, response);
	}
}
