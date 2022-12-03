package com.autobots.automanager.controles;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Credencial;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.entidades.Email;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.enumeracoes.PerfilUsuario;
import com.autobots.automanager.modelos.AdicionadorLinkUsuario;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.RepositorioUsuario;
import com.autobots.automanager.repositorios.RepositorioVeiculo;
import com.autobots.automanager.repositorios.RepositorioVenda;

@RestController
@RequestMapping("/usuario")
public class UsuarioControle {
	
	@Autowired
	private RepositorioUsuario repositorio;
	@Autowired
	private RepositorioEmpresa repositorioEmpresa;
	@Autowired
	private RepositorioVenda repositorioVenda;
	@Autowired
	private RepositorioVeiculo repositorioVeiculo;
	@Autowired
	private AdicionadorLinkUsuario adicionarLink;
	
	@GetMapping("/buscar")
	public ResponseEntity<List<Usuario>> buscarUsuarios(){
		List<Usuario> usuarios = repositorio.findAll();
		adicionarLink.adicionarLink(usuarios);
		if(!usuarios.isEmpty()) {
			for(Usuario usuario: usuarios) {
				adicionarLink.adicionarLinkUpdate(usuario);
				adicionarLink.adicionarLinkDelete(usuario);
			}
		}
		return new ResponseEntity<List<Usuario>>(usuarios,HttpStatus.FOUND);
	}
	
	@GetMapping("/buscar/{id}")
	public ResponseEntity<Usuario> buscarUsuario(@PathVariable Long id){
		Usuario usuario = repositorio.findById(id).orElse(null);
		HttpStatus status = null;
		if(usuario == null) {
			status = HttpStatus.NOT_FOUND;
		}else {
			adicionarLink.adicionarLink(usuario);
			adicionarLink.adicionarLinkUpdate(usuario);
			adicionarLink.adicionarLinkDelete(usuario);
			status = HttpStatus.FOUND;
		}
		return new ResponseEntity<Usuario>(usuario,status);
	}
	
	@PostMapping("/cadastrar-cliente")
	public ResponseEntity<Usuario> cadastrarUsuarioCliente(@RequestBody Usuario dados){
		dados.getPerfis().add(PerfilUsuario.CLIENTE);
		Usuario usuario = repositorio.save(dados);
		adicionarLink.adicionarLink(usuario);
		adicionarLink.adicionarLinkUpdate(usuario);
		adicionarLink.adicionarLinkDelete(usuario);
		return new ResponseEntity<Usuario>(usuario,HttpStatus.CREATED);
	}
	
	@PostMapping("/cadastrar-funcionario/{idEmpresa}")
	public ResponseEntity<?> cadastrarUsuarioFuncionario(@RequestBody Usuario dados, @PathVariable Long idEmpresa){
		dados.getPerfis().add(PerfilUsuario.FUNCIONARIO);
		Empresa empresa = repositorioEmpresa.findById(idEmpresa).orElse(null);
		if(empresa == null) {
			return new ResponseEntity<String>("Empresa não encontrada...", HttpStatus.NOT_FOUND);
		}else {
			empresa.getUsuarios().add(dados);
			repositorioEmpresa.save(empresa);
			for(Usuario usuario: empresa.getUsuarios()) {
				adicionarLink.adicionarLink(usuario);
				adicionarLink.adicionarLinkUpdate(usuario);
				adicionarLink.adicionarLinkDelete(usuario);
			}
			return new ResponseEntity<Empresa>(empresa, HttpStatus.CREATED);
		}
	}
	
	@PostMapping("/cadastrar-fornecedor")
	public ResponseEntity<Usuario> cadastrarUsuarioFornecedor(@RequestBody Usuario dados){
		dados.getPerfis().add(PerfilUsuario.FORNECEDOR);
		Usuario usuario = repositorio.save(dados);
		adicionarLink.adicionarLink(usuario);
		adicionarLink.adicionarLinkUpdate(usuario);
		adicionarLink.adicionarLinkDelete(usuario);
		return new ResponseEntity<Usuario>(usuario,HttpStatus.CREATED);
	}
	
	@PutMapping("/atualizar/{idUsuario}")
	public ResponseEntity<?> atualizarUsuario(@PathVariable Long idUsuario, @RequestBody Usuario dados){
		Usuario usuario = repositorio.findById(idUsuario).orElse(null);
		if(usuario == null) {
			return new ResponseEntity<String>("Usuario não encontrado...",HttpStatus.NOT_FOUND);
		}else {
			if(dados != null) {
				if(dados.getNome() != null) {
					usuario.setNome(dados.getNome());
				}
				if(dados.getNomeSocial() != null) {
					usuario.setNomeSocial(dados.getNomeSocial());
				}
				repositorio.save(usuario);
			}
			return new ResponseEntity<>(usuario, HttpStatus.ACCEPTED);
		}
	}
	
	@DeleteMapping("/excluir/{idUsuario}")
	public ResponseEntity<?> excluirUsuario(@PathVariable Long idUsuario){
		Usuario verificacao = repositorio.findById(idUsuario).orElse(null);
		if(verificacao == null) {
			return new ResponseEntity<String>("Usuario não encontrado...",HttpStatus.NOT_FOUND);
		}else {
			
			//venda
			for(Venda venda: repositorioVenda.findAll()) {
				if(venda.getCliente() != null) {		
					if(venda.getCliente().getId() == idUsuario) {
						venda.setCliente(null);
						repositorioVenda.save(venda);
					}
				}
				if(venda.getFuncionario() != null) {	
					if(venda.getFuncionario().getId() == idUsuario) {
						venda.setFuncionario(null);
						repositorioVenda.save(venda);
					}
				}
			}
			
			//veiculo
			for(Veiculo veiculo: repositorioVeiculo.findAll()) {
				if(veiculo.getProprietario() != null) {	
					if(veiculo.getProprietario().getId() == idUsuario) {
						veiculo.setProprietario(null);
						repositorioVeiculo.save(veiculo);
					}
				}
			}
			
			for(Empresa empresa: repositorioEmpresa.findAll()) {
				if(!empresa.getUsuarios().isEmpty()) {
					for(Usuario usuario: empresa.getUsuarios()) {
						if(usuario.getId() == idUsuario) {
							empresa.getUsuarios().remove(usuario);
							repositorioEmpresa.save(empresa);
						}
						break;
					}
				}
			}
			
			repositorio.deleteById(idUsuario);
			return new ResponseEntity<>(repositorio.findAll(),HttpStatus.ACCEPTED);
		}
	}
}
