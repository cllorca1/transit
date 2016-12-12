package writeOutputFiles;

import transitSystem.TransitLine;
import transitSystem.TransitStop;
import transitSystem.TransitTrip;

import java.util.ArrayList;

/**
 * Created by carlloga on 24/11/16.
 */
public class WriteOutputs {

    public void writeOutputs(String outputFolder, ArrayList<TransitStop> listOfStops, ArrayList<TransitLine> listOfLines, ArrayList<TransitTrip> listOfTrips){
        String stationFileName = outputFolder + "/stationListOut.csv";
        String linesFileName = outputFolder + "/linesListOut.csv";
        String lineLinksFileName = outputFolder + "/lineLinksListOut.csv";
        String tripFileName = outputFolder + "/tripListOut.csv";

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
