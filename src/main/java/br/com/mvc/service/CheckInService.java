package br.com.mvc.service;

import br.com.mvc.dao.CheckInDAO;
import br.com.mvc.dao.QuartoDAO;
import br.com.mvc.model.CheckIn;
import br.com.mvc.model.Hospede;
import br.com.mvc.model.Quarto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Coracao da regra de negocio do sistema.
 *
 * Fluxo do recepcionista:
 *   passo 1 -> dados do hospede (validados aqui, sem gravar)
 *   passo 2 -> escolhe um quarto DISPONIVEL, informa dias e forma de pagamento
 *   final   -> grava hospede + hospedagem e o quarto passa para OCUPADO
 */
public class CheckInService {

    public static final String STATUS_DISPONIVEL = "Disponível";
    public static final String STATUS_OCUPADO = "Ocupado";
    public static final String STATUS_MANUTENCAO = "Manutenção";

    public static final Set<String> FORMAS_PAGAMENTO =
            Set.of("Dinheiro", "Pix", "Cartão de Débito", "Cartão de Crédito");

    private static final int DIAS_MINIMO = 1;
    private static final int DIAS_MAXIMO = 60;

    private final CheckInDAO checkInDAO;
    private final QuartoDAO quartoDAO;
    private final HospedeService hospedeService;

    public CheckInService() {
        this.checkInDAO = new CheckInDAO();
        this.quartoDAO = new QuartoDAO();
        this.hospedeService = new HospedeService();
    }

    /** Passo 1: so valida os dados pessoais, ainda nao grava nada. */
    public void validarDadosHospede(Hospede hospede) {
        this.hospedeService.validarDados(hospede);
    }

    /** Passo 2: lista o que o recepcionista pode escolher. */
    public List<Quarto> listarQuartosDisponiveis() {
        return this.quartoDAO.listarPorStatus(STATUS_DISPONIVEL);
    }

    public List<String> listarFormasPagamento() {
        return FORMAS_PAGAMENTO.stream().sorted().toList();
    }

    /**
     * Finaliza o cadastro: grava o hospede, abre a hospedagem e ocupa o quarto.
     */
    public CheckIn realizarCheckIn(Hospede dadosHospede, Long quartoId,
                                   Integer quantidadeDias, String formaPagamento) {

        this.validarQuantidadeDias(quantidadeDias);
        this.validarFormaPagamento(formaPagamento);

        if (quartoId == null) {
            throw new IllegalArgumentException("Escolha um quarto para continuar.");
        }

        Quarto quarto = this.quartoDAO.buscarPorId(quartoId);
        if (quarto == null) {
            throw new IllegalArgumentException("Quarto não encontrado.");
        }
        if (!STATUS_DISPONIVEL.equalsIgnoreCase(quarto.getStatus())) {
            throw new IllegalStateException("O quarto " + quarto.getNumero()
                    + " não está disponível. Escolha outro.");
        }
        if (this.checkInDAO.buscarAtivoPorQuarto(quartoId) != null) {
            throw new IllegalStateException("Já existe uma hospedagem em aberto neste quarto.");
        }

        // Grava (ou reaproveita) o cadastro do hospede pelo CPF.
        Hospede hospede = this.hospedeService.salvarOuAtualizarPorCpf(dadosHospede);

        if (this.checkInDAO.buscarAtivoPorHospede(hospede.getId()) != null) {
            throw new IllegalStateException(
                    "Este hóspede já está hospedado. Faça o check-out antes de um novo check-in.");
        }

        LocalDateTime entrada = LocalDateTime.now();

        CheckIn checkIn = new CheckIn();
        checkIn.setHospedeId(hospede.getId());
        checkIn.setQuartoId(quarto.getId());
        checkIn.setQuantidadeDias(quantidadeDias);
        checkIn.setFormaPagamento(formaPagamento);
        checkIn.setDataCheckin(entrada);
        checkIn.setDataPrevistaSaida(entrada.plusDays(quantidadeDias));
        this.checkInDAO.inserir(checkIn);

        quarto.setStatus(STATUS_OCUPADO);
        this.quartoDAO.alterar(quarto);

        checkIn.setHospedeNome(hospede.getNome());
        checkIn.setQuartoNumero(quarto.getNumero());
        return checkIn;
    }

    /** Check-out: fecha a hospedagem e libera o quarto. */
    public void realizarCheckOut(Long checkInId) {
        if (checkInId == null) {
            throw new IllegalArgumentException("Id da hospedagem é obrigatório.");
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
        if (quarto != null && !STATUS_MANUTENCAO.equalsIgnoreCase(quarto.getStatus())) {
            quarto.setStatus(STATUS_DISPONIVEL);
            this.quartoDAO.alterar(quarto);
        }
    }

    public List<CheckIn> listarAtivos() {
        return this.checkInDAO.listarAtivos();
    }

    public List<CheckIn> listarHistorico() {
        return this.checkInDAO.listarTodos();
    }

    public CheckIn buscarPorId(Long id) {
        return (id == null) ? null : this.checkInDAO.buscarPorId(id);
    }

    private void validarQuantidadeDias(Integer dias) {
        if (dias == null) {
            throw new IllegalArgumentException("Informe a quantidade de dias.");
        }
        if (dias < DIAS_MINIMO || dias > DIAS_MAXIMO) {
            throw new IllegalArgumentException(
                    "A quantidade de dias deve ser entre " + DIAS_MINIMO + " e " + DIAS_MAXIMO + ".");
        }
    }

    private void validarFormaPagamento(String formaPagamento) {
        if (formaPagamento == null || formaPagamento.isBlank()) {
            throw new IllegalArgumentException("Informe a forma de pagamento.");
        }
        if (!FORMAS_PAGAMENTO.contains(formaPagamento)) {
            throw new IllegalArgumentException("Forma de pagamento inválida.");
        }
    }
}
