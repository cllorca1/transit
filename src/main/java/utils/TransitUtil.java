package utils;

import org.apache.log4j.Logger;

public class TransitUtil {

    static Logger logger = Logger.getLogger(TransitUtil.class);

    public TransitUtil(){}

    public static int findPositionInArray(String element, String[] arr) {
        // return index position of element in array arr
        int ind = -1;
        for (int a = 0; a < arr.length; a++) if (arr[a].equalsIgnoreCase(element)) ind = a;
        if (ind == -1) logger.error("Could not find element " + element +
                " in array (see method <findPositionInArray> in class <TransitUtil>");
        return ind;
    }

}
