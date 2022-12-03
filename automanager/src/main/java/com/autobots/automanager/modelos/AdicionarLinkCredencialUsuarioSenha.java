package com.autobots.automanager.modelos;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.CredencialControle;
import com.autobots.automanager.entidades.CredencialUsuarioSenha;

@Component
public class AdicionarLinkCredencialUsuarioSenha implements AdicionadorLink<CredencialUsuarioSenha>{

	@Override
	public void adicionarLink(List<CredencialUsuarioSenha> lista) {
		for(CredencialUsuarioSenha credencial:lista) {
			long id = credencial.getId();
			Link linkProprio = WebMvcLinkBuilder
					.linkTo(WebMvcLinkBuilder
							.methodOn(CredencialControle.class)
							.buscarCredencialUsuarioSenhaPorId(id))
					.withRel("Visualizar credencial de id " + id);
			credencial.add(linkProprio);
		}
	}

	@Override
	public void adicionarLink(CredencialUsuarioSenha objeto) {
		Link linkProprio = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(CredencialControle.class)
						.buscarCredenciaisUsuariosSenhas())
				.withRel("Lista de credenciais");
		objeto.add(linkProprio);
	}

	@Override
	public void adicionarLinkUpdate(CredencialUsuarioSenha objeto) {
		Link linkProprio = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(CredencialControle.class)
						.atualizarCredencialUserSenha(objeto.getId(), objeto))
				.withRel("Atualizar credencial de id " + objeto.getId());
		objeto.add(linkProprio);
	}

	@Override
	public void adicionarLinkDelete(CredencialUsuarioSenha objeto) {
		Link linkProprio = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(CredencialControle.class)
						.excluirCredencialUserSenha(objeto.getId()))
				.withRel("Excluir credencial de id " + objeto.getId());
		objeto.add(linkProprio);
	}

}
