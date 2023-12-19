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
package edu.nwmissouri.isl.aasis.case.ec_cap;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.ksu.cis.macr.goal.model.InstanceParameters;
import edu.ksu.cis.macr.obaa_pp.ec.base.IExecutionComponent;
import edu.ksu.cis.macr.obaa_pp.ec_cap.AbstractOrganizationCapability;
import edu.ksu.cis.macr.obaa_pp.objects.IDisplayInformation;
import edu.ksu.cis.macr.obaa_pp.org.IExecutableOrganization;
import edu.ksu.cis.macr.organization.model.InstanceGoal;
import edu.ksu.cis.macr.organization.model.identifiers.StringIdentifier;
import edu.nwmissouri.isl.aasis.case.guidelines.IInitialGuidelines;

/**
 * The {@code SetupGameCapability} provides the ability to act autonomously.
 * To startup, access central control systems, get authorizations and new
 * capabilities, and initiate communications with other agents.
 */
public class SetupGameCapability extends AbstractOrganizationCapability {
  private static final boolean debug = true;
  private static final Logger LOG = LoggerFactory.getLogger(SetupGameCapability.class);
  private IInitialGuidelines initialGuidelines = null;
  private int playerInt;

  /**
   * Construct a new instance.
   *
   * @param owner
   *            - the agent possessing this capability.
   * @param org
   *            - the immediate organization in which this agent operates.
   */
  public SetupGameCapability(final IExecutionComponent owner, final IExecutableOrganization org) {
    super(SetupGameCapability.class, owner, org);
    this.owner = owner;
  }

  public synchronized void callForConfiguration() {
    // TODO Add ability "phone home" on startup
  }

  public synchronized int[][] generateDefaultBoard() {
    if (debug)
      LOG.debug("Entering generateDefaultBoard");
    int[][] board = new int[3][3];
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        board[i][j] = -1;
      }
    }
    return board;
  }

  @Override
  public synchronized double getFailure() {
    return 0;
  }

  /**
   * Get all parameters from this instance goal and use them to initialize the
   * capability.
   *
   * @param instanceGoal
   *            - this instance of the specification goal
   */
  public synchronized void init(InstanceGoal<?> instanceGoal) {
    if (debug)
    LOG.debug("Entering init()");
  // Get the parameter values from the existing active instance goal
  final InstanceParameters params = Objects.requireNonNull((InstanceParameters) instanceGoal.getParameter());
  if (debug)
    LOG.debug("params={}", params);
  final IInitialGuidelines g = Objects
      .requireNonNull((IInitialGuidelines) params.getValue(StringIdentifier.getIdentifier("initialGuidelines")));

  if (debug)
    LOG.debug("initialGuidelines={}", g);

  // Set the goal parameter guidelines
  this.setInitialGuidelines(g);
  this.setPlayerInt(g.getPlayerInt());
  }

  public IInitialGuidelines getInitialGuidelines() {
    return this.initialGuidelines;
  }

  private void setInitialGuidelines(IInitialGuidelines g) {
    this.initialGuidelines = g;
  }

  public int getPlayerInt() {
    return this.playerInt;
  }

  public void setPlayerInt(final int i) {
    this.playerInt = i;
  }

  @Override
  public synchronized void populateCapabilitiesOfDisplayObject(final IDisplayInformation displayInformation) {
    super.populateCapabilitiesOfDisplayObject(displayInformation);
  }

  @Override
  public synchronized void reset() {
  }

  @Override
  public synchronized Element toElement(final Document document) {
    final Element capability = super.toElement(document);
    return capability;
  }
}
