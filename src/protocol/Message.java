package protocol;

import org.json.JSONObject;

public class Message {

    public static final int REQUEST = 0;
    public static final int REPLY   = 1;

    private int messageType;
    private int requestId;
    private String objectReference;
    private String methodId;
    private String arguments; // JSON string com os argumentos ou resultado

    public Message() {}

    public Message(int messageType, int requestId, String objectReference,
                   String methodId, String arguments) {
        this.messageType = messageType;
        this.requestId = requestId;
        this.objectReference = objectReference;
        this.methodId = methodId;
        this.arguments = arguments;
    }

    public byte[] toBytes() {
        JSONObject obj = new JSONObject();
        obj.put("messageType", messageType);
        obj.put("requestId", requestId);
        obj.put("objectReference", objectReference != null ? objectReference : "");
        obj.put("methodId", methodId != null ? methodId : "");
        obj.put("arguments", arguments != null ? arguments : "");
        String json = obj.toString();
        byte[] data = json.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] frame = new byte[4 + data.length];
        frame[0] = (byte) ((data.length >> 24) & 0xFF);
        frame[1] = (byte) ((data.length >> 16) & 0xFF);
        frame[2] = (byte) ((data.length >> 8)  & 0xFF);
        frame[3] = (byte)  (data.length        & 0xFF);
        System.arraycopy(data, 0, frame, 4, data.length);
        return frame;
    }

    public static Message fromBytes(byte[] bytes) {
        String json = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        JSONObject obj = new JSONObject(json);
        return new Message(
                obj.getInt("messageType"),
                obj.getInt("requestId"),
                obj.getString("objectReference"),
                obj.getString("methodId"),
                obj.getString("arguments")
        );
    }

    public int getMessageType() { return messageType; }
    public void setMessageType(int messageType) { this.messageType = messageType; }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public String getObjectReference() { return objectReference; }
    public void setObjectReference(String objectReference) { this.objectReference = objectReference; }

    public String getMethodId() { return methodId; }
    public void setMethodId(String methodId) { this.methodId = methodId; }

    public String getArguments() { return arguments; }
    public void setArguments(String arguments) { this.arguments = arguments; }

    @Override
    public String toString() {
        String tipo = (messageType == REQUEST) ? "REQUEST" : "REPLY";
        return String.format("Message{type=%s, reqId=%d, obj='%s', method='%s'}",
                tipo, requestId, objectReference, methodId);
    }
}
