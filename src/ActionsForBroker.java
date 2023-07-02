import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class ActionsForBroker  extends Thread {
    ObjectInputStream in;
    ObjectOutputStream out;
    ServerSocket providerSocket;
    Socket connection= null;
    int port;
    int i=1;

    //TODO change path
    public String path = "C:\\Users\\user\\Desktop\\erg_katanemimena_2"+"\\" ;


    public ActionsForBroker(int my_port){
        try {
            port=my_port;
            providerSocket = new ServerSocket(my_port, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {
        //waiting for messages from broker
        while (true) {

            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
                connection = providerSocket.accept();

                out = new ObjectOutputStream(connection.getOutputStream());
                in = new ObjectInputStream(connection.getInputStream());
                String typosarxeiou=in.readUTF();

                int size = in.readInt();

                for (int i=0; i<size ; i++){
                   outputStream.write((byte[])in.readObject());
                }

                String sender = in.readUTF();
                //System.out.println("The message was sent by: "+sender);

                byte[] c= outputStream.toByteArray();
                //System.out.println(c.length);

                if ( typosarxeiou.equals("image")){
                    FileOutputStream out = new FileOutputStream(path +Integer.toString(port)+"\\" +Integer.toString(i) +".png");
                    out.write(c);
                    out.close();
                    System.out.println(sender +": sent an image");
                    i=i+1;
                }
                if ( typosarxeiou.equals("video")){
                    FileOutputStream out = new FileOutputStream(path +Integer.toString(port)+"\\" +Integer.toString(i) +".mp4");
                    out.write(c);
                    out.close();
                    System.out.println(sender +": sent a video");
                    i=i+1;
                }
                if( typosarxeiou.equals("text")){
                    FileOutputStream out = new FileOutputStream(path +Integer.toString(port)+"\\" +Integer.toString(i) +".txt");
                    out.write(c);
                   out.close();
                    System.out.println(sender +": "+new String(c, StandardCharsets.UTF_8));
                    i=i+1;
                }
                if ( typosarxeiou.equals("story")){
                    FileOutputStream out = new FileOutputStream(path +"stories"+Integer.toString(port)+"\\" +sender);
                    out.write(c);
                    out.close();
                    System.out.println("story: " +sender +" was watched");
                    i=i+1;
                }


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }





    }
}
