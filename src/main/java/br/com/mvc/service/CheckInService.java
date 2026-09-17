package br.com.mvc.service;

import br.com.mvc.dao.CheckInDAO;
import br.com.mvc.dao.HospedeDAO;
import br.com.mvc.dao.QuartoDAO;
import br.com.mvc.model.CheckIn;
import br.com.mvc.model.Hospede;
import br.com.mvc.model.Quarto;

import java.time.LocalDateTime;
import java.util.List;

public class CheckInService {

    // Mesmos valores usados no cadastro de quartos (QuartoService e quartos/form.jsp).
    public static final String STATUS_DISPONIVEL = "Disponível";
    public static final String STATUS_OCUPADO = "Ocupado";

    private final CheckInDAO checkInDAO;
    private final QuartoDAO quartoDAO;
    private final HospedeDAO hospedeDAO;

    public CheckInService() {
        this.checkInDAO = new CheckInDAO();
        this.quartoDAO = new QuartoDAO();
        this.hospedeDAO = new HospedeDAO();
    }

    public void realizarCheckIn(Long hospedeId, Long quartoId) {

        if (hospedeId == null || quartoId == null) {
            throw new IllegalArgumentException("Hóspede e quarto são obrigatórios.");
        }

        Hospede hospede = this.hospedeDAO.buscarPorId(hospedeId);
        if (hospede == null) {
            throw new IllegalArgumentException("Hóspede não encontrado.");
        }

        Quarto quarto = this.quartoDAO.buscarPorId(quartoId);
        if (quarto == null) {
            throw new IllegalArgumentException("Quarto não encontrado.");
        }

        if (!STATUS_DISPONIVEL.equalsIgnoreCase(quarto.getStatus())) {
            throw new IllegalStateException("O quarto " + quarto.getNumero()
                    + " não está disponível para check-in.");
        }

        if (this.checkInDAO.buscarAtivoPorQuarto(quartoId) != null) {
            throw new IllegalStateException("Já existe uma hospedagem em aberto neste quarto.");
        }

        if (this.possuiHospedagemAtiva(hospedeId)) {
            throw new IllegalStateException("Este hóspede já possui uma hospedagem em aberto.");
        }

        CheckIn checkIn = new CheckIn();
        checkIn.setHospedeId(hospedeId);
        checkIn.setQuartoId(quartoId);
        checkIn.setDataCheckin(LocalDateTime.now());
        this.checkInDAO.inserir(checkIn);

        quarto.setStatus(STATUS_OCUPADO);
        this.quartoDAO.alterar(quarto);
    }

    public void realizarCheckOut(Long checkInId) {

        if (checkInId == null) {
            throw new IllegalArgumentException("Id do check-in é obrigatório.");
        }

        CheckIn checkIn = this.checkInDAO.buscarPorId(checkInId);
        if (checkIn == null) {
            throw new IllegalArgumentException("Hospedagem não encontrada.");
        }

        if (!checkIn.isAtivo()) {
            throw new IllegalStateException("Esta hospedagem já foi finalizada.");
        }

        this.checkInDAO.registrarCheckout(checkInId, LocalDateTime.now());

        Quarto quarto = this.quartoDAO.buscarPorId(checkIn.getQuartoId());
        if (quarto != null) {
            quarto.setStatus(STATUS_DISPONIVEL);
            this.quartoDAO.alterar(quarto);
        }
    }

    public List<CheckIn> listarHistorico() {
        return this.checkInDAO.listarTodos();
    }

    public List<CheckIn> listarAtivos() {
        return this.checkInDAO.listarAtivos();
    }

    public CheckIn buscarPorId(Long id) {
        return (id == null) ? null : this.checkInDAO.buscarPorId(id);
    }

    public List<Quarto> listarQuartosDisponiveis() {
        return this.quartoDAO.listarTodos()
                .stream()
                .filter(q -> STATUS_DISPONIVEL.equalsIgnoreCase(q.getStatus()))
                .toList();
    }

    public List<Hospede> listarHospedes() {
        return this.hospedeDAO.listarTodos();
    }

    private boolean possuiHospedagemAtiva(Long hospedeId) {
        return this.checkInDAO.listarPorHospede(hospedeId)
                .stream()
                .anyMatch(CheckIn::isAtivo);
    }
}
