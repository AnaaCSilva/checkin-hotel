package br.com.mvc.service;

import br.com.mvc.dao.PerfilDAO;
import br.com.mvc.dao.UsuarioDAO;
import br.com.mvc.model.Usuario;

import java.util.List;

public class UsuarioService {

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

    public List<Usuario> listar() {
        return this.usuarioDAO.listarTodos();
    }

    public Usuario buscarPorId(Long id) {
        if (id == null) {
            return null;
        }
        return this.usuarioDAO.buscarPorId(id);
    }

    public void salvar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário é obrigatório.");
        }

        this.prepararDados(usuario);
        this.validarCamposObrigatorios(usuario);
        this.validarSenha(usuario.getSenha());
        this.validarPerfilExistente(usuario.getPerfilId());
        this.validarLoginUnico(usuario);

        if (usuario.getId() == null) {
            this.usuarioDAO.inserir(usuario);
            return;
        }

        if (this.usuarioDAO.buscarPorId(usuario.getId()) == null) {
            throw new IllegalArgumentException("Usuário não encontrado para alteração.");
        }
        this.usuarioDAO.alterar(usuario);
    }

    public void deletar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id é obrigatório para excluir.");
        }
        if (this.usuarioDAO.buscarPorId(id) == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        this.usuarioDAO.deletar(id);
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
            throw new IllegalArgumentException("Senha deve ter no mínimo " + SENHA_MINIMA + " caracteres.");
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