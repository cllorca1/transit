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

            boolean writeAllStops = false;
            PrintWriter pw = new PrintWriter(new FileWriter(outputFile, false));
            if (writeAllStops) {
                pw.println("stopId;stopName;lat;lon;bus;tram;subway;stopPositionFlag;line;lineId;x;y");
            } else {
                pw.println("stopId;stopName;lat;lon;bus;tram;subway;stopPositionFlag;x;y");
            }


            System.out.println("Writing output file for " + listOfStops.size() + " stations.");

            for (TransitStop transitStop : listOfStops) {
                //the next loop writes as many times the stop as lines stop at, and does not write non-served stops if writeAllStops = false

                if (writeAllStops) {
                    for (String line : transitStop.getLines()) {
                        pw.println(transitStop.getStopId() + ";"
                                + transitStop.getStopName() + ";"
                                + transitStop.getLat() + ";"
                                + transitStop.getLon() + ";"
                                + transitStop.isBus() + ";"
                                + transitStop.isTram() + ";"
                                + transitStop.isSubway() + ";"
                                + transitStop.isStopPositionFlag()
                                + ";" + line + ";"
                                + transitStop.getLineIds().get(transitStop.getLines().indexOf(line))+ ";"
                                + transitStop.getX() + ";"
                                + transitStop.getY());
                    }
                } else {
                    pw.println(transitStop.getStopId() + ";"
                            + transitStop.getStopName() + ";"
                            + transitStop.getLat() + ";"
                            + transitStop.getLon() + ";"
                            + transitStop.isBus() + ";"
                            + transitStop.isTram() + ";"
                            + transitStop.isSubway() + ";"
                            + transitStop.isStopPositionFlag()+ ";"
                            + transitStop.getX() + ";"
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