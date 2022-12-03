package com.autobots.automanager.controles;

import java.util.Date;
import java.util.List;

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

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.modelos.AdicionadorLinkEmpresa;
import com.autobots.automanager.repositorios.RepositorioEmpresa;

@RestController
@RequestMapping("/empresa")
public class EmpresaControle {
	
	@Autowired
	private RepositorioEmpresa repositorio;
	@Autowired
	private AdicionadorLinkEmpresa adicionarLink;
	
	@GetMapping("/buscar")
	public ResponseEntity<List<Empresa>> buscarEmpresas() {
		List<Empresa> empresas = repositorio.findAll();
		adicionarLink.adicionarLink(empresas);
		if(!empresas.isEmpty()) {
			for(Empresa empresa: empresas) {
				adicionarLink.adicionarLinkUpdate(empresa);
				adicionarLink.adicionarLinkDelete(empresa);
			}
		}
		return new ResponseEntity<List<Empresa>>(empresas,HttpStatus.FOUND);
	}
	
	@GetMapping("/buscar/{id}")
	public ResponseEntity<Empresa> buscarEmpresa(@PathVariable Long id){
		Empresa empresa = repositorio.findById(id).orElse(null);
		HttpStatus status = null;
		if(empresa == null) {
			status = HttpStatus.NOT_FOUND;
		}else {
			adicionarLink.adicionarLink(empresa);
			adicionarLink.adicionarLinkUpdate(empresa);
			adicionarLink.adicionarLinkDelete(empresa);
			status = HttpStatus.FOUND;
		}
		return new ResponseEntity<Empresa>(empresa,status);
	}
	
	@PostMapping("/cadastrar")
	public ResponseEntity<Empresa> cadastrarEmpresa(@RequestBody Empresa dados){
		dados.setCadastro(new Date());
		Empresa empresa = repositorio.save(dados);
		adicionarLink.adicionarLink(empresa);
		adicionarLink.adicionarLinkUpdate(empresa);
		adicionarLink.adicionarLinkDelete(empresa);
		return new ResponseEntity<Empresa>(empresa,HttpStatus.CREATED);
	}
	
	@PutMapping("/atualizar/{idEmpresa}")
	public ResponseEntity<?> atualizarEmpresa(@PathVariable Long idEmpresa, @RequestBody Empresa dados){
		Empresa empresa = repositorio.findById(idEmpresa).orElse(null);
		if(empresa == null) {
			return new ResponseEntity<>("Empresa não econtrada...", HttpStatus.NOT_FOUND);
		}else {
			if(dados != null) {
				if(dados.getNomeFantasia() != null) {
					empresa.setNomeFantasia(dados.getNomeFantasia());
				}
				if(dados.getRazaoSocial() != null) {
					empresa.setRazaoSocial(dados.getRazaoSocial());
				}
				repositorio.save(empresa);
			}
			return new ResponseEntity<>(empresa, HttpStatus.ACCEPTED);
		}
	}
	
	@DeleteMapping("/excluir/{idEmpresa}")
	public ResponseEntity<?> excluirEmpresa(@PathVariable Long idEmpresa){
		Empresa verificacao = repositorio.findById(idEmpresa).orElse(null);
		
		if(verificacao == null) {
			return new ResponseEntity<>("Empresa não econtrada...", HttpStatus.NOT_FOUND);
		}else {
			repositorio.deleteById(idEmpresa);
			return new ResponseEntity<>("Empresa excluida com sucesso...", HttpStatus.ACCEPTED);
		}
	}
}
