package consistencyChecker;

import org.apache.log4j.Logger;
import org.cts.crs.CRSException;
import transitSystem.*;
import writeMATSimXMLFiles.TransformCoordinates;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

public class TravelSpeedChecker implements Checker {

    private ArrayList<TransitStop> listOfStops;
    private ArrayList<TransitLine> listOfLines;
    private ArrayList<TransitTrip> listOfTrips;
    private static Logger logger = Logger.getLogger(TravelSpeedChecker.class);
    private String reportFileName;
    private TransformCoordinates transformCoordinates;

    @Override
    public void load(TransitDataContainer transitDataContainer) {

        this.listOfStops = transitDataContainer.getListOfStops();
        this.listOfLines = transitDataContainer.getListOfLines();
        this.listOfTrips = transitDataContainer.getListOfTrips();

        try {
            transformCoordinates = new TransformCoordinates();
        } catch (CRSException e) {
            e.printStackTrace();
        }



    }

    public void setReportFileName(String reportFileName) {
        this.reportFileName = reportFileName;
    }

    @Override
    public void check() {

        PrintWriter pw;
        try {
            pw = new PrintWriter(new FileWriter(reportFileName, false));

        pw.println("lineId,seq,fromStopId,toStopId,eucDistance,travelTime,speed");


        System.out.println("Writing output file for " + listOfLines.size() + " lines.");

        for (TransitTrip trip : listOfTrips){
            trip.getTransitLine().getStopList().values().forEach(stop -> {
                transformCoordinates.transformCoordinates(stop);

            });

            int seq = 0;

            Map<Integer, TransitStopToStop> stopToStopList  = trip.getStopToStopList();
            for (TransitStopToStop transitStopToStop : stopToStopList.values()){

                double time = transitStopToStop.getArrivalTime() - transitStopToStop.getDepartureTime();
                double distance = getDistanceFromStops(transitStopToStop.getOrigTransitStop(), transitStopToStop.getDestTransitStop());

                double speed = distance / time * 3.6;

                if (speed <= 0){
                    //assign 50 km/h to correct negative or null speeds
                    speed = 50 / 3.6 ;
                    time = distance / speed;
                    transitStopToStop.setDepartureTime(8*3600);
                    transitStopToStop.setArrivalTime(8*3600 + (int) time);

                }

                pw.print(trip.getTransitLine().getLineId());
                pw.print(",");
                pw.print(seq);
                seq++;
                pw.print(",");
                pw.print(transitStopToStop.getOrigTransitStop().getStopId());
                pw.print(",");
                pw.print(transitStopToStop.getDestTransitStop().getStopId());
                pw.print(",");
                pw.print(distance);
                pw.print(",");
                pw.print(time);
                pw.print(",");
                pw.print(speed);
                pw.println();

            }

        }

            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public double getDistanceFromStops(TransitStop stop1, TransitStop stop2) {
        double distance;
        distance = Math.pow(stop1.getX() - stop2.getX(), 2) +
                Math.pow(stop1.getX() - stop2.getX(), 2);
        return Math.sqrt(distance);

    }
}
