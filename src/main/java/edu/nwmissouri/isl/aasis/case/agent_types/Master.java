/**
 *
 * Copyright 2018 Denise Case
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
package edu.nwmissouri.isl.aasis.case.agent_types;

import org.w3c.dom.Element;

import edu.ksu.cis.macr.obaa_pp.org.IExecutableOrganization;
import edu.nwmissouri.isl.aasis.case.agent.BaseAgent;

/**
 * A supervisoring master agent that runs the internal organization of persona in an AASIS agent.
 */
public class Master extends BaseAgent {
	/**
	 * Constructs a new instance in accordance with the provided information.
	 * Additional agent capabilities can be specified in the agent configuration
	 * file (e.g. Agent.xml).
	 *
	 * @param organization
	 *            the internal organization, containing information
	 *            about agents and objects in the internal system.
	 * @param name
	 *            a string containing the unique name of this agent.
	 * @param knowledge
	 *            an XML representation of the agents knowledge of the
	 *            SelfOrganization and the organization.
	 */
	public Master(final IExecutableOrganization organization, final String name, final Element knowledge) {
		super(organization, name, knowledge);
	}

	/**
	 * Constructs a new instance in accordance with the provided information.
	 * Additional agent capabilities can be specified in the agent configuration
	 * file (e.g. Agent.xml).
	 *
	 * @param name
	 *            a string containing the unique name of this agent.
	 */
	public Master(final String name) {
		super(name);
	}

	@Override
	public String toString() {
		return "Master{" + super.toString() + '}';
	}
}
