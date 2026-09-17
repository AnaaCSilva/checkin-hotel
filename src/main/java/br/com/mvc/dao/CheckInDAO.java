package br.com.mvc.dao;

import br.com.mvc.model.CheckIn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CheckInDAO extends MysqlDAO {

    private static final String SELECT_BASE =
            "SELECT c.id, c.hospede_id, c.quarto_id, c.data_checkin, c.data_checkout, "
          + "       h.nome AS hospede_nome, q.numero AS quarto_numero "
          + "FROM checkin c "
          + "INNER JOIN hospedes h ON h.id = c.hospede_id "
          + "INNER JOIN quartos q ON q.id = c.quarto_id ";

    public void inserir(CheckIn checkIn) {
        String sql = "INSERT INTO checkin (hospede_id, quarto_id, data_checkin) VALUES (?, ?, ?)";
        try {
            LocalDateTime entrada = (checkIn.getDataCheckin() != null)
                    ? checkIn.getDataCheckin()
                    : LocalDateTime.now();

            super.executarUpdate(sql,
                    checkIn.getHospedeId(),
                    checkIn.getQuartoId(),
                    Timestamp.valueOf(entrada));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar check-in.", e);
        }
    }

    public List<CheckIn> listarTodos() {
        String sql = SELECT_BASE + "ORDER BY c.data_checkin DESC";
        try (Connection conn = super.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<CheckIn> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(this.mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar check-ins.", e);
        }
    }

    public List<CheckIn> listarAtivos() {
        String sql = SELECT_BASE + "WHERE c.data_checkout IS NULL ORDER BY c.data_checkin DESC";
        try (Connection conn = super.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<CheckIn> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(this.mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar check-ins ativos.", e);
        }
    }

    public List<CheckIn> listarPorHospede(Long hospedeId) {
        String sql = SELECT_BASE + "WHERE c.hospede_id = ? ORDER BY c.data_checkin DESC";
        try (Connection conn = super.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            super.preencherParametros(stmt, hospedeId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<CheckIn> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(this.mapear(rs));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar check-ins do hospede.", e);
        }
    }

    public CheckIn buscarPorId(Long id) {
        String sql = SELECT_BASE + "WHERE c.id = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            super.preencherParametros(stmt, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return this.mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar check-in por id.", e);
        }
        return null;
    }

    public CheckIn buscarAtivoPorQuarto(Long quartoId) {
        String sql = SELECT_BASE + "WHERE c.quarto_id = ? AND c.data_checkout IS NULL";
        try (Connection conn = super.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            super.preencherParametros(stmt, quartoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return this.mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar check-in ativo do quarto.", e);
        }
        return null;
    }

    public void registrarCheckout(Long id, LocalDateTime saida) {
        String sql = "UPDATE checkin SET data_checkout = ? WHERE id = ? AND data_checkout IS NULL";
        try {
            LocalDateTime dataSaida = (saida != null) ? saida : LocalDateTime.now();
            int linhas = super.executarUpdate(sql, Timestamp.valueOf(dataSaida), id);
            if (linhas == 0) {
                throw new IllegalStateException("Check-in inexistente ou ja finalizado.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar check-out.", e);
        }
    }

    private CheckIn mapear(ResultSet rs) throws SQLException {
        CheckIn checkIn = new CheckIn();
        checkIn.setId(rs.getLong("id"));
        checkIn.setHospedeId(rs.getLong("hospede_id"));
        checkIn.setQuartoId(rs.getLong("quarto_id"));
        checkIn.setHospedeNome(rs.getString("hospede_nome"));
        checkIn.setQuartoNumero(rs.getString("quarto_numero"));

        Timestamp entrada = rs.getTimestamp("data_checkin");
        checkIn.setDataCheckin(entrada != null ? entrada.toLocalDateTime() : null);

        Timestamp saida = rs.getTimestamp("data_checkout");
        checkIn.setDataCheckout(saida != null ? saida.toLocalDateTime() : null);

        return checkIn;
    }
}
