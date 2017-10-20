package writeMATSimXMLFiles;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.*;
import org.matsim.contrib.accessibility.utils.MergeNetworks;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.LinkFactory;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.scenario.ScenarioUtils;

//created by Carlos Llorca 20.10.17

//adapted from org.matsim.contrib.accessibility.utils.MergeNetworks to provide a merged network as an output and write it in a file


import java.util.Iterator;

public class NetworkMergeTool {

    private String roadNetowrkFileName;
    private String transitNetworkFileName;
    private String finalNetowrkFileName;

    public NetworkMergeTool(String roadNetowrkFileName, String transitNetworkFileName, String finalNetowrkFileName) {
        this.roadNetowrkFileName = roadNetowrkFileName;
        this.transitNetworkFileName = transitNetworkFileName;
        this.finalNetowrkFileName = finalNetowrkFileName;
    }

    public void mergeWithRoadNetwork(){

        Config config1 = ConfigUtils.createConfig();
        config1.network().setInputFile(roadNetowrkFileName);

        Scenario scenario1 = ScenarioUtils.loadScenario(config1);
        Network network1 = scenario1.getNetwork();

        Config config2 = ConfigUtils.createConfig();
        config2.network().setInputFile(transitNetworkFileName);

        Scenario scenario2 = ScenarioUtils.loadScenario(config2);
        Network network2 = scenario2.getNetwork();

        Network finalNetwork = merge(network1, "", network2);

        new NetworkWriter(finalNetwork).write(finalNetowrkFileName);


    }

    public Network merge(Network baseNetwork, String addPrefix, Network addNetwork) {

        NetworkFactory factory = baseNetwork.getFactory();
        Iterator addNetowrkIterator = addNetwork.getNodes().values().iterator();

        while(addNetowrkIterator.hasNext()) {
            Node node = (Node)addNetowrkIterator.next();
            Node node2 = factory.createNode(Id.create(addPrefix + node.getId().toString(), Node.class), node.getCoord());
            baseNetwork.addNode(node2);
        }

        addNetowrkIterator = addNetwork.getLinks().values().iterator();

        while(addNetowrkIterator.hasNext()) {
            Link link = (Link)addNetowrkIterator.next();
            Id<Node> fromNodeId = Id.create(addPrefix + link.getFromNode().getId().toString(), Node.class);
            Id<Node> toNodeId = Id.create(addPrefix + link.getToNode().getId().toString(), Node.class);
            Node fromNode = (Node)baseNetwork.getNodes().get(fromNodeId);
            Node toNode = (Node)baseNetwork.getNodes().get(toNodeId);
            Link link2 = factory.createLink(Id.create(addPrefix + link.getId().toString(), Link.class), fromNode, toNode);
            link2.setAllowedModes(link.getAllowedModes());
            link2.setCapacity(link.getCapacity());
            link2.setFreespeed(link.getFreespeed());
            link2.setLength(link.getLength());
            link2.setNumberOfLanes(link.getNumberOfLanes());
            baseNetwork.addLink(link2);
        }

        return baseNetwork;

    }



}
