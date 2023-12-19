package edu.nwmissouri.isl.aasis.case.messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import edu.ksu.cis.macr.aasis.messaging.Exchange;
import edu.ksu.cis.macr.aasis.messaging.IMessagingFocus;
import edu.ksu.cis.macr.aasis.messaging.IMessagingManager;
import edu.nwmissouri.isl.aasis.case.config.MessagingFocus;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.messaging.TicTacToeMessagingManager;
import edu.nwmissouri.isl.aasis.case.org.poker.messaging.PokerMessagingManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 The {@code MessagingManager} singleton provides a central exchange for communication between agents.

 Open a browser to http://localhost:15672/ when running a simulation to see associated information.
 */
public enum MessagingManager implements IMessagingManager {

  /**
   Singleton instance of the Communications Manager (one per JVM).
   */
  INSTANCE;
  /**
   The exchange is a named entity to which messages are sent. The type of exchange determines its routing
   behavior. We use different exchanges based on the types of information conveyed.
   */
  public static final Map<IMessagingFocus, Exchange> specs = new HashMap<IMessagingFocus, Exchange>() {
    {
    }
    private static final long serialVersionUID = 8323789558019617154L;
  };

  public static void main(String[] args) {
    initialize();
  }

  /**
   The maximum time messages can remain on a queue before expiring (being deleted without being delivered).
   */
  public static final int MESSAGES_EXPIRE_IN_SECONDS = 60;
  /**
   Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(MessagingManager.class);
  /**
   If true, the optional debug messages in this file will be shown. If false, they will not be issued.
   */
  private static boolean debug = false;

  public Map<IMessagingFocus, Exchange> getSpecs() {
    return specs;
  }

  public static void initialize() {
    LOG.info("INITIALIZING MESSAGING CENTRAL EXCHANGES ......................................");
    try {

      // get specs from all systems

      Map<IMessagingFocus, Exchange> a = TicTacToeMessagingManager.getSpecs();
      Map<IMessagingFocus, Exchange> b = PokerMessagingManager.getSpecs();
      specs.putAll(a);
      specs.putAll(b);

      for (Map.Entry<IMessagingFocus, Exchange> entry : specs.entrySet()) {
        Exchange spec = entry.getValue();
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost(spec.getExchangeHost());
        Connection conn = factory.newConnection();
        spec.setConnection(conn);
        spec.setChannel(spec.getConnection().createChannel());

        // declare exchange - created if does not exist
        spec.getChannel().exchangeDeclare(spec.getExchangeName(), spec.getExchangeType());
        specs.put(entry.getKey(), spec);
        if (debug)
          LOG.debug("   Exchange: {}", spec.toString());
      }

    } catch (Exception e) {
      LOG.error("Error: Could not initialize message exchanges. Verify RabbitMQ is running. README.txt for info. {}",
          e.getMessage());
      System.exit(1);
    }
    LOG.info("\"\t SUCCESS: MessagingManager initialized. ");
  }

  public static String getQueueFocus(IMessagingFocus focus) {
    if (focus == edu.ksu.cis.macr.aasis.messaging.MessagingFocus.GENERAL)
      return "_GENERAL";
    return "GENERAL";
  }

  public static String getFullQueueName(final String queueLink, final String purpose) {
    return purpose + "." + queueLink;
  }

  public static Channel getChannel(IMessagingFocus messagingFocus) {
    return MessagingManager.specs.get(messagingFocus).getChannel();
  }

  public static String getExchangeName(IMessagingFocus messagingFocus) {
    return MessagingManager.specs.get(messagingFocus).getExchangeName();
  }
}
