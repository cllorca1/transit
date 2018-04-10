package writeOutputFiles;

import transitSystem.TransitDataContainer;
import transitSystem.TransitLine;
import transitSystem.TransitStop;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by carlloga on 5/12/16.
 */
public class WriteLineLinksFile {

    private String csvSplitBy = ",";

    public void write(String outputFile, TransitDataContainer transitDataContainer) {


        ArrayList<TransitLine> listOfLines = transitDataContainer.getListOfLines();

        try {


            PrintWriter pw = new PrintWriter(new FileWriter(outputFile, false));
            pw.println("lineId,lineRef,from,to,bus,tram,subway,sequence,linkid");


            System.out.println("Writing output linelinks file for " + listOfLines.size() + " lines.");


            for (TransitLine transitLine : listOfLines) {
                Map<Integer, Long> linkList = transitLine.getLinkList();
                try {
                    for (int sequence : linkList.keySet()) {
                        pw.println(transitLine.getLineId() + csvSplitBy
                                + transitLine.getLineName() + csvSplitBy
                                + transitLine.getFromStop() + csvSplitBy
                                + transitLine.getToStop() + csvSplitBy
                                + transitLine.isBus() + csvSplitBy
                                + transitLine.isTram() + csvSplitBy
                                + transitLine.isSubway() + csvSplitBy
                                + sequence + csvSplitBy
                                + linkList.get(sequence));

                    }
                } catch (Exception e){

                }
            }

            pw.flush();
            pw.close();


        } catch (IOException e) {
            e.printStackTrace();


        }
    }
}
