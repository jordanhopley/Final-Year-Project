import com.leapmotion.leap.Frame;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server implements Runnable {

    private Socket fromClient;
    private ServerSocket serverSocket;
    private PrintStream send;
    private BufferedReader receive;
    private Thread thread;

    private Handler handler;
    private String mode = "none";
    private boolean isConnected = false;

    private ArrayList<Frame> frames = new ArrayList<>();

    public Server(Handler handler) {

        this.handler = handler;

        thread = new Thread(this);
        thread.start();

    }

    public void kill() {
        try {
            serverSocket.close();
            send.close();
            receive.close();
            fromClient.close();
        } catch (Exception e) {

        }
    }

    public void run() {

        try {

            serverSocket = new ServerSocket(1001);
            System.out.println("Server started on port: " + 1001);

            fromClient = serverSocket.accept();
            receive = new BufferedReader(new InputStreamReader((fromClient.getInputStream())));
            send = new PrintStream(fromClient.getOutputStream());

            String str = receive.readLine();
            System.out.println(str + " has connected");
            send.println("You have connected to " + InetAddress.getLocalHost().getHostAddress());

            DataInputStream dis = new DataInputStream(fromClient.getInputStream());

            send.println("sendframe");
            boolean isConnected = true;
            frames = new ArrayList<>();
            while (isConnected) {
                int length = dis.readInt();
                if (length > 0) {
                    byte[] serializedFrames = new byte[length];
                    dis.readFully(serializedFrames, 0, serializedFrames.length);
                    Frame frame = new Frame();
                    frame.deserialize(serializedFrames);
                    handler.indicator(frame, handler.getWindow().getGui().getLeftHandLabelTwo(), handler.getWindow().getGui().getRightHandLabelTwo());
                    if (handler.isRecording()) {
                        handler.getFramesFromTwo().add(frame);
                    }
                }
            }

            serverSocket.close();
            System.out.println("Server closed");

        }

        catch (Exception e) {

        }

        finally {

            try {
                serverSocket.close();
                send.close();
                receive.close();
                fromClient.close();
            } catch (Exception e) {
                System.out.println("Client connection not closed: " + e);
            }

        }

    }

}
