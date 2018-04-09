package travelTimeUsingMATSim;


import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.*;
import org.matsim.contrib.analysis.filters.population.PersonIntersectAreaFilter;
import org.matsim.contrib.dvrp.router.DijkstraTree;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.utils.leastcostpathtree.LeastCostPathTree;
import transitSystem.TransitDataContainer;
import transitSystem.TransitLine;
import transitSystem.TransitStop;
import transitSystem.TransitStopToStop;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TravelTimeMatsim {

    private static Logger logger = Logger.getLogger(TravelTimeMatsim.class);
    private TransitDataContainer transitDataContainer;
    private Config config;
    private Controler controler;
    private Network network;
    private DijkstraTree dijkstraTime;
    private double departureTime;


    public TravelTimeMatsim(TransitDataContainer transitDataContainer){
        this.transitDataContainer = transitDataContainer;
    }

    public void loadMatsim(String networkFile, String outputDirectory){

        config = ConfigUtils.createConfig();
        config.network().setInputFile(networkFile);
        config.controler().setOutputDirectory(outputDirectory);
        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        config.controler().setLastIteration(1);
        config.controler().setWriteEventsInterval(1);
        Population population = createMinimalPopulation();
        MutableScenario scenario = (MutableScenario) ScenarioUtils.loadScenario(config);
        scenario.setPopulation(population);
        PlanCalcScoreConfigGroup.ActivityParams homeActivity = new PlanCalcScoreConfigGroup.ActivityParams("things");
        homeActivity.setTypicalDuration(12 * 60 * 60);
        config.planCalcScore().addActivityParams(homeActivity);
        StrategyConfigGroup.StrategySettings strategySettings = new StrategyConfigGroup.StrategySettings();
        strategySettings.setStrategyName("ChangeExpBeta");
        strategySettings.setWeight(0.8);
        config.strategy().addStrategySettings(strategySettings);
        controler = new Controler(scenario);
        controler.run();

        network = scenario.getNetwork();
        TravelTime travelTime;
        TravelDisutility travelTimeDisutility;

        travelTime = controler.getLinkTravelTimes();
        travelTimeDisutility = controler.getTravelDisutilityFactory().createTravelDisutility(travelTime);

        dijkstraTime = new DijkstraTree(network, travelTimeDisutility, travelTime);
        departureTime = 8*3600;


    }

    private Population createMinimalPopulation() {

        Population population = PopulationUtils.createPopulation(config);
        PopulationFactory factory = population.getFactory();
        Person person = factory.createPerson(Id.createPersonId(1));
        Plan plan = factory.createPlan();
        Coord coord1 = new Coord(transitDataContainer.getListOfStops().get(0).getX(), transitDataContainer.getListOfStops().get(0).getY());
        Activity activity = factory.createActivityFromCoord("things", coord1);
        activity.setEndTime(8*3600);
        plan.addActivity(activity);
        plan.addLeg(factory.createLeg(TransportMode.car));
        Coord coord2 = new Coord(transitDataContainer.getListOfStops().get(10).getX(), transitDataContainer.getListOfStops().get(10).getY());
        Activity secondActivity = factory.createActivityFromCoord("things", coord2);
        plan.addActivity(secondActivity);
        person.addPlan(plan);
        population.addPerson(person);

        return population;
    }

    public void getTravelTimes(){
        for (TransitLine line : transitDataContainer.getListOfLines()){
            if (!line.isValidity()){
                getLineTravelTimes(line);
                logger.info("Completed line " + line.getLineName() + " in MATSim");
            }



        }

    }

    private void getLineTravelTimes(TransitLine line) {
        Map<Integer, TransitStop> stopMap = line.getStopList();

        Iterator<TransitStop> stopIterator = stopMap.values().iterator();
        TransitStop fromStop = stopIterator.next();
        Coord fromStopCoord = new Coord(fromStop.getX(), fromStop.getY());
        TransitStop toStop;
        int departureTimeInSeconds = 8*60*60;

        Map <Integer, TransitStopToStop> stopToStopList = new HashMap<>();
        int seq =0;
        while(stopIterator.hasNext()){
            if (stopIterator.hasNext()){
                toStop = stopIterator.next();
                Coord toStopCoord = new Coord(fromStop.getX(), fromStop.getY());
                int arrivalTimeInSeconds = (int) (calculateTimeBetweenStops(fromStopCoord, toStopCoord) + departureTimeInSeconds) ;
                TransitStopToStop transitStopToStop = new TransitStopToStop(fromStop, toStop, arrivalTimeInSeconds, departureTimeInSeconds);
                stopToStopList.put(seq, transitStopToStop);
                seq++;
                departureTimeInSeconds = arrivalTimeInSeconds + 20;
                fromStop = toStop;
            }

        }


    }

    private float calculateTimeBetweenStops(Coord fromStopCoord, Coord toStopCoord) {


        Node originNode = NetworkUtils.getNearestNode(network, fromStopCoord);
        Node destinationNode = NetworkUtils.getNearestNode(network, toStopCoord);

        dijkstraTime.calcLeastCostPathTree(originNode, departureTime);

        return (float)(dijkstraTime.getTime(destinationNode) - departureTime);
    }


}
