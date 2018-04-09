package travelTimeUsingMATSim;


import org.apache.log4j.Logger;
import transitSystem.TransitDataContainer;

public class TravelTimeMatsim {

    private static Logger logger = Logger.getLogger(TravelTimeMatsim.class);
    private TransitDataContainer transitDataContainer;

    public TravelTimeMatsim(TransitDataContainer transitDataContainer){
        this.transitDataContainer = transitDataContainer;
    }


}
