/*
 * Copyright 2018 Denise Case
 * 
 * See License.txt file for the license agreement. 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package edu.nwmissouri.isl.aasis.case.org.poker.capabilities.admin;


import edu.ksu.cis.macr.aasis.agent.persona.AbstractOrganizationCapability;
import edu.ksu.cis.macr.aasis.agent.persona.IOrganization;
import edu.ksu.cis.macr.aasis.agent.persona.IPersona;
import edu.ksu.cis.macr.aasis.common.IConnectionGuidelines;
import edu.ksu.cis.macr.aasis.common.IConnections;
import edu.ksu.cis.macr.aasis.messaging.IMessagingFocus;
import edu.ksu.cis.macr.goal.model.InstanceParameters;
import edu.nwmissouri.isl.aasis.case.org.poker.guidelines.deal.IPokerGuidelines;
import edu.nwmissouri.isl.aasis.case.org.poker.messages.*;
import edu.nwmissouri.isl.aasis.case.org.poker.messaging.PokerMessagingFocus;
import edu.ksu.cis.macr.organization.model.Agent;
import edu.ksu.cis.macr.organization.model.InstanceGoal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.*;

/**
 * Provides the ability to broker power auctions.
 */
public class DealPokerCapability extends AbstractOrganizationCapability {
    private static final Logger LOG = LoggerFactory.getLogger(DealPokerCapability.class);
    private static final boolean debug = false;
    private static final IMessagingFocus messagingFocus = PokerMessagingFocus.POKER;
    private IConnections tictactoeConnections;
    private IPokerGuidelines PokerGuidelines;
    private int currentIteration = 0;

    /**
     * @param owner - the entity to which this capability belongs
     * @param org   - the organization in which the entity is operating.
     */
    public DealPokerCapability(final IPersona owner, final IOrganization org) {
        super(DealPokerCapability.class, owner, org);
    }

    public synchronized static String getMapAuctionString(TreeMap<String, IPokerMessageContent> treeMap) {
        StringBuilder b = new StringBuilder("\n");
        for (Map.Entry<String, IPokerMessageContent> entry : treeMap.entrySet()) {
            b.append(entry.getKey() + ": ");
            b.append(entry.getValue().toString() + "\n");
        }
        return b.toString();
    }

    public synchronized int getCurrentIteration() {
        return currentIteration;
    }

    public synchronized void setCurrentIteration(int currentIteration) {
        this.currentIteration = currentIteration;
    }

    public synchronized IPokerGuidelines getPokerGuidelines() {
        return PokerGuidelines;
    }

    public synchronized void setPokerGuidelines(IPokerGuidelines PokerGuidelines) {
        this.PokerGuidelines = PokerGuidelines;
    }

    public synchronized IConnections getChildConnections() {
        return this.tictactoeConnections;
    }

    public synchronized void setChildConnections(IConnections tictactoeConnections) {
        this.tictactoeConnections = tictactoeConnections;
    }

    private IPokerMessageContent createPokerMessageContent(Map<String, IPokerMessageContent> mostRecentReadings) {
        return null;
    }

    @Override
    public synchronized void reset() {
    }

    @Override
    public synchronized double getFailure() {
        return 0;
    }

    @Override
    public Element toElement(final Document document) {
        final Element capability = super.toElement(document);
        return capability;
    }

    public synchronized boolean isDoneIterating() {
        boolean doneIterating = this.currentIteration >= this.PokerGuidelines.getMaxIteration();
        if (debug) LOG.debug("Done iterating = {}. This iteration = {}, max iterations allowed = {}", doneIterating,
                this.currentIteration, this.PokerGuidelines.getMaxIteration());
        return doneIterating;
    }

    /**
     * Gets the set of local registered prosumer agents given the set of all agents. Do not include the supervisors (control
     * component masters in this local organization) and do not include any independent forecaster agents (but a child that
     * is also performing a forecast role should be included).
     *
     * @return - the set of all prosumer agents registered in this local organization (does not include
     * other types of agents such as forecasters, etc)
     */
    public synchronized Set<Agent<?>> getLocalRegisteredProsumers() {
        // TODO: add ability to indicate sub is a prosumer agent (not a forecaster).
        final Set<Agent<?>> allAgents = this.getOwner()
                .getPersonaControlComponent().getOrganizationModel().getAgents();

        final Set<Agent<?>> prosumers = new HashSet<>();
        for (Agent<?> agent : allAgents) {
            boolean isMaster = (agent.getIdentifier() == this.owner.getPersonaControlComponent().getLocalMaster());
            boolean isExternalForecaster = agent.getIdentifier().toString().contains("_F");
            if (!isMaster && !isExternalForecaster) {
                prosumers.add(agent);
            }
        }
        return prosumers;
    }

    /**
     * Get the parameters from this instance goal and use them to set the goal-specific guidelines.
     *
     * @param ig - this instance of the specification goal
     */
    public synchronized void init(InstanceGoal<?> ig) {
        if (debug) LOG.info("Creating external organization from goal: {}.", ig);
        // Get the parameter values from the existing active instance goal
        final InstanceParameters params = Objects.requireNonNull((InstanceParameters) ig.getParameter());
        if (debug) LOG.debug("Initializing with the given goal parameter guidelines: {}.", params);
        this.setPokerGuidelines(IPokerGuidelines.extractGuidelines(params));
        if (this.PokerGuidelines == null) {
            LOG.error("Broker initializing organization with no broker guidelines. params={}", params);
            System.exit(-51);
        }
        this.setChildConnections(IConnections.extractConnections(params, "connections"));
        if (this.tictactoeConnections == null) {
            LOG.error("Broker initializing external organization with no participants. params={}", params);
            System.exit(-56);
        } else {
            if (debug) LOG.info("Starting initialization of new organization. ");
            if (debug)
                LOG.debug("{} authorized connections to child participants.", tictactoeConnections.getListConnectionGuidelines().size());
            final IConnectionGuidelines cg = tictactoeConnections.getListConnectionGuidelines().get(0);
            if (debug) LOG.debug("Organization guidelines found at {}:  ", cg.getSpecificationFilePath());
        }
    }

	public boolean allBidsReceived(TreeMap<String, IPokerMessageContent> inputs) {
		return false;
	}

	public IPokerMessageContent evaluateHand(TreeMap<String, IPokerMessageContent> inputs) {
		return null;
	}
   
}
