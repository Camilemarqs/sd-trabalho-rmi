package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Departamento implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private Colaborador gerente;                // "tem-um" Colaborador responsável
    private List<Colaborador> colaboradores;    // "tem-um" coleção de Colaboradores

    public Departamento() {
        this.colaboradores = new ArrayList<>();
    }

    public Departamento(int id, String nome, Colaborador gerente) {
        this.id = id;
        this.nome = nome;
        this.gerente = gerente;
        this.colaboradores = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Colaborador getGerente() { return gerente; }
    public void setGerente(Colaborador gerente) { this.gerente = gerente; }

    public List<Colaborador> getColaboradores() { return colaboradores; }
    public void setColaboradores(List<Colaborador> colaboradores) {
        this.colaboradores = colaboradores;
    }

    public void adicionarColaborador(Colaborador c) {
        colaboradores.add(c);
    }

    public boolean removerColaborador(int idColaborador) {
        return colaboradores.removeIf(c -> c.getId() == idColaborador);
    }

    public double calcularFolha() {
        return colaboradores.stream().mapToDouble(Colaborador::calcularCustoTotal).sum();
    }

    @Override
    public String toString() {
        String gerenteNome = (gerente != null) ? gerente.getNome() : "N/A";
        return String.format("Depto id=%d | nome=%s | gerente=%s | membros=%d | folha=R$%.2f",
                id, nome, gerenteNome, colaboradores.size(), calcularFolha());
    }
}
