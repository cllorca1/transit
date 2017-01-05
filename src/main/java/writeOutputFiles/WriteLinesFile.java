package writeOutputFiles;

import importOsm.ReadXmlFile;
import transitSystem.TransitLine;
import transitSystem.TransitStop;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by carlloga on 25/11/16.
 */
public class WriteLinesFile {

    public void writeLinesFile(String outputFile, ArrayList<TransitLine> listOfLines) {


        try {


            PrintWriter pw = new PrintWriter(new FileWriter(outputFile, false));
            pw.println("lineId; lineRef; from; to; bus; tram; subway; sequence; stopId; stopName");


            System.out.println("Writing output file for " + listOfLines.size() + " lines.");




            for (TransitLine transitLine : listOfLines) {
                Map<Integer, TransitStop> stopList = transitLine.getStopList();
                for (int sequence : stopList.keySet()) {
                    pw.println(transitLine.getLineId() + ";"
                            + transitLine.getLineName()+ ";"
                            + transitLine.getFromStop() + ";"
                            + transitLine.getToStop() + ";"
                            + transitLine.isBus()+ ";"
                            + transitLine.isTram() + ";"
                            + transitLine.isSubway() + ";"
                            + sequence + ";"
                            + stopList.get(sequence).getStopId() + ";"
                            + stopList.get(sequence).getStopName());


                }
            }

            pw.flush();
            pw.close();


        } catch (IOException e) {
            e.printStackTrace();


        }

    }
}
