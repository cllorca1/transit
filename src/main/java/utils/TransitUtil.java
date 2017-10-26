package utils;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.Random;

public class TransitUtil {

    static Logger logger = Logger.getLogger(TransitUtil.class);

    public static Random rand;
    public TransitUtil(){}

    public static int findPositionInArray(String element, String[] arr) {
        // return index position of element in array arr
        int ind = -1;
        for (int a = 0; a < arr.length; a++) if (arr[a].equalsIgnoreCase(element)) ind = a;
        if (ind == -1) logger.error("Could not find element " + element +
                " in array (see method <findPositionInArray> in class <TransitUtil>");
        return ind;
    }

    public static void initializeRandomNumber() {
        // initialize random number generator

        int seed = 1;
        if (seed == -1) {
            TransitUtil.rand = new Random();

        } else {
            TransitUtil.rand = new Random(seed);
        }

    }

}
