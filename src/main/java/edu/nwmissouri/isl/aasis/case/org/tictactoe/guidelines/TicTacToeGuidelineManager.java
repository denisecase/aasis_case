package edu.nwmissouri.isl.aasis.case.org.tictactoe.guidelines;

import edu.ksu.cis.macr.aasis.common.Connections;
import edu.ksu.cis.macr.aasis.common.IConnections;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.goals.TicTacToeGoalParameters;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.guidelines.player.TicTacToePlayerGuidelinesBuilder;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.guidelines.player.ITicTacToePlayerGuidelines;
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
 * The {@code TicTacToeGuidelineManager} reads goal guidelines (the top goal parameters) from a given XML
 * file.
 */
public enum TicTacToeGuidelineManager {
  INSTANCE;
  private static final Logger LOG = LoggerFactory.getLogger(TicTacToeGuidelineManager.class);
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
    initializeConnections(absPathToFile, goalParameterValues);
    initializeTicTacToePlayerGuidelines(goalParameterValues, configDoc);
    if (debug)
      LOG.debug("Before returning, the top goal guidelines are {}.", goalParameterValues);
    return goalParameterValues;
  }

  private static void initializeConnections(String absPathToFile,
      HashMap<UniqueIdentifier, Object> goalParameterValues) {
    IConnections c = Connections.createConnections(absPathToFile, "tictactoeConnections");
    if (!c.getListConnectionGuidelines().isEmpty()) {
      goalParameterValues.put(TicTacToeGoalParameters.tictactoeConnections, c);
    }
  }

  private static void initializeTicTacToePlayerGuidelines(HashMap<UniqueIdentifier, Object> goalParameterValues, Document configDoc) {
        NodeList nodeList = configDoc.getElementsByTagName("TicTacToePlayerGuidelines");
        if (debug) LOG.debug("There are {} initial guidelines.", nodeList.getLength());
        if (nodeList.getLength() == 0) goalParameterValues.put(TicTacToeGoalParameters.tictactoeGuidelines, null);
        else {
            ITicTacToePlayerGuidelines g = TicTacToePlayerGuidelinesBuilder.createGuidelines();
            TicTacToePlayerGuidelinesBuilder.readElements(nodeList, g);
            goalParameterValues.put(TicTacToeGoalParameters.tictactoeGuidelines, g);
            
        }
    }

}
