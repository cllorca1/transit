package importOsm;

import org.apache.log4j.Logger;
import transitSystem.LineType;
import transitSystem.TransitLine;
import transitSystem.TransitVehicle;
import utils.TransitUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class VehicleTypeReader {


    private static Logger logger = Logger.getLogger(VehicleTypeReader.class);
    private String cvsSplitBy = ",";
    private ResourceBundle rb;
    private String fileName;
    BufferedReader br = null;
    String line = "";

    public VehicleTypeReader(ResourceBundle rb) {
        this.rb = rb;
        fileName = rb.getString("vehicle.type.file");


    }



    public Map < LineType, TransitVehicle> createTransitVehicleTypes() {

        Map < LineType, TransitVehicle> vehicleTypeMap = new HashMap<>();

        try {

            br = new BufferedReader(new FileReader(fileName));

            line = br.readLine();


            String[] header = line.split(cvsSplitBy);

            int posCode = TransitUtil.findPositionInArray("code", header);
            int posDesc = TransitUtil.findPositionInArray("description", header);
            int posSeatCap = TransitUtil.findPositionInArray("seat_capacity", header);
            int posStandCap = TransitUtil.findPositionInArray("standing_capacity", header);
            int posLength = TransitUtil.findPositionInArray("length", header);



            while ((line = br.readLine()) != null) {

                String[] row = line.split(cvsSplitBy);

                LineType type = LineType.convertFromCode(Integer.parseInt(row[posCode]));
                String description = row[posDesc];
                int seatCapacity = Integer.parseInt(row[posSeatCap]);
                int standCapacity = Integer.parseInt(row[posStandCap]);
                int length = Integer.parseInt(row[posLength]);

                vehicleTypeMap.put(type, new TransitVehicle(type, description, seatCapacity, standCapacity, length));

            }

            logger.info("Vehicle type file was read");

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return vehicleTypeMap;


    }


}