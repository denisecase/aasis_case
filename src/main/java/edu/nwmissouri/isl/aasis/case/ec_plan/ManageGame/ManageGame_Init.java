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

import edu.ksu.cis.macr.obaa_pp.ec.base.IExecutor;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IExecutablePlan;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IPlanState;
import edu.ksu.cis.macr.organization.model.InstanceGoal;
import edu.nwmissouri.isl.aasis.case.config.RunManager;
import edu.nwmissouri.isl.aasis.case.ec_cap.RefereeGameCapability;
import edu.nwmissouri.isl.aasis.case.ec_cap.SetupGameCapability;
import edu.ksu.cis.macr.goal.model.InstanceParameters;
import edu.ksu.cis.macr.organization.model.identifiers.StringIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * This is the first step in the plan. It includes initialization tasks
 * for social agents such as reading goal guidelines, calling centralized control centers for authentication, authorization, software updates, etc.
 * It also includes establishing access to the needed communications exchanges, and triggering appropriate
 * participation goals based on the agent guidelines provided.
 */
public enum ManageGame_Init implements IPlanState<ManageGame_Plan> {
  INSTANCE;
  private static final Logger LOG = LoggerFactory.getLogger(ManageGame_Init.class);
  private static final boolean debug = true;

  @Override
  public synchronized void Enter(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
    // Nothing
  }

  @Override
  public synchronized void Execute(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
    if (debug)
      LOG.debug("Entering Execute(plan={},ec={},ig={}).", plan, ec, ig);
    Objects.requireNonNull(ec);
    Objects.requireNonNull(ig);
    Objects.requireNonNull(ec.getCapability(RefereeGameCapability.class), "Role requires RefereeGameCapability.");
    Objects.requireNonNull(ec.getCapability(SetupGameCapability.class), "Role requires SetupGameCapability.");

    plan.heartBeat(this.getClass().getName());
    plan.setPreemptible(true);

    ec.getCapability(RefereeGameCapability.class).init(ig);
    ec.getCapability(SetupGameCapability.class).init(ig);

    String myPersona = ec.getUniqueIdentifier().toString();
    if (debug)
      LOG.debug("My name is {}.", myPersona);

    // trigger participation goals (if appropriate) - these need to be consistent with the goal model
    ec.getCapability(RefereeGameCapability.class).triggerGoals(ig);

    if (debug)
      LOG.debug("playersReady={}", ec.getCapability(RefereeGameCapability.class).playersReady);

    if (!ec.getCapability(RefereeGameCapability.class).gameStarted) {
      if (debug)
        LOG.debug("Game not started yet. gameStarted={}", ec.getCapability(RefereeGameCapability.class).gameStarted);

      ec.getCapability(RefereeGameCapability.class)
          .setBoard(ec.getCapability(SetupGameCapability.class).generateDefaultBoard());

      LOG.info("Started game.");
      ec.getCapability(RefereeGameCapability.class).showBoard();

      if (debug)
        LOG.debug("Changing state.");
      plan.getStateMachine().changeState(ManageGame.INSTANCE, ec, ig);

    }

    if ((edu.nwmissouri.isl.aasis.case.config.RunManager.isStopped())) {
      if (debug)
        LOG.debug("Changing state.");
      plan.getStateMachine().changeState(ManageGame_Stop.INSTANCE, ec, ig);
    }
  }

  @Override
  public synchronized void Exit(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
    // Nothing
  }
}
