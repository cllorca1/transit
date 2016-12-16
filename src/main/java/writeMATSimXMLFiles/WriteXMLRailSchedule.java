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

    public void writeXMLRailSchedule(ArrayList<TransitStop> listOfStops, ArrayList<TransitTrip> listOfTrips) {

        try {

            PrintWriter pw = new PrintWriter(new FileWriter("./output/XML/schedule.xml", true));
            PrintWriter pw2 = new PrintWriter(new FileWriter("./output/XML/vehicles.xml", true));

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
            for (TransitStop transitStop : listOfStops) {
                if (transitStop.isPrintXMLNode()) {
                    //write xml line for stop, including name and networkLink
                    pw.print("<stopFacility id=\"");
                    pw.print(transitStop.getStopId());
                    pw.print("\" x=\"");
                    pw.print(transitStop.getX());
                    pw.print("\" y=\"");
                    pw.print(transitStop.getY());
                    pw.print("\" linkRefId=\"");
                    pw.print(transitStop.getNetworkLink());
                    pw.print("\" name=\"");
                    pw.print(transitStop.getStopName());
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
                        pw.print(stopToStop.getOrigTransitStop().getStopId());
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
                        pw.print(stopToStop.getOrigTransitStop().getStopId());
                        pw.print("\" arrivalOffset=\"");
                        pw.print(printTime(arrival));
                        pw.print("\" departureOffset=\"");
                        pw.print(printTime(departure));
                        pw.println("\" awaitDeparture=\"true\"/>");


                        long lastArrivalOffset = (stopToStop.getArrivalTime() - absoluteDepartureTime - 3600) * 1000;
                        DateTime lastArrival = new DateTime(lastArrivalOffset);

                        pw.print("<stop refId=\"");
                        pw.print(stopToStop.getDestTransitStop().getStopId());
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
                        pw.print(stopToStop.getOrigTransitStop().getStopId());
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
                        stopToStop.getOrigTransitStop().getNetworkLink();
                        pw.print("<link refId=\"");
                        pw.print(stopToStop.getOrigTransitStop().getNetworkLink());
                        pw.println("\" />");
                    }
                    pw.print("<link refId=\"");
                    pw.print(stopToStop.getDestTransitStop().getNetworkLink());
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
                    pw.print("train" + i);
                    pw.println("\" />");

                    pw2.print("<vehicle id=\"");
                    pw2.print("train" + i);
                    pw2.println("\" type=\"1\"");
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
