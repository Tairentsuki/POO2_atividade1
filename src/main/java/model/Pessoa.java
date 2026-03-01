package model;
import java.time.Period;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public abstract class Pessoa {
    private int id;
    private String nomeCompleto;
    private String cpf;
    private LocalDate dataDeNascimento;
    private String sexo;

    public Pessoa(String[] strings) {
    }

    public Pessoa(int _id, String nomeCompleto, String cpf, LocalDate dataDeNascimento, String sexo) {
        this.setId(_id);
        this.setNomeCompleto(nomeCompleto);
        this.setCpf(cpf);
        this.setDataDeNascimento(dataDeNascimento);
        this.setSexo(sexo);
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getNomeCompleto() {
        return this.nomeCompleto;
    }

    public void setId(int _id) {
        this.id = _id;
    }

    public int getId() {
        return this.id;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCpf() {
        return this.cpf;
    }

    public void setDataDeNascimento(LocalDate dataDeNascimento) {
        try {
            this.dataDeNascimento = dataDeNascimento;
        } catch (DateTimeParseException e) {
            System.err.println("Erro: A data deve estar no formato YYYY-MM-DD. Valor recebido: " + dataDeNascimento);
        }
    }

    public LocalDate getDataDeNascimento() {
        return this.dataDeNascimento;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getSexo() {
        return this.sexo;
    }
    public int calcularIdade(){
        LocalDate hoje = LocalDate.now();
        int idade = Period.between(this.dataDeNascimento, hoje).getYears();
        return idade;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("id: ").append(id).append("\n");
        sb.append("nomeCompleto: ").append(nomeCompleto).append("\n");
        sb.append("Cpf: ").append(cpf).append("\n");
        sb.append("Data de nascimento: ").append(dataDeNascimento).append("\n");
        sb.append("Sexo: ").append(sexo).append("\n");
        return sb.toString();
    }

    public String toCsv() {
        StringBuilder sb = new StringBuilder();

        sb.append(id).append(',');
        sb.append(nomeCompleto).append(',');
        sb.append(dataDeNascimento).append(',');
        sb.append(cpf).append(',');
        sb.append(sexo != null ? sexo : "");

        return sb.toString();
    }
}
