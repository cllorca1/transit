package importOsm;

import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;
import transitSystem.TransitLine;
import utils.TransitUtil;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


public class CSVFrequencyReader {

    private static Logger logger = Logger.getLogger(CSVFrequencyReader.class);
    private String cvsSplitBy = ",";
    private ResourceBundle rb;
    private String fileName;
    BufferedReader br = null;
    String line = "";
    private Map<Long, TransitLine> lineMap;


    public CSVFrequencyReader(ResourceBundle rb) {
        this.rb = rb;
        fileName = rb.getString("csv.frequency.file");
        lineMap = new HashMap<>();

    }

    public void mapLines(ArrayList<TransitLine> listOfLines){
        for (TransitLine line : listOfLines){
            lineMap.put(line.getLineId(), line);
        }

    }


    public void readExternalFrequencies() {

        try {

            br = new BufferedReader(new FileReader(fileName));

            line = br.readLine();


            String[] header = line.split(cvsSplitBy);

            int posLineId = TransitUtil.findPositionInArray("lineId", header);
            int posHeadway = TransitUtil.findPositionInArray("headway", header);



            while ((line = br.readLine()) != null) {

                String[] row = line.split(cvsSplitBy);

                long lineId = Long.parseLong(row[posLineId]);
                double headway = Double.parseDouble(row[posHeadway]);

                try{
                    TransitLine line = lineMap.get(lineId);
                    line.setHeadway(headway);

                    logger.info(line.getHeadway());

                } catch (NullPointerException e){
                    logger.warn("Line not found: " + lineId);
                }


                }

            logger.info("Frequency file was read");


            } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }


}
