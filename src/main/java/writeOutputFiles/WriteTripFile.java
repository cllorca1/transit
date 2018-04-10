package writeOutputFiles;

import transitSystem.TransitDataContainer;
import transitSystem.TransitStop;
import transitSystem.TransitStopToStop;
import transitSystem.TransitTrip;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by carlloga on 28/11/16.
 */
public class WriteTripFile {

    private String csvSplitBy = ",";

    public void write(String outputFile, TransitDataContainer transitDataContainer) {

        ArrayList<TransitTrip> listOfTrips = transitDataContainer.getListOfTrips();

        try {

            PrintWriter pw = new PrintWriter(new FileWriter(outputFile, false));
            pw.println("lineId,lineRef,fromStopId,fromStop,toStopId,toStop,departureTime,arrivalTime");

            for (TransitTrip transitTrip : listOfTrips){

                Map<Integer, TransitStopToStop> stopToStopMap = transitTrip.getStopToStopList();

                if (transitTrip.getFrequencyInSeconds()>0){
                    pw.println(transitTrip.getTransitLine().getLineName() + csvSplitBy + transitTrip.getFrequencyInSeconds()+ csvSplitBy + "FREQUENCY");
                }

                for (TransitStopToStop transitStopToStop : stopToStopMap.values()){

                    pw.println( transitTrip.getTransitLine().getLineId() + csvSplitBy
                            + transitTrip.getTransitLine().getLineName() + csvSplitBy
                            + transitStopToStop.getOrigTransitStop().getStopId() + csvSplitBy
                            + transitStopToStop.getOrigTransitStop().getStopName() + csvSplitBy
                            + transitStopToStop.getDestTransitStop().getStopId() + csvSplitBy
                            + transitStopToStop.getDestTransitStop(). getStopName() + csvSplitBy
                            + transitStopToStop.getDepartureTime() + csvSplitBy
                            + transitStopToStop.getArrivalTime());
                }

            }

            pw.flush();
            pw.close();



        } catch (IOException e) {
            e.printStackTrace();


        }

        }
}
