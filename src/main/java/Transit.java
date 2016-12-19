

import geocoding.Geocode;
import importOsm.ReadCSVFile;
import importOsm.ReadXmlFile;
import transitSystem.TransitLine;
import transitSystem.TransitStop;
import transitSystem.TransitTrip;
import travelTimeFromGoogle.LineFrequency;
import travelTimeFromGoogle.TravelTimeFromGoogle;
import writeMATSimXMLFiles.TransformCoordinates;
import writeMATSimXMLFiles.WriteXMLRaiFiles;
import writeOutputFiles.WriteOutputs;

import java.io.File;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.pb.common.util.ResourceUtil;



/**
 * Created by carlloga on 21/11/16.
 */
public class Transit {

    private ResourceBundle rb;
    private static ArrayList<TransitStop> listOfStops = new ArrayList<TransitStop>();
    private static ArrayList<TransitLine> listOfLines = new ArrayList<TransitLine>();
    private static ArrayList<TransitTrip> listOfTrips = new ArrayList<TransitTrip>();


    private Transit (ResourceBundle rb){
        this.rb=rb;
    }

    public static void main (String[] args){

        File propFile = new File("transit.properties");
        ResourceBundle rb = ResourceUtil.getPropertyBundle(propFile);


       //additional tool to geocode addresses: address to coordinates
        //Geocode geocode = new Geocode(rb);
        //geocode.geocodeAdress();

        boolean extractXML = false;
        if (extractXML) {
            //extract data from XML OSM files
            ReadXmlFile readXml = new ReadXmlFile();
            readXml.readXMLFile("input/transit.osm");
            listOfStops = readXml.getListOfStops();
            listOfLines = readXml.getListOfLines();

        } else {

        }

        boolean readCSV = true;
        if (readCSV){
            ReadCSVFile readCSVFile = new ReadCSVFile();
            readCSVFile.readCsv();
            listOfStops = readCSVFile.getListOfStops();
            listOfLines = readCSVFile.getListOfLines();
            listOfTrips = readCSVFile.getListOfTrips();
            System.out.println(listOfStops.size() + " stops read from csv");
            System.out.println(listOfLines.size() + " lines read from csv");


        }

        boolean getTimes= false;
        if (getTimes) {
            TravelTimeFromGoogle travelTimeFromGoogle = new TravelTimeFromGoogle();
            travelTimeFromGoogle.getTimes(listOfLines);
            listOfTrips = travelTimeFromGoogle.getListOfTrips();
        }

        boolean getFrequencies = false;
        if (getFrequencies){
            LineFrequency lineFrequency = new LineFrequency();
            lineFrequency.getLineFrequency(listOfLines);
            listOfTrips = lineFrequency.getListOfTrips();
        }

        boolean writeOutputFiles = false;
        //write outputs

        if (writeOutputFiles) {
            WriteOutputs writeOutputs = new WriteOutputs();
            System.out.println("Number of trips = " + listOfTrips.size());
            writeOutputs.writeOutputs("./output", listOfStops, listOfLines, listOfTrips);
        }

        boolean writeMATSimFiles = true;
        if (writeMATSimFiles) {

            WriteXMLRaiFiles writeXMLRaiFiles = new WriteXMLRaiFiles();
            writeXMLRaiFiles.writeXMLFiles(listOfStops, listOfLines, listOfTrips);
        }

    }

}
