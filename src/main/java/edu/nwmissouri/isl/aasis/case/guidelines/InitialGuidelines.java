/**
 *
 * Copyright 2012-2018 Denise Case
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

import java.io.Serializable;

public class InitialGuidelines implements Serializable, IInitialGuidelines {
  private static final long serialVersionUID = 1L;
  private int playerInt;

  /**
   * Construct new new {@code}InitialGuidelines} with default values.
   */
  public InitialGuidelines() {
  }

  @Override
  public int getPlayerInt() {
    return this.playerInt;
  }

  @Override
  public void setPlayerInt(int i) {
    this.playerInt = i;
  }

public void getPlayerInt(int parseInt) {
}

}
