import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Listener;

import java.io.IOException;

public class LeapListener extends Listener {

    private Client client;

    public LeapListener(Client client) {

        this.client = client;

        Controller controller = new Controller();
        controller.addListener(this);
        //controller.setPolicy(Controller.PolicyFlag.POLICY_OPTIMIZE_HMD);

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        controller.removeListener(this);

    }

    @Override
    public void onFrame(Controller controller) {

        Frame frame = controller.frame();

        if (client.isRecording()) {
            try { client.sendFrame(frame); } catch (Exception e) { }
        }

    }

}
