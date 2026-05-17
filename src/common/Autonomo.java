package common;

public class Autonomo extends Colaborador {

    private String especialidade;
    private String cnpj;

    public Autonomo() {}

    public Autonomo(int id, String nome, double salario, String especialidade, String cnpj) {
        super(id, nome, salario);
        this.especialidade = especialidade;
        this.cnpj = cnpj;
    }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    @Override
    public String getTipo() { return "Autonomo"; }

    @Override
    public double calcularCustoTotal() {
        return salario * 1.05;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | especialidade=%s | cnpj=%s", especialidade, cnpj);
    }
}
