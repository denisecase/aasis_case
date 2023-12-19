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
package edu.nwmissouri.isl.aasis.case.ec_plan.ManageGame;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ksu.cis.macr.obaa_pp.ec.base.IExecutor;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IExecutablePlan;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IPlanState;
import edu.ksu.cis.macr.organization.model.InstanceGoal;
import edu.nwmissouri.isl.aasis.case.ec_cap.DateTimeCapability;
import edu.nwmissouri.isl.aasis.case.ec_cap.RefereeGameCapability;
import edu.nwmissouri.isl.aasis.case.ec_cap.SetupGameCapability;

/**
 * The {@code Self_Control} state is the main step in the
 * {@code Self_Control_Plan}. It will monitor connections and attempt to restart
 * any that have been dropped. It retrieves messages from sensor sub agents and
 * forwards them to sub agents participating in external organizations,
 * reviewing and biasing content before sending to reflect the multiple
 * objectives and biases of this agent.
 */
public enum ManageGame implements IPlanState<ManageGame_Plan> {
  INSTANCE;

  private static final boolean debug = true;

  private static final Logger LOG = LoggerFactory.getLogger(ManageGame.class);

  @Override
  public synchronized void Enter(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
    // Nothing
  }

  @Override
  public synchronized void Execute(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
    if (debug)
      LOG.debug("Starting with instance goal: {}.", ig);
    Objects.requireNonNull(ec);
    Objects.requireNonNull(ig);
    Objects.requireNonNull(ec.getCapability(DateTimeCapability.class), "Role requires DateTimeCapability.");
    Objects.requireNonNull(ec.getCapability(RefereeGameCapability.class), "Role requires RefereeGameCapability.");
    Objects.requireNonNull(ec.getCapability(SetupGameCapability.class), "Role requires SetupGameCapability.");

    plan.heartBeat(this.getClass().getName());
    plan.setPreemptible(true);

    // initialize guidelines from instance goal
    ec.getCapability(RefereeGameCapability.class).init(ig);
    if (debug)
      LOG.debug("Set RefereeGameCapability guidelines from instance goal. {}", ig);

    ec.getCapability(SetupGameCapability.class).init(ig);
    if (debug)
      LOG.debug("Set SetupGameCapability guidelines from instance goal. {}", ig);

    String myPersona = ec.getUniqueIdentifier().toString();
    LOG.debug("My name is {}.", myPersona);

    // get current timeSlice
    long currentTimeSlice = Objects.requireNonNull(
        ec.getCapability(DateTimeCapability.class).getTimeSlicesElapsedSinceStart(),
        "ERROR: Need a timeSlice to get sensor data.");
    if (debug)
      LOG.debug("This currentTimeSlice is {}.", currentTimeSlice);

    if (debug)
      LOG.debug("playersReady={}", ec.getCapability(RefereeGameCapability.class).playersReady);
    if (debug)
      LOG.debug("gameStarted={}", ec.getCapability(RefereeGameCapability.class).gameStarted);
    if (debug)
      LOG.debug("gameFinished={}", ec.getCapability(RefereeGameCapability.class).gameFinished);

    if (ec.getCapability(RefereeGameCapability.class).gameFinished) {
      if (debug)
        LOG.debug("Changing state.");
      plan.getStateMachine().changeState(ManageGame_Stop.INSTANCE, ec, ig);

    }
    ec.getCapability(RefereeGameCapability.class).processMove();
    ec.getCapability(RefereeGameCapability.class).showBoard();
    if (debug)
      LOG.debug("Continued game.");

  }

  @Override
  public synchronized void Exit(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
    // Nothing
  }
}
