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
import java.util.ResourceBundle;

/**
 * Created by carlloga on 24/11/16.
 */
public class TravelTimeFromGoogle {

    private ResourceBundle rb;
    private ArrayList<TransitTrip> listOfTrips = new ArrayList<TransitTrip>();

    public TravelTimeFromGoogle(ResourceBundle rb){
        this.rb=rb;
    }

    public void getTimes(ArrayList<TransitLine> listOfLines) {

        //int queryNumber = 0;
        for (TransitLine transitLine : listOfLines) {
            //if (transitLine.isSubway()) {

                Map<Integer, TransitStop> stopList = transitLine.getStopList();
                int numberOfStops = stopList.size();

                int hours = 8;
                int minutes = 0;
                int tripDepartureTimeInSeconds = hours*3600+minutes*60;
                Map <Integer, TransitStopToStop> stopToStopList = new HashMap<Integer, TransitStopToStop>();


                DateTime timeOfDay = new DateTime(2016,12,29,hours,minutes);
                for (int i = 0; i < numberOfStops - 1; i++) {

                    int origSeq = i;
                    int destSeq = i + 1;

                    String origLatitude = stopList.get(origSeq).getLat();
                    String origLongitude = stopList.get(origSeq).getLon();
                    String destLatitude = stopList.get(destSeq).getLat();
                    String destLongitude = stopList.get(destSeq).getLon();

                    String origin = origLatitude + "," + origLongitude;
                    String destination = destLatitude + "," + destLongitude;


                    GetTransitTravelTime getTransitTravelTime = new GetTransitTravelTime(rb);

                    try {
                        DirectionsResult directionsResult = getTransitTravelTime.getTravelTime(origin, destination, timeOfDay);

                        System.out.println(stopList.get(origSeq).getStopName() + "  TO  " + stopList.get(destSeq).getStopName());

                        for (int k=0; k< directionsResult.routes[0].legs.length; k++){

                            for (int j = 0; j < directionsResult.routes[0].legs[k].steps.length; j++) {

                            if (directionsResult.routes[0].legs[k].steps[j].travelMode.equals(TravelMode.TRANSIT)) {
                                if (directionsResult.routes[0].legs[k].steps[j].transitDetails.line.vehicle.type.equals(VehicleType.TRAM)) {
                                    //String vehicleName = directionsResult.routes[0].legs[k].steps[j].transitDetails.line.vehicle.name;
                                    //String departureStop = directionsResult.routes[0].legs[k].steps[j].transitDetails.departureStop.name;
                                    //String arrivalStop = directionsResult.routes[0].legs[k].steps[j].transitDetails.arrivalStop.name;
                                    DateTime departureTime = directionsResult.routes[0].legs[k].steps[j].transitDetails.departureTime;
                                    int departureTimeInSeconds = departureTime.getSecondOfDay();

                                    DateTime arrivalTime = directionsResult.routes[0].legs[k].steps[j].transitDetails.arrivalTime;
                                    timeOfDay = arrivalTime;
                                    int arrivalTimeInSeconds = arrivalTime.getSecondOfDay();
                                    //String lineName = directionsResult.routes[0].legs[k].steps[j].transitDetails.line.shortName;

                                    //time adds exactly tha same as duration + stop time avg. (20 seconds)
                                    //minutes += Math.round((directionsResult.routes[0].legs[k].steps[j].duration.inSeconds+20)/60);

//                                    System.out.println(queryNumber + ";" + vehicleName + ";" + departureStop +
//                                            ";" + arrivalStop + ";" + departureTime + ";" + arrivalTime + ";" + lineName + ";" + directionsResult.routes[0].legs[k].steps[j].duration.inSeconds);
//                                    queryNumber++;

                                    TransitStopToStop transitStopToStop = new TransitStopToStop(stopList.get(origSeq), stopList.get(destSeq), arrivalTimeInSeconds, departureTimeInSeconds);
                                    stopToStopList.put(origSeq, transitStopToStop);
                                }
                            }
                        }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

                TransitTrip transitTrip = new TransitTrip(transitLine, tripDepartureTimeInSeconds, stopToStopList);
                listOfTrips.add(transitTrip);
                //System.out.println(listOfTrips.size());

            //}


        }
    }

    public ArrayList<TransitTrip> getListOfTrips() {
        return listOfTrips;
    }
}
