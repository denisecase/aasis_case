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
package edu.nwmissouri.isl.aasis.case.org.poker.messages;

import edu.ksu.cis.macr.aasis.agent.cc_message.BaseMessage;
import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Objects;


public class PokerMessage extends BaseMessage<PokerPerformative> implements IPokerMessage {
    private static final Logger LOG = LoggerFactory.getLogger(PokerMessage.class);
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new instance of {@code PokerMessage}.
     *
     * @param sender       - the UniqueIdentifier of the sending agent
     * @param receiver     - the UniqueIdentifier of the receiving agent
     * @param performative - the unique name of the associated messages action on the plan model
     * @param content      - the object containing the custom contents of the messages
     */
    private PokerMessage(final UniqueIdentifier sender, final UniqueIdentifier receiver,
                           final PokerPerformative performative, final Object content) {
        super(sender, receiver, performative, content);
        Objects.requireNonNull(content, "Auction Content cannot be null.");
    }

    /**
     * Constructs a new instance of a {@code PokerMessage}.
     */
    public PokerMessage() {
        super("", "", PokerPerformative.PLAY, null);
        // LOG.debug("Created new ConnectMessage {}", this.toString());
    }

    /**
     * Constructs a new instance of {@code PokerMessage}.
     *
     * @param sender       - the String name of the sending agent
     * @param receiver     - the String name of the receiving agent
     * @param performative - the unique name of the associated messages action on the plan model
     * @param content      - the object containing the custom contents of the messages
     */
    public PokerMessage(final String sender, final String receiver,
                          final PokerPerformative performative, final Object content) {
        super(sender, receiver, performative, content);
        Objects.requireNonNull(content, "Remote Message Content cannot be null.");
    }

    /**
     * /** Constructs a new instance of a default {@code PokerMessage}.
     *
     * @return the {@code IPrimaryMessage} created
     */
    public static PokerMessage createPokerMessage() {
        return new PokerMessage();
    }

    /**
     * /** Constructs a new instance of {@code PokerMessage}.
     *
     * @param sender       - String name of the agent sending the message
     * @param receiver     - String name of the agent to whom the message is sent
     * @param performative - the {@code PowerPerformative} indicating the type of message
     * @param content      - the message content
     * @return the IPokerMessage created
     */
    public static IPokerMessage createLocal(final UniqueIdentifier sender, final UniqueIdentifier receiver,
                                              final PokerPerformative performative, final Object content) {
        return new PokerMessage(sender, receiver, performative, content);
    }

    /**
     * /** Constructs a new instance of {@code PokerMessage}.
     *
     * @param senderString   - String name of the agent sending the message
     * @param receiverString - String name of the agent to whom the message is sent
     * @param performative   - the {@code PowerPerformative} indicating the type of message
     * @param content        - the message content
     * @return the IPokerMessage created
     */
    public static IPokerMessage createRemote(final String senderString, final String receiverString,
                                               final PokerPerformative performative, final Object content) {
        return new PokerMessage(senderString, receiverString, performative, content);
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
