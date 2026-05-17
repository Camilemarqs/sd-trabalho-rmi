package common;

public class Efetivo extends Funcionario {

    private double bonusAnual;
    private int anosDeEmpresa;

    public Efetivo() {}

    public Efetivo(int id, String nome, double salario, String cargo,
                   String dataAdmissao, double bonusAnual, int anosDeEmpresa) {
        super(id, nome, salario, cargo, dataAdmissao);
        this.bonusAnual = bonusAnual;
        this.anosDeEmpresa = anosDeEmpresa;
    }

    public double getBonusAnual() { return bonusAnual; }
    public void setBonusAnual(double bonusAnual) { this.bonusAnual = bonusAnual; }

    public int getAnosDeEmpresa() { return anosDeEmpresa; }
    public void setAnosDeEmpresa(int anosDeEmpresa) { this.anosDeEmpresa = anosDeEmpresa; }

    @Override
    public String getTipo() { return "Efetivo"; }

    /**
     * Custo total: encargos CLT (68%) + rateio mensal do bônus anual.
     */
    @Override
    public double calcularCustoTotal() {
        return salario * 1.68 + (bonusAnual / 12.0);
    }

    @Override
    public String admitir() {
        return String.format("Colaborador %s efetivado no cargo '%s' em %s com bônus anual de R$%.2f.",
                nome, getCargo(), getDataAdmissao(), bonusAnual);
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | bonus=R$%.2f/ano | anos=%d", bonusAnual, anosDeEmpresa);
    }
}
