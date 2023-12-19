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
package edu.nwmissouri.isl.aasis.case.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.Discriminator;

/**
 * A class to assist with thread-based logging. See: http://java.dzone.com/articles/siftingappender-logging.
 */
public class ThreadNameBasedDiscriminator implements Discriminator<ILoggingEvent> {
    private static final String KEY = "threadName";
    private boolean started;


    public boolean isStarted() {
        return started;
    }

    @Override
    public String getDiscriminatingValue(ILoggingEvent iLoggingEvent) {
        return Thread.currentThread().getName();
    }

    @Override
    public String getKey() {
        return KEY;
    }

    public synchronized void start() {
        started = true;
    }

    public synchronized void stop() {
        started = false;
    }
}