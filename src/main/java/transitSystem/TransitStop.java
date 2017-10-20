package transitSystem;

import java.util.ArrayList;

/**
 * Created by carlloga on 21/11/16.
 */
public class TransitStop {
    long stopId = 0;
    String stopName = null;
    String lat;
    String lon;
    float x;
    float y;
    boolean bus;
    boolean tram;
    boolean subway;
    boolean stopPositionFlag;
    boolean printXMLNode = false;
    ArrayList<String> lines=new ArrayList<String>();
    ArrayList<Long> lineIds = new ArrayList<Long>();


    public TransitStop(long stopId, String stopName, String lat, String lon, boolean bus, boolean tram, boolean subway, boolean stopPositionFlag) {
        this.stopId = stopId;
        this.stopName = stopName;
        this.lat = lat;
        this.lon = lon;
        this.bus = bus;
        this.tram = tram;
        this.subway = subway;
        this.stopPositionFlag = stopPositionFlag;
        this.x = 0;
        this.y = 0;
    }

    public void addLine(String lineName, long lineId){
        this.lines.add(lineName);
        this.lineIds.add(lineId);
    }

    public long getStopId() {
        return stopId;
    }

    public String getStopName() {
        if (stopName!=null) {
            return stopName.replace(";", "-");
        } else{
            return null;
        }
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public boolean isBus() {
        return bus;
    }

    public boolean isTram() {
        return tram;
    }

    public boolean isStopPositionFlag() {
        return stopPositionFlag;
    }

    public boolean isSubway() {
        return subway;

    }

    public ArrayList<String> getLines() {
        return lines;
    }

    public ArrayList<Long> getLineIds() {
        return lineIds;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isPrintXMLNode() {
        return printXMLNode;
    }

    public void setPrintXMLNode(boolean printXMLNode) {
        this.printXMLNode = printXMLNode;
    }


}
