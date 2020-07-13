import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class Handler implements Runnable {

    private Window window;
    private GUI gui;
    private Server server;

    private boolean serverStarted = false;
    private boolean recording = false;
    private boolean creatingNewGesture = false;
    private int count = 3;
    private String name;
    private String gesturetoTest;
    private HashMap<String, ArrayList<CustomVector>> nameThenPremadeGestures = new HashMap<>();
    private ArrayList<ArrayList<CustomVector>> premadeGestures = new ArrayList<>();

    private String positionOfOne;
    private String positionOfTwo;

    private ArrayList<Frame> framesFromOne = new ArrayList<>();
    private ArrayList<Frame> framesFromTwo = new ArrayList<>();

    private String winningLeft;
    private String winningRight;
    private String config;

    private ArrayList<CustomVector> finalGesture = new ArrayList<>();

    public Handler() {

        window = new Window();
        gui = window.getGui();
        gui.setHandler(this);

        init();

    }

    @Override
    public void run() {

        framesFromOne = new ArrayList<>();
        framesFromTwo = new ArrayList<>();

        while (count > 0) {
            gui.getInstructions().setText(Integer.toString(count));
            count--;
            try { Thread.sleep(1000); } catch (InterruptedException e) { }
        }

        gui.getInstructions().setText("Reading frames...");
        gui.getInstructions().setBackground(new Color(255, 74, 70));
        recording = true;
        try { Thread.sleep(3000); } catch (InterruptedException e) { }
        gui.getInstructions().setText("Finished!");
        gui.getInstructions().setBackground(null);
        recording = false;
        //try { Thread.sleep(3000);} catch (InterruptedException e) { }
        gui.getInstructions().setText("");
        gui.getCardLayout().show(gui.getActionArea(), "ep");
        gui.getBeginTestingButton().setText("Begin Testing");
        count = 3;
        processFrames(framesFromOne, framesFromTwo);


    }

    public void processFrames(ArrayList<Frame> framesFromOne, ArrayList<Frame> framesFromTwo) {

        String positionOne = positionOfOne;
        String positionTwo = positionOfTwo;

        // 1 Sensor
        if (positionTwo == null) {
            GestureCreator gestureCreator = new GestureCreator(framesFromOne, positionOne);
            finalGesture = gestureCreator.getFinalLeftOne();
            finalGesture.addAll(gestureCreator.getFinalRightOne());
            ArrayList<CustomVector> gesture = finalGesture;
            if (creatingNewGesture) {
                writeToFile(gesture, name.toUpperCase());
                addToLog("Gesture: " + name.toUpperCase() + " added.");
                gui.getDlm().addElement(name.toUpperCase());
                creatingNewGesture = false;
            } else {
                GestureCalculator gestureCalculator = new GestureCalculator(nameThenPremadeGestures, gesture);
                processResults(gestureCalculator, "SingleSensor"); // RESULTS
            }
        }

        // 2 Sensors
        else {

            // Data Level
            GestureCreator gestureCreator = new GestureCreator(framesFromOne, framesFromTwo, positionOne, positionTwo);
            ArrayList<CustomVector> leftOne = gestureCreator.getFinalLeftOne();
            ArrayList<CustomVector> rightOne = gestureCreator.getFinalRightOne();
            ArrayList<CustomVector> leftTwo = gestureCreator.getFinalLeftTwo();
            ArrayList<CustomVector> rightTwo = gestureCreator.getFinalRightTwo();

            Fusion fusion = new Fusion(leftOne, rightOne, leftTwo, rightTwo);
            ArrayList<CustomVector> lowLevelFused = fusion.getLowLevelFused();

            // Process Data Level Results
            System.out.println("========================= LOW LEVEL ========================= ");
            GestureCalculator gestureCalculator = new GestureCalculator(nameThenPremadeGestures, lowLevelFused);
            processResults(gestureCalculator, "DataLevel"); // RESULTS

            // Feature Level
            System.out.println("========================= FEATURE LEVEL ========================= ");
            ArrayList<Hand> winningHands = fusion.featureLevel(framesFromOne, framesFromTwo);
            String winningL = fusion.getWinningLaptopForLeftHand();
            String winningR = fusion.getWinningLaptopForRightHand();
            String position1 = "";
            String position2 = "";
            if (winningL.equalsIgnoreCase("Left")) {
                position1 = positionOfOne;
            } else if (winningL.equalsIgnoreCase("Right")) {
                position1 = positionOfTwo;
            }

            if (winningR.equalsIgnoreCase("Right")) {
                position2 = positionOfTwo;
            } else if (winningR.equalsIgnoreCase("Left")){
                position2 = positionOfOne;
            }
            Hand leftHand = winningHands.get(0);
            Hand rightHand = winningHands.get(1);

            GestureCreator gestureCreator2 = new GestureCreator(leftHand, rightHand, position1, position2);
            ArrayList<CustomVector> featureLevelFused = gestureCreator2.getRotatedLeftHand();
            featureLevelFused.addAll(gestureCreator2.getRotatedRightHand());
            GestureCalculator gestureCalculator2 = new GestureCalculator(nameThenPremadeGestures, featureLevelFused);
            processResults(gestureCalculator2, "FeatureLevel"); // RESULTS
            winningLeft = fusion.getWinningLaptopForLeftHand();
            winningRight = fusion.getWinningLaptopForRightHand();
            System.out.println(fusion.getWinningLaptopForLeftHand() + " " + fusion.getWinningLaptopForRightHand());

            // Decision Level
            System.out.println("========================= DECISION LEVEL ========================= ");
            GestureCreator gestureCreator3 = new GestureCreator(framesFromOne, positionOne);
            finalGesture = gestureCreator3.getFinalLeftOne();
            finalGesture.addAll(gestureCreator3.getFinalRightOne());
            ArrayList<CustomVector> gesture1 = finalGesture;
            GestureCreator gestureCreator4 = new GestureCreator(framesFromTwo, positionTwo);
            finalGesture = gestureCreator4.getFinalLeftOne();
            finalGesture.addAll(gestureCreator4.getFinalRightOne());
            ArrayList<CustomVector> gesture2 = finalGesture;
            GestureCalculator gestureCalculator3 = new GestureCalculator(nameThenPremadeGestures, gesture1);
            GestureCalculator gestureCalculator4 = new GestureCalculator(nameThenPremadeGestures, gesture2);

            processResults(gestureCalculator3, "DecisionLevel");
            processResults(gestureCalculator4, "DecisionLevel");

            Fusion decisionFusion = new Fusion();
            GestureCalculator gc = decisionFusion.highLevel(gestureCalculator3, gestureCalculator4);
            processResults(gc, "DecisionLevel");

        }
    }

    public void processResults(GestureCalculator gestureCalculator, String config) {

        this.config = config;

        String filePath;
        if (positionOfTwo == null) {
            filePath = "Results/" + gui.getSelectedGesture() + "/" + config + "/" + positionOfOne + ".txt";
        } else {
            filePath = "Results/" + gui.getSelectedGesture() + "/" + config + "/" + positionOfOne + positionOfTwo + ".txt";
        }
        System.out.println(filePath);

            for (Map.Entry<String, Double> entry : gestureCalculator.getLetterAndVariances().entrySet()) {
                System.out.println(entry.getKey() + "\t" + (entry.getValue()));
            }
            saveResults(gestureCalculator, filePath);

            //saveResults(gestureCalculator, filePath);


        //////////////////////////////


    }

    public void saveResults(GestureCalculator gestureCalculator, String filePath) {

        String data = "";
        for (Map.Entry<String, Double> entry : gestureCalculator.getLetterAndVariances().entrySet()) {
            String key = entry.getKey();
            Double confidence = entry.getValue();
            data = data.concat(key + "\t" + confidence + "\n");
        }
        if (config.equalsIgnoreCase("FeatureLevel")) {
            data = data.concat("\n" + "Winning Left Hand:"+winningLeft + "   Winning Right Hand:"+winningRight + "\n\n\n");
        } else {
            data = data.concat("\n\n\n");
        }
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(data);
            bw.close();
        } catch (Exception e) {

        }

    }

    public void writeToFile(ArrayList<CustomVector> gesture, String name) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("Gestures/" + name);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(gesture);
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addGesture() {

        String input = JOptionPane.showInputDialog("Enter the name of the gesture");
        if (input == null) return;

        name = input;

        gui.getCardLayout().show(gui.getActionArea(), "cp");
        gui.getBeginTestingButton().setText("Confirm Selection");
        gui.getInstructions().setText("Please select your configuration");

        creatingNewGesture = true;

    }

    public void beginTesting() {
        if (gui.getSelectedGesture() == null) {
            JOptionPane.showMessageDialog(gui, "Please select a gesture before continuing");
            return;
        }

        gesturetoTest = gui.getSelectedGesture();

        gui.getCardLayout().show(gui.getActionArea(), "cp");
        gui.getBeginTestingButton().setText("Confirm Selection");
        gui.getInstructions().setText("Please select your configuration");
    }

    public void confirmSelection() {

        String position = gui.getPositionsOfSelected();
        if (position == null) {
            return;
        }
        try {
            positionOfOne = position.split(" ")[0];
            positionOfTwo = position.split(" ")[1];
        } catch (ArrayIndexOutOfBoundsException e) { }

        gui.getBeginTestingButton().setText("Cancel");
        gui.getCardLayout().show(gui.getActionArea(), "ip");
        try {

            /***
             *
             *   Images used in this implemenation are taken from:
             *      https://www.british-sign.co.uk/fingerspelling-alphabet-charts/
             *
             */

            BufferedImage bufferedImage = ImageIO.read(new File("Images/" + name + ".png"));
            Image newImg = bufferedImage.getScaledInstance(gui.getImageLabel().getWidth(), gui.getImageLabel().getHeight(), Image.SCALE_SMOOTH);
            gui.getImageLabel().setIcon(new ImageIcon(newImg));
        } catch (IOException e) {
            try {
                BufferedImage bufferedImage = ImageIO.read(new File("Images/" + gui.getSelectedGesture() + ".png"));
                Image newImg = bufferedImage.getScaledInstance(gui.getImageLabel().getWidth(), gui.getImageLabel().getHeight(), Image.SCALE_SMOOTH);
                gui.getImageLabel().setIcon(new ImageIcon(newImg));
            } catch (IOException ee) { }
        }
        Thread thread = new Thread(this);
        thread.start();

    }

    public void startServer() {
        if (serverStarted) return;
        serverStarted = true;
        server = new Server(this);
    }

    public void destroyServer() {
        server.kill();
    }

    public void indicator(Frame frame, JLabel leftLabel, JLabel rightLabel) {

        HandList hands = frame.hands();
        Hand leftHand = hands.get(0);
        Hand rightHand = hands.get(1);
        if (!leftHand.isLeft()) {
            leftHand = hands.get(1);
            rightHand = hands.get(0);
        }

        if (leftHand.isValid() && leftHand.confidence() >= 0.5) {
            leftLabel.setBackground(new Color(36, 163, 75));
        } else if (leftHand.isValid() && leftHand.confidence() < 0.5) {
            leftLabel.setBackground(new Color(163, 133, 40));
        } else {
            leftLabel.setBackground(new Color(163, 44, 42));
        }

        if (rightHand.isValid() && rightHand.confidence() >= 0.5) {
            rightLabel.setBackground(new Color(36, 163, 75));
        } else if (rightHand.isValid() && rightHand.confidence() < 0.5) {
            rightLabel.setBackground(new Color(163, 133, 40));
        } else {
            rightLabel.setBackground(new Color(163, 44, 42));
        }

    }

    public void removeGesture() {

        String gestureToRemove = gui.getSelectedGesture();
        if (gui.getDlm().contains(gestureToRemove)) {
            gui.getDlm().removeElement(gestureToRemove);
        }

    }

    public void init() {

        String alphabet[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        //String alphabet[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        //String alphabet[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

        for (String letter : alphabet) {

            String filepath = "Gestures/" + letter;
            try {
                FileInputStream fileInputStream = new FileInputStream(filepath);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

                ArrayList<CustomVector> premadeGesture = (ArrayList<CustomVector>) objectInputStream.readObject();
                nameThenPremadeGestures.put(letter, premadeGesture);
                premadeGestures.add(premadeGesture);
                gui.getDlm().addElement(letter);
                gui.getGestureList().updateUI();
                addToLog("Gesture: " + letter + " loaded");

            } catch (Exception e) {

            }
        }
        gui.getGestureList().updateUI();

    }

    public void addToLog(String message) {
        LocalDateTime localDateTime = LocalDateTime.now();
        int hour = localDateTime.getHour();
        int minute = localDateTime.getMinute();
        int second = localDateTime.getSecond();
        String hourString = Integer.toString(hour);
        String minuteString = Integer.toString(minute);
        String secondString = Integer.toString(second);
        if (hour < 10) {
            hourString = "0" + Integer.toString(hour);
        }
        if (minute < 10) {
            minuteString = "0" + Integer.toString(minute);
        }
        if (second < 10) {
            secondString = "0" + Integer.toString(second);
        }
        String time = hourString + ":" + minuteString + ":" + secondString;
        gui.getEventLogListModel().addElement(time + " - " + message);
    }

    public Window getWindow() {
        return window;
    }

    public boolean isRecording() {
        return recording;
    }

    public ArrayList<Frame> getFramesFromOne() {
        return framesFromOne;
    }

    public ArrayList<Frame> getFramesFromTwo() {
        return framesFromTwo;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
