package transitSystem;

/**
 * Created by carlloga on 14/2/17.
 */
public class OSMNode {

    private long id;
    private String lon;
    private String lat;

    public OSMNode(long id, String lon, String lat) {
        this.id = id;
        this.lon = lon;
        this.lat = lat;
    }

    public long getId() {
        return id;
    }

    public String getLon() {
        return lon;
    }

    public String getLat() {
        return lat;
    }
}
