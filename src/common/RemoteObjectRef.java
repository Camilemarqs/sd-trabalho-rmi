package common;

public class RemoteObjectRef {

    private String host;
    private int port;
    private String objectName; // nome do serviço: "ColaboradorService" | "DepartamentoService"

    public RemoteObjectRef() {}

    public RemoteObjectRef(String host, int port, String objectName) {
        this.host = host;
        this.port = port;
        this.objectName = objectName;
    }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getObjectName() { return objectName; }
    public void setObjectName(String objectName) { this.objectName = objectName; }

    @Override
    public String toString() {
        return String.format("RemoteObjectRef{host='%s', port=%d, object='%s'}", host, port, objectName);
    }
}
