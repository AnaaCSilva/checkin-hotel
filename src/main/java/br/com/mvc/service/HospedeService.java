package br.com.mvc.service;

import br.com.mvc.dao.HospedeDAO;
import br.com.mvc.model.Hospede;

import java.util.List;

/**
 * SERVICE de Hospede — regras de negocio ficam aqui.
 *
 * Controller so chama estes metodos e decide a view.
 * DAO so executa SQL.
 */
public class HospedeService {

    private final HospedeDAO hospedeDAO;

    public HospedeService() {
        this.hospedeDAO = new HospedeDAO();
    }

    public List<Hospede> listar() {
        return this.hospedeDAO.listarTodos();
    }

    public Hospede buscarPorId(Long id) {
        if (id == null) {
            return null;
        }
        return this.hospedeDAO.buscarPorId(id);
    }

    /**
     * Regra de salvamento:
     * - sem id  -> cadastro novo
     * - com id  -> alteracao (hospede precisa existir)
     */
    public void salvar(Hospede hospede) {
        if (hospede == null) {
            throw new IllegalArgumentException("Hospede e obrigatorio.");
        }

        this.prepararDados(hospede);
        this.validarCamposObrigatorios(hospede);
        this.validarCpfUnico(hospede);

        if (hospede.getId() == null) {
            this.hospedeDAO.inserir(hospede);
            return;
        }

        if (this.hospedeDAO.buscarPorId(hospede.getId()) == null) {
            throw new IllegalArgumentException("Hospede nao encontrado para alteracao.");
        }
        this.hospedeDAO.alterar(hospede);
    }

    public void deletar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id e obrigatorio para excluir.");
        }
        if (this.hospedeDAO.buscarPorId(id) == null) {
            throw new IllegalArgumentException("Hospede nao encontrado.");
        }
        this.hospedeDAO.deletar(id);
    }

    private void prepararDados(Hospede hospede) {
        hospede.setNome(this.normalizar(hospede.getNome()));
        hospede.setCpf(this.normalizar(hospede.getCpf()));
        hospede.setTelefone(this.normalizar(hospede.getTelefone()));
        hospede.setEmail(this.normalizar(hospede.getEmail()));
    }

    private void validarCamposObrigatorios(Hospede hospede) {
        if (hospede.getNome() == null) {
            throw new IllegalArgumentException("Nome e obrigatorio.");
        }
        if (hospede.getCpf() == null) {
            throw new IllegalArgumentException("CPF e obrigatorio.");
        }
    }

    private void validarCpfUnico(Hospede hospede) {
        Hospede existente = this.hospedeDAO.buscarPorCpf(hospede.getCpf());
        if (existente == null) {
            return;
        }
        if (hospede.getId() == null) {
            throw new IllegalArgumentException("Ja existe um hospede com este CPF.");
        }
        if (!existente.getId().equals(hospede.getId())) {
            throw new IllegalArgumentException("Ja existe um hospede com este CPF.");
        }
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return null;
        }
        String limpo = valor.trim();
        return limpo.isEmpty() ? null : limpo;
    }
}