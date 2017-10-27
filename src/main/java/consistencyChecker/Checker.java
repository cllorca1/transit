package consistencyChecker;

import transitSystem.TransitDataContainer;
import transitSystem.TransitLine;
import transitSystem.TransitStop;
import transitSystem.TransitTrip;

import java.util.ArrayList;

public interface Checker {


    void load(TransitDataContainer transitDataContainer);

    void check();

}
