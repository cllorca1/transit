package writeMATSimXMLFiles;

import org.apache.log4j.Logger;
import org.cts.crs.CRSException;
import transitSystem.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by carlloga on 16/12/16.
 */
public class WriteXMLRailNetwork {

    private static Logger logger = Logger.getLogger(WriteXMLRailNetwork.class);
    private TransitDataContainer transitDataContainer;


    private static String networkPrefix = "bus";

    public static String getNetworkPrefix() {
        return networkPrefix;
    }

    public WriteXMLRailNetwork(TransitDataContainer transitDataContainer) {
        this.transitDataContainer = transitDataContainer;
    }

    public void writeXMLRailNetwork(String fileName) {

        ArrayList<TransitStop> listOfStops = transitDataContainer.getListOfStops();
        ArrayList<TransitTrip> listOfTrips = transitDataContainer.getListOfTrips();

        //sub-method to write links as stopToStop connectors
        int networkLink = 0;
        try {

            PrintWriter pw = new PrintWriter(new FileWriter(fileName, false));

            pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            pw.println("<!DOCTYPE network SYSTEM \"http://www.matsim.org/files/dtd/network_v1.dtd\">");

            pw.println("<network>");



            for (TransitTrip transitTrip : listOfTrips) {

                Map<Integer, TransitStopToStop> stopToStopMap = transitTrip.getStopToStopList();

                int sequenceNumber = 0;

                TransformCoordinates tc = new TransformCoordinates();

                //sequential version
                for (TransitStopToStop transitStopToStop : stopToStopMap.values()) {

                    //this nodes will be writen in XML list of nodes ()

                    TransitStop origStop = transitStopToStop.getOrigTransitStop();
                    origStop.setPrintXMLNode(true);
                    TransitStop destStop = transitStopToStop.getDestTransitStop();
                    destStop.setPrintXMLNode(true);

                    tc.transformCoordinates(destStop);

                    tc.transformCoordinates(origStop);

//                    int duration = transitStopToStop.getArrivalTime() - transitStopToStop.getDepartureTime();
//                    double dx = transitStopToStop.getOrigTransitStop().getX() - transitStopToStop.getDestTransitStop().getX();
//                    double dy = transitStopToStop.getOrigTransitStop().getY() - transitStopToStop.getDestTransitStop().getY();
//                    double distance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
//                    double freeFlowSpeed = distance / duration * 1.5;
                    //multiplied by 1.5 to allow trains get on time
                }
            }

            logger.info("Coordinates of nodes already transformed");

            //sub=method to print nodes that were found in trip list
            //PrintWriter pw = new PrintWriter(new FileWriter("./output/XML/nodes.xml", true));

            pw.println("<nodes>");

            for (TransitStop transitStop : listOfStops) {

                if (transitStop.isPrintXMLNode()) {


                    pw.print("<node id=\"pt" + networkPrefix);
                    pw.print(transitStop.getStopId());
                    pw.print("\" x=\"");
                    pw.print(transitStop.getX());
                    pw.print("\" y=\"");
                    pw.print(transitStop.getY());
                    pw.println("\" />");

                }

            }
            pw.println("</nodes>");

            logger.info("Nodes for transit stations written");

            pw.println("<links>");



            for (TransitTrip transitTrip : listOfTrips) {


                Map<Integer, TransitStopToStop> stopToStopMap = transitTrip.getStopToStopList();

                int sequenceNumber = 0;
                for (TransitStopToStop transitStopToStop : stopToStopMap.values()) {


                    //this nodes will be writen in XML list of nodes (This replicates some assignments of the variable...)

                    int duration = transitStopToStop.getArrivalTime() - transitStopToStop.getDepartureTime();
                    double dx = transitStopToStop.getOrigTransitStop().getX() - transitStopToStop.getDestTransitStop().getX();
                    double dy = transitStopToStop.getOrigTransitStop().getY() - transitStopToStop.getDestTransitStop().getY();
                    double distance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
                    distance = distance==0? 0.1: distance;
                    double freeFlowSpeed = distance / duration * 1.5;
                    freeFlowSpeed = freeFlowSpeed>200/3.6? 1: freeFlowSpeed;

                    //multiplied by 1.5 to allow trains get on time

//                    String networkPrefix = "";

                    //todo need to generate an extra link to store the bus/train before getting first stop and assign them very high speed and typical length over vehicle length
                    if (sequenceNumber==0){
                        transitTrip.setSidingLink(networkLink);
                        pw.print("<link id=\"ptExtra" + networkPrefix+networkLink);
                        pw.print("\" from=\"pt" + networkPrefix);
                        pw.print(transitStopToStop.getDestTransitStop().getStopId());
                        pw.print("\" to=\"pt"+ networkPrefix);
                        pw.print(transitStopToStop.getOrigTransitStop().getStopId());
                        pw.print("\" length=\"" );
                        pw.print(distance);
                        pw.print("\" freespeed=\"");
                        pw.print(freeFlowSpeed);
                        pw.println("\" capacity=\"100000.0\" permlanes=\"1.0\" oneway=\"1\" modes=\"pt\" />");

                    }

                    pw.print("<link id=\"pt" + networkPrefix);
                    pw.print(networkLink);

                    transitStopToStop.setNetworkLink(networkLink);
                    /*if (sequenceNumber==0){
                        transitStopToStop.getOrigTransitStop().setNetworkLink(networkLink);

                    }
                    transitStopToStop.getDestTransitStop().setNetworkLink(networkLink);*/
                    sequenceNumber++;
                    networkLink++;
                    pw.print("\" from=\"pt" + networkPrefix);
                    pw.print(transitStopToStop.getOrigTransitStop().getStopId());
                    pw.print("\" to=\"pt"+ networkPrefix);
                    pw.print(transitStopToStop.getDestTransitStop().getStopId());
                    pw.print("\" length=\"");
                    pw.print(distance);
                    pw.print("\" freespeed=\"");
                    pw.print(freeFlowSpeed);
                    pw.println("\" capacity=\"100000.0\" permlanes=\"1.0\" oneway=\"1\" modes=\"pt\" />");

                }

            }

            logger.info("Links for transit lines written");

            pw.println("</links>");


            pw.println("</network>");

            pw.flush();
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CRSException e) {
            e.printStackTrace();
        }


        logger.info("Netowork xml file for transit written");

    }


}
