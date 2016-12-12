package transitSystem;

import java.util.Map;

/**
 * Created by carlloga on 21/11/16.
 */
public class TransitLine {
    long lineId;
    String fromStop;
    String toStop;
    boolean bus;
    boolean subway;
    boolean tram;
    String lineName;
    Map<Integer,TransitStop> stopList;
    Map<Integer, Long> linkList;

    public TransitLine(long lineId, String fromStop, String toStop, boolean bus, boolean subway, boolean tram, String lineName, Map<Integer, TransitStop> stopList) {
        this.lineId = lineId;
        this.fromStop = fromStop;
        this.toStop = toStop;
        this.bus = bus;
        this.subway = subway;
        this.tram = tram;
        this.lineName = lineName;
        this.stopList = stopList;
    }

    public TransitLine(long lineId, String fromStop, String toStop, boolean bus, boolean subway, boolean tram, String lineName, Map<Integer, TransitStop> stopList, Map<Integer, Long> linkList) {
        this.lineId = lineId;
        this.fromStop = fromStop;
        this.toStop = toStop;
        this.bus = bus;
        this.subway = subway;
        this.tram = tram;
        this.lineName = lineName;
        this.stopList = stopList;
        this.linkList = linkList;
    }

    public long getLineId() {
        return lineId;
    }

    public String getFromStop() {
        return fromStop;
    }

    public String getToStop() {
        return toStop;
    }

    public boolean isBus() {
        return bus;
    }

    public boolean isSubway() {
        return subway;
    }

    public boolean isTram() {
        return tram;
    }

    public String getLineName() {
        return lineName;
    }

    public Map<Integer, TransitStop> getStopList() {
        return stopList;
    }

    public Map<Integer, Long> getLinkList() {
        return linkList;
    }

    public void setLinkList(Map<Integer, Long> linkList) {
        this.linkList = linkList;
    }
}
