package common;

import java.io.Serializable;

public abstract class Colaborador implements Serializable {

    private static final long serialVersionUID = 1L;

    protected int id;
    protected String nome;
    protected double salario;

    public Colaborador() {}

    public Colaborador(int id, String nome, double salario) {
        this.id = id;
        this.nome = nome;
        this.salario = salario;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }


    public abstract String getTipo();


    public abstract double calcularCustoTotal();

    @Override
    public String toString() {
        return String.format("[%s] id=%d | nome=%s | salario=R$%.2f | custoTotal=R$%.2f",
                getTipo(), id, nome, salario, calcularCustoTotal());
    }
}
