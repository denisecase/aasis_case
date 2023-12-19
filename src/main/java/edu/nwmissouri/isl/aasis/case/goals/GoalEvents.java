/**
 *
 * Copyright 2016 William Hargrave, Denise Case
 * Intelligent Systems Lab Northwest Missouri State University
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
package edu.nwmissouri.isl.aasis.case.goals;

import java.lang.reflect.Field;

import edu.ksu.cis.macr.organization.model.identifiers.StringIdentifier;
import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;

/**
 * A singleton enum class mapping all goal events shown on the refined goal
 * models to associated {@code UniqueIdentifier}s. The string names must exactly
 * match the event name shown on the refined goal models. By convention, the
 * unique identifier names should be exactly the same as the name strings.
 */
public enum GoalEvents {
  INSTANCE;

  public static final UniqueIdentifier Referees = StringIdentifier.getIdentifier("Referees");
  public static final UniqueIdentifier join = StringIdentifier.getIdentifier("join");
  public static final UniqueIdentifier administer = StringIdentifier.getIdentifier("administer");

  @Override
  public String toString() {
    String s = "";
    try {
      final Field fields[] = Class.forName(this.getClass().getName()).getDeclaredFields();
      String s2 = "";
      for (final Field field : fields)
        s2 = s.concat(field + " ");
      return s2.concat(".");
    } catch (final ClassNotFoundException | SecurityException e) {
      s = "ERROR creating goal event toString()" + e.getMessage();
    }
    return s;
  }
}
