package travelTimeFromGoogle;

import com.google.maps.DirectionsApi;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.*;
import org.joda.time.DateTime;

import java.util.ResourceBundle;

/**
 * Created by carlloga on 15/2/17.
 */
public class GetMatrixResult {

    private ResourceBundle rb;

    public GetMatrixResult(ResourceBundle rb) {
        this.rb = rb;
    }

    public DistanceMatrix getTravelTime(String[] origins, String[] destinations, DateTime timeOfDay) {

        GeoApiContext context = new GeoApiContext().setApiKey(rb.getString("api.key"));
        DistanceMatrix distanceMatrixResult = null;

        try {

            distanceMatrixResult = DistanceMatrixApi.getDistanceMatrix(context, origins, destinations)
                    .mode(TravelMode.TRANSIT).
                            transitModes(TransitMode.BUS).
                            departureTime(timeOfDay).await();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return distanceMatrixResult;

    }
}
