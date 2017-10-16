package consistencyChecker;

import org.apache.log4j.Logger;
import org.omg.CORBA.TRANSACTION_MODE;
import transitSystem.TransitLine;
import transitSystem.TransitStop;
import transitSystem.TransitTrip;

import java.util.*;

public class StopOrderChecker implements Checker {

    private ArrayList<TransitStop> listOfStops;
    private ArrayList<TransitLine> listOfLines;
    private ArrayList<TransitTrip> listOfTrips;
    private static Logger logger = Logger.getLogger(IntersectionChecker.class);
    private double distanceThreshold;

    private double alphaDistanceThreshold;


    public void load(ArrayList<TransitStop> listOfStops, ArrayList<TransitLine> listOfLines, ArrayList<TransitTrip> listOfTrips) {
        this.listOfStops = listOfStops;
        this.listOfLines = listOfLines;
        this.listOfTrips = listOfTrips;
        alphaDistanceThreshold = 2;

    }

    public void check() {

        int errCount = 0;

        for (TransitTrip trip : listOfTrips) {
            TransitLine line = trip.getTransitLine();
            boolean error = false;
            Map<Integer, TransitStop> stopMap = line.getStopList();
            //only from one of the edges:
            int i = 0;
            TransitStop fromStop = stopMap.get(i);

            int furthestStopIndex = stopMap.size() - 1;
            Map<Integer, Double> distanceToOriginMap = new HashMap<>();
            //loop to store the cumulative distance along the line
            double cumDistance = 0;
            for (int j : stopMap.keySet()) {
                cumDistance += getDistanceFromStops(fromStop, stopMap.get(j));
                fromStop = stopMap.get(j);
                distanceToOriginMap.put(j,cumDistance);
            }

            //calculate the on the route distance to furthest stop
            fromStop = stopMap.get(i);
            double slope = getDistanceFromStops(fromStop, stopMap.get(furthestStopIndex)) / cumDistance;


            //loop to find unordered stops as difference between straight and on the line distances

            for (int j : stopMap.keySet()) {
                if (Math.abs(getDistanceFromStops(fromStop, stopMap.get(j)) - slope * distanceToOriginMap.get(j)) >
                        slope * getDistanceFromStops(fromStop,stopMap.get(j))*alphaDistanceThreshold) {
                    error = true;
                }

            }

            //}

            if (error) {
                errCount++;
                long lineid = trip.getTransitLine().getLineId();
                String lineName = trip.getTransitLine().getLineName();
                //logger.info(cumDistance);
                logger.warn("The line " + lineName + "%" + lineid + "%  has incorrect stop order");
            }

        }

        logger.warn("Total number of errors due to 'incorrect stop order' is: " + errCount);

    }

    public double getDistanceFromStops(TransitStop stop1, TransitStop stop2) {
        double distance;
        distance = Math.pow(Double.parseDouble(stop1.getLat()) - Double.parseDouble(stop2.getLat()), 2) +
                Math.pow(Double.parseDouble(stop1.getLon()) - Double.parseDouble(stop2.getLon()), 2);
        return Math.sqrt(distance);

    }


}
