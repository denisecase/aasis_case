package edu.nwmissouri.isl.aasis.case.org.poker.capabilities.connect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ksu.cis.macr.aasis.agent.cc_message.connect.ConnectMessage;
import edu.ksu.cis.macr.aasis.agent.cc_message.connect.IConnectMessage;
import edu.ksu.cis.macr.aasis.agent.persona.HierarchicalConnectCapability;
import edu.ksu.cis.macr.aasis.agent.persona.IOrganization;
import edu.ksu.cis.macr.aasis.agent.persona.IPersona;
import edu.ksu.cis.macr.aasis.common.Connections;
import edu.ksu.cis.macr.aasis.common.IConnectionGuidelines;
import edu.ksu.cis.macr.aasis.common.IConnections;
import edu.ksu.cis.macr.aasis.messaging.IMessagingFocus;
import edu.ksu.cis.macr.goal.model.InstanceParameters;
import edu.ksu.cis.macr.obaa_pp.events.IOrganizationEvent;
import edu.ksu.cis.macr.obaa_pp.events.OrganizationEvent;
import edu.ksu.cis.macr.obaa_pp.events.OrganizationEventType;
import edu.ksu.cis.macr.organization.model.InstanceGoal;
import edu.ksu.cis.macr.organization.model.identifiers.StringIdentifier;
import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;
import edu.nwmissouri.isl.aasis.case.config.RunManager;
import edu.nwmissouri.isl.aasis.case.org.poker.goals.PokerGoalEvents;
import edu.nwmissouri.isl.aasis.case.org.poker.goals.PokerGoalParameters;
import edu.nwmissouri.isl.aasis.case.org.poker.guidelines.deal.IPokerGuidelines;
import edu.nwmissouri.isl.aasis.case.org.poker.guidelines.play.IPokerPlayerGuidelines;
import edu.nwmissouri.isl.aasis.case.org.poker.messaging.PokerMessagingFocus;
import edu.nwmissouri.isl.aasis.case.org.poker.messaging.PokerMessagingManager;

/**
 * The {@code ConnectCapability} implements communication capabilities needed to establish initial connections. To monitor
 * RabbitMQ, point a browser to (the final slash is required): http://localhost:15672/ and login with: guest / guest
 */
public class PokerConnectCapability extends HierarchicalConnectCapability implements IPokerConnectCapability {
  private static final Logger LOG = LoggerFactory.getLogger(PokerConnectCapability.class);
  private static final boolean debug = false;
  private static IMessagingFocus messagingFocus;

  /**
   * @param owner        - the entity to which this capability belongs.
   * @param organization - the {@code Organization} in which this {@code IAgent} acts.
   */
  public PokerConnectCapability(final IPersona owner, final IOrganization organization) {
    super(IPokerConnectCapability.class, owner, organization);
    COMMUNICATION_CHANNEL_ID = "PokerConnectCommunicationChannel";
    messagingFocus = PokerMessagingFocus.POKER_PARTICIPATE;
    LOG.debug("Before getting channel from Messaging Manager, channel = {}", channel);
    channel = PokerMessagingManager.getChannel(messagingFocus);
    LOG.debug("After getting channel from Messaging Manager, channel = {}", channel);
  }

  /**
   * Constructs a new instance of {@code ConnectCapability}.
   *
   * @param owner        - the entity to which this capability belongs.
   * @param organization - the {@code IAgentInternalOrganization} in which this {@code IAgent} acts.
   * @param connections  - the guidelines for all authorized market connections.
   */
  public PokerConnectCapability(final IPersona owner, final IOrganization organization, IConnections connections) {
    super(IPokerConnectCapability.class, owner, organization);
    COMMUNICATION_CHANNEL_ID = "PokerConnectCommunicationChannel";
    messagingFocus = PokerMessagingFocus.POKER_PARTICIPATE;
    LOG.debug("Before getting channel from Messaging Manager, channel = {}", channel);
    channel = PokerMessagingManager.getChannel(messagingFocus);
    LOG.debug("After getting channel from Messaging Manager, channel = {}", channel);
    this.connections = connections;
  }

  public static String getCommunicationChannelID() {
    return PokerConnectCapability.COMMUNICATION_CHANNEL_ID;
  }

  @Override
  public void sendREMOTE(final IConnectMessage message) {
    LOG.debug("Beginning sendREMOTE. messagingFocus={} message={}.", messagingFocus, message);
    final String queueLink = buildQueueLinkFromSenderAndReceiver(message.getRemoteSender(),
        message.getRemoteReceiver());
    LOG.debug("sendREMOTE queueLink ={}.", queueLink);
    String fullQueueName = PokerMessagingManager.getFullQueueName(queueLink,
        PokerMessagingManager.getQueueFocus(messagingFocus));
    final String routingKey = fullQueueName;
    LOG.debug("sendREMOTE fullQueueName = routingKey ={}.", fullQueueName);
    PokerMessagingManager.declareAndBindConsumerQueue(messagingFocus, queueLink);
    LOG.info("SENDING HELLO TO {}. {}", fullQueueName, message.toString());
    try {
      byte[] messageBodyBytes = message.serialize();
      if (debug)
        LOG.debug("Serialized HELLO TO routingKey: {} Size: ({} bytes) ", routingKey, messageBodyBytes.length);
      channel.basicPublish(PokerMessagingManager.getExchangeName(messagingFocus), routingKey,
          MessageProperties.PERSISTENT_TEXT_PLAIN, messageBodyBytes);
      if (debug)
        LOG.debug("SENT HELLO TO {}: {}", fullQueueName, message.toString());
    } catch (Exception e) {
      LOG.error("ERROR in sendREMOTE message {} from {}. ", message.toString(), message.getRemoteSender());
      System.exit(-56);
    }
  }

  @Override
  public String toString() {
    return "PokerConnectCapability{" + ", connections=" + connections +  ", parentConnections=" + parentConnections + ", allConnected=" + allConnected + '}';
  }

  @Override
  public synchronized IConnectMessage remoteRECEIVE(final String queueLink)
      throws IOException, ShutdownSignalException, InterruptedException {
    if (debug)
      LOG.debug("Setting consumer with queueLink={}", queueLink);
    PokerMessagingManager.declareAndBindConsumerQueue(messagingFocus, queueLink);
    if (debug)
      LOG.debug("declareAndBindConsumerQueue {}", queueLink);
    String fullQueueName = PokerMessagingManager.getFullQueueName(queueLink,
        PokerMessagingManager.getQueueFocus(messagingFocus));
    QueueingConsumer consumer = new QueueingConsumer(
        Objects.requireNonNull(channel, "Error null channel in receive()."));
    String basicConsume = channel.basicConsume(fullQueueName, true, consumer);
    if (debug)
      LOG.debug("basicConsume {}", basicConsume);
    QueueingConsumer.Delivery delivery = consumer.nextDelivery(RunManager.getDeliveryCheckTime_ms());
    if (debug)
      LOG.debug("in remoteRECEIVE. delivery={}", delivery);
    if (delivery != null) {
      LOG.debug("got something on {}", fullQueueName);
      try {
        IConnectMessage received = (IConnectMessage) ConnectMessage.createEmptyConnectMessage()
            .deserialize(delivery.getBody());
        LOG.debug("Deserialized remote POKER CONNECT message on {}. {}.", fullQueueName, received);
        if (!queueLink.contains(received.getRemoteSender())) {
          LOG.error("ERROR: Got Message On Wrong Queue. Deserialized remote POKER CONNECT message on {}. {}.",
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

  @Override
  public boolean registerWithExchange() {
    RunManager.registered(ec.getIdentifierString(), messagingFocus);
    return true;
  }

  /**
   /**
   * Get the parameters from this instance goal and use them to set the goal-specific guidelines for any parent
   * connections.
   *
   * @param instanceGoal - this instance of the specification goal
   */
  public synchronized void initializeParentConnections(InstanceGoal<?> instanceGoal) {
    if (debug)
      LOG.debug("Initializing connections to  parents from goal: {}.", instanceGoal);

    // Get the parameter values from the existing active instance goal
    final InstanceParameters params = Objects.requireNonNull((InstanceParameters) instanceGoal.getParameter());
    if (debug)
      LOG.debug("Initializing broker connections from params: {}.", params);
    IConnections parentConnections = (IConnections) params.getValue(PokerGoalParameters.pokerDealerConnections);
    this.setParentConnections(parentConnections);
    if (noParents()) {
      LOG.debug("{} child initialized without any parent.", this.getOwner().getUniqueIdentifier().toString(),
          instanceGoal.toString());
    }
    if (debug)
      LOG.debug("{} parent connections.", parentConnections.getListConnectionGuidelines().size());
  }

  @Override
  public synchronized void initializeChildConnections(InstanceGoal<?> instanceGoal) {
    if (debug)
      LOG.debug("Initializing connections to children from goal: {}.", instanceGoal);
    // Get the parameter values from the existing active instance goal
    final InstanceParameters params = Objects.requireNonNull((InstanceParameters) instanceGoal.getParameter());
    if (debug)
      LOG.debug("Initializing child connections from params: {}.", params);

    final IConnections pokerConnections = Objects
        .requireNonNull((IConnections) params.getValue(PokerGoalParameters.pokerConnections));

    this.setAllChildConnectionGuidelines(pokerConnections);
    if (pokerConnections != null && pokerConnections.getListConnectionGuidelines() != null
        && pokerConnections.getListConnectionGuidelines().size() > 0) {
      LOG.info("{} child connections.", pokerConnections.getListConnectionGuidelines().size());
    }
  }

  /**
   * Get all parameters from this instance goal and use them to initialize the capability.
   *
   * @param instanceGoal - this instance of the specification goal
   */
  @Override
  public void init(final InstanceGoal<?> instanceGoal) {
    LOG.info("Entering init(instanceGoal={}.", instanceGoal);
    // Get the parameter values from the existing active instance goal
    final InstanceParameters params = Objects.requireNonNull((InstanceParameters) instanceGoal.getParameter());
    if (debug)
      LOG.debug("Initializing params: {}.", params);

   
    this.parentConnections = (IConnections) params.getValue(StringIdentifier.getIdentifier("pokerDealerConnections"));
    if (debug)
      LOG.debug("Initializing parent connections: {}.", parentConnections);

    if (parentConnections == null) {
      if (debug)
        LOG.debug("There are no parent connections to other agents.");
    }
    if (parentConnections != null && parentConnections.getListConnectionGuidelines() != null
        && parentConnections.getListConnectionGuidelines().size() > 0) {
      if (debug)
        LOG.debug("{} possible parent connections.", parentConnections.getListConnectionGuidelines().size());
    }
    
   
  }

  @Override
  public boolean send(IConnectMessage message) {
    LOG.error("Too general - do not use. ");
    System.exit(-99);
    return false;
  }

  /**
   * Trigger the associated goal.
   *
   * @param ig - the instance goal that is triggering the new goal.
   */
  public synchronized void triggerChildGoal(final InstanceGoal<?> ig) {
    this.init(ig);
    LOG.debug("Parent connections are: {}", this.parentConnections);

    // get the parent connections from the list of all connections
    if (this.parentConnections == null)
      return;
    List<? extends IConnectionGuidelines> lst = this.parentConnections.getListConnectionGuidelines();
    LOG.info("Connection guidelines are: {}", lst);
    if (lst == null || lst.isEmpty())
      return;
    // create new guidelines

    InstanceParameters params = (InstanceParameters) ig.getParameter();
    LOG.info("Instance goal parameters = {}", params);
    IPokerPlayerGuidelines PokerPlayerGuidelines = IPokerPlayerGuidelines.extractGuidelines(params);
    LOG.info("Player guidelines = {}", PokerPlayerGuidelines);

    if (PokerPlayerGuidelines == null) {
      LOG.error("WARNING: agent has broker connections, but no auction guidelines yet. ");
      return;
    }

    // set the guidelines from the triggering goal
    HashMap<UniqueIdentifier, Object> map = new HashMap<>();
    map.put(PokerGoalParameters.pokerDealerConnections, this.parentConnections);
    LOG.info("Passing on goal guidelines ={}", parentConnections);

    map.put(PokerGoalParameters.pokerPlayerGuidelines, PokerPlayerGuidelines);
    LOG.info("Passing on goal guidelines ={}", PokerPlayerGuidelines);

    final InstanceParameters newParams = new InstanceParameters(map);
    LOG.info("New params ={}", newParams);

    // create an organization event
    IOrganizationEvent event = new OrganizationEvent(OrganizationEventType.EVENT, PokerGoalEvents.addPokerParticipant, ig,
        newParams);
    LOG.info("Created new organization GOAL_MODEL_EVENT (to addParticipant): {}", event.toString());

    // add the event to an organization events list
    List<IOrganizationEvent> lstEvents = new ArrayList<>();
    LOG.info("Adding event={}", event.toString());
    lstEvents.add(event);

    // add the event list to the control component's event list
    this.owner.getOrganizationEvents().addEventListToQueue(lstEvents);
  }

  
 

}
