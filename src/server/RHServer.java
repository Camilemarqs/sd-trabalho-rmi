package server;

import common.RemoteObjectRef;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RHServer {

    public static final int REGISTRY_PORT = RemoteObjectRef.DEFAULT_REGISTRY_PORT;

    public void start() {
        try {
            ColaboradorServiceImpl colaboradorService = new ColaboradorServiceImpl();
            DepartamentoServiceImpl departamentoService = new DepartamentoServiceImpl(colaboradorService);

            Registry registry = LocateRegistry.createRegistry(REGISTRY_PORT);
            registry.rebind("ColaboradorService", colaboradorService);
            registry.rebind("DepartamentoService", departamentoService);

            System.out.println("=== Servidor RMI de RH iniciado ===");
            System.out.println("Registry na porta " + REGISTRY_PORT);
            System.out.println("Objetos remotos: ColaboradorService, DepartamentoService");
            System.out.println("Aguardando invocações remotas...");

            synchronized (RHServer.class) {
                RHServer.class.wait();
            }

        } catch (Exception e) {
            System.err.println("[SERVER] Erro fatal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new RHServer().start();
    }
}
