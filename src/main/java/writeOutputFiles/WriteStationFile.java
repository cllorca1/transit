package writeOutputFiles;

import importOsm.ReadXmlFile;
import transitSystem.TransitDataContainer;
import transitSystem.TransitStop;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by carlloga on 24/11/16.
 */
public class WriteStationFile {

    private String csvSplitBy = ",";

    public void write(String outputFile, TransitDataContainer transitDataContainer) {

        ArrayList<TransitStop> listOfStops = transitDataContainer.getListOfStops();

        try {

            boolean writeAllStops = false;
            PrintWriter pw = new PrintWriter(new FileWriter(outputFile, false));
            if (writeAllStops) {
                pw.println("stopId,stopName,lat,lon,bus,tram,subway,stopPositionFlag,line,lineId,x,y");
            } else {
                pw.println("stopId,stopName,lat,lon,bus,tram,subway,stopPositionFlag,x,y");
            }


            System.out.println("Writing output file for " + listOfStops.size() + " stations.");

            for (TransitStop transitStop : listOfStops) {
                //the next loop writes as many times the stop as lines stop at, and does not write non-served stops if writeAllStops = false

                if (writeAllStops) {
                    for (String line : transitStop.getLines()) {
                        pw.println(transitStop.getStopId() + csvSplitBy
                                + transitStop.getStopName() + csvSplitBy
                                + transitStop.getLat() + csvSplitBy
                                + transitStop.getLon() +csvSplitBy
                                + transitStop.isBus() + csvSplitBy
                                + transitStop.isTram() + csvSplitBy
                                + transitStop.isSubway() + csvSplitBy
                                + transitStop.isStopPositionFlag()
                                + csvSplitBy + line + csvSplitBy
                                + transitStop.getLineIds().get(transitStop.getLines().indexOf(line))+ csvSplitBy
                                + transitStop.getX() + csvSplitBy
                                + transitStop.getY());
                    }
                } else {
                    pw.println(transitStop.getStopId() + csvSplitBy
                            + transitStop.getStopName() + csvSplitBy
                            + transitStop.getLat() +csvSplitBy
                            + transitStop.getLon() +csvSplitBy
                            + transitStop.isBus() + csvSplitBy
                            + transitStop.isTram() + csvSplitBy
                            + transitStop.isSubway() + csvSplitBy
                            + transitStop.isStopPositionFlag()+ csvSplitBy
                            + transitStop.getX() + csvSplitBy
                            + transitStop.getY());
                }
            }

            pw.flush();
            pw.close();



        } catch (IOException e) {
            e.printStackTrace();


        }

    }
}