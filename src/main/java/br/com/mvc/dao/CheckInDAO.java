package br.com.mvc.dao;

import br.com.mvc.model.CheckIn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CheckInDAO extends MysqlDAO {

    private static final String SELECT_BASE =
            "SELECT c.id, c.hospede_id, c.quarto_id, c.quantidade_dias, c.forma_pagamento, "
          + "       c.data_checkin, c.data_prevista_saida, c.data_checkout, "
          + "       h.nome AS hospede_nome, h.cpf AS hospede_cpf, "
          + "       q.numero AS quarto_numero, q.tipo AS quarto_tipo "
          + "FROM checkin c "
          + "INNER JOIN hospedes h ON h.id = c.hospede_id "
          + "INNER JOIN quartos q ON q.id = c.quarto_id ";

    public CheckInDAO() {
        super();
    }

    public void inserir(CheckIn checkIn) {
        String sql = "INSERT INTO checkin "
                   + "(hospede_id, quarto_id, quantidade_dias, forma_pagamento, data_checkin, data_prevista_saida) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            super.executarUpdate(sql,
                    checkIn.getHospedeId(),
                    checkIn.getQuartoId(),
                    checkIn.getQuantidadeDias(),
                    checkIn.getFormaPagamento(),
                    Timestamp.valueOf(checkIn.getDataCheckin()),
                    Timestamp.valueOf(checkIn.getDataPrevistaSaida()));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar check-in.", e);
        }
    }

    public List<CheckIn> listarTodos() {
        return this.consultarLista(SELECT_BASE + "ORDER BY c.data_checkin DESC");
    }

    public List<CheckIn> listarAtivos() {
        return this.consultarLista(
                SELECT_BASE + "WHERE c.data_checkout IS NULL ORDER BY c.data_checkin DESC");
    }

    public List<CheckIn> listarPorHospede(Long hospedeId) {
        return this.consultarLista(
                SELECT_BASE + "WHERE c.hospede_id = ? ORDER BY c.data_checkin DESC", hospedeId);
    }

    public CheckIn buscarPorId(Long id) {
        return this.consultarUm(SELECT_BASE + "WHERE c.id = ?", id);
    }

    public CheckIn buscarAtivoPorQuarto(Long quartoId) {
        return this.consultarUm(
                SELECT_BASE + "WHERE c.quarto_id = ? AND c.data_checkout IS NULL", quartoId);
    }

    public CheckIn buscarAtivoPorHospede(Long hospedeId) {
        return this.consultarUm(
                SELECT_BASE + "WHERE c.hospede_id = ? AND c.data_checkout IS NULL", hospedeId);
    }

    public void registrarCheckout(Long id, LocalDateTime saida) {
        String sql = "UPDATE checkin SET data_checkout = ? WHERE id = ? AND data_checkout IS NULL";
        try {
            LocalDateTime dataSaida = (saida != null) ? saida : LocalDateTime.now();
            int linhas = super.executarUpdate(sql, Timestamp.valueOf(dataSaida), id);
            if (linhas == 0) {
                throw new IllegalStateException("Hospedagem inexistente ou ja finalizada.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar check-out.", e);
        }
    }

    private List<CheckIn> consultarLista(String sql, Object... parametros) {
        List<CheckIn> lista = new ArrayList<>();
        try (ResultSet rs = super.executar(sql, parametros)) {
            while (rs.next()) {
                lista.add(this.mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar check-ins.", e);
        }
        return lista;
    }

    private CheckIn consultarUm(String sql, Object... parametros) {
        try (ResultSet rs = super.executar(sql, parametros)) {
            if (rs.next()) {
                return this.mapear(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar check-in.", e);
        }
        return null;
    }

    private CheckIn mapear(ResultSet rs) throws SQLException {
        CheckIn checkIn = new CheckIn();
        checkIn.setId(rs.getLong("id"));
        checkIn.setHospedeId(rs.getLong("hospede_id"));
        checkIn.setQuartoId(rs.getLong("quarto_id"));
        checkIn.setQuantidadeDias(rs.getInt("quantidade_dias"));
        checkIn.setFormaPagamento(rs.getString("forma_pagamento"));
        checkIn.setHospedeNome(rs.getString("hospede_nome"));
        checkIn.setHospedeCpf(rs.getString("hospede_cpf"));
        checkIn.setQuartoNumero(rs.getString("quarto_numero"));
        checkIn.setQuartoTipo(rs.getString("quarto_tipo"));

        Timestamp entrada = rs.getTimestamp("data_checkin");
        checkIn.setDataCheckin(entrada != null ? entrada.toLocalDateTime() : null);

        Timestamp prevista = rs.getTimestamp("data_prevista_saida");
        checkIn.setDataPrevistaSaida(prevista != null ? prevista.toLocalDateTime() : null);

        Timestamp saida = rs.getTimestamp("data_checkout");
        checkIn.setDataCheckout(saida != null ? saida.toLocalDateTime() : null);

        return checkIn;
    }
}
