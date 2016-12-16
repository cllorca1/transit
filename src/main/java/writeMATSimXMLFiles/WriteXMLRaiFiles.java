package writeMATSimXMLFiles;

import transitSystem.TransitLine;
import transitSystem.TransitStop;
import transitSystem.TransitTrip;

import java.util.ArrayList;

/**
 * Created by carlloga on 16/12/16.
 */
public class WriteXMLRaiFiles {

    public void writeXMLFiles(ArrayList<TransitStop> listOfStops, ArrayList<TransitLine> listOfLines, ArrayList<TransitTrip> listOfTrips){

        WriteXMLRailNetwork writeXMLRailNetwork = new WriteXMLRailNetwork();
        writeXMLRailNetwork.writeXMLRailNetwork(listOfStops, listOfTrips);

        WriteXMLRailSchedule writeXMLRailSchedule = new WriteXMLRailSchedule();
        writeXMLRailSchedule.writeXMLRailSchedule(listOfStops, listOfTrips);


    }

}
