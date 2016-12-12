package writeOutputFiles;

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


    public void writeLineLinksFile(String outputFile, ArrayList<TransitLine> listOfLines) {


        try {


            PrintWriter pw = new PrintWriter(new FileWriter(outputFile, true));
            pw.println("lineId; lineRef; from; to; bus; tram; subway; sequence; linkd");


            System.out.println("Writing output linelinks file for " + listOfLines.size() + " lines.");


            for (TransitLine transitLine : listOfLines) {
                Map<Integer, Long> linkList = transitLine.getLinkList();
                for (int sequence : linkList.keySet()) {
                    pw.println(transitLine.getLineId() + ";"
                            + transitLine.getLineName() + ";"
                            + transitLine.getFromStop() + ";"
                            + transitLine.getToStop() + ";"
                            + transitLine.isBus() + ";"
                            + transitLine.isTram() + ";"
                            + transitLine.isSubway() + ";"
                            + sequence + ";"
                            + linkList.get(sequence));

                }
            }

            pw.flush();
            pw.close();


        } catch (IOException e) {
            e.printStackTrace();


        }
    }
}
