/*
 * Copyright 2018 Denise Case
 * 
 * See License.txt file for the license agreement. 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package edu.nwmissouri.isl.aasis.case.org.tictactoe.capabilities.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.ksu.cis.macr.aasis.agent.persona.AbstractOrganizationCapability;
import edu.ksu.cis.macr.aasis.agent.persona.IOrganization;
import edu.ksu.cis.macr.aasis.agent.persona.IPersona;
import edu.ksu.cis.macr.aasis.common.IConnections;
import edu.ksu.cis.macr.aasis.messaging.IMessagingFocus;
import edu.ksu.cis.macr.aasis.messaging.MessagingReliabilityManager;
import edu.ksu.cis.macr.goal.model.InstanceParameters;
import edu.ksu.cis.macr.obaa_pp.events.IOrganizationEvent;
import edu.ksu.cis.macr.obaa_pp.events.OrganizationEvent;
import edu.ksu.cis.macr.obaa_pp.events.OrganizationEventType;
import edu.ksu.cis.macr.organization.model.Agent;
import edu.ksu.cis.macr.organization.model.InstanceGoal;
import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;
import edu.nwmissouri.isl.aasis.case.config.RunManager;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.goals.TicTacToeGoalEvents;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.messaging.TicTacToeMessagingFocus;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.messaging.TicTacToeMessagingManager;
import edu.nwmissouri.isl.aasis.case.primary.messages.IPrimaryMessage;
import edu.nwmissouri.isl.aasis.case.primary.messages.IPrimaryMessageContent;
import edu.nwmissouri.isl.aasis.case.primary.messages.PrimaryMessage;
import edu.nwmissouri.isl.aasis.case.primary.messages.PrimaryMessageContent;

/**
 Provides the ability to act as a super holon in a power distribution control organization.
 */
public class TicTacToeRefereeCapability extends AbstractOrganizationCapability {
  private static final Logger LOG = LoggerFactory.getLogger(TicTacToeRefereeCapability.class);
  private static final String QUEUE_PURPOSE = "TICTACTOE";
  private static Channel channel;
  private static final boolean debug = false;
  private static final IMessagingFocus messagingFocus = TicTacToeMessagingFocus.TICTACTOE;
  private static UniqueIdentifier myID;
  double communicationReliability = 1.0;
  double communicationDelay = 0.0;
  private final String COMMUNICATION_CHANNEL_ID = "TicTacToeCommunicationChannel";
  private int NUMBER_OF_PARTICPANT_MESSAGES_TO_STORE = 1;
  private Map<InstanceGoal<?>, BlockingQueue<IPrimaryMessageContent>> allParticipantData;
  private IConnections allChildConnections;
  private QueueingConsumer consumer;

  /**
   @param owner - the entity to which this capability belongs
   @param org - the organization in which it is using this capability
   */
  public TicTacToeRefereeCapability(final IPersona owner, final IOrganization org) {
    super(TicTacToeRefereeCapability.class, owner, org);
    this.setOwner(Objects.requireNonNull(owner));
    channel = TicTacToeMessagingManager.getChannel(messagingFocus);
    consumer = new QueueingConsumer(Objects.requireNonNull(channel, "Error null channel in receive()."));
    initializeReliabilityAndDelay();
  }

  private void initializeReliabilityAndDelay() {
    try {
      this.communicationReliability = MessagingReliabilityManager.getCommunicationReliability();
      if (debug)
        LOG.debug("\t New  communicationReliability.");
      communicationDelay = MessagingReliabilityManager.getCommunicationDelay();
    } catch (Exception e) {
      // just use the defaults
      communicationDelay = 0.0;
      communicationReliability = 1.0;
    }
    if (debug)
      LOG.debug("New comm cap with reliability = {} and delay = {}", communicationReliability, communicationDelay);
  }

  public IConnections getAllChildConnections() {
    return this.allChildConnections;
  }

  public synchronized void setAllChildConnections(IConnections allChildConnections) {
    this.allChildConnections = allChildConnections;
  }

  @Override
  public synchronized void reset() {
  }

  @Override
  public double getFailure() {
    return 0;
  }

  @Override
  public Element toElement(final Document document) {
    final Element capability = super.toElement(document);
    return capability;
  }

  /**
   Get the parameters from this instance goal and use them to set the goal-specific guidelines.
   @param instanceGoal - this instance of the specification goal
   */
  public synchronized void initializeFromGoal(InstanceGoal<?> instanceGoal) {
    if (debug)
      LOG.debug("Initializing guidelines from goal {} ", instanceGoal);

    try {
      // Get the parameter values from the existing active instance goal
      final InstanceParameters params = (InstanceParameters) instanceGoal.getParameter();

    } catch (Exception e) {
      LOG.error("Error: {}", e.getCause());
      System.exit(-83);
    }
  }

  /**
   Get the parameters from this instance goal and use them to set the goal-specific guidelines.
   @param instanceGoal - this instance of the specification goal
   */
  public synchronized void initializeGuidelines(InstanceGoal<?> instanceGoal) {
    if (debug)
      LOG.debug("Initializing all guidelines from goal: {}.", instanceGoal);

    // Get the parameter values from the existing active instance goal
    final InstanceParameters params = Objects.requireNonNull((InstanceParameters) instanceGoal.getParameter());
    if (debug)
      LOG.debug("Initializing sub connections params: {}.", params);

    setAllChildConnections(IConnections.extractConnections(params, "tictactoeConnections"));

  }

  /**
   Calculate new guidelines, get reports from holons, and forward the information up the line. May create new
   organization events with the updated goal parameters for each participant if immediate local response is desired.
   @return true if a local solution is possible, false if not
   */
  public IPrimaryMessageContent processTicTacToePowerReports() {
    if (debug)
      LOG.debug("Beginning processTicTacToePowerReports - getting holonic guidelines");

    // get the combined guidelines
    final double combinedMaxKW = this.allChildConnections.getListConnectionGuidelines().get(0).getCombinedKW();
    String orgName = this.allChildConnections.getListConnectionGuidelines().get(0).getOrganizationAbbrev();
    String orgLevel = this.allChildConnections.getListConnectionGuidelines().get(0).getOrganizationLevel();
    if (debug)
      LOG.debug("{} org {} guidelines include a combined max of {}", orgLevel, orgName, combinedMaxKW);

    // get the most recent data from participant getNumberOfMessages

    if (debug)
      LOG.debug("Checking sub reports for {}.", orgName);
    Map<String, IPrimaryMessageContent> mostRecentReadings = this.getMostRecentReadings();

    int readCount = mostRecentReadings.size();
    int subCount = this.getAllChildConnections().getListConnectionGuidelines().size();
    // int subCount = org.getNumberOfChildProsumers();
    if (debug)
      LOG.info("{} recent reports out of {} total sub holons in {}. {}", readCount, subCount, orgName,
          Arrays.toString(mostRecentReadings.keySet().toArray()));

    // if the reporting is complete, aggregate and forward to internal self for processing
    IPrimaryMessageContent aggregateContent = null;
    if ((readCount > 0)) { // && (readCount == subCount)) {
      LOG.info("Reporting complete: {} of {} reads are available. Forwarding aggregated totals.", readCount, subCount);
      aggregateContent = createPrimaryMessageContent(mostRecentReadings);
      LOG.info("Aggregated message content to pass up is: {}", aggregateContent);
    }
    return aggregateContent;
  }

  private Map<String, IPrimaryMessageContent> getMostRecentReadings() {
	return null;
}

/**
   @param personaName - the name of the subagent
   @return String messages - Grabs messages from Queue
   @throws IOException - Handles all IO Exceptions
   @throws ShutdownSignalException - Handles all Shutdown signal Exceptions
   @throws ConsumerCancelledException - Handles all Consumer Cancelled Exceptions
   @throws InterruptedException - Handles Interrupted Exceptions
   */
  public synchronized IPrimaryMessage receiveRemotePrimaryMessage(String personaName)
      throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
    String fullQueueName = TicTacToeMessagingManager.getFullQueueName(personaName,
        TicTacToeMessagingManager.getQueueFocus(messagingFocus));
    TicTacToeMessagingManager.declareAndBindConsumerQueue(messagingFocus, personaName);
    String basicConsume = channel.basicConsume(fullQueueName, true, consumer);

    // check for delivery for given milliseconds
    final QueueingConsumer.Delivery delivery = consumer.nextDelivery(RunManager.getDeliveryCheckTime_ms());
    if (delivery == null) {
      if (debug)
        LOG.debug("got nothing on {}", fullQueueName);
      return null;
    }
    if (debug)
      LOG.debug("got something on {}", fullQueueName);
    IPrimaryMessage message = new PrimaryMessage();
    try {
      message = (IPrimaryMessage) message.deserialize(delivery.getBody());
      if (debug)
        LOG.info("Received REMOTE TICTACTOE MESSAGE on {}. {}.", fullQueueName, message);
    } catch (Exception ex) {
      LOG.error("ERROR: {}", ex.toString());
      System.exit(-42);
    }
    return message;
  }

  private IPrimaryMessageContent createPrimaryMessageContent(Map<String, IPrimaryMessageContent> mostRecentReadings) {
    LOG.info("Creating new aggregate power message");
    IPrimaryMessageContent agg = PrimaryMessageContent.createPrimaryMessageContent();
    for (Entry<String, IPrimaryMessageContent> entry : mostRecentReadings.entrySet()) {
      IPrimaryMessageContent item = entry.getValue();
      LOG.info("Adding message content from {}: {}.", entry.getKey(), entry.getValue().toString());
      agg.add(item);
    }
    return agg;
  }

  /**
   Trigger a "Manage Load" goal for each prosumer participant in this local organization. Use the combined guidelines and
   distribute them among the registered participants.
   @param instanceGoal - the supervisor's goal instance
   */
  public synchronized void triggerManageGoals(final InstanceGoal<?> instanceGoal) {
    // Set some initial goal parameter guidelines for each participant
    final Map<UniqueIdentifier, Object> participantGoalParameters = this.getInitialParticipantGoalParameters();
    if (participantGoalParameters == null) {
      return;
    }

    // create an organization event to trigger each power participant's manage goal
    final List<IOrganizationEvent> organizationEvents = this.createParticipantEvents(instanceGoal,
        participantGoalParameters);

    // add each organization events to the control component's event list
    this.owner.getOrganizationEvents().addEventListToQueue(organizationEvents);
  }

  public Map<UniqueIdentifier, Object> getInitialParticipantGoalParameters() {
    Set<Agent<?>> prosumers = this.getLocalRegisteredProsumers();
    int numProsumers = prosumers.size();
    if (numProsumers == 0) {
      return null;
    }
    final Map<UniqueIdentifier, Object> participantGoalParameters = new HashMap<>();
    return participantGoalParameters;
  }

  /**
   Gets the set of local registered prosumer agents given the set of all agents. Do not include the supervisors (control
   component masters in this local organization) and do not include any independent forecaster agents (but a child that
   is also performing a forecast role should be included).
   @return - the set of all prosumer agents registered in this local organization (does not include
   other types of agents such as forecasters, etc)
   */
  public Set<Agent<?>> getLocalRegisteredProsumers() {
    // TODO: add ability to indicate sub is a prosumer agent (not a forecaster).
    final Set<Agent<?>> allAgents = this.getOwner().getPersonaControlComponent().getOrganizationModel().getAgents();

    final Set<Agent<?>> prosumers = new HashSet<>();
    for (Agent<?> agent : allAgents) {
      boolean isMaster = (agent.getIdentifier() == this.owner.getPersonaControlComponent().getLocalMaster());
      boolean isExternalForecaster = agent.getIdentifier().toString().contains("_F");
      if (!isMaster && !isExternalForecaster) {
        prosumers.add(agent);
      }
    }
    return prosumers;
  }

  public List<IOrganizationEvent> createParticipantEvents(InstanceGoal<?> instanceGoal,
      Map<UniqueIdentifier, Object> participantParams) {
    final InstanceParameters params = new InstanceParameters(participantParams);
    final ArrayList<IOrganizationEvent> orgEvents = new ArrayList<>();

    for (int i = 1; i <= this.getLocalRegisteredProsumers().size(); i++) {
      final IOrganizationEvent event = new OrganizationEvent(OrganizationEventType.EVENT, TicTacToeGoalEvents.addTicTacToeParticipant,
          instanceGoal, params);
      if (debug)
        LOG.debug("Created new organization event in plan: {}", event.toString());
      orgEvents.add(event);
    }
    return orgEvents;
  }

  /*
  * Called when goal parameters need to be updated within the system.
  *
  */
  public List<IOrganizationEvent> updateGoalParameters(final Map<InstanceGoal<?>, Double> newGuidelines) {
    List<IOrganizationEvent> oes = new ArrayList<>();

    for (Entry<InstanceGoal<?>, Double> read : newGuidelines.entrySet()) {
      Map<UniqueIdentifier, Object> goals = new HashMap<>();
      InstanceParameters instanceParams = new InstanceParameters(goals);

      IOrganizationEvent oe = new OrganizationEvent(OrganizationEventType.GOAL_MODEL_MODIFICATION,
          TicTacToeGoalEvents.addTicTacToeParticipant, read.getKey(), instanceParams);
      oes.add(oe);
    }
    return oes;
  }
}
