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
package edu.nwmissouri.isl.aasis.case;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.util.StatusPrinter;
import edu.ksu.cis.macr.aasis.agent.cc_p.ConnectionModel;
import edu.ksu.cis.macr.aasis.agent.persona.SelfTurnCounter;
import edu.ksu.cis.macr.aasis.self.IInnerOrganization;
import edu.ksu.cis.macr.aasis.simulator.clock.Clock;
import edu.ksu.cis.macr.aasis.simulator.player.Player;
import edu.ksu.cis.macr.aasis.simulator.scenario.MessagingCheckpoint;

import edu.nwmissouri.isl.aasis.case.config.RunManager;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.roles.TicTacToeRoleIdentifiers;
import edu.nwmissouri.isl.aasis.case.messaging.MessagingManager;
import edu.nwmissouri.isl.aasis.case.self.InnerOrganizationFactory;

import edu.ksu.cis.macr.obaa_pp.org.IExecutableOrganization;
import edu.ksu.cis.macr.obaa_pp.org.OrganizationFactory;
import edu.ksu.cis.macr.obaa_pp.spec.IOrganizationSpecification;
import edu.ksu.cis.macr.obaa_pp.spec.OrganizationSpecification;
import edu.ksu.cis.macr.obaa_pp.views.OrganizationView;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Starts up a group of organization-based agents.
 */
public enum Launcher {
    /**
     * Singleton instance of the Launcher (one per JVM).
     */
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);
    private static final boolean debug = true;
    private static boolean stepInLauncher = false;

    /**
     * Allows the external setting (either through GUI or config file to determine whether step mode should stop at steps in
     * this file.
     *
     * @return true if stepping in this file is desired; false if we should not stop in this file even during step
     * mode.
     */
    public static boolean isStepInLauncher() {
        return stepInLauncher;
    }

    /**
     * Sets whether to stop in this file during step mode.
     *
     * @param stepInLauncher - true if stepping in this file is desired; false if we should not stop in this file even during
     *                       step mode.
     */
    public static void setStepInLauncher(final boolean stepInLauncher) {
        Launcher.stepInLauncher = stepInLauncher;
    }

    /**
     * Prints out to the console the help for launching the main.
     *
     * @param argumentsDescription the description of the command-line arguments.
     */
    @SuppressWarnings("unused")
	private static void printHelp(final String[] argumentsDescription) {
        System.out.println(String.format("%d arguments required:",
                argumentsDescription.length));
        for (int i = 0; i < argumentsDescription.length; i++) {
            System.out.println(String.format("arg%d - %s", i,
                    argumentsDescription[i]));
        }
    }

    /**
     * The main program that runs a single executable organization of agents.
     *
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
      Thread.currentThread().setName("Launcher");
      initLogging();
      load();
      if (RunManager.getIsSimpleMAS()) {
        beginMAS();
      } else {
        beginAASISAgent();
      }
    }

    

    private static void load() {
      // load user parameters for this run. Includes scenario, player information
      edu.ksu.cis.macr.obaa_pp.config.RunManager.load();
      LOG.info("========= READ OBAA++ USER INPUTS {}  =========\n\n");
  
      RunManager.load();
      LOG.info("========= READ APP USER INPUTS {}  =========\n\n");
      step();
    }

    private static void beginMAS() {

        
        // SCRIPT THE AGENT.XML file from the contents of INitialize.xml ******************************************************************
        //***************************************************************************************
        // load user parameters for this run. Includes scenario, player information
        edu.ksu.cis.macr.obaa_pp.config.RunManager.load();
        LOG.info("========= READ OBAA++ USER INPUTS {}  =========\n\n");

        RunManager.load();
        LOG.info("========= READ APP USER INPUTS {}  =========\n\n");
        step();

        // get the organization specification folder
        final File folder = new File(RunManager.getAbsolutePathToTestCaseFolder());
        LOG.info("========= FOUND FOLDER {}  =========\n\n", folder);
        step();

        // get the organization specification
        IOrganizationSpecification spec;
        try {
            spec = new OrganizationSpecification(folder.getAbsolutePath());
            LOG.info("========= CREATED SPECIFICATION: {}  =========\n\n", spec);
        } catch (final Exception e) {
            LOG.error("ERROR: Could not initialize org {}. {}", folder, e.getMessage());
            return;
        }
        step();

        IExecutableOrganization org = null;
        try {
            org = OrganizationFactory.getOrganization(spec);
            LOG.info("org:{}", org);
            LOG.info("========= CREATED ORG {}  =========\n\n", folder.getName());
        } catch (final Exception e) {
            LOG.error("ERROR: Could not initialize org {}. {}", folder, e.getMessage());
            System.exit(-1);
        }
        step();

        // display org viewer (for the control component parts)
        OrganizationView.createOrganizationView(org);
        LOG.info("========= VIEW STARTED  =========\n\n");
        step();

        // run organization
        LOG.info("========= STARTING ORGANIZATION  =========\n\n");
       org.run();
    }

    private static void beginAASISAgent() {

      LOG.info("========= INITIALIZING AASIS AGENT  =========\n\n");
      
      LOG.info("Test case name is {}", RunManager.getTestCaseName());
  
      // initialize communication exchange for messaging
      MessagingManager.initialize();
      step();
  
      // open browser to the RabbitMQ local host website.
      DisplayCommunicationsInBrowser(RunManager.getShowCommunicationInBrowser());
      step();
  
      // set the simulation time to first time slice
      Clock.setTimeSlicesElapsedSinceStart(1);
      step();
  
      LOG.info("========= SET CLOCK  =========\n\n");
  
      // show overall connection view
      ConnectionModel.updateConnectionModel();
     // ConnectionView.createConnectionView();
  
      // get list of agents
      final File[] selfAgentFolders = getTestCaseSelfFolders(RunManager.getAbsolutePathToTestCaseFolder());
      step();
  
      // set total number of agents
      SelfTurnCounter.setNumberOfOrganizations(selfAgentFolders.length);
      ConnectionModel.setNumberOfAgents(selfAgentFolders.length);
  
      // create agents based on specifications provided
      ArrayList<IInnerOrganization> allAASISAgents = createAgentsAndCounts(selfAgentFolders);
      step();
     
      // initialize goal parameters (must be done after the agents are created)
      initializeGoalGuidelines(allAASISAgents);
      step();
  
      // load environment objects from environment configuration files
      loadEnvironmentObjects(allAASISAgents);
      step();
  
      // load persona from agent files
      loadPersona(allAASISAgents);
      Player.step();
  
      // display inner organizations for each agent
     // displayAgentOrganizations(allAASISAgents, RunManager.getShowAgentOrganizations());
      Player.step();
  
      // start all self agents and run them in discrete time slices
      for (final IInnerOrganization self : allAASISAgents) {
        self.run();
      }
    }


    /**
     * Creates agents and returns them as a list.
     *
     * @param agentFolders the File[] of self organization agent folders
     * @return - an ArrayList<{@code}IAgentInternalOrganization}>
     */
  synchronized static ArrayList<IInnerOrganization> createAgentsAndCounts(final File[] agentFolders) {
    LOG.info("========= CREATING ALL AGENTS FOR {} =========\n.", RunManager.getTestCaseName().toUpperCase());

    final ArrayList<IInnerOrganization> listSelfOrgs = new ArrayList<IInnerOrganization>();
    for (final File agentFolder : agentFolders) {
      LOG.debug("reading agentFolder={}", agentFolder);
      LOG.debug("reading agentFolder.getAbsolutePath()={}", agentFolder.getAbsolutePath());
      LOG.debug("reading agentFolder.getName()={}", agentFolder.getName());
      try {
        //  IPersonaOrganization agentOrg = SelfOrganizationFactory.create(agentFolder.getAbsolutePath(), agentFolder.getName());
        IInnerOrganization agentOrg = edu.nwmissouri.isl.aasis.case.self.InnerOrganizationFactory
            .create(agentFolder.getAbsolutePath(), agentFolder.getName());
        LOG.debug("agentOrg={}", agentOrg);
        listSelfOrgs.add(agentOrg);
        RunManager.add(agentOrg.getAgentType(), agentOrg.getName());
      } catch (final Exception e) {
        LOG.error("ERROR: Could not initialize agent {}. {}", agentFolder, e.getMessage());
        throw e;
      }
    }
    //RunManager.displayCounts();
    LOG.info("==========  {} AGENTS CREATED FROM SPECIFICATION FILES  ==========\n\n", listSelfOrgs.size());
    return listSelfOrgs;
  }

/**
    * Initialize the goal models. Gets the top goal instance parameters, determines the initial change list of goal
    * modifications, updates the initial active goals, and sets the roles.  We can do it all in the constructors where
    * it's hidden, or do it out here in the open for testing, etc.
    *
    * @param allDeviceMAS - the list of IAgentOrganizations
    */
    synchronized static void initializeGoalGuidelines(final ArrayList<IInnerOrganization> allDeviceMAS) {
      for (IInnerOrganization deviceMASAgent : allDeviceMAS) {
        deviceMASAgent.loadTopGoalGuidelines();
      }
      LOG.info("========= {} INITIAL GOALS SET - READY TO CREATE PERSONA THREADS AND RUN  =========\n\n",
          allDeviceMAS.size());
    }



      /**
   * Load the environment objects in all the self organizations.
   *
   * @param allDeviceMAS - the {@code ArrayList} of {@code SelfOrganization}.
   */
  static void loadEnvironmentObjects(ArrayList<IInnerOrganization> allDeviceMAS) {
    for (IInnerOrganization self : allDeviceMAS) {
      self.loadObjectFile();
    }
    LOG.info("========= {} AGENTS INITIALIZED WITH ENVIRONMENT OBJECTS ==========\n\n", allDeviceMAS.size());
  }

    /**
     * Load the persona in all the self organizations.
     *
     * @param allAASISAgents - the {@code ArrayList} of {@code IAgentInternalOrganization}.
     */
  static void loadPersona(ArrayList<IInnerOrganization> allAASISAgents) {
    for (IInnerOrganization agent : allAASISAgents) {
      agent.loadAgentFile();
      LOG.info("\nEVENT: AASIS AGENT_INITIALIZED. =========  AGENT {} INITIALIZED WITH SUB AGENTS ===\n", agent.getName());

    }
    LOG.info("EVENT: AGENT_INITIALIZATION_COMPLETE. =========  {} AGENTS INITIALIZED WITH SUB AGENTS ==========\n\n",
        allAASISAgents.size());
  }

    private static void DisplayCommunicationsInBrowser(boolean isShown) {
      if (isShown) {
        if (java.awt.Desktop.isDesktopSupported()) {
          java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
          if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
            try {
              java.net.URI uri = new java.net.URI("http://localhost:15672/");
              desktop.browse(uri);
            } catch (Exception e) {
              // Handle exception
            }
          }
        }
      }
    }

     /**
     * Return a list of all self agent folders in this test case.
     *
     * @param testCaseFolder - the root test case folder
     * @return File[] of subfolders for self agents
     */
  private static File[] getTestCaseSelfFolders(final String testCaseFolder) {
    if (debug)
      LOG.debug("Getting folders from node {}", testCaseFolder);

    final File[] devices = getAllFoldersOnDevice(new File(testCaseFolder));
    final ArrayList<File> list = new ArrayList<>();
    for (final File device : devices) {
      if (debug)
        LOG.debug("Getting folders from node {}", device.getAbsolutePath());

      final File[] allOrganizationFolders = getAllFoldersOnDevice(device);
      if (debug)
        LOG.debug("    {} agent folders found on node {}", allOrganizationFolders.length, device);
      for (final File orgFolder : allOrganizationFolders) {
        if (orgFolder.getName().startsWith("self")) {
          list.add(orgFolder);
        }
      }
    }
    final File[] selfFolders = list.toArray(new File[list.size()]);
    LOG.info("Total: {} self agent folders found. First is {}", selfFolders.length, selfFolders[0].getName());
    return selfFolders;
  }

    /**
    * Get all subfolders under a given device folder.
    *
    * @param deviceDir - the File folder for the device (JVM)
    * @return File[] with the subfolders
    */
  private static File[] getAllFoldersOnDevice(final File deviceDir) {
    final FileFilter selfFolderFilter = File::isDirectory;
    return deviceDir.listFiles(selfFolderFilter);
  }

    private static void initLogging() {
      if (stepInLauncher) {
        Player.setStepMode(Player.StepMode.STEP_BY_STEP);
      }
  
      // assume SLF4J bound to logback in the current environment
      Context lc = (Context) LoggerFactory.getILoggerFactory();
      LOG.info("========= GOT LOGGER CONTEXT  =========\n\n");
  
      // print logback's internal status
      StatusPrinter.print(lc);
      LOG.info("========= DISPLAYED LOGBACK STATUS  =========\n\n");
  
      // delete any existing logfiles
      cleanLogFiles();
      LOG.info("========= DELETED LOG FILES  =========\n\n");
    }

    private static void cleanLogFiles() {
        try {
            String curDir = System.getProperty("user.dir");
            FileUtils.cleanDirectory(new File(String.format("%s//logs", curDir)));
        } catch (Exception ex) {
            LOG.debug("Some logfiles in use. Cannot delete.");
        }
    }

    /**
     * Calls step. Allows shutting off the stepping just in the launcher file if stepInLauncher == false.
     */
    private static void step() {
        // change this as desired to test parts of the system
        if (stepInLauncher) {
            Player.step();
        }
    }

    
    @Override
    public String toString() {
        return "Launcher{" +
                "INSTANCE=" + INSTANCE +
                ", stepInLauncher=" + stepInLauncher +
                '}';
    }

    

  }

    
