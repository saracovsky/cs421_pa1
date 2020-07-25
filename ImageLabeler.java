import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Scanner;

public class ImageLabeler{

    public static void main(String[] args) throws Exception{
        //Socket clientSocket=new Socket("127.0.0.1",60000);
        Socket clientSocket = new Socket();
        clientSocket.connect(new InetSocketAddress ("127.0.0.1", 60000),6000);

        DataOutputStream o = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.US_ASCII));

        String userline = "USER bilkentstu\r\n";
        String passLine = "PASS cs421f2019\r\n";
        String iGetLine = "IGET\r\n";

        boolean okstatus;
        int round = 1;

        try{

            o.write(userline.getBytes(StandardCharsets.US_ASCII));
            String response_username = in.readLine();
            System.out.println("SERVER RESPONSE FOR USERNAME: " + response_username);
            o.write(passLine.getBytes(StandardCharsets.US_ASCII));
            String response_pass = in.readLine();
            System.out.println("SERVER RESPONSE FOR PASSWORD: " + response_pass);

            while( round < 5){
               
                o.write(iGetLine.getBytes(StandardCharsets.US_ASCII));
                System.out.println("ROUND " + round); 
                okstatus = true;

                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());

                byte [] msg;
                String msg2 = "";
                byte [] imgsize;
                int imagesize = -99;
                byte[] img;

                for (int j = 1; j < 4; j++){

                    msg = new byte[4];

                    for (int i = 0; i < 4; i++){
                        msg[i] = dis.readByte();
                    }

                    msg2 = new String(msg);
                    System.out.println("CODE: " + msg2);

                    imgsize = new byte[4];
                    for (int i = 1; i < 4; i++){
                        imgsize[i] = dis.readByte();
                    }

                    imgsize[0] = 0;
                    imagesize = ByteBuffer.wrap(imgsize).getInt();
                    System.out.println("IMAGE SIZE: " + imagesize);

                    img = new byte[imagesize];
                    for (int i = 0; i < imagesize; i++){
                        img[i] = dis.readByte();
                    }
                    FileOutputStream fos = new FileOutputStream("received"+j+".jpg");
                    fos.write(img);

                }

                Scanner scn = new Scanner(System.in);

                while (okstatus == true){
                    System.out.println("Give label for image 1:");
                    String lbl1 = scn.nextLine();
                    System.out.println("Give label for image 2:");
                    String lbl2 = scn.nextLine();
                    System.out.println("Give label for image 3:");
                    String lbl3 = scn.nextLine();

                    String labelLine = "ILBL " + lbl1 + "," + lbl2 + "," + lbl3 + "\r\n";
                    o.write(labelLine.getBytes(StandardCharsets.US_ASCII));  
                    String response_labels = in.readLine();
                    System.out.println("SERVER RESPONSE FOR LABELS:" + response_labels);

                    if (response_labels.equals("OK")){
                        okstatus = false;
                        round++;
                    }

                }

            }


            String exitLine = "EXIT\r\n";
            o.write(exitLine.getBytes(StandardCharsets.US_ASCII));  
            String response_exit = in.readLine();
            System.out.println("SERVER RESPONSE FOR EXIT: " + response_exit);
                

        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        catch (Exception e) {
            e.printStackTrace();
        }
        
        clientSocket.close();

    }
}

