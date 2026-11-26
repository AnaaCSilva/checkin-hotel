package br.com.mvc.controller.api;

import br.com.mvc.dto.CheckInRequest;
import br.com.mvc.exception.RecursoNaoEncontradoException;
import br.com.mvc.model.CheckIn;
import br.com.mvc.service.CheckInService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * REST das hospedagens (check-in / check-out).
 *
 *   GET  /api/checkins               historico completo (?ativos=true so os em aberto)
 *   GET  /api/checkins/{id}          uma hospedagem
 *   POST /api/checkins               realiza o check-in
 *   POST /api/checkins/{id}/checkout realiza o check-out
 *
 * Nao ha PUT nem DELETE: o historico de hospedagens nao pode ser apagado ou
 * reescrito. O "update" do ciclo de vida e o check-out.
 */
@WebServlet("/api/checkins/*")
public class CheckInApiController extends ApiBaseServlet {

    private final CheckInService checkInService = new CheckInService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Long id = this.idDaUrl(req);
            if (id == null) {
                boolean soAtivos = "true".equalsIgnoreCase(req.getParameter("ativos"));
                this.json(resp, HttpServletResponse.SC_OK, soAtivos
                        ? this.checkInService.listarAtivos()
                        : this.checkInService.listarHistorico());
                return;
            }
            this.json(resp, HttpServletResponse.SC_OK, this.buscarOuFalhar(id));
        } catch (Exception e) {
            this.tratarExcecao(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String[] partes = this.segmentos(req);

            if (partes.length == 0) {
                CheckInRequest dados = this.lerCorpo(req, CheckInRequest.class);
                CheckIn criado = this.checkInService.realizarCheckIn(
                        dados.getHospede(),
                        dados.getQuartoId(),
                        dados.getQuantidadeDias(),
                        dados.getFormaPagamento());
                this.json(resp, HttpServletResponse.SC_CREATED, criado);
                return;
            }

            if (partes.length == 2 && "checkout".equals(partes[1])) {
                Long id = this.paraId(partes[0]);
                this.checkInService.realizarCheckOut(id);
                this.json(resp, HttpServletResponse.SC_OK, this.buscarOuFalhar(id));
                return;
            }

            throw new RecursoNaoEncontradoException("Rota não encontrada.");
        } catch (Exception e) {
            this.tratarExcecao(resp, e);
        }
    }

    private CheckIn buscarOuFalhar(Long id) {
        CheckIn checkIn = this.checkInService.buscarPorId(id);
        if (checkIn == null) {
            throw new RecursoNaoEncontradoException("Hospedagem não encontrada.");
        }
        return checkIn;
    }
}
