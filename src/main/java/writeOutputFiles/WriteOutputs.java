package writeOutputFiles;

import transitSystem.TransitDataContainer;
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

    public void writeOutputs(TransitDataContainer transitDataContainer){
        String stationFileName = rb.getString("out.csv.stations");
        String linesFileName = rb.getString("out.csv.lines");
        String lineLinksFileName = rb.getString("out.csv.trips");
        String tripFileName = rb.getString("out.csv.line.links");

        WriteStationFile writeStationFile = new WriteStationFile();
        writeStationFile.write(stationFileName, transitDataContainer);

        WriteLinesFile writeLinesFile = new WriteLinesFile();
        writeLinesFile.write(linesFileName, transitDataContainer);

        WriteLineLinksFile writeLineLinksFile = new WriteLineLinksFile();
        writeLineLinksFile.write(lineLinksFileName, transitDataContainer);

        WriteTripFile writeTripFile = new WriteTripFile();
        writeTripFile.write(tripFileName, transitDataContainer);



    }
}
