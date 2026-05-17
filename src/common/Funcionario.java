package common;

public class Funcionario extends Colaborador implements Admissivel {

    private String cargo;
    private String dataAdmissao; // formato dd/MM/yyyy

    public Funcionario() {}

    public Funcionario(int id, String nome, double salario, String cargo, String dataAdmissao) {
        super(id, nome, salario);
        this.cargo = cargo;
        this.dataAdmissao = dataAdmissao;
    }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    @Override
    public String getDataAdmissao() { return dataAdmissao; }
    public void setDataAdmissao(String dataAdmissao) { this.dataAdmissao = dataAdmissao; }

    @Override
    public String getTipo() { return "Funcionario"; }

    /** Custo total inclui 68% de encargos trabalhistas sobre o salário bruto. */
    @Override
    public double calcularCustoTotal() {
        return salario * 1.68;
    }

    @Override
    public String admitir() {
        return String.format("Funcionário %s admitido no cargo '%s' em %s.", nome, cargo, dataAdmissao);
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | cargo=%s | admissao=%s", cargo, dataAdmissao);
    }
}
