package geocoding;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.GeocodingResult;
import transitSystem.TransitStop;

import java.io.*;
import java.util.ArrayList;
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

        ArrayList<String> listOfZones = new ArrayList<String>();


        BufferedReader br = null;
        String line = "";

        try {

            FileReader fr = new FileReader("C:/Models/bikeSharing/geocoding/" + "zonesBerlin.csv");

            br = new BufferedReader(fr);

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


                GeoApiContext context = new GeoApiContext().setApiKey(rb.getString("api.key"));



                zoneName = zoneName + "+Berlin+Deutschland";


                GeocodingResult[] result = GeocodingApi.newRequest(context)
                        .address(zoneName).
                                language("deutsch").
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
