package travelTimeFromGoogle;

import com.google.maps.*;
import com.google.maps.model.*;

import org.joda.time.DateTime;

import java.util.ResourceBundle;


/**
 * Created by carlloga on 11/11/16.
 */
public class GetTransitTravelTime {

    private ResourceBundle rb;

    public GetTransitTravelTime(ResourceBundle rb) {
        this.rb=rb;
    }

    public DirectionsResult getTravelTime(String origin, String destination, DateTime timeOfDay){
        GeoApiContext context = new GeoApiContext().setApiKey(rb.getString("api.key"));
        DirectionsResult directionsResult = new DirectionsResult();

      try {

          directionsResult = DirectionsApi.newRequest(context)
                  .units(Unit.METRIC)
                  .mode(TravelMode.TRANSIT)
                  .transitMode(TransitMode.BUS)
                  .transitRoutingPreference(TransitRoutingPreference.LESS_WALKING)
                  .origin(origin)
                  .departureTime(timeOfDay)
                  .destination(destination).await();

        } catch (Exception e) {
            e.printStackTrace();
        }



        return directionsResult;

    }
}
