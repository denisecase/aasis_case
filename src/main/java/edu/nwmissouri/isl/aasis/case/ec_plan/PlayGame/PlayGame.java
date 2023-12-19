/**
 *
 * Copyright 2012-2016 Denise Case Northwest Missouri State University
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
package edu.nwmissouri.isl.aasis.case.ec_plan.PlayGame;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ksu.cis.macr.obaa_pp.ec.base.IExecutor;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IExecutablePlan;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IPlanState;
import edu.ksu.cis.macr.organization.model.InstanceGoal;
import edu.nwmissouri.isl.aasis.case.ec_cap.DateTimeCapability;
import edu.nwmissouri.isl.aasis.case.ec_cap.PlayGameCapability;
import edu.nwmissouri.isl.aasis.case.ec_cap.RefereeGameCapability;

/**
 * The {@code PlayGame} state is the main step in the
 * {@code PlayGame_Plan}. 
 */
public enum PlayGame implements IPlanState<PlayGame_Plan> {
  INSTANCE;

  private static final boolean debug = true;

  private static final Logger LOG = LoggerFactory.getLogger(PlayGame.class);

  @Override
  public synchronized void Enter(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
    // Nothing
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * edu.ksu.cis.macr.obaa_pp.ec.plans.IPlanState#Execute(edu.ksu.cis.macr.
   * obaa_pp.ec.plans.IExecutablePlan,
   * edu.ksu.cis.macr.obaa_pp.ec.base.IExecutor,
   * edu.ksu.cis.macr.organization.model.InstanceGoal)
   */
  @Override
  public synchronized void Execute(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
    if (debug)
      LOG.debug("Starting with instance goal: {}.", ig);

    Objects.requireNonNull(ec);
    Objects.requireNonNull(ig);
    Objects.requireNonNull(ec.getCapability(DateTimeCapability.class), "Role requires DateTimeCapability.");
    Objects.requireNonNull(ec.getCapability(PlayGameCapability.class), "play the game, son.");

    plan.heartBeat(this.getClass().getName());

    String myPersona = ec.getUniqueIdentifier().toString();
    if (debug)
      LOG.debug("This persona is {}", myPersona);

    long currentTimeSlice = Objects.requireNonNull(
        ec.getCapability(DateTimeCapability.class).getTimeSlicesElapsedSinceStart(),
        "ERROR: Need a timeSlice to get sensor data.");

    if (debug)
      LOG.debug("Current timeslice is {}", currentTimeSlice);

    ec.getCapability(PlayGameCapability.class).makeMove(RefereeGameCapability.peek());

  }

  @Override
  public synchronized void Exit(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
    // Nothing
  }
}
