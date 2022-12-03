package com.autobots.automanager.controles;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
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

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.modelos.AdicionadorLinkVenda;
import com.autobots.automanager.modelos.VendaMolde;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.RepositorioMercadoria;
import com.autobots.automanager.repositorios.RepositorioServico;
import com.autobots.automanager.repositorios.RepositorioUsuario;
import com.autobots.automanager.repositorios.RepositorioVeiculo;
import com.autobots.automanager.repositorios.RepositorioVenda;

@RestController
@RequestMapping("/venda")
public class VendaControle {

	@Autowired
	public RepositorioVenda repositorio;
	@Autowired
	public RepositorioEmpresa repositorioEmpresa;
	@Autowired
	public RepositorioUsuario repositorioUsuario;
	@Autowired
	public RepositorioMercadoria repositorioMercadoria;
	@Autowired
	public RepositorioServico repositorioServico;
	@Autowired
	public RepositorioVeiculo repositorioVeiculo;
	@Autowired
	public AdicionadorLinkVenda adicionarLink;

	@GetMapping("/buscar")
	public ResponseEntity<List<Venda>> buscarVendas() {
		List<Venda> vendas = repositorio.findAll();
		adicionarLink.adicionarLink(vendas);
		if(!vendas.isEmpty()) {
			for(Venda venda: vendas) {
				adicionarLink.adicionarLinkUpdate(venda);
				adicionarLink.adicionarLinkDelete(venda);
			}
		}
		return new ResponseEntity<List<Venda>>(vendas, HttpStatus.FOUND);
	}

	@GetMapping("/buscar/{id}")
	public ResponseEntity<Venda> buscarVenda(@PathVariable Long id) {
		Venda venda = repositorio.findById(id).orElse(null);
		HttpStatus status = null;
		if (venda == null) {
			status = HttpStatus.NOT_FOUND;
		} else {
			adicionarLink.adicionarLink(venda);
			adicionarLink.adicionarLinkUpdate(venda);
			adicionarLink.adicionarLinkDelete(venda);
			status = HttpStatus.FOUND;
		}
		return new ResponseEntity<Venda>(venda, status);
	}

	@PostMapping("/cadastrar")
	public ResponseEntity<Empresa> cadastrarVenda(@RequestBody VendaMolde dados){
		Empresa empresa = repositorioEmpresa.findById(dados.getIdEmpresa()).orElse(null);
		Venda venda = new Venda();
		if(empresa == null) {
			return new ResponseEntity<Empresa>(empresa,HttpStatus.NOT_FOUND);
		}else {
			venda.setCliente(repositorioUsuario.findById(dados.getIdCliente()).orElse(null));
			venda.setFuncionario(repositorioUsuario.findById(dados.getIdFuncionario()).orElse(null));
			venda.setVeiculo(repositorioVeiculo.findById(dados.getIdVeiculo()).orElse(null));
			venda.setCadastro(new Date());
			venda.setIdentificacao(dados.getIdentificacao());
			repositorio.save(venda);
			
			Set<Long> idsMercadorias = dados.getIdMercadorias();
			Set<Long> idsServicos = dados.getIdServicos();
			
			if(dados.getIdMercadorias() != null) {
				if(!dados.getIdMercadorias().isEmpty()) {	
					for (Long id : idsMercadorias) {
						Mercadoria respostaBuscar = repositorioMercadoria.getById(id);
						Mercadoria mercadoria = new Mercadoria();
						mercadoria.setValidade(respostaBuscar.getValidade());
						mercadoria.setFabricao(respostaBuscar.getFabricao());
						mercadoria.setCadastro(respostaBuscar.getCadastro());
						mercadoria.setNome(respostaBuscar.getNome());
						mercadoria.setQuantidade(respostaBuscar.getQuantidade());
						mercadoria.setValor(respostaBuscar.getValor());
						mercadoria.setDescricao(respostaBuscar.getDescricao());
						mercadoria.setOriginal(false);
						venda.getMercadorias().add(mercadoria);
					}
				}
			}

			if(dados.getIdServicos() != null) {	
				if(!dados.getIdServicos().isEmpty()) {				
					for (Long id : idsServicos) {
						Servico respostaBusca = repositorioServico.getById(id);
						Servico servico = new Servico();
						servico.setNome(respostaBusca.getNome());
						servico.setDescricao(respostaBusca.getDescricao());
						servico.setValor(respostaBusca.getValor());
						servico.setOriginal(false);
						venda.getServicos().add(servico);
					}
				}
			}
			
			repositorio.save(venda);
			
			empresa.getVendas().add(venda);
			Usuario funcionario = venda.getFuncionario();
			
			funcionario.getVendas().add(venda);
			repositorioEmpresa.save(empresa);
			
			for(Venda vendaRegistrada: empresa.getVendas()) {
				adicionarLink.adicionarLink(vendaRegistrada);
				adicionarLink.adicionarLinkUpdate(vendaRegistrada);
				adicionarLink.adicionarLinkDelete(vendaRegistrada);
			}
			
			return new ResponseEntity<Empresa>(empresa,HttpStatus.CREATED);
		}
	}

	@PutMapping("/atualizar/{idVenda}")
	public ResponseEntity<?> atualizarVenda(@PathVariable Long idVenda, @RequestBody Venda dados) {
		Venda venda = repositorio.findById(idVenda).orElse(null);
		if (venda == null) {
			return new ResponseEntity<>("Venda não encontrada...", HttpStatus.NOT_FOUND);
		} else {
			if (dados != null) {
				if (dados.getIdentificacao() != null) {
					venda.setIdentificacao(dados.getIdentificacao());
				}
				repositorio.save(venda);
			}
			return new ResponseEntity<>(venda, HttpStatus.ACCEPTED);
		}
	}

	@DeleteMapping("/excluir/{idVenda}")
	public ResponseEntity<?> excluirVenda(@PathVariable Long idVenda) {
		List<Empresa> empresas = repositorioEmpresa.findAll();
		List<Usuario> usuarios = repositorioUsuario.findAll();
		List<Veiculo> veiculos = repositorioVeiculo.findAll();
		Venda verificador = repositorio.findById(idVenda).orElse(null);

		if (verificador == null) {
			return new ResponseEntity<>("Venda não encontrada...", HttpStatus.NOT_FOUND);
		} else {

			// empresa
			for (Empresa empresa : repositorioEmpresa.findAll()) {
				if (!empresa.getVendas().isEmpty()) {
					for (Venda vendaEmpresa : empresa.getVendas()) {
						if (vendaEmpresa.getId() == idVenda) {
							for (Empresa empresaRegistrada : empresas) {
								empresaRegistrada.getVendas().remove(vendaEmpresa);
							}
						}
					}
				}
			}

			// usuarios
			for (Usuario usuario : repositorioUsuario.findAll()) {
				if (!usuario.getVendas().isEmpty()) {
					for (Venda vendaUsuario : usuario.getVendas()) {
						if (vendaUsuario.getId() == idVenda) {
							for (Usuario usuarioRegistrado : usuarios) {
								usuarioRegistrado.getVendas().remove(vendaUsuario);
							}
						}
					}
				}
			}

			// veiculos
			for (Veiculo veiculo : repositorioVeiculo.findAll()) {
				if (!veiculo.getVendas().isEmpty()) {
					for (Venda vendaVeiculo : veiculo.getVendas()) {
						if (vendaVeiculo.getId() == idVenda) {
							for (Veiculo veiculoRegistrado : veiculos) {
								veiculoRegistrado.getVendas().remove(vendaVeiculo);
							}
						}
					}
				}
			}

			empresas = repositorioEmpresa.findAll();
			usuarios = repositorioUsuario.findAll();
			veiculos = repositorioVeiculo.findAll();
			repositorio.deleteById(idVenda);
			return new ResponseEntity<>("Venda excluida com sucesso...", HttpStatus.ACCEPTED);
		}
	}

}