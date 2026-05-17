package protocol;

import common.RemoteObjectRef;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestReplyProtocol {

    private static final AtomicInteger requestCounter = new AtomicInteger(1);

    public byte[] doOperation(RemoteObjectRef ref, String methodId, byte[] arguments) throws IOException {
        int reqId = requestCounter.getAndIncrement();

        Message request = new Message(
                Message.REQUEST,
                reqId,
                ref.getObjectName(),
                methodId,
                new String(arguments, java.nio.charset.StandardCharsets.UTF_8)
        );

        System.out.printf("[CLIENT] doOperation → objeto='%s' método='%s' reqId=%d%n",
                ref.getObjectName(), methodId, reqId);

        try (Socket socket = new Socket(ref.getHost(), ref.getPort());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream  in  = new DataInputStream(socket.getInputStream())) {

            // Envia a requisição
            byte[] frame = request.toBytes();
            out.write(frame);
            out.flush();

            // Aguarda a resposta
            byte[] replyBytes = readFrame(in);
            Message reply = Message.fromBytes(replyBytes);

            System.out.printf("[CLIENT] Resposta recebida ← reqId=%d%n", reply.getRequestId());
            return reply.getArguments().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    public byte[] getRequest(DataInputStream in) throws IOException {
        return readFrame(in);
    }

    public void sendReply(byte[] replyPayload, int requestId, DataOutputStream out) throws IOException {
        Message reply = new Message(
                Message.REPLY,
                requestId,
                "",
                "",
                new String(replyPayload, java.nio.charset.StandardCharsets.UTF_8)
        );
        byte[] frame = reply.toBytes();
        out.write(frame);
        out.flush();
        System.out.printf("[SERVER] Resposta enviada → reqId=%d%n", requestId);
    }

    private byte[] readFrame(DataInputStream in) throws IOException {
        byte[] lenBytes = new byte[4];
        in.readFully(lenBytes);
        int length = ((lenBytes[0] & 0xFF) << 24)
                   | ((lenBytes[1] & 0xFF) << 16)
                   | ((lenBytes[2] & 0xFF) << 8)
                   |  (lenBytes[3] & 0xFF);

        byte[] payload = new byte[length];
        in.readFully(payload);
        return payload;
    }
}
