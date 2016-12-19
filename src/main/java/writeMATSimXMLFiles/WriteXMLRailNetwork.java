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

    public void writeXMLRailNetwork(ArrayList<TransitStop> listOfStops, ArrayList<TransitTrip> listOfTrips) {

        //sub-method to write links as stopToStop connectors
        int networkLink = 0;
        try {

            PrintWriter pw = new PrintWriter(new FileWriter("./output/XML/links.xml", true));

            pw.println("<links>");

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
                    double freeFlowSpeed = distance / duration*1.5;
                    //multiplied by 1.5 to allow trains get on time

                    pw.print("<link id=\"pt");
                    pw.print(networkLink);
                    //todo check if this works to assign twice the first link and then the previous link until the end of the trip
                    //todo need to solve this problem, probably manually
                    //todo ASSIGN NETWORKLINK TO STOPTOSTOP OBJECT
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
            pw.flush();
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //sub=method to print nodes that were found in trip list
        try {

            PrintWriter pw = new PrintWriter(new FileWriter("./output/XML/nodes.xml", true));

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
            pw.flush();
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
