package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.modelos.AdicionadorLinkEndereco;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.RepositorioEndereco;
import com.autobots.automanager.repositorios.RepositorioUsuario;

@RestController
@RequestMapping("/endereco")
public class EnderecoControle {
	
	@Autowired
	private RepositorioEndereco repositorio;
	@Autowired
	private RepositorioUsuario repositorioUsuario;
	@Autowired
	private RepositorioEmpresa repositorioEmpresa;
	@Autowired
	private AdicionadorLinkEndereco adicionarLink;
	
	@GetMapping("/buscar")
	public ResponseEntity<List<Endereco>> buscarEnderecos(){
		List<Endereco> enderecos = repositorio.findAll();
		adicionarLink.adicionarLink(enderecos);
		if(!enderecos.isEmpty()) {			
			for(Endereco endereco: enderecos) {
				adicionarLink.adicionarLinkUpdate(endereco);
				adicionarLink.adicionarLinkDelete(endereco);
			}
		}
		return new ResponseEntity<List<Endereco>>(enderecos,HttpStatus.FOUND);
	}
	
	@GetMapping("/buscar/{id}")
	public ResponseEntity<Endereco> buscarEndereco(@PathVariable Long id){
		Endereco endereco = repositorio.findById(id).orElse(null);
		HttpStatus status = null;
		if(endereco == null) {
			status = HttpStatus.NOT_FOUND;
		}else {
			adicionarLink.adicionarLink(endereco);
			adicionarLink.adicionarLinkUpdate(endereco);
			adicionarLink.adicionarLinkDelete(endereco);
			status = HttpStatus.FOUND;
		}
		return new ResponseEntity<Endereco>(endereco,status);
	}
	
	@PutMapping("/atualizar/{idEndereco}")
	public ResponseEntity<?> atualizarEndereco(@PathVariable Long idEndereco, @RequestBody Endereco dados){
		Endereco endereco = repositorio.findById(idEndereco).orElse(null);
		if(endereco == null) {
			return new ResponseEntity<>("Endereco não econtrado...", HttpStatus.NOT_FOUND);
		}else {
			if(dados != null) {
				if(dados.getEstado() != null) {
					endereco.setEstado(dados.getEstado());
				}
				if(dados.getCidade() != null) {
					endereco.setCidade(dados.getCidade());
				}
				if(dados.getBairro() != null) {
					endereco.setBairro(dados.getBairro());
				}
				if(dados.getRua() != null) {
					endereco.setRua(dados.getRua());
				}
				if(dados.getNumero() != null) {
					endereco.setNumero(dados.getNumero());
				}
				if(dados.getCodigoPostal() != null) {
					endereco.setCodigoPostal(dados.getCodigoPostal());
				}
				if(dados.getInformacoesAdicionais() != null) {
					endereco.setInformacoesAdicionais(dados.getInformacoesAdicionais());
				}
				repositorio.save(endereco);
			}
			return new ResponseEntity<>(endereco, HttpStatus.ACCEPTED);
		}
	}
	
	@DeleteMapping("/excluir/{idEndereco}")
	public ResponseEntity<?> excluirEndereco(@PathVariable Long idEndereco){
		Endereco verificacao = repositorio.findById(idEndereco).orElse(null);
		if(verificacao == null) {
			return new ResponseEntity<>("Endereco não econtrado...", HttpStatus.NOT_FOUND);
		}else {

			//usuario
			List<Usuario> usuarios = repositorioUsuario.findAll(); 
			for(Usuario usuario: usuarios) {
				if(usuario.getEndereco() != null) {
					if(usuario.getEndereco().getId() == idEndereco) {
						usuario.setEndereco(null);
						repositorioUsuario.save(usuario);
						break;
					}
				}
			}

			//empresa
			for(Empresa empresa: repositorioEmpresa.findAll()) {
				if(empresa.getEndereco() != null) {
					if(empresa.getEndereco().getId() == idEndereco) {
						empresa.setEndereco(null);
						repositorioEmpresa.save(empresa);
						break;
					}
				}
			}
			return new ResponseEntity<>("Endereco excluido com sucesso...", HttpStatus.ACCEPTED);
		}
	}

}
