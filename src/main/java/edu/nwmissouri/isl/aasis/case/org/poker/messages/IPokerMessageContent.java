package edu.nwmissouri.isl.aasis.case.org.poker.messages;

import java.io.IOException;

/**
 * {@code IPokerMessageContent} provides an interface for defining the payload content in an {@code IPokerMessage}.
 */
public interface IPokerMessageContent {

  Object deserialize(byte[] bytes) throws Exception;

  String getDealer();

  long getPurchaseTimeSlice();

  byte[] serialize() throws IOException;

}
