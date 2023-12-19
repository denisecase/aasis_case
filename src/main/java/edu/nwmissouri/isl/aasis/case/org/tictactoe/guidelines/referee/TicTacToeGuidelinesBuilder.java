package edu.nwmissouri.isl.aasis.case.org.tictactoe.guidelines.referee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class TicTacToeGuidelinesBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(TicTacToeGuidelinesBuilder.class);
  private static final boolean debug = false;
  private static final String STRING_IDENTIFIER = "tictactoeGuidelines";
  private static long openTimeSlice = 0;
  private static HashSet<String> authorizedParticipants = new HashSet<>();

  public static TicTacToeGuidelines create() {
    return new TicTacToeGuidelines(openTimeSlice, authorizedParticipants);
  }

  public static TicTacToeGuidelines createGuidelines(Document configDoc) {
    LOG.debug("Entering createGuidelines(configDoc={})", configDoc);
    NodeList nodeList = configDoc.getElementsByTagName(STRING_IDENTIFIER);
    if (debug)
      LOG.debug("There are {} initial {}.", nodeList.getLength(), STRING_IDENTIFIER);
      TicTacToeGuidelines g = new TicTacToeGuidelines();
    if (nodeList.getLength() > 0) {
      TicTacToeGuidelinesBuilder.readElements(nodeList, g);
    }
    LOG.debug("Exiting createTicTacToeGuidelines: g={}", g);
    return g;
  }

  public static void readElements(NodeList nodeList, ITicTacToeGuidelines g) {
    for (int i = 0; i < nodeList.getLength(); ++i) {
      Element element = (Element) nodeList.item(i);
      String s = "";

      try {
        s = "openTimeSlice";
        g.setOpenTimeSlice(Long.parseLong(element.getAttribute(s)));
      } catch (Exception e) {
        LOG.info("{} could not be read from the XML element. {} ", s, element);
        System.exit(-47);
      }
      try {
        s = "authorizedParticipants";
        String input = element.getAttribute(s);
        List<String> lst = Arrays.asList(input.split("/s*,/s*"));
        g.setAuthorizedParticipants(new HashSet<>(lst));
      } catch (Exception e) {
        LOG.info("{} could not be read from the XML element. {} ", s, element);
        System.exit(-47);
      }
    
    }
  }

  /**
   * Add the broker guidelines to the given xml document.
   *
   * @param doc               - xml document
   * @param rootElement       - the root element that will get updated
   * @param n                 - the broker agent id number
   * @param firstLetter       - the first letter of the broker agent name
   * @param childNumberString - the child number string, eg. 44, 45, 46, 47
   */
  public static void addPokerGuidelines(Document doc, Element rootElement, int n, String firstLetter,
      String childNumberString) {
    if (debug)
      LOG.debug("Dealer num = {}.", n);
    Element conn = doc.createElement("TicTacToeGuidelines");
      conn.setAttribute("openTimeSlice", Long.toString(1));
      conn.setAttribute("authorizedParticipants", childNumberString);
    rootElement.appendChild(conn);
  }

  public TicTacToeGuidelinesBuilder setAuthorizedParticipants(HashSet<String> authorizedParticipants) {
    TicTacToeGuidelinesBuilder.authorizedParticipants = authorizedParticipants;
    return this;
  }

  public TicTacToeGuidelinesBuilder setOpenTimeSlice(long openTimeSlice) {
    TicTacToeGuidelinesBuilder.openTimeSlice = openTimeSlice;
    return this;
  }

 
}
