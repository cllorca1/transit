package writeMATSimXMLFiles;

import org.cts.CRSFactory;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CRSException;
import org.cts.crs.GeodeticCRS;
import org.cts.op.CoordinateOperation;
import org.cts.op.CoordinateOperationFactory;
import org.cts.op.NonInvertibleOperationException;
import org.cts.op.transformation.SevenParameterTransformation;
import org.cts.registry.EPSGRegistry;
import org.cts.registry.RegistryManager;
import transitSystem.TransitStop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.cts.op.transformation.SevenParameterTransformation.createBursaWolfTransformation;

/**
 * Created by carlloga on 15/12/16.
 */
public class TransformCoordinates {


    private  List<CoordinateOperation> coortzdOps;

    public TransformCoordinates() throws CRSException {


        CRSFactory cRSFactory = new CRSFactory();
        RegistryManager registryManager = cRSFactory.getRegistryManager();
        registryManager.addRegistry(new EPSGRegistry());


        try {
            GeodeticCRS sourceGCRS = (GeodeticCRS) cRSFactory.getCRS("EPSG:4326");
            GeodeticCRS targetGCRS = (GeodeticCRS) cRSFactory.getCRS("EPSG:31468");
            //System.out.println( sourceGCRS.getName());
            //System.out.println( targetGCRS.getName());

            coortzdOps = new ArrayList<CoordinateOperation>(
                    CoordinateOperationFactory.createCoordinateOperations(sourceGCRS, targetGCRS));


        } catch (CRSException e) {
            e.printStackTrace();

        }

    }

    public void transformCoordinates(TransitStop transitStop) {


        try {

            double[] coord = new double[]{Double.parseDouble(transitStop.getLon()), Double.parseDouble(transitStop.getLat())};
            //SevenParameterTransformation op = createBursaWolfTransformation(612.4, 77, 440.2, -0.054, 0.057, -2.797, 2.55);

            for (int i = 0; i < coortzdOps.size(); i++) {
                CoordinateOperation op = coortzdOps.get(i);

                coord = op.transform(coord);
            }

            transitStop.setX((float) coord[0]);
            transitStop.setY((float) coord[1]);


        } catch (IllegalCoordinateException e) {
            e.printStackTrace();

        }
    }

}
