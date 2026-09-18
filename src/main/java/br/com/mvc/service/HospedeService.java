package br.com.mvc.service;

import br.com.mvc.dao.CheckInDAO;
import br.com.mvc.dao.HospedeDAO;
import br.com.mvc.model.Hospede;

import java.util.List;

/**
 * Regras de negocio do hospede.
 * O CPF e sempre guardado apenas com digitos (sem ponto e sem traco).
 */
public class HospedeService {

    private final HospedeDAO hospedeDAO;
    private final CheckInDAO checkInDAO;

    public HospedeService() {
        this.hospedeDAO = new HospedeDAO();
        this.checkInDAO = new CheckInDAO();
    }

    public HospedeService(HospedeDAO hospedeDAO, CheckInDAO checkInDAO) {
        this.hospedeDAO = hospedeDAO;
        this.checkInDAO = checkInDAO;
    }

    public List<Hospede> listar() {
        return this.hospedeDAO.listarTodos();
    }

    public Hospede buscarPorId(Long id) {
        return (id == null) ? null : this.hospedeDAO.buscarPorId(id);
    }

    public Hospede buscarPorCpf(String cpf) {
        String limpo = this.somenteDigitos(cpf);
        return (limpo == null) ? null : this.hospedeDAO.buscarPorCpf(limpo);
    }

    /**
     * Valida os dados do passo 1 do check-in SEM gravar nada no banco.
     * Assim o recepcionista so descobre erro de CPF antes de escolher o quarto.
     */
    public void validarDados(Hospede hospede) {
        if (hospede == null) {
            throw new IllegalArgumentException("Dados do hóspede são obrigatórios.");
        }

        this.prepararDados(hospede);

        if (hospede.getNome() == null) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        if (hospede.getNome().length() < 3) {
            throw new IllegalArgumentException("Nome deve ter no mínimo 3 caracteres.");
        }
        if (hospede.getCpf() == null) {
            throw new IllegalArgumentException("CPF é obrigatório.");
        }
        if (!this.cpfValido(hospede.getCpf())) {
            throw new IllegalArgumentException("CPF inválido.");
        }
    }

    public void salvar(Hospede hospede) {
        this.validarDados(hospede);
        this.validarCpfUnico(hospede);

        if (hospede.getId() == null) {
            this.hospedeDAO.inserir(hospede);
            return;
        }

        if (this.hospedeDAO.buscarPorId(hospede.getId()) == null) {
            throw new IllegalArgumentException("Hóspede não encontrado para alteração.");
        }
        this.hospedeDAO.alterar(hospede);
    }

    /**
     * Usado pelo check-in: se o CPF ja existe, reaproveita o cadastro
     * (atualizando nome/contato); se nao existe, cadastra e devolve o novo.
     */
    public Hospede salvarOuAtualizarPorCpf(Hospede dados) {
        this.validarDados(dados);

        Hospede existente = this.hospedeDAO.buscarPorCpf(dados.getCpf());
        if (existente == null) {
            this.hospedeDAO.inserir(dados);
            return this.hospedeDAO.buscarPorCpf(dados.getCpf());
        }

        existente.setNome(dados.getNome());
        existente.setTelefone(dados.getTelefone());
        existente.setEmail(dados.getEmail());
        this.hospedeDAO.alterar(existente);
        return existente;
    }

    public void deletar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id é obrigatório para excluir.");
        }
        if (this.hospedeDAO.buscarPorId(id) == null) {
            throw new IllegalArgumentException("Hóspede não encontrado.");
        }
        // O banco tem FK de checkin -> hospedes: sem esta checagem o erro
        // chegaria na tela como falha de SQL.
        if (!this.checkInDAO.listarPorHospede(id).isEmpty()) {
            throw new IllegalArgumentException(
                    "Não é possível excluir: este hóspede tem hospedagens registradas.");
        }
        this.hospedeDAO.deletar(id);
    }

    private void prepararDados(Hospede hospede) {
        hospede.setNome(this.normalizar(hospede.getNome()));
        hospede.setCpf(this.somenteDigitos(hospede.getCpf()));
        hospede.setTelefone(this.normalizar(hospede.getTelefone()));
        hospede.setEmail(this.normalizar(hospede.getEmail()));
    }

    private void validarCpfUnico(Hospede hospede) {
        Hospede existente = this.hospedeDAO.buscarPorCpf(hospede.getCpf());
        if (existente == null) {
            return;
        }
        if (hospede.getId() == null || !existente.getId().equals(hospede.getId())) {
            throw new IllegalArgumentException("Já existe um hóspede cadastrado com este CPF.");
        }
    }

    /** Validacao de CPF com os dois digitos verificadores. */
    public boolean cpfValido(String cpf) {
        String numero = this.somenteDigitos(cpf);
        if (numero == null || numero.length() != 11) {
            return false;
        }
        // Rejeita 00000000000, 11111111111, etc.
        if (numero.chars().distinct().count() == 1) {
            return false;
        }

        int primeiro = this.digitoVerificador(numero, 9, 10);
        int segundo = this.digitoVerificador(numero, 10, 11);

        return primeiro == Character.getNumericValue(numero.charAt(9))
            && segundo == Character.getNumericValue(numero.charAt(10));
    }

    private int digitoVerificador(String numero, int quantidade, int pesoInicial) {
        int soma = 0;
        int peso = pesoInicial;
        for (int i = 0; i < quantidade; i++) {
            soma += Character.getNumericValue(numero.charAt(i)) * peso;
            peso--;
        }
        int resto = (soma * 10) % 11;
        return (resto == 10) ? 0 : resto;
    }

    private String somenteDigitos(String valor) {
        if (valor == null) {
            return null;
        }
        String limpo = valor.replaceAll("\\D", "");
        return limpo.isEmpty() ? null : limpo;
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return null;
        }
        String limpo = valor.trim();
        return limpo.isEmpty() ? null : limpo;
    }
}
