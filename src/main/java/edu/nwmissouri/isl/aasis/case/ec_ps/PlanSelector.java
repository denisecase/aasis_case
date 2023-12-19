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
package edu.nwmissouri.isl.aasis.case.ec_ps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ksu.cis.macr.obaa_pp.ec.plans.IExecutablePlan;
import edu.ksu.cis.macr.obaa_pp.ec_ps.IPlanSelector;
import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;
import edu.nwmissouri.isl.aasis.case.ec_plan.ManageGame.ManageGame_Plan;
import edu.nwmissouri.isl.aasis.case.ec_plan.PlayGame.PlayGame_Plan;
import edu.nwmissouri.isl.aasis.case.ec_role.RoleIdentifiers;
import edu.nwmissouri.isl.aasis.case.goals.GoalIdentifiers;


/**
 * Class that defines the logic for selecting the plan to perform a role
 * assigned to achieve a goal.
 */
public class PlanSelector implements IPlanSelector {
  private static final Boolean debug = true;
  private static final Logger LOG = LoggerFactory.getLogger(PlanSelector.class);

  public PlanSelector() {
  }

  public synchronized static IPlanSelector createPlanSelector() {
    return new PlanSelector();
  }

  /**
   * Returns the plan that will perform this role to achieve the desired goal.
   *
   * @param roleIdentifier
   *            - the assigned role
   * @param goalIdentifier
   *            - the goal to be achieved
   * @return - the execution plan to perform the role
   */
  @Override
  public synchronized IExecutablePlan getPlan(final UniqueIdentifier roleIdentifier,
      final UniqueIdentifier goalIdentifier) {
    if (debug)
      LOG.debug("checkcheck Entering getPlan(role={}, goal={}.", roleIdentifier, goalIdentifier);
    IExecutablePlan result = null;
    if (roleIdentifier.equals(edu.nwmissouri.isl.aasis.case.ec_role.RoleIdentifiers.PlayerRole) && 
    goalIdentifier.equals(edu.nwmissouri.isl.aasis.case.goals.GoalIdentifiers.PlayGame))
      result = new PlayGame_Plan();
    else if (roleIdentifier.equals(edu.nwmissouri.isl.aasis.case.ec_role.RoleIdentifiers.RefereeRole) && 
    goalIdentifier.equals(edu.nwmissouri.isl.aasis.case.goals.GoalIdentifiers.ManageGame))
      result = new ManageGame_Plan();

    if (debug)
      LOG.debug("Exiting getPlan(): result={}.", result);
    return result;
  }
}
