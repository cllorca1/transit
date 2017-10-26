

import consistencyChecker.TransitDataChecker;
import importOsm.CSVFrequencyReader;
import importOsm.ReadCSVFile;
import importOsm.ReadXmlFile;
import importOsm.VehicleTypeReader;
import transitSystem.*;
import travelTimeFromGoogle.LineFrequency;
import travelTimeFromGoogle.TravelTimeFromGoogle;
import travelTimeFromGoogle.TravelTimeMatrixFromGoogle;
import utils.TransitUtil;
import writeMATSimXMLFiles.WriteXMLRaiFiles;
import writeOutputFiles.WriteOutputs;

import javax.activation.DataContentHandler;
import java.io.*;
import java.util.*;


/**
 * Created by carlloga on 21/11/16.
 */
public class Transit {

    private ResourceBundle rb;
    private ArrayList<TransitStop> listOfStops = new ArrayList<TransitStop>();
    private ArrayList<TransitLine> listOfLines = new ArrayList<TransitLine>();
    private ArrayList<TransitTrip> listOfTrips = new ArrayList<TransitTrip>();

    private TransitDataContainer transitDataContainer;


    private Transit (ResourceBundle rb){
        this.rb=rb;
    }

    public static void main (String[] args) {

        File propFile = new File("transit.properties");
        ResourceBundle rb = null;
        try {
            InputStream inputStream = new FileInputStream(propFile);
            rb = new PropertyResourceBundle(inputStream);
            System.out.println("Property-file read");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        TransitUtil.initializeRandomNumber();

        Transit t = new Transit(rb);
        t.runTransitTools();

    }

    public void runTransitTools(){


        boolean extractXML = Boolean.parseBoolean(rb.getString("extract.XML"));
        if (extractXML) {
            //extract data from XML OSM files
            ReadXmlFile readXml = new ReadXmlFile(rb);
            readXml.readXMLFile();
            listOfStops = readXml.getListOfStops();
            listOfLines = readXml.getListOfLines();

        }

        boolean readCSV = Boolean.parseBoolean(rb.getString("read.CSV"));
        if (readCSV){
            ReadCSVFile readCSVFile = new ReadCSVFile(rb);
            readCSVFile.readCsv();
            listOfStops = readCSVFile.getListOfStops();
            listOfLines = readCSVFile.getListOfLines();
            listOfTrips = readCSVFile.getListOfTrips();
            System.out.println(listOfStops.size() + " stops read from csv");
            System.out.println(listOfLines.size() + " lines read from csv");
            System.out.println(listOfTrips.size() + " trips read from csv");

            CSVFrequencyReader frequencyReader = new CSVFrequencyReader(rb);
            frequencyReader.mapLines(listOfLines);
            frequencyReader.readExternalFrequencies();

            VehicleTypeReader vehicleTypeReader = new VehicleTypeReader(rb);
            Map<LineType, TransitVehicle> vehicleMap = vehicleTypeReader.createTransitVehicleTypes();

//            Temporary

            transitDataContainer = new TransitDataContainer(listOfStops, listOfLines, listOfTrips, vehicleMap);

        }


        boolean manualFilters = true;

        if (manualFilters){

            char mode = 't';
            ArrayList<TransitTrip> newListOfTrips = new ArrayList<TransitTrip>();

            switch (mode){
                case 'b':

                    for (int i = 0; i < listOfTrips.size(); i++){
                        TransitTrip trip = listOfTrips.get(i);
                        TransitLine line = trip.getTransitLine();
                        if (line.isSubway() || line.isTram() || (!line.isSubway() && !line.isBus() && !line.isTram())){
                            //is a metro or a tram or a train -- >

                        } else {
                            newListOfTrips.add(trip);
                        }
                    }
                    break;
                case 'm':
                   for (int i = 0; i < listOfTrips.size(); i++){
                       TransitTrip trip = listOfTrips.get(i);
                       TransitLine line = trip.getTransitLine();
                       if (!line.isBus() && !line.isTram() && !line.isSubway()){
                            //is a train --> remove
                       } else {
                           newListOfTrips.add(trip);
                       }
                    }
                    break;
                case 't':
                    newListOfTrips = listOfTrips;
                    break;
            }

            System.out.println(newListOfTrips.size() + " trips after applying filters");

            //todo
            listOfTrips = newListOfTrips;
            transitDataContainer.setListOfTrips(listOfTrips);
        }



        boolean getTimes= Boolean.parseBoolean(rb.getString("get.times"));
        if (getTimes) {
            TravelTimeFromGoogle travelTimeFromGoogle = new TravelTimeFromGoogle(rb);
            travelTimeFromGoogle.getTimes(listOfLines);
            listOfTrips = travelTimeFromGoogle.getListOfTrips();
            //todo
            transitDataContainer.setListOfTrips(listOfTrips);
        }

        boolean getTimesUsingMatrix= Boolean.parseBoolean(rb.getString("get.times.matrix"));
        if (getTimesUsingMatrix) {
            TravelTimeMatrixFromGoogle travelTimeMAtrixFromGoogle = new TravelTimeMatrixFromGoogle(rb);
            travelTimeMAtrixFromGoogle.getTimesFromMatrix(listOfLines);
            //replaces the list of trips
            listOfTrips = travelTimeMAtrixFromGoogle.getListOfTrips();
            //todo
            transitDataContainer.setListOfTrips(listOfTrips);
        }

        boolean getFrequencies = Boolean.parseBoolean(rb.getString("get.frequencies"));
        if (getFrequencies){
            //this module is no longer used -- inconsistent with further matsim schedule generation
            LineFrequency lineFrequency = new LineFrequency();
            lineFrequency.getLineFrequency(listOfLines);
            //todo
            listOfTrips = lineFrequency.getListOfTrips();
        }

        boolean check= Boolean.parseBoolean(rb.getString("check.consistency"));


        if (check) {
            TransitDataChecker transitDataChecker = new TransitDataChecker();
            transitDataChecker.load(listOfStops, listOfLines, listOfTrips);
            transitDataChecker.check();
        }


        boolean writeMATSimFiles = Boolean.parseBoolean(rb.getString("write.output.XML"));
        if (writeMATSimFiles) {

            WriteXMLRaiFiles writeXMLRaiFiles = new WriteXMLRaiFiles(rb, transitDataContainer);
            writeXMLRaiFiles.writeXMLFiles();
        }

        boolean writeOutputFiles = Boolean.parseBoolean(rb.getString("write.output.CSV"));
        //write outputs

        if (writeOutputFiles) {
            WriteOutputs writeOutputs = new WriteOutputs(rb);
            System.out.println("Number of trips = " + listOfTrips.size());
            writeOutputs.writeOutputs(listOfStops, listOfLines, listOfTrips);
        }

    }

    public ResourceBundle getRb() {
        return rb;
    }
}
