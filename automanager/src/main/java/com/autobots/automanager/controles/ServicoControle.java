package com.autobots.automanager.controles;

import java.util.ArrayList;
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
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.modelos.AdicionadorLinkServico;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.RepositorioServico;
import com.autobots.automanager.repositorios.RepositorioVenda;

@RestController
@RequestMapping("/servico")
public class ServicoControle {

	@Autowired
	private RepositorioServico repositorio;
	@Autowired
	private RepositorioEmpresa repositorioEmpresa;
	@Autowired
	private RepositorioVenda repositorioVenda;
	@Autowired
	private AdicionadorLinkServico adicionarLink;
	
	@GetMapping("/buscar")
	public ResponseEntity<List<Servico>> buscarServicos(){
		List<Servico> servicos = repositorio.findAll();
		List<Servico> novaListaServicos = new ArrayList<Servico>();
		for(Servico servicoRegistrado: servicos) {
			if(servicoRegistrado.getOriginal() != null) {	
				if(servicoRegistrado.getOriginal() == true) {
					adicionarLink.adicionarLinkUpdate(servicoRegistrado);
					adicionarLink.adicionarLinkDelete(servicoRegistrado);
					novaListaServicos.add(servicoRegistrado);
				}
			}
		}
		adicionarLink.adicionarLink(novaListaServicos);
		return new ResponseEntity<List<Servico>>(novaListaServicos, HttpStatus.FOUND);
	}
	
	@GetMapping("/buscar/{id}")
	public ResponseEntity<Servico> buscarServico(@PathVariable Long id){
		Servico servico = repositorio.findById(id).orElse(null);
		HttpStatus status = null;
		if(servico == null) {
			status = HttpStatus.NOT_FOUND;
		}else {
			adicionarLink.adicionarLink(servico);
			adicionarLink.adicionarLinkUpdate(servico);
			adicionarLink.adicionarLinkDelete(servico);
			status = HttpStatus.FOUND;
		}
		return new ResponseEntity<Servico>(servico, status);
	}
	
	@PostMapping("/cadastrar/{idEmpresa}")
	public ResponseEntity<Empresa> cadastrarServicoEmpresa(@RequestBody Servico dados, @PathVariable Long idEmpresa){
		dados.setOriginal(true);
		Empresa empresa = repositorioEmpresa.findById(idEmpresa).orElse(null);
		HttpStatus status = null;
		if(empresa == null) {
			status = HttpStatus.NOT_FOUND;
		}else {
			empresa.getServicos().add(dados);
			repositorioEmpresa.save(empresa);
			for(Servico servico: empresa.getServicos()) {
				adicionarLink.adicionarLink(servico);
				adicionarLink.adicionarLinkUpdate(servico);
				adicionarLink.adicionarLinkDelete(servico);
			}
			status = HttpStatus.CREATED;
		}
		return new ResponseEntity<Empresa>(empresa, status);
	}
	
	@PutMapping("/atualizar/{idServico}")
	public ResponseEntity<?> atualizarServico(@PathVariable Long idServico, @RequestBody Servico dados){
		Servico servico = repositorio.findById(idServico).orElse(null);
		if(servico == null) {
			return new ResponseEntity<>("Servico não encontrado", HttpStatus.NOT_FOUND);
		}else {
			if(dados != null) {
				if(dados.getNome() != null) {
					servico.setNome(dados.getNome());
				}
				if(dados.getDescricao() != null) {
					servico.setDescricao(dados.getDescricao());
				}
				
				servico.setValor(dados.getValor());
				repositorio.save(servico);
			}
			return new ResponseEntity<>(servico,HttpStatus.ACCEPTED);
		}
	}
	
	@DeleteMapping("/excluir/{idServico}")
	public ResponseEntity<?> excluirServico(@PathVariable Long idServico){
		List<Empresa> empresas = repositorioEmpresa.findAll();
		List<Venda> vendas = repositorioVenda.findAll();
		Servico verificador = repositorio.findById(idServico).orElse(null);
		
		if(verificador == null) {
			return new ResponseEntity<>("Servico não encontrado", HttpStatus.NOT_FOUND);
		}else {
			//empresa
			for(Empresa empresa: repositorioEmpresa.findAll()) {
				if(empresa.getServicos().size() > 0) {
					for(Servico servicoEmpresa: empresa.getServicos()) {
						if(servicoEmpresa.getId() == idServico) {
							for(Empresa empresaRegistrado:empresas) {
								empresaRegistrado.getServicos().remove(servicoEmpresa);
							}
						}
					}
				}
			}

			//venda
			for(Venda venda: repositorioVenda.findAll()) {
				if(venda.getServicos().size() > 0) {
					for(Servico servicoVenda: venda.getServicos()) {
						if(servicoVenda.getId() == idServico) {
							for(Venda vendaRegistrada: vendas) {
								vendaRegistrada.getServicos().remove(servicoVenda);
							}
						}
					}
				}
			}
			
			repositorio.deleteById(idServico);
			return new ResponseEntity<>("Serviço excluido com sucesso...",HttpStatus.ACCEPTED);
		}
	}
	
}
