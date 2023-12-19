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
package edu.nwmissouri.isl.aasis.case.goals;

import java.lang.reflect.Field;

import edu.ksu.cis.macr.organization.model.identifiers.StringIdentifier;
import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;

/**
 * A singleton enum GoalIdentifiers class mapping all goals shown on the refined
 * goal models to associated {@code UniqueIdentifier}s. The string names must
 * exactly match the goal names shown on the refined goal models and role
 * models. By convention, the unique identifier names should be exactly the same
 * as the name strings.
 */
public enum GoalIdentifiers {
	INSTANCE;
	
	public static final UniqueIdentifier ManageGame = StringIdentifier.getIdentifier("ManageGame");
	public static final UniqueIdentifier PlayGame = StringIdentifier.getIdentifier("PlayGame");
  public static final UniqueIdentifier Self_Control = StringIdentifier.getIdentifier("Self Control");
  public static final UniqueIdentifier Self_Manage = StringIdentifier.getIdentifier("Self Manage");
  public static final UniqueIdentifier Join_Organization = StringIdentifier.getIdentifier("Join Organization");
  public static final UniqueIdentifier Administer_Organization = StringIdentifier.getIdentifier("Administer Organization");

	/**
	 * The top goal in all goal models.
	 */
	public static final UniqueIdentifier Succeed = StringIdentifier.getIdentifier("Succeed");

	@Override
	public String toString() {
		try {
			final Field fields[] = Class.forName(this.getClass().getName()).getDeclaredFields();
			final String s = "";
			for (final Field field : fields)
				s.concat(field + " ");
			return s.concat(".");
		} catch (ClassNotFoundException e) {
			return "";
		} catch (SecurityException e) {
			return "";
		}
	}
}
