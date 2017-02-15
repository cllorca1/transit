package writeMATSimXMLFiles;

import javafx.scene.chart.PieChart;
import org.joda.time.DateTime;
import transitSystem.TransitStop;
import transitSystem.TransitStopToStop;
import transitSystem.TransitTrip;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by carlloga on 16/12/16.
 */
public class WriteXMLRailSchedule {

    public void writeXMLRailSchedule(ArrayList<TransitTrip> listOfTrips, String vehicleFileName, String scheduleFileName) {

        WriteXMLRailNetwork writeXMLRailNetwork = new WriteXMLRailNetwork();
        String networkPrefix = writeXMLRailNetwork.getNetworkPrefix();

        try {

            PrintWriter pw = new PrintWriter(new FileWriter(scheduleFileName, false));
            PrintWriter pw2 = new PrintWriter(new FileWriter(vehicleFileName, false));

            //schedule file
            pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            pw.println("<!DOCTYPE transitSchedule SYSTEM \"http://www.matsim.org/files/dtd/transitSchedule_v1.dtd\">");

            pw.println("<transitSchedule>");

            //vehicle file header
            pw2.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            pw2.println("<vehicleDefinitions xmlns=\"http://www.matsim.org/files/dtd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.matsim.org/files/dtd http://www.matsim.org/files/dtd/vehicleDefinitions_v1.0.xsd\">");
            pw2.println("<vehicleType id=\"1\">");
            pw2.println("<description>S-Bahn</description>");
            pw2.println("<capacity>");
            pw2.println("<seats persons=\"150\"/>");
            pw2.println("<standingRoom persons=\"200\"/>");
            pw2.println("</capacity>");
            pw2.println("<length meter=\"150.0\"/>");
            pw2.println("</vehicleType>");
            //end of vehicle file header


            pw.println("<transitStops>");
            //TODO loop along transit trip transitStopToStop instead (?)
            for (TransitTrip transitTrip : listOfTrips){
                for (int i : transitTrip.getStopToStopList().keySet()){
                    if (i==0){
                        pw.print("<stopFacility id=\"");
                        pw.print(transitTrip.getStopToStopList().get(i).getOrigTransitStop().getStopId()+ "-" +  transitTrip.getTransitLine().getLineId());
                        pw.print("\" x=\"");
                        pw.print(transitTrip.getStopToStopList().get(i).getOrigTransitStop().getX());
                        pw.print("\" y=\"");
                        pw.print(transitTrip.getStopToStopList().get(i).getOrigTransitStop().getY());
                        //discontinued
                        //pw.print("\" linkRefId=\"pt"+ networkPrefix);
                        // writes as link the last link of the other direction
                        //int lastOpposingStopSequence = transitTrip.getOpposingTrip().getStopToStopList().size();
                        //pw.print(transitTrip.getOpposingTrip().getStopToStopList().get(lastOpposingStopSequence-1).getNetworkLink());
                        //new version
                        // writes as link 0 the siding link
                        pw.print("\" linkRefId=\"ptExtra"+ networkPrefix);
                        pw.print(transitTrip.getSidingLink());
                        pw.print("\" name=\"");
                        pw.print(transitTrip.getStopToStopList().get(i).getOrigTransitStop().getStopName());
                        pw.println("\" />");
                    }
                    pw.print("<stopFacility id=\"");
                    pw.print(transitTrip.getStopToStopList().get(i).getDestTransitStop().getStopId() + "-" +  transitTrip.getTransitLine().getLineId());
                    pw.print("\" x=\"");
                    pw.print(transitTrip.getStopToStopList().get(i).getDestTransitStop().getX());
                    pw.print("\" y=\"");
                    pw.print(transitTrip.getStopToStopList().get(i).getDestTransitStop().getY());
                    pw.print("\" linkRefId=\"pt" + networkPrefix);
                    pw.print(transitTrip.getStopToStopList().get(i).getNetworkLink());
                    pw.print("\" name=\"");
                    pw.print(transitTrip.getStopToStopList().get(i).getDestTransitStop().getStopName());
                    pw.println("\" />");
                }
            }

            pw.println("</transitStops>");

            for (TransitTrip transitTrip : listOfTrips) {
                pw.println("<transitLine id=\">" + transitTrip.getTransitLine().getLineId() + "\">");
                pw.println("<transitRoute id=\">" + transitTrip.getTransitLine().getFromStop() + "-" +
                        transitTrip.getTransitLine().getToStop() + "\">");
                pw.println("<transportMode>\"pt\"</transportMode>");
                pw.println("<routeProfile>");
                int absoluteDepartureTime = transitTrip.getStopToStopList().get(0).getDepartureTime();



                for (int i : transitTrip.getStopToStopList().keySet()) {
                    // fill route profile (times)
                    TransitStopToStop stopToStop = transitTrip.getStopToStopList().get(i);

                    if (i == 0) {
                        //writes departure from the first segment to dest
                        stopToStop.getOrigTransitStop().getStopId();
                        long departureOffset = (stopToStop.getDepartureTime() - absoluteDepartureTime - 3600) * 1000;
                        DateTime departure = new DateTime(departureOffset);

                        pw.print("<stop refId=\"");
                        pw.print(stopToStop.getOrigTransitStop().getStopId() + "-" +  transitTrip.getTransitLine().getLineId());
                        pw.print("\" departureOffset=\"");
                        pw.print(printTime(departure));
                        pw.println("\" awaitDeparture=\"true\"/>");


                    } else if (i == transitTrip.getStopToStopList().size() - 1) {
                        //writes the arrival of previous segment to dest and departure from the current segment from orig
                        //AND writes the arrival of this segment to dest
                        TransitStopToStop previousStopToStop = transitTrip.getStopToStopList().get(i - 1);
                        long arrivalOffset = (previousStopToStop.getArrivalTime() - absoluteDepartureTime - 3600) * 1000;
                        DateTime arrival = new DateTime(arrivalOffset);
                        long departureOffset = (stopToStop.getDepartureTime() - absoluteDepartureTime - 3600) * 1000;
                        DateTime departure = new DateTime(departureOffset);

                        pw.print("<stop refId=\"");
                        pw.print(stopToStop.getOrigTransitStop().getStopId()+ "-" +  transitTrip.getTransitLine().getLineId());
                        pw.print("\" arrivalOffset=\"");
                        pw.print(printTime(arrival));
                        pw.print("\" departureOffset=\"");
                        pw.print(printTime(departure));
                        pw.println("\" awaitDeparture=\"true\"/>");


                        long lastArrivalOffset = (stopToStop.getArrivalTime() - absoluteDepartureTime - 3600) * 1000;
                        DateTime lastArrival = new DateTime(lastArrivalOffset);

                        pw.print("<stop refId=\"");
                        pw.print(stopToStop.getDestTransitStop().getStopId()+ "-" +  transitTrip.getTransitLine().getLineId());
                        pw.print("\" arrivalOffset=\"");
                        pw.print(printTime(lastArrival));
                        pw.println("\" awaitDeparture=\"true\"/>");


                    } else {
                        //writes arrival of previous segment to dest and departure from the current segment from orig
                        TransitStopToStop previousStopToStop = transitTrip.getStopToStopList().get(i - 1);
                        long arrivalOffset = (previousStopToStop.getArrivalTime() - absoluteDepartureTime - 3600) * 1000;
                        DateTime arrival = new DateTime(arrivalOffset);
                        long departureOffset = (stopToStop.getDepartureTime() - absoluteDepartureTime - 3600) * 1000;
                        DateTime departure = new DateTime(departureOffset);

                        pw.print("<stop refId=\"");
                        pw.print(stopToStop.getOrigTransitStop().getStopId()+ "-" +  transitTrip.getTransitLine().getLineId());
                        pw.print("\" arrivalOffset=\"");
                        pw.print(printTime(arrival));
                        pw.print("\" departureOffset=\"");
                        pw.print(printTime(departure));
                        pw.println("\" awaitDeparture=\"true\"/>");

                    }

                }
                pw.println("</routeProfile>");
                pw.println("<route>");
                for (int i : transitTrip.getStopToStopList().keySet()) {
                    // fill route profile (times)
                    TransitStopToStop stopToStop = transitTrip.getStopToStopList().get(i);

                    if (i == 0) {
                        //writes departure from the first segment to dest
                        //todo may need to change to accept non bi-directional lines
                        //discontinued
                        //pw.print("<link refId=\"pt" + networkPrefix);
                        //int lastOpposingStopSequence = transitTrip.getOpposingTrip().getStopToStopList().size();
                        //pw.print(transitTrip.getOpposingTrip().getStopToStopList().get(lastOpposingStopSequence-1).getNetworkLink());
                        //pw.print(stopToStop.getNetworkLink());
                        //new version
                        pw.print("<link refId=\"ptExtra" + networkPrefix);
                        pw.print(transitTrip.getSidingLink());
                        pw.println("\" />");
                    }
                    pw.print("<link refId=\"pt" + networkPrefix);

                    pw.print(stopToStop.getNetworkLink());
                    pw.println("\" />");

                }
                pw.println("</route>");
                pw.println("<departures>");
                // write  departures given the first one and the frequency
                // write vehicles associated with each departure (every departure a different vehicle)

                //create 30 depatures every 20 mins
                for (int i = 0; i < 30; i++) {
                    DateTime firstDeparture = new DateTime((absoluteDepartureTime + 60*20*i - 3600) * 1000);
                    pw.print("<departure id=\"");
                    pw.print(i);
                    pw.print("\" departureTime=\"");
                    pw.print(printTime(firstDeparture));
                    pw.print("\" vehicleRefId=\"");
                    pw.print("train" + transitTrip.getTransitLine().getLineId() + "-"  + i );
                    pw.println("\" />");

                    pw2.print("<vehicle id=\"");
                    pw2.print("train" + transitTrip.getTransitLine().getLineId() + "-"  + i);
                    pw2.println("\" type=\"1\" />");
                }
                pw.println("</departures>");


                pw.println("</transitRoute>");
                pw.println("</transitLine>");
            }

            pw.println("</transitSchedule>");
            pw2.println("</vehicleDefinitions>");

            pw.flush();
            pw.close();

            pw2.flush();
            pw2.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String printTime(DateTime dateTime){

        String dateTimeString = dateTime.getHourOfDay() + ":" + dateTime.getMinuteOfHour() + ":" + dateTime.getSecondOfMinute();
        return dateTimeString;
    }
}
