package geocoding;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.*;
import org.joda.time.DateTime;
import travelTimeFromGoogle.GetMatrixResult;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by carlloga on 15/2/17.
 */
public class GetTravelTimeBetweenPoints {
    private ResourceBundle rb;

    public GetTravelTimeBetweenPoints(ResourceBundle rb){
        this.rb=rb;
    }

    public void getBicycleTimes() throws IOException {



        String fileName = "./input/callABikeBerlin/koelnODCoordinates.csv";
        String newFileName = "./input/callABikeBerlin/koelnODTravelTimes.csv";

        PrintWriter pw = new PrintWriter(new FileWriter(newFileName, true));
        pw.println("odName; travelTime(s); travelDistance(m)");

        BufferedReader br = null;
        String line = "";

        try {

            FileReader fr = new FileReader(fileName);

            br = new BufferedReader(fr);

            int lineIndex = 0;
            while ((line = br.readLine()) != null ) {

                if (lineIndex > 0) {
                    String[] row = line.split(";");

                    String ODname = row[6];
                    float origLon = Float.parseFloat(row[1].replace(',', '.'));
                    float origLat = Float.parseFloat(row[2].replace(',', '.'));
                    float destLon = Float.parseFloat(row[4].replace(',', '.'));
                    float destLat = Float.parseFloat(row[5].replace(',', '.'));

                    String origin = origLat + "," + origLon;
                    String destination = destLat + "," + destLon;


                    if (lineIndex < 2500) {

                        GeoApiContext context = new GeoApiContext().setApiKey(rb.getString("api.key.katrin"));
                        DirectionsResult directionsResult = new DirectionsResult();
                        DateTime timeOfDay = new DateTime(2017, 03, 13, 9, 0);

                        try {

                            directionsResult = DirectionsApi.newRequest(context)
                                    .units(Unit.METRIC)
                                    .mode(TravelMode.BICYCLING)
                                    .origin(origin)
                                    .departureTime(timeOfDay)
                                    .destination(destination).await();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        long travelTime = directionsResult.routes[0].legs[0].duration.inSeconds;
                        long travelDistance = directionsResult.routes[0].legs[0].distance.inMeters;

//                        System.out.println("trip duration= " + travelTime);
//                        System.out.println("trip distance= " + travelDistance);

                        pw.println(ODname + ";" +  travelTime + ";" + travelDistance);

                    }
                }

                lineIndex++;

            }
            pw.flush();
            pw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
