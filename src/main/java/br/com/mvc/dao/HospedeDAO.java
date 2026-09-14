package br.com.mvc.dao;

import br.com.mvc.model.Hospede;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HospedeDAO extends MysqlDAO {

    public List<Hospede> listarTodos() {
        String sql = "SELECT id, nome, tipo_documento, numero_documento, telefone, email FROM hospedes ORDER BY nome";
        List<Hospede> lista = new ArrayList<>();
        try (Connection conn = super.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                lista.add(this.mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar hospedes.", e);
        }
        return lista;
    }

    public Hospede buscarPorId(Long id) {
        String sql = "SELECT id, nome, tipo_documento, numero_documento, telefone, email FROM hospedes WHERE id = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            super.preencherParametros(stmt, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return this.mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar por id.", e);
        }
        return null;
    }

    public Hospede buscarPorNumeroDocumento(String numeroDocumento) {
        String sql = "SELECT id, nome, tipo_documento, numero_documento, telefone, email FROM hospedes WHERE numero_documento = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            super.preencherParametros(stmt, numeroDocumento);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return this.mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar por documento.", e);
        }
        return null;
    }

    public void inserir(Hospede hospede) {
        String sql = "INSERT INTO hospedes (nome, tipo_documento, numero_documento, telefone, email) VALUES (?, ?, ?, ?, ?)";
        try {
            super.executarUpdate(
                    sql,
                    hospede.getNome(),
                    hospede.getTipoDocumento(),
                    hospede.getNumeroDocumento(),
                    hospede.getTelefone(),
                    hospede.getEmail());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir hospede.", e);
        }
    }

    public void alterar(Hospede hospede) {
        String sql = "UPDATE hospedes SET nome = ?, tipo_documento = ?, numero_documento = ?, telefone = ?, email = ? WHERE id = ?";
        try {
            super.executarUpdate(
                    sql,
                    hospede.getNome(),
                    hospede.getTipoDocumento(),
                    hospede.getNumeroDocumento(),
                    hospede.getTelefone(),
                    hospede.getEmail(),
                    hospede.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar hospede.", e);
        }
    }

    public void deletar(Long id) {
        String sql = "DELETE FROM hospedes WHERE id = ?";
        try {
            super.executarUpdate(sql, id);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar hospede.", e);
        }
    }

    private Hospede mapear(ResultSet rs) throws SQLException {
        Hospede hospede = new Hospede();
        hospede.setId(rs.getLong("id"));
        hospede.setNome(rs.getString("nome"));
        hospede.setTipoDocumento(rs.getString("tipo_documento"));
        hospede.setNumeroDocumento(rs.getString("numero_documento"));
        hospede.setTelefone(rs.getString("telefone"));
        hospede.setEmail(rs.getString("email"));
        return hospede;
    }
}