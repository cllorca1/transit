package travelTimeFromGoogle;

import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.google.maps.model.VehicleType;
import org.joda.time.DateTime;
import transitSystem.TransitLine;
import transitSystem.TransitStop;
import transitSystem.TransitStopToStop;
import transitSystem.TransitTrip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by carlloga on 29/11/16.
 */
public class LineFrequency {

    private ArrayList<TransitTrip> listOfTrips = new ArrayList<TransitTrip>();

    public void getLineFrequency(ArrayList<TransitLine> listOfLines) {

        for (TransitLine transitLine : listOfLines) {
            //if (transitLine.getLineName().equals("25")) {

                Map<Integer, TransitStop> stopList = transitLine.getStopList();

                int hours = 8;
                int minutes = 0;

                //this parameter requires extra calibration
                int queryFrequency = 5;


                int origSeq = 0;

                int destSeq = 4;

                String origLatitude = stopList.get(origSeq).getLat();
                String origLongitude = stopList.get(origSeq).getLon();
                String destLatitude = stopList.get(destSeq).getLat();
                String destLongitude = stopList.get(destSeq).getLon();

                String origin = origLatitude + "," + origLongitude;
                String destination = destLatitude + "," + destLongitude;

                //int[] departureList = new int[10];
                ArrayList<Integer> departures = new ArrayList<Integer>();

                for (int query = 0; query < 10; query++) {

                    minutes = query * queryFrequency;
                    //repeats the query at 0, 2, 4, 6 etc.

                    DateTime timeOfDay = new DateTime(2016, 11, 29, hours, minutes);

                    GetTransitTravelTime getTransitTravelTime = new GetTransitTravelTime();

                    try {
                        DirectionsResult directionsResult = getTransitTravelTime.getTravelTime(origin, destination, timeOfDay);

                        //System.out.println(stopList.get(origSeq).getStopName() + "  TO  " + stopList.get(destSeq).getStopName());

                        for (int k = 0; k < directionsResult.routes[0].legs.length; k++) {
                            for (int j = 0; j < directionsResult.routes[0].legs[k].steps.length; j++) {
                                if (directionsResult.routes[0].legs[k].steps[j].travelMode.equals(TravelMode.TRANSIT)) {
                                    if (directionsResult.routes[0].legs[k].steps[j].transitDetails.line.vehicle.type.equals(VehicleType.COMMUTER_TRAIN)) {
                                        String lineName = directionsResult.routes[0].legs[k].steps[j].transitDetails.line.shortName;
                                        if (lineName.equals(transitLine.getLineName())) {
                                            DateTime departureTime = directionsResult.routes[0].legs[k].steps[j].transitDetails.departureTime;
                                            //departureList[query] = departureTime.getSecondOfDay();
                                            departures.add(departureTime.getSecondOfDay());
                                            System.out.println("Query = " + query + ": Line " + transitLine.getLineName() + " departs at " + departureTime.getSecondOfDay());
                                        } else {
                                            //departureList[query] = 0;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                int[] departureList = new int[departures.size()];
                int i = 0;
                for (int departure : departures) {
                    departureList[i] = departure;
                    i++;
                }
                float lineFrequencyInSeconds = 0;
                i = 0;
                for (int j = 0; j < departures.size() - 1; j++) {
                    if (departureList[j + 1] != departureList[j]) {
                        lineFrequencyInSeconds += departureList[j + 1] - departureList[j];
                        i++;
                    }
                }

                if (i>1) {
                    lineFrequencyInSeconds = lineFrequencyInSeconds / i;
                    TransitTrip transitTrip = new TransitTrip(departureList[0], Math.round(lineFrequencyInSeconds), transitLine);
                    listOfTrips.add(transitTrip);
                }
            //}

        }
    }


    public ArrayList<TransitTrip> getListOfTrips() {
        return listOfTrips;
    }

}
