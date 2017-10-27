package consistencyChecker;

import org.apache.log4j.Logger;
import transitSystem.TransitDataContainer;
import transitSystem.TransitLine;
import transitSystem.TransitStop;
import transitSystem.TransitTrip;


import java.util.ArrayList;

public class BidirectionalChecker implements Checker {
    private ArrayList<TransitStop> listOfStops;
    private ArrayList<TransitLine> listOfLines;
    private ArrayList<TransitTrip> listOfTrips;
    private static Logger logger = Logger.getLogger(BidirectionalChecker.class);


    public void load(TransitDataContainer transitDataContainer) {
        this.listOfStops = transitDataContainer.getListOfStops();
        this.listOfLines = transitDataContainer.getListOfLines();
        this.listOfTrips = transitDataContainer.getListOfTrips();
    }

    public void check() {

        for (TransitTrip transitTrip : listOfTrips){

            for (TransitTrip opposingTrip : listOfTrips){


                String origin1 = transitTrip.getStopToStopList().get(0).getOrigTransitStop().getStopName();
                String dest1 = transitTrip.getStopToStopList().get(transitTrip.getStopToStopList().size()-1).getDestTransitStop().getStopName();
                String origin2 = opposingTrip.getStopToStopList().get(0).getOrigTransitStop().getStopName();
                String dest2 = opposingTrip.getStopToStopList().get(opposingTrip.getStopToStopList().size()-1).getDestTransitStop().getStopName();

                if( origin1.equals(dest2) && origin2.equals(dest1) ){
                    transitTrip.setOpposingTrip(opposingTrip);
                }
            }


        }
        int errCount = 0;
        for (TransitTrip transitTrip : listOfTrips){
            TransitTrip opposingTrip = transitTrip.getOpposingTrip();
            if(opposingTrip == null){
                long lineid = transitTrip.getTransitLine().getLineId();
                String lineName = transitTrip.getTransitLine().getLineName();

                logger.warn("The line " + lineName + "%" + lineid + "%  does not have an opposing direction line");
                errCount ++;

            }
        }

        logger.warn("Total number of errors 'absence of opposing line' is: " + errCount);

    }
}
