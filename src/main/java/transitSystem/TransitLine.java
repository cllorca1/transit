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
        if (fromStop!=null) {
            return fromStop.replace(";", "-");
        } else{
        return null;
        }
    }

    public String getToStop() {
        if (toStop!=null) {
            return toStop.replace(";", "-");
        } else{
            return null;
        }
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
        if (lineName!=null) {
            return lineName.replace(";", "-");
        } else{
            return null;
        }
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

    public void setLineId(long lineId) {
        this.lineId = lineId;
    }

}
