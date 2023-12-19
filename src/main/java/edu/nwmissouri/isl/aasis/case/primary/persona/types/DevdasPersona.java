/**
 *
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
package edu.nwmissouri.isl.aasis.case.primary.persona.types;

import edu.ksu.cis.macr.aasis.agent.persona.IOrganization;
import edu.ksu.cis.macr.aasis.spec.OrganizationFocus;
import edu.nwmissouri.isl.aasis.case.primary.persona.Persona;
import org.w3c.dom.Element;

/**
 The base persona for this AASIS agent.
 */
public class DevdasPersona extends Persona {

  /**
   Constructs a new instance of a home agent in accordance with the provided information. Additional agent capabilities
   can be specified in the agent configuration file (e.g. Agent.xml).

   @param organization the SelfOrganization organization, containing information about agents and objects in the
   SelfOrganization system.
   @param identifier a string containing the unique name of this agent.
   @param knowledge an XML representation of the agents knowledge of the organization.
   @param focus the enum that contains what kind of focus the object is (Agent or External).
   */
  public DevdasPersona(final IOrganization organization,
                         final String identifier, final Element knowledge, OrganizationFocus focus) {
    super(organization, identifier, knowledge, focus);
  }

  /**
   Constructs a new instance of a home agent in accordance with the provided information. Additional agent capabilities
   can be specified in the agent configuration file (e.g. Agent.xml).

   @param identifier a string containing the unique name of this agent.
   */
  public DevdasPersona(final String identifier) {
    super(identifier);
  }
}
