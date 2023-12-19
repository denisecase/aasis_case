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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.ksu.cis.macr.goal.model.InstanceParameters;
import edu.ksu.cis.macr.obaa_pp.ec.base.IExecutionComponent;
import edu.ksu.cis.macr.obaa_pp.ec_cap.AbstractOrganizationCapability;
import edu.ksu.cis.macr.obaa_pp.events.IOrganizationEvent;
import edu.ksu.cis.macr.obaa_pp.events.OrganizationEvent;
import edu.ksu.cis.macr.obaa_pp.events.OrganizationEventType;
import edu.ksu.cis.macr.obaa_pp.objects.IDisplayInformation;
import edu.ksu.cis.macr.obaa_pp.org.IExecutableOrganization;
import edu.ksu.cis.macr.organization.model.InstanceGoal;
import edu.ksu.cis.macr.organization.model.identifiers.StringIdentifier;
import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;
import edu.nwmissouri.isl.aasis.case.goals.GoalEvents;
import edu.nwmissouri.isl.aasis.case.guidelines.IInitialGuidelines;

/**
 * The {@code RefereeGameCapability} provides the ability to run a local game organization.
 */
public class RefereeGameCapability extends AbstractOrganizationCapability {
  private static final boolean debug = true;
  private static int[][] board;
  public Boolean gameFinished = false;
  public Boolean gameStarted = false;
  public Boolean playersReady = false;
  private IInitialGuidelines initialGuidelines = null;
  private int playerInt;
  private static final Logger LOG = LoggerFactory.getLogger(RefereeGameCapability.class);
  private static int numPlayers = 0;
  public static final ArrayList<int[]> PLAYER_MOVE_QUEUE = new ArrayList<int[]>();
  public static boolean player1Move = true;

  /**
   * Construct a new instance.
   *
   * @param owner
   *            - the agent possessing this capability.
   * @param org
   *            - the immediate organization in which this agent operates.
   */
  public RefereeGameCapability(final IExecutionComponent owner, final IExecutableOrganization org) {
    super(RefereeGameCapability.class, owner, org);
    this.owner = owner;
  }

  public static synchronized int[][] peek() {
    int[][] boardCopy = new int[board.length][];
    System.arraycopy(board, 0, boardCopy, 0, boardCopy.length);
    return boardCopy;
  }

  public synchronized void callForConfiguration() {
    // TODO Add ability "phone home" on startup

  }

  private synchronized int diagonalWin() {
    int won = -1;
    if (board[0][0] != -1) {
      int start = board[0][0];
      boolean matching = true;
      for (int i = 0; i < board.length; i++)
        if (board[i][i] != start) {
          matching = false;
          break;
        }

      if (matching)
        won = start;
    }
    if (won == -1 && board[2][2] != -1) {
      int start = board[2][2];
      boolean matching = true;
      for (int i = board.length - 1; i >= 0; i--)
        if (board[i][i] != start) {
          matching = false;
          break;
        }

      if (matching)
        won = start;
    }
    return won;
  }

  private synchronized int gameWon() {
    if (horizontalWin() != -1)
      return horizontalWin();
    else if (verticalWin() != -1)
      return verticalWin();
    else if (diagonalWin() != -1)
      return diagonalWin();
    else
      return -1;
  }

  @Override
  public synchronized double getFailure() {
    return 0;
  }

  private synchronized int horizontalWin() {
    int won = -1;
    for (int[] row : board) {
      if (row[0] == -1)
        continue;

      int start = row[0];
      boolean matching = true;
      for (int cell : row)
        if (cell != start) {
          matching = false;
          break;
        }
      if (matching) {
        won = start;
        break;
      }
    }
    return won;
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
    if (debug)
      LOG.debug("setInitialGuidelines={}", g);
    this.initialGuidelines = g;
  }

  public int getPlayerInt() {
    return this.playerInt;
  }

  public void setPlayerInt(final int i) {
    if (debug)
      LOG.debug("setPlayerInt={}", i);
    this.playerInt = i;
  }

  @Override
  public synchronized void populateCapabilitiesOfDisplayObject(final IDisplayInformation displayInformation) {
    super.populateCapabilitiesOfDisplayObject(displayInformation);
  }

  public synchronized void processMove() {
    if (debug)
      LOG.debug("Entering processMove()");
    if (gameFinished || PLAYER_MOVE_QUEUE.size() == 0)
      return;

    int[] move = PLAYER_MOVE_QUEUE.get(PLAYER_MOVE_QUEUE.size() - 1);
    if (validateTurn(move)) {
      board[move[1]][move[2]] = move[0];
      if (gameWon() != -1)
        LOG.debug("Winner is player {}", gameWon());
    } else
      PLAYER_MOVE_QUEUE.remove(PLAYER_MOVE_QUEUE.size() - 1);
    updateGameFinishedState();
    if (!gameFinished)
      player1Move = !player1Move;
  }

  @Override
  public synchronized void reset() {
  }

  public synchronized void setBoard(int[][] board) {
    if (debug)
      LOG.debug("Entering setBoard. board ={}", board.toString());
    if (board != null)
      RefereeGameCapability.board = board;
    if (debug)
      LOG.debug("Setting local board ={}", board.toString());
  }

  public synchronized void showBoard() {
    LOG.info("=====");
    for (int row[] : board) {
      String s = "";
      for (int cell : row) {
        if (cell == -1) {
          s += "  ";
        } else {
          s += Integer.toString(cell) + " ";
        }
      }
      LOG.info(s);
    }
    LOG.info("=====");
    gameStarted = true;
  }

  @Override
  public synchronized Element toElement(final Document document) {
    final Element capability = super.toElement(document);
    return capability;
  }

  /**
   * Trigger the associated goal.
   *
   * @param ig
   *            - the instance goal that is triggering the new goal.
   */
  public synchronized void triggerGoals(final InstanceGoal<?> ig) {
    LOG.info("STARTING triggerGoals with ig={}", ig);
    while (!playersReady) {
      setupAndTriggerGoal(ig, numPlayers);
      numPlayers++;
      LOG.debug("numPlayers={}", numPlayers);
      if (numPlayers == 2) {
        playersReady = true;
        LOG.debug("playersReady={}", playersReady);
      }
    }

  }

  private synchronized void setupAndTriggerGoal(final InstanceGoal<?> ig, int numPlayers) {
    LOG.info("STARTING setupAndTriggerGoal with ig={} and numPlayers={}", ig, numPlayers);
    InstanceParameters params = Objects.requireNonNull((InstanceParameters) ig.getParameter());
    HashMap<UniqueIdentifier, Object> map = new HashMap<>();
    //map.put(StringIdentifier.getIdentifier("messageQueue"), PLAYER_MOVE_QUEUE);
    map.put(StringIdentifier.getIdentifier("playerInt"), numPlayers);
    final InstanceParameters newParams = new InstanceParameters(map);

    // add the event to an organization events list
    List<IOrganizationEvent> organizationEvents = new ArrayList<>();

    // create an organization event
    IOrganizationEvent newEvent = new OrganizationEvent(OrganizationEventType.EVENT, GoalEvents.Referees, ig,
        newParams);
    LOG.info("Created new organization GOAL_MODEL_EVENT (to find): {}", newEvent.toString());
    organizationEvents.add(newEvent);

    // add the event list to the control component's event list
    owner.getOrganizationEvents().addEventListToQueue(organizationEvents);
  }

  private synchronized void updateGameFinishedState() {
    if (gameWon() != -1)
      gameFinished = true;
    else {
      boolean finished = true;
      for (int[] row : board)
        for (int cell : row)
          if (cell == -1)
            finished = false;
      gameFinished = finished;
    }
  }

  public synchronized boolean validateTurn(int[] moveData) {
    int playerNum = moveData[0];
    int row = moveData[1];
    int col = moveData[2];
    if (player1Move && playerNum == 1 || !player1Move && playerNum == 0)
      return false;

    if (row >= board.length || col >= board[0].length)
      return false;

    if (board[row][col] == -1)
      return true;

    return false;
  }

  private synchronized int verticalWin() {
    int won = -1;
    for (int j = 0; j < board.length; j++) {
      if (board[0][j] == -1)
        continue;

      int start = board[0][j];
      boolean matching = true;
      for (int i = 0; i < board.length; i++)
        if (board[i][j] != start) {
          matching = false;
          break;
        }
      if (matching) {
        won = start;
        break;
      }
    }
    return won;
  }
}
