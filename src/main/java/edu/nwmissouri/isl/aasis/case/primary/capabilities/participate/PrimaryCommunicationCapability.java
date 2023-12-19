/*
 * Copyright 2012 - 2018 Denise Case
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
package edu.nwmissouri.isl.aasis.case.primary.capabilities.participate;

import com.rabbitmq.client.*;
import edu.ksu.cis.macr.aasis.agent.persona.AbstractOrganizationCapability;
import edu.ksu.cis.macr.aasis.agent.persona.IInternalCommunicationCapability;
import edu.ksu.cis.macr.aasis.agent.persona.IOrganization;
import edu.ksu.cis.macr.aasis.agent.persona.IPersona;
import edu.ksu.cis.macr.aasis.common.IConnectionGuidelines;
import edu.ksu.cis.macr.aasis.common.IConnections;
import edu.ksu.cis.macr.aasis.messaging.MessagingManager;
import edu.ksu.cis.macr.aasis.messaging.MessagingReliabilityManager;
import edu.ksu.cis.macr.goal.model.InstanceParameters;
import edu.nwmissouri.isl.aasis.case.config.RunManager;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.messaging.TicTacToeMessagingFocus;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.messaging.TicTacToeMessagingManager;
import edu.nwmissouri.isl.aasis.case.guidelines.IInitialGuidelines;
import edu.nwmissouri.isl.aasis.case.primary.messages.*;
import edu.ksu.cis.macr.organization.model.InstanceGoal;
import edu.ksu.cis.macr.organization.model.identifiers.StringIdentifier;
import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The {@code PrimaryCommunicationCapability} used to communicate power allocations.
 */
public class PrimaryCommunicationCapability extends AbstractOrganizationCapability implements IPrimaryCommunicationCapability {
    private static final Logger LOG = LoggerFactory.getLogger(PrimaryCommunicationCapability.class);
    private static final boolean debug = true;
    private static final String QUEUE_PURPOSE = "PRIMARY";
    private static final String COMMUNICATION_CHANNEL_ID = "PrimaryCommunicationChannel";
    private static ConcurrentLinkedQueue<IPrimaryMessage> localMessages = new ConcurrentLinkedQueue<>();
    private IInitialGuidelines initialGuidelines = null;
    private IConnections parentConnections = null;
    private IConnections tictactoeConnections = null;
    private static final TicTacToeMessagingFocus messagingFocus = TicTacToeMessagingFocus.PRIMARY;
    private static Channel channel;
    private QueueingConsumer consumer;
    double communicationReliability = 1.0;
    double communicationDelay = 0.0;

    /**
     * Constructs a new instance of {@code PrimaryCommunication}.
     *
     * @param owner - the entity to which this capability belongs.
     * @param org   - the {@code Organization} in which this {@code IAgent} acts.
     */
    public PrimaryCommunicationCapability(IPersona owner, IOrganization org) {
        super(IPrimaryCommunicationCapability.class, owner, org);
        if (debug) LOG.debug("\t New PrimaryCommunicationCapability from super class. Owner={}, org={}.", owner, org);
        this.setOwner(Objects.requireNonNull(owner));
        LOG.debug("Owner set");
        channel = TicTacToeMessagingManager.getChannel(messagingFocus);
        LOG.debug("channel set");
        consumer = new QueueingConsumer(Objects.requireNonNull(channel, "Error null channel in receive()."));
        LOG.debug("consumer set");
        initializeReliabilityAndDelay();
        LOG.debug("ReliabilityAndDelay initialized.");
    }

    private void initializeReliabilityAndDelay() {
        try {
            this.communicationReliability = MessagingReliabilityManager.getCommunicationReliability();
            if (debug) LOG.debug("\t New  communicationReliability.");
            this.communicationDelay = MessagingReliabilityManager.getCommunicationDelay();
        } catch (Exception e) {
            // just use the defaults
            this.communicationDelay = 0.0;
            this.communicationReliability = 1.0;
        }
        if (debug)
            LOG.debug("New comm cap with reliability = {} and delay = {}", communicationReliability, communicationDelay);
    }

    /**
     * @param owner        - the entity to which this capability belongs.
     * @param organization - the {@code Organization} in which this {@code IAgent} acts.
     * @param Parameter    - optional parameter
     */
    public PrimaryCommunicationCapability(IPersona owner, IOrganization organization, String Parameter) {
        super(IPrimaryCommunicationCapability.class, owner, organization);
        this.setOwner(Objects.requireNonNull(owner));
        channel = TicTacToeMessagingManager.getChannel(messagingFocus);
        consumer = new QueueingConsumer(Objects.requireNonNull(channel, "Error null channel in receive()."));
        initializeReliabilityAndDelay();
    }

    @Override
    public synchronized void channelContent(final Object content) {
        localMessages.add((IPrimaryMessage) content);
    }

    public IPrimaryMessage checkForLocalPrimaryMessage() {
        return receive();
    }


    @Override
    public IPrimaryMessage checkFromSelf() {
        int numMessages = this.getLocalMessagesAsList().size();
        if (debug && numMessages > 0) LOG.info("There are {} messages to check.", numMessages);
        for (IPrimaryMessage checkMessage : this.getLocalMessagesAsList()) {
            String sender = checkMessage.getLocalSender().toString(); // e.g. selfH44
            String senderNoSelf = checkMessage.getLocalSender().toString().replace("self", "");  // e.g. H44
            String me = getOwner().getUniqueIdentifier().toString(); // eg. H44inN43
            String meNoInOrg = me.substring(0, me.indexOf("in"));

            if (debug)
                LOG.debug("  CHECKING: checkFromSelf sender= {} senderNoSelf={} me={} meNoInOrg={}. {}", sender, senderNoSelf, me, meNoInOrg, checkMessage);
            if (sender.contains("self") && senderNoSelf.equals(meNoInOrg)) {
                LOG.info("MESSAGE forwarded to sub RECEIVED: {}.", checkMessage.toString());
                return checkMessage;
            }
        }
        return null;
    }

    @Override
    public IPrimaryMessage checkForLocalPrimaryMessageFromWorker() {
        int numMessagesToCheck = this.getLocalMessagesAsList().size();
        if (debug && numMessagesToCheck > 0)
            LOG.debug("There are {} local messages to check (looking for sensor worker messages).", numMessagesToCheck);
        for (IPrimaryMessage checkMessage : this.getLocalMessagesAsList()) {
            String rec = checkMessage.getLocalReceiver().toString();
            String me = getOwner().getUniqueIdentifier().toString();
            if (debug)
                LOG.debug("  CHECKING: checkForLocalPrimaryMessageFromWorker rec= {} me={}. {}", rec, me, checkMessage);

            if (rec.contains("self") && rec.equals(me)) {
                LOG.info("Local MESSAGE message to self RECEIVED from sensor: {}.", checkMessage.toString());
                return checkMessage;
            }
        }
        return null;
    }

    @Override
    public synchronized IPrimaryMessage checkFromAdmin(String ownerSelfPersona) {
        if (ownerSelfPersona.startsWith("selfH")) {
            if (debug)
                LOG.debug("This is persona {} and home agents don't have a super holon persona.", ownerSelfPersona);
            return null;
        }
        if (debug)
            LOG.debug("{} Checking for REMOTE MESSAGE (Aggregate) from internal ADMIN {}", ownerSelfPersona, ownerSelfPersona);
        try {
            final String agentName = ownerSelfPersona.replace("self", "");
            final String sender = agentName + "in" + agentName;
            final String receiver = ownerSelfPersona;
            final String queueName = buildQueueLinkFromSenderAndReceiver(sender, receiver);
            if (debug)
                LOG.debug("{} Checking for REMOTE MESSAGE (Aggregate) on {} from an internal admin persona to {}", ownerSelfPersona, queueName, ownerSelfPersona);
            IPrimaryMessage m = this.receiveRemoteFromAdmin(queueName);
            if (m != null) {
                LOG.info("SELF RECEIVED ADMIN MESSAGE: {}. ", m.toString());
                return m;
            }
        } catch (ShutdownSignalException | ConsumerCancelledException | IOException | InterruptedException e) {
            LOG.error("ERROR: checking for power message from an internal acting super ({})", e.getMessage());
        }
        return null;
    }

    public static synchronized String buildQueueLinkFromSenderAndReceiver(final String remoteSender, final String remoteReceiver) {
        return remoteSender + "-" + remoteReceiver;
    }

    @Override
    public synchronized IPrimaryMessage createLocalPrimaryMessageForSelf(long timeSlice) {
        IPrimaryMessageContent rptMessageContent = PrimaryMessageContent.createPrimaryMessageContent();
        rptMessageContent.setTimeSlice(timeSlice);

        if (debug)
            LOG.debug("Creating new power message for self at time slice {} with min={} and max={}", timeSlice);
      

        final UniqueIdentifier me = this.getOwner().getUniqueIdentifier();
        UniqueIdentifier localMaster = this.getOwner().getPersonaControlComponent().getLocalMaster();

        PrimaryPerformative perf = PrimaryPerformative.REPORT_OK;

        IPrimaryMessage localPrimaryMessage = PrimaryMessage.createLocal(me, localMaster, perf, rptMessageContent);
        LOG.debug("Primary message created: {}.", localPrimaryMessage);
        return localPrimaryMessage;
    }

    private IPrimaryMessage createLocalPrimaryMessageForSub(UniqueIdentifier me, UniqueIdentifier subPersona, PrimaryPerformative perf, IPrimaryMessageContent rptMessageContent) {
        IPrimaryMessage localPrimaryMessage = PrimaryMessage.createLocal(me, subPersona, perf, rptMessageContent);
        if (debug) LOG.debug("Primary message for sub created: {}.", localPrimaryMessage);
        return localPrimaryMessage;
    }

    @Override
    public synchronized IPrimaryMessage createLocalPrimaryMessageForSuperSelf(IPrimaryMessageContent givenContent) {
        if (givenContent == null) return null;
        if (debug) LOG.info("Creating new power message for super self at time slice {}", givenContent.getTimeSlice());

        final UniqueIdentifier me = this.getOwner().getUniqueIdentifier();
        final String meString = me.toString();
        final String recString = "self" + meString.substring(0, meString.indexOf("in"));
        final UniqueIdentifier rec = StringIdentifier.getIdentifier(recString);

        PrimaryPerformative perf = PrimaryPerformative.REPORT_OK;


        IPrimaryMessage localPrimaryMessage = PrimaryMessage.createLocal(me, rec, perf, givenContent);
        if (debug) LOG.debug("Primary message created: {}.", localPrimaryMessage);
        return localPrimaryMessage;
    }

    @Override
    public synchronized void sendUp(IPrimaryMessage localMessage, IConnections upConnections) {
        if (debug) LOG.info("sendUp: received local message was: {}", localMessage);
        if (localMessage == null) return;

        // change receiver to admin and send over external messaging system.
        for (IConnectionGuidelines cg : upConnections.getListConnectionGuidelines()) {
            if (debug) LOG.debug("Guidelines include super holon {}.", cg.getOtherAgentAbbrev());

            UniqueIdentifier newSender = localMessage.getLocalReceiver();
            UniqueIdentifier newReceiver = StringIdentifier.getIdentifier(cg.getExpectedMasterAbbrev());
            IPrimaryMessage newLocalMessage = PrimaryMessage.createLocal(newSender, newReceiver, localMessage.getPerformativeType(), localMessage.getContent());

            String sender = newSender.toString();
            String rec = newReceiver.toString();

            // new local message becomes the entire message contents for the remote message
            IPrimaryMessage remoteMessage = PrimaryMessage.createRemote(sender, rec, newLocalMessage.getPerformativeType(), newLocalMessage.getContent());
            LOG.info("Created REMOTE MESSAGE for org admin: {}", remoteMessage.toString());
            Objects.requireNonNull(remoteMessage, "ERROR: Can't send a null  message.");
            Objects.requireNonNull(channel, "ERROR: null channel in send().");
            sendRemoteMessage(remoteMessage, remoteMessage.getRemoteSender() + "-" + remoteMessage.getRemoteReceiver());
        }
    }

    @Override
    public synchronized boolean forwardToParticipant(IPrimaryMessage origMessage, UniqueIdentifier subIdentifier) {
        IPrimaryMessage localPrimaryMessage = PrimaryMessage.createLocal(this.getOwner().getUniqueIdentifier(), subIdentifier, origMessage.getPerformativeType(), origMessage.getContent());
        if (debug) LOG.debug("New power message for sub is {}", localPrimaryMessage);
        boolean success = sendLocal(localPrimaryMessage);
        return success;
    }

    public synchronized IConnections getChildConnections() {
        return this.tictactoeConnections;
    }

    public synchronized void setChildConnections(IConnections tictactoeConnections) {
        this.tictactoeConnections = tictactoeConnections;
    }

    @Override
    public IInternalCommunicationCapability.ICommunicationChannel getCommunicationChannel() {
        return this;
    }

    @Override
    public String getCommunicationChannelID() {
        return COMMUNICATION_CHANNEL_ID;
    }

    @Override
    public double getFailure() {
        return MIN_FAILURE;
    }


    public synchronized IInitialGuidelines getInitialGuidelines() {
        return this.initialGuidelines;
    }

    public synchronized void setInitialGuidelines(IInitialGuidelines g) {
        this.initialGuidelines = g;
    }


    @Override
    public synchronized Queue<IPrimaryMessage> getLocalMessages() {
        LOG.debug(" {} LOCAL MESSAGES in Queue ", localMessages.size());
        return localMessages;
    }

    @Override
    public List<IPrimaryMessage> getLocalMessagesAsList() {
        return new ArrayList<>(localMessages);
    }


    public synchronized IConnections getParentConnections() {
        return this.parentConnections;
    }

    public synchronized void setParentConnections(IConnections parentConnections) {
        this.parentConnections = parentConnections;
    }

    @Override
    public synchronized void initializeChildConnections(InstanceGoal<?> instanceGoal) {
        if (debug) LOG.debug("Initializing power connection to sub holon from goal: {}.", instanceGoal);

        // Get the parameter values from the existing active instance goal
        final InstanceParameters params = Objects.
                requireNonNull((InstanceParameters) instanceGoal
                        .getParameter());
        setChildConnections(IConnections.extractConnections(params, "tictactoeConnections"));
    }

    @Override
    public synchronized void init(InstanceGoal<?> instanceGoal) {

        // Get the parameter values from the existing active instance goal
        final InstanceParameters params = (InstanceParameters) instanceGoal.getParameter();
        if (debug) LOG.debug("params={}", params);

        final IInitialGuidelines g = (IInitialGuidelines) params.getValue(StringIdentifier.getIdentifier("playerGuidelines"));
        if (debug && g != null) LOG.debug("g={}",g);
        this.setInitialGuidelines(g);

    }

    /**
     * Get the parameters from this instance goal and use them to set the goal-specific guidelines for any parent
     * connections.
     *
     * @param instanceGoal - this instance of the specification goal
     */
    @Override
    public synchronized void initializeParentConnections(InstanceGoal<?> instanceGoal) {
        if (debug) LOG.debug("Initializing super holon power connections from goal: {}.", instanceGoal);

        // Get the parameter values from the existing active instance goal
        final InstanceParameters params = Objects.
                requireNonNull((InstanceParameters) instanceGoal
                        .getParameter());
        if (debug) LOG.debug("Initializing external power communication params: {}.", params);

        setParentConnections(IConnections.extractConnections(params, "parentConnections"));
    }

    @Override
    public int messages() {
        return localMessages.size();
    }

    public synchronized boolean publish(String fullQueueName, byte[] messageBodyBytes) {
        if (debug) LOG.debug("SENDING serialized MESSAGE to {}. bytes={}", fullQueueName, messageBodyBytes.length);

        Objects.requireNonNull(channel, "ERROR: null channel in send().");
        try {
            channel.basicPublish(MessagingManager.getExchangeName(messagingFocus), fullQueueName, MessageProperties.PERSISTENT_TEXT_PLAIN, messageBodyBytes);
            LOG.info("SENT serialized MESSAGE to {}. bytes={}", fullQueueName, messageBodyBytes.length);
            return true;
        } catch (Exception e) {
            LOG.error("Error sending serialized message on {}", fullQueueName);
            System.exit(-56);
        }
        return false;
    }

//  @Override
//  public IPrimaryMessage receive(String queueName) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
//    return null;
//  }

    @Override
    public synchronized IPrimaryMessage receive() {
        return localMessages.poll();
    }

    private IPrimaryMessage receiveRemote(String queueLink) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {

        LOG.debug("Attempting to receive remote power message on queue {}.", queueLink);
        return remoteRECEIVE(queueLink);

    }


    /**
     * @param queueLink - the agent-based part of the queue name
     * @return String messages - Grabs messages from Queue
     * @throws java.io.IOException - Handles all IO Exceptions
     * @throws com.rabbitmq.client.ShutdownSignalException - Handles Shutdown Signal Exception from RabbitMQ
     * @throws com.rabbitmq.client.ConsumerCancelledException - Handles Consumer Cancellation Exception from RabbitMQ
     * @throws InterruptedException - Handles all interrupts
     */
    public synchronized IPrimaryMessage receiveRemoteFromAdmin(String queueLink) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
        String fullQueueName = TicTacToeMessagingManager.getFullQueueName(queueLink, QUEUE_PURPOSE);
        MessagingManager.declareAndBindConsumerQueue(messagingFocus, fullQueueName);
        LOG.info("SELF CHECKING FOR remote power on {} ", fullQueueName);
        String basicConsume = channel.basicConsume(fullQueueName, true, consumer);
        //if (debug) LOG.debug("Result of basicConsume: {}", basicConsume);

        // check for delivery for given milliseconds
        QueueingConsumer.Delivery delivery = consumer.nextDelivery(RunManager.getDeliveryCheckTime_ms());
        if (delivery == null) {
            LOG.debug("got nothing on {}", fullQueueName);
            return null;
        }
        if (debug) LOG.debug("got something on {}", fullQueueName);
        IPrimaryMessage message = new PrimaryMessage();
        try {
            message = (IPrimaryMessage) message.deserialize(delivery.getBody());
            if (debug) LOG.debug("Received REMOTE MESSAGE on {}. {}.", fullQueueName, message);
        } catch (Exception ex) {
            LOG.error("ERROR: {}", ex.toString());
        }
        return message;
    }

    public synchronized IPrimaryMessage remoteRECEIVE(final String queueLink) throws IOException, ShutdownSignalException, InterruptedException {
        final QueueingConsumer consumer = new QueueingConsumer(Objects.requireNonNull(channel, "Error null channel in receive()."));
        final String fullQueueName = TicTacToeMessagingManager.getFullQueueName(queueLink, QUEUE_PURPOSE);

        MessagingManager.declareAndBindConsumerQueue(messagingFocus, fullQueueName);
        if (debug) LOG.debug("Checking for remote TICTACTOE message on queue {}", fullQueueName);
        String basicConsume = channel.basicConsume(fullQueueName, true, consumer);

        // check for delivery for given milliseconds
        final QueueingConsumer.Delivery delivery = consumer.nextDelivery(RunManager.getDeliveryCheckTime_ms());

        if (delivery != null) {
            if (debug) LOG.debug("got something on {}", fullQueueName);
            IPrimaryMessage message = PrimaryMessage.createPrimaryMessage();
            if (debug)
                LOG.debug("Deserializing delivery. Created new PrimaryMessage={}. Delivery body={}", message.toString(), Arrays.toString(delivery.getBody()));
            try {
                message = (IPrimaryMessage) message.deserialize(delivery.getBody());
                LOG.info("RECEIVED remote message on {}. {}.", fullQueueName, message);
                return message;
            } catch (Exception ex) {
                LOG.error("ERROR deserializing power message: {}", ex.getCause().toString());
                //System.exit(-11);
            }
        }
        if (debug) LOG.debug("got nothing on {}", fullQueueName);
        return null;
    }

    private String getFullQueueName(final String queueLink) {
        return QUEUE_PURPOSE + "." + queueLink;
    }

    @Override
    public synchronized void reset() {
    }

    @Override
    public synchronized boolean send(IPrimaryMessage message) {
        return sendLocal(message);
    }

    @Override
    public synchronized void sendControllerRequest(PrimaryPerformative reportOutOfBounds, IPrimaryMessageContent request) {
        LOG.error("Not implemented yet.");
    }

    @Override
    public synchronized boolean sendLocal(IPrimaryMessage message) {
        if (debug) LOG.info(" sending local power messages {} kW from {} to {}", message
                .toString(), message.getLocalSender().toString(), message.getLocalReceiver().toString());

        // note: using the sendLocal method in IInternalCommunicationCapability
        boolean success = getOwner().getCapability(IInternalCommunicationCapability.class).sendLocal(
                message.getLocalReceiver(), this.getCommunicationChannelID(), message);
        if (!success) {
            if (debug) LOG.debug(" sending local power message failed ");
        }
        return success;
    }

    public synchronized boolean sendRemoteMessage(IPrimaryMessage message, String queueLink) {
        Objects.requireNonNull(message, "ERROR: Can't send a null remote message.");
        String fullQueueName = TicTacToeMessagingManager.getFullQueueName(queueLink, QUEUE_PURPOSE);
        LOG.info("Forwarding REMOTE MESSAGE on {}. {}", fullQueueName, message);
        MessagingManager.declareAndBindConsumerQueue(messagingFocus, fullQueueName);
        publish(fullQueueName, serializeMessage(message));
        LOG.info("Sent REMOTE MESSAGE on {}. {}. ", fullQueueName, message.toString());
        return true;
    }

    @Override
    public synchronized boolean sendRemotePrimaryMessageAggregateToSuperSelf(IPrimaryMessage selfLocalMessage) {
        if (selfLocalMessage == null) return false;
        if (debug) LOG.debug("The local power message to send is: {} ", selfLocalMessage);

        //  local message becomes the entire message contents for the remote message
        String sender = selfLocalMessage.getLocalSender().toString();
        String rec = selfLocalMessage.getLocalReceiver().toString();

        IPrimaryMessage remoteMessage = PrimaryMessage.createRemote(sender, rec, selfLocalMessage.getPerformativeType(), selfLocalMessage.getContent());
        LOG.info("The REMOTE MESSAGE for super holon's self persona is {}", remoteMessage.toString());
        sendRemoteMessage(remoteMessage, remoteMessage.getRemoteSender() + "-" + remoteMessage.getRemoteReceiver());
        return true;
    }

    private synchronized boolean sendRemotePrimaryMessageToSuperList(IPrimaryMessage localPrimaryMessageFromSelf, IConnections parentConnections) {
        if (localPrimaryMessageFromSelf == null) return false;
        if (debug)
            LOG.debug("sendRemotePrimaryMessageToSuperList: {} {}", localPrimaryMessageFromSelf, parentConnections.getListConnectionGuidelines().get(0));

        // change receiver to super holon and send over external messaging system.
        for (IConnectionGuidelines cg : parentConnections.getListConnectionGuidelines()) {
            if (debug) LOG.debug("Guidelines include super holon {}.", cg.getOtherAgentAbbrev());

            UniqueIdentifier newSender = localPrimaryMessageFromSelf.getLocalReceiver();
            UniqueIdentifier newReceiver = StringIdentifier.getIdentifier(cg.getExpectedMasterAbbrev());
            IPrimaryMessage newLocalMessage = PrimaryMessage.createLocal(newSender, newReceiver, localPrimaryMessageFromSelf.getPerformativeType(), localPrimaryMessageFromSelf.getContent());

            String sender = newSender.toString();
            String rec = newReceiver.toString();

            // new local message becomes the entire message contents for the remote message
            IPrimaryMessage remoteMessage = PrimaryMessage.createRemote(sender, rec, newLocalMessage.getPerformativeType(), newLocalMessage.getContent());
            LOG.info("Created REMOTE TICTACTOE MESSAGE for external organization super holon: {}", remoteMessage.toString());
            Objects.requireNonNull(remoteMessage, "ERROR: Can't send a null remote power message.");
            Objects.requireNonNull(channel, "ERROR: null channel in send().");
            sendRemoteMessage(remoteMessage, remoteMessage.getRemoteSender() + "-" + remoteMessage.getRemoteReceiver());
        }
        return true;
    }

    private synchronized byte[] serializeMessage(final IPrimaryMessage message) {
        byte[] messageBodyBytes = null;
        try {
            messageBodyBytes = message.serialize();
        } catch (IOException e) {
            LOG.error("Error serializing message {}", message.toString());
            System.exit(-56);
        }
        return messageBodyBytes;
    }

    @Override
    public synchronized void setupPrimaryMessagingToSuper() {
        for (IConnectionGuidelines g : this.getParentConnections().getListConnectionGuidelines()) {
            if (debug) LOG.debug("Setting up queues and bindings to any super holon persona: {}.", g);
            final String other = g.getOtherAgentAbbrev().trim();
            final String org = g.getOrganizationAbbrev().trim();
            final String master = g.getExpectedMasterAbbrev().trim();
            final String myPersona = owner.getUniqueIdentifier().toString();
            final String queueLink = myPersona + "-" + other;
            // set up messaging to handle messages from child to remote parent
            String fullQueueName = TicTacToeMessagingManager.getFullQueueName(queueLink, QUEUE_PURPOSE);
            LOG.debug("Setting up queues and bindings to connect self persona {} to {} via {} for {}.", myPersona, other, fullQueueName, messagingFocus);
            MessagingManager.declareAndBindConsumerQueue(messagingFocus, fullQueueName);

        }
    }

    private void setupQueuesAndBindings(String queueLink) {
        String fullQueueName = TicTacToeMessagingManager.getFullQueueName(queueLink, QUEUE_PURPOSE);
        MessagingManager.declareAndBindConsumerQueue(messagingFocus, fullQueueName);
    }

    @Override
    public synchronized void setupQueuesAndBindingsForSelfPersonaFromSuper() {
        if (debug) LOG.debug("Setting up queues and bindings for self (me) from any grid super holons.");
        final String selfPersona = owner.getUniqueIdentifier().toString();  // eg. "N43inN43"
        final String agentName = selfPersona.replace("self", "");
        final String superPersona = agentName + "in" + agentName;
        final String queueLink = superPersona + "-" + selfPersona;
        final String fullQueueName = TicTacToeMessagingManager.getFullQueueName(queueLink, QUEUE_PURPOSE);
        LOG.debug("Setting up queues and bindings for self persona (me) from super: {} to {} via {} for {}.", superPersona, selfPersona, fullQueueName, messagingFocus);
        MessagingManager.declareAndBindConsumerQueue(messagingFocus, fullQueueName);

    }

    @Override
    public synchronized void setupQueuesAndBindingsForSuperSelfPersona() {
        LOG.debug("Setting up queues and bindings to my self persona.");
        final String myPersona = owner.getUniqueIdentifier().toString();  // eg. "N43inN43"
        final String agentName = myPersona.substring(0, myPersona.indexOf("in"));
        final String selfPersona = "self" + agentName;
        final String queueLink = myPersona + "-" + selfPersona;
        // set up messaging to forward aggregate messages to my self persona
        String fullQueueName = TicTacToeMessagingManager.getFullQueueName(queueLink, QUEUE_PURPOSE);
        LOG.debug("Setting up queues and bindings for super self persona: {} (me) to {} via {} for {}.", myPersona, selfPersona, fullQueueName, messagingFocus);
        MessagingManager.declareAndBindConsumerQueue(messagingFocus, fullQueueName);

    }

}
