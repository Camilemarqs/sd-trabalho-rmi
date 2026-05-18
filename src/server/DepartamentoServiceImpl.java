package server;

import common.*;
import org.json.JSONObject;
import protocol.Message;
import protocol.RequestReplyProtocol;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DepartamentoServiceImpl extends UnicastRemoteObject implements RemoteService {

    private final ConcurrentHashMap<Integer, Departamento> repositorio = new ConcurrentHashMap<>();
    private final ColaboradorServiceImpl colaboradorService;
    private final RequestReplyProtocol protocol = new RequestReplyProtocol();

    public DepartamentoServiceImpl(ColaboradorServiceImpl colaboradorService) throws RemoteException {
        super();
        this.colaboradorService = colaboradorService;

        Colaborador gerente1 = colaboradorService.buscarPorId(4);
        Departamento ti = new Departamento(1, "Tecnologia da Informação", gerente1);
        ti.adicionarColaborador(colaboradorService.buscarPorId(1));
        ti.adicionarColaborador(colaboradorService.buscarPorId(2));
        repositorio.put(1, ti);

        Departamento design = new Departamento(2, "Design & UX", null);
        design.adicionarColaborador(colaboradorService.buscarPorId(3));
        repositorio.put(2, design);
    }

    @Override
    public byte[] processRequest(byte[] requestFrame) throws RemoteException {
        try {
            protocol.receiveIncoming(requestFrame);
            protocol.getRequest();
            Message req = protocol.getLastRequest();

            System.out.println("[SERVER] Recebido: " + req);

            byte[] result = invocar(req.getMethodId(), req.getArguments());
            return protocol.sendReply(result, null, 0);

        } catch (IOException e) {
            throw new RemoteException("Erro ao processar requisição", e);
        }
    }

    byte[] invocar(String method, String argsJson) {
        return switch (method) {
            case "criarDepartamento"                  -> criarDepartamento(argsJson);
            case "buscarDepartamento"                 -> buscarDepartamento(argsJson);
            case "adicionarColaboradorAoDepartamento" -> adicionarColaboradorAoDepartamento(argsJson);
            case "listarDepartamentos"                -> listarDepartamentos();
            case "calcularFolhaDepartamento"          -> calcularFolhaDepartamento(argsJson);
            default -> erro("Método desconhecido: " + method);
        };
    }

    private byte[] criarDepartamento(String argsJson) {
        JSONObject args = new JSONObject(argsJson);
        int id = args.getInt("id");
        String nome = args.getString("nome");

        Colaborador gerente = null;
        if (args.has("idGerente") && !args.isNull("idGerente")) {
            gerente = colaboradorService.buscarPorId(args.getInt("idGerente"));
        }

        if (repositorio.containsKey(id)) {
            return erro("Departamento id=" + id + " já existe.");
        }

        Departamento d = new Departamento(id, nome, gerente);
        repositorio.put(id, d);

        JSONObject resp = new JSONObject();
        resp.put("status", "ok");
        resp.put("mensagem", "Departamento '" + nome + "' criado.");
        return resp.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private byte[] buscarDepartamento(String argsJson) {
        int id = new JSONObject(argsJson).getInt("id");
        Departamento d = repositorio.get(id);

        if (d == null) return erro("Departamento id=" + id + " não encontrado.");
        return JsonSerializer.departamentoParaJson(d).toString()
                .getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private byte[] adicionarColaboradorAoDepartamento(String argsJson) {
        JSONObject args = new JSONObject(argsJson);
        int idDept = args.getInt("idDepartamento");
        int idColab = args.getInt("idColaborador");

        Departamento d = repositorio.get(idDept);
        if (d == null) return erro("Departamento id=" + idDept + " não encontrado.");

        Colaborador c = colaboradorService.buscarPorId(idColab);
        if (c == null) return erro("Colaborador id=" + idColab + " não encontrado.");

        d.adicionarColaborador(c);

        JSONObject resp = new JSONObject();
        resp.put("status", "ok");
        resp.put("mensagem", "Colaborador '" + c.getNome() + "' adicionado ao depto '" + d.getNome() + "'.");
        return resp.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private byte[] listarDepartamentos() {
        List<Departamento> lista = new ArrayList<>(repositorio.values());
        lista.sort((a, b) -> Integer.compare(a.getId(), b.getId()));

        JSONObject resp = new JSONObject();
        resp.put("departamentos", JsonSerializer.listaDepartamentosParaJson(lista));
        return resp.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private byte[] calcularFolhaDepartamento(String argsJson) {
        int id = new JSONObject(argsJson).getInt("id");
        Departamento d = repositorio.get(id);
        if (d == null) return erro("Departamento id=" + id + " não encontrado.");

        JSONObject resp = new JSONObject();
        resp.put("departamento", d.getNome());
        resp.put("folha", d.calcularFolha());
        return resp.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private byte[] erro(String msg) {
        return ("{\"status\":\"erro\",\"mensagem\":\"" + msg + "\"}")
                .getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
}
