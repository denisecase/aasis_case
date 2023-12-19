/**
 *
 * Copyright 2018 Denise Case
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
package edu.nwmissouri.isl.aasis.case.org.poker.persona;


import edu.ksu.cis.macr.aasis.agent.persona.IOrganization;
import edu.ksu.cis.macr.aasis.spec.OrganizationFocus;
import edu.nwmissouri.isl.aasis.case.org.poker.capabilities.participate.PokerCommunicationCapability;
import edu.nwmissouri.isl.aasis.case.org.poker.capabilities.participate.PokerCapability;
import edu.nwmissouri.isl.aasis.case.org.poker.capabilities.participate.IPokerCommunicationCapability;
import edu.nwmissouri.isl.aasis.case.org.poker.capabilities.participate.IPokerCapability;
import edu.nwmissouri.isl.aasis.case.org.poker.plan_selector.PokerPlanSelector;
import edu.nwmissouri.isl.aasis.case.primary.persona.Persona;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;


/**
 A persona for this affiliate organization. Uses organization-specific plan selector and organization-specific communication capabilities.
 */
public abstract class PokerPersona extends Persona {
    private static final Logger LOG = LoggerFactory.getLogger(PokerPersona.class);
    private static final Boolean debug =  false;
    protected IPokerCommunicationCapability localPokerCommunicationCapability;
    protected IPokerCapability pokerCapability;

    /**
     * Constructs a new instance of an agent using the organization-based agent architecture. Each prosumer agent will have
     * the capabilities needed to cc in a peer-based organization both as a peer and as a supervisor and contains its own
     * knowledge about the immediate organization in which it participates.
     *
     * @param org              the organization, containing information about agents and objects in the organization system.
     * @param identifierString a string containing a name that uniquely identifies this in the system.
     * @param knowledge        an XML specification of the organization.
     * @param focus            an Enum defining what focus the organization is.
     */
    public PokerPersona(final IOrganization org, final String identifierString, final Element knowledge, final OrganizationFocus focus) {
        super(org, identifierString, knowledge, focus);
        planSelector = new PokerPlanSelector();

        this.localPokerCommunicationCapability = new PokerCommunicationCapability(this, org);
        if (debug) LOG.debug("\t New PokerCommunicationCapability={}", this.localPokerCommunicationCapability);
        addCapability(localPokerCommunicationCapability);
        if (debug) LOG.debug("\t Added PokerCommunicationCapability={}.", this.localPokerCommunicationCapability);

        this.pokerCapability = new PokerCapability(this, org);
        if (debug) LOG.debug("\t New actionPowerCapability={}", this.pokerCapability);
        addCapability(pokerCapability);
        if (debug) LOG.debug("\t Added actionPowerCapability={}.", this.pokerCapability);

        if (this.internalCommunicationCapability == null) {
            LOG.debug("ERROR: internalCommunicationCapability is null - Can't add auction communication capabilities.");
        } else {
            if (debug)
                LOG.debug("\tlocalPokerCommunicationCapability.getCommunicationChannelID()={}.", localPokerCommunicationCapability.getCommunicationChannelID());
            if (debug)
                LOG.debug("\tactionPowerCapability.getCommunicationChannelID()={}.", pokerCapability.getCommunicationChannelID());

            this.internalCommunicationCapability.addChannel(localPokerCommunicationCapability.getCommunicationChannelID(),
                    this.localPokerCommunicationCapability);
            if (debug) LOG.debug("\t Added localPokerCommunicationCapability internally to add the channel.");

            this.internalCommunicationCapability.addChannel(pokerCapability.getCommunicationChannelID(),
                    this.pokerCapability);
            if (debug) LOG.debug("\t Added actionPowerCapability internally to add the channel.");
       }
            LOG.info("\t..................DONE CONSTRUCTING POKER PERSONA(org={}, identifier={}, knowledge={}, focus={}", org, identifierString, knowledge, focus);
    }

    /**
     * Constructs a new instance of an agent using the organization-based agent architecture. Each prosumer agent will have
     * the capabilities needed to cc in a peer-based organization both as a peer and as a supervisor and contains its own
     * knowledge about the immediate organization in which it participates.
     *
     * @param identifierString a string containing a name that uniquely
     */
    public PokerPersona(final String identifierString) {
        super(identifierString);
        planSelector = new PokerPlanSelector();
        this.localPokerCommunicationCapability = new PokerCommunicationCapability(this, this.organization);
        if (debug) LOG.debug("\t New PokerCommunicationCapability={}", this.localPokerCommunicationCapability);
        addCapability(localPokerCommunicationCapability);
        if (debug) LOG.debug("\t Added PokerCommunicationCapability={}.", this.localPokerCommunicationCapability);

        if (this.internalCommunicationCapability == null) {
            LOG.debug("ERROR: internalCommunicationCapability is null - Can't add localPokerCommunicationCapability.");
        } else {
            if (debug)
                LOG.debug("\tlocalPokerCommunicationCapability.getCommunicationChannelID()={}.", localPokerCommunicationCapability.getCommunicationChannelID());
            if (debug)
                LOG.debug("\tactionPowerCapability.getCommunicationChannelID()={}.", pokerCapability.getCommunicationChannelID());

            this.internalCommunicationCapability.addChannel(localPokerCommunicationCapability.getCommunicationChannelID(),
                    this.localPokerCommunicationCapability);
            if (debug) LOG.debug("\t Added localPokerCommunicationCapability internally to add the channel.");

            this.internalCommunicationCapability.addChannel(pokerCapability.getCommunicationChannelID(),
                    this.pokerCapability);
            if (debug) LOG.debug("\t Added actionPowerCapability internally to add the channel.");
        }
        LOG.info("\t..................DONE CONSTRUCTING POKER PERSONA {}.", identifierString);
    }

    @Override
    public String toString() {
        return "PokerPersona [identifierString=" + this.identifierString + "]";
    }

}
