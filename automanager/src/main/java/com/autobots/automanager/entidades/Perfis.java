package com.autobots.automanager.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;

import com.autobots.automanager.enumeracoes.PerfilUsuario;

@Entity
public class Perfis implements GrantedAuthority{
	
	private static final long serialVersionUID = 3078636239920155012L;
	
	@Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
	private Long id;
    
    @Column(name = "nome")
    private PerfilUsuario nome;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PerfilUsuario getNome() {
		return nome;
	}

	public void setNome(PerfilUsuario nome) {
		this.nome = nome;
	}

	@Override
	public String getAuthority() {
		return this.nome.toString();
	}

}
