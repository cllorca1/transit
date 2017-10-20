package writeMATSimXMLFiles;

import org.apache.log4j.Logger;
import transitSystem.TransitLine;
import transitSystem.TransitStop;
import transitSystem.TransitTrip;

import java.util.ArrayList;


/**
 * Created by carlloga on 19/12/16.
 */
public class AssignOpposing {

    private static Logger logger = Logger.getLogger(AssignOpposing.class);

    public void assignOpposingDirection(ArrayList<TransitTrip> listOfTrips){

        for (TransitTrip transitTrip : listOfTrips){

            for (TransitTrip opposingTrip : listOfTrips){


                String origin1 = transitTrip.getStopToStopList().get(0).getOrigTransitStop().getStopName();
                String dest1 = transitTrip.getStopToStopList().get(transitTrip.getStopToStopList().size()-1).getDestTransitStop().getStopName();
                String origin2 = opposingTrip.getStopToStopList().get(0).getOrigTransitStop().getStopName();
                String dest2 = opposingTrip.getStopToStopList().get(opposingTrip.getStopToStopList().size()-1).getDestTransitStop().getStopName();


                if( origin1.equals(dest2) & origin2.equals(dest1) ){

                    transitTrip.setOpposingTrip(opposingTrip);
                    //System.out.println(origin1 + "-" + origin2);

                }

            }


        }

        logger.info("Opposing direction lines assigned");

    }
}
