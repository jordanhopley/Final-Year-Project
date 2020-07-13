import com.leapmotion.leap.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Fusion {

    private ArrayList<CustomVector> leftOne;
    private ArrayList<CustomVector> rightOne;
    private ArrayList<CustomVector> leftTwo;
    private ArrayList<CustomVector> rightTwo;

    private String winningLaptopForLeftHand = "";
    private String winningLaptopForRightHand = "";

    private ArrayList<CustomVector> lowLevelFused;
    private ArrayList<CustomVector> highLevelFused;
    private ArrayList<CustomVector> featureLevelFused;

    public Fusion(ArrayList<CustomVector> leftOne, ArrayList<CustomVector> rightOne, ArrayList<CustomVector> leftTwo, ArrayList<CustomVector> rightTwo) {

        this.leftOne = leftOne;
        this.rightOne = rightOne;
        this.leftTwo = leftTwo;
        this.rightTwo = rightTwo;

        lowLevel();

    }

    public Fusion() {

    }

    public void lowLevel() {

        lowLevelFused = new ArrayList<>();

        for (int i = 0; i < leftOne.size(); i++) {
            Vector firstLeftSelection = new Vector(leftOne.get(i).getX(), leftOne.get(i).getY(), leftOne.get(i).getZ());
            Vector secondLeftSelection = new Vector(leftTwo.get(i).getX(), leftTwo.get(i).getY(), leftTwo.get(i).getZ());
            Vector fusedLeft = firstLeftSelection.plus(secondLeftSelection).divide(2);
            lowLevelFused.add(new CustomVector(fusedLeft.getX(), fusedLeft.getY(), fusedLeft.getZ()));
        }
        for (int i = 0; i < rightOne.size(); i++) {
            Vector firstRightSelection = new Vector(rightOne.get(i).getX(), rightOne.get(i).getY(), rightOne.get(i).getZ());
            Vector secondRightSelection = new Vector(rightTwo.get(i).getX(), rightTwo.get(i).getY(), rightTwo.get(i).getZ());
            Vector fusedRight = firstRightSelection.plus(secondRightSelection).divide(2);
            lowLevelFused.add(new CustomVector(fusedRight.getX(), fusedRight.getY(), fusedRight.getZ()));
        }

    }

    public GestureCalculator highLevel(GestureCalculator one, GestureCalculator two) {

        HashMap<String, Double> newVar = new HashMap<>();

        for (Map.Entry<String, Double> entry : one.getLetterAndVariances().entrySet()) {
            String key = entry.getKey();
            Double var1 = entry.getValue();
            Double var2 = two.getLetterAndVariances().get(key);
            Double var3 = (var1 + var2) / 2;
            newVar.put(key, var3);
        }

        one.setLettersAndVariances(newVar);
        return one;

    }

    public ArrayList<Hand> featureLevel(ArrayList<Frame> leftFrames, ArrayList<Frame> rightFrames) {

        Hand bestLeftL = new Hand();
        Hand bestRightL = new Hand();
        for (Frame f : leftFrames) {
            HandList hands = f.hands();
            Hand leftHand = hands.get(0);
            Hand rightHand = hands.get(1);
            if (!leftHand.isLeft()) {
                leftHand = hands.get(1);
                rightHand = hands.get(0);
            }
            if (leftHand.confidence() > bestLeftL.confidence()) {
                bestLeftL = leftHand;
            }
            if (rightHand.confidence() > bestRightL.confidence()) {
                bestRightL = rightHand;
            }
        }

        Hand bestLeftR = new Hand();
        Hand bestRightR = new Hand();
        for (Frame f : rightFrames) {
            HandList hands = f.hands();
            Hand leftHand = hands.get(0);
            Hand rightHand = hands.get(1);
            if (!leftHand.isLeft()) {
                leftHand = hands.get(1);
                rightHand = hands.get(0);
            }
            if (leftHand.confidence() > bestLeftR.confidence()) {
                bestLeftR = leftHand;
            }
            if (rightHand.confidence() > bestRightR.confidence()) {
                bestRightR = rightHand;
            }
        }

        Hand winningLeft = bestLeftL;
        Hand winningRight = bestRightL;

        winningLaptopForLeftHand = "Left";
        winningLaptopForRightHand = "Left";

        if (bestLeftL.confidence() < bestLeftR.confidence()) {
            winningLeft = bestLeftR;
            winningLaptopForLeftHand = "Right";
        }

        if (bestRightL.confidence() < bestRightR.confidence()) {
            winningRight = bestRightR;
            winningLaptopForRightHand = "Right";
        }

        ArrayList<Hand> winningHands = new ArrayList<>();
        winningHands.add(winningLeft);
        winningHands.add(winningRight);

        return winningHands;

    }

    public ArrayList<CustomVector> getLeftOne() {
        return leftOne;
    }

    public ArrayList<CustomVector> getRightOne() {
        return rightOne;
    }

    public ArrayList<CustomVector> getLeftTwo() {
        return leftTwo;
    }

    public ArrayList<CustomVector> getRightTwo() {
        return rightTwo;
    }

    public ArrayList<CustomVector> getLowLevelFused() {
        return lowLevelFused;
    }

    public ArrayList<CustomVector> getHighLevelFused() {
        return highLevelFused;
    }

    public ArrayList<CustomVector> getFeatureLevelFused() {
        return featureLevelFused;
    }

    public String getWinningLaptopForLeftHand() {
        return winningLaptopForLeftHand;
    }

    public String getWinningLaptopForRightHand() {
        return winningLaptopForRightHand;
    }

}
