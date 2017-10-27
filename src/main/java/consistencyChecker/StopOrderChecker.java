package consistencyChecker;

import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;
import org.omg.CORBA.TRANSACTION_MODE;
import transitSystem.TransitDataContainer;
import transitSystem.TransitLine;
import transitSystem.TransitStop;
import transitSystem.TransitTrip;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class StopOrderChecker implements Checker {

    private ArrayList<TransitStop> listOfStops;
    private ArrayList<TransitLine> listOfLines;
    private ArrayList<TransitTrip> listOfTrips;
    private static Logger logger = Logger.getLogger(IntersectionChecker.class);
    private String reportFileName;


    private double alphaDistanceUpperThreshold;
    private double alphaDistanceLowerThreshold;


    public void load(TransitDataContainer transitDataContainer) {
        this.listOfStops = transitDataContainer.getListOfStops();
        this.listOfLines = transitDataContainer.getListOfLines();
        this.listOfTrips = transitDataContainer.getListOfTrips();
        alphaDistanceUpperThreshold = 3;
        alphaDistanceLowerThreshold = .25;

    }

    public void setReportFileName(String reportFileName) {
        this.reportFileName = reportFileName;
    }


    public void check() {

        int errCount = 0;


        try {

            PrintWriter pw = new PrintWriter(new FileWriter(reportFileName, false));
            pw.println("lineId,lineRef,seq,stopId,stopName,eucDist,onLineDist,accepted");


            System.out.println("Writing output file for " + listOfLines.size() + " lines.");


            for (TransitTrip trip : listOfTrips) {
                TransitLine line = trip.getTransitLine();
                boolean error = false;
                Map<Integer, TransitStop> stopMap = new HashMap<>();
                try {
                    stopMap = line.getStopList();
                } catch(NullPointerException e){
                    logger.info("!");
                }
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
                    distanceToOriginMap.put(j, cumDistance);
                }

                //calculate the on the route distance to furthest stop
                fromStop = stopMap.get(i);
                double slope = getDistanceFromStops(fromStop, stopMap.get(furthestStopIndex)) / cumDistance;


                //loop to find unordered stops as difference between straight and on the line distances

                for (int j : stopMap.keySet()) {

                    double euclideanDist = getDistanceFromStops(fromStop, stopMap.get(j));
                    double ifLinealUpperThreshold = slope * distanceToOriginMap.get(j) * alphaDistanceUpperThreshold;
                    double ifLinealLowerThreshold = slope * distanceToOriginMap.get(j) * alphaDistanceLowerThreshold;
                    char acc = 'a';
                    if ((euclideanDist < ifLinealLowerThreshold) ||
                            (euclideanDist > ifLinealUpperThreshold)) {
                        error = true;
                        acc = 'r';
                    }

                    pw.println(line.getLineId() + "," +
                            line.getLineName().replace(",","&") + "," +
                            j + "," +
                            stopMap.get(j).getStopId() + "," +
                            stopMap.get(j).getStopName().replace(",","&") + "," +
                            euclideanDist + "," +
                            slope * distanceToOriginMap.get(j) + "," +
                            acc);

                }

                //}

                if (error) {
                    errCount++;
                    long lineid = trip.getTransitLine().getLineId();
                    String lineName = trip.getTransitLine().getLineName();
                    //logger.info(cumDistance);
                    logger.warn("The line " + lineName + "%" + lineid + "%  has incorrect stop order - if the line is lineal");
                }

            }

            logger.warn("Total number of errors due to 'incorrect stop order' is: " + errCount);


            pw.flush();
            pw.close();


        } catch (IOException e) {
            e.printStackTrace();


        }
    }

    public double getDistanceFromStops(TransitStop stop1, TransitStop stop2) {
        double distance;
        distance = Math.pow(Double.parseDouble(stop1.getLat()) - Double.parseDouble(stop2.getLat()), 2) +
                Math.pow(Double.parseDouble(stop1.getLon()) - Double.parseDouble(stop2.getLon()), 2);
        return Math.sqrt(distance);

    }


}
