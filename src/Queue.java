import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;


public class Queue {


    Node  head = null;
    Node  tail  = null;
    String welcome = " welcome you idiots :())()()()()";
    int counter = 0;

    public boolean isEmpty(){
        if (head ==null){
            return true;
        }else{
            return false;
        }
    }

    public Node getHead(){
        return head;
    }

    public void addFirst(String topic, ArrayList<byte[]> value,String type,String username){
        Node  node =new Node (topic,value,type,username);
        counter+=1;
        if (isEmpty()==true){
            head= node;
            tail= node;
        }
        else{
            node.setNext(head);
            head.setPrev(node);
            head = node;

        }
    }

    public ArrayList<byte[]> removeFirst() throws NoSuchElementException{
        if (isEmpty()==true){
            throw new NoSuchElementException();
        }
        ArrayList<byte[]> save = head.getValue();
        counter--;

        if (head==tail){
            head = null;
            tail = null;

        }
        else {
            head = head.getNext();
            head.setPrev(null);
        }
        return save;
    }


    public void addLast(String topic, ArrayList<byte[]> value,String type, String username){
        Node node = new Node (topic,value,type,username);
        counter +=1;

        if ( isEmpty()==true){
            head =node;
            tail=node;
        }
        else{
            tail.setNext(node);
            node.setPrev(tail);
            tail=node;

        }
    }

    public  ArrayList<byte[]> removeLast() throws NoSuchElementException{
        if (isEmpty()==true){
            throw new NoSuchElementException();
        }

        ArrayList<byte[]> save = tail.value;

        counter--;

        if (head==tail){
            head=null;
            tail=null;
        }
        else{
            tail =tail.getPrev();
            tail.setNext(null);
        }
        return save;
    }


    public  ArrayList<byte[]> getFirst(){
        if (isEmpty()== true){
            throw new NoSuchElementException();
        }
        else{
            return head.getValue();
        }

    }

    public String  getFirst2(){
        if (isEmpty()== true){
            throw new NoSuchElementException();
        }
        else{
            return head.getTopic();
        }

    }

    public  ArrayList<byte[]> getLast(){
        if (isEmpty()== true){
            throw new NoSuchElementException();
        }
        else{
            return tail.getValue();
        }
    }

    public void printQueue(PrintStream stream){
        if(isEmpty()==true){
            stream.println("null");
        }
        else{
            for(Node  t=head; t!=null; t=t.getNext()){
                stream.println("stoixeio "+t.getTopic());

            }
        }
    }
    public int size(){
        return counter;
    }

    public ArrayList<Node> Relatedmessages ( String topic ) {
        Node first = head;
        ArrayList<Node> list  = new ArrayList<>();

        while (first.getNext() != null){

            if ( first.getTopic().equals(topic)){
                list.add(first);

            }
            first = first.getNext();
        }
        Collections.reverse(list);
        return list;
    }







    public static void main(String[] args) {
        Queue list = new Queue();
        list.getFirst();
    }




}
