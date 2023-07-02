import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Node implements Serializable{
    String topic;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    String username;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    ArrayList<byte[]> value;
    String type;
    Node next;
    Node prev;
    Node(String topic,ArrayList<byte[]> value , String type, String username){
        this.topic=topic;
        this.value=value;
        this.type=type;
        this.username=username;
        next = null;
        prev = null;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public ArrayList<byte[]> getValue() {
        return value;
    }

    public void setValue(ArrayList<byte[]> value) {
        this.value = value;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }
}
