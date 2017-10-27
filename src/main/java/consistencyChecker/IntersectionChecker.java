package consistencyChecker;

import org.apache.log4j.Logger;
import transitSystem.*;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Map;

public class IntersectionChecker implements Checker {

    private ArrayList<TransitStop> listOfStops;
    private ArrayList<TransitLine> listOfLines;
    private ArrayList<TransitTrip> listOfTrips;
    private static Logger logger = Logger.getLogger(IntersectionChecker.class);


    public void load(TransitDataContainer transitDataContainer) {
        this.listOfStops = transitDataContainer.getListOfStops();
        this.listOfLines = transitDataContainer.getListOfLines();
        this.listOfTrips = transitDataContainer.getListOfTrips();

    }

    public void check() {

        int errCount = 0;
        for (TransitTrip trip : listOfTrips){
            boolean error = false;
            Map<Integer, TransitStopToStop> segmentMap = trip.getStopToStopList();
            for (TransitStopToStop segment1 : segmentMap.values() ){
                Point2D p1 = new Point2D.Double(Double.parseDouble(segment1.getOrigTransitStop().getLat()), Double.parseDouble(segment1.getOrigTransitStop().getLon()));
                Point2D p2 = new Point2D.Double(Double.parseDouble(segment1.getDestTransitStop().getLat()), Double.parseDouble(segment1.getDestTransitStop().getLon()));

                Line2D line1 = new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY());
                for (TransitStopToStop segment2 : segmentMap.values())
                    if (!segment1.equals(segment2)){
                        Point2D p3 = new Point2D.Double(Double.parseDouble(segment2.getOrigTransitStop().getLat()), Double.parseDouble(segment2.getOrigTransitStop().getLon()));
                        Point2D p4 = new Point2D.Double(Double.parseDouble(segment2.getDestTransitStop().getLat()), Double.parseDouble(segment2.getDestTransitStop().getLon()));

                        Line2D line2 = new Line2D.Double(p3.getX(), p3.getY(), p4.getX(), p4.getY());
                        if(line1.intersectsLine(line2) && !p1.equals(p3) && !p1.equals(p4) && !p2.equals(p3) && !p2.equals(p4)){
                            error = true;
                        }
                    }



            }
            if (error){
                errCount++;
                long lineid = trip.getTransitLine().getLineId();
                String lineName = trip.getTransitLine().getLineName();
                logger.warn("The line " + lineName + "%" + lineid + "%  has intersecting segments");
            }
        }



        logger.warn("Total number of errors due to 'intersection' is: " + errCount);

    }
}
