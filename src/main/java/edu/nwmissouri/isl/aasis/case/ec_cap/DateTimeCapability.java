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
package edu.nwmissouri.isl.aasis.case.ec_cap;

import edu.ksu.cis.macr.obaa_pp.ec.ICapability;
import edu.ksu.cis.macr.obaa_pp.ec.base.IExecutionComponent;
import edu.ksu.cis.macr.obaa_pp.ec_cap.AbstractOrganizationCapability;
import edu.ksu.cis.macr.obaa_pp.objects.IDisplayInformation;
import edu.ksu.cis.macr.obaa_pp.org.IExecutableOrganization;
import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;
import edu.nwmissouri.isl.aasis.case.clock.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code DateTimeCapability} provides the ability to access to the system time.
 */
public class DateTimeCapability extends AbstractOrganizationCapability implements ICapability {
  private static final Logger LOG = LoggerFactory.getLogger(DateTimeCapability.class);
  private static final boolean debug = false;
  private final Map<UniqueIdentifier, Map<String, String>> capabilities = new HashMap<>();
  private double interval_seconds = 60.0;
  private GregorianCalendar simulationTime = new GregorianCalendar();

  /**
   * Construct a new {@code DateTimeCapability} instance.
   *
   * @param owner - the agent possessing this capability.
   * @param org   - the immediate organization in which this agent operates.
   */
  public DateTimeCapability(final IExecutionComponent owner, final IExecutableOrganization org) {
    super(DateTimeCapability.class, owner, org);

  }

  public synchronized static long incrementTimeSlice() {
    Clock.setTimeSlicesElapsedSinceStart(Clock.getTimeSlicesElapsedSinceStart() + 1);
    return Clock.getTimeSlicesElapsedSinceStart();
  }

  /**
   * @return the the length of a time slice interval in seconds.
   */
  public synchronized double getInterval_seconds() {
    return interval_seconds;
  }

  @Override
  public synchronized String toString() {
    double planningHorizon_minutes = 15.0;
    return "DateTimeCapability{" + ", interval_seconds=" + interval_seconds + ", simulationTime=" + simulationTime
        + ", planningHorizon_minutes=" + planningHorizon_minutes + '}';
  }

  /**
   * Returns the {@code DisplayInformation} object containing the information for the {@code ICapability}.
   *
   * @param displayInformation the data display.
   */
  @Override
  public synchronized void populateCapabilitiesOfDisplayObject(IDisplayInformation displayInformation) {
    super.populateCapabilitiesOfDisplayObject(displayInformation);
  }

  @Override
  public synchronized void reset() {

  }

  // @Override
  public synchronized double getFailure() {
    return 0;
  }

  /**
   * Returns the {@code DOM} {@code Element} of the {@code IAttributable} or {@code ICapability}.  This method should be
   * overwritten by subclasses if there are additional variables defined whose values should be saved if they affect the
   * state of the object.  Overwriting can be done in two ways: adding additional information to the {@code Element}
   * returned by the super class, or creating a completely new element from scratch.
   *
   * @param document the document in which to create the {@code DOM} {@code Element}s.
   * @return the {@code DOM} {@code Element} of the {@code IAttributable} or {@code ICapability}.
   */
  @Override
  public synchronized Element toElement(Document document) {
    return null;
  }

  /**
   * @return the length of a time slice interval in milliseconds.
   */
  private synchronized int getInterval_milliseconds() {
    return (int) (this.getInterval_seconds() * 1000.0);
  }

  public synchronized int getMaxTimeSlices() {
    return Clock.getMaxTimeSlices();
  }

  /**
   * @return the simulationStartTime
   */
  public synchronized GregorianCalendar getSimulationStartTime() {
    return Clock.getSimulationStartTime();
  }

  /**
   * @return the simulationTime
   */
  public synchronized GregorianCalendar getSimulationTime() {
    return Clock.getSimulationTime();
  }

  /**
   * @return - the integer number of time slices elapsed since the simulation began
   */
  public int getTimeSlice() {
    return Clock.getTimeSlicesElapsedSinceStart();
  }

  /**
   * @return the the number of time slices elapsed since the simulation began.
   */
  public synchronized int getTimeSlicesElapsedSinceStart() {
    int numElapsed = Clock.getTimeSlicesElapsedSinceStart();
    int maxTimeslices = Clock.getMaxTimeSlices();

    if (maxTimeslices > 0 && numElapsed > maxTimeslices) {
      LOG.info("SIMULATION COMPLETE after {} timeslices.....................................", maxTimeslices);
    }
    return numElapsed;
  }

  public synchronized void setTimeSlicesElapsedSinceStart(final int timeSlicesElapsedSinceStart) {
    Clock.setTimeSlicesElapsedSinceStart(timeSlicesElapsedSinceStart);
  }
}
