/**
 *
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
package edu.nwmissouri.isl.aasis.case.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ksu.cis.macr.aasis.messaging.IMessagingFocus;
import edu.ksu.cis.macr.aasis.types.IAgentType;

/**
 * This {@code RunManager} reads the information provided in the run.properties 
 * file when the AASIS agent first initializes and
 * provides useful information to the SelfOrganization system in two singleton classes: The {@code Scenario} describes
 * the physical system being simulated. 
 * The {@code Player} describes the way the agent should execute. 
 * <p> Set maxtimeslices to zero to run continuously. 
 * Use this to add new new simulation variables and avoid hard-coding 'magic
 * constants' into the code. 
 * Variables should be in all lower case letters. 
 * Do not use quotations. 
 * All properties are automatically read as strings. 
 * Do not include empty spaces after the equal signs. </p> 
 * Conversions from strings to ints, arrays, etc should be done here 
 * provided no additional SelfOrganization classes are needed. If additional
 * SelfOrganization information is needed, then just pass along the string (less preferred). 
 * <p> A singleton will have one instance per JVM.</p>
 */
public enum RunManager {
  /**
   * Singleton instance of the run manager that reads user settings for this AASIS agent.
   */
  INSTANCE;
  public static final boolean FORCE_STOP_AFTER_INITIALLY_CONNECTED = false;
  public static final double INITIAL_CONNECTION_THRESHOLD_FRACTION = 1.00; // 1.00 means 100% must connect
  private static final String CUR_DIR = System.getProperty("user.dir"); // singleton - if you can count the instances of a class,
  private static final Logger LOG = LoggerFactory.getLogger(RunManager.class);
  private static String absolutePathToConfigsFolder;
  private static String absolutePathToLogbackXMLFile;
  private static String absolutePathToSourceFolder;
  private static String absolutePathToStandardAgentModelsFolder;
  private static String absolutePathToStandardOrganizationModelsFolder;
  private static String absolutePathToStandardPropertiesFolder;
  private static String absolutePathToTestCaseFolder;
  private static String absolutePathtoStaticLayoutImageFile;
  private static boolean showCommunicationInBrowser;
  private static final boolean debug = false;
  private static int deliveryCheckTime_ms = 500;
  private static boolean fullyConnected = false;
  private static boolean initializeGoalsInConstructors;
  private static boolean initiallyConnected = false;
  private static boolean isLoaded = Boolean.FALSE;
  private static boolean isStopped = false;
  private static int planningHorizonInMinutes;
  private static Properties properties;
  private static HashSet<String> registered = new HashSet<>();
  private static Boolean isSimpleMAS;
  private static Boolean showAgentOrganizations;
  private static int standardWaitTime_ms = 100;
  private static String staticLayoutImageResourceFileName;
  private static boolean stopped;
  private static String testCaseName;
  private static String topGoalName = "";
  private static int totalConnectionCount;
  private static int participantRetryDelay_ms;
  private static int participantTimeout_ms;
  private static HashSet<String> pokerRegistered = new HashSet<>();
  private static HashSet<String> tictactoeRegistered = new HashSet<>();
  private static HashMap<IAgentType, ArrayList<String>> agentMap = new HashMap<>();

  public synchronized static void add(IAgentType agentType, String agentName) {
    if (debug)
      LOG.debug("Adding {} {}.", agentType, agentName);
    ArrayList<String> lst = RunManager.agentMap.get(agentType);
    if (lst == null)
      lst = new ArrayList<>();
    lst.add(agentName);
    RunManager.agentMap.put(agentType, lst);
    if (debug)
      LOG.debug("List of {}= {}.", agentType, RunManager.agentMap.get(agentType));
  }

  public synchronized static void setInitiallyConnected(final boolean initiallyConnected) {
    RunManager.initiallyConnected = initiallyConnected;
    if (RunManager.isInitiallyConnected() && RunManager.FORCE_STOP_AFTER_INITIALLY_CONNECTED)
      terminateSuccessfully();
    if (RunManager.isInitiallyConnected() && !RunManager.FORCE_STOP_AFTER_INITIALLY_CONNECTED)
      LOG.info(" ..........INITIALLY FULLY CONNECTED ");
  }

  private static void terminateSuccessfully() {
  }

  public synchronized static void registered(final String identifierString, final IMessagingFocus messagingFocus) {
    if (messagingFocus.equals(MessagingFocus.TICTACTOE_PARTICIPATE)) {
      tictactoeRegistered.add(identifierString);
      if (debug)
        LOG.info("{} is the {} agent to register.", identifierString, tictactoeRegistered.size());
    }
    if (messagingFocus.equals(MessagingFocus.POKER_PARTICIPATE)) {
      pokerRegistered.add(identifierString);
      if (debug)
        LOG.info("{} is the {} agent to register.", identifierString, pokerRegistered.size());
    }

  }

  public static void addPrimaryMessageSelfToSub(String sender, String receiver, long timeSlice) {
    // TreeSet<String> tsReports = RunManager.smartTicTacToeSelfToSubForwardsHome.get(timeSlice);
    // if (tsReports == null) tsReports = new TreeSet<>();
    // tsReports.add(sender + "-" + receiver);
    // RunManager.smartTicTacToeSelfToSubForwardsHome.put(timeSlice, tsReports);
    // LOG.debug("Number of self persona forwarding reviewed sensor report to sub persona in timeslice {}: {} of {}", timeSlice, RunManager.smartTicTacToeSelfToSubForwardsHome.get(timeSlice).size(), getAgentCountByType(AgentType.Home));
    // if (allPowerMessageSelfToSub(timeSlice) && RunManager.FORCE_STOP_AFTER_SELF_HOME_REPORTS) {
    //     //terminateSuccessfully();
    // }
  }

  public static String getAbsolutePathToConfigsFolder() {
    return absolutePathToConfigsFolder;
  }

  public static String getAbsolutePathToLogbackXMLFile() {
    return absolutePathToLogbackXMLFile;
  }

  public static String getAbsolutePathToSourceFolder() {
    return absolutePathToSourceFolder;
  }

  public static String getAbsolutePathToStandardAgentModelsFolder() {
    return absolutePathToStandardAgentModelsFolder;
  }

  public static String getAbsolutePathToStandardOrganizationModelsFolder() {
    return absolutePathToStandardOrganizationModelsFolder;
  }

  public static String getAbsolutePathToStandardPropertiesFolder() {
    return absolutePathToStandardPropertiesFolder;
  }

  public static String getAbsolutePathToTestCaseFolder() {
    return absolutePathToTestCaseFolder;
  }

  public static String getAbsolutePathtoStaticLayoutImageFile() {
    return absolutePathtoStaticLayoutImageFile;
  }

  public static int getDeliveryCheckTime_ms() {
    return deliveryCheckTime_ms;
  }

  public static double getInitialConnectionThresholdFraction() {
    return INITIAL_CONNECTION_THRESHOLD_FRACTION;
  }

  public static int getPlanningHorizonInMinutes() {
    return planningHorizonInMinutes;
  }

  public static Properties getProperties() {
    return properties;
  }

  public static HashSet<String> getRegistered() {
    return registered;
  }

  public static int getStandardWaitTime_ms() {
    return standardWaitTime_ms;
  }

  public static String getStaticLayoutImageResourceFileName() {
    return staticLayoutImageResourceFileName;
  }

  public static String getTestCaseName() {
    return testCaseName;
  }

  public static String getTopGoalName() {
    return RunManager.topGoalName;
  }

  public static int getTotalConnectionCount() {
    return totalConnectionCount;
  }

  private static String verifyFolderExists(final String strFolder, final String title) {
    final File f = new File(strFolder);
    if (f.exists() && f.isDirectory()) {
      if (debug)
        LOG.debug("{} path is {}", title, strFolder);
    } else {
      LOG.error("ERROR: {} path {} not found. ", title, strFolder);
    }
    return strFolder;
  }

  private static void initializeAbsolutePathToStandardPropertiesFolder(final String standardpropertiespath) {
    String connector = "//";
    final String relpath = standardpropertiespath.trim();
    if (relpath.startsWith("/")) {
      connector = "";
    }
    final String strFolder = CUR_DIR + connector + relpath;
    final String title = "Standard properties folder";
    RunManager.setAbsolutePathToStandardPropertiesFolder(verifyFolderExists(strFolder, title));
  }

  public static void setAbsolutePathToStandardPropertiesFolder(String absolutePathToStandardPropertiesFolder) {
    RunManager.absolutePathToStandardPropertiesFolder = absolutePathToStandardPropertiesFolder;
  }

  private static String getAbsolutePathToConfigsFolder(final String configpath) {
    String connector = "/";
    if (configpath.trim().endsWith("/")) {
      connector = "";
    }
    final String strFolder = CUR_DIR + configpath + connector;
    final String title = "Configs folder";
    return verifyFolderExists(strFolder, title);
  }

  public static String getAbsolutePathToStandardAgentGoalModel(IAgentType agentType) {
    LOG.info("Getting standard agent goal model for {}", agentType);
    String strFile;
    if (agentType.toString().endsWith("Agent")) {
      strFile = getAbsolutePathToStandardAgentModelsFolder() + "/" + agentType.toString() + "GoalModel.goal";
      LOG.info("Goal model = {}", strFile);
    } else {
      strFile = getAbsolutePathToStandardAgentModelsFolder() + "/" + agentType.toString() + "AgentGoalModel.goal";
      LOG.info("Agent Goal model = {}", strFile);
    }

    File f = new File(strFile);
    if (f.exists() && !f.isDirectory()) {
      if (debug)
        LOG.info("{} Standard agent goal model file is {}", agentType.toString(), strFile);
    } else {
      LOG.error("ERROR: {} Standard agent goal model file {} not found. ", agentType.toString(), strFile);
    }
    return strFile;
  }

  public static String getAbsolutePathToStandardAgentRoleModel(IAgentType agentType) {
    LOG.info("Getting standard agent role model for {}", agentType);
    String strFile;
    if (agentType.toString().endsWith("Agent")) {
      strFile = getAbsolutePathToStandardAgentModelsFolder() + "/" + agentType.toString() + "RoleModel.role";
      LOG.info("Role model = {}", strFile);
    } else {
      strFile = getAbsolutePathToStandardAgentModelsFolder() + "/" + agentType.toString() + "AgentRoleModel.role";
      LOG.info("Agent Role model = {}", strFile);
    }
    File f = new File(strFile);
    if (f.exists() && !f.isDirectory()) {
      if (debug)
        LOG.info("{} Standard agent role model file is {}", agentType.toString(), strFile);
    } else {
      LOG.error("ERROR: {} Standard agent role model file {} not found. ", agentType.toString(), strFile);
    }
    return strFile;
  }

  private static String verifyFileExists(final String strFile, final String title) {
    final File f = new File(strFile);
    if (f.exists() && !f.isDirectory()) {
      if (debug)
        LOG.debug("title is {}", strFile);
    } else {
      LOG.info("INFO: {} {} not found. ", title, strFile);
    }
    return strFile;
  }

  private static void initializeIsSimpleMAS(String value) {
    final String s = value.trim().toLowerCase();
    RunManager.setIsSimpleMAS(s.equals("yes") || s.equals("true") || s.equals("y"));
  }

  public static void setIsSimpleMAS(Boolean value) {
    RunManager.isSimpleMAS = value;
    edu.ksu.cis.macr.obaa_pp.config.RunManager.setIsSimpleMAS(value);
  }

  private static void initializeShowAgentOrganizations(String value) {
    final String s = value.trim().toLowerCase();
    RunManager.setShowAgentOrganizations(s.equals("yes") || s.equals("true") || s.equals("y"));
  }

  public static void setShowAgentOrganizations(Boolean value) {
    RunManager.showAgentOrganizations = value;
  }

  private static void initializeShowCommunicationInBrowser(String value) {
    final String s = value.trim().toLowerCase();
    RunManager.setShowCommunicationInBrowser(s.equals("yes") || s.equals("true") || s.equals("y"));
  }

  public static void setShowCommunicationInBrowser(Boolean value) {
    RunManager.showCommunicationInBrowser = value;
  }

  public static void setStaticLayoutImageResourceFileName(String staticLayoutImageResourceFileName) {
    RunManager.staticLayoutImageResourceFileName = staticLayoutImageResourceFileName;
  }

  public static boolean isInitiallyConnected() {
    return initiallyConnected;
  }

  public static boolean isStopped() {
    return stopped;
  }

  public static void main(String[] args) throws IOException {
    load();
  }

  public static void load() {
    setProperties(new Properties());
    final File f = new File(CUR_DIR, "run.properties");
    LOG.info("Reading user-specified information for {}.", f.getAbsolutePath());

    // try loading from the current directory
    try {
      try (FileInputStream fileInputStream = new FileInputStream(f)) {
        setProperties(new Properties());
        getProperties().load(fileInputStream);
      }
      for (Object o : new TreeSet<>(getProperties().keySet())) {
        final String key = (String) o;
        final String value = getProperties().getProperty(key);
        LOG.info("\t Reading run property {}: {}", key, value);
      }
      isLoaded = true;
      initializeAbsolutePathToSourceFolder(getValue("sourcepath"));
      initializeAbsolutePathToLogConfigFile(getValue("logpath"));
      initializeAbsolutePathToConfigsFolder(getValue("configpath"));
      initializeAbsolutePathToStandardAgentModelsFolder(getValue("standardagentmodelspath"));
      initializeAbsolutePathToStandardOrganizationModelsFolder(getValue("standardorganizationmodelspath"));
      initializeTestCaseName(getValue("configpath"), getValue("testcase"));
      initializeGetAbsolutePathToTestCaseFolder(getValue("configpath"), getValue("testcase"));
      initializeTopGoal(getValue("topgoal"));
      initializeInitializeGoalsInConstructors(getValue("initializegoalsinconstructors"));
      initializeDeliveryCheckTime(getValue("deliverychecktimems"));
      initializeWaitTime(getValue("standardwaitms"));
      initializeIsSimpleMAS(getValue("issimplemas"));
      initializeShowAgentOrganizations(getValue("showagentorganizations"));
      initializeShowCommunicationInBrowser(getValue("showcommunicationinbrowser"));
      initializeParticipantRetryDelayMS(getValue("participantretrydelayms"));
      initializeParticipantTimeoutMS(getValue("participanttimeoutms"));

    } catch (FileNotFoundException e) {
      LOG.error("Run properties file not found. {}", e.getMessage());
    } catch (IOException e) {
      LOG.error("Run properties file - error reading contents. {}", e.getMessage());
    } catch (Exception e) {
      LOG.error("Run properties file - error reading contents. {}", e.getMessage());
    }
  }

  /**
   * @param properties the properties to set
   */
  private static void setProperties(Properties properties) {
    RunManager.properties = properties;
  }

  private static void initializeAbsolutePathToSourceFolder(final String sourcepath) {
    final String strFolder = CUR_DIR + sourcepath.trim();
    final String title = "source folder";
    RunManager.setAbsolutePathToSourceFolder(verifyFolderExists(strFolder, title));
  }

  public static void setAbsolutePathToSourceFolder(String absolutePathToSourceFolder) {
    RunManager.absolutePathToSourceFolder = absolutePathToSourceFolder;
  }

  private static void initializeAbsolutePathToLogConfigFile(final String logpath) {
    final String strFile = CUR_DIR + logpath.trim();
    final String title = "log configuration file";
    RunManager.setAbsolutePathToLogbackXMLFile(verifyFileExists(strFile, title));
  }

  public static void setAbsolutePathToLogbackXMLFile(String absolutePathToLogbackXMLFile) {
    RunManager.absolutePathToLogbackXMLFile = absolutePathToLogbackXMLFile;
  }

  private static void initializeAbsolutePathToConfigsFolder(final String configpath) {
    final String strFolder = CUR_DIR + configpath.trim();
    final String title = "config folder";
    RunManager.setAbsolutePathToConfigsFolder(verifyFolderExists(strFolder, title));
  }

  public static void setAbsolutePathToConfigsFolder(String absolutePathToConfigsFolder) {
    RunManager.absolutePathToConfigsFolder = absolutePathToConfigsFolder;
  }

  private static void initializeAbsolutePathToStandardAgentModelsFolder(String path) {
    final String strFolder = CUR_DIR + path.trim();
    final String title = "standard agent models folder";
    RunManager.setAbsolutePathToStandardAgentModelsFolder(verifyFolderExists(strFolder, title));
  }

  public static void setAbsolutePathToStandardAgentModelsFolder(String absolutePathToStandardAgentModelsFolder) {
    RunManager.absolutePathToStandardAgentModelsFolder = absolutePathToStandardAgentModelsFolder;
  }

  private static void initializeAbsolutePathToStandardOrganizationModelsFolder(final String path) {
    final String strFolder = CUR_DIR + path.trim();
    final String title = "standard org models folder";
    RunManager.setAbsolutePathToStandardOrganizationModelsFolder(verifyFolderExists(strFolder, title));
  }

  public static void setAbsolutePathToStandardOrganizationModelsFolder(
      String absolutePathToStandardOrganizationModelsFolder) {
    RunManager.absolutePathToStandardOrganizationModelsFolder = absolutePathToStandardOrganizationModelsFolder;
  }

  private static void initializeTestCaseName(final String configpath, final String testcase) {
    String connector = "//";
    if (configpath.endsWith("/") || testcase.startsWith("/")) {
      connector = "";
    }
    final String strFolder = CUR_DIR + configpath.trim() + connector + testcase.trim();
    final String title = "Test case folder";
    if (!verifyFolderExists(strFolder, title).isEmpty()) {
      RunManager.setTestCaseName(testcase.trim());
    }
  }

  public static void setTestCaseName(final String testCaseName) {
    RunManager.testCaseName = testCaseName;
  }

  private static void initializeGetAbsolutePathToTestCaseFolder(final String configpath, final String testcase) {
    String connector = "//";
    if (configpath.endsWith("/") || testcase.startsWith("/")) {
      connector = "";
    }
    final String strFolder = CUR_DIR + configpath.trim() + connector + testcase.trim();
    final String title = "Test case folder";
    RunManager.setAbsolutePathToTestCaseFolder(verifyFolderExists(strFolder, title));
  }

  public static void setAbsolutePathToTestCaseFolder(String absolutePathToTestCaseFolder) {
    RunManager.absolutePathToTestCaseFolder = absolutePathToTestCaseFolder;
  }

  private static void initializeTopGoal(final String topgoal) {
    RunManager.setTopGoalName(topgoal.trim());
  }

  public static void setTopGoalName(String topGoalName) {
    RunManager.topGoalName = topGoalName;
  }

  private static void initializeInitializeGoalsInConstructors(final String initializegoalsinconstructors) {
    RunManager.setInitializeGoalsInConstructors(Boolean.TRUE);
    try {
      final String strValue = initializegoalsinconstructors.trim().toLowerCase();
      RunManager
          .setInitializeGoalsInConstructors(strValue.equals("yes") || strValue.equals("true") || strValue.equals("y"));
    } catch (Exception e) {
      // continue
    }
  }

  public static void setInitializeGoalsInConstructors(boolean initializeGoalsInConstructors) {
    RunManager.initializeGoalsInConstructors = initializeGoalsInConstructors;
  }

  private static void initializeDeliveryCheckTime(String input) {
    try {
      int msecs = Integer.parseInt(input.trim());
      RunManager.setDeliveryCheckTime_ms(msecs);
    } catch (Exception e) {
      LOG.error("ERROR: delivery checktime in ms could not be read. {}", input);
    }
  }

  public static void setDeliveryCheckTime_ms(int deliveryCheckTime_ms) {
    RunManager.deliveryCheckTime_ms = deliveryCheckTime_ms;
  }

  private static void initializeWaitTime(String input) {
    try {
      int msecs = Integer.parseInt(input.trim());
      RunManager.setStandardWaitTime_ms(msecs);
    } catch (Exception e) {
      LOG.error("ERROR: standard wait time in ms could not be read. {}", input);
    }
  }

  public static void setStandardWaitTime_ms(int standardWaitTime_ms) {
    RunManager.standardWaitTime_ms = standardWaitTime_ms;
  }

  public static String getValue(String propertyName) {
    if (!isLoaded) {
      RunManager.load();
    }
    return RunManager.getProperties().getProperty(propertyName);
  }

  public static Boolean getIsSimpleMAS() {
    return isSimpleMAS;
  }

  public static Boolean getShowAgentOrganizations() {
    return showAgentOrganizations;
  }

  public static Boolean getShowCommunicationInBrowser() {
    return showCommunicationInBrowser;
  }

  public static String getAbsolutePathToStandardOrganizationGoalModel(String orgModelFolder) {
    String connector = "//";
    final String relpath = orgModelFolder.trim();
    if (relpath.startsWith("/")) {
      connector = "";
    }
    final String strFolder = CUR_DIR + connector + relpath;
    final String title = "Standard org model folder";
    verifyFolderExists(strFolder, title);
    return strFolder;
  }

  public static String getAbsolutePathToStandardOrganizationRoleModel(String orgModelFolder) {
    String connector = "//";
    final String relpath = orgModelFolder.trim();
    if (relpath.startsWith("/")) {
      connector = "";
    }
    final String strFolder = CUR_DIR + connector + relpath;
    final String title = "Standard org model folder";
    verifyFolderExists(strFolder, title);
    return strFolder;
  }

  public static String getTopGoal() {
    return "Succeed";
  }

  private static void initializeParticipantRetryDelayMS(String input) {
    try {
      int participantRetryDelay = Integer.parseInt(input.trim());
      RunManager.setParticipantRetryDelayMS(participantRetryDelay);
    } catch (Exception e) {
      LOG.error("ERROR: participant retry delay could not be read. {}", input);
    }
  }

  public static int getParticipantRetryDelay_ms() {
    return RunManager.participantRetryDelay_ms;
  }

  public static int getParticipantTimeout_ms() {
    return RunManager.participantTimeout_ms;
  }

  public static void setParticipantRetryDelayMS(int retryDelay) {
    RunManager.participantRetryDelay_ms = retryDelay;
  }

  public static void setParticipantTimeoutMS(int v) {
    RunManager.participantTimeout_ms = v;
  }

  private static void initializeParticipantTimeoutMS(String input) {
    try {
      int v = Integer.parseInt(input.trim());
      RunManager.setParticipantTimeoutMS(v);
    } catch (Exception e) {
      LOG.error("ERROR: participant timeout ms could not be read. {}", input);
    }
  }

}
