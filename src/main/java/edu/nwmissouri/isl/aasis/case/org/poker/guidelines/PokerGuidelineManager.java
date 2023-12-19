package edu.nwmissouri.isl.aasis.case.org.poker.guidelines;


import edu.ksu.cis.macr.aasis.common.Connections;
import edu.ksu.cis.macr.aasis.common.IConnections;
import edu.nwmissouri.isl.aasis.case.org.poker.goals.PokerGoalParameters;
import edu.nwmissouri.isl.aasis.case.org.poker.guidelines.play.PokerPlayerGuidelinesBuilder;
import edu.nwmissouri.isl.aasis.case.org.poker.guidelines.play.IPokerPlayerGuidelines;
import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code GuidelineManager} reads goal guidelines (the top goal parameters) from a given XML
 * file.
 */
public enum PokerGuidelineManager {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(PokerGuidelineManager.class);
    private static final boolean debug = false;
    private static final String CUR_DIR = System.getProperty("user.dir");

    public synchronized static Map<UniqueIdentifier, Object> getGuidelines(String absPathToFile) {
        HashMap<UniqueIdentifier, Object> goalParameterValues = new HashMap<>();
        DocumentBuilder db = null;
        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            if (db == null) {
                throw new ParserConfigurationException();
            }
        } catch (ParserConfigurationException e3) {
            LOG.error("PARSER ERROR: Cannot read guidelines from initialize.xml ({}).", absPathToFile);
            System.exit(-44);
        }
        // Read goal parameters initialization file to configure goals
        Document configDoc = null;
        try {
            configDoc = db.parse(new File(absPathToFile));
            if (configDoc == null) {
                throw new IOException();
            }
        } catch (SAXException | IOException e2) {
            LOG.error("IO ERROR: Cannot read  guidelines from initialize.xml ({}).", absPathToFile);
            System.exit(-45);
        }
        initializeAuctionConnections(absPathToFile, goalParameterValues);
        initializePokerPlayerGuidelines(goalParameterValues, configDoc);
        if (debug) LOG.debug("Before returning, the top goal guidelines are {}.", goalParameterValues);
        return goalParameterValues;
    }

    private static void initializeAuctionConnections(String absPathToFile, HashMap<UniqueIdentifier, Object> goalParameterValues) {
        // get market connections
        IConnections connections = Connections.createConnections(absPathToFile, "connections");
        if (!connections.getListConnectionGuidelines().isEmpty()) {
            goalParameterValues.put(PokerGoalParameters.pokerConnections, connections);
        }
    }

    private static void initializePokerPlayerGuidelines(HashMap<UniqueIdentifier, Object> goalParameterValues, Document configDoc) {
        // check for auction guidelines...
        NodeList nodeList = configDoc.getElementsByTagName("PokerPlayerGuidelines");
        if (debug) LOG.debug("There are {} initial auction guidelines.", nodeList.getLength());
        if (nodeList.getLength() == 0) goalParameterValues.put(PokerGoalParameters.pokerPlayerGuidelines, null);
        else {
            IPokerPlayerGuidelines g = PokerPlayerGuidelinesBuilder.createGuidelines();
          PokerPlayerGuidelinesBuilder.readElements(nodeList, g);
            goalParameterValues.put(PokerGoalParameters.pokerPlayerGuidelines, g);
            if (debug)
                LOG.debug("{} read from Initialize.xml: {}.", PokerGoalParameters.pokerPlayerGuidelines, g.toString());
        }
    }

}
