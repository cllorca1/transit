package consistencyChecker;

import org.apache.log4j.Logger;
import transitSystem.TransitDataContainer;
import transitSystem.TransitLine;
import transitSystem.TransitStop;
import transitSystem.TransitTrip;

import java.util.ArrayList;

public class TransitDataChecker implements Checker {

private BidirectionalChecker bidirectionalChecker;
private IntersectionChecker intersectionChecker;
private SinuosityChecker sinuosityChecker;
private StopOrderChecker stopOrderChecker;
private TravelSpeedChecker travelSpeedChecker;
private static Logger logger = Logger.getLogger(TransitDataChecker.class);
private TransitDataContainer transitDataContainer;

    public TransitDataChecker(String outputFolder) {
        bidirectionalChecker = new BidirectionalChecker();
        intersectionChecker = new IntersectionChecker();
        sinuosityChecker = new SinuosityChecker();
        stopOrderChecker = new StopOrderChecker();
        travelSpeedChecker = new TravelSpeedChecker();




        stopOrderChecker.setReportFileName(outputFolder + "/" + "stopOrder.csv");
        travelSpeedChecker.setReportFileName(outputFolder + "/" + "speedCheck.csv");

    }

    public void load(TransitDataContainer transitDataContainer) {
        bidirectionalChecker.load(transitDataContainer);
        intersectionChecker.load(transitDataContainer);
        sinuosityChecker.load(transitDataContainer);
        stopOrderChecker.load(transitDataContainer);
        travelSpeedChecker.load(transitDataContainer);

    }

    public void check() {
        bidirectionalChecker.check();
        intersectionChecker.check();
        sinuosityChecker.check();
        stopOrderChecker.check();
        travelSpeedChecker.check();
    }
}


