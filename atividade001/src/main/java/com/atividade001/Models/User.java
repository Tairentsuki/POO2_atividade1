package com.atividade001.Models;

public class User {
    private String login;
    private String senha;
    private Setor setor;

    public User() {

    }

    public User(String login, String senha, Setor setor) {
        this.login = login;
        this.senha = senha;
        this.setor = setor;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return this.login;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getSenha(String senha) {
        return this.senha;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    public Setor getSetor() {
        return this.setor;
    }

}
