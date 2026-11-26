package br.com.mvc.service;

import br.com.mvc.dao.CheckInDAO;
import br.com.mvc.dao.QuartoDAO;
import br.com.mvc.exception.ConflitoException;
import br.com.mvc.exception.RecursoNaoEncontradoException;
import br.com.mvc.model.Quarto;

import java.util.List;
import java.util.Set;

public class QuartoService {

    private static final Set<String> STATUS_VALIDOS = Set.of(
            CheckInService.STATUS_DISPONIVEL,
            CheckInService.STATUS_OCUPADO,
            CheckInService.STATUS_MANUTENCAO);

    private final QuartoDAO quartoDAO;
    private final CheckInDAO checkInDAO;

    public QuartoService() {
        this(new QuartoDAO(), new CheckInDAO());
    }

    public QuartoService(QuartoDAO quartoDAO) {
        this(quartoDAO, new CheckInDAO());
    }

    public QuartoService(QuartoDAO quartoDAO, CheckInDAO checkInDAO) {
        this.quartoDAO = quartoDAO;
        this.checkInDAO = checkInDAO;
    }

    public List<Quarto> listarTodos() {
        return quartoDAO.listarTodos();
    }

    public List<Quarto> listarPorStatus(String status) {
        String limpo = normalizar(status);
        validarStatus(limpo);
        return quartoDAO.listarPorStatus(limpo);
    }

    public Quarto buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do quarto não pode ser nulo.");
        }
        return quartoDAO.buscarPorId(id);
    }

    public Quarto buscarPorNumero(String numero) {
        String limpo = normalizar(numero);
        return (limpo == null) ? null : quartoDAO.buscarPorNumero(limpo);
    }

    public void salvar(Quarto quarto) {
        if (quarto == null) {
            throw new IllegalArgumentException("Quarto é obrigatório.");
        }

        quarto.setNumero(normalizar(quarto.getNumero()));
        quarto.setTipo(normalizar(quarto.getTipo()));
        quarto.setStatus(normalizar(quarto.getStatus()));

        if (quarto.getNumero() == null) {
            throw new IllegalArgumentException("O número do quarto é obrigatório.");
        }
        if (quarto.getTipo() == null) {
            throw new IllegalArgumentException("O tipo do quarto é obrigatório.");
        }
        validarStatus(quarto.getStatus());

        validarNumeroUnico(quarto);

        if (quarto.getId() == null) {
            if (quarto.getStatus() == null) {
                quarto.setStatus(CheckInService.STATUS_DISPONIVEL);
            }
            quartoDAO.inserir(quarto);
            return;
        }

        Quarto atual = quartoDAO.buscarPorId(quarto.getId());
        if (atual == null) {
            throw new RecursoNaoEncontradoException("Quarto não encontrado para alteração.");
        }
        if (quarto.getStatus() == null) {
            quarto.setStatus(atual.getStatus());
        }
        quartoDAO.alterar(quarto);
    }

    public void excluir(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID inválido para exclusão.");
        }

        Quarto quarto = quartoDAO.buscarPorId(id);
        if (quarto == null) {
            throw new RecursoNaoEncontradoException("Quarto não encontrado.");
        }
        if (CheckInService.STATUS_OCUPADO.equalsIgnoreCase(quarto.getStatus())) {
            throw new ConflitoException("Não é possível excluir um quarto que está ocupado.");
        }
        // O banco tem FK de checkin -> quartos: sem esta checagem o erro chegaria como falha de SQL.
        if (!checkInDAO.listarPorQuarto(id).isEmpty()) {
            throw new ConflitoException("Não é possível excluir: este quarto tem hospedagens registradas.");
        }

        quartoDAO.deletar(id);
    }

    private void validarStatus(String status) {
        if (status != null && !STATUS_VALIDOS.contains(status)) {
            throw new IllegalArgumentException("Situação deve ser Disponível, Ocupado ou Manutenção.");
        }
    }

    private void validarNumeroUnico(Quarto quarto) {
        Quarto existente = quartoDAO.buscarPorNumero(quarto.getNumero());

        if (existente == null) {
            return;
        }

        if (quarto.getId() == null || !existente.getId().equals(quarto.getId())) {
            throw new ConflitoException("Já existe um quarto registrado com este número.");
        }
    }

    private String normalizar(String valor) {
        if (valor == null) return null;
        String limpo = valor.trim();
        return limpo.isEmpty() ? null : limpo;
    }
}
