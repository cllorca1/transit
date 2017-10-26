package importOsm;

import transitSystem.TransitLine;
import transitSystem.TransitStop;
import transitSystem.TransitStopToStop;
import transitSystem.TransitTrip;
import utils.TransitUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by carlloga on 28/11/16.
 */
public class ReadCSVFile {

    private ArrayList<TransitStop> listOfStops = new ArrayList<TransitStop>();
    private ArrayList<TransitLine> listOfLines = new ArrayList<TransitLine>();
    private ArrayList<TransitTrip> listOfTrips = new ArrayList<TransitTrip>();
    private HashMap<Long, TransitStop> stopMap = new HashMap();
    private HashMap<Long, TransitLine> lineMap = new HashMap();
    private String csvSplitBy = ";";
    private ResourceBundle rb;

    public ReadCSVFile(ResourceBundle rb){
        this.rb=rb;
    }

    public void readCsv() {

        readCsvStations(rb.getString("csv.station.file"));
        readCsvLines(rb.getString("csv.line.file"));
        readCsvLineLinks(rb.getString("csv.line.link.file"));
        readCsvStopToStop(rb.getString("csv.trip.file"));



    }

    public void readCsvStations(String fileName) {


        BufferedReader br = null;
        String line = "";


        try {

            br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"ISO-8859-15"));

            Long previousStopId = Long.parseLong("0");

            line = br.readLine();

            String[] header = line.split(csvSplitBy);

            int posId = TransitUtil.findPositionInArray("stopId", header);
            int posName = TransitUtil.findPositionInArray("stopName", header);
            int posLat = TransitUtil.findPositionInArray("lat", header);
            int posLon = TransitUtil.findPositionInArray("lon", header);
            int posBus = TransitUtil.findPositionInArray("bus", header);
            int posTram = TransitUtil.findPositionInArray("tram", header);
            int posSubway = TransitUtil.findPositionInArray("subway", header);
            int posFlag = TransitUtil.findPositionInArray("stopPositionFlag", header);
            int posX = TransitUtil.findPositionInArray("x", header);
            int posY = TransitUtil.findPositionInArray("y", header);



            int rowCounter = 1;
            while ((line = br.readLine()) != null) {




                    String[] row = line.split(csvSplitBy);
                    long stopId = Long.parseLong(row[posId]);

                    if (stopId != previousStopId) {
                        //found a new stop
                        previousStopId = stopId;
                        String stopName = deGermanizeString(row[posName]);
                        System.out.println(stopName);
                        String lat = row[posLat];
                        String lon = row[posLon];
                        boolean bus = Boolean.parseBoolean(row[posBus]);
                        boolean tram = Boolean.parseBoolean(row[posTram]);
                        boolean subway = Boolean.parseBoolean(row[posSubway]);
                        boolean stopPositionFlag = Boolean.parseBoolean(row[posFlag]);
                        TransitStop transitStop = new TransitStop(stopId, stopName, lat, lon, bus, tram, subway, stopPositionFlag);
                        listOfStops.add(transitStop);
                        stopMap.put(stopId, transitStop);

                        if (posX != -1 && posY != -1){
                            transitStop.setX(Float.parseFloat(row[posX]));
                            transitStop.setY(Float.parseFloat(row[posY]));

                        }

                    }

                rowCounter++;

            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void readCsvLines(String fileName) {

        BufferedReader br = null;
        String line = "";


        try {

            Map<Integer, String[]> rowMap = new HashMap<Integer, String[]>();

            br = new BufferedReader(new FileReader(fileName));

            String[] header = br.readLine().split(csvSplitBy);

            int posId = TransitUtil.findPositionInArray( "lineId", header);
            int posRef= TransitUtil.findPositionInArray( "lineId", header);
            int posFromSt= TransitUtil.findPositionInArray( "lineId", header);
            int posToSt= TransitUtil.findPositionInArray( "lineId", header);
            int posBus= TransitUtil.findPositionInArray( "lineId", header);
            int posTram= TransitUtil.findPositionInArray( "lineId", header);
            int posSubway= TransitUtil.findPositionInArray( "lineId", header);
            int posSeq= TransitUtil.findPositionInArray( "lineId", header);
            int posStopId= TransitUtil.findPositionInArray( "lineId", header);

            int lineIndex = 0;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(csvSplitBy);
                rowMap.put(lineIndex, row);
                lineIndex++;
            }

            //Added a final row line to allow to store the final line
            rowMap.put(rowMap.size(), rowMap.get(1));


            //starts with the map
            //read the first line with content
            String[] row1 = rowMap.get(1);
            long lineId = Long.parseLong(row1[posId]);
            String lineRef = row1[posRef];
            String fromStation = row1[posFromSt];
            String toStation = row1[posToSt];
            boolean bus = Boolean.parseBoolean(row1[posBus]);
            boolean tram = Boolean.parseBoolean(row1[posTram]);
            boolean subway = Boolean.parseBoolean(row1[posSubway]);
            int sequenceNumber = Integer.parseInt(row1[posSeq]);
            //this variable cleans the jumps in sequence number of the input file, if exist
            int reSequenceNumber = 0;
            long stopId = Long.parseLong(row1[posStopId]);
            Map<Integer, TransitStop> stopList = new HashMap<Integer, TransitStop>();

            if (stopMap.get(stopId) != null) {
                TransitStop transitStop = stopMap.get(stopId);
                //stopList.put(sequenceNumber, transitStop);
                stopList.put(reSequenceNumber, transitStop);
                reSequenceNumber++;
                transitStop.addLine(lineRef, lineId);
            }

            //read the second and further stops
            for (int i = 2; i < rowMap.size(); i++) {
                String[] currentRow = rowMap.get(i);
                long currentLineId = Long.parseLong(currentRow[posId]);
                if (lineId == currentLineId) {
                    //continue adding stations to stationList
                    sequenceNumber = Integer.parseInt(currentRow[posSeq]);

                    TransitStop transitStop;
                    if (stopMap.get(stopId) != null) {
                        stopId = Long.parseLong(currentRow[posStopId]);
                        transitStop = stopMap.get(stopId);
                        //stopList.put(sequenceNumber, transitStop);
                        stopList.put(reSequenceNumber, transitStop);
                        reSequenceNumber++;
                        //System.out.println(lineRef + transitStop.getStopName());
                        transitStop.addLine(lineRef, lineId);
                    }


                } else {
                    //create line (already read) and clear map, and recollect line data
                    TransitLine transitLine = new TransitLine(lineId, fromStation, toStation, bus, subway, tram, lineRef, stopList);
                    listOfLines.add(transitLine);
                    lineMap.put(lineId, transitLine);
                    stopList = new HashMap<>();

                    lineId = Long.parseLong(currentRow[posId]);
                    lineRef = currentRow[posRef];
                    fromStation = currentRow[posFromSt];
                    toStation = currentRow[posToSt];
                    bus = Boolean.parseBoolean(currentRow[posBus]);
                    tram = Boolean.parseBoolean(currentRow[posTram]);
                    subway = Boolean.parseBoolean(currentRow[posSubway]);
                    sequenceNumber = Integer.parseInt(currentRow[posSeq]);
                    reSequenceNumber =0;
//                    stopId = Long.parseLong(currentRow[posStopId]);

                    TransitStop transitStop;

                    if (stopMap.get(stopId) != null) {
                        stopId = Long.parseLong(currentRow[posStopId]);
                        transitStop = stopMap.get(stopId);
                        //stopList.put(sequenceNumber, transitStop);
                        stopList.put(reSequenceNumber, transitStop);
                        reSequenceNumber++;
                        transitStop.addLine(lineRef, lineId);
                    }

                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void readCsvLineLinks(String fileName) {
        BufferedReader br = null;
        String line = "";

        try {

            Map<Integer, String[]> rowMap = new HashMap<Integer, String[]>();

            br = new BufferedReader(new FileReader(fileName));

            String[] header = br.readLine().split(csvSplitBy);

            int posLineId = TransitUtil.findPositionInArray("lineId", header);
            int posSeq = TransitUtil.findPositionInArray("sequence", header);
            int posLinkId = TransitUtil.findPositionInArray("linkid", header);


            int lines = 1;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(csvSplitBy);
                rowMap.put(lines, row);
                lines++;
            }


            String[] row1 = rowMap.get(1);
            long lineId = Long.parseLong(row1[posLineId]);
            TransitLine transitLine = null;

                int sequenceNumber = Integer.parseInt(row1[posSeq]);
                long linkId = Long.parseLong(row1[posLinkId]);
                Map<Integer, Long> linkList = new HashMap<Integer, Long>();
                linkList.put(sequenceNumber, linkId);

            if (lineMap.containsKey(lineId)) {
                transitLine = lineMap.get(lineId);

            }


            for (int i = 2; i < rowMap.size(); i++) {
                String[] currentRow = rowMap.get(i);
                long currentLineId = Long.parseLong(currentRow[posLineId]);
                if (lineId == currentLineId) {
                    //continue adding links to linklist
                    sequenceNumber = Integer.parseInt(currentRow[posSeq]);
                    linkId = Long.parseLong(currentRow[posLinkId]);
                    linkList.put(sequenceNumber, linkId);


                } else {
                    //create line and clear map, and recollect line data
                    if (lineMap.containsKey(lineId)) {
                        transitLine.setLinkList(linkList);
                    }


                        linkList = new HashMap<Integer, Long>();
                        lineId = currentLineId;
                        transitLine = lineMap.get(currentLineId);
                        sequenceNumber = Integer.parseInt(currentRow[posSeq]);
                        linkId = Long.parseLong(currentRow[posLinkId]);
                        linkList.put(sequenceNumber, linkId);

                }

            }
            //add the last line
            if (lineMap.containsKey(lineId)) {
                transitLine.setLinkList(linkList);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public void readCsvStopToStop(String fileName) {
        BufferedReader br = null;
        String line = "";

        try {

            Map<Integer, String[]> rowMap = new HashMap<Integer, String[]>();

            br = new BufferedReader(new FileReader(fileName));

            String[] header = br.readLine().split(csvSplitBy);

            int posLineId = TransitUtil.findPositionInArray("lineId", header);
            int posFromStopId = TransitUtil.findPositionInArray("fromStopId", header);
            int posToStopId = TransitUtil.findPositionInArray("toStopId", header);
            int posDepTime = TransitUtil.findPositionInArray("departureTime", header);
            int posArrTime = TransitUtil.findPositionInArray("arrivalTime", header);


            int lines = 1;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(csvSplitBy);
                rowMap.put(lines, row);
                lines++;
            }




            //System.out.println(lines);

            if(rowMap.size()>1) {

                //get line 1
                String[] row1 = rowMap.get(1);
                long lineId = Long.parseLong(row1[posLineId]);
                TransitLine transitLine = lineMap.get(lineId);

                long fromStopId = Long.parseLong(row1[posFromStopId]);
                long toStopId = Long.parseLong(row1[posToStopId]);
                TransitStop fromStop = stopMap.get(fromStopId);
                TransitStop toStop = stopMap.get(toStopId);

                int departureTime = Integer.parseInt(row1[posDepTime]);
                int arrivalTime = Integer.parseInt(row1[posArrTime]);
                int sequence = 0;

                TransitStopToStop transitStopToStop = new TransitStopToStop(fromStop, toStop, arrivalTime, departureTime);
                Map<Integer, TransitStopToStop> stopToStopMap = new HashMap<Integer, TransitStopToStop>();
                stopToStopMap.put(sequence, transitStopToStop);
                sequence++;

                //read lines 1 to N

                for (int i = 2; i < rowMap.size(); i++) {
                    String[] currentRow = rowMap.get(i);
                    long currentLineId = Long.parseLong(currentRow[posLineId]);
                    if (lineId == currentLineId) {
                        //still in the same transit line
                        fromStop = stopMap.get(Long.parseLong(currentRow[posFromStopId]));
                        toStop = stopMap.get(Long.parseLong(currentRow[posToStopId]));
                        departureTime = Integer.parseInt(currentRow[posDepTime]);
                        arrivalTime = Integer.parseInt(currentRow[posArrTime]);

                        transitStopToStop = new TransitStopToStop(fromStop, toStop, arrivalTime, departureTime);
                        stopToStopMap.put(sequence, transitStopToStop);
                        sequence++;

                    } else {
                        //found a new transit line
                        TransitTrip transitTrip = new TransitTrip(transitLine, stopToStopMap.get(0).getDepartureTime(), stopToStopMap);
                        listOfTrips.add(transitTrip);
                        stopToStopMap = new HashMap<Integer, TransitStopToStop>();
                        sequence = 0;

                        lineId = Long.parseLong(currentRow[posLineId]);
                        transitLine = lineMap.get(lineId);
                        fromStop = stopMap.get(Long.parseLong(currentRow[posFromStopId]));
                        toStop = stopMap.get(Long.parseLong(currentRow[posToStopId]));
                        departureTime = Integer.parseInt(currentRow[posDepTime]);
                        arrivalTime = Integer.parseInt(currentRow[posArrTime]);
                        transitStopToStop = new TransitStopToStop(fromStop, toStop, arrivalTime, departureTime);
                        stopToStopMap.put(sequence, transitStopToStop);
                        sequence++;
                    }
                }

                TransitTrip transitTrip = new TransitTrip(transitLine, stopToStopMap.get(0).getDepartureTime(), stopToStopMap);
                listOfTrips.add(transitTrip);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public ArrayList<TransitStop> getListOfStops() {
        return listOfStops;
    }

    public ArrayList<TransitLine> getListOfLines() {
        return listOfLines;
    }

    public ArrayList<TransitTrip> getListOfTrips() {
        return listOfTrips;
    }

    public String deGermanizeString(String germanString){
        String nonGermanString = germanString.replace("ß", "ss");
        nonGermanString = nonGermanString.replace("ä", "ae");
        nonGermanString = nonGermanString.replace("ö", "oe");
        nonGermanString = nonGermanString.replace("ü", "ue");
        nonGermanString = nonGermanString.replace("Ä", "AE");
        nonGermanString = nonGermanString.replace("Ö", "OE");
        nonGermanString = nonGermanString.replace("Ü", "UE");

        return nonGermanString;
    }

}
