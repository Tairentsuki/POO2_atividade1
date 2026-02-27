package model;

public class Setor {
    private int id;
    private String nome;
    private String ramal;

    public Setor() {
    }

    public Setor(String nome, String ramal) {
        this.nome = nome;
        this.ramal = ramal;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return this.nome;
    }

    public void setRamal(String ramal) {
        this.ramal = ramal;
    }

    public String getRamal() {
        return this.ramal;
    }

}
