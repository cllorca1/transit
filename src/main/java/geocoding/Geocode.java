package geocoding;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.GeocodingResult;
import transitSystem.TransitStop;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * Created by carlloga on 5/12/16.
 */
public class Geocode {

    private ResourceBundle rb;

    public Geocode(ResourceBundle rb) {
        this.rb = rb;
    }

    public void geocodeAdress() {

        Map<Integer, String> listOfZones = new HashMap<Integer, String>();


        BufferedReader br = null;
        String line = "";

        try {

            FileReader fr = new FileReader("C:/Models/bikeSharing/geocoding/zones/" + "allZones.csv");

            br = new BufferedReader(fr);

            while ((line = br.readLine()) != null) {

                String[] row = line.split(",");

                int zoneNumber = Integer.parseInt(row[0]);
                String zoneName = row[1] + row[2];

                listOfZones.put(zoneNumber,zoneName);
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

            PrintWriter pw = new PrintWriter(new FileWriter("C:/Models/bikeSharing/geocoding/zones" + "allZonesCoordinates.csv", true));
            pw.println("zoneName, lat, lng");

            for (int i : listOfZones.keySet()) {

                String zoneName = listOfZones.get(i);

                GeoApiContext context = new GeoApiContext().setApiKey(rb.getString("api.key"));




                GeocodingResult[] result = GeocodingApi.newRequest(context)
                        .address(zoneName).
                                language("deutsch").
                                await();

                if (result.length > 0) {

                    pw.println(i + "," + zoneName + "," + result[0].geometry.location.lat + "," + result[0].geometry.location.lng);

                } else {
                    pw.println(i + "," + zoneName + ","+ ","+ ",notFound" );
                }
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
