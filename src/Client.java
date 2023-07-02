import java.io.*;
import java.net.*;
import java.util.*;

public class Client extends Thread {

    static InetAddress add;
    static InetAddress broker_ip;

    {
        try {
            //add = InetAddress.getByName("27.0.0.1");
            //TODO Client IP
            add=InetAddress.getLocalHost();
            //TODO change broker's IP, the IP of the broker you connect to for the first connection
            broker_ip= InetAddress.getLocalHost();
            //broker_ip= InetAddress.getByName("192.168.56.1");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static Userinfo t ;
    public String res;
    public ArrayList<Brokerinfo> res2;
    public String answer3;
    public String answer;
    public String onoma;
    public int broker_port2;
    public int broker_port3;
    public InetAddress broker_ip2;
    public InetAddress broker_ip3;

    //TODO Client's port
    public int my_port=2300;
    //TODO Change path
    public String path = "C:\\Users\\user\\Desktop\\erg_katanemimena_2" + "\\";
    //TODO Change broker's port for the first connection
    public int broker_port = 1989;



    private static ArrayList<String> brokers_list = new ArrayList<>();
    // first connection between client and broker
    public void first_connection(){
        Socket requestSocket = null;

        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            // first broker that will return us all broker's info
            requestSocket = new Socket(broker_ip,broker_port );

            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            //stelnoume to user info, 0 pou shmainei first connection kai to topic
            out.writeObject(t);
            out.writeInt(0);
            out.writeUTF(answer3);
            out.flush();

            // dexomaste ta stoixeia pou maw stelnei o broker sxetika me to pou tha broume to topic
            res =in.readUTF();

            res2 = (ArrayList<Brokerinfo>) in.readObject();




        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }catch (ClassNotFoundException e){
            //TODO auto-generated catch
            e.printStackTrace();
        }
        finally {
            try {


                    in.close();	out.close();

                    requestSocket.close();




            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void run() {
        //client is pulling each message when it is sent on the topic he connected to
        ActionsForBroker for_pulling= new ActionsForBroker(my_port);
        for_pulling.start();

        Scanner input3 = new Scanner(System.in);
        System.out.println("give username");
        onoma = input3.nextLine();
        System.out.println("give topic");
        answer3 = input3.nextLine();

        // ta stoixeia tou client pou vriskomaste
        t = new Userinfo(my_port,add,answer3,onoma);

        // create folders for messages and stories
        File f1=new File(path + Integer.toString(t.getNumber()));
        File f2=new File(path+"stories" + Integer.toString(t.getNumber()));

        boolean bool = f1.mkdir();
        if(bool){
            System.out.println("Folder is created successfully");
        }else{
            System.out.println("Error Found!");
        }
        boolean bool2 = f2.mkdir();
        if(bool2){
            System.out.println("Stories Folder is created successfully");
        }else{
            System.out.println("Error Found!");
        }

        // first connection between client and broker who returns us all broker's info
        first_connection();
        System.out.println(res);
        System.out.println("first broker's address: "+res2.get(0).getAdd() +" " +" port: "+ res2.get(0).getPort()+ " topics: "+res2.get(0).getBroker_topics());
        System.out.println("second broker's address: "+res2.get(1).getAdd() +" "+" port: "+ res2.get(1).getPort()+ " topics: "+res2.get(1).getBroker_topics());
        System.out.println("third broker's address: "+res2.get(2).getAdd() +" " +" port: "+ res2.get(2).getPort()+ " topics: "+res2.get(2).getBroker_topics());
        // finding the broker port and ip that we need for the wanted topic
        if(res.equals("vrethike allou")){
            for (Brokerinfo item: res2){
                if(item.getBroker_topics().contains(answer3)){
                    broker_port= item.getPort();
                    broker_ip= item.getAdd();
                    System.out.println(broker_port);
                }
            }
        }
        if(res.equals("den vrethike")){
            Scanner input2 = new Scanner(System.in);
            System.out.println("search for another topic");


                for (Brokerinfo item : res2 ){
                    System.out.println(item.getBroker_topics());
                }
                System.out.println("which topic do you want?");
                String answer2 = input2.nextLine();
                t.setTopic(answer2);
                for (Brokerinfo item: res2){
                    if(item.getBroker_topics().contains(answer2)){
                        broker_port= item.getPort();
                        broker_ip= item.getAdd();
                        System.out.println(broker_port);
                    }
                }


        }
        // we store all port and ips because we will need them for the stories part
        if(broker_port==res2.get(0).getPort()){
            broker_port2=res2.get(1).getPort();
            broker_ip2=res2.get(1).getAdd();
            broker_port3=res2.get(2).getPort();
            broker_ip3=res2.get(2).getAdd();
        }
        if(broker_port==res2.get(1).getPort()){
            broker_port2=res2.get(0).getPort();
            broker_ip2=res2.get(0).getAdd();
            broker_port3=res2.get(2).getPort();
            broker_ip3=res2.get(2).getAdd();
        }
        if(broker_port==res2.get(2).getPort()){
            broker_port2=res2.get(1).getPort();
            broker_ip2=res2.get(1).getAdd();
            broker_port3=res2.get(0).getPort();
            broker_ip3=res2.get(0).getAdd();
        }

        try {
                //pulling all messages when first connected
                //pull();
                Pulling p = new Pulling(path, broker_ip, broker_port, t, my_port);
                p.start();
                // pushing messages
                push();

        } catch (IOException /*| ClassNotFoundException*/ e) {
            e.printStackTrace();
        }


    }

    //pulling all messages sent to the topic we just registered
    public void pull() throws IOException, ClassNotFoundException {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        requestSocket = new Socket(broker_ip, broker_port);

        out = new ObjectOutputStream(requestSocket.getOutputStream());
        in = new ObjectInputStream(requestSocket.getInputStream());
        //o client stelnei ta stoixeia toy
        out.writeObject(t);
        System.out.println("to topic mou einai "+t.getTopic());
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

    //sending messages to a broker
    public void push() throws IOException {
        Socket requestSocket = null;

        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        Socket requestSocket2 = null;

        ObjectOutputStream out2 = null;
        ObjectInputStream in2 = null;

        Socket requestSocket3 = null;

        ObjectOutputStream out3 = null;
        ObjectInputStream in3 = null;


        Scanner input = new Scanner(System.in);


        System.out.println("type what you want to continue with. The options are: (image/ text/ video/ upload story/ watch story)");
        while(true) {

            requestSocket = new Socket(broker_ip, broker_port);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());




            String apanthsh = input.nextLine();
            //o client stelnei ta stoixeia tou
            out.writeObject(t);
            //kai sth sinexeia stelnei ti thelei na kanei
            if (apanthsh.equals("upload story")) {
                out.writeInt(3);
            } else if (apanthsh.equals("watch story")) {

                out.writeInt(4);
                out.flush();
            } else if(apanthsh.equals("image")||apanthsh.equals("video")||apanthsh.equals("text")){
                out.writeInt(1);
            }
            Boolean boo = false;
            byte[] bytes = null;
            while (!boo) {

                if (apanthsh.equals("image")) {
                    System.out.println("dose eikona");
                    String eikona = input.nextLine();

                    BufferedInputStream fileInputStream = null;

                    File file = new File(path + "data\\" + eikona + ".png");
                    fileInputStream = new BufferedInputStream(new FileInputStream(file));
                    bytes = new byte[(int) file.length()];
                    fileInputStream.read(bytes);
                    boo = true;

                } else if (apanthsh.equals("text")) {
                    //h to stelnoyme me txt
                    System.out.println("dose to mhnyma");
                    String mhnyma = input.nextLine();

                    bytes = mhnyma.getBytes();


                    boo = true;

                } else if (apanthsh.equals("video")) {
                    System.out.println("dose video");
                    String video = input.nextLine();

                    BufferedInputStream fileInputStream = null;

                    File file = new File(path + "data\\" + video + ".mp4");
                    fileInputStream = new BufferedInputStream(new FileInputStream(file));
                    bytes = new byte[(int) file.length()];
                    fileInputStream.read(bytes);
                    boo = true;
                } else if (apanthsh.equals("upload story")) {
                    // kanei upload story stelnontaw to story kai stous 3 brokers
                    requestSocket2 = new Socket(broker_ip2, broker_port2);
                    out2 = new ObjectOutputStream(requestSocket2.getOutputStream());
                    in2 = new ObjectInputStream(requestSocket2.getInputStream());
                    requestSocket3 = new Socket(broker_ip3, broker_port3);
                    out3 = new ObjectOutputStream(requestSocket3.getOutputStream());
                    in3 = new ObjectInputStream(requestSocket3.getInputStream());

                    System.out.println("dose story (onoma arxeiou + .kataliksi) ");
                    String story = input.nextLine();
                    BufferedInputStream fileInputStream = null;

                    File file = new File(path + "data\\" + story);
                    fileInputStream = new BufferedInputStream(new FileInputStream(file));
                    bytes = new byte[(int) file.length()];
                    fileInputStream.read(bytes);

                    out.writeUTF(story);
                    out.flush();

                    out2.writeObject(t);
                    out3.writeObject(t);
                    out2.writeInt(3);
                    out3.writeInt(3);


                    out2.writeUTF(story);
                    out2.flush();
                    out3.writeUTF(story);
                    out3.flush();
                    boo = true;

                } else if (apanthsh.equals("watch story")) {
                    File dir = new File(path+"stories"+Integer.toString(my_port) );
                    //svinoume ta story pou exoume sto fakelo mas gia na dextoume tou broker pou ua einai mesa sto xroniko orio tis mias imera h oti allo exoume thesei
                    for (File file : Objects.requireNonNull(dir.listFiles())) {

                        file.delete();

                    }

                    boo = true;
                } else {
                    System.out.println("lathos tupos mhnymatos ksanadwse to mhnyma pou thes");
                    System.out.println("Type again (image / text / video/ upload story/ watch story)");
                    apanthsh = input.nextLine();
                    if (apanthsh.equals("upload story")) {
                        out.writeInt(3);
                        out.flush();
                    } else if (apanthsh.equals("watch story")) {

                        out.writeInt(4);
                        out.flush();
                    } else if(apanthsh.equals("image")||apanthsh.equals("video")||apanthsh.equals("text")) {
                        out.writeInt(1);
                        out.flush();
                    }
                }

            }


            if (!apanthsh.equals("watch story")) {
                System.out.println("To synoliko length einai: "+bytes.length);
                ArrayList<byte[]> chunksList = chunker(bytes); // create a list with the song in pieces
                int numberOfChunks = chunksList.size(); // number of chunks to return
                out.writeUTF(apanthsh);
                out.writeInt(numberOfChunks);

                out.flush();
                for (int i = 0; i < numberOfChunks; i++) { // send each chunk separately
                    System.out.println("Sending chunk number " + (i + 1));
                    System.out.println("to length kathe chunk einai: " + chunksList.get(i).length);
                    out.writeObject(chunksList.get(i));
                    out.flush();
                }

                if ((apanthsh.equals("upload story"))) {
                    out2.writeUTF(apanthsh);
                    out2.writeInt(numberOfChunks);
                    out3.writeUTF(apanthsh);
                    out3.writeInt(numberOfChunks);
                    out2.flush();
                    out3.flush();

                    for (int i = 0; i < numberOfChunks; i++) { // send each chunk separately

                        out2.writeObject(chunksList.get(i));
                        out2.flush();
                    }
                    for (int i = 0; i < numberOfChunks; i++) { // send each chunk separately

                        out3.writeObject(chunksList.get(i));
                        out3.flush();
                    }
                }

                out.flush();
            }
        }

    }
    // synartish pou dhmioyrgei ta chunks
    public static ArrayList<byte[]> chunker(byte[] bytearray) {
        int blockSize = 512 * 1024;
        ArrayList<byte[]> list = new ArrayList<>();
        //ypologizoume to plithos ton blocks pou tha dimiourgisoume
        int blockCount = (bytearray.length + blockSize - 1) / blockSize;
        byte[] range = null;
        int end = -1;
        for (int i = 1; i < blockCount; i++) {
            int index = (i - 1) * blockSize;
            range = Arrays.copyOfRange(bytearray, index, index + blockSize);
            list.add(range);
        }
        //ektash
        if (bytearray.length % blockSize == 0) {
            end = bytearray.length;
        } else {
            end = bytearray.length % blockSize + blockSize * (blockCount - 1);
        }

        range = Arrays.copyOfRange(bytearray, (blockCount - 1) * blockSize, end);
        list.add(range);
        return list;
    }

    public static void main(String args[]) throws IOException {


        new Client().start();
        //TODO don't forget to change tha path
        brokers_list = initBrokers("C:\\Users\\user\\Desktop\\erg_katanemimena_2" + "\\data\\brokers.txt");
    }

    static ArrayList<String> initBrokers(String path) throws IOException {
        File file = new File(path);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String st = br.readLine();
        String[] info = st.split(" ");
        ArrayList <String> brokers = new ArrayList<>();
        for (int i = 0; i < info.length; i++){
            brokers.add(info[i]);
        }
        br.close();
        fr.close();
        return brokers;
    }



}

