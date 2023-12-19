/**
 *
 * Copyright 2012-2018 Denise Case Northwest Missouri State University
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

import edu.ksu.cis.macr.obaa_pp.ec.base.IExecutionComponent;
import edu.ksu.cis.macr.obaa_pp.ec_cap.AbstractOrganizationCapability;
import edu.ksu.cis.macr.obaa_pp.org.IExecutableOrganization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The {@code AutonomousOperationCapability} provides the ability to operate independent of any external organization.
 */
public class AutonomousOperationCapability extends AbstractOrganizationCapability {
  private static final Logger LOG = LoggerFactory.getLogger(AutonomousOperationCapability.class);
  private static final boolean debug = true;

  /**
   * Construct a new {@code AutonomousOperationCapability} instance.
   *
   * @param owner - the agent possessing this capability.
   * @param org   - the immediate organization in which this agent operates.
   */
  public AutonomousOperationCapability(final IExecutionComponent owner, final IExecutableOrganization org) {
    super(AutonomousOperationCapability.class, owner, org);
  }

  @Override
  public synchronized String toString() {
    return "AutonomousOperationCapability [no content yet=]";
  }

  @Override
  public synchronized void reset() {
  }

  @Override
  public synchronized double getFailure() {
    return 0;
  }

  @Override
  public synchronized Element toElement(final Document document) {
    final Element capability = super.toElement(document);
    return capability;
  }
}
