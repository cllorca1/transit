package travelTimeFromGoogle;

import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import com.google.maps.model.VehicleType;
import org.joda.time.DateTime;
import transitSystem.TransitLine;
import transitSystem.TransitStop;
import transitSystem.TransitStopToStop;
import transitSystem.TransitTrip;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by carlloga on 15/2/17.
 */
public class TravelTimeMatrixFromGoogle {

    private ResourceBundle rb;
    private ArrayList<TransitTrip> listOfTrips = new ArrayList<TransitTrip>();

    public TravelTimeMatrixFromGoogle(ResourceBundle rb) {
        this.rb = rb;
    }

    public void getTimesFromMatrix(ArrayList<TransitLine> listOfLines) {

        //int queryNumber = 0;
        for (TransitLine transitLine : listOfLines) {
            //if (transitLine.isSubway()) {

            Map<Integer, TransitStop> stopList = transitLine.getStopList();
            int numberOfStops = stopList.size();

            int hours = 8;
            int minutes = 0;
            int tripDepartureTimeInSeconds = hours * 3600 + minutes * 60;
            Map<Integer, TransitStopToStop> stopToStopList = new HashMap<Integer, TransitStopToStop>();


            DateTime timeOfDay = new DateTime(2017, 03, 13, hours, minutes);

            String[] origins = new String[numberOfStops];

            for (int i = 0; i < numberOfStops - 1; i++) {

                String latitude = stopList.get(i).getLat();
                String longitude = stopList.get(i).getLon();


                origins[i] = latitude + "," + longitude;

            }

            GetMatrixResult getMatrixResult = new GetMatrixResult(rb);

            try {

                long departureTimeInSeconds = 8*60*60;
                DistanceMatrix distanceMatrix = getMatrixResult.getTravelTime(origins, origins, timeOfDay);
                for (int i = 0; i < numberOfStops - 1; i++) {


                    long duration = distanceMatrix.rows[i].elements[i+1].duration.inSeconds;
                    long arrivalTimeInSeconds = departureTimeInSeconds + duration;

                    TransitStopToStop transitStopToStop = new TransitStopToStop(stopList.get(i), stopList.get(i+1), (int)arrivalTimeInSeconds, (int)departureTimeInSeconds);
                    stopToStopList.put(i, transitStopToStop);

                    departureTimeInSeconds = arrivalTimeInSeconds;

                }

                TransitTrip transitTrip = new TransitTrip(transitLine, tripDepartureTimeInSeconds, stopToStopList);
                listOfTrips.add(transitTrip);

                String matrixFileName = "./output/matrices" + transitLine.getLineId() + "matrix.csv";
                PrintWriter pw = new PrintWriter(new FileWriter(matrixFileName, true));
                pw.print("origin");
                for (int i = 0; i < numberOfStops - 1; i++) {
                pw.print(";" + stopList.get(i).getStopId());
                }
                pw.println();
                for (int i = 0; i < numberOfStops - 1; i++) {
                    pw.print(stopList.get(i).getStopId());
                    for (int j = 0; j < numberOfStops - 1; j++) {

                        pw.print(";" + distanceMatrix.rows[i].elements[j].duration.inSeconds);
                    }
                    pw.println();
                }

                pw.flush();
                pw.close();


            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }




    public ArrayList<TransitTrip> getListOfTrips() {
        return listOfTrips;
    }
}


