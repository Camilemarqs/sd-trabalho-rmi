package common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonSerializer {

    // Colaborador → JSON
    public static JSONObject colaboradorParaJson(Colaborador c) {
        JSONObject obj = new JSONObject();
        obj.put("tipo", c.getTipo());
        obj.put("id", c.getId());
        obj.put("nome", c.getNome());
        obj.put("salario", c.getSalario());

        switch (c.getTipo()) {
            case "Funcionario" -> {
                Funcionario f = (Funcionario) c;
                obj.put("cargo", f.getCargo());
                obj.put("dataAdmissao", f.getDataAdmissao());
            }
            case "Estagiario" -> {
                Estagiario e = (Estagiario) c;
                obj.put("curso", e.getCurso());
                obj.put("cargaHorariaSemanal", e.getCargaHorariaSemanal());
            }
            case "Autonomo" -> {
                Autonomo a = (Autonomo) c;
                obj.put("especialidade", a.getEspecialidade());
                obj.put("cnpj", a.getCnpj());
            }
            case "Efetivo" -> {
                Efetivo ef = (Efetivo) c;
                obj.put("cargo", ef.getCargo());
                obj.put("dataAdmissao", ef.getDataAdmissao());
                obj.put("bonusAnual", ef.getBonusAnual());
                obj.put("anosDeEmpresa", ef.getAnosDeEmpresa());
            }
        }
        return obj;
    }

    public static Colaborador jsonParaColaborador(JSONObject obj) {
        String tipo = obj.getString("tipo");
        int id = obj.getInt("id");
        String nome = obj.getString("nome");
        double salario = obj.getDouble("salario");

        return switch (tipo) {
            case "Funcionario" -> new Funcionario(id, nome, salario,
                    obj.getString("cargo"), obj.getString("dataAdmissao"));
            case "Estagiario" -> new Estagiario(id, nome, salario,
                    obj.getString("curso"), obj.getInt("cargaHorariaSemanal"));
            case "Autonomo" -> new Autonomo(id, nome, salario,
                    obj.getString("especialidade"), obj.getString("cnpj"));
            case "Efetivo" -> new Efetivo(id, nome, salario,
                    obj.getString("cargo"), obj.getString("dataAdmissao"),
                    obj.getDouble("bonusAnual"), obj.getInt("anosDeEmpresa"));
            default -> throw new IllegalArgumentException("Tipo desconhecido: " + tipo);
        };
    }

    // Departamento → JSON
    public static JSONObject departamentoParaJson(Departamento d) {
        JSONObject obj = new JSONObject();
        obj.put("id", d.getId());
        obj.put("nome", d.getNome());

        if (d.getGerente() != null) {
            obj.put("gerente", colaboradorParaJson(d.getGerente()));
        }

        JSONArray membros = new JSONArray();
        for (Colaborador c : d.getColaboradores()) {
            membros.put(colaboradorParaJson(c));
        }
        obj.put("colaboradores", membros);
        return obj;
    }

    public static Departamento jsonParaDepartamento(JSONObject obj) {
        int id = obj.getInt("id");
        String nome = obj.getString("nome");

        Colaborador gerente = null;
        if (obj.has("gerente") && !obj.isNull("gerente")) {
            gerente = jsonParaColaborador(obj.getJSONObject("gerente"));
        }

        Departamento d = new Departamento(id, nome, gerente);

        if (obj.has("colaboradores")) {
            JSONArray membros = obj.getJSONArray("colaboradores");
            for (int i = 0; i < membros.length(); i++) {
                d.adicionarColaborador(jsonParaColaborador(membros.getJSONObject(i)));
            }
        }
        return d;
    }

    // Lista de Colaboradores → JSON
    public static JSONArray listaColaboradoresParaJson(List<Colaborador> lista) {
        JSONArray arr = new JSONArray();
        for (Colaborador c : lista) arr.put(colaboradorParaJson(c));
        return arr;
    }

    public static List<Colaborador> jsonParaListaColaboradores(JSONArray arr) {
        List<Colaborador> lista = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            lista.add(jsonParaColaborador(arr.getJSONObject(i)));
        }
        return lista;
    }

    // Lista de Departamentos → JSON
    public static JSONArray listaDepartamentosParaJson(List<Departamento> lista) {
        JSONArray arr = new JSONArray();
        for (Departamento d : lista) arr.put(departamentoParaJson(d));
        return arr;
    }

    public static List<Departamento> jsonParaListaDepartamentos(JSONArray arr) {
        List<Departamento> lista = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            lista.add(jsonParaDepartamento(arr.getJSONObject(i)));
        }
        return lista;
    }

    // RemoteObjectRef → JSON
    public static JSONObject refParaJson(RemoteObjectRef ref) {
        JSONObject obj = new JSONObject();
        obj.put("host", ref.getHost());
        obj.put("port", ref.getPort());
        obj.put("objectName", ref.getObjectName());
        return obj;
    }

    public static RemoteObjectRef jsonParaRef(JSONObject obj) {
        return new RemoteObjectRef(
                obj.getString("host"),
                obj.getInt("port"),
                obj.getString("objectName")
        );
    }
}
