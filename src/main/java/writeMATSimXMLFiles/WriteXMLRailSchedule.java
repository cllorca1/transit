package writeMATSimXMLFiles;

import com.sun.media.jfxmediaimpl.MediaDisposer;
import importOsm.CSVFrequencyReader;
import javafx.scene.chart.PieChart;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import transitSystem.*;
import utils.TransitUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by carlloga on 16/12/16.
 */
public class WriteXMLRailSchedule {

    private ResourceBundle rb;
    private TransitDataContainer transitDataContainer;
    private static Logger logger = Logger.getLogger(WriteXMLRailSchedule.class);

    public WriteXMLRailSchedule(ResourceBundle rb, TransitDataContainer transitDataContainer) {
        this.rb = rb;
        this.transitDataContainer = transitDataContainer;
    }

    public void writeXMLRailSchedule(String vehicleFileName, String scheduleFileName) {

        ArrayList<TransitTrip> listOfTrips = transitDataContainer.getListOfTrips();

        String networkPrefix = WriteXMLRailNetwork.getNetworkPrefix();

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






            //todo loop for various vehicle descriptions

            Map<LineType, TransitVehicle> vehicleMap = transitDataContainer.getVehicleTypeMap();

            for (LineType lineType : LineType.values()) {

                TransitVehicle vehicle = vehicleMap.get(lineType);

                pw2.println("<vehicleType id=\"" + lineType.ordinal() + "\">");
                pw2.println("<description>" + lineType.toString() + "</description>");
                pw2.println("<capacity>");
                pw2.println("<seats persons=\"" + vehicle.getSeatCapacity() + "\"/>");
                pw2.println("<standingRoom persons=\"" + vehicle.getStandUpCapacity() + "\"/>");
                pw2.println("</capacity>");
                pw2.println("<length meter=\"" + vehicle.getLength() + "\"/>");
                pw2.println("</vehicleType>");


            }

            //end of vehicle file header
            pw.println("<transitStops>");


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
                        pw.print(transitTrip.getStopToStopList().get(i).getOrigTransitStop().getStopName().replace("\"","-" ));
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
                    pw.print(transitTrip.getStopToStopList().get(i).getDestTransitStop().getStopName().replace("\"","-" ));
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
                int absoluteDepartureInSeconds = Integer.parseInt(rb.getString("first.departure.hour"))*3600;
                int lastDepartureInSeconds = Integer.parseInt(rb.getString("last.departure.hour"))*3600;

                //this is because the stopToStop element stores departure and arrival time and not travel Time
                //int referenceDepartureInSeconds = transitTrip.getStopToStopList().get(0).getDepartureTime();
                int secondsOffset = -3600;


                for (int i : transitTrip.getStopToStopList().keySet()) {
                    // fill route profile (times)
                    TransitStopToStop stopToStop = transitTrip.getStopToStopList().get(i);

                    if (i == 0) {
                        //writes departure from the first segment to dest
                        stopToStop.getOrigTransitStop().getStopId();
                        long departureOffset = secondsOffset * 1000;
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
                        secondsOffset += previousStopToStop.getArrivalTime() - previousStopToStop.getDepartureTime();

                        //long arrivalOffset = (previousStopToStop.getArrivalTime() - referenceDepartureInSeconds - 3600) * 1000;
                        long arrivalOffset = secondsOffset * 1000;
                        DateTime arrival = new DateTime(arrivalOffset);

                        secondsOffset += 20;
                        long departureOffset = secondsOffset * 1000;
                        DateTime departure = new DateTime(departureOffset);

                        pw.print("<stop refId=\"");
                        pw.print(stopToStop.getOrigTransitStop().getStopId()+ "-" +  transitTrip.getTransitLine().getLineId());
                        pw.print("\" arrivalOffset=\"");
                        pw.print(printTime(arrival));
                        pw.print("\" departureOffset=\"");
                        pw.print(printTime(departure));
                        pw.println("\" awaitDeparture=\"true\"/>");


                        secondsOffset += stopToStop.getArrivalTime() - stopToStop.getDepartureTime();
                        long lastArrivalOffset = secondsOffset * 1000;
                        DateTime lastArrival = new DateTime(lastArrivalOffset);

                        pw.print("<stop refId=\"");
                        pw.print(stopToStop.getDestTransitStop().getStopId()+ "-" +  transitTrip.getTransitLine().getLineId());
                        pw.print("\" arrivalOffset=\"");
                        pw.print(printTime(lastArrival));
                        pw.println("\" awaitDeparture=\"true\"/>");


                    } else {
                        //writes arrival of previous segment to dest and departure from the current segment from orig
                        TransitStopToStop previousStopToStop = transitTrip.getStopToStopList().get(i - 1);
                        secondsOffset += previousStopToStop.getArrivalTime() - previousStopToStop.getDepartureTime();
                        long arrivalOffset = secondsOffset * 1000;
                        DateTime arrival = new DateTime(arrivalOffset);
                        secondsOffset += 20;
                        long departureOffset = secondsOffset * 1000;
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

                //logger.info("The route profile were generated in the schedule file");

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

                //logger.info("The routes were generated in the schedule file");

                pw.println("<departures>");
                // write  departures given the first one and the frequency
                // write vehicles associated with each departure (every departure a different vehicle)



                double headway;
                boolean assignFrequencies = Boolean.parseBoolean(rb.getString("assign.frequencies"));
                if (assignFrequencies) {
                    headway = transitTrip.getTransitLine().getHeadway();

                } else {
                    headway = 20;
                }


                int i = 0;
                DateTime departure = new DateTime(absoluteDepartureInSeconds * 1000).plusMinutes((int)Math.round(TransitUtil.rand.nextFloat()*headway));
                DateTime lastDeparture = new DateTime(lastDepartureInSeconds * 1000);
                //create departure until departure time is higher than the final departure
                while (departure.isBefore(lastDeparture)) {



                    //DateTime firstDeparture = new DateTime((absoluteDepartureTime + 60*20*i - 3600) * 1000);

                    pw.print("<departure id=\"");
                    pw.print(i);
                    pw.print("\" departureTime=\"");
                    pw.print(printTime(departure));
                    pw.print("\" vehicleRefId=\"");
                    pw.print("train" + transitTrip.getTransitLine().getLineId() + "-"  + i );
                    pw.println("\" />");


                    //todo assign here different vehicle types based on line type
                    pw2.print("<vehicle id=\"");
                    pw2.print("train" + transitTrip.getTransitLine().getLineId() + "-"  + i);
                    pw2.println("\" type=\""+ transitTrip.getTransitLine().getLineType().ordinal() + "\" />");

                    departure = departure.plusSeconds(((int) Math.round(headway)*60));
                    i++;

                }
                pw.println("</departures>");


                pw.println("</transitRoute>");
                pw.println("</transitLine>");
            }

            //logger.info("The departures and vehicles were generated in the schedule file");

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
