package server;

import protocol.Message;
import protocol.RequestReplyProtocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RHServer {

    private static final int PORT = 5555;

    private final ColaboradorService colaboradorService  = new ColaboradorService();
    private final DepartamentoService departamentoService = new DepartamentoService(colaboradorService);

    private final RequestReplyProtocol protocol = new RequestReplyProtocol();

    public void start() {
        System.out.println("=== Servidor RMI de RH iniciado na porta " + PORT + " ===");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SERVER] Nova conexão: " + clientSocket.getInetAddress());
                Thread.ofVirtual().start(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("[SERVER] Erro fatal: " + e.getMessage());
        }
    }

    private void handleClient(Socket socket) {
        try (DataInputStream  in  = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            byte[] rawRequest = protocol.getRequest(in);
            Message request   = Message.fromBytes(rawRequest);

            System.out.println("[SERVER] Recebido: " + request);

            byte[] result = dispatch(request);
            protocol.sendReply(result, request.getRequestId(), out);

        } catch (IOException e) {
            System.err.println("[SERVER] Erro ao processar cliente: " + e.getMessage());
        }
    }

    private byte[] dispatch(Message request) {
        String obj    = request.getObjectReference();
        String method = request.getMethodId();
        String args   = request.getArguments();

        try {
            return switch (obj) {
                case "ColaboradorService"  -> colaboradorService.invocar(method, args);
                case "DepartamentoService" -> departamentoService.invocar(method, args);
                default -> erro("Objeto remoto desconhecido: " + obj);
            };
        } catch (Exception e) {
            return erro("Erro ao executar " + obj + "." + method + ": " + e.getMessage());
        }
    }

    private byte[] erro(String msg) {
        System.err.println("[SERVER] " + msg);
        return ("{\"erro\":\"" + msg + "\"}").getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        new RHServer().start();
    }
}
