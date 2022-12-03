package com.autobots.automanager.adaptadores;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.autobots.automanager.enumeracoes.Perfil;

@SuppressWarnings("serial")
public class UserDetailsImpl implements UserDetails {
	private String email;
	private String password;
	private Collection<? extends GrantedAuthority> autenticacao;

	public UserDetailsImpl(String email, String password, List<Perfil> acesso) {
		this.email = email;
		this.password = password;
		this.gerarAutoridades(acesso);
	}

	private void gerarAutoridades(List<Perfil> perfis) {
        List<SimpleGrantedAuthority> autoridadesPerfis = new ArrayList<>();
        for (Perfil perfil : perfis) {
            autoridadesPerfis.add(new SimpleGrantedAuthority(perfil.name()));
        }
        this.autenticacao = autoridadesPerfis;
    }

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.autenticacao;
	}
}