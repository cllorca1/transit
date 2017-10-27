package consistencyChecker;

import org.apache.log4j.Logger;
import transitSystem.*;

import java.util.ArrayList;
import java.util.Map;

public class SinuosityChecker implements Checker {
    private ArrayList<TransitStop> listOfStops;
    private ArrayList<TransitLine> listOfLines;
    private ArrayList<TransitTrip> listOfTrips;
    private static Logger logger = Logger.getLogger(SinuosityChecker.class);
    private double alpha;


    public void load(TransitDataContainer transitDataContainer) {
        this.listOfStops = transitDataContainer.getListOfStops();
        this.listOfLines = transitDataContainer.getListOfLines();
        this.listOfTrips = transitDataContainer.getListOfTrips();
        alpha = 1.9;

    }

    public void check() {
        int errCount = 0;
        for (TransitTrip trip  : listOfTrips){
            boolean error = false;
            Map<Integer, TransitStopToStop> stopToStopMap = trip.getStopToStopList();
            for (int seq : stopToStopMap.keySet()){
                TransitStop stop1 = stopToStopMap.get(seq).getOrigTransitStop();
                TransitStop stop2 = stopToStopMap.get(seq).getDestTransitStop();
                double distance12 = getDistanceFromStops(stop1, stop2);
                for (TransitStopToStop stopToStop : stopToStopMap.values()){
                    TransitStop stop3 = stopToStop.getOrigTransitStop();
                    //TransitStop stop4 = stopToStop.getDestTransitStop();
                    if (!stop1.equals(stop3) && !stop2.equals(stop3)){
                        double distance13 = getDistanceFromStops(stop1,stop3) ;
                        double distance32 = getDistanceFromStops(stop3,stop2);
                        if (distance12 > distance13*alpha && distance12 > distance32*alpha){
                            error = true;
                        }

                    }
                }



            }
            if (error){
                errCount++;
                long lineid = trip.getTransitLine().getLineId();
                String lineName = trip.getTransitLine().getLineName();
                logger.warn("The line " + lineName + "%" + lineid + "% is too sinuous");
            }


        }
        logger.warn("Total number of errors 'sinuous' is: " + errCount);
    }

    public double getDistanceFromStops (TransitStop stop1, TransitStop stop2){
        double distance;
        distance = Math.pow(Double.parseDouble(stop1.getLat()) - Double.parseDouble(stop2.getLat()),2) +
        Math.pow(Double.parseDouble(stop1.getLon()) - Double.parseDouble(stop2.getLon()),2);
        return Math.sqrt(distance);

    }
}
