package transitSystem;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by carlloga on 25/11/16.
 */
public class TransitTrip {

    private TransitLine transitLine;
    private int departureTimeSeconds = 0;
    private int frequencyInSeconds = 0;
    private Map <Integer, TransitStopToStop> stopToStopList = new HashMap<Integer, TransitStopToStop>();

    private int sidingLink;

    private TransitTrip opposingTrip;

    public TransitTrip(int departureTimeSeconds, int frequencyInSeconds, TransitLine transitLine) {
        this.departureTimeSeconds = departureTimeSeconds;
        this.frequencyInSeconds = frequencyInSeconds;
        this.transitLine = transitLine;
    }

    public TransitTrip(TransitLine transitLine, int departureTimeSeconds, Map<Integer, TransitStopToStop> stopToStopList) {
        this.transitLine = transitLine;
        this.departureTimeSeconds = departureTimeSeconds;
        this.stopToStopList = stopToStopList;
    }

    public TransitLine getTransitLine() {
        return transitLine;
    }

    public int getDepartureTimeSeconds() {
        return departureTimeSeconds;
    }

    public Map<Integer, TransitStopToStop> getStopToStopList() {
        return stopToStopList;
    }

    public int getFrequencyInSeconds() { return frequencyInSeconds; }

    public void setFrequencyInSeconds(int frequencyInSeconds) {this.frequencyInSeconds = frequencyInSeconds; }

    public TransitTrip getOpposingTrip() {
        return opposingTrip;
    }
    public void setOpposingTrip(TransitTrip opposingTrip) {
        this.opposingTrip = opposingTrip;
    }

    public int getSidingLink() {
        return sidingLink;
    }

    public void setSidingLink(int sidingLink) {
        this.sidingLink = sidingLink;
    }
}
