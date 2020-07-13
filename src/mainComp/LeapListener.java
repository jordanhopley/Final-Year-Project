import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Listener;
import java.io.IOException;

public class LeapListener extends Listener {

    private Handler handler;

    public LeapListener(Handler handler) {

        this.handler = handler;

        handler.getWindow().revalidate();

        Controller controller = new Controller();
        controller.addListener(this);

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
        handler.indicator(frame, handler.getWindow().getGui().getLeftHandLabelOne(), handler.getWindow().getGui().getRightHandLabelOne());



        if (handler.isRecording()) {
            handler.getFramesFromOne().add(frame);
        }

    }

}
