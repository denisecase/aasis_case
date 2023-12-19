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

import edu.ksu.cis.macr.aasis.agent.persona.ICapability;
import edu.ksu.cis.macr.aasis.agent.persona.IInternalCommunicationCapability;
import edu.ksu.cis.macr.aasis.common.IConnections;
import edu.nwmissouri.isl.aasis.case.org.poker.guidelines.play.IPokerPlayerGuidelines;
import edu.nwmissouri.isl.aasis.case.org.poker.messages.IPokerMessage;
import edu.ksu.cis.macr.organization.model.InstanceGoal;

/**
 * Defines connecting and related capabilities.
 */
public interface IPokerCapability extends ICapability, IInternalCommunicationCapability.ICommunicationChannel {

  IConnections getParentConnections();

  /**
   * Used to create an Auction message to send to the auction exchange.
   *
   * @param currentTimeSlice - the long that is the current time slice.
   * @return - Auction Message that is just now created.
   */
  IPokerMessage createPokerMessage(long currentTimeSlice);

  /**
   * Get the auction guidelines.
   * @return the auction guidelines
   */
  IPokerPlayerGuidelines getPokerPlayerGuidelines();

  /**
   * @return - the communication channel string associated with this communication capability.
   */
  String getCommunicationChannelID();

  /**
   * Get the parameters from this instance goal and use them to set the goal-specific guidelines.
   *
   * @param ig - the instance goal with the behavior information
   */
  void init(InstanceGoal<?> ig);

  /**
   * Get the number of messages.
   * @return - the number of {@code IPokerMessage} on this local messages queue
   */
  int messages();

  /**
   * Receive a message.
   * @return {@code PokerMessage} received
   */
  IPokerMessage receive();

}