package importOsm;

import transitSystem.TransitLine;
import transitSystem.TransitStop;
import transitSystem.TransitStopToStop;
import transitSystem.TransitTrip;
import utils.TransitUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
    private String cvsSplitBy = ";";
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

            br = new BufferedReader(new FileReader(fileName));
            Long previousStopId = Long.parseLong("0");

            line = br.readLine();

            String[] header = line.split(cvsSplitBy);

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



            int rowCounter = 0;
            while ((line = br.readLine()) != null) {


                    String[] row = line.split(cvsSplitBy);
                    long stopId = Long.parseLong(row[posId]);

                    if (stopId != previousStopId) {
                        //found a new stop
                        previousStopId = stopId;
                        String stopName = row[posName];
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

            int lines = 0;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(cvsSplitBy);
                rowMap.put(lines, row);
                lines++;
            }
            rowMap.put(rowMap.size(), rowMap.get(1));

            //System.out.println(lines);

            String[] row1 = rowMap.get(1);
            long lineId = Long.parseLong(row1[0]);
            String lineRef = row1[1];
            String fromStation = row1[2];
            String toStation = row1[3];
            boolean bus = Boolean.parseBoolean(row1[4]);
            boolean tram = Boolean.parseBoolean(row1[5]);
            boolean subway = Boolean.parseBoolean(row1[6]);
            int sequenceNumber = Integer.parseInt(row1[7]);
            int reSequenceNumber = 0;
            long stopId = Long.parseLong(row1[8]);
            Map<Integer, TransitStop> stopList = new HashMap<Integer, TransitStop>();

            if (stopMap.get(stopId) != null) {
                TransitStop transitStop = stopMap.get(stopId);
                //stopList.put(sequenceNumber, transitStop);
                stopList.put(reSequenceNumber, transitStop);
                reSequenceNumber++;
                transitStop.addLine(lineRef, lineId);
            }


            for (int i = 2; i < rowMap.size(); i++) {
                String[] currentRow = rowMap.get(i);
                long currentLineId = Long.parseLong(currentRow[0]);
                if (lineId == currentLineId) {
                    //continue adding stations to stationList
                    sequenceNumber = Integer.parseInt(currentRow[7]);

                    TransitStop transitStop;
                    if (stopMap.get(stopId) != null) {
                        stopId = Long.parseLong(currentRow[8]);
                        transitStop = stopMap.get(stopId);
                        //stopList.put(sequenceNumber, transitStop);
                        stopList.put(reSequenceNumber, transitStop);
                        reSequenceNumber++;
                        //System.out.println(lineRef + transitStop.getStopName());
                        transitStop.addLine(lineRef, lineId);
                    }


                } else {
                    //create line and clear map, and recollect line data
                    TransitLine transitLine = new TransitLine(lineId, fromStation, toStation, bus, subway, tram, lineRef, stopList);
                    listOfLines.add(transitLine);
                    lineMap.put(lineId, transitLine);
                    stopList = new HashMap<Integer, TransitStop>();

                    lineId = Long.parseLong(currentRow[0]);
                    lineRef = currentRow[1];
                    fromStation = currentRow[2];
                    toStation = currentRow[3];
                    bus = Boolean.parseBoolean(currentRow[4]);
                    tram = Boolean.parseBoolean(currentRow[5]);
                    subway = Boolean.parseBoolean(currentRow[6]);
                    sequenceNumber = Integer.parseInt(currentRow[7]);
                    reSequenceNumber =0;
                    stopId = Long.parseLong(currentRow[8]);

                    TransitStop transitStop;

                    if (stopMap.get(stopId) != null) {
                        stopId = Long.parseLong(currentRow[8]);
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

            int lines = 0;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(cvsSplitBy);
                rowMap.put(lines, row);
                lines++;
            }
            //rowMap.put(rowMap.size(),rowMap.get(1));

            //System.out.println(lines);

            String[] row1 = rowMap.get(1);
            long lineId = Long.parseLong(row1[0]);
            TransitLine transitLine = null;

                int sequenceNumber = Integer.parseInt(row1[7]);
                long linkId = Long.parseLong(row1[8]);
                Map<Integer, Long> linkList = new HashMap<Integer, Long>();
                linkList.put(sequenceNumber, linkId);
            if (lineMap.containsKey(lineId)) {
                transitLine = lineMap.get(lineId);

            }


            for (int i = 2; i < rowMap.size(); i++) {
                String[] currentRow = rowMap.get(i);
                long currentLineId = Long.parseLong(currentRow[0]);
                if (lineId == currentLineId) {
                    //continue adding links to linklist
                    sequenceNumber = Integer.parseInt(currentRow[7]);
                    linkId = Long.parseLong(currentRow[8]);
                    linkList.put(sequenceNumber, linkId);


                } else {
                    //create line and clear map, and recollect line data
                    if (lineMap.containsKey(lineId)) {
                        transitLine.setLinkList(linkList);
                    }


                        linkList = new HashMap<Integer, Long>();
                        lineId = currentLineId;
                        transitLine = lineMap.get(currentLineId);
                        sequenceNumber = Integer.parseInt(currentRow[7]);
                        linkId = Long.parseLong(currentRow[8]);
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

            int lines = 0;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(cvsSplitBy);
                rowMap.put(lines, row);
                lines++;
            }
            //rowMap.put(rowMap.size(),rowMap.get(1));

            //System.out.println(lines);

            if(rowMap.size()>1) {

                //get line 1
                String[] row1 = rowMap.get(1);
                long lineId = Long.parseLong(row1[0]);
                TransitLine transitLine = lineMap.get(lineId);

                long fromStopId = Long.parseLong(row1[2]);
                long toStopId = Long.parseLong(row1[4]);
                TransitStop fromStop = stopMap.get(fromStopId);
                TransitStop toStop = stopMap.get(toStopId);

                int departureTime = Integer.parseInt(row1[6]);
                int arrivalTime = Integer.parseInt(row1[7]);
                int sequence = 0;

                TransitStopToStop transitStopToStop = new TransitStopToStop(fromStop, toStop, arrivalTime, departureTime);
                Map<Integer, TransitStopToStop> stopToStopMap = new HashMap<Integer, TransitStopToStop>();
                stopToStopMap.put(sequence, transitStopToStop);
                sequence++;

                //read lines 1 to N

                for (int i = 2; i < rowMap.size(); i++) {
                    String[] currentRow = rowMap.get(i);
                    long currentLineId = Long.parseLong(currentRow[0]);
                    if (lineId == currentLineId) {
                        //still in the same transit line
                        fromStop = stopMap.get(Long.parseLong(currentRow[2]));
                        toStop = stopMap.get(Long.parseLong(currentRow[4]));
                        departureTime = Integer.parseInt(currentRow[6]);
                        arrivalTime = Integer.parseInt(currentRow[7]);

                        transitStopToStop = new TransitStopToStop(fromStop, toStop, arrivalTime, departureTime);
                        stopToStopMap.put(sequence, transitStopToStop);
                        sequence++;

                    } else {
                        //found a new transit line
                        TransitTrip transitTrip = new TransitTrip(transitLine, stopToStopMap.get(0).getDepartureTime(), stopToStopMap);
                        listOfTrips.add(transitTrip);
                        stopToStopMap = new HashMap<Integer, TransitStopToStop>();
                        sequence = 0;

                        lineId = Long.parseLong(currentRow[0]);
                        transitLine = lineMap.get(lineId);
                        fromStop = stopMap.get(Long.parseLong(currentRow[2]));
                        toStop = stopMap.get(Long.parseLong(currentRow[4]));
                        departureTime = Integer.parseInt(currentRow[6]);
                        arrivalTime = Integer.parseInt(currentRow[7]);
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
}
