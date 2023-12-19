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
package edu.nwmissouri.isl.aasis.case.cc_m;

import edu.ksu.cis.macr.goal.model.InstanceParameters;
import edu.ksu.cis.macr.goal.model.InstanceTreeChanges;
import edu.ksu.cis.macr.obaa_pp.agent.IAbstractAgent;
import edu.ksu.cis.macr.obaa_pp.cc_m.AbstractControlComponentMaster;
import edu.ksu.cis.macr.obaa_pp.cc_m.IControlComponentMaster;
import edu.nwmissouri.isl.aasis.case.cc_reorg.ReorganizationAlgorithm;
import edu.nwmissouri.isl.aasis.case.guidelines.GuidelineManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import java.io.File;
import java.util.Objects;

/**
 * Extends the {@code AbstractBaseControlComponent} with the functionality
 * necessary to act as the master for the local organization.
 */
public class Master extends AbstractControlComponentMaster implements IControlComponentMaster {
  private static final Logger LOG = LoggerFactory.getLogger(Master.class);
  private static final Boolean debug = true;

  /**
   * @param name      - the agent / organization name
   * @param agent     - the subagent
   * @param knowledge - the XML knowledge about the organization
   */
  public Master(final String name, IAbstractAgent agent, Element knowledge) {
    super(name, agent, knowledge);
    if (debug)
      LOG.debug("Entering constructor Master(name={}, agent={}, knowledge={})", name, agent, knowledge);

    if (debug)
      LOG.debug("Calling setReorganizationAlgorithm()");
    this.setReorganizationAlgorithm(ReorganizationAlgorithm.createReorganizationAlgorithm(name));

    if (debug)
      LOG.debug("Calling initializeGuidelines(agent={})", agent);
    initializeGuidelines(agent);

    if (debug)
      LOG.debug("Calling setInitialRoles()");
    setInitialRoles();
    LOG.info("Exiting constructor: ReorganizationAlgorithm={},guidelines={},initialRoles={}",
        this.getReorganizationAlgorithm(), this.getTopGoalInstanceParameters(), this.initialRoles);
  }

  public synchronized void initializeGuidelines(IAbstractAgent agent) {
    LOG.info("Entering initializeGuidelines(agent={}, agentOrg={}).", agent, agent.getBaseOrganization());
    LOG.info("Entering initializeGuidelines(orgSpec={}).", agent.getBaseOrganization().getOrganizationSpecification());
    final String strFile = Objects.requireNonNull(
        agent.getBaseOrganization().getOrganizationSpecification().getAbsolutePathToGuidelinesFile(),
        "Control component needs guideline file.");
    if (debug)
      LOG.debug("The guideline file is {}", strFile);
    File f = new File(strFile);
    try {
      checkFile(f);
      if (debug)
        LOG.debug("The guideline file was found. {} ", strFile);
    } catch (Exception e) {
      LOG.error("Error failed checking the guideline file at {}.", strFile, e);
      System.exit(-723);
    }
    this.goalParameterValues = GuidelineManager.getGoalParameterValues(strFile);
    LOG.info("goalParameterValues={}.", goalParameterValues);
    final InstanceParameters topParams = Objects.requireNonNull(new InstanceParameters(goalParameterValues),
        "Error: top goal instance parameters are required.");
    LOG.info("topParams = {}", topParams);
    InstanceTreeChanges changeList = this.getInitialGoalModelChangeList(topParams);
    LOG.info("changeList = {}", changeList);
    updateInitialActiveGoals(changeList);
    LOG.info("Exiting initializeGuidelines(): changeList={}.", changeList);
  }

  /**
   * The {@code content} that will be channeled by extensions.
   *
   * @param content the {@code content} to be passed along the {@code ICommunicationChannel}.
   */
  @Override
  public synchronized void channelContent(final Object content) {
    internalComm.channelContent(content);
  }

}
