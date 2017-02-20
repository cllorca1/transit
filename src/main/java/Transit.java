

import com.sun.jndi.dns.ResourceRecord;
import geocoding.Geocode;
import geocoding.GetTravelTimeBetweenPoints;
import importOsm.ReadCSVFile;
import importOsm.ReadXmlFile;
import sun.security.tools.keytool.Resources_sv;
import transitSystem.TransitLine;
import transitSystem.TransitStop;
import transitSystem.TransitTrip;
import travelTimeFromGoogle.LineFrequency;
import travelTimeFromGoogle.TravelTimeFromGoogle;
import travelTimeFromGoogle.TravelTimeMatrixFromGoogle;
import writeMATSimXMLFiles.TransformCoordinates;
import writeMATSimXMLFiles.WriteXMLRaiFiles;
import writeOutputFiles.WriteOutputs;

import java.io.*;
import java.util.ArrayList;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;





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

        //travel time by bike
        /*GetTravelTimeBetweenPoints ttBike = new GetTravelTimeBetweenPoints(rb);
        try {
            ttBike.getBicycleTimes();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

       //additional tool to geocode addresses: address to coordinates
        //Geocode geocode = new Geocode(rb);
        //geocode.geocodeAdress();

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


        }

        boolean getTimes= Boolean.parseBoolean(rb.getString("get.times"));
        if (getTimes) {
            TravelTimeFromGoogle travelTimeFromGoogle = new TravelTimeFromGoogle(rb);
            travelTimeFromGoogle.getTimes(listOfLines);
            listOfTrips = travelTimeFromGoogle.getListOfTrips();
        }

        boolean getTimesUsingMatrix= Boolean.parseBoolean(rb.getString("get.times.matrix"));
        if (getTimesUsingMatrix) {
            TravelTimeMatrixFromGoogle travelTimeMAtrixFromGoogle = new TravelTimeMatrixFromGoogle(rb);
            travelTimeMAtrixFromGoogle.getTimesFromMatrix(listOfLines);
            listOfTrips = travelTimeMAtrixFromGoogle.getListOfTrips();
        }

        boolean getFrequencies = Boolean.parseBoolean(rb.getString("get.frequencies"));
        if (getFrequencies){
            LineFrequency lineFrequency = new LineFrequency();
            lineFrequency.getLineFrequency(listOfLines);
            listOfTrips = lineFrequency.getListOfTrips();
        }

        boolean writeOutputFiles = Boolean.parseBoolean(rb.getString("write.output.CSV"));
        //write outputs

        if (writeOutputFiles) {
            WriteOutputs writeOutputs = new WriteOutputs(rb);
            System.out.println("Number of trips = " + listOfTrips.size());
            writeOutputs.writeOutputs(listOfStops, listOfLines, listOfTrips);
        }

        boolean writeMATSimFiles = Boolean.parseBoolean(rb.getString("write.output.XML"));
        if (writeMATSimFiles) {

            WriteXMLRaiFiles writeXMLRaiFiles = new WriteXMLRaiFiles(rb);
            writeXMLRaiFiles.writeXMLFiles(listOfStops, listOfLines, listOfTrips);
        }

    }

    public ResourceBundle getRb() {
        return rb;
    }
}
