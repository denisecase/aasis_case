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
package edu.nwmissouri.isl.aasis.case.primary.capabilities.participate;

import edu.ksu.cis.macr.aasis.agent.persona.ICapability;
import edu.ksu.cis.macr.aasis.agent.persona.IInternalCommunicationCapability;
import edu.ksu.cis.macr.aasis.common.IConnections;
import edu.nwmissouri.isl.aasis.case.primary.messages.IPrimaryMessage;
import edu.nwmissouri.isl.aasis.case.primary.messages.IPrimaryMessageContent;
import edu.nwmissouri.isl.aasis.case.primary.messages.PrimaryPerformative;
import edu.ksu.cis.macr.organization.model.InstanceGoal;
import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;
import java.util.List;
import java.util.Queue;

/**
 The {@code IPrimaryCommunicationCapability} interface for defining power-related communication capabilities.
 */
public interface IPrimaryCommunicationCapability
    extends ICapability, IInternalCommunicationCapability.ICommunicationChannel {

  IPrimaryMessage checkFromSelf();

  IPrimaryMessage checkForLocalPrimaryMessageFromWorker();

  IPrimaryMessage checkFromAdmin(String ownerSelfPersona);

  /**
   Used by the sensor persona to create a local power message to send to the self persona for review.
   @param timeSlice - the time slice since the simulation began
   @return the {@code IPrimaryMessage} created
   */
  IPrimaryMessage createLocalPrimaryMessageForSelf(long timeSlice);

  IPrimaryMessage createLocalPrimaryMessageForSuperSelf(IPrimaryMessageContent newContent);

  void sendUp(IPrimaryMessage localMessage, IConnections upConnections);

  boolean forwardToParticipant(IPrimaryMessage localPrimaryMessage, UniqueIdentifier subIdentifier);

  /**
   @return the {@code ICommunicationChannel} this {@code Capability} uses.
   */
  IInternalCommunicationCapability.ICommunicationChannel getCommunicationChannel();

  /**
   @return - the communication channel string associated with this communication capability.
   */
  String getCommunicationChannelID();

  /*
  * Returns the set of {@code IPrimaryMessage} as a queue.
  */
  Queue<IPrimaryMessage> getLocalMessages();

  /*
  * Returns the set of {@code IPrimaryMessage} as a list.
  */
  List<IPrimaryMessage> getLocalMessagesAsList();

  /**
   Get the parameters from this instance goal and use them to set the goal-specific guidelines for any child
   connections.
  
   @param instanceGoal - the instance goal provided
   */
  void initializeChildConnections(InstanceGoal<?> instanceGoal);

  /**
   Get the parameters from this instance goal and use them to set the goal-specific guidelines.
  
   @param ig - the instance goal with the behavior information
   */
  void init(InstanceGoal<?> ig);

  void initializeParentConnections(InstanceGoal<?> instanceGoal);

  /**
   @return - the number of {@code IPrimaryMessage} on this local messages queue
   */
  int messages();

  /**
   @return {@code PrimaryMessage} received
   */
  IPrimaryMessage receive();

  /**
   @param message - the PrimaryMessage messages to be sent
   @return {@code true} if the messages was sent, {@code false} otherwise.
   */
  boolean send(IPrimaryMessage message);

  void sendControllerRequest(PrimaryPerformative reportOutOfBounds, IPrimaryMessageContent request);

  /**
   Send local message.
  
   @param message - the PrimaryMessage messages to be sent through the internal agent communication system.
   @return {@code true} if the messages was sent, {@code false } otherwise.
   */
  boolean sendLocal(IPrimaryMessage message);

  // boolean sendRemotePrimaryMessageToSuperList(IPrimaryMessage localPrimaryMessage, IConnections parentConnections);

  boolean sendRemotePrimaryMessageAggregateToSuperSelf(IPrimaryMessage selfLocalMessage);

  /**
   Sets up the RabbitMQ queues and binding keys required for operation.
   */
  void setupPrimaryMessagingToSuper();

  void setupQueuesAndBindingsForSelfPersonaFromSuper();

  /**
   Sets up the RabbitMQ queues and binding keys required for operation.
   */
  void setupQueuesAndBindingsForSuperSelfPersona();
}