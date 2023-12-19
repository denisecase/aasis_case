package edu.nwmissouri.isl.aasis.case.self.guidelines;

import edu.ksu.cis.macr.aasis.common.IConnections;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.goals.TicTacToeGoalParameters;
import edu.nwmissouri.isl.aasis.case.org.poker.goals.PokerGoalParameters;
import edu.nwmissouri.isl.aasis.case.org.poker.guidelines.play.PokerPlayerGuidelinesBuilder;
import edu.nwmissouri.isl.aasis.case.org.poker.guidelines.deal.PokerGuidelines;
import edu.nwmissouri.isl.aasis.case.org.poker.guidelines.deal.PokerGuidelinesBuilder;
import edu.nwmissouri.isl.aasis.case.self.goals.SelfGoalParameters;
import edu.nwmissouri.isl.aasis.case.self.guidelines.*;
import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.nwmissouri.isl.aasis.case.self.plan_selector.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 The {@code SelfGuidelineManager} reads goal guidelines (the top goal parameters) from a given XML
 file.
 */
public enum SelfGuidelineManager {
  INSTANCE;
  private static final Logger LOG = LoggerFactory.getLogger(SelfGuidelineManager.class);
  private static final boolean debug = false;

  public synchronized static Map<UniqueIdentifier, Object> getGuidelines(final String absPathToFile) {
    LOG.info("Entering getGuidelines(absPathToFile={})", absPathToFile);
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
    initializeTicTacToeConnections(absPathToFile, goalParameterValues);

    goalParameterValues.put(SelfGoalParameters.pokerGuidelines,
        PokerGuidelinesBuilder.createGuidelines(configDoc));
    goalParameterValues.put(SelfGoalParameters.pokerPlayerGuidelines,
        PokerPlayerGuidelinesBuilder.createGuidelines(configDoc));

 //   initializeHomeGuidelines(goalParameterValues, configDoc);

    if (debug)
      LOG.debug("Before returning, the top goal guidelines are {}.", goalParameterValues);
    return goalParameterValues;
  }

  // private static void initializeHomeGuidelines(HashMap<UniqueIdentifier, Object> goalParameterValues,
  //     Document configDoc) {
  //   LOG.debug("Entering initializeHomeGuidelines(goalParameterValues={}, configDoc={}. ", goalParameterValues,
  //       configDoc);
  //   NodeList nodeList;
  //   nodeList = configDoc.getElementsByTagName("playerGuidelines");
  //   PokerPlayerGuidelines g = null;
  //   if (debug)
  //     LOG.debug("There are {} initial home guidelines.", nodeList.getLength());
  //   if (nodeList.getLength() > 0) {
  //     g = new PokerPlayerGuidelines();
  //     readElements(nodeList, g);
  //     goalParameterValues.put(AgentGoalParameters.playerGuidelines, g);

  //   }
  //   LOG.info("Exiting initializeHomeGuidelines: g={}. ", g);
  // }

  private static void readElements(NodeList nodeList, PokerGuidelines g) {
    LOG.info("Entering readElements(class={},nodeList={}", g.getClass().toString(), nodeList);
    for (int i = 0; i < nodeList.getLength(); ++i) {
      Element element = (Element) nodeList.item(i);

      // try {
      //   g.setNetDeltaP(Double.parseDouble(element.getAttribute("netDeltaP")));
      // } catch (Exception e) {
      //   if (debug)
      //     LOG.info(
      //         "INFO: The netDeltaP for this organization could not be read from the XML element. Using default value. ",
      //         element);
      //   //    System.exit(-46);
      // }
      // try {
      //   g.setMaxVoltageMultiplier(Double.parseDouble(element.getAttribute("maxVoltageMultiplier")));
      // } catch (Exception e) {
      //   if (debug)
      //     LOG.info(
      //         "INFO: The maxVoltageMultiplier for this organization could not be read from the XML element. Using default value. ",
      //         element);
      //   //    System.exit(-46);
      // }
      // try {
      //   g.setConstantInelasticLoad_kw(Double.parseDouble(element.getAttribute("minKW")));
      // } catch (Exception e) {
      //   if (debug)
      //     LOG.info(
      //         "INFO: The minKW for this organization could not be read from the XML element. Using default value. ",
      //         element);
      //   //    System.exit(-46);
      // }
     
    }
  }

  private static void initializeTicTacToeConnections(String absPathToFile,
      HashMap<UniqueIdentifier, Object> goalParameterValues) {
    LOG.debug("Entering initializeTicTacToeConnections(absPathToFile={},goalParameterValues={}", absPathToFile,
        goalParameterValues);
    IConnections c = Connections.createConnections(absPathToFile, "tictactoeConnections");
    if (!c.getListConnectionGuidelines().isEmpty()) {
      goalParameterValues.put(SelfGoalParameters.tictactoeConnections, c);
    }
  }

 

}
