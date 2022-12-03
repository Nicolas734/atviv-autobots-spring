package com.autobots.automanager.jwt;

import java.util.Date;

import io.jsonwebtoken.Claims;

class ValidadorJwt {
	public boolean validar(Claims reivindicacoes) {
		if (reivindicacoes != null) {
			String nomeUsuario = reivindicacoes.getSubject();
			Date dataExpiracao = reivindicacoes.getExpiration();
			Date agora = new Date(System.currentTimeMillis());
			if (nomeUsuario != null && dataExpiracao != null && agora.before(dataExpiracao)) {
				return true;
			}
		}
		return false;
	}
}
