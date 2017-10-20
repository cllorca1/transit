package writeMATSimXMLFiles;

import transitSystem.TransitLine;
import transitSystem.TransitStop;
import transitSystem.TransitTrip;

import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by carlloga on 16/12/16.
 */
public class WriteXMLRaiFiles {

    private ResourceBundle rb;

    public WriteXMLRaiFiles(ResourceBundle rb) {
        this.rb = rb;
    }

    public void writeXMLFiles(ArrayList<TransitStop> listOfStops, ArrayList<TransitLine> listOfLines, ArrayList<TransitTrip> listOfTrips){

        AssignOpposing assignOpposing = new AssignOpposing();
        assignOpposing.assignOpposingDirection(listOfTrips);

        WriteXMLRailNetwork writeXMLRailNetwork = new WriteXMLRailNetwork();
        writeXMLRailNetwork.writeXMLRailNetwork(listOfStops, listOfTrips, rb.getString("out.xml.network"));


        NetworkMergeTool merger = new NetworkMergeTool(rb.getString("road.network.file"),
                rb.getString("out.xml.network"),
                rb.getString("final.network.file") );
        merger.mergeWithRoadNetwork();


        WriteXMLRailSchedule writeXMLRailSchedule = new WriteXMLRailSchedule();
        writeXMLRailSchedule.writeXMLRailSchedule(listOfTrips, rb.getString("out.xml.vehicles"),rb.getString("out.xml.schedule"));


    }

}
