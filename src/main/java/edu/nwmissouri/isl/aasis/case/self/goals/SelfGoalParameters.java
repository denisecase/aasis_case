/**
 * Copyright 2012 
 * Kansas State University MACR Laboratory http://macr.cis.ksu.edu/
 * Department of Computing & Information Sciences
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
package edu.nwmissouri.isl.aasis.case.self.goals;

import edu.ksu.cis.macr.organization.model.identifiers.StringIdentifier;
import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;

import java.lang.reflect.Field;

/**
 A singleton enum GoalParameters class mapping all goal parameters shown on the refined goal models to associated {@code UniqueIdentifier}s.  The string names must exactly match the variable names (not types) of the goal parameters shown
 on the refined goal models.  By convention, the unique identifier names should be exactly the same as the name
 strings.
 */
public enum SelfGoalParameters {
  INSTANCE;
  public static final UniqueIdentifier pokerConnections = StringIdentifier.getIdentifier("pokerConnections");
  public static final UniqueIdentifier pokerGuidelines = StringIdentifier.getIdentifier("pokerGuidelines");
  public static final UniqueIdentifier pokerDealerConnections = StringIdentifier.getIdentifier("pokerDealerConnections");
  public static final UniqueIdentifier pokerPlayerGuidelines = StringIdentifier.getIdentifier("pokerPlayerGuidelines");
  public static final UniqueIdentifier tictactoeConnections = StringIdentifier.getIdentifier("tictactoeConnections");
  public static final UniqueIdentifier tictactoeGuidelines = StringIdentifier.getIdentifier("tictactoeGuidelines");
  public static final UniqueIdentifier tictactoeRefereeConnections = StringIdentifier.getIdentifier("tictactoeRefereeConnections");
  public static final UniqueIdentifier tictactoePlayerGuidelines = StringIdentifier.getIdentifier("tictactoePlayerGuidelines");

  @Override
  public String toString() {
    try {
      Field fields[] = Class.forName(this.getClass().getName()).getDeclaredFields();
      String s = "";
      for (Field field : fields)
        s.concat(field + " ");
      return s.concat(".");
    } catch (Exception e) {
      return "";
    }
  }
}
