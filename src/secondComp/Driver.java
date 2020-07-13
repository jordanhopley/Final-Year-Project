public class Driver {

    public static void main(String[] args) {

        String ip = args[0];

        new LeapListener(new Client(ip));

    }

}
