import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class ActionsForClients extends Thread {
    ObjectInputStream in;
    ObjectOutputStream out;
    private static ArrayList<String> topics_list2 = new ArrayList<>();
    private static ArrayList<String> responsible_broker2 = new ArrayList<>();
    private static ArrayList<Brokerinfo> my_info;
    private static ArrayList<byte[]> oldmessages ;
    private static ArrayList<Node> old=new ArrayList<>();
    Userinfo us ;
    Queue broker_queue ;//= new Queue();
    private static ArrayList<Userinfo> userinfo  ;
    String typosarxeiou;
    boolean saveus=false;
    boolean saveus2=false;
    int my_port;
    Userinfo info;
    ServerSocket providerSocket2;



    //TODO Change path
    public static String path = "C:\\Users\\user\\Desktop\\erg_katanemimena_2" + "\\";



    public ActionsForClients(Socket connection, ArrayList<String> topics_list,ArrayList<String> responsible_broker,ArrayList<Brokerinfo> myinfo, Queue broker_queue,ArrayList<Userinfo> userinfo,int my_port, ServerSocket providerSocket2) {
        try {
            this.providerSocket2=providerSocket2;
            my_info=myinfo;
            this.my_port=my_port;
            topics_list2 = topics_list;
            responsible_broker2 =responsible_broker;
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
            this.broker_queue = broker_queue;
            this.userinfo = userinfo;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Userinfo ret_user(){
        return us;
    }

    boolean bool= false;
    boolean bool2=false;
    ArrayList<byte[]> chunksList= new ArrayList<>();

    public void run() {
        try {



            chunksList=new ArrayList<>();
            bool= false;
            bool2=false;
            // info: antikeimeno klashs Userinfo to opoio periexei ta stoixeia enos client pou epikoinonoume
            //info = (Userinfo) in.readObject();
            int port = in.readInt();
            InetAddress address = (InetAddress) in.readObject();
            String topicsent = in.readUTF();
            String namesender= in.readUTF();

            info= new Userinfo(port,address, topicsent, namesender);

            // dexomaste enan Int pou prosdiorizei ti eidous doyleia prepei na kanei o broker ( message = {0,1,2,3,4} )
            int message =in.readInt();



            // first connection request from a client
            if (message == 0){
                System.out.println("first connection of the client: "+info.getUsername());
                // dexomaste to topic pou psaxnei o client
                String message2 = in.readUTF();
                //epistrefoume thn apantisi
                String top = "";
                for (Brokerinfo inf: my_info){
                    for (String topiccs: inf.getBroker_topics()){
                        top = top+ " , " +  topiccs;
                    }
                }
                out.writeUTF(top );

                // elegxoume an exoume ayto to topic h anhkei se allon broker h den yparxei kai apantame katallhla
                for(String item: responsible_broker2 ){
                    if (message2.equals(item)) {
                        us = info;
                        bool=true;
                        out.writeUTF("vrethike");
                        out.writeInt(my_port);
                        for ( Brokerinfo br : my_info){
                            if(br.getPort()==my_port){
                                out.writeObject(br.getAdd());
                                out.writeInt(br.getSecond_broker_port());
                            }
                        }


                        synchronized (userinfo) {
                            userinfo.add(info);
                        }
                    }
                }
                if(bool==false){
                    for (String topic: topics_list2){
                        if (message2.equals(topic)) {
                            bool2=true;
                            out.writeUTF("vrethike allou");
                            System.out.println("vrethike allouuuuuuuuuuuuuu");
                            for ( Brokerinfo br : my_info){
                                if(br.getBroker_topics().contains(message2)){
                                    out.writeInt(br.getPort());

                                    out.writeObject(br.getAdd());
                                    out.writeInt(br.getSecond_broker_port());
                                }
                            }
                        }
                    }
                    if(bool2==false){
                        out.writeUTF("den vrethike");
                    }
                }

                out.flush();

            }else if (message==2) {


                saveus2=false;
                // adding client to registered clients list
                if (userinfo.isEmpty()){
                    synchronized (userinfo) {
                        userinfo.add(info);
                    }
                }
                for (Userinfo user : userinfo){
                    if (((user.getTopic().equals(info.getTopic()))  && ( user.getNumber()==info.getNumber()))){

                        saveus2=true;

                    }
                }
                if (saveus2==false){
                    userinfo.add(info);
                }

                System.out.println("sending all previous messages to the client: "+ info.getUsername());
                // we send all received messages to a client that just connected
                 old = broker_queue.Relatedmessages(info.getTopic());
                //out.writeObject(old);
                //ArrayList<byte[]> chunksList2= new ArrayList<>();
                out.writeInt(old.size());
                for ( Node n: old){
                    out.writeUTF(n.getType());
                    out.writeUTF(n.getUsername());
                    out.writeInt(n.getValue().size());
                    for (byte[] b:n.getValue()){
                        out.writeObject(b);
                    }
                }


                out.flush();


            //we save a story file
            }else if(message==3) {

                System.out.println("saving the story file that client "+ info.getUsername()+" sent");
                //dexomaste apo ton client to onoma tou arxeiou, ton typo kai to plithos ton chunks
                String onomastory = in.readUTF();
                typosarxeiou = in.readUTF();
                int chunks = in.readInt();

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
                System.out.println("to plithos ton chunks einai: "+chunks);
                for(int i = 0; i < chunks; i++) {
                    System.out.println("receiving chunk number " + i);

                    outputStream.write((byte[])in.readObject());


                }
                byte[] c= outputStream.toByteArray();
                FileOutputStream out = new FileOutputStream( path+"stories"+Integer.toString(my_port) +"\\"+ onomastory);
                out.write(c);
                out.close();
            }
            else if (message==4){
                // sending back stories


                System.out.println("sending all available stories to the client: "+info.getUsername());
                byte[] bytes = null;
                ArrayList<byte[]> chunksList= new ArrayList<>();


                File dir = new File(path+"stories"+Integer.toString(my_port) );
                //deleting files that passed the time due and sending the rest back to the client that asked to watch the stories
                for (File file : dir.listFiles()) {


                    long diff = new Date().getTime() - file.lastModified();

                    //TODO story poy menoun gia 30 deyterolepta
                    if (diff >  /*24 * 60*/   30 * 1000) {
                        out.flush();
                        //System.out.println(file.getName());
                        file.delete();
                        out.flush();

                    }
                    else{
                        BufferedInputStream fileInputStream = null;
                        fileInputStream = new BufferedInputStream(new FileInputStream(file));
                        bytes = new byte[(int) file.length()];
                        fileInputStream.read(bytes);
                        chunksList = chunker(bytes);
                        //fileInputStream.close();
                        int numberOfChunks = chunksList.size(); // number of chunks to return
                        typosarxeiou="story";
                        //sending story
                        pull2(chunksList,"story",file.getName());
                        fileInputStream.close();


                    }
                }

            }
            else {
                System.out.println("receiving and sending files");
                saveus=false;
                //saving client to registered clients
                if (userinfo.isEmpty()){
                    synchronized (userinfo) {
                        userinfo.add(info);
                    }
                }
                for (Userinfo user : userinfo){
                    if (((user.getTopic().equals(info.getTopic()))  && ( user.getNumber()==info.getNumber()))){

                         saveus=true;

                    }
                }
                if (saveus==false){
                    userinfo.add(info);
                }







                typosarxeiou = in.readUTF();
                int chunks = in.readInt();


                System.out.println("to plithos ton chunks einai: "+chunks);
                for(int i = 0; i < chunks; i++) {
                    System.out.println("receiving chunk number " + i);
                    byte[] newChunk = (byte[]) in.readObject();
                    chunksList.add(newChunk);


                }

                //sending the received message back to clients
                //pull2(chunksList,info.getTopic(),info.getUsername());
                sendback(chunksList,info.getTopic(),info.getUsername());
                //saving messages to a queue
                synchronized (broker_queue) {
                    broker_queue.addFirst(info.getTopic(), chunksList, typosarxeiou,info.getUsername());
                    //broker_queue.printQueue(System.out);
                }
                System.out.println("registered users on this broker");
                for (Userinfo in : userinfo){
                    System.out.println("user "+in.getUsername()+" me port: " +in.getNumber() +",ip: "+in.getAdd()+" kai topic: "+in.getTopic());

                }


            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e){
            //TODO auto-generated catch
            e.printStackTrace();
        }
        finally {
            try {
                in.close();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    //sending a file
    public void pull2(ArrayList<byte[]> chunksList,String topic,String sender){

        Socket requestSocket2 = null;
        Socket connection2=null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {

            // stelnoume pisw stous clients pou einai sindedemenoi sto topic
            if(!topic.equals("story")) {
                for (Userinfo user : userinfo) {

                    if (user.getTopic().equals(topic)) {
                        System.out.println(user.getAdd());
                        System.out.println(user.getNumber());
                        requestSocket2 = new Socket(user.getAdd(),user.getNumber());
                        //connection2 = requestSocket2.accept();
                        System.out.println("bike bike bike");
                        //InetAddress dokimi = requestSocket2.getInetAddress();
                        //System.out.println(dokimi.toString());
                        out = new ObjectOutputStream(requestSocket2.getOutputStream());
                        in = new ObjectInputStream(requestSocket2.getInputStream());
                        out.writeUTF(typosarxeiou);
                        out.writeInt(chunksList.size());
                        out.flush();
                        for (int i = 0; i < chunksList.size(); i++) {
                            out.writeObject(chunksList.get(i));
                        }

                        out.flush();
                        out.writeUTF(sender);
                        out.flush();
                    }
                }
                out.close();
                in.close();
            }/*else{
                //stelnoume to arxeio ston client pou zitise na dei to story
                requestSocket2 = new Socket(info.getAdd(), info.getNumber());
                out = new ObjectOutputStream(requestSocket2.getOutputStream());
                in = new ObjectInputStream(requestSocket2.getInputStream());
                out.writeUTF(typosarxeiou);
                out.writeInt(chunksList.size());
                out.flush();
                for (int i = 0; i < chunksList.size(); i++) {
                    out.writeObject(chunksList.get(i));
                }

                out.flush();
                out.writeUTF(sender);
                out.flush();
                out.close();
                in.close();
            }*/






        } catch (IOException e) {
            e.printStackTrace();

        }


    }
    // sinartisi pou dimioyrgei ta chunks
    public static ArrayList<byte[]> chunker(byte[] bytearray) {
        int blockSize = 512 * 1024;
        ArrayList<byte[]> list = new ArrayList<>();
        // ypologizoume to plithos ton blocks
        int blockCount = (bytearray.length + blockSize - 1) / blockSize;
        byte[] range = null;
        int end = -1;
        for (int i = 1; i < blockCount; i++) {
            int index = (i - 1) * blockSize;
            range = Arrays.copyOfRange(bytearray, index, index + blockSize);
            list.add(range);
        }
        //ypologizoyme thn ektash
        if (bytearray.length % blockSize == 0) {
            end = bytearray.length;
        } else {
            end = bytearray.length % blockSize + blockSize * (blockCount - 1);
        }

        range = Arrays.copyOfRange(bytearray, (blockCount - 1) * blockSize, end);
        list.add(range);
        return list;
    }


    public void sendback(ArrayList<byte[]> chunksList,String topic,String sender){

        Socket requestSocket2 = null;
        Socket connection2=null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {

            // stelnoume pisw stous clients pou einai sindedemenoi sto topic
            if(!topic.equals("story")) {
                for (Userinfo user : userinfo) {

                    if (user.getTopic().equals(topic) /*&& !user.getUsername().equals(sender)*/) {
                        System.out.println("o user einai "+ user.getUsername());
                        System.out.println("sender is " +sender);

                        Socket connection5 = providerSocket2.accept();
                        synchronized (connection5){
                            //connection2 = requestSocket2.accept();
                            System.out.println("bike bike bike");
                            //InetAddress dokimi = requestSocket2.getInetAddress();
                            //System.out.println(dokimi.toString());
                            out = new ObjectOutputStream(connection5.getOutputStream());
                            in = new ObjectInputStream(connection5.getInputStream());
                            out.writeUTF(sender);
                            out.flush();
                            out.writeUTF(typosarxeiou);
                            out.writeInt(chunksList.size());
                            out.flush();
                            for (int i = 0; i < chunksList.size(); i++) {
                                out.writeObject(chunksList.get(i));
                            }

                            out.flush();


                            out.close();
                            in.close();
                        }



                        connection5.close();
                    }
                }

            }/*else{
                //stelnoume to arxeio ston client pou zitise na dei to story
                requestSocket2 = new Socket(info.getAdd(), info.getNumber());
                out = new ObjectOutputStream(requestSocket2.getOutputStream());
                in = new ObjectInputStream(requestSocket2.getInputStream());
                out.writeUTF(typosarxeiou);
                out.writeInt(chunksList.size());
                out.flush();
                for (int i = 0; i < chunksList.size(); i++) {
                    out.writeObject(chunksList.get(i));
                }

                out.flush();
                out.writeUTF(sender);
                out.flush();
                out.close();
                in.close();
            }*/






        } catch (IOException e) {
            e.printStackTrace();

        }


    }




}
