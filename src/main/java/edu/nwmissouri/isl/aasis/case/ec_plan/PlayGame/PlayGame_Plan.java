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


import edu.ksu.cis.macr.obaa_pp.ec.base.IExecutor;
import edu.ksu.cis.macr.obaa_pp.ec.plans.AbstractExecutablePlan;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IExecutablePlan;
import edu.ksu.cis.macr.organization.model.InstanceGoal;

public class PlayGame_Plan extends AbstractExecutablePlan implements IExecutablePlan {
    public PlayGame_Plan() {
        getStateMachine().setCurrentState(PlayGame_Init.INSTANCE);
    }

    @Override
    public synchronized void execute(IExecutor ec, InstanceGoal<?> ig) {
        getStateMachine().update(ec, ig);
    }
}
