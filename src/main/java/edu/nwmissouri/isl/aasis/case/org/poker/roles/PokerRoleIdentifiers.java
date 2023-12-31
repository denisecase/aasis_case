/**
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
package edu.nwmissouri.isl.aasis.case.org.poker.roles;

import edu.ksu.cis.macr.organization.model.identifiers.StringIdentifier;
import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class listing the specified roles in the system.  The string names must exactly match the role names shown on the
 * refined goal models and role models.  By convention, the unique identifier names should be exactly the same as the
 * name strings.
 */
public class PokerRoleIdentifiers {

    public static final UniqueIdentifier Play_Poker_Role = StringIdentifier.getIdentifier("Play Poker Role");

    public static final UniqueIdentifier Deal_Poker_Role = StringIdentifier.getIdentifier("Deal Poker Role");


    @Override
    public String toString() {
        try {
            final String s = "";
            final Field fields[] = Class.forName(this.getClass().getName())
                    .getDeclaredFields();
            for (final Field field : fields) {
                s.concat(field + " ");
            }
            return s.concat(".");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PokerRoleIdentifiers.class.getName()).log(Level.SEVERE,
                    null, ex);
            return "";
        }

    }
}
