/**
 *
 * Copyright 2012-2018 Rui Zhuang and Denise Case
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
package edu.nwmissouri.isl.aasis.case.agent;

import edu.ksu.cis.macr.goal.model.SpecificationEvent;
import edu.ksu.cis.macr.obaa_pp.agent.AbstractAgent;
import edu.ksu.cis.macr.obaa_pp.agent.IAbstractAgent;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IExecutablePlan;
import edu.ksu.cis.macr.obaa_pp.ec.staticorg.IStaticOrganization;
import edu.ksu.cis.macr.obaa_pp.ec_cap.IInternalCommunicationCapability;
import edu.ksu.cis.macr.obaa_pp.ec_ps.IPlanSelector;
import edu.ksu.cis.macr.obaa_pp.ec_task.ITask;
import edu.ksu.cis.macr.obaa_pp.ec_task.Task;
import edu.ksu.cis.macr.obaa_pp.ec_task.TaskManager;
import edu.ksu.cis.macr.obaa_pp.events.IEventManager;
import edu.ksu.cis.macr.obaa_pp.events.IOrganizationEvent;
import edu.ksu.cis.macr.obaa_pp.events.OrganizationEvent;
import edu.ksu.cis.macr.obaa_pp.events.OrganizationEventType;
import edu.ksu.cis.macr.obaa_pp.org.IExecutableOrganization;
import edu.ksu.cis.macr.organization.model.Assignment;
import edu.ksu.cis.macr.organization.model.Capability;
import edu.ksu.cis.macr.organization.model.InstanceGoal;
import edu.ksu.cis.macr.organization.model.RoleGoodnessFunction;
import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;
import edu.nwmissouri.isl.aasis.case.ec_ps.PlanSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Rui Zhuang and Denise Case
 *
 */
public class BaseAgent extends AbstractAgent {
  protected static final UniqueIdentifier ACHIEVED_EVENT = SpecificationEvent.ACHIEVED_EVENT.getIdentifier();
  private static final Logger LOG = LoggerFactory.getLogger(BaseAgent.class);
  private static final Boolean debug = false;
  protected String identifierString;
  protected IPlanSelector planSelector = new PlanSelector();

  /**
   * Constructs a new instance of an agent using the organization-based agent architecture. Each agent will have
   * the capabilities needed to cc in a peer-based organization both as a peer and as a supervisor and contains its own
   * knowledge about the immediate organization in which it participates.
   *
   * @param identifierString a string containing a name that uniquely
   */
  public BaseAgent(final String identifierString) {
    super(identifierString);
    LOG.info("\t..................CONSTRUCTING BASE AGENT {}.", identifierString);
    this.identifierString = identifierString;

    this.taskManager = new TaskManager(this.identifierString);
    if (debug)
      LOG.debug("\t New TaskManager.");
    this.internalCommunicationCapability = getCapability(IInternalCommunicationCapability.class);
    addCapability(internalCommunicationCapability);
    if (debug)
      LOG.debug("\t Added internalCommunicationCapability.");

    LOG.info("\t..................EXITING BASE AGENT(identifier={})", identifierString);
  }

  /**
   * Constructs a new instance of an agent using the organization-based agent architecture.
   *
   * @param org              the organization, containing information about agents and objects in the organization system.
   * @param identifierString a string containing a name that uniquely identifies this in the system.
   * @param knowledge        an XML specification of the organization.
    */
  public BaseAgent(final IExecutableOrganization org, final String identifierString, final Element knowledge) {
    super(org, identifierString, knowledge);
    LOG.info("\t..................CONSTRUCTING BaseAgent(org={}, identifier={}, knowledge={}", org, identifierString,
        knowledge);
    this.identifierString = identifierString;
    organization = org;
    this.taskManager = new TaskManager(this.identifierString);
    if (debug)
      LOG.debug("\t New TaskManager.");

    this.internalCommunicationCapability = getCapability(IInternalCommunicationCapability.class);
    addCapability(internalCommunicationCapability);
    if (debug)
      LOG.debug("\t Added internalCommunicationCapability.");
    LOG.info("	..................EXITING BASE AGENT()...........................");
  }

  /**
   * @return the identifierString
   */
  @Override
  public String getIdentifierString() {
    return identifierString;
  }

  /**
   * @param identifierString the identifierString to set
   */
  public void setIdentifierString(String identifierString) {
    this.identifierString = identifierString;
  }

  public IInternalCommunicationCapability getInternalCommunicationCapability() {
    return internalCommunicationCapability;
  }

  @Override
  public String toString() {
    return "BaseAgent [identifierString=" + this.identifierString + "]";
  }

  @Override
  public void execute() {
    if (debug)
      LOG.debug("Executing plan for {}. {} assignments.", this.getIdentifierString(),
          this.taskManager.getAssignments().size());
    while (isAlive()) {

      /* first: update the ec */
      try {
        while (this.taskManager.getAssignments().size() > 0) {
          if (debug)
            LOG.debug("Number of Assignments = {}", this.taskManager.getAssignments().size());
          final Assignment assignment = this.taskManager.pollAssignment();
          if (debug)
            LOG.debug("next assignment = {}. Adding to assigned task queue.", assignment);
          this.taskManager.addAssignedTask(new Task(assignment));
        }
      } catch (Exception ex) {
        LOG.error("ERROR in EC EXECUTE Assignment processing {}. Illegal arg execption: {}  {}{}",
            this.getIdentifierString(), ex.getMessage(), Arrays.toString(ex.getStackTrace()));
        System.exit(-14);
      }

      try {
        /* second: remove the deassignments */
        while (this.taskManager.getDeAssignments().size() > 0) {
          if (debug)
            LOG.debug("Number of DeAssignments = {}", this.taskManager.getDeAssignments().size());
          this.taskManager.removeAssignments(this.taskManager.pollDeAssignment());
        }
      } catch (Exception ex) {
        LOG.error("ERROR in EC EXECUTE deassignment processing {}. Illegal arg execption: {}  {}{}",
            this.getIdentifierString(), ex.getMessage(), Arrays.toString(ex.getStackTrace()));
        System.exit(-15);
      }
      ITask assignedTask = null;
      try {
        /* third: select & execute the highest priority task from queues */
        if (debug)
          LOG.debug("ASSIGNED TASK: Getting next assigned task.");
        assignedTask = this.taskManager.getNextAssignedTask();
        if (debug)
          LOG.debug("Assigned task is {}.", assignedTask);
      } catch (Exception ex) {
        LOG.error("ERROR in EC EXECUTE getNextAssignedTask {}.Execption: {}  {}{}", this.getIdentifierString(),
            ex.getMessage(), Arrays.toString(ex.getStackTrace()));
        System.exit(-16);
      }
      try {
        if (assignedTask == null) {
          // if (debug)LOG.debug("ASSIGNED TASK IS NULL: No assignment, calling endTurn() and executing CC plan()");
          endTurn();
        } else if (assignedTask != null) {
          LOG.debug("Executing assigned task: {}", assignedTask);
          this.executeTask(assignedTask);
        }
      } catch (Exception ex) {
        LOG.error("ERROR EXECUTING ASSIGNED TASK id={}, goal={}, role={}, plan={}, class={}, cause={}, trace={}",
            this.getIdentifierString(), assignedTask.getAssignment().getInstanceGoal(),
            assignedTask.getAssignment().getRole(), assignedTask.getPlan(), ex.getClass(), ex.getCause(),
            ex.getStackTrace());
        System.exit(-17);
      }
    }
    if (debug)
      LOG.debug("exiting EC execute()..............................");
  }

  @Override
  public IStaticOrganization getBaseOrganization() {
    return this.organization;
  }

  /**
   * Execute the given task.
   *
   * @param task - the task to be executed.
   */
  @Override
  public void executeTask(ITask task) {
    if (debug)
      LOG.debug("Entering executeTask() {}",
          String.format("%s plays %s to achieve %s", task.getAssignment().getAgent().getIdentifier(),
              task.getAssignment().getRole().getIdentifier(), task.getAssignment().getInstanceGoal().getIdentifier()));

    final edu.ksu.cis.macr.organization.model.Agent<?> agent = task.getAssignment().getAgent();
    final InstanceGoal<?> goal = task.getAssignment().getInstanceGoal();
    final double goodnessScore = task.getAssignment().getRole().goodness(agent, goal, null);
    if (goodnessScore > RoleGoodnessFunction.MIN_SCORE) {
      if (debug)
        LOG.debug("executeTask() Inside real execution");
      IExecutablePlan executablePlan = task.getPlan();
      if (executablePlan == null) {
        // iExecutablePlan = this.taskManager.getTaskPlan(task);

        UniqueIdentifier roleIdentifier = Objects.requireNonNull(task.getAssignment().getRole().getIdentifier());
        if (debug)
          LOG.debug("Task role is {}.", roleIdentifier);
        UniqueIdentifier goalIdentifier = Objects
            .requireNonNull(task.getAssignment().getInstanceGoal().getSpecificationIdentifier());
        if (debug)
          LOG.debug("Task goal is {}.", goalIdentifier.toString());

        try {
          executablePlan = planSelector.getPlan(roleIdentifier, goalIdentifier);
          LOG.debug("Selected plan for {} to achieve {} is {}.", roleIdentifier, goalIdentifier.toString(),
              executablePlan.toString());
        } catch (Exception e) {
          LOG.error("Error getting plan from AgentPlanSelector.getPlan when role={} and goal={}",
              roleIdentifier.toString(), goalIdentifier.toString());
          System.exit(-44);
        }
        if (executablePlan == null) {
          LOG.error("Error: Plan is still null. Please create a plan for goal={}, role={}, task={}.",
              task.getAssignment().getInstanceGoal(), task.getAssignment().getRole(), task.toString());
          System.exit(-797);
        }
        try {
          task.setExecutionPlan(executablePlan);
        } catch (Exception e) {
          LOG.error("Error setting execution plan.");
          System.exit(-4);
        }
      }

      do {
        try {
          executablePlan.execute(this, task.getAssignment().getInstanceGoal());
        } catch (Exception ex) {
          LOG.error("ERROR EXECUTING ASSIGNED TASK id={}, goal={}, role={}, plan={}, class={}, cause={}, trace={}",
              this.getIdentifierString(), task.getAssignment().getInstanceGoal(), task.getAssignment().getRole(),
              task.getPlan(), ex.getClass(), ex.getCause(), ex.getStackTrace());
          System.exit(-27);
        }

        endTurn();
      } while (!executablePlan.isPreemptible(this));

      if (executablePlan.isDone()) {
        doAssignmentTaskCompleted((IAbstractAgent) this, task);
      } else {
        this.taskManager.addAssignedTask(task);
      }
    } else {
      doTaskFailed(task);
    }
    if (debug)
      LOG.debug("Exiting executeTask().");
  }

  /**
   * Removes the assignment task from the task queue.
   *
   * @param agent        - the agent assigned to the task
   * @param assignedTask -
   */
  public void doAssignmentTaskCompleted(final IAbstractAgent agent, final ITask assignedTask) {
    Objects.requireNonNull(agent, "IExecutionComponent cannot be null");
    Objects.requireNonNull(assignedTask, "assignmentTask cannot be null");

    this.taskManager.getTaskQueue().remove(assignedTask);
    final IOrganizationEvent organizationEvent = new OrganizationEvent(OrganizationEventType.EVENT, ACHIEVED_EVENT,
        assignedTask.getAssignment().getInstanceGoal(), null);
    List<IOrganizationEvent> organizationEvents = new ArrayList<IOrganizationEvent>();
    organizationEvents.add(organizationEvent);
    informControlComponent(organizationEvents);
  }

  /**
   * Execute the behaviors required after failing a task.
   *
   * @param task - the assignment (Agent-Role-Goal) along with status information.
   */
  public void doTaskFailed(final ITask task) {
    if (debug)
      LOG.debug("Entering assignmentTaskFailed() {}", String.format("Task \"%s\" Failed", task));
    this.taskManager.getTaskQueue().remove(task);
    final IOrganizationEvent organizationEvent = new OrganizationEvent(OrganizationEventType.TASK_FAILURE_EVENT, null,
        task.getAssignment().getInstanceGoal(), null);
    final List<IOrganizationEvent> organizationEvents = new ArrayList<>();
    organizationEvents.add(organizationEvent);
    informControlComponent(organizationEvents);
  }

  @Override
  public IExecutableOrganization getOrganization() {
    return this.organization;
  }

  /**
   * @return a string containing a more detailed summary of this agent.
   */
  public String toVerboseString() {
    try {
      String s = "";
      s.concat(this.identifierString + " with ");
      for (final Capability cap : this.getCapabilities()) {
        s.concat(cap + " ");
      }
      return s;
    } catch (final Exception e) {
      return "";
    }
  }

  @Override
  public void addAssignment(Assignment assignment) {
    this.taskManager.addAssignment(assignment);
  }

  @Override
  public void addDeAssignment(Assignment assignment) {
    this.taskManager.addDeAssignment(assignment);
  }

  @Override
  public IEventManager getOrganizationEvents() {
    return (IEventManager) this.getEventManager();
  }

  @Override
  public IEventManager getEventManager() {
    return this.eventManager;
  }

}