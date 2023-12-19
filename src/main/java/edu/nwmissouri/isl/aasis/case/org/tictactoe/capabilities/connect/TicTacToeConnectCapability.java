package edu.nwmissouri.isl.aasis.case.org.tictactoe.capabilities.connect;

import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import edu.ksu.cis.macr.aasis.agent.cc_message.connect.ConnectMessage;
import edu.ksu.cis.macr.aasis.agent.cc_message.connect.IConnectMessage;
import edu.ksu.cis.macr.aasis.agent.cc_p.ConnectionModel;
import edu.ksu.cis.macr.aasis.agent.persona.HierarchicalConnectCapability;
import edu.ksu.cis.macr.aasis.agent.persona.IOrganization;
import edu.ksu.cis.macr.aasis.agent.persona.IPersona;
import edu.ksu.cis.macr.aasis.common.Connections;
import edu.ksu.cis.macr.aasis.common.IConnectionGuidelines;
import edu.ksu.cis.macr.aasis.common.IConnections;
import edu.ksu.cis.macr.aasis.messaging.IMessagingFocus;
import edu.ksu.cis.macr.goal.model.InstanceParameters;
import edu.nwmissouri.isl.aasis.case.config.RunManager;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.goals.TicTacToeGoalEvents;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.goals.TicTacToeGoalParameters;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.messaging.TicTacToeMessagingFocus;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.messaging.TicTacToeMessagingManager;
import edu.ksu.cis.macr.obaa_pp.events.IOrganizationEvent;
import edu.ksu.cis.macr.obaa_pp.events.OrganizationEvent;
import edu.ksu.cis.macr.obaa_pp.events.OrganizationEventType;
import edu.ksu.cis.macr.organization.model.InstanceGoal;
import edu.ksu.cis.macr.organization.model.identifiers.StringIdentifier;
import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * The {@code TicTacToeConnectCapability} implements communication capabilities needed to establish initial connections. To monitor
 * RabbitMQ, point a browser to (the final slash is required): http://localhost:15672/ and login with: guest / guest
 */
public class TicTacToeConnectCapability extends HierarchicalConnectCapability implements ITicTacToeConnectCapability {
  private static final Logger LOG = LoggerFactory.getLogger(TicTacToeConnectCapability.class);
  private static final boolean debug = false;
  private static IMessagingFocus messagingFocus;

  /**
   * @param owner        - the entity to which this capability belongs.
   * @param organization - the {@code Organization} in which this {@code IAgent} acts.
   */
  public TicTacToeConnectCapability(final IPersona owner, final IOrganization organization) {
    super(ITicTacToeConnectCapability.class, owner, organization);
    COMMUNICATION_CHANNEL_ID = "TicTacToeConnectCommunicationChannel";
    messagingFocus = TicTacToeMessagingFocus.TICTACTOE_PARTICIPATE;
    LOG.debug("Before getting channel from Messaging Manager, channel = {}", channel);
    channel = TicTacToeMessagingManager.getChannel(messagingFocus);
    LOG.debug("After getting channel from Messaging Manager, channel = {}", channel);
  }

  /**
   * Constructs a new instance of {@code ConnectCapability}.
   *
   * @param owner           - the entity to which this capability belongs.
   * @param organization    - the {@code IAgentInternalOrganization} in which this {@code IAgent} acts.
   * @param connections - the guidelines for all authorized grid connections.
   */
  public TicTacToeConnectCapability(final IPersona owner, final IOrganization organization, IConnections connections) {
    super(ITicTacToeConnectCapability.class, owner, organization, connections);
    COMMUNICATION_CHANNEL_ID = "TicTacToeConnectCommunicationChannel";
    messagingFocus = TicTacToeMessagingFocus.TICTACTOE_PARTICIPATE;
    LOG.debug("Before getting channel from Messaging Manager, channel = {}", channel);
    channel = TicTacToeMessagingManager.getChannel(messagingFocus);
    LOG.debug("After getting channel from Messaging Manager, channel = {}", channel);
    this.connections = connections;
  }

  @Override
  public void sendREMOTE(final IConnectMessage message) {
    LOG.debug("sendREMOTE. message={}", message);
    final String queueLink = buildQueueLinkFromSenderAndReceiver(message.getRemoteSender(),
        message.getRemoteReceiver());
    final String fullQueueName = TicTacToeMessagingManager.getFullQueueName(queueLink,
        TicTacToeMessagingManager.getQueueFocus(messagingFocus));
    final String routingKey = fullQueueName;
    TicTacToeMessagingManager.declareAndBindConsumerQueue(messagingFocus, queueLink);
    LOG.debug("SENDING MESSAGE TO {}. {}", fullQueueName, message.toString());
    try {
      byte[] messageBodyBytes = message.serialize();
      if (debug)
        LOG.debug("Serialized HELLO TO routingKey: {} Size: ({} bytes) ", routingKey, messageBodyBytes.length);
      channel.basicPublish(TicTacToeMessagingManager.getExchangeName(messagingFocus), routingKey,
          MessageProperties.PERSISTENT_TEXT_PLAIN, messageBodyBytes);
      LOG.info("SENT HELLO TO {}: {}", fullQueueName, message.toString());
    } catch (Exception e) {
      LOG.error("ERROR send() messages {} from {}. ", message.toString(), message.getRemoteSender());
      System.exit(-56);
    }
  }

  public static String getCommunicationChannelID() {
    return TicTacToeConnectCapability.COMMUNICATION_CHANNEL_ID;
  }

  @Override
  public String toString() {
    return "ConnectCapability{" + "connections=" + getAllConnections() + ", parentConnections=" + getParentConnections()
        + ", tictactoeConnections=" + getChildConnections() + ", ec=" + ec + '}';
  }

  /**
   * Get all parameters from this instance goal and use them to initialize the capability.
   *
   * @param instanceGoal - this instance of the specification goal
   */
  @Override
  public void init(InstanceGoal<?> instanceGoal) {
    LOG.info("Entering init(instanceGoal={}.", instanceGoal);

    // Get the parameter values from the existing active instance goal
    final InstanceParameters params = Objects.requireNonNull((InstanceParameters) instanceGoal.getParameter());
    if (debug)
      LOG.debug("Initializing params: {}.", params);

    final IConnections tictactoeConnections = (IConnections) params
        .getValue(StringIdentifier.getIdentifier("tictactoeConnections"));
    if (debug)
      LOG.debug("Initializing tictactoeConnections: {}.", tictactoeConnections);
    this.setAllConnections(tictactoeConnections);

    if (this.connections == null) {
      IConnections cn = (IConnections) params.getValue(TicTacToeGoalParameters.tictactoeConnections);
      if (debug)
        LOG.debug("tictactoeConnections: {}.", cn);

      this.setAllChildConnectionGuidelines(cn);
      this.setParentConnections(parentConnections);
    } else {
      if (debug)
        LOG.debug("Connections to other agents: {}", tictactoeConnections.getListConnectionGuidelines());
    

    }

  }

  @Override
  public boolean registerWithExchange() {
    RunManager.registered(ec.getIdentifierString(), messagingFocus);
    return true;
  }

  @Override
  public boolean send(IConnectMessage message) {
    LOG.error("Too general - do not use. ");
    System.exit(-99);
    return false;
  }

  /**
   * Trigger an associated "be sub holon" goal.
   *
   * @param instanceGoal - the instance goal that is triggering the new goal.
   */
  @Override
  public void triggerChildGoal(final InstanceGoal<?> instanceGoal) {
    final List<? extends IConnectionGuidelines> lstAll = this.connections.getListConnectionGuidelines();
    if (debug)
      LOG.debug("All connections are: {}", lstAll);

    // get the parent connections from the list of all connections
    final List<? extends IConnectionGuidelines> lstParentConnections = getAllParentConnections(lstAll);
    LOG.info("Parent  connections are: {}", lstParentConnections);
    if (lstParentConnections == null || lstParentConnections.isEmpty())
      return;

    // create new guidelines
    final IConnections c = Connections.createConnections(lstParentConnections, "tictactoeConnections");

    // set the guidelines from the triggering goal
    HashMap<UniqueIdentifier, Object> map = new HashMap<>();
    map.put(TicTacToeGoalParameters.tictactoeConnections, c);
    final InstanceParameters instanceParams = new InstanceParameters(map);

    // create an organization event
    final IOrganizationEvent event = new OrganizationEvent(OrganizationEventType.EVENT,
        TicTacToeGoalEvents.addTicTacToeParticipant, instanceGoal, instanceParams);
    LOG.info("!!Created new organization GOAL_MODEL_EVENT (to addTicTacToeParticipant): {}", event.toString());

    // add the event to an organization events list
    List<IOrganizationEvent> lstEvents = new ArrayList<>();
    LOG.info("Adding event={}", event.toString());
    lstEvents.add(event);

    // add the event list to the control component's event list
    this.owner.getOrganizationEvents().addEventListToQueue(lstEvents);
  }

  /**
   * Trigger an associated parent goal.
   *
   * @param instanceGoal - the instance goal that is triggering the new goal.
  
   */
  @Override
  public synchronized void triggerParentGoal(final InstanceGoal<?> instanceGoal) {
    final List<? extends IConnectionGuidelines> lstAll = this.connections.getListConnectionGuidelines();
    if (debug)
      LOG.debug("All connections are: {}", lstAll);

    // get the child connections from the list of all connections
    List<? extends IConnectionGuidelines> lstChildConnections = getAllChildConnections(lstAll);
    if (debug)
      LOG.debug("Child connections are: {}", lstChildConnections);

    if (lstChildConnections == null || lstChildConnections.isEmpty())
      return;
    final IConnections tictactoeConnections = Connections.createConnections(lstChildConnections,
        "tictactoeConnections");

    // set the guidelines from the triggering goal
    HashMap<UniqueIdentifier, Object> map = new HashMap<>();
    map.put(TicTacToeGoalParameters.tictactoeConnections, tictactoeConnections);
    final InstanceParameters instanceParams = new InstanceParameters(map);

    // create an organization event
    final IOrganizationEvent organizationEvent = new OrganizationEvent(OrganizationEventType.EVENT,
        TicTacToeGoalEvents.addTicTacToeParticipant, instanceGoal, instanceParams);
    LOG.info("Created new organization GOAL_MODEL_EVENT (to addTicTacToeParticipant): {}",
        organizationEvent.toString());

    // add the event to an organization events list
    ArrayList<IOrganizationEvent> organizationEvents = new ArrayList<>();
    organizationEvents.add(organizationEvent);

    // add the event list to the control component's event list
    this.owner.getOrganizationEvents().addEventListToQueue(organizationEvents);
  }

  protected boolean connectToChild(IConnectionGuidelines cg) {
    final String other = cg.getOtherAgentAbbrev();
    final String org = cg.getOrganizationAbbrev();
    final String master = cg.getExpectedMasterAbbrev();
    final String myPersona = ec.getUniqueIdentifier().toString();

    if (alreadyInConnectionList(myPersona, other)) {
      cg.setConnected(true);
      LOG.debug("CONNECTED. Parent {} already in the conn list to child {}.", myPersona, other);
    }
    if (cg.isConnected())
      return true;

    if (debug)
      LOG.debug("Parent {} not yet connected to child {}. Checking for hello.", myPersona, other);
    final IConnectMessage helloMessage = checkForRemoteConnectMessage(other, myPersona);

    if (helloMessage != null) {
      LOG.debug("Received CONNECT MESSAGE from participant: {}.", helloMessage);
      try {
        final String sender = helloMessage.getRemoteSender(); // other
        final String receiver = helloMessage.getRemoteReceiver(); // me
        cg.setConnected(true);
        updateConnectionList(sender, receiver);
        LOG.info("Parent {} now connected to sub holon {}.", receiver, sender);
      } catch (Exception e) {
        LOG.error("Error getting info from received hello. {}", helloMessage.toString());
        System.exit(-4);
      }
    } else {
      if (debug)
        LOG.debug("No connect received. Parent {} sending again to sub holon {}.", myPersona, other);
      sendRemoteHelloMessage(other, org, master, myPersona);
    }
    return cg.isConnected();
  }

  /**
   * @param queueLink - prefix for the messaging queue
   * @return String messages - Grabs messages from Queue
   * @throws IOException - Handles any IO Exceptions
   * @throws ShutdownSignalException - Handles any ShutdownSignal Exceptions
   * @throws InterruptedException - Handles any Interrupted Exceptions.
   */
  @Override
  public IConnectMessage remoteRECEIVE(final String queueLink)
      throws IOException, ShutdownSignalException, InterruptedException {
    if (debug)
      LOG.debug("Setting consumer with queueLink={}", queueLink);
    TicTacToeMessagingManager.declareAndBindConsumerQueue(messagingFocus, queueLink);
    if (debug)
      LOG.debug("declareAndBindConsumerQueue {}", queueLink);
    String fullQueueName = TicTacToeMessagingManager.getFullQueueName(queueLink,
        TicTacToeMessagingManager.getQueueFocus(messagingFocus));
    QueueingConsumer consumer = new QueueingConsumer(
        Objects.requireNonNull(channel, "Error null channel in receive()."));
    String basicConsume = channel.basicConsume(fullQueueName, true, consumer);
    if (debug)
      LOG.debug("basicConsume {}", basicConsume);

    // check for delivery for given milliseconds
    QueueingConsumer.Delivery delivery = consumer.nextDelivery(RunManager.getDeliveryCheckTime_ms());
    if (debug)
      LOG.debug("in remoteRECEIVE. delivery={}", delivery);
    if (delivery != null) {
      if (debug)
        LOG.debug("got something on {}", fullQueueName);
      try {
        IConnectMessage received = (IConnectMessage) ConnectMessage.createEmptyConnectMessage()
            .deserialize(delivery.getBody());
        if (debug)
          LOG.debug("Deserialized remote TICTACTOE CONNECT message on {}. {}.", fullQueueName, received);
        if (!queueLink.contains(received.getRemoteSender())) {
          LOG.error("ERROR: Got Message On Wrong Queue. Deserialized remote TICTACTOE CONNECT message on {}. {}.",
              fullQueueName, received);
          System.exit(-9);
        }
        return received;
      } catch (Exception ex) {
        LOG.error("ERROR deserializing connect message: {}", ex.getCause().toString());
        System.exit(-11);
      }
    }
    if (debug)
      LOG.debug("got nothing on {}", fullQueueName);
    return null;
  }

  protected void setConsumer(final String queueLink) throws IOException {
    if (debug)
      LOG.debug("Setting consumer with queueLink={}", queueLink);
    TicTacToeMessagingManager.declareAndBindConsumerQueue(messagingFocus, queueLink);
    if (debug)
      LOG.debug("declareAndBindConsumerQueue {}", queueLink);
    String fullQueueName = TicTacToeMessagingManager.getFullQueueName(queueLink,
        TicTacToeMessagingManager.getQueueFocus(messagingFocus));
    QueueingConsumer consumer = new QueueingConsumer(
        Objects.requireNonNull(channel, "Error null channel in receive()."));
    String basicConsume = channel.basicConsume(fullQueueName, true, consumer);
    if (debug)
      LOG.debug("basicConsume {}", basicConsume);
  }

  protected synchronized void updateConnectionList(final String sender, final String receiver) {
    if (debug)
      LOG.debug("Starting updateConnectionList() with sender ={} and receiver={}.", sender, receiver);
    // assume sender comes before receiver alphabetically
    String first = sender;
    String second = receiver;
    // if instead, receiver comes before sender alphabetically, adjust
    if (receiver.compareTo(sender) < 0) {
      first = receiver;
      second = sender;
    }
    TreeSet<String> conns = ConnectionModel.getConnectionSet();
    if (debug)
      LOG.debug("The connection set already had {} entries.", conns.size());
    //add it (only in the alphabetical order - don't duplicate the connection)
    final String connection = first + " - " + second + "\n";
    ConnectionModel.insertNewConnection(connection);

    final int numConnections = ConnectionModel.getConnectionSet().size();
    final int totalPossibleParentConnections = RunManager.getTotalConnectionCount();

    LOG.debug("{}", ConnectionModel.getSummaryString());
    RunManager.setInitiallyConnected(
        numConnections >= RunManager.INITIAL_CONNECTION_THRESHOLD_FRACTION * totalPossibleParentConnections);
  }

 


  @Override
  public synchronized void initializeChildConnections(InstanceGoal<?> instanceGoal) {
    if (debug)
      LOG.debug("Initializing connections to children from goal: {}.", instanceGoal);
    // Get the parameter values from the existing active instance goal
    final InstanceParameters params = Objects.requireNonNull((InstanceParameters) instanceGoal.getParameter());
    if (debug)
      LOG.debug("Initializing child connections from params: {}.", params);

    final IConnections tictactoeConnections = Objects
        .requireNonNull((IConnections) params.getValue(TicTacToeGoalParameters.tictactoeConnections));

    this.setAllChildConnectionGuidelines(tictactoeConnections);
    if (tictactoeConnections != null && tictactoeConnections.getListConnectionGuidelines() != null
        && tictactoeConnections.getListConnectionGuidelines().size() > 0) {
      LOG.info("{} child connections.", tictactoeConnections.getListConnectionGuidelines().size());
    }
  }

  /**
   * Send a hello message.
   *
   * @param other     - the agent receiving the message
   * @param org       - the organization abbreviation
   * @param master    - the organization master
   * @param myPersona - the agent sending the message
   * @return - the {@code IConnectMessage} sent
   */
  @Override
  public IConnectMessage sendRemoteHelloMessage(final String other, final String org, final String master,
      final String myPersona) {
    final IConnectMessage message = this.createRemoteHelloMessage(other, org, master, myPersona);
    if (debug)
      LOG.debug("COMPOSED remote hello message {} ", message.toString());
    try {
      sendREMOTE(message);
      if (debug)
        LOG.debug("Remote HELLO SENT: {} ", message.toString());
    } catch (Exception e) {
      LOG.error("Remote HELLO NOT SENT: {} ", message.toString());
      System.exit(-88);
    }
    return message;
  }

}
