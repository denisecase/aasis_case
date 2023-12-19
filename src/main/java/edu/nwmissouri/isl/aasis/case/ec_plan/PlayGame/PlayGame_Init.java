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
import edu.nwmissouri.isl.aasis.case.config.RunManager;
import edu.nwmissouri.isl.aasis.case.ec_cap.DateTimeCapability;
import edu.nwmissouri.isl.aasis.case.ec_cap.PlayGameCapability;

/**
 * This is the first step in the plan. It includes initialization tasks for
 * social agents such as reading goal guidelines, calling centralized control
 * centers for authentication, authorization, software updates, etc. It also
 * includes establishing access to the needed communications exchanges, and
 * triggering appropriate participation goals based on the agent guidelines
 * provided.
 */
public enum PlayGame_Init implements IPlanState<PlayGame_Plan> {
  INSTANCE;

  private static final boolean debug = true;
  private static final Logger LOG = LoggerFactory.getLogger(PlayGame_Init.class);

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
    Objects.requireNonNull(ec.getCapability(PlayGameCapability.class), "Role requires PlayGameCapability.");

    plan.heartBeat(this.getClass().getName());

    ec.getCapability(PlayGameCapability.class).init(ig);
    ec.getCapability(PlayGameCapability.class).callForConfiguration();

    String myPersona = ec.getUniqueIdentifier().toString();
    if (debug)
      LOG.debug("My name is {}.", myPersona);


    if (debug)
      LOG.debug("Changing state.");
    plan.getStateMachine().changeState(PlayGame.INSTANCE, ec, ig);

    if (RunManager.isStopped()) {
      if (debug)
        LOG.debug("Changing state.");
      plan.getStateMachine().changeState(PlayGame_Stop.INSTANCE, ec, ig);
    }
  }

  @Override
  public synchronized void Exit(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
    // Nothing
  }
}
