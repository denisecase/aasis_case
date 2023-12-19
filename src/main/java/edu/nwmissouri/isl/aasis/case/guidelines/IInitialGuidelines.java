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
package edu.nwmissouri.isl.aasis.case.guidelines;

import edu.ksu.cis.macr.goal.model.InstanceParameters;
import edu.ksu.cis.macr.organization.model.identifiers.StringIdentifier;

/**
 * The {@code IInitialGuidelines} provide information about the goal
 * guidelines for this participant in the organization.
 */
public interface IInitialGuidelines {

  public static IInitialGuidelines extractGuidelines(InstanceParameters params) {
    return (IInitialGuidelines) params.getValue(StringIdentifier.getIdentifier("initialGuidelines"));
  }

  int getPlayerInt();

  void setPlayerInt(int i);
}
