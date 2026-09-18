package br.com.mvc.service;

import br.com.mvc.dao.PerfilDAO;
import br.com.mvc.dao.UsuarioDAO;
import br.com.mvc.model.Perfil;
import br.com.mvc.model.Usuario;

import java.util.List;

public class UsuarioService {

    public static final String PERFIL_GERENTE = "Gerente";

    private static final int SENHA_MINIMA = 6;

    private final UsuarioDAO usuarioDAO;
    private final PerfilDAO perfilDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
        this.perfilDAO = new PerfilDAO();
    }

    public UsuarioService(UsuarioDAO usuarioDAO, PerfilDAO perfilDAO) {
        this.usuarioDAO = usuarioDAO;
        this.perfilDAO = perfilDAO;
    }

    public Usuario autenticar(String login, String senha) {
        login = this.normalizar(login);
        senha = this.normalizar(senha);

        if (login == null || senha == null) {
            throw new IllegalArgumentException("Informe login e senha.");
        }

        Usuario usuario = this.usuarioDAO.buscarPorLoginESenha(login, senha);
        if (usuario == null) {
            throw new IllegalArgumentException("Login ou senha inválidos.");
        }
        return usuario;
    }

    /** Usado pelo filtro e pelas telas para liberar a área restrita. */
    public static boolean ehGerente(Usuario usuario) {
        return usuario != null
            && usuario.getPerfil() != null
            && PERFIL_GERENTE.equalsIgnoreCase(usuario.getPerfil().getNome());
    }

    public List<Usuario> listar() {
        return this.usuarioDAO.listarTodos();
    }

    public Usuario buscarPorId(Long id) {
        return (id == null) ? null : this.usuarioDAO.buscarPorId(id);
    }

    public void salvar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário é obrigatório.");
        }

        this.prepararDados(usuario);

        Usuario atual = null;
        if (usuario.getId() != null) {
            atual = this.usuarioDAO.buscarPorId(usuario.getId());
            if (atual == null) {
                throw new IllegalArgumentException("Usuário não encontrado para alteração.");
            }
            // Na edicao, senha em branco significa "manter a senha atual".
            if (usuario.getSenha() == null) {
                usuario.setSenha(atual.getSenha());
            }
        }

        this.validarCamposObrigatorios(usuario);
        this.validarSenha(usuario.getSenha());
        this.validarPerfilExistente(usuario.getPerfilId());
        this.validarLoginUnico(usuario);

        if (usuario.getId() == null) {
            this.usuarioDAO.inserir(usuario);
        } else {
            this.usuarioDAO.alterar(usuario);
        }
    }

    /**
     * Regra: o hotel nao pode ficar sem gerente.
     * O gerente tambem nao pode excluir o proprio login.
     */
    public void deletar(Long id, Usuario usuarioLogado) {
        if (id == null) {
            throw new IllegalArgumentException("Id é obrigatório para excluir.");
        }

        Usuario alvo = this.usuarioDAO.buscarPorId(id);
        if (alvo == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        if (usuarioLogado != null && id.equals(usuarioLogado.getId())) {
            throw new IllegalArgumentException("Você não pode excluir o próprio usuário.");
        }
        if (ehGerente(alvo) && this.contarGerentes() <= 1) {
            throw new IllegalArgumentException("O sistema precisa de pelo menos um gerente.");
        }

        this.usuarioDAO.deletar(id);
    }

    private int contarGerentes() {
        Perfil gerente = this.perfilDAO.buscarPorNome(PERFIL_GERENTE);
        return (gerente == null) ? 0 : this.usuarioDAO.contarPorPerfil(gerente.getId());
    }

    private void prepararDados(Usuario usuario) {
        usuario.setNome(this.normalizar(usuario.getNome()));
        usuario.setLogin(this.normalizar(usuario.getLogin()));
        usuario.setSenha(this.normalizar(usuario.getSenha()));
    }

    private void validarCamposObrigatorios(Usuario usuario) {
        if (usuario.getNome() == null) throw new IllegalArgumentException("Nome é obrigatório.");
        if (usuario.getLogin() == null) throw new IllegalArgumentException("Login é obrigatório.");
        if (usuario.getSenha() == null) throw new IllegalArgumentException("Senha é obrigatória.");
        if (usuario.getPerfilId() == null) throw new IllegalArgumentException("Perfil é obrigatório.");
    }

    private void validarSenha(String senha) {
        if (senha.length() < SENHA_MINIMA) {
            throw new IllegalArgumentException(
                    "Senha deve ter no mínimo " + SENHA_MINIMA + " caracteres.");
        }
    }

    private void validarPerfilExistente(Long perfilId) {
        if (this.perfilDAO.buscarPorId(perfilId) == null) {
            throw new IllegalArgumentException("Perfil informado não existe.");
        }
    }

    private void validarLoginUnico(Usuario usuario) {
        Usuario existente = this.usuarioDAO.buscarPorLogin(usuario.getLogin());
        if (existente == null) return;

        if (usuario.getId() == null || !existente.getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Já existe um usuário com este login.");
        }
    }

    private String normalizar(String valor) {
        if (valor == null) return null;
        String limpo = valor.trim();
        return limpo.isEmpty() ? null : limpo;
    }
}
