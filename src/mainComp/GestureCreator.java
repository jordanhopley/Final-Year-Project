import com.leapmotion.leap.*;

import java.util.ArrayList;

public class GestureCreator {

    private String name = "";

    private ArrayList<Frame> framesFromOne;
    private ArrayList<Frame> optimisedFramesFromOne;
    private ArrayList<CustomVector> leftHandPositionsOne;
    private ArrayList<CustomVector> rightHandPositionsOne;
    private ArrayList<CustomVector> averageLeftOne;
    private ArrayList<CustomVector> averageRightOne;
    private int angleOne;
    private ArrayList<CustomVector> finalLeftOne;
    private ArrayList<CustomVector> finalRightOne;

    private ArrayList<Frame> framesFromTwo;
    private ArrayList<Frame> optimisedFramesFromTwo;
    private ArrayList<CustomVector> leftHandPositionsTwo;
    private ArrayList<CustomVector> rightHandPositionsTwo;
    private ArrayList<CustomVector> averageLeftTwo;
    private ArrayList<CustomVector> averageRightTwo;
    private int angleTwo;
    private ArrayList<CustomVector> finalLeftTwo;
    private ArrayList<CustomVector> finalRightTwo;

    private boolean isLeftHandPresent;
    private boolean isRightHandPresent;

    private Hand leftHand;
    private Hand rightHand;
    private ArrayList<CustomVector> rotatedLeftHand;
    private ArrayList<CustomVector> rotatedRightHand;

    public GestureCreator(ArrayList<Frame> framesFromOne, ArrayList<Frame> framesFromTwo, String positionOne, String positionTwo) {
        this.name = name;

        this.framesFromOne = framesFromOne;
        this.framesFromTwo = framesFromTwo;

        this.angleOne = getAngle(positionOne);
        this.angleTwo = getAngle(positionTwo);

        this.optimisedFramesFromOne = optimiseFrames(framesFromOne, 1);
        this.optimisedFramesFromTwo = optimiseFrames(framesFromTwo, 1);

        this.leftHandPositionsOne = getPositions(optimisedFramesFromOne, "L");
        this.rightHandPositionsOne = getPositions(optimisedFramesFromOne, "R");
        this.leftHandPositionsTwo = getPositions(optimisedFramesFromTwo, "L");
        this.rightHandPositionsTwo = getPositions(optimisedFramesFromTwo, "R");

        this.averageLeftOne = createAverageFrame(leftHandPositionsOne);
        this.averageRightOne = createAverageFrame(rightHandPositionsOne);
        this.averageLeftTwo = createAverageFrame(leftHandPositionsTwo);
        this.averageRightTwo = createAverageFrame(rightHandPositionsTwo);

        this.finalLeftOne = rotateGesture(averageLeftOne, angleOne);
        this.finalRightOne = rotateGesture(averageRightOne, angleOne);
        this.finalLeftTwo = rotateGesture(averageLeftTwo, angleTwo);
        this.finalRightTwo = rotateGesture(averageRightTwo, angleTwo);

    }

    public GestureCreator(ArrayList<Frame> framesFromOne, String positionOne) {
        this.name = name;

        this.framesFromOne = framesFromOne;

        this.angleOne = getAngle(positionOne);

        this.optimisedFramesFromOne = optimiseFrames(framesFromOne, 1);

        this.leftHandPositionsOne = getPositions(optimisedFramesFromOne, "L");
        this.rightHandPositionsOne = getPositions(optimisedFramesFromOne, "R");

        this.averageLeftOne = createAverageFrame(leftHandPositionsOne);
        this.averageRightOne = createAverageFrame(rightHandPositionsOne);

        this.finalLeftOne = rotateGesture(averageLeftOne, angleOne);
        this.finalRightOne = rotateGesture(averageRightOne, angleOne);

    }

    public GestureCreator(Hand leftHand, Hand rightHand, String positionOne, String positionTwo) {

        int angleOne = getAngle(positionOne);
        int angleTwo = getAngle(positionTwo);

        ArrayList<CustomVector> leftHandPositions = getHandPositions(leftHand);
        ArrayList<CustomVector> rightHandPositions = getHandPositions(rightHand);

        rotatedLeftHand = rotateGesture(leftHandPositions, angleOne);
        rotatedRightHand = rotateGesture(rightHandPositions, angleTwo);

    }

    public ArrayList<CustomVector> getHandPositions(Hand hand) {

        ArrayList<CustomVector> handPositions = new ArrayList<>();

        for (Finger.Type fingerType : Finger.Type.values()) {
            for (Bone.Type boneType : Bone.Type.values()) {
                Vector vector = hand.fingers().fingerType(fingerType).get(0).bone(boneType).center();
                CustomVector cv = new CustomVector(fingerType.toString().concat(" ".concat(boneType.toString())), vector.getX(), vector.getY(), vector.getZ());
                handPositions.add(cv);
            }
        }
        Vector palmPosition = hand.palmPosition();
        Vector wristPosition = hand.wristPosition();
        handPositions.add(new CustomVector("Palm", palmPosition.getX(), palmPosition.getY(), palmPosition.getZ()));
        handPositions.add(new CustomVector("Wrist", wristPosition.getX(), wristPosition.getY(), wristPosition.getZ()));

        return handPositions;
    }

    public ArrayList<CustomVector> rotateGesture(ArrayList<CustomVector> gesture, int angle) {

        ArrayList<CustomVector> rotatedGesture = new ArrayList<>();

        for (CustomVector cv : gesture) {
            Matrix matrix = new Matrix(new Vector(0, 0, 1), (float) Math.toRadians(angle));
            Vector vector = new Vector(cv.getX(), cv.getY(), cv.getZ());
            Vector rotatedVector = matrix.transformDirection(vector);
            rotatedGesture.add(new CustomVector(rotatedVector.getX(), rotatedVector.getY(), rotatedVector.getZ()));
        }

        return rotatedGesture;
    }

    public ArrayList<CustomVector> createAverageFrame(ArrayList<CustomVector> positions) {

        ArrayList<CustomVector> averageFrame = new ArrayList<>();

        for (int i = 0; i < 22; i++) {
            Vector first = new Vector(0, 0, 0);
            for (int j = 0; j < positions.size(); j+=22) {
                Vector second = new Vector(positions.get(i+j).getX(), positions.get(i+j).getY(), positions.get(i+j).getZ());
                first = first.plus(second);
            }
            first = first.divide(positions.size()/(float)22);
            CustomVector averageVector = new CustomVector(first.getX(), first.getY(), first.getZ());
            averageFrame.add(averageVector);
        }

        return averageFrame;
    }

    public ArrayList<CustomVector> getPositions(ArrayList<Frame> frames, String identifier) {

        ArrayList<CustomVector> averageFrame = new ArrayList<>();
        Hand hand = new Hand();

        for (Frame frame : frames) {
            if (identifier.equalsIgnoreCase("L")) {
                hand = getLeftHand(frame);
            } else if (identifier.equalsIgnoreCase("R")) {
                hand = getRightHand(frame);
            }
            for (Finger.Type fingerType : Finger.Type.values()) {
                for (Bone.Type boneType : Bone.Type.values()) {
                    Vector vector = hand.fingers().fingerType(fingerType).get(0).bone(boneType).center();
                    CustomVector cv = new CustomVector(fingerType.toString().concat(" ".concat(boneType.toString())), vector.getX(), vector.getY(), vector.getZ());
                    averageFrame.add(cv);
                }
            }
            Vector palmPosition = hand.palmPosition();
            Vector wristPosition = hand.wristPosition();
            averageFrame.add(new CustomVector("Palm", palmPosition.getX(), palmPosition.getY(), palmPosition.getZ())); // --
            averageFrame.add(new CustomVector("Wrist", wristPosition.getX(), wristPosition.getY(), palmPosition.getZ())); // --
        }

        return averageFrame;
    }

    public ArrayList<Frame> optimiseFrames(ArrayList<Frame> framesToOptimise, double maxConfidence) {

        ArrayList<Frame> optimisedFrames = framesToOptimise;
        ArrayList<Frame> badFrames = new ArrayList<>();

        // Check if there is only one hand
        int oneHandCount = 0;
        int twoHandCount = 0;
        for (Frame frame : framesFromOne) {
            if (frame.hands().count() == 1) {
                oneHandCount++;
            }
            if (frame.hands().count() == 2) {
                twoHandCount++;
            }
        }

        // If there is only one hand
        if (oneHandCount > twoHandCount) {
            for (Frame frame : framesToOptimise) {
                Hand hand1 = frame.hands().get(0);
                if (hand1.confidence() < maxConfidence) {
                    badFrames.add(frame);
                }
            }
        }

        // If there are two hands
        else {
            for (Frame frame : framesToOptimise) {
                Hand hand1 = frame.hands().get(0);
                Hand hand2 = frame.hands().get(1);
                if (hand1.confidence() < maxConfidence || hand2.confidence() < maxConfidence) {
                    badFrames.add(frame);
                }
            }
        }

        if (badFrames.size() == optimisedFrames.size()) {
            if (maxConfidence == 0) {
                System.out.println("All frames are too bad");
                return null;
            }
            maxConfidence = maxConfidence - 0.1;
            optimiseFrames(framesToOptimise, maxConfidence);
        } else {
            for (Frame badFrame : badFrames) {
                optimisedFrames.remove(badFrame);
            }
        }

        return optimisedFrames;

    }

    public Hand getLeftHand(Frame frame) {
        Hand leftHand = frame.hands().get(0);
        if (!leftHand.isLeft()) {
            leftHand = frame.hands().get(1);
        }
        return leftHand;
    }

    public Hand getRightHand(Frame frame) {
        Hand rightHand = frame.hands().get(0);
        if (!rightHand.isRight()) {
            rightHand = frame.hands().get(1);
        }
        return rightHand;
    }

    public int getAngle(String position) {

        if (position.equalsIgnoreCase("South")) return 0;
        if (position.equalsIgnoreCase("SouthWest")) return 0; /////////////////////////////////// 45
        if (position.equalsIgnoreCase("West")) return 90;
        if (position.equalsIgnoreCase("Northwest")) return 135;
        if (position.equalsIgnoreCase("North")) return 180;
        if (position.equalsIgnoreCase("Northeast")) return 225;
        if (position.equalsIgnoreCase("East")) return 270;
        if (position.equalsIgnoreCase("SouthEast")) return 0; ////////////////////////////////// 315
        return -1;

    }

    public ArrayList<CustomVector> getFinalLeftOne() {
        return finalLeftOne;
    }

    public ArrayList<CustomVector> getFinalRightOne() {
        return finalRightOne;
    }

    public ArrayList<CustomVector> getFinalLeftTwo() {
        return finalLeftTwo;
    }

    public ArrayList<CustomVector> getFinalRightTwo() {
        return finalRightTwo;
    }

     public String getName() {
        return name;
     }

    public ArrayList<CustomVector> getRotatedLeftHand() {
        return rotatedLeftHand;
    }

    public ArrayList<CustomVector> getRotatedRightHand() {
        return rotatedRightHand;
    }

}
