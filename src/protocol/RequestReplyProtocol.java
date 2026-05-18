package protocol;

import common.MethodIds;
import common.RemoteObjectRef;
import common.RemoteService;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Protocolo request-reply (seção 5.2). A comunicação de rede é delegada ao
 * Java RMI — este protocolo empacota/desempacota mensagens e invoca stubs remotos.
 */
public class RequestReplyProtocol {

    private static final AtomicInteger requestCounter = new AtomicInteger(1);

    private final Map<String, RemoteService> stubCache = new ConcurrentHashMap<>();

    private byte[] pendingIncoming;
    private Message lastRequest;

    /**
     * Envia uma requisição ao objeto remoto via RMI e retorna o payload da resposta.
     */
    public byte[] doOperation(RemoteObjectRef ref, int methodId, byte[] arguments)
            throws RemoteException, NotBoundException {

        int reqId = requestCounter.getAndIncrement();

        Message request = new Message(
                Message.REQUEST,
                reqId,
                ref.getObjectName(),
                MethodIds.nomeMetodo(methodId),
                new String(arguments, java.nio.charset.StandardCharsets.UTF_8)
        );

        System.out.printf("[CLIENT] doOperation → objeto='%s' methodId=%d (%s) reqId=%d%n",
                ref.getObjectName(), methodId, request.getMethodId(), reqId);

        RemoteService stub = lookupStub(ref);
        byte[] replyFrame = stub.processRequest(request.toBytes());

        Message reply = Message.fromBytes(extractPayload(replyFrame));
        System.out.printf("[CLIENT] Resposta recebida ← reqId=%d%n", reply.getRequestId());

        return reply.getArguments().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    /**
     * Prepara a requisição recebida via RMI para processamento no servidor.
     * Equivalente a {@code getRequest()} da seção 5.2 — o transporte é feito pelo RMI.
     */
    public byte[] getRequest() throws IOException {
        if (pendingIncoming == null) {
            throw new IllegalStateException("Nenhuma requisição pendente.");
        }
        byte[] payload = extractPayload(pendingIncoming);
        pendingIncoming = null;
        lastRequest = Message.fromBytes(payload);
        return payload;
    }

    /**
     * Formata a resposta. Com RMI, o frame retornado é enviado ao cliente pelo
     * runtime RMI (em UDP seria endereçado a clientHost:clientPort).
     */
    public byte[] sendReply(byte[] reply, InetAddress clientHost, int clientPort) {
        int reqId = (lastRequest != null) ? lastRequest.getRequestId() : 0;

        Message replyMsg = new Message(
                Message.REPLY,
                reqId,
                "",
                "",
                new String(reply, java.nio.charset.StandardCharsets.UTF_8)
        );

        if (clientHost != null) {
            System.out.printf("[SERVER] sendReply → %s:%d reqId=%d%n",
                    clientHost.getHostAddress(), clientPort, reqId);
        } else {
            System.out.printf("[SERVER] sendReply → reqId=%d (via RMI)%n", reqId);
        }

        return replyMsg.toBytes();
    }

    /** Chamado pelo objeto remoto antes de {@link #getRequest()}. */
    public void receiveIncoming(byte[] frame) {
        this.pendingIncoming = frame;
    }

    public Message getLastRequest() {
        return lastRequest;
    }

    private RemoteService lookupStub(RemoteObjectRef ref)
            throws RemoteException, NotBoundException {

        String key = ref.getHost() + ":" + ref.getPort() + "/" + ref.getObjectName();
        RemoteService cached = stubCache.get(key);
        if (cached != null) return cached;

        Registry registry = LocateRegistry.getRegistry(ref.getHost(), ref.getPort());
        RemoteService stub = (RemoteService) registry.lookup(ref.getObjectName());
        stubCache.put(key, stub);
        return stub;
    }

    static byte[] extractPayload(byte[] frame) {
        if (frame.length < 4) {
            return frame;
        }
        int length = ((frame[0] & 0xFF) << 24)
                   | ((frame[1] & 0xFF) << 16)
                   | ((frame[2] & 0xFF) << 8)
                   |  (frame[3] & 0xFF);

        if (length <= 0 || length + 4 != frame.length) {
            return frame;
        }

        byte[] payload = new byte[length];
        System.arraycopy(frame, 4, payload, 0, length);
        return payload;
    }
}
