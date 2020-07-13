import com.leapmotion.leap.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GestureCalculator {

    private HashMap<String, ArrayList<CustomVector>> premadeGestures;
    private ArrayList<CustomVector> gesture;

    private HashMap<String, ArrayList<Double>> letterAndDistances = new HashMap<>();
    private HashMap<String, Double> letterAndVariances = new HashMap<>();
    private HashMap<String, Double> lettersAndCoefficients = new HashMap<>();

    private HashMap<String, Double> decisionLevel = new HashMap<>();

    public GestureCalculator(HashMap<String, ArrayList<CustomVector>> premadeGestures, ArrayList<CustomVector> gesture) {
        this.premadeGestures = premadeGestures;
        this.gesture = gesture;

        calculateDistances();
        calculateVariances();
        calculateCoefficient();

    }

    public void calculateDistances() {

        for (Map.Entry<String, ArrayList<CustomVector>> entry : premadeGestures.entrySet()) {
            String key = entry.getKey();
            ArrayList<CustomVector> premadeGesture = entry.getValue();

            ArrayList<Double> distances = new ArrayList<>();
            for (int j = 0; j < premadeGesture.size(); j++) {
                Vector premade = new Vector(premadeGesture.get(j).getX(), premadeGesture.get(j).getY(), premadeGesture.get(j).getZ());
                Vector testing = new Vector(gesture.get(j).getX(), gesture.get(j).getY(), gesture.get(j).getZ());
                double distance = premade.distanceTo(testing);
                distances.add(distance);
            }
            letterAndDistances.put(key, distances);
        }

    }

    public void calculateVariances() {

        for (Map.Entry<String, ArrayList<Double>> entry : letterAndDistances.entrySet()) {
            String key = entry.getKey();
            ArrayList<Double> distances = entry.getValue();

            double mean = 0;
            for (Double dist : distances) {
                mean += dist;
            }
            mean = mean / distances.size();

            double variance = 0;
            for (Double dist : distances) {
                variance = Math.pow((dist - mean), 2);
                variance = variance / distances.size();
            }
            letterAndVariances.put(key, variance);
        }

    }

    public void calculateCoefficient() {

        for (Map.Entry<String, ArrayList<Double>> entry : letterAndDistances.entrySet()) {

            String key = entry.getKey();
            ArrayList<Double> distances = entry.getValue();

            double mean = 0;
            for (Double dist : distances) {
                mean += dist;
            }
            mean = mean / distances.size();

            double var = 0;
            for (Double dist : distances) {
                var = Math.pow((dist - mean), 2);
                var = var / distances.size();
            }

            double std = Math.sqrt(var);
            double cof = 100-((std / mean) * 100);

            lettersAndCoefficients.put(key, cof);

        }

    }

    public HashMap<String, Double> getLetterAndVariances() {
        return letterAndVariances;
    }

    public HashMap<String, Double> getLettersAndCoefficients() {
        return lettersAndCoefficients;
    }

    public void setLettersAndVariances(HashMap<String, Double> newCof) {
        this.letterAndVariances = newCof;
    }

}
