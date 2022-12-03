package com.autobots.automanager.adaptadores;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.autobots.automanager.entidades.Credencial;
import com.autobots.automanager.entidades.CredencialUsuarioSenha;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.repositorios.RepositorioCredencialUsuarioSenha;
import com.autobots.automanager.repositorios.RepositorioUsuario;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private RepositorioUsuario repositorio;

	@Autowired
	private RepositorioCredencialUsuarioSenha repositorioCredencialUsuarioSenha;

	public Usuario selecionar(List<Usuario> objetos, String identificador) {
        Usuario usuario = null;
        for (Usuario objeto : objetos) {
            Set<Credencial> credencial = objeto.getCredenciais();
            for(Credencial credencia: credencial) {
                for(CredencialUsuarioSenha cred : repositorioCredencialUsuarioSenha.findAll()) {
                    if(credencia.getId() == cred.getId()) {
                        String nomeUsuario = cred.getNomeUsuario();
                        if (nomeUsuario.trim().equals(identificador.trim())) {
                            usuario = objeto;
                            break;
                        }
                    }
                }
            }
        }
        return usuario;
    }

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<Usuario> usuarios = repositorio.findAll();
        Usuario usuario = selecionar(usuarios, username);
        if (usuario == null) {
            throw new UsernameNotFoundException(username);
        }
        String nomeUsuario = "";
        String password = "";
        for(Credencial credencia: usuario.getCredenciais()) {
            for(CredencialUsuarioSenha cred : repositorioCredencialUsuarioSenha.findAll()) {
                if(credencia.getId() == cred.getId()) {
                    nomeUsuario = cred.getNomeUsuario();
                    password = cred.getSenha();
                }
            }
        }
        return new UserDetailsImpl(nomeUsuario, password, usuario.getNivelDeAcesso());
    }
}