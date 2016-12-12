package geocoding;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.GeocodingResult;
import transitSystem.TransitStop;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by carlloga on 5/12/16.
 */
public class Geocode {

    public void geocodeAdress() {

        ArrayList<String> listOfZones = new ArrayList<String>();

        BufferedReader br = null;
        String line = "";

        try {

            br = new BufferedReader(new FileReader("C:/Models/bikeSharing/geocoding/" + "zonesBerlin.csv"));

            while ((line = br.readLine()) != null) {


                String[] row = line.split(";");

                String zoneName = row[0];

                listOfZones.add(zoneName);
            }

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

        //System.out.println(listOfZones.size());



            try {

                PrintWriter pw = new PrintWriter(new FileWriter("C:/Models/bikeSharing/geocoding/" + "zonesBerlinCoordinates.csv", true));
                pw.println("zoneName, lat, lng");

                for (String zoneName : listOfZones) {


                    GeoApiContext context = new GeoApiContext().setApiKey("YOUR_KEY");


                zoneName = zoneName + "+Berlin";


                GeocodingResult[] result = GeocodingApi.newRequest(context)
                        .address(zoneName).
                                await();

                pw.println(zoneName + "," + result[0].geometry.location.lat + "," + result[0].geometry.location.lng);

                }
                pw.flush();
                pw.close();

            } catch (IOException e) {
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();
            }



    }

}
