package writeOutputFiles;

import transitSystem.TransitLine;
import transitSystem.TransitStop;
import transitSystem.TransitTrip;

import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by carlloga on 24/11/16.
 */
public class WriteOutputs {

    private ResourceBundle rb;

    public WriteOutputs(ResourceBundle rb) {
        this.rb = rb;
    }

    public void writeOutputs(ArrayList<TransitStop> listOfStops, ArrayList<TransitLine> listOfLines, ArrayList<TransitTrip> listOfTrips){
        String stationFileName = rb.getString("out.csv.stations");
        String linesFileName = rb.getString("out.csv.lines");
        String lineLinksFileName = rb.getString("out.csv.trips");
        String tripFileName = rb.getString("out.csv.line.links");

        WriteStationFile writeStationFile = new WriteStationFile();
        writeStationFile.writeStationFile(stationFileName, listOfStops);

        WriteLinesFile writeLinesFile = new WriteLinesFile();
        writeLinesFile.writeLinesFile(linesFileName, listOfLines);

        WriteLineLinksFile writeLineLinksFile = new WriteLineLinksFile();
        writeLineLinksFile.writeLineLinksFile(lineLinksFileName, listOfLines);

        WriteTripFile writeTripFile = new WriteTripFile();
        writeTripFile.writeTripFile(tripFileName, listOfTrips);



    }
}
