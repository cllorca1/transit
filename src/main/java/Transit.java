import geocoding.Geocode;
import importOsm.ReadCSVFile;
import importOsm.ReadXmlFile;
import transitSystem.TransitLine;
import transitSystem.TransitStop;
import transitSystem.TransitTrip;
import travelTimeFromGoogle.*;
import writeOutputFiles.WriteOutputs;

import java.util.ArrayList;

/**
 * Created by carlloga on 21/11/16.
 */
public class Transit {



    private static ArrayList<TransitStop> listOfStops = new ArrayList<TransitStop>();
    private static ArrayList<TransitLine> listOfLines = new ArrayList<TransitLine>();
    private static ArrayList<TransitTrip> listOfTrips = new ArrayList<TransitTrip>();


    public static void main (String[] args){


       //additional tool to geocode addresses: adress to coordinates
        //Geocode geocode = new Geocode();
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

        boolean readCSV = false;
        if (readCSV){
            ReadCSVFile readCSVFile = new ReadCSVFile();
            readCSVFile.readCsv();
            listOfStops = readCSVFile.getListOfStops();
            listOfLines = readCSVFile.getListOfLines();
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


    }

}
