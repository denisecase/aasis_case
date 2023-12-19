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

import java.io.*;

/**
 The {@code PrimaryMessageContent} provides detailed information about a local prosumer agent's power needs and
 capabilities.
 */
public class PrimaryMessageContent implements Serializable, IPrimaryMessageContent {

  private static final long serialVersionUID = 1L;
  private long timeSlice = 0;

  /**
   Constructs a new instance of {@code PrimaryMessageContent}.
   */
  private PrimaryMessageContent() {
  }

  /**
   Constructs a new instance of {@code PrimaryMessageContent}.
  
   @param timeSlice - the current time slice.
   */
  public PrimaryMessageContent(final long timeSlice) {
    this.timeSlice = timeSlice;

  }

  public static PrimaryMessageContent createPrimaryMessageContent() {
    return new PrimaryMessageContent();
  }

  @Override
  public synchronized void add(IPrimaryMessageContent item) {

  }

  /**
   Deserialize the message.
  
   @param bytes - an array of bytes
   @return the deserialized {@code Message}
   @throws Exception - if an exception occurs.
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
  public long getTimeSlice() {
    return timeSlice;
  }

  @Override
  public synchronized void setTimeSlice(long timeSlice) {
    this.timeSlice = timeSlice;
  }

  @Override
  public boolean isEmpty() {
    return this.isEmpty();
  }

  private void readObject(java.io.ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException {
    stream.defaultReadObject();
  }

  /**
   Serialize the message.
  
   @return a byte array with the contents.
   @throws IOException - If an I/O error occurs.
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

  @Override
  public String toString() {
    return "PrimaryMessageContent{" + "timeSlice=" + timeSlice + '}';
  }

  private void writeObject(java.io.ObjectOutputStream stream) throws java.io.IOException {
    stream.defaultWriteObject();
  }

}
