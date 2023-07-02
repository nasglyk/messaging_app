import java.io.Serializable;
import java.net.InetAddress;

public class Userinfo implements Serializable{
    int port;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    String username;

    public InetAddress getAdd() {
        return add;
    }

    public void setAdd(InetAddress add) {
        this.add = add;
    }

    InetAddress add;
    String topic;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Userinfo(int port, InetAddress add, String topic, String username) {
        this.port = port;
        this.username=username;
        this.add = add;
        this.topic=topic;
    }

    public int getNumber() {
        return port;
    }

    public void setNumber(int number) {
        this.port = port;
    }

    public InetAddress isFlag() {
        return add;
    }

    public void setFlag(InetAddress add) {
        this.add = add;
    }
}
