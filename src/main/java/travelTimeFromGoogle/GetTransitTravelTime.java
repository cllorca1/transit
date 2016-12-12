package travelTimeFromGoogle;

import com.google.maps.*;
import com.google.maps.model.*;

import org.joda.time.DateTime;


/**
 * Created by carlloga on 11/11/16.
 */
public class GetTransitTravelTime {

    public DirectionsResult getTravelTime(String origin, String destination, DateTime timeOfDay){
        GeoApiContext context = new GeoApiContext().setApiKey("YOUR_KEY");
        DirectionsResult directionsResult = new DirectionsResult();


      try {


/*
          GeocodingResult[] origPlace = GeocodingApi.newRequest(context)
                  .region("Munich")
                  .language("deutsch")
                  .resultType(AddressType.TRANSIT_STATION)
                  .address("milbertshofen metro").await();

          System.out.println(origPlace.length);
          System.out.println(origPlace[0].formattedAddress);
          System.out.println(origPlace[0].placeId);
          System.out.println(origPlace[0].geometry.location);
          for (AddressType type : origPlace[0].types) {
              System.out.println(type.toString());
          }

          GeocodingResult[] destPlace = GeocodingApi.newRequest(context)
                  .language("german")
                  .region("Deutschland")
                  .resultType(AddressType.TRANSIT_STATION)
                  .address("marienplatz metro").await();

          System.out.println(destPlace.length);
          System.out.println(destPlace[0].formattedAddress);
          System.out.println(destPlace[0].placeId);
          System.out.println(destPlace[0].geometry.location);
          for (AddressType type : origPlace[0].types) {
              System.out.println(type.toString());
          }

*/



          directionsResult = DirectionsApi.newRequest(context)
                  .units(Unit.METRIC)
                  .mode(TravelMode.TRANSIT)
                  .origin(origin)
                  .departureTime(timeOfDay)
                  .destination(destination).await();


          /*System.out.println(result.routes[0].legs[0].duration);
          int numberSteps = result.routes[0].legs[0].steps.length;
          System.out.println("Number of steps = " + numberSteps);
          for (int i=0; i<numberSteps;i++){
              if(result.routes[0].legs[0].steps[i].travelMode.equals(TravelMode.TRANSIT)){
                  System.out.println(result.routes[0].legs[0].steps[i].transitDetails.line.vehicle.name);
                  System.out.println(result.routes[0].legs[0].steps[i].transitDetails.departureStop.name);
                  System.out.println(result.routes[0].legs[0].steps[i].transitDetails.arrivalStop.name);
                  System.out.println(result.routes[0].legs[0].steps[i].duration.toString());
                  System.out.println(result.routes[0].legs[0].steps[i].transitDetails.numStops);
                  System.out.println(result.routes[0].legs[0].steps[i].transitDetails.line.shortName);
              }

              else{
                  System.out.println(result.routes[0].legs[0].steps[i].travelMode.toString());
              }
          }
          */


        } catch (Exception e) {
            e.printStackTrace();
        }



        return directionsResult;

    }
}
