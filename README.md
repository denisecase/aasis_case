# AASIS Case Agent

Northwest Missouri State University

AASIS Agent representing Denise Case

Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

Denise Case, Assistant Professor
dcase@nwmissouri.edu

## About this project

   This project provides an example implementation of an intelligent system.

   This project uses OMACS, OBAA++, and GMoDS and is based on the AASIS architecture.

## Dependencies

Install Chocolatey, a package manager for windows.

Install the Gradle build tool.

Install 64-bit JDK 1.8 with auto-update

We use Git for version control.

If Windows, install both Git for Windows and TortoiseGit.  

Install Intellij IDEA Community Edition or VS Code

Install 64-bit Eclipse with agentTool III for goal and role models.

```PowerShell
choco intall gradle
choco install jdk8
choco install git
choco install tortoisegit
choco install eclipse-standard-kepler
choco install visualstudiocode
```

The following Eclipse plugins are used for modifying goal & role 
models and for running Spock tests, respectively:
	
AgentTool3 Eclipse plugin (Core and Process Editor) from         http://agenttool.cis.ksu.edu/
        
Groovy Eclipse plugin from        http://dist.springsource.org/release/GRECLIPSE/e4.3/

## Eclipse Workspace folders
 
Eclipse uses workspace folders to hold IDE configuration information that 
we do not want shared across machines. 

Our current location is a 
sub folder of the associated workspace. See:
http://eclipse.dzone.com/articles/eclipse-workspace-tips
for more information. 
        
## Code
	
Go to a folder where you want to keep your code, say a "projects" folder.

Use Git to clone the code from this repository.

Open a PowerShell window in this folder and run:
  
```
gradlew run
```

When everything is running successfully, you'll see screens appear showing 	agents and assigned tasks.

To create documentation, open a command window and run

```
gradlew javaDoc
```

The project comes with its own  gradle wrapper (included). Installing it locally allows for updates and more exploration.

## Configuration files

The following configuration files are required:

- Agent.xml. local organization agents and their capabilities.

- Initialize.xml. provides custom goal parameters.

- GoalModel.goal. describes the objectives of the system

- RoleModel.role. describes capabilities needed to play roles and roles that agents can play to achieve specific goals.
                          
The utils package has builder programs to assist with auto-generation of the many agent and initialize files used in the test cases.

## Editing goal and role models

Two of the configuration files - the goal models (.goal) and the
role models (.role) can be easily edited using the recently updated
agentTool3 modeling plugins.

See http://agenttool.cis.ksu.edu for more information.

## Adding new behavior

Please reference AO-MASE and associated papers for more information about this project as well as the process for adding new behavior to this system.

https://bitbucket.org/professorcase/aasis_tutorial_paper
https://bitbucket.org/professorcase/aasis_case
https://bitbucket.org/professorcase/aasis_chen

