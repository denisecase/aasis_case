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
package edu.nwmissouri.isl.aasis.case.cc_p;


import edu.ksu.cis.macr.obaa_pp.agent.IAbstractAgent;
import edu.ksu.cis.macr.obaa_pp.cc_p.AbstractControlComponentParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * Extends {@code AbstractBaseControlComponent} to add the organization-specific functionality needed for registering and
 * participating in the agent's internal organization.
 */
public class Participant extends AbstractControlComponentParticipant {
    private static final Logger LOG = LoggerFactory.getLogger(Participant.class);
    private static final Boolean debug =  true;



    /**
     * A participant registers with the local control component master.
     *
     * @param name      - participant name
     * @param agent     - registering agent
     * @param knowledge - the XML knowledge about the organization
     */
    public Participant(final String name, final IAbstractAgent agent, final Element knowledge) {
        super(name, agent, knowledge);
        LOG.info("Constructing agent participant. Name={}, agent={}, knowledge={}",
                name, agent, knowledge);

    }


}