package edu.nwmissouri.isl.aasis.case.org.poker.plans.deal_poker;

import edu.nwmissouri.isl.aasis.case.config.RunManager;
import edu.nwmissouri.isl.aasis.case.org.poker.PokerTerminationCriteria;
import edu.nwmissouri.isl.aasis.case.org.poker.capabilities.admin.DealPokerCapability;
import edu.nwmissouri.isl.aasis.case.org.poker.capabilities.admin.PokerAdminCapability;
import edu.nwmissouri.isl.aasis.case.org.poker.capabilities.connect.IPokerConnectCapability;
import edu.nwmissouri.isl.aasis.case.org.poker.capabilities.participate.IPokerCommunicationCapability;
import edu.nwmissouri.isl.aasis.case.org.poker.messages.IPokerMessageContent;
import edu.nwmissouri.isl.aasis.case.ec_cap.DateTimeCapability;
import edu.nwmissouri.isl.aasis.case.primary.capabilities.participate.ParticipateCapability;
import edu.ksu.cis.macr.obaa_pp.ec.base.IExecutor;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IExecutablePlan;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IPlanState;
import edu.ksu.cis.macr.organization.model.InstanceGoal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.TreeMap;

/**
 * This is the main step in the plan. If it receives a stop messages, it will move to the stop
 * state.
 */
public enum Deal_Poker implements IPlanState<Deal_Poker_Plan> {
  INSTANCE;
  private static final Logger LOG = LoggerFactory.getLogger(Deal_Poker.class);
  private static final boolean debug = false;

  @Override
  public void Enter(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
    // Nothing
  }

  @Override
  public void Execute(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
    if (debug)
      LOG.debug("Starting with instance goal: {}.", ig);
    Objects.requireNonNull(ig);
    Objects.requireNonNull(ec);
    Objects.requireNonNull(ec.getCapability(DealPokerCapability.class), "Role requires DealPokerCapability.");
    Objects.requireNonNull(ec.getCapability(DateTimeCapability.class), "Role requires DateTimeCapability.");

    try {
      plan.heartBeat(this.getClass().getName(),
          String.format("Unconnected=%s. All registered=%s.",
              ec.getCapability(IPokerConnectCapability.class).getUnconnectedChildren().toString(),
              ec.getCapability(PokerAdminCapability.class).processingRegistrationIsComplete()));
    } catch (Exception ex) {
      plan.heartBeat(this.getClass().getName());
    }

    if (!ec.getCapability(PokerAdminCapability.class).isAllRegistered()) {
      LOG.info("{} all participants no longer fully registered. Changing state.", ec.getUniqueIdentifier().toString());
      plan.getStateMachine().changeState(Deal_Poker_Processing_Registrations.INSTANCE, ec, ig);
    } else {

      // if I'm the "real" persona in the agent, do real work
      if (ec.getCapability(ParticipateCapability.class) != null) {

        // initialize my guidelines as defined in the parametrized instance goal
        ec.getCapability(DealPokerCapability.class).init(ig);
        ec.getCapability(IPokerCommunicationCapability.class).init(ig);
        if (debug)
          LOG.debug("Initialized capabilities.");

        // get current timeSlice from datetime capability
        int currentTimeSlice = Objects.requireNonNull(
            ec.getCapability(DateTimeCapability.class).getTimeSlicesElapsedSinceStart(),
            "ERROR: Need a timeSlice to get data.");
        if (debug)
          LOG.debug("Current time slice ={}. ", currentTimeSlice);

        if (!ec.getCapability(DealPokerCapability.class).isDoneIterating()) {
          LOG.info("{} all participants have registered. Ready to start.", ec.getUniqueIdentifier().toString());

          TreeMap<String, IPokerMessageContent> inputs = ec.getCapability(IPokerCommunicationCapability.class)
              .getBidMessages();
          if (inputs != null) {
            if (debug)
              LOG.debug("Getting bids. {} bids so far = {}", inputs.size(), inputs);

            if (ec.getCapability(DealPokerCapability.class).allBidsReceived(inputs)) {
              LOG.info("EVENT: ALL_BIDS_RECEIVED. Num bids = {} for purchase time slice = {}.", inputs.size(),
                  inputs.firstEntry().getValue().getPurchaseTimeSlice());

              IPokerMessageContent summaryContent = ec.getCapability(DealPokerCapability.class).evaluateHand(inputs);
              LOG.info("Summary content= {}", summaryContent);

              boolean sent = ec.getCapability(IPokerCommunicationCapability.class).forwardToSelf(summaryContent);
              if (debug)
                LOG.debug("Forward to self for review. Summary content sent = {}", sent);

              if (sent) {
               LOG.info("EVENT: FORWARDED UP RESULTS.({}).", ec.getUniqueIdentifier());

              }
              LOG.debug("Iteration complete.");
            }
          }
        }
      }
    }
    if ((RunManager.isStopped())) {
      plan.getStateMachine().changeState(Deal_Poker_Stop.INSTANCE, ec, ig);
    }
  }

  @Override
  public void Exit(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
    // Nothing
  }
}
