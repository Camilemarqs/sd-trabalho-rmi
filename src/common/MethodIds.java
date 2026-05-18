package common;

public final class MethodIds {

    private MethodIds() {}

    // ColaboradorService
    public static final int ADICIONAR_COLABORADOR  = 1;
    public static final int BUSCAR_COLABORADOR     = 2;
    public static final int LISTAR_COLABORADORES   = 3;
    public static final int REMOVER_COLABORADOR    = 4;
    public static final int CALCULAR_FOLHA_TOTAL   = 5;

    // DepartamentoService
    public static final int CRIAR_DEPARTAMENTO                  = 10;
    public static final int BUSCAR_DEPARTAMENTO                 = 11;
    public static final int ADICIONAR_COLABORADOR_AO_DEPARTAMENTO = 12;
    public static final int LISTAR_DEPARTAMENTOS                = 13;
    public static final int CALCULAR_FOLHA_DEPARTAMENTO         = 14;

    public static String nomeMetodo(int methodId) {
        return switch (methodId) {
            case ADICIONAR_COLABORADOR  -> "adicionarColaborador";
            case BUSCAR_COLABORADOR     -> "buscarColaborador";
            case LISTAR_COLABORADORES   -> "listarColaboradores";
            case REMOVER_COLABORADOR    -> "removerColaborador";
            case CALCULAR_FOLHA_TOTAL   -> "calcularFolhaTotal";
            case CRIAR_DEPARTAMENTO     -> "criarDepartamento";
            case BUSCAR_DEPARTAMENTO    -> "buscarDepartamento";
            case ADICIONAR_COLABORADOR_AO_DEPARTAMENTO -> "adicionarColaboradorAoDepartamento";
            case LISTAR_DEPARTAMENTOS   -> "listarDepartamentos";
            case CALCULAR_FOLHA_DEPARTAMENTO -> "calcularFolhaDepartamento";
            default -> throw new IllegalArgumentException("methodId desconhecido: " + methodId);
        };
    }
}
