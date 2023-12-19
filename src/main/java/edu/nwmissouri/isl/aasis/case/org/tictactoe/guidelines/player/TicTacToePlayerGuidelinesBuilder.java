package edu.nwmissouri.isl.aasis.case.org.tictactoe.guidelines.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.nwmissouri.isl.aasis.case.org.tictactoe.goals.TicTacToeGoalParameters;

public class TicTacToePlayerGuidelinesBuilder {
  private static final Logger LOG = LoggerFactory.getLogger(TicTacToePlayerGuidelinesBuilder.class);
  private static final boolean debug = false;
  private static final String PARAMETER_NAME = TicTacToeGoalParameters.tictactoePlayerGuidelines.toString();
  private static long openingTimeSlice;

  public static ITicTacToePlayerGuidelines createGuidelines() {
    LOG.debug("Creating default empty guidelines.");
    return new TicTacToePlayerGuidelines(openingTimeSlice);
  }

  public synchronized static ITicTacToePlayerGuidelines createGuidelines(final Document configDoc) {
    LOG.debug("Entering createGuidelines(configDoc={})", configDoc);
    NodeList nodeList = configDoc.getElementsByTagName(PARAMETER_NAME);
    LOG.info("There are {} initial {}.", nodeList.getLength(), PARAMETER_NAME);
    ITicTacToePlayerGuidelines g = TicTacToePlayerGuidelinesBuilder.createGuidelines();
    if (nodeList.getLength() > 0) {
      TicTacToePlayerGuidelinesBuilder.readElements(nodeList, g);
    }
    LOG.debug("Exiting createGuidelines: g={}", g);
    return g;
  }

  public synchronized static void readElements(NodeList nodeList, ITicTacToePlayerGuidelines g) {
    LOG.debug("Reading guidelines object from XML elements.");
    for (int i = 0; i < nodeList.getLength(); ++i) {
      Element element = (Element) nodeList.item(i);
      String s;

      s = "openingTimeSlice";
      try {
        g.setOpeningTimeSlice(Long.parseLong(element.getAttribute(s)));
      } catch (Exception e) {
        LOG.info("{} could not be read from the XML element. {} ", s, element);
        System.exit(-47);
      }
    }
  }

  public TicTacToePlayerGuidelinesBuilder setOpeningTimeSlice(long openingTimeSlice) {
    TicTacToePlayerGuidelinesBuilder.openingTimeSlice = openingTimeSlice;
    return this;
  }

}