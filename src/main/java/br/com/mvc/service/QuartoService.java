package br.com.mvc.service;

import br.com.mvc.dao.QuartoDAO;
import br.com.mvc.model.Quarto;
import java.util.List;

public class QuartoService {

    private final QuartoDAO quartoDAO;

    public QuartoService() {
        this.quartoDAO = new QuartoDAO();
    }

    public QuartoService(QuartoDAO quartoDAO) {
        this.quartoDAO = quartoDAO;
    }

    public List<Quarto> listarTodos() {
        return quartoDAO.listarTodos();
    }

    public Quarto buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do quarto não pode ser nulo.");
        }
        return quartoDAO.buscarPorId(id);
    }

    public void salvar(Quarto quarto) {
        if (quarto == null) {
            throw new IllegalArgumentException("Quarto é obrigatório.");
        }
        
        quarto.setNumero(normalizar(quarto.getNumero()));
        quarto.setTipo(normalizar(quarto.getTipo()));

        if (quarto.getNumero() == null) {
            throw new IllegalArgumentException("O número do quarto é obrigatório.");
        }
        if (quarto.getTipo() == null) {
            throw new IllegalArgumentException("O tipo do quarto é obrigatório.");
        }

        validarNumeroUnico(quarto);

        if (quarto.getId() == null) {
            if (quarto.getStatus() == null || quarto.getStatus().trim().isEmpty()) {
                quarto.setStatus("Disponível");
            }
            quartoDAO.inserir(quarto);
        } else {
            quartoDAO.alterar(quarto);
        }
    }

    public void excluir(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID inválido para exclusão.");
        }
        
        Quarto quarto = quartoDAO.buscarPorId(id);
        if (quarto == null) {
            throw new IllegalArgumentException("Quarto não encontrado.");
        }
        if ("Ocupado".equalsIgnoreCase(quarto.getStatus())) {
            throw new IllegalArgumentException("Não é possível excluir um quarto que está ocupado.");
        }

        quartoDAO.deletar(id);
    }

    private void validarNumeroUnico(Quarto quarto) {
        Quarto existente = quartoDAO.buscarPorNumero(quarto.getNumero());

        if (existente == null) {
            return;
        }

        if (quarto.getId() == null || !existente.getId().equals(quarto.getId())) {
            throw new IllegalArgumentException("Já existe um quarto registrado com este número.");
        }
    }

    private String normalizar(String valor) {
        if (valor == null) return null;
        String limpo = valor.trim();
        return limpo.isEmpty() ? null : limpo;
    }
}