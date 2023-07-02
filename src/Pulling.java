import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Pulling extends Thread {
    String path;
    InetAddress broker_ip;
    int broker_port;
    Userinfo t;
    int my_port;


    public Pulling(String path, InetAddress broker_ip,int broker_port, Userinfo t, int my_port){
        this.path=path;
        this.broker_ip=broker_ip;
        this.broker_port=broker_port;
        this.t=t;
        this.my_port=my_port;

    }



    public void run() {
        try {
            pull();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void pull() throws IOException, ClassNotFoundException {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        requestSocket = new Socket(broker_ip, broker_port);

        out = new ObjectOutputStream(requestSocket.getOutputStream());
        in = new ObjectInputStream(requestSocket.getInputStream());
        //o client stelnei ta stoixeia toy
        out.writeObject(t);
        //System.out.println("to topic mou einai "+t.getTopic());
        //o client enhmeronei ton broker oti thelei na kanei pull ta palia mnmt
        out.writeInt(2);
        out.flush();
        // arraylist pou dexomaste apo to broker me ta palia minimata
        ArrayList<Node> paliamhn = (ArrayList<Node>) in.readObject() ;
        int i=1000;
        //apothikeysi ton files sto systima
        for(Node item: paliamhn){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            for(byte[] item2: item.getValue()){
                outputStream.write(item2);
            }
            byte[] c= outputStream.toByteArray();
            if (   item.getType().equals("image")){
                FileOutputStream out2 = new FileOutputStream(path +Integer.toString(my_port)+"\\" +Integer.toString(i) +".png");
                out2.write(c);
                out2.close();
                i=i+1;
            }
            if ( item.getType().equals("video")){
                FileOutputStream out2 = new FileOutputStream(path +Integer.toString(my_port)+"\\" +Integer.toString(i) +".mp4");
                out2.write(c);
                out2.close();
                i=i+1;
            }
            if ( item.getType().equals("text")){
                FileOutputStream out2 = new FileOutputStream(path  +Integer.toString(my_port)+"\\" +Integer.toString(i) +".txt");
                out2.write(c);
                out2.close();
                i=i+1;
            }
        }

    }
}
