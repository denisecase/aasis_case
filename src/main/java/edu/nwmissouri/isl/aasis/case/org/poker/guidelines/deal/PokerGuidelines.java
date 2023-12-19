/**
 * Copyright 2018 Denise Case
 *
 * See License.txt file for the license agreement.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package edu.nwmissouri.isl.aasis.case.org.poker.guidelines.deal;

import edu.ksu.cis.macr.goal.model.InstanceParameters;
import edu.ksu.cis.macr.organization.model.identifiers.StringIdentifier;

import java.io.Serializable;
import java.util.HashSet;

public class PokerGuidelines implements IPokerGuidelines, Serializable {
  private static final long serialVersionUID = 1L;
  private long purchaseTimeSlice;
  private int iteration;
  private int maxIteration;
  private long openTimeSlice;
  private HashSet<String> authorizedParticipants = new HashSet<>();

  public PokerGuidelines() {
  }

  public PokerGuidelines(long purchaseTimeSlice, int iteration, int maxIteration, long openTimeSlice,
      HashSet<String> authorizedParticipants) {

    this.purchaseTimeSlice = purchaseTimeSlice;

    this.maxIteration = maxIteration;
    this.openTimeSlice = openTimeSlice;
    this.authorizedParticipants = authorizedParticipants;
  }

  public synchronized static IPokerGuidelines extractGuidelines(InstanceParameters params) {
    return (IPokerGuidelines) params.getValue(StringIdentifier.getIdentifier("dealerGuidelines"));
  }

  public synchronized HashSet<String> getAuthorizedParticipants() {
    return authorizedParticipants;
  }

  public synchronized void setAuthorizedParticipants(HashSet<String> authorizedParticipants) {
    this.authorizedParticipants = authorizedParticipants;
  }

  public synchronized int getMaxIteration() {
    return this.maxIteration;
  }

  public synchronized void setMaxIteration(int maxIteration) {
    this.maxIteration = maxIteration;
  }

  public synchronized long getOpenTimeSlice() {
    return openTimeSlice;
  }

  public synchronized void setOpenTimeSlice(long openTimeSlice) {
    this.openTimeSlice = openTimeSlice;
  }

  public synchronized long getPurchaseTimeSlice() {
    return purchaseTimeSlice;
  }

  public synchronized void setPurchaseTimeSlice(long purchaseTimeSlice) {
    this.purchaseTimeSlice = purchaseTimeSlice;
  }

  @Override
  public String toString() {
    return "PokerGuidelines{" + ", purchaseTimeSlice=" + purchaseTimeSlice + ", iteration=" + iteration
        + ", maxIteration=" + maxIteration + ", openTimeSlice=" + openTimeSlice + ", authorizedParticipants="
        + authorizedParticipants + '}';
  }
}
