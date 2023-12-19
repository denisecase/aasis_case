package edu.nwmissouri.isl.aasis.case.org.poker.plans.play_poker;

import edu.ksu.cis.macr.aasis.common.IConnections;
import edu.nwmissouri.isl.aasis.case.config.RunManager;
import edu.nwmissouri.isl.aasis.case.org.poker.capabilities.participate.IPokerCommunicationCapability;
import edu.nwmissouri.isl.aasis.case.org.poker.capabilities.participate.IPokerCapability;
import edu.nwmissouri.isl.aasis.case.org.poker.capabilities.participate.PokerParticipateCapability;
import edu.nwmissouri.isl.aasis.case.org.poker.messages.IPokerMessage;
import edu.nwmissouri.isl.aasis.case.org.poker.messages.IPokerMessageContent;
import edu.nwmissouri.isl.aasis.case.primary.capabilities.participate.DateTimeCapability;
import edu.ksu.cis.macr.obaa_pp.ec.base.IExecutor;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IExecutablePlan;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IPlanState;
import edu.ksu.cis.macr.organization.model.InstanceGoal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * This is the main managing step in the auction power plan. If it receives a stop messages, it will move to the stop
 * state.
 */
public enum Play_Poker implements IPlanState<Play_Poker_Plan> {
  INSTANCE;
  private static final Logger LOG = LoggerFactory.getLogger(Play_Poker.class);
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
    Objects.requireNonNull(ec.getCapability(IPokerCapability.class), "Role requires IPokerCapability.");

    plan.heartBeat(this.getClass().getName());

    // initialize my guidelines as defined in the parametrized instance goal
    ec.getCapability(IPokerCapability.class).init(ig);
    if (debug)
      LOG.debug("Initialized IPokerCapability.");

    ec.getCapability(PokerParticipateCapability.class).init(ig);
    if (debug)
      LOG.debug("Initialized PokerParticipateCapability.");

    ec.getCapability(IPokerCommunicationCapability.class).init(ig);
    if (debug)
      LOG.debug("Initialized PokerCommunicationCapability.");

    if (!ec.getCapability(PokerParticipateCapability.class).isRegistered()) {
      LOG.debug("{} no longer registered with parent. Changing state.", ec.getUniqueIdentifier().toString());
      plan.getStateMachine().changeState(Play_Poker_Registering.INSTANCE, ec, ig);
      return;
    }
    LOG.info("{} registered and available.", ec.getUniqueIdentifier().toString());

    // get current timeslice
    int currentTimeSlice = Objects.requireNonNull(
        ec.getCapability(DateTimeCapability.class).getTimeSlicesElapsedSinceStart(),
        "ERROR: Need a current time slice.");
    if (debug)
      LOG.debug("Current time slice is {}.", currentTimeSlice);

    // initialize my guidelines as defined in the parametrized instance goal
    ec.getCapability(IPokerCommunicationCapability.class).init(ig);
    if (debug)
      LOG.debug("Initialized IPokerCommunicationCapability.");

    // create auction message only once when equal (just for initial testing)
    IPokerMessage msg = ec.getCapability(IPokerCapability.class).createPokerMessage(currentTimeSlice);
    if (debug)
      LOG.debug("Message = {}", msg);

    boolean sent = ec.getCapability(IPokerCommunicationCapability.class).sendRemoteMessage(msg);
    if (sent) {
      LOG.info("EVENT: BID_SENT. Bidder = {} sent message. Message = {}", ec.getUniqueIdentifier().toString(),
          msg.toString());
      plan.getStateMachine().changeState(Play_Poker_Sent.INSTANCE, ec, ig);
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
