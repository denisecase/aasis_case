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

import java.io.*;

/**
 * The {@code PokerMessageContent} provides information needed for power auction messages. capabilities.
 */
public class PokerMessageContent implements Serializable, IPokerMessageContent {

  private static final long serialVersionUID = 1L;

  private final long purchaseTimeSlice;
  private final String dealer;

  private PokerMessageContent(long purchaseTimeSlice, String broker) {
    this.purchaseTimeSlice = purchaseTimeSlice;
    this.dealer = broker;

  }

  /**
   * Create message content.
   *
   * @param purchaseTimeSlice - the time slice 
   * @param broker            - the unique name of the dealer
   * @return a new Message content.
   */
  public static PokerMessageContent create(long purchaseTimeSlice, String dealer) {
    return new PokerMessageContent(purchaseTimeSlice, dealer);
  }

  public String getDealer() {
    return dealer;
  }

  /**
   * Deserialize the message.
   *
   * @param bytes - an array of bytes
   * @return the deserialized {@code Message}
   * @throws Exception - if an exception occurs.
   */
  @Override
  public Object deserialize(final byte[] bytes) throws Exception {
    try (ByteArrayInputStream b = new ByteArrayInputStream(bytes)) {
      try (ObjectInput o = new ObjectInputStream(b)) {
        return o.readObject();
      }
    }
  }

  @Override
  public long getPurchaseTimeSlice() {
    return purchaseTimeSlice;
  }

  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
  }

  @Override
  public String toString() {
    return "MessageContent{" + "purchaseTimeSlice=" + purchaseTimeSlice + ", dealer='" + dealer + '\'' + '}';
  }

  /**
   * Serialize the message.
   *
   * @return a byte array with the contents.
   * @throws IOException - If an I/O error occurs.
   */
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
