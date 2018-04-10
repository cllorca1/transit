package writeOutputFiles;

import importOsm.ReadXmlFile;
import transitSystem.TransitDataContainer;
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
public class WriteLinesFile implements CSVWriter {

    private String csvSplitBy = ",";

    public void write(String outputFile, TransitDataContainer transitDataContainer) {

        ArrayList<TransitLine> listOfLines = transitDataContainer.getListOfLines();

        try {


            PrintWriter pw = new PrintWriter(new FileWriter(outputFile, false));
            pw.println("lineId,lineRef,from,to,bus,tram,subway,sequence,stopId,stopName");


            System.out.println("Writing output file for " + listOfLines.size() + " lines.");




            for (TransitLine transitLine : listOfLines) {
                Map<Integer, TransitStop> stopList = transitLine.getStopList();
                //check the name of the stop is not repeated
                String lastStopName = "";
                for (int sequence : stopList.keySet()) {
                    //check and if different substitute lastStopName by currentStopName
                    if(!stopList.get(sequence).getStopName().equals(lastStopName) || stopList.get(sequence).getStopName().equals("")) {
                        lastStopName = stopList.get(sequence).getStopName();
                        pw.println(transitLine.getLineId() + csvSplitBy
                                + transitLine.getLineName() + csvSplitBy
                                + transitLine.getFromStop() + csvSplitBy
                                + transitLine.getToStop() + csvSplitBy
                                + transitLine.isBus() + csvSplitBy
                                + transitLine.isTram() +csvSplitBy
                                + transitLine.isSubway() + csvSplitBy
                                + sequence + csvSplitBy
                                + stopList.get(sequence).getStopId() + csvSplitBy
                                + stopList.get(sequence).getStopName());

                    }
                }
            }

            pw.flush();
            pw.close();


        } catch (IOException e) {
            e.printStackTrace();


        }

    }
}
