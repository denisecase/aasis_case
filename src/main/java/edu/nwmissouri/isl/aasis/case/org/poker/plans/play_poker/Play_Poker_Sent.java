package edu.nwmissouri.isl.aasis.case.org.poker.plans.play_poker;

import edu.nwmissouri.isl.aasis.case.config.RunManager;
import edu.nwmissouri.isl.aasis.case.org.poker.capabilities.participate.IPokerCommunicationCapability;
import edu.nwmissouri.isl.aasis.case.org.poker.capabilities.participate.PokerParticipateCapability;
import edu.nwmissouri.isl.aasis.case.primary.capabilities.participate.DateTimeCapability;
import edu.ksu.cis.macr.obaa_pp.ec.base.IExecutor;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IExecutablePlan;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IPlanState;
import edu.ksu.cis.macr.organization.model.InstanceGoal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * The last step in the plan. It allows for any functionality needed when exiting the plan.
 */
public enum Play_Poker_Sent implements IPlanState<Play_Poker_Plan> {
  INSTANCE;
  private static final Logger LOG = LoggerFactory.getLogger(Play_Poker_Sent.class);
  private static final boolean debug = false;

  @Override
  public synchronized void Enter(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
    // Nothing
  }

  @Override
  public synchronized void Execute(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
    if (debug)
      LOG.debug("Starting with instance goal: {}.", ig);
    Objects.requireNonNull(ig);
    Objects.requireNonNull(ec);

    plan.heartBeat(this.getClass().getName());

    if (!ec.getCapability(PokerParticipateCapability.class).isRegistered()) {
      LOG.info("{} no longer registered with parent. Changing state.", ec.getUniqueIdentifier().toString());
      plan.getStateMachine().changeState(Play_Poker_Registering.INSTANCE, ec, ig);
    } else {
      LOG.info("{} registered and bid sent. Waiting for response.", ec.getUniqueIdentifier().toString());

      // get current timeslice
      int currentTS = Objects.requireNonNull(
          ec.getCapability(DateTimeCapability.class).getTimeSlicesElapsedSinceStart(),
          "ERROR: Need a current time to bid in auctions.");
      if (debug)
        LOG.debug("Current time slice is {}.", currentTS);

      // initialize my guidelines as defined in the parametrized instance goal
      ec.getCapability(IPokerCommunicationCapability.class).init(ig);
      if (debug)
        LOG.debug("Initialized IPokerCommunicationCapability.");
     
      boolean done = ec.getCapability(IPokerCommunicationCapability.class).getBidResponse();
      if (done) {
        LOG.debug("Participation complete.");
        plan.getStateMachine().changeState(Play_Poker_Stop.INSTANCE, ec, ig);
      }

    }

    if ((RunManager.isStopped())) {
      plan.getStateMachine().changeState(Play_Poker_Stop.INSTANCE, ec, ig);
    }

  }

  @Override
  public synchronized void Exit(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
    // Nothing
  }
}
