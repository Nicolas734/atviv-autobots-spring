package com.autobots.automanager.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

class AnalisadorJwt {
	private String assinatura;
	private String jwt;

	public AnalisadorJwt(String assinatura, String jwt) {
		this.assinatura = assinatura;
		this.jwt = jwt;
	}

	public Claims obterReivindicacoes() {
		try {
			return Jwts.parser().setSigningKey(assinatura.getBytes()).parseClaimsJws(jwt).getBody();
		} catch (Exception e) {
			return null;
		}
	}
	
	public String obterNomeUsuairo(Claims reivindicacoes) {
		if (reivindicacoes != null) {
			String nomeUsuario = reivindicacoes.getSubject();
			return nomeUsuario;
		}
		return null;
	}
}
