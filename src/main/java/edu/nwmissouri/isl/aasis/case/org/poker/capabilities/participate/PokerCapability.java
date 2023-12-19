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
package edu.nwmissouri.isl.aasis.case.org.poker.capabilities.participate;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import edu.ksu.cis.macr.aasis.agent.persona.AbstractOrganizationCapability;
import edu.ksu.cis.macr.aasis.agent.persona.IOrganization;
import edu.ksu.cis.macr.aasis.agent.persona.IPersona;
import edu.ksu.cis.macr.aasis.common.IConnections;
import edu.ksu.cis.macr.aasis.messaging.IMessagingFocus;
import edu.ksu.cis.macr.aasis.messaging.MessagingReliabilityManager;
import edu.ksu.cis.macr.goal.model.InstanceParameters;
import edu.nwmissouri.isl.aasis.case.org.poker.goals.PokerGoalParameters;
import edu.nwmissouri.isl.aasis.case.org.poker.guidelines.play.IPokerPlayerGuidelines;
import edu.nwmissouri.isl.aasis.case.org.poker.messages.*;
import edu.nwmissouri.isl.aasis.case.org.poker.messaging.PokerMessagingFocus;
import edu.nwmissouri.isl.aasis.case.org.poker.messaging.PokerMessagingManager;
import edu.ksu.cis.macr.obaa_pp.objects.IDisplayInformation;
import edu.ksu.cis.macr.organization.model.InstanceGoal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Objects;

/**
 */
public class PokerCapability extends AbstractOrganizationCapability implements IPokerCapability {
  private static final Logger LOG = LoggerFactory.getLogger(PokerCapability.class);
  private static final boolean debug = false;
  private static final String COMMUNICATION_CHANNEL_ID = "PokerChannel";
  private static final IMessagingFocus messagingFocus = PokerMessagingFocus.POKER;
  private static Channel channel;
  double communicationReliability = 1.0;
  double communicationDelay = 0.0;
  private IPokerPlayerGuidelines PokerPlayerGuidelines;
  private IConnections connections;
  private IConnections tictactoeConnections;
  private IConnections parentConnections;
  private QueueingConsumer consumer;

  /**
   * Constructs a new instance of {@code PrimaryCommunication}.
   *
   * @param owner        - the entity to which this capability belongs.
   * @param organization - the {@code Organization} in which this {@code IAgent} acts.
   */
  public PokerCapability(IPersona owner, IOrganization organization) {
    super(IPokerCapability.class, owner, organization);
    channel = PokerMessagingManager.getChannel(messagingFocus);
    consumer = new QueueingConsumer(Objects.requireNonNull(channel, "Error null channel in receive()."));
    initializeReliabilityAndDelay();

  }

  /**
   * @param owner        - the entity to which this capability belongs.
   * @param organization - the {@code Organization} in which this {@code IAgent} acts.
   * @param Parameter    - additional custom parameter
   */
  public PokerCapability(IPersona owner, IOrganization organization, String Parameter) {
    super(IPokerCapability.class, owner, organization);
    this.setOwner(Objects.requireNonNull(owner));
    channel = PokerMessagingManager.getChannel(messagingFocus);
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

  public IConnections getChildConnections() {
    return tictactoeConnections;
  }

  public void setChildConnections(IConnections tictactoeConnections) {
    this.tictactoeConnections = tictactoeConnections;
  }

  @Override
  public IConnections getParentConnections() {
    return this.parentConnections;
  }

  public void setParentConnections(final IConnections parentConnections) {
    this.parentConnections = parentConnections;
    LOG.debug("Set parent connections to broker. {}", this.parentConnections.toString());
  }

  /**
   * The {@code content} that will be channeled by extensions.
   *
   * @param content the {@code content} to be passed along the {@code ICommunicationChannel}.
   */
  @Override
  public void channelContent(final Object content) {
  }

  /**
   * Used to create an Auction message to send to the auction exchange.
   *
   * @return Auction message of the newly created message.
   */
  @Override
  public IPokerMessage createPokerMessage(long timeSlice) {
    if (debug)
      LOG.debug("Creating message.");

    String player = this.owner.getUniqueIdentifier().toString();
    if (debug)
      LOG.debug(" player = {}", player);

    String dealer = this.parentConnections.getListConnectionGuidelines().get(0).getExpectedMasterAbbrev();
    if (debug)
      LOG.debug(" dealer = {}", dealer);

    // add the broker and the timeslice to make message content
    IPokerMessageContent content = PokerMessageContent.create(timeSlice, dealer);
    if (debug)
      LOG.debug(" message content = {}", content);

    // create the remote message and return it
    IPokerMessage msg = PokerMessage.createRemote(this.owner.getUniqueIdentifier().toString(), dealer,
        PokerPerformative.PLAY, content);
    if (debug)
      LOG.debug(" message = {}", msg);
    return msg;
  }

  @Override
  public IPokerPlayerGuidelines getPokerPlayerGuidelines() {
    return this.PokerPlayerGuidelines;
  }

  public void setPokerPlayerGuidelines(final IPokerPlayerGuidelines PokerPlayerGuidelines) {
    this.PokerPlayerGuidelines = PokerPlayerGuidelines;
  }

  /**
   * @return - the communication channel string associated with this communication capability.
   */
  @Override
  public String getCommunicationChannelID() {
    return COMMUNICATION_CHANNEL_ID;
  }

  /**
   * Get a failure rate between the MIN_FAILURE and MAX_FAILURE rates.
   *
   * @return - the failure value.
   */
  @Override
  public synchronized double getFailure() {
    return 0;
  }

  public synchronized IPersona getOwner() {
    return owner;
  }

  public synchronized void setOwner(IPersona owner) {
    this.owner = owner;
  }

  /**
   * Get the parameters from this instance goal and use them to set the goal-specific guidelines.
   *
   * @param ig - the instance goal with the behavior information
   */
  @Override
  public synchronized void init(InstanceGoal<?> ig) {
    if (debug)
      LOG.debug("Initializing guidelines from goal: {}.", ig);

    // Get the parameter values from the existing active instance goal
    InstanceParameters params = Objects.requireNonNull((InstanceParameters) ig.getParameter());
    if (debug)
      LOG.info("Initializing with the given goal parameter guidelines: {}.", params);
    if (params == null) {
      LOG.error("Error: we need goal parameters to guide the auction communiction. ");
      System.exit(-4);
    }
    this.setPokerPlayerGuidelines(IPokerPlayerGuidelines.extractGuidelines(params));
    if (this.PokerPlayerGuidelines == null) {
      if (debug)
        LOG.info("Guidelines are null. params={}", params);
    }
    final IConnections bc = (IConnections) params.getParameters().get(PokerGoalParameters.pokerDealerConnections);
    LOG.info("Connections ={}. params={}", bc, params);

    this.setParentConnections(bc);
    if (this.parentConnections == null) {
      if (debug)
        LOG.info("Parent connections are null. params={}", params);
    }
  }

  /**
   * @return - the number of {@code IPokerMessage} on this local messages queue
   */
  @Override
  public int messages() {
    return 0;
  }

  /**
   * Returns the {@code DisplayInformation} object containing the information for the {@code ICapability}.
   *
   * @param displayInformation the data display.
   */
  @Override
  public synchronized void populateCapabilitiesOfDisplayObject(IDisplayInformation displayInformation) {
    super.populateCapabilitiesOfDisplayObject(displayInformation);
  }

  /**
   * @return {@code PokerMessage} received
   */
  @Override
  public IPokerMessage receive() {
    return null;
  }

  /**
   * Resets the {@code ICapability} and allows actions in the {@code ICapability} to be performed again for the new turn.
   */
  @Override
  public synchronized void reset() {

  }

  /**
   * Returns the {@code DOM} {@code Element} of the {@code IAttributable} or {@code ICapability}.  This method should
   * be overwritten by subclasses if there are additional variables defined whose values should be saved if they affect the
   * state of the object.  Overwritting can be done in two ways: adding additional information to the {@code Element}
   * returned by the super class, or creating a completely new element from scratch.
   *
   * @param document the document in which to create the {@code DOM} {@code Element}s.
   * @return the {@code DOM} {@code Element} of the {@code IAttributable} or {@code ICapability}.
   */
  @Override
  public Element toElement(Document document) {
    final Element capability = super.toElement(document);
    return capability;
  }

}
