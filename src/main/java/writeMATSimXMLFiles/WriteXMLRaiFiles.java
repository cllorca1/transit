package writeMATSimXMLFiles;

import transitSystem.TransitDataContainer;
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
    private TransitDataContainer transitDataContainer;

    public WriteXMLRaiFiles(ResourceBundle rb, TransitDataContainer transitDataContainer) {
        this.rb = rb;
        this.transitDataContainer = transitDataContainer;
    }

    public void writeXMLFiles(){

        AssignOpposing assignOpposing = new AssignOpposing();
        assignOpposing.assignOpposingDirection(transitDataContainer.getListOfTrips());

        WriteXMLRailNetwork writeXMLRailNetwork = new WriteXMLRailNetwork(transitDataContainer);
        writeXMLRailNetwork.writeXMLRailNetwork(rb.getString("out.xml.network"));


        NetworkMergeTool merger = new NetworkMergeTool(rb.getString("road.network.file"),
                rb.getString("out.xml.network"),
                rb.getString("final.network.file") );
        merger.mergeWithRoadNetwork();


        WriteXMLRailSchedule writeXMLRailSchedule = new WriteXMLRailSchedule(rb, transitDataContainer);
        writeXMLRailSchedule.writeXMLRailSchedule(rb.getString("out.xml.vehicles"),rb.getString("out.xml.schedule"));


    }

}
