package common;

public class Estagiario extends Colaborador {

    private String curso;
    private int cargaHorariaSemanal; // horas por semana

    public Estagiario() {}

    public Estagiario(int id, String nome, double salario, String curso, int cargaHorariaSemanal) {
        super(id, nome, salario);
        this.curso = curso;
        this.cargaHorariaSemanal = cargaHorariaSemanal;
    }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    public int getCargaHorariaSemanal() { return cargaHorariaSemanal; }
    public void setCargaHorariaSemanal(int cargaHorariaSemanal) {
        this.cargaHorariaSemanal = cargaHorariaSemanal;
    }

    @Override
    public String getTipo() { return "Estagiario"; }

    /** Custo total: salário + vale-transporte (fixo R$200) + seguro (5%). */
    @Override
    public double calcularCustoTotal() {
        return salario + 200.0 + salario * 0.05;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | curso=%s | cargaH=%dh/sem", curso, cargaHorariaSemanal);
    }
}
