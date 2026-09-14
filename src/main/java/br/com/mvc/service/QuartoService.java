package service;

import dao.QuartoDAO;
import model.Quarto;
import java.util.List;

public class QuartoService {

    private QuartoDAO quartoDAO;

    public QuartoService() {
        this.quartoDAO = new QuartoDAO();
    }

    public List<Quarto> listarTodos() {
        return quartoDAO.listarTodos();
    }

    public Quarto buscarPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do quarto não pode ser nulo.");
        }
        return quartoDAO.buscarPorId(id);
    }

    public void salvar(Quarto quarto) {
        // Validações básicas de campos obrigatórios
        if (quarto.getNumero() == null || quarto.getNumero().trim().isEmpty()) {
            throw new IllegalArgumentException("O número do quarto é obrigatório.");
        }
        if (quarto.getTipo() == null || quarto.getTipo().trim().isEmpty()) {
            throw new IllegalArgumentException("O tipo do quarto é obrigatório.");
        }

        // Regra de negócio: Número único
        validarNumeroUnico(quarto);

        if (quarto.getId() == null) {
            // Se não tem ID, define status inicial como "Disponível" e insere
            if (quarto.getStatus() == null) {
                quarto.setStatus("Disponível");
            }
            quartoDAO.inserir(quarto);
        } else {
            // Se já tem ID, atualiza o registro existente
            quartoDAO.atualizar(quarto);
        }
    }

    private void validarNumeroUnico(Quarto quarto) {
        Quarto existente = quartoDAO.buscarPorNumero(quarto.getNumero());

        // 1. Se ninguém usa esse número ainda, está liberado
        if (existente == null) {
            return;
        }

        // 2. Se é um cadastro NOVO e o número já existe -> Erro
        if (quarto.getId() == null) {
            throw new IllegalArgumentException("Já existe um quarto cadastrado com este número.");
        }

        // 3. Se é EDIÇÃO e o número pertence a OUTRO quarto (IDs diferentes) -> Erro
        if (!existente.getId().equals(quarto.getId())) {
            throw new IllegalArgumentException("Já existe outro quarto registrado com este número.");
        }

        // 4. Se o ID é o mesmo (é o próprio quarto sendo editado) -> Permite salvar!
    }

    public void excluir(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID inválido para exclusão.");
        }
        
        // Regra de segurança: Não permitir excluir quarto se estiver ocupado
        Quarto quarto = quartoDAO.buscarPorId(id);
        if (quarto != null && "Ocupado".equalsIgnoreCase(quarto.getStatus())) {
            throw new IllegalArgumentException("Não é possível excluir um quarto que está ocupado.");
        }

        quartoDAO.deletar(id);
    }
}