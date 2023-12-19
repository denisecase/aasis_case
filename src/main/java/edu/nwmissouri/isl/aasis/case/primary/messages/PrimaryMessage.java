/**
 *
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
package edu.nwmissouri.isl.aasis.case.primary.messages;

import edu.ksu.cis.macr.aasis.agent.cc_message.BaseMessage;
import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Objects;

public class PrimaryMessage extends BaseMessage<PrimaryPerformative> implements IPrimaryMessage {
  private static final Logger LOG = LoggerFactory.getLogger(PrimaryMessage.class);
  private static final long serialVersionUID = 1L;

  /**
   Constructs a new instance of {@code PrimaryMessage}.
  
   @param sender - the UniqueIdentifier of the sending agent
   @param receiver - the UniqueIdentifier of the receiving agent
   @param performative - the unique name of the associated messages action on the plan model
   @param content - the object containing the custom contents of the messages
   */
  public PrimaryMessage(final UniqueIdentifier sender, final UniqueIdentifier receiver,
      final PrimaryPerformative performative, final Object content) {
    super(sender, receiver, performative, content);
    Objects.requireNonNull(content, "Local Primary Message Content cannot be null.");
  }

  /**
   Constructs a new instance of a {@code ConnectMessage}.
   */
  public PrimaryMessage() {
    super("", "", PrimaryPerformative.REPORT_OK, null);
    // LOG.debug("Created new PrimaryMessage {}", this.toString());
  }

  /**
   Constructs a new instance of {@code PrimaryMessage}.
  
   @param sender - the String name of the sending agent
   @param receiver - the String name of the receiving agent
   @param performative - the unique name of the associated messages action on the plan model
   @param content - the object containing the custom contents of the messages
   */
  public PrimaryMessage(final String sender, final String receiver, final PrimaryPerformative performative,
      final Object content) {
    super(sender, receiver, performative, content);
    Objects.requireNonNull(content, "Remote Message Content cannot be null.");
  }

  /**
   /** Constructs a new instance of a default {@code PrimaryMessage}.
  
   @return the {@code IPrimaryMessage} created
   */
  public static IPrimaryMessage createPrimaryMessage() {
    return new PrimaryMessage();
  }

  /**
   /** Constructs a new instance of {@code PrimaryMessage}.
  
   @param sender - String name of the agent sending the message
   @param receiver - String name of the agent to whom the message is sent
   @param performative - the {@code PrimaryPerformative} indicating the type of message
   @param content - the message content
   @return the IPrimaryMessage created
   */
  public static IPrimaryMessage createLocal(final UniqueIdentifier sender, final UniqueIdentifier receiver,
      final PrimaryPerformative performative, final Object content) {
    return new PrimaryMessage(sender, receiver, performative, content);
  }

  /**
   /** Constructs a new instance of {@code PrimaryPerformative}.
  
   @param senderString - String name of the agent sending the message
   @param receiverString - String name of the agent to whom the message is sent
   @param performative - the {@code PrimaryPerformative} indicating the type of message
   @param content - the message content
   @return - the IPrimaryMessage created
   */
  public static IPrimaryMessage createRemote(final String senderString, final String receiverString,
      final PrimaryPerformative performative, final Object content) {
    return new PrimaryMessage(senderString, receiverString, performative, content);
  }

  @Override
  public Object deserialize(final byte[] bytes) throws Exception {
    try (ByteArrayInputStream b = new ByteArrayInputStream(bytes)) {
      try (ObjectInput o = new ObjectInputStream(b)) {
        return o.readObject();
      }
    }
  }

  @Override
  public byte[] serialize() throws IOException {
    try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
      try (ObjectOutput o = new ObjectOutputStream(b)) {
        o.writeObject(this);
      }
      return b.toByteArray();
    }
  }

}
