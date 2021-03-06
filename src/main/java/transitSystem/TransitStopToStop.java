package transitSystem;

/**
 * Created by carlloga on 25/11/16.
 */
public class TransitStopToStop {
    private TransitStop origTransitStop;
    private TransitStop destTransitStop;
    private int arrivalTime;
    private int departureTime;

    private int networkLink;



    public TransitStopToStop(TransitStop origTransitStop, TransitStop destTransitStop, int arrivalTime, int departureTime) {
        this.origTransitStop = origTransitStop;
        this.destTransitStop = destTransitStop;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
    }

    public TransitStop getOrigTransitStop() {
        return origTransitStop;
    }

    public int getDepartureTime() {
        return departureTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public TransitStop getDestTransitStop() {
        return destTransitStop;
    }

    public int getNetworkLink() {
        return networkLink;
    }

    public void setNetworkLink(int networkLink) {
        this.networkLink = networkLink;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setDepartureTime(int departureTime) {
        this.departureTime = departureTime;
    }
}
