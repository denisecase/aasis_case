/**
 *
 * Copyright 2016 William Hargrave, Denise Case
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

import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code GuidelineManager} reads goal guidelines (the top goal parameters)
 * from a given XML file.
 */
public enum GuidelineManager {
  INSTANCE;
  private static final Logger LOG = LoggerFactory.getLogger(GuidelineManager.class);
  private static final boolean debug = false;

  public synchronized static Map<UniqueIdentifier, Object> getGoalParameterValues(String absPathToFile) {
    LOG.debug("STOP Entering getGoalParameterValues(absolutePathToFile={})", absPathToFile);
    Map<UniqueIdentifier, Object> goalParameterValues = new HashMap<UniqueIdentifier, Object>();

    DocumentBuilder db = null;
    try {
      db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      if (db == null) {
        throw new ParserConfigurationException();
      }
    } catch (ParserConfigurationException e3) {
      LOG.error("PARSER ERROR: Cannot read guidelines from initialize.xml ({}).", absPathToFile);
      System.exit(-44);
    }
    // Read goal parameters initialization file to configure goals
    Document configDoc = null;

    try {

      configDoc = db.parse(new File(absPathToFile));
      if (configDoc == null) {
        throw new IOException();
      }
    } catch (SAXException | IOException e2) {
      LOG.error("IO ERROR: Cannot read  guidelines from initialize.xml ({}).", absPathToFile);
      System.exit(-45);
    }
    // given the XML, set up the goal parameter map
    NodeList nodeList;
    nodeList = configDoc.getElementsByTagName("initialGuidelines");

    LOG.debug("There are {} initial guidelines.", nodeList.getLength());
    if (nodeList.getLength() == 0) {
      goalParameterValues.put(GoalParameters.initialGuidelines, null);

    } else {
      InitialGuidelines g = new InitialGuidelines();
      goalParameterValues.put(GoalParameters.initialGuidelines, g);
      if (debug)
        LOG.debug("{} read from Initialize.xml: {}.", GoalParameters.initialGuidelines, g.toString());
    }
    if (debug)
      LOG.debug("Exiting initializeGuidelines(goalParameterValues={})", goalParameterValues);
    return goalParameterValues;
  }

}
