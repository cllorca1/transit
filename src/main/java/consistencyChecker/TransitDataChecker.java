package consistencyChecker;

import org.apache.log4j.Logger;
import transitSystem.TransitLine;
import transitSystem.TransitStop;
import transitSystem.TransitTrip;

import java.util.ArrayList;

public class TransitDataChecker implements Checker {

private BidirectionalChecker bidirectionalChecker;
private IntersectionChecker intersectionChecker;
private SinuosityChecker sinuosityChecker;
private StopOrderChecker stopOrderChecker;
private static Logger logger = Logger.getLogger(TransitDataChecker.class);

    public TransitDataChecker() {
        bidirectionalChecker = new BidirectionalChecker();
        intersectionChecker = new IntersectionChecker();
        sinuosityChecker = new SinuosityChecker();
        stopOrderChecker = new StopOrderChecker();

    }

    public void load(ArrayList<TransitStop> listOfStops, ArrayList<TransitLine> listOfLines, ArrayList<TransitTrip> listOfTrips) {
        bidirectionalChecker.load(listOfStops,listOfLines, listOfTrips);
        intersectionChecker.load(listOfStops,listOfLines, listOfTrips);
        sinuosityChecker.load(listOfStops,listOfLines, listOfTrips);
        stopOrderChecker.load(listOfStops, listOfLines, listOfTrips);
    }

    public void check() {
        bidirectionalChecker.check();
        intersectionChecker.check();
        sinuosityChecker.check();
        stopOrderChecker.check();
    }
}


