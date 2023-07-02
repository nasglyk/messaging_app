import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

public class Brokerinfo  implements Serializable {
    public int port;
    public InetAddress add;
    public ArrayList<String> broker_topics;

    public int getSecond_broker_port() {
        return second_broker_port;
    }

    public void setSecond_broker_port(int second_broker_port) {
        this.second_broker_port = second_broker_port;
    }

    public int second_broker_port;

    public Brokerinfo(int broker_port, InetAddress addr, ArrayList<String> responsible_broker, int second_broker_port) {
        this.port=broker_port;
        this.add=addr;
        this.broker_topics=responsible_broker;
        this.second_broker_port=second_broker_port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public InetAddress getAdd() {
        return add;
    }

    public void setAdd(InetAddress add) {
        this.add = add;
    }

    public ArrayList<String> getBroker_topics() {
        return broker_topics;
    }

    public void setBroker_topics(ArrayList<String> broker_topics) {
        this.broker_topics = broker_topics;
    }
}
