package writeMATSimXMLFiles;

import transitSystem.TransitLine;
import transitSystem.TransitStop;
import transitSystem.TransitStopToStop;
import transitSystem.TransitTrip;

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

    public void writeXMLRailNetwork(ArrayList<TransitStop> listOfStops, ArrayList<TransitTrip> listOfTrips, String fileName) {

        //sub-method to write links as stopToStop connectors
        int networkLink = 0;
        try {

            PrintWriter pw = new PrintWriter(new FileWriter(fileName, false));

            pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            pw.println("<!DOCTYPE network SYSTEM \"http://www.matsim.org/files/dtd/network_v1.dtd\">");

            pw.println("<network>");

            //TODO The order of writing nodes and links needs to (but cannot) be changed --- seems solved!

            for (TransitTrip transitTrip : listOfTrips) {

                Map<Integer, TransitStopToStop> stopToStopMap = transitTrip.getStopToStopList();

                int sequenceNumber = 0;
                for (TransitStopToStop transitStopToStop : stopToStopMap.values()) {

                    //this nodes will be writen in XML list of nodes (This replicates some assignments of the variable...)

                    TransitStop origStop = transitStopToStop.getOrigTransitStop();
                    origStop.setPrintXMLNode(true);
                    TransitStop destStop = transitStopToStop.getDestTransitStop();
                    destStop.setPrintXMLNode(true);

                    TransformCoordinates tc = new TransformCoordinates();
                    tc.transformCoordinates(destStop);
                    tc.transformCoordinates(origStop);

                    int duration = transitStopToStop.getArrivalTime() - transitStopToStop.getDepartureTime();
                    double dx = transitStopToStop.getOrigTransitStop().getX() - transitStopToStop.getDestTransitStop().getX();
                    double dy = transitStopToStop.getOrigTransitStop().getY() - transitStopToStop.getDestTransitStop().getY();
                    double distance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
                    double freeFlowSpeed = distance / duration * 1.5;
                    //multiplied by 1.5 to allow trains get on time
                }
            }

            //sub=method to print nodes that were found in trip list
            //PrintWriter pw = new PrintWriter(new FileWriter("./output/XML/nodes.xml", true));

            pw.println("<nodes>");

            for (TransitStop transitStop : listOfStops) {

                if (transitStop.isPrintXMLNode()) {


                    pw.print("<node id=\"");
                    pw.print(transitStop.getStopId());
                    pw.print("\" x=\"");
                    pw.print(transitStop.getX());
                    pw.print("\" y=\"");
                    pw.print(transitStop.getY());
                    pw.println("\" />");

                }

            }
            pw.println("</nodes>");

            pw.println("<links>");

            //TODO The order of writing nodes and links needs to (but cannot) be changed

            for (TransitTrip transitTrip : listOfTrips) {


                Map<Integer, TransitStopToStop> stopToStopMap = transitTrip.getStopToStopList();

                int sequenceNumber = 0;
                for (TransitStopToStop transitStopToStop : stopToStopMap.values()) {


                    //this nodes will be writen in XML list of nodes (This replicates some assignments of the variable...)

                    int duration = transitStopToStop.getArrivalTime() - transitStopToStop.getDepartureTime();
                    double dx = transitStopToStop.getOrigTransitStop().getX() - transitStopToStop.getDestTransitStop().getX();
                    double dy = transitStopToStop.getOrigTransitStop().getY() - transitStopToStop.getDestTransitStop().getY();
                    double distance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
                    double freeFlowSpeed = distance / duration * 1.5;
                    //multiplied by 1.5 to allow trains get on time

                    String networkPrefix = "";

                    //todo need to generate an extra link to store the bus/train before getting first stop and assign them very high speed and typical length over vehicle length
                    if (transitStopToStop.hashCode()==0){

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
                    pw.print("\" from=\"");
                    pw.print(transitStopToStop.getOrigTransitStop().getStopId());
                    pw.print("\" to=\"");
                    pw.print(transitStopToStop.getDestTransitStop().getStopId());
                    pw.print("\" length=\"");
                    pw.print(distance);
                    pw.print("\" freespeed=\"");
                    pw.print(freeFlowSpeed);
                    pw.println("\" capacity=\"100000.0\" permlanes=\"1.0\" oneway=\"1\" modes=\"pt\" />");

                }

            }

            pw.println("</links>");


            pw.println("</network>");

            pw.flush();
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
