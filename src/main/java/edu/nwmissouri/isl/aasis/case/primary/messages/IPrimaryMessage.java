package edu.nwmissouri.isl.aasis.case.primary.messages;

import edu.ksu.cis.macr.aasis.agent.cc_message.IBaseMessage;
import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;

import java.io.IOException;

/**
 {@code IPrimaryMessage} provides an interface for describing messages between agents containing {@code IPrimaryMessageContent}.
 */
public interface IPrimaryMessage extends IBaseMessage<PrimaryPerformative> {

  /**
   Deserialize the message.

   @param bytes - an array of bytes
   @return the deserialized message
   @throws IOException - Handles any IO Exceptions
   @throws ClassNotFoundException - Handles any ClassNotFound Exceptions
   */
  @Override
  Object deserialize(byte[] bytes) throws Exception;

  /**
   Serialize the message.

   @return a byte array with the contents.
   @throws IOException - If an I/O error occurs.
   */
  @Override
  byte[] serialize() throws IOException;

    /**
     /** Constructs a new instance of {@code PrimaryMessage}.

     @param sender - String name of the agent sending the message
     @param receiver - String name of the agent to whom the message is sent
     @param performative - the {@code PrimaryPerformative} indicating the type of message
     @param content - the message content
     @return the IPrimaryMessage created
     */
    public static IPrimaryMessage createLocal(final UniqueIdentifier sender, final UniqueIdentifier receiver,
                                            final PrimaryPerformative performative, final Object content) {
        return new PrimaryMessage(sender, receiver, performative, content);
    }

    /**
     /** Constructs a new instance of {@code PrimaryMessage}.

     @param senderString - String name of the agent sending the message
     @param receiverString - String name of the agent to whom the message is sent
     @param performative - the {@code PrimaryPerformative} indicating the type of message
     @param content - the message content
     @return - the IPrimaryMessage created
     */
    public static IPrimaryMessage createRemote(final String senderString, final String receiverString,
                                             final PrimaryPerformative performative, final Object content) {
        return new PrimaryMessage(senderString, receiverString, performative, content);
    }
}
