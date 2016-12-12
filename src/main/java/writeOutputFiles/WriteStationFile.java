package writeOutputFiles;

import importOsm.ReadXmlFile;
import transitSystem.TransitStop;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by carlloga on 24/11/16.
 */
public class WriteStationFile {

    public void writeStationFile(String outputFile, ArrayList<TransitStop> listOfStops) {

        try {


            PrintWriter pw = new PrintWriter(new FileWriter(outputFile, true));
            pw.println("stopId; stopName; lat; lon; bus; tram; subway; stopPositionFlag; line; lineId");


            System.out.println("Writing output file for " + listOfStops.size() + " stations.");

            for (TransitStop transitStop : listOfStops) {
                for (String line : transitStop.getLines()) {
                    pw.println(transitStop.getStopId() + ";"
                            + transitStop.getStopName() + ";"
                            + transitStop.getLat() + ";"
                            + transitStop.getLon() + ";"
                            + transitStop.isBus() + ";"
                            + transitStop.isTram() + ";"
                            + transitStop.isSubway() + ";"
                            + transitStop.isStopPositionFlag()+ ";"
                            + line + ";"
                            + transitStop.getLineIds().get(transitStop.getLines().indexOf(line)));
                }
            }

            pw.flush();
            pw.close();



        } catch (IOException e) {
            e.printStackTrace();


        }

    }
}