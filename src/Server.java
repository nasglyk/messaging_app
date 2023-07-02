import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Server {
    ServerSocket providerSocket;
    ServerSocket providerSocket2;
    Socket connection = null;
    private static ArrayList<String> brokers_list = new ArrayList<>();
    private static ArrayList<String> topics_list = new ArrayList<>();
    private static ArrayList<Integer> broker_hash = new ArrayList<>();
    private static int broker_port= 1989;
    private static int sec_broker_port= 3000;
    private static InetAddress addr;
    private static String welcome = " welcome ";
    private static byte[] bytes = welcome.getBytes();
    private static ArrayList<byte[]> help = new ArrayList<>();


    //TODO Change path in the lab
    public static String path = "C:\\Users\\user\\Desktop\\erg_katanemimena_2"+"\\";


    static {
        try {
            //TODO change IP, don't forget to change the IPs in the file
            //ksanaddeeeeeeeeeeeees
            addr = InetAddress.getByName("192.168.1.214");
            //addr=InetAddress.getLocalHost();
            System.out.println(addr);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> responsible_broker1 = new ArrayList<>();
    private static ArrayList<String> responsible_broker2 = new ArrayList<>();
    private static ArrayList<String> responsible_broker3 = new ArrayList<>();
    private static ArrayList<Userinfo> userinfo  = new ArrayList<>();
    private static ArrayList<String> responsible_broker = new ArrayList<>();
    //oi plirofories toy broker pou vriskomaste tora
    private static Brokerinfo my_info = new Brokerinfo(broker_port,addr,responsible_broker, sec_broker_port );
    public static ArrayList<Brokerinfo> allbrokers= new ArrayList<>();
    public static ArrayList<Brokerinfo> test ;
    public static Brokerinfo bi1;
    public static Brokerinfo bi2;
    public static Brokerinfo bi3;
    public static  Queue broker_queue = new Queue();

    //makes a list that contains broker's information
    static ArrayList<Brokerinfo> brokerlist (ArrayList<String> brokers , ArrayList<String> res1, ArrayList<String> res2 , ArrayList<String> res3) throws UnknownHostException {
        test = new ArrayList<>();
         bi1 = new Brokerinfo(Integer.parseInt(brokers.get(1)),InetAddress.getByName(brokers.get(0)),res1,3000);
         bi2 = new Brokerinfo((Integer.parseInt(brokers.get(3))),InetAddress.getByName(brokers.get(2)),res2,3100);
         bi3 = new Brokerinfo((Integer.parseInt(brokers.get(5))),InetAddress.getByName(brokers.get(4)),res3,3200);
        test.add(bi1);
        test.add(bi2);
        test.add(bi3);

        return test;
    }


    public static void main(String args[]) throws IOException {
        System.out.println("Initializing Brokers");
        //TODO change path
        brokers_list = initBrokers("C:\\Users\\user\\Desktop\\erg_katanemimena_2" + "\\data\\brokers.txt");
        System.out.println(brokers_list);
        //TODO change path
        topics_list = initTopics("C:\\Users\\user\\Desktop\\erg_katanemimena_2" + "\\data\\topics.txt");
        System.out.println(topics_list);
        broker_hash = hash(topics_list);
        System.out.println(broker_hash);



        //TODO Change port

        broker_port=1312;

        //TODO Change broker's second port : 1989-3000, 1312-3100, 1990-3200

        sec_broker_port = 3100;

        // creating a folder for broker's stories
        File f1=new File(path +"stories"+ broker_port);

        boolean bool = f1.mkdir();
        if(bool){
            System.out.println("Stories Folder is created successfully");
        }else{
            System.out.println("Error Found!");
        }




        responsible_broker1 = responsibilities(brokers_list,topics_list,broker_hash,1989);
        System.out.println("o prwtos broker einai ipeythinos gia " + responsible_broker1);
        responsible_broker2 = responsibilities(brokers_list,topics_list,broker_hash,1312);
        System.out.println("o deyteros broker einai ipeythinos gia " + responsible_broker2);
        responsible_broker3 = responsibilities(brokers_list,topics_list,broker_hash,1990);
        System.out.println("o tritos broker einai ipeythinos gia " + responsible_broker3);
        responsible_broker = responsibilities(brokers_list,topics_list,broker_hash,broker_port);
        my_info.setBroker_topics(responsible_broker);
        allbrokers= brokerlist(brokers_list,responsible_broker1,responsible_broker2,responsible_broker3);




        
        help.add(bytes);
        broker_queue.addFirst("dokimastiko",help,"text","hxristinapseftra");





        new Server().openServer();
        ;
    //prosthese isws kalytera try catch
    }

    // reading topics from file
    static ArrayList<String> initTopics(String path) throws IOException {
        File file = new File(path);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String st = br.readLine();
        String[] info = st.split(" ");
        ArrayList <String> topics = new ArrayList<>();
        for (int i = 0; i < info.length; i++){
            topics.add(info[i]);
        }
        br.close();
        fr.close();
        return topics;
    }

    //reading brokers from file
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

    boolean bool = false;




    void openServer() {
        try {
            System.out.println("broker port " + broker_port);
            providerSocket = new ServerSocket(broker_port, 10);
            providerSocket2 = new ServerSocket(sec_broker_port, 10);
            //providerSocket2 = new ServerSocket(broker_port, 10);
            while (true) {

                connection = providerSocket.accept();


                ActionsForClients t = new ActionsForClients(connection,topics_list,responsible_broker,allbrokers,broker_queue,userinfo,broker_port,providerSocket2);

                t.start();




                Userinfo newuser = t.ret_user();
                if (newuser!=null) {
                    System.out.println(newuser.getNumber());
                }
                if (newuser!=null){
                    userinfo.add(newuser);
                }

            }
        } catch (IOException ioException){ // | InterruptedException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    // SHA-1
    public static BigInteger encryptThisString(String input)
    {
        try {
            // getInstance() method is called with algorithm SHA-1
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            return no;


        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    //hashing function
    public static ArrayList<Integer> hash  (ArrayList<String> list){
        ArrayList <BigInteger> hashed = new ArrayList<>();
        for (String item : list){
            hashed.add(encryptThisString(item));
        }
        BigInteger d = new BigInteger ("3");
        ArrayList <Integer> broker_num = new ArrayList<>();
        for(BigInteger item: hashed){
            BigInteger a=item;

            a= a.mod(d);



            broker_num.add(a.intValue());
        }
        return broker_num;

    }
//TODO change ports
    public static ArrayList<String> responsibilities(ArrayList<String> brokers_list,ArrayList<String> topics_list,ArrayList<Integer> broker_hash, int port ){
        ArrayList <String> lista = new ArrayList<>();
        if (port==1989){
            for(int i=0;i<=topics_list.size()-1;i++){
                if (broker_hash.get(i)==0) {
                    lista.add(topics_list.get(i));
                }
            }

        }

        if (port==1312){
            for(int i=0;i<=topics_list.size()-1;i++){
                if (broker_hash.get(i)==1) {
                    lista.add(topics_list.get(i));
                }
            }

        }

        if (port==1990){
            for(int i=0;i<=topics_list.size()-1;i++){
                if (broker_hash.get(i)==2) {
                    lista.add(topics_list.get(i));
                }
            }

        }

        return lista;

    }
}
