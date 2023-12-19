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

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

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

/**
 * The {@code SelfControlCapability} provides the ability to act autonomously.
 * To startup, access central control systems, get authorizations and new
 * capabilities, and initiate communications with other agents.
 */
public class PlayGameCapability extends AbstractOrganizationCapability {
  private static final Logger LOG = LoggerFactory.getLogger(PlayGameCapability.class);
  private static final ArrayList<int[]> PLAYER_MOVE_QUEUE = RefereeGameCapability.PLAYER_MOVE_QUEUE;
  private int playerInt = -1;
  private String gname = "";

  /**
   * Construct a new instance.
   *
   * @param owner
   *            - the agent possessing this capability.
   * @param org
   *            - the immediate organization in which this agent operates.
   */
  public PlayGameCapability(final IExecutionComponent owner, final IExecutableOrganization org) {
    super(PlayGameCapability.class, owner, org);
    this.owner = owner;
    this.playerInt = -1;
  }

  /**
   * Construct a new instance.
   *
   * @param owner
   *            - the agent possessing this capability.
   * @param org
   *            - the immediate organization in which this agent operates.
  * @param playerInt
   *            - the digit the player uses.
   */
  public PlayGameCapability(final IExecutionComponent owner, final IExecutableOrganization org, final int playerInt) {
    super(PlayGameCapability.class, owner, org);
    this.owner = owner;
    this.playerInt = playerInt;
  }

  public synchronized void callForConfiguration() {
    // TODO Add ability "phone home" on startup
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
    this.gname = instanceGoal.getInstanceIdentifier().toString();
    LOG.debug("Goal name = {}", gname);
    // Get the parameter values from the existing active instance goal
    final InstanceParameters params = Objects.requireNonNull((InstanceParameters) instanceGoal.getParameter());
    System.out.println(params.getParameters().get(StringIdentifier.getIdentifier("messageQueue")));
  }

  public void makeMove(int[][] board) {
    boolean player1 = getOwner().getUniqueIdentifier().toString().equals("Player 1");
    if (player1 ^ RefereeGameCapability.player1Move){return;}
    ArrayList<int[]> openMoves = new ArrayList<int[]>();
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        if (board[i][j] == -1) {
          openMoves.add(new int[] { i, j });
        }
      }
    }
    if (openMoves.size() == 0) {
      LOG.info("There are no more open spaces. Game Over.");
      System.exit(0);
    }
    Random r = new Random();
    int[] move = openMoves.get(r.nextInt(openMoves.size()));
    PLAYER_MOVE_QUEUE.add(new int[] { player1 ? 0 : 1, move[0], move[1] });
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
