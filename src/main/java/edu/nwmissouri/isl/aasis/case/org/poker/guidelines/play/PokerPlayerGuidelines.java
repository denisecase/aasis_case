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
package edu.nwmissouri.isl.aasis.case.org.poker.guidelines.play;

import edu.ksu.cis.macr.goal.model.InstanceParameters;
import edu.ksu.cis.macr.organization.model.identifiers.StringIdentifier;

import java.io.Serializable;

/**
 * Class describing the prosumers behavior in the power market - indicates the time, quantity, price, and whether this is
 * available to buy or sell (current guidelines are for a single auction at a single future time); AO-MaSE Process for
 * adding fields:
 * 1) Add new fields with getters and setters.
 * 2) Add them as constructor parameters;
 * 3) Regenerate toString();
 * 4) Add getters and setters to associated interface class;
 * 5) Add fields to PokerPlayerGuidelinesBuilder class;
 * 6) Add code to Builder util as needed to create Initialize.xml file.
 * 7) Run builder.
 */
public class PokerPlayerGuidelines implements IPokerPlayerGuidelines, Serializable {
  public static final String STRING_IDENTIFIER = "PokerPlayerGuidelines";
  private static final long serialVersionUID = 1L;
  private long openingTimeSlice;
  private long purchaseTimeSlice;

  public PokerPlayerGuidelines() {
  }

  public PokerPlayerGuidelines(final long openingTimeSlice, final long purchaseTimeSlice) {
    this.openingTimeSlice = openingTimeSlice;
    this.purchaseTimeSlice = purchaseTimeSlice;
  }

  public synchronized static IPokerPlayerGuidelines extractGuidelines(final InstanceParameters params) {
    return (IPokerPlayerGuidelines) params.getValue(StringIdentifier.getIdentifier(STRING_IDENTIFIER));
  }

  @Override
  public synchronized long getOpeningTimeSlice() {
    return this.openingTimeSlice;
  }

  @Override
  public synchronized void setOpeningTimeSlice(final long openingTimeSlice) {
    this.openingTimeSlice = openingTimeSlice;
  }

  public synchronized long getPurchaseTimeSlice() {
    return this.purchaseTimeSlice;
  }

  @Override
  public synchronized void setPurchaseTimeSlice(final long purchaseTimeSlice) {
    this.purchaseTimeSlice = purchaseTimeSlice;
  }

  @Override
  public String toString() {
    return "PokerPlayerGuidelines{" +
        ", openingTimeSlice=" + openingTimeSlice + ", purchaseTimeSlice=" + purchaseTimeSlice +
        '}';
  }
}
