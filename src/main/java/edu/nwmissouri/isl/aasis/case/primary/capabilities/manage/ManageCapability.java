package edu.nwmissouri.isl.aasis.case.primary.capabilities.manage;

import edu.ksu.cis.macr.aasis.agent.persona.AbstractOrganizationCapability;
import edu.ksu.cis.macr.aasis.agent.persona.IOrganization;
import edu.ksu.cis.macr.aasis.agent.persona.IPersona;
import edu.ksu.cis.macr.aasis.common.IConnections;
import edu.ksu.cis.macr.goal.model.InstanceParameters;
import edu.ksu.cis.macr.organization.model.InstanceGoal;
import edu.ksu.cis.macr.organization.model.identifiers.StringIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Objects;


/**
 The {@code ManageHomeCapability} provides the functionality necessary to manage power quality guidelines).
 */
public class ManageCapability extends AbstractOrganizationCapability {
  private static final Logger LOG = LoggerFactory.getLogger(ManageCapability.class);
  private static final boolean debug = false;

  private InstanceParameters params;
  private IPersona owner;


  /**
   Construct a new {@code ManageHomeCapability} instance.

   @param owner - the agent possessing this capability.
   @param org - the immediate organization in which this agent operates.
   */
  public ManageCapability(final IPersona owner, final IOrganization org) {
    super(ManageCapability.class, owner, org);
    this.owner = Objects.requireNonNull(owner);
  }

  @Override
  public double getFailure() {
    return 0;
  }


  /**
   Get the parameters from this instance goal and use them to initialize the capability.

   @param instanceGoal - this instance of the specification goal
   */
  public synchronized void initializeFromGoal(InstanceGoal<?> instanceGoal) {
    // Get the parameter values from the existing active instance goal
    final InstanceParameters params = Objects.requireNonNull((InstanceParameters) instanceGoal.getParameter());
   
     IConnections pc = (IConnections) params.getValue(StringIdentifier.getIdentifier("parentConnections"));
    if (pc != null) {
      if (debug) LOG.debug("{} authorized connections to parents.", pc.getListConnectionGuidelines().size());
    }
  }

  public boolean isWorkingAsMaster() {
    return this.owner.getPersonaControlComponent().isMaster();
  }

  public boolean isWorkingAsSlave() {
    return this.owner.getPersonaControlComponent().isSlave();
  }

  @Override
  public synchronized void reset() {
  }

  /**
   Get the parameters from this instance goal and use them to set the goal-specific guidelines.

   @param instanceGoal - this instance of the specification goal
   */
  public synchronized void setGuidelines(InstanceGoal<?> instanceGoal) {
    // Get the parameter values from the existing active instance goal
    final InstanceParameters params = Objects.requireNonNull((InstanceParameters) instanceGoal.getParameter());
    }


  @Override
  public Element toElement(final Document document) {
    final Element capability = super.toElement(document);
    return capability;
  }


}
