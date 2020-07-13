import com.leapmotion.leap.Frame;
import java.io.*;
import java.net.Socket;

public class Client implements Runnable {

    private String ip;

    private PrintStream printStream;
    private BufferedReader bufferedReader;
    private Socket socket;

    private DataOutputStream dos;

    private boolean recording = false;


    public Client(String ip) {

        this.ip = ip;

        Thread thread = new Thread(this);
        thread.start();

    }

    public void run() {

        try {

            //socket = new Socket("10.32.150.45", 1001);
            //socket = new Socket("192.168.0.110", 1001);
            socket = new Socket(ip, 1001);

            bufferedReader = new BufferedReader(new InputStreamReader((socket.getInputStream())));
            printStream = new PrintStream(socket.getOutputStream());
            String name = "Laptop 2";
            printStream.println(name);
            String str = bufferedReader.readLine();
            System.out.println(str);

            dos = new DataOutputStream(socket.getOutputStream());
            String mode = bufferedReader.readLine();
            while (mode.equals("sendframe")) {
                recording = true;
            }
            recording = false;

            dos.close();
            socket.close();
            System.exit(0);

        }

        catch (Exception e) {

            try {
                dos.close();
                socket.close();
                System.exit(0);
            } catch (Exception ee) {
            }

        }

    }

    public void sendFrame(Frame frame) throws Exception {
        byte[] serializedFrame = frame.serialize();
        dos.writeInt(serializedFrame.length);
        dos.write(serializedFrame);
        System.out.println("Frame sent: " + frame);
    }

    public boolean isRecording() {
        return recording;
    }

}