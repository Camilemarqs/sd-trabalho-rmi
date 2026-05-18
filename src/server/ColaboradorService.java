package server;

import common.*;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ColaboradorService {

    private final ConcurrentHashMap<Integer, Colaborador> repositorio = new ConcurrentHashMap<>();

    public ColaboradorService() {
        repositorio.put(1, new Funcionario(1, "Ana Lima",    4500.0, "Analista",  "01/03/2022"));
        repositorio.put(2, new Estagiario(2, "Carlos Melo",  1200.0, "Ciência da Computação", 30));
        repositorio.put(3, new Autonomo  (3, "Diana Souza",  8000.0, "UX Designer", "12.345.678/0001-99"));
        repositorio.put(4, new Efetivo   (4, "Eduardo Neto", 7200.0, "Tech Lead",
                "15/06/2018", 12000.0, 7));
    }

    public byte[] invocar(String method, String argsJson) {
        return switch (method) {
            case "adicionarColaborador"  -> adicionarColaborador(argsJson);
            case "buscarColaborador"     -> buscarColaborador(argsJson);
            case "listarColaboradores"   -> listarColaboradores();
            case "removerColaborador"    -> removerColaborador(argsJson);
            case "calcularFolhaTotal"    -> calcularFolhaTotal();
            default -> erro("Método desconhecido: " + method);
        };
    }

    private byte[] adicionarColaborador(String argsJson) {
        JSONObject args = new JSONObject(argsJson);
        Colaborador c = JsonSerializer.jsonParaColaborador(args.getJSONObject("colaborador"));

        if (repositorio.containsKey(c.getId())) {
            return erro("Colaborador com id=" + c.getId() + " já existe.");
        }
        repositorio.put(c.getId(), c);

        JSONObject resp = new JSONObject();
        resp.put("status", "ok");
        resp.put("mensagem", "Colaborador '" + c.getNome() + "' adicionado com sucesso.");
        return resp.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private byte[] buscarColaborador(String argsJson) {
        int id = new JSONObject(argsJson).getInt("id");
        Colaborador c = repositorio.get(id);

        if (c == null) {
            return erro("Colaborador id=" + id + " não encontrado.");
        }
        return JsonSerializer.colaboradorParaJson(c).toString()
                .getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private byte[] listarColaboradores() {
        List<Colaborador> lista = new ArrayList<>(repositorio.values());
        lista.sort((a, b) -> Integer.compare(a.getId(), b.getId()));

        JSONObject resp = new JSONObject();
        resp.put("colaboradores", JsonSerializer.listaColaboradoresParaJson(lista));
        return resp.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private byte[] removerColaborador(String argsJson) {
        int id = new JSONObject(argsJson).getInt("id");
        Colaborador removido = repositorio.remove(id);

        JSONObject resp = new JSONObject();
        if (removido != null) {
            resp.put("status", "ok");
            resp.put("mensagem", "Colaborador '" + removido.getNome() + "' removido.");
        } else {
            resp.put("status", "erro");
            resp.put("mensagem", "Colaborador id=" + id + " não encontrado.");
        }
        return resp.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private byte[] calcularFolhaTotal() {
        double total = repositorio.values().stream()
                .mapToDouble(Colaborador::calcularCustoTotal).sum();
        JSONObject resp = new JSONObject();
        resp.put("folhaTotal", total);
        return resp.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    public Colaborador buscarPorId(int id) {
        return repositorio.get(id);
    }

    public ConcurrentHashMap<Integer, Colaborador> getRepositorio() {
        return repositorio;
    }

    private byte[] erro(String msg) {
        return ("{\"status\":\"erro\",\"mensagem\":\"" + msg + "\"}")
                .getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
}
