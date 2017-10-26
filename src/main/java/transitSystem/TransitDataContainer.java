package transitSystem;

import com.google.maps.model.VehicleType;

import java.util.ArrayList;
import java.util.Map;

public class TransitDataContainer {

    private ArrayList<TransitStop> listOfStops;
    private ArrayList<TransitLine> listOfLines;
    private ArrayList<TransitTrip> listOfTrips;
    private Map<LineType, TransitVehicle> vehicleTypeMap;

    public TransitDataContainer(ArrayList<TransitStop> listOfStops, ArrayList<TransitLine> listOfLines, ArrayList<TransitTrip> listOfTrips, Map<LineType, TransitVehicle> vehicleTypeMap) {
        this.listOfStops = listOfStops;
        this.listOfLines = listOfLines;
        this.listOfTrips = listOfTrips;
        this.vehicleTypeMap = vehicleTypeMap;
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

    public Map<LineType, TransitVehicle> getVehicleTypeMap() {
        return vehicleTypeMap;
    }

    public void setListOfStops(ArrayList<TransitStop> listOfStops) {
        this.listOfStops = listOfStops;
    }

    public void setListOfLines(ArrayList<TransitLine> listOfLines) {
        this.listOfLines = listOfLines;
    }

    public void setListOfTrips(ArrayList<TransitTrip> listOfTrips) {
        this.listOfTrips = listOfTrips;
    }

    public void setVehicleTypeMap(Map<LineType, TransitVehicle> vehicleTypeMap) {
        this.vehicleTypeMap = vehicleTypeMap;
    }
}
