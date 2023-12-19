/**
 *
 * Copyright 2018 Denise Case
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
package edu.nwmissouri.isl.aasis.case.ec_role;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.ksu.cis.macr.organization.model.identifiers.StringIdentifier;
import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;

/**
 * A class listing the specified roles in the system. The string names must
 * exactly match the role names shown on the refined goal models and role
 * models. By convention, the unique identifier names should be exactly the same
 * as the name strings.
 */
public class RoleIdentifiers {
  public static final UniqueIdentifier PlayerRole = StringIdentifier.getIdentifier("PlayerRole");
  public static final UniqueIdentifier RefereeRole = StringIdentifier.getIdentifier("RefereeRole");
  public static final UniqueIdentifier Self_Control_Role = StringIdentifier.getIdentifier("Self Control Role");
  public static final UniqueIdentifier Self_Manage_Role = StringIdentifier.getIdentifier("Self Manage Role");
  public static final UniqueIdentifier Join_Organization_Role = StringIdentifier
      .getIdentifier("Join Organization Role");
  public static final UniqueIdentifier Administer_Organization_Role = StringIdentifier
      .getIdentifier("Administer Organization Role");

  @Override
  public String toString() {
    try {
      final String s = "";
      final Field fields[] = Class.forName(this.getClass().getName()).getDeclaredFields();
      for (final Field field : fields)
        s.concat(field + " ");
      return s.concat(".");
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(RoleIdentifiers.class.getName()).log(Level.SEVERE, null, ex);
      return "";
    }
  }
}
