package client;

import common.*;
import protocol.RequestReplyProtocol;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class RHClient {

    private static final String HOST = "localhost";
    private static final int    PORT = 5555;

    private final RequestReplyProtocol protocol = new RequestReplyProtocol();
    private final RemoteObjectRef colaboradorRef;
    private final RemoteObjectRef departamentoRef;

    public RHClient() {
        this.colaboradorRef   = new RemoteObjectRef(HOST, PORT, "ColaboradorService");
        this.departamentoRef  = new RemoteObjectRef(HOST, PORT, "DepartamentoService");
    }

    public void listarColaboradores() throws IOException {
        byte[] respBytes = protocol.doOperation(colaboradorRef, "listarColaboradores",
                "{}".getBytes());
        JSONObject resp = new JSONObject(new String(respBytes));
        JSONArray lista = resp.getJSONArray("colaboradores");
        List<Colaborador> colaboradores = JsonSerializer.jsonParaListaColaboradores(lista);

        System.out.println("\n── Colaboradores cadastrados ──────────────────────");
        for (Colaborador c : colaboradores) System.out.println("  " + c);
        System.out.println("──────────────────────────────────────────────────");
    }

    public void buscarColaborador(int id) throws IOException {
        JSONObject args = new JSONObject();
        args.put("id", id);

        byte[] respBytes = protocol.doOperation(colaboradorRef, "buscarColaborador",
                args.toString().getBytes());
        JSONObject resp = new JSONObject(new String(respBytes));

        if (resp.has("erro") || (resp.has("status") && resp.getString("status").equals("erro"))) {
            System.out.println("  Erro: " + (resp.has("mensagem") ? resp.getString("mensagem") : resp.getString("erro")));
        } else {
            Colaborador c = JsonSerializer.jsonParaColaborador(resp);
            System.out.println("\n  Encontrado: " + c);

            if (c instanceof Admissivel a) {
                System.out.println("  Admissão: " + a.admitir());
            }
        }
    }

    public void adicionarColaborador(Colaborador colaborador) throws IOException {
        JSONObject args = new JSONObject();
        args.put("colaborador", JsonSerializer.colaboradorParaJson(colaborador));

        byte[] respBytes = protocol.doOperation(colaboradorRef, "adicionarColaborador",
                args.toString().getBytes());
        JSONObject resp = new JSONObject(new String(respBytes));
        System.out.println("  " + resp.getString("mensagem"));
    }

    public void removerColaborador(int id) throws IOException {
        JSONObject args = new JSONObject();
        args.put("id", id);

        byte[] respBytes = protocol.doOperation(colaboradorRef, "removerColaborador",
                args.toString().getBytes());
        JSONObject resp = new JSONObject(new String(respBytes));
        System.out.println("  " + resp.getString("mensagem"));
    }

    public void calcularFolhaTotal() throws IOException {
        byte[] respBytes = protocol.doOperation(colaboradorRef, "calcularFolhaTotal",
                "{}".getBytes());
        JSONObject resp = new JSONObject(new String(respBytes));
        System.out.printf("  Folha total da empresa: R$%.2f%n", resp.getDouble("folhaTotal"));
    }

    public void listarDepartamentos() throws IOException {
        byte[] respBytes = protocol.doOperation(departamentoRef, "listarDepartamentos",
                "{}".getBytes());
        JSONObject resp = new JSONObject(new String(respBytes));
        JSONArray lista = resp.getJSONArray("departamentos");
        List<Departamento> deptos = JsonSerializer.jsonParaListaDepartamentos(lista);

        System.out.println("\n── Departamentos ──────────────────────────────────");
        for (Departamento d : deptos) System.out.println("  " + d);
        System.out.println("──────────────────────────────────────────────────");
    }

    public void buscarDepartamento(int id) throws IOException {
        JSONObject args = new JSONObject();
        args.put("id", id);
        byte[] respBytes = protocol.doOperation(departamentoRef, "buscarDepartamento",
                args.toString().getBytes());
        JSONObject resp = new JSONObject(new String(respBytes));

        if (resp.has("status") && resp.getString("status").equals("erro")) {
            System.out.println("  Erro: " + resp.getString("mensagem"));
        } else {
            Departamento d = JsonSerializer.jsonParaDepartamento(resp);
            System.out.println("\n  " + d);
            System.out.println("  Membros:");
            for (Colaborador c : d.getColaboradores()) {
                System.out.println("    - " + c);
            }
        }
    }

    public void criarDepartamento(int id, String nome, Integer idGerente) throws IOException {
        JSONObject args = new JSONObject();
        args.put("id", id);
        args.put("nome", nome);
        if (idGerente != null) args.put("idGerente", idGerente);

        byte[] respBytes = protocol.doOperation(departamentoRef, "criarDepartamento",
                args.toString().getBytes());
        JSONObject resp = new JSONObject(new String(respBytes));
        System.out.println("  " + resp.getString("mensagem"));
    }

    public void adicionarColaboradorAoDepartamento(int idDept, int idColab) throws IOException {
        JSONObject args = new JSONObject();
        args.put("idDepartamento", idDept);
        args.put("idColaborador", idColab);

        byte[] respBytes = protocol.doOperation(departamentoRef, "adicionarColaboradorAoDepartamento",
                args.toString().getBytes());
        JSONObject resp = new JSONObject(new String(respBytes));
        System.out.println("  " + resp.getString("mensagem"));
    }

    public void calcularFolhaDepartamento(int id) throws IOException {
        JSONObject args = new JSONObject();
        args.put("id", id);
        byte[] respBytes = protocol.doOperation(departamentoRef, "calcularFolhaDepartamento",
                args.toString().getBytes());
        JSONObject resp = new JSONObject(new String(respBytes));
        if (resp.has("folha")) {
            System.out.printf("  Folha do depto '%s': R$%.2f%n",
                    resp.getString("departamento"), resp.getDouble("folha"));
        } else {
            System.out.println("  " + resp.getString("mensagem"));
        }
    }

    public void menuPrincipal() {
        Scanner sc = new Scanner(System.in);
        int opcao = -1;

        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║   Sistema de Gestão de RH — RMI Client   ║");
        System.out.println("╚══════════════════════════════════════════╝");

        while (opcao != 0) {
            System.out.println("""
                    \n─── MENU ────────────────────────────────────
                    [1] Listar colaboradores
                    [2] Buscar colaborador por id
                    [3] Adicionar colaborador
                    [4] Remover colaborador
                    [5] Calcular folha total da empresa
                    [6] Listar departamentos
                    [7] Buscar departamento por id
                    [8] Criar departamento
                    [9] Adicionar colaborador a departamento
                    [10] Calcular folha de departamento
                    [0] Sair
                    ─────────────────────────────────────────────""");
            System.out.print("Opção: ");

            try {
                opcao = Integer.parseInt(sc.nextLine().trim());
                switch (opcao) {
                    case 1 -> listarColaboradores();
                    case 2 -> {
                        System.out.print("ID do colaborador: ");
                        buscarColaborador(Integer.parseInt(sc.nextLine().trim()));
                    }
                    case 3 -> {
                        Colaborador novo = lerColaborador(sc);
                        if (novo != null) adicionarColaborador(novo);
                    }
                    case 4 -> {
                        System.out.print("ID a remover: ");
                        removerColaborador(Integer.parseInt(sc.nextLine().trim()));
                    }
                    case 5 -> calcularFolhaTotal();
                    case 6 -> listarDepartamentos();
                    case 7 -> {
                        System.out.print("ID do departamento: ");
                        buscarDepartamento(Integer.parseInt(sc.nextLine().trim()));
                    }
                    case 8 -> {
                        System.out.print("ID do novo depto: ");
                        int idD = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Nome do depto: ");
                        String nomeD = sc.nextLine().trim();
                        System.out.print("ID do gerente (Enter para nenhum): ");
                        String g = sc.nextLine().trim();
                        criarDepartamento(idD, nomeD, g.isEmpty() ? null : Integer.parseInt(g));
                    }
                    case 9 -> {
                        System.out.print("ID do departamento: ");
                        int idD = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("ID do colaborador: ");
                        int idC = Integer.parseInt(sc.nextLine().trim());
                        adicionarColaboradorAoDepartamento(idD, idC);
                    }
                    case 10 -> {
                        System.out.print("ID do departamento: ");
                        calcularFolhaDepartamento(Integer.parseInt(sc.nextLine().trim()));
                    }
                    case 0 -> System.out.println("Encerrando cliente...");
                    default -> System.out.println("Opção inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("  Entrada inválida.");
            } catch (IOException e) {
                System.err.println("  Erro de comunicação com o servidor: " + e.getMessage());
            }
        }
    }

    private Colaborador lerColaborador(Scanner sc) {
        try {
            System.out.println("Tipo: [1] Funcionario [2] Estagiario [3] Autonomo [4] Efetivo");
            int tipo = Integer.parseInt(sc.nextLine().trim());
            System.out.print("ID: ");
            int id = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Nome: ");
            String nome = sc.nextLine().trim();
            System.out.print("Salário: ");
            double sal = Double.parseDouble(sc.nextLine().trim().replace(",", "."));

            return switch (tipo) {
                case 1 -> {
                    System.out.print("Cargo: ");
                    String cargo = sc.nextLine().trim();
                    System.out.print("Data de admissão (dd/MM/yyyy): ");
                    String data = sc.nextLine().trim();
                    yield new Funcionario(id, nome, sal, cargo, data);
                }
                case 2 -> {
                    System.out.print("Curso: ");
                    String curso = sc.nextLine().trim();
                    System.out.print("Carga horária semanal: ");
                    int ch = Integer.parseInt(sc.nextLine().trim());
                    yield new Estagiario(id, nome, sal, curso, ch);
                }
                case 3 -> {
                    System.out.print("Especialidade: ");
                    String esp = sc.nextLine().trim();
                    System.out.print("CNPJ: ");
                    String cnpj = sc.nextLine().trim();
                    yield new Autonomo(id, nome, sal, esp, cnpj);
                }
                case 4 -> {
                    System.out.print("Cargo: ");
                    String cargo = sc.nextLine().trim();
                    System.out.print("Data de admissão (dd/MM/yyyy): ");
                    String data = sc.nextLine().trim();
                    System.out.print("Bônus anual: ");
                    double bonus = Double.parseDouble(sc.nextLine().trim().replace(",", "."));
                    System.out.print("Anos de empresa: ");
                    int anos = Integer.parseInt(sc.nextLine().trim());
                    yield new Efetivo(id, nome, sal, cargo, data, bonus, anos);
                }
                default -> {
                    System.out.println("Tipo inválido.");
                    yield null;
                }
            };
        } catch (Exception e) {
            System.out.println("Dados inválidos: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        new RHClient().menuPrincipal();
    }
}
