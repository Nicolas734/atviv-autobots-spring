package com.autobots.automanager.modelos;

import java.util.Set;

public class VendaMolde {
	private String identificacao;
	private Long idEmpresa;
	private Long idCliente;
	private Long idFuncionario;
	private Set<Long> idMercadorias;
	private Set<Long> idServicos;
	private Long idVeiculo;

	public String getIdentificacao() {
		return identificacao;
	}
	public void setIdentificacao(String identificacao) {
		this.identificacao = identificacao;
	}
	public Long getIdEmpresa() {
		return idEmpresa;
	}
	public void setIdEmpresa(Long idEmpresa) {
		this.idEmpresa = idEmpresa;
	}
	public Long getIdCliente() {
		return idCliente;
	}
	public void setIdCliente(Long idCliente) {
		this.idCliente = idCliente;
	}
	public Long getIdFuncionario() {
		return idFuncionario;
	}
	public void setIdFuncionario(Long idFuncionario) {
		this.idFuncionario = idFuncionario;
	}
	public Set<Long> getIdMercadorias() {
		return idMercadorias;
	}
	public void setIdMercadorias(Set<Long> idMercadoria) {
		this.idMercadorias = idMercadoria;
	}
	public Set<Long> getIdServicos() {
		return idServicos;
	}
	public void setIdServicos(Set<Long> idServico) {
		this.idServicos = idServico;
	}
	public Long getIdVeiculo() {
		return idVeiculo;
	}
	public void setIdVeiculo(Long idVeiculo) {
		this.idVeiculo = idVeiculo;
	}

}
