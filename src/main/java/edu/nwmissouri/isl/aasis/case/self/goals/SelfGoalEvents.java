/**
 * Copyright 2012 Kansas State University MACR Laboratory
 * http://macr.cis.ksu.edu/ Department of Computing & Information Sciences
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
package edu.nwmissouri.isl.aasis.case.self.goals;

import edu.ksu.cis.macr.organization.model.identifiers.StringIdentifier;
import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;

import java.lang.reflect.Field;

/**
 A singleton enum class mapping all goal events shown on the refined goal models to associated {@code UniqueIdentifier}s.  The string names must exactly match the event name shown on the refined goal models.  By
 convention, the unique identifier names should be exactly the same as the name strings.
 */
public enum SelfGoalEvents {
  INSTANCE;
  public static final UniqueIdentifier addTicTacToeParticipant = StringIdentifier.getIdentifier("addTicTacToeParticipant");
  public static final UniqueIdentifier removeTicTacToeParticipant = StringIdentifier.getIdentifier("removeTicTacToeParticipant");
  public static final UniqueIdentifier addPokerParticipant = StringIdentifier.getIdentifier("addPokerParticipant");
  public static final UniqueIdentifier removePokerParticipant = StringIdentifier.getIdentifier("removePokerParticipant");

  @Override
  public String toString() {
    String s = "";
    try {
      final Field fields[] = Class.forName(this.getClass().getName()).getDeclaredFields();
      String s2 = "";
      for (final Field field : fields) {
        s2 = s.concat(field + " ");
      }
      return s2.concat(".");
    } catch (final ClassNotFoundException | SecurityException e) {
      s = "ERROR creating goal event toString()" + e.getMessage();
    }
    return s;
  }
}
