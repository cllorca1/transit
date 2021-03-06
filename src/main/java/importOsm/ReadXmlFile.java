package importOsm;


import org.w3c.dom.*;
import transitSystem.OSMNode;
import transitSystem.TransitStop;
import transitSystem.TransitLine;


import java.io.FileWriter;

import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by carlloga on 21/11/16.
 */
public class ReadXmlFile {


    private ArrayList<TransitStop> listOfStops = new ArrayList<TransitStop>();
    private ArrayList<TransitLine> listOfLines = new ArrayList<TransitLine>();

    private HashMap<Long, TransitStop> stopMap = new HashMap();

    private HashMap<Long, OSMNode> nodeMap = new HashMap();

    private ResourceBundle rb;

    public ReadXmlFile(ResourceBundle rb) {
        this.rb = rb;
    }

    public void readXMLFile() {

        String fileName = rb.getString("xml.osm.file");

        listOfStops = getStationsFromXML(fileName);
        listOfLines = getLinesFromXML(fileName);


    }

    public ArrayList<TransitStop> getStationsFromXML(String fileName) {

        ArrayList<TransitStop> listOfStops = new ArrayList<TransitStop>();

        try {
            File inputFile = new File(fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("node");

            // to find nodes = stop_positions in the OSM file
            int numberStops = 0;
            //for (int i = 0; i < 5000; i++) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element nodeElement = (Element) node;
                    long id = Long.parseLong(nodeElement.getAttribute("id"));
                    //System.out.println(id);
                    String lat = nodeElement.getAttribute("lat");
                    String lon = nodeElement.getAttribute("lon");

                    //this is only needed to store nodes to give coordinates to way-based stops
                    nodeMap.put(id, new OSMNode(id, lon, lat));

                    String stopName = "null";
                    boolean bus = false;
                    boolean tram = false;
                    boolean subway = false;
                    boolean stop = false;
                    boolean stopPositionFlag = false;

                    NodeList childNodes = nodeElement.getChildNodes();
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node childNode = childNodes.item(j);
                        if (childNode.getNodeName().equals("tag")) {
                            if (childNode.getAttributes().getLength() == 2) {
                                String attributeName = childNode.getAttributes().item(0).getNodeValue();
                                String attributeValue = childNode.getAttributes().item(1).getNodeValue();
                                //System.out.println(attributeName + "=" + attributeValue);
                                if (attributeName.equals("public_transport") & attributeValue.equals("stop_position")) {
                                    stop = true;
                                    stopPositionFlag = true;
                                } else if (attributeName.equals("highway") & attributeValue.equals("bus_stop")) {
                                    stop = true;
                                } else if (attributeName.equals("name")) {
                                    stopName = attributeValue;
                                } else if (attributeName.equals("tram") & attributeValue.equals("yes")) {
                                    tram = true;
                                } else if (attributeName.equals("subway") & attributeValue.equals("yes")) {
                                    subway = true;
                                } else if (attributeName.equals("bus") & attributeValue.equals("yes")) {
                                    bus = true;

                                }

                            }
                        }
                    }
                    if (stop) {
                        TransitStop transitStop = new TransitStop(id, stopName, lat, lon, bus, tram, subway, stopPositionFlag);
                        /*pw.println(transitStop.getStopId() + ";"
                                + transitStop.getStopName() + ";"
                                + transitStop.getLat()+ ";"
                                + transitStop.getLon()+ ";"
                                + transitStop.isBus() + ";"
                                + transitStop.isTram() + ";"
                                + transitStop.isSubway() + ";"
                                + stopPositionFlag);*/
                        System.out.println("Stops: " + numberStops + " id: " + id + " name: " + stopName + " type: " + stopPositionFlag);
                        listOfStops.add(transitStop);
                        numberStops++;
                        stopMap.put(id, transitStop);
                    }

                }
            }

            System.out.println("The total number of nodes in the file is " + nodeMap.size());

            //add additional stations that are coded as ways/platform
            nodeList = doc.getElementsByTagName("way");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element nodeElement = (Element) node;

                    long id = Long.parseLong(nodeElement.getAttribute("id"));
                    boolean stop = false;
                    String stopName = "";
                    long nodeId = 0L;
                    boolean bus = false;
                    boolean tram = false;
                    boolean subway = false;


                    NodeList childNodes = nodeElement.getChildNodes();
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node childNode = childNodes.item(j);
                        if (childNode.getNodeName().equals("tag")) {

                            if (childNode.getAttributes().getLength() == 2) {
                                String attributeName = childNode.getAttributes().item(0).getNodeValue();
                                String attributeValue = childNode.getAttributes().item(1).getNodeValue();

                                if (attributeName.equals("public_transport") & attributeValue.equals("platform")) {
                                    stop = true;
                                } else if (attributeName.equals("name")) {
                                    stopName = attributeValue;
                                } else if (attributeName.equals("tram") & attributeValue.equals("yes")) {
                                    tram = true;
                                } else if (attributeName.equals("subway") & attributeValue.equals("yes")) {
                                    subway = true;
                                } else if (attributeName.equals("bus") & attributeValue.equals("yes")) {
                                    bus = true;
                                }
                            }
                        } else if (childNode.getNodeName().equals("nd")) {
                            String attributeValue = childNode.getAttributes().item(0).getNodeValue();
                            nodeId = Long.parseLong(attributeValue);
                        }
                    }
                    if (stop) {
                        if (nodeMap.containsKey(nodeId)) {
                            OSMNode osmNode = nodeMap.get(nodeId);
                            TransitStop transitStop = new TransitStop(id, stopName, osmNode.getLat(), osmNode.getLon(), bus, tram, subway, false);
                            stopMap.put(id, transitStop);
                            listOfStops.add(transitStop);
                            numberStops++;

                            System.out.println("Stops: " + numberStops + " id: " + id + " name: " + stopName + " type: " + "WAY-PLATFORM");
                        }
                    }
                }
            }
            //pw.flush();
            //pw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return listOfStops;
    }

    public ArrayList<TransitLine> getLinesFromXML(String fileName) {

        ArrayList<TransitLine> listOfLines = new ArrayList<TransitLine>();

        try {

            File inputFile = new File(fileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();


            //System.out.println("Created map of stops: " + stopMap.size());

            //to print out lines
            //PrintWriter pw2 = new PrintWriter(new FileWriter("output/linesListv2.csv", true));
            //pw2.println("line number; lineId; lineRef; from; to; bus; subway; sequence; stopId; stopName");

            //to read lines in the OSM file
            //int numberLines = 0;
            NodeList relationList = doc.getElementsByTagName("relation");
            for (int i = 0; i < relationList.getLength(); i++) {
                Node node = relationList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element nodeElement = (Element) node;
                    long lineId = Long.parseLong(nodeElement.getAttribute("id"));
                    boolean line = false;
                    boolean bus = false;
                    boolean tram = false;
                    boolean subway = false;
                    String fromStop = null;
                    String toStop = null;
                    String lineName = "";
                    Map<Integer, TransitStop> stopList = new HashMap<Integer, TransitStop>();
                    int stopSequence = 0;
                    Map<Integer, Long> linkList = new HashMap<Integer, Long>();
                    int linkSequence = 0;
                    NodeList childNodes = nodeElement.getChildNodes();
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node childNode = childNodes.item(j);
                        if (childNode.getNodeName().equals("tag")) {
                            if (childNode.getAttributes().getLength() == 2) {
                                String attributeName = childNode.getAttributes().item(0).getNodeValue();
                                String attributeValue = childNode.getAttributes().item(1).getNodeValue();
                                if (attributeName.equals("type") & attributeValue.equals("route")) {
                                    line = true;
                                } else if (attributeName.equals("route") & attributeValue.equals("bus")) {
                                    line = true;
                                    bus = true;
                                } else if (attributeName.equals("route") & attributeValue.equals("tram")) {
                                    line = true;
                                    tram = true;
                                } else if (attributeName.equals("route") & attributeValue.equals("subway")) {
                                    line = true;
                                    subway = true;
                                } else if (attributeName.equals("ref")) {
                                    lineName = attributeValue;
                                } else if (attributeName.equals("from")) {
                                    fromStop = attributeValue;
                                } else if (attributeName.equals("to")) {
                                    toStop = attributeValue;
                                }
                            }


                        }
                    }
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node childNode = childNodes.item(j);
                        if (childNode.getNodeName().equals("member")) {
                            if (childNode.getAttributes().getLength() == 3) {
                                //because not all relation > member have 3 attributes, they are not lines, but other type of relation in OSM
                                String type = "";
                                String ref = "";
                                String role = "";
                                for (int itemNumber = 0; itemNumber < 3; itemNumber++) {
                                    if (childNode.getAttributes().item(itemNumber).getNodeName().equals("type")) {
                                        type = childNode.getAttributes().item(itemNumber).getNodeValue();
                                    } else if (childNode.getAttributes().item(itemNumber).getNodeName().equals("ref")) {
                                        ref = childNode.getAttributes().item(itemNumber).getNodeValue();
                                    } else if (childNode.getAttributes().item(itemNumber).getNodeName().equals("role")) {
                                        role = childNode.getAttributes().item(itemNumber).getNodeValue();
                                    }
                                }

                                //the next adds every node/link as an stop
                                if (type.equals("node") | (role.equals("platform") & type.equals("way"))) {
                                    long stopId = Long.parseLong(ref);
                                    //System.out.println(stopId);
                                    // the stop must be in the map of stops
                                    if (stopMap.get(stopId) != null) {
                                        TransitStop transitStop = stopMap.get(stopId);
                                        stopList.put(stopSequence, transitStop);
                                        transitStop.addLine(lineName, lineId);
                                        stopSequence++;
                                    }

                                } else if (role.equals("") & type.equals("way")) {
                                    Long linkId = Long.parseLong(ref);
                                    linkList.put(linkSequence, linkId);
                                    linkSequence++;
                                    //System.out.println("The link " + linkId + " was added to the line " + lineName);
                                }
                            }
                        }
                    }

                    if (line) {
                        TransitLine transitLine = new TransitLine(lineId, fromStop, toStop,
                                bus, subway, tram, lineName, stopList, linkList);
                        listOfLines.add(transitLine);

                        /*for (int seq : stopList.keySet()) {
                            pw2.println(numberLines + ";"
                                    + lineId + ";"
                                    + lineName + ";"
                                    + fromStop + ";"
                                    + toStop + ";"
                                    + bus + ";"
                                    + tram + ";"
                                    + subway + ";"
                                    + seq + ";"
                                    + stopList.get(seq).getStopId() + ";"
                                    + stopList.get(seq).getStopName());
                            System.out.println(numberLines + ";"
                                    + lineId + ";"
                                    + lineName + ";"
                                    + fromStop + ";"
                                    + toStop + ";"
                                    + bus + ";"
                                    + tram + ";"
                                    + subway + ";"
                                    + seq + ";"
                                    + stopList.get(seq).getStopId() + ";"
                                    + stopList.get(seq).getStopName());
                        }*/
                    }
                    //numberLines++;
                }
            }
            //pw2.flush();
            //pw2.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return listOfLines;
    }

    public ArrayList<TransitStop> getListOfStops() {
        return listOfStops;
    }

    public ArrayList<TransitLine> getListOfLines() {
        return listOfLines;
    }
}

