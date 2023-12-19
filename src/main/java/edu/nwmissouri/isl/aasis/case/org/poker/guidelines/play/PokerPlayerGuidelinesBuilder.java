package edu.nwmissouri.isl.aasis.case.org.poker.guidelines.play;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.nwmissouri.isl.aasis.case.org.poker.goals.PokerGoalParameters;

public class PokerPlayerGuidelinesBuilder {
  private static final Logger LOG = LoggerFactory.getLogger(PokerPlayerGuidelinesBuilder.class);
  private static final boolean debug = false;
  private static final String PARAMETER_NAME = PokerGoalParameters.pokerPlayerGuidelines.toString();
  private static long openingTimeSlice;
  private static long purchaseTimeSlice;

  public static IPokerPlayerGuidelines createGuidelines() {
    LOG.debug("Creating default empty guidelines.");
    return new PokerPlayerGuidelines(openingTimeSlice, purchaseTimeSlice);
  }

  public synchronized static IPokerPlayerGuidelines createGuidelines(final Document configDoc) {
    LOG.debug("Entering createGuidelines(configDoc={})", configDoc);
    NodeList nodeList = configDoc.getElementsByTagName(PARAMETER_NAME);
    LOG.info("There are {} initial {}.", nodeList.getLength(), PARAMETER_NAME);
    IPokerPlayerGuidelines g = PokerPlayerGuidelinesBuilder.createGuidelines();
    if (nodeList.getLength() > 0) {
      PokerPlayerGuidelinesBuilder.readElements(nodeList, g);
    }
    LOG.debug("Exiting createGuidelines: g={}", g);
    return g;

  }

  public synchronized static void readElements(NodeList nodeList, IPokerPlayerGuidelines g) {
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
      s = "purchaseTimeSlice";
      try {
        g.setPurchaseTimeSlice(Long.parseLong(element.getAttribute(s)));
      } catch (Exception e) {
        LOG.info("{} could not be read from the XML element. {} ", s, element);
        System.exit(-47);
      }

    }
  }

  public PokerPlayerGuidelinesBuilder setOpeningTimeSlice(long openingTimeSlice) {
    PokerPlayerGuidelinesBuilder.openingTimeSlice = openingTimeSlice;
    return this;
  }

  public PokerPlayerGuidelinesBuilder setPurchaseTimeSlice(long purchaseTimeSlice) {
    PokerPlayerGuidelinesBuilder.purchaseTimeSlice = purchaseTimeSlice;
    return this;
  }

}