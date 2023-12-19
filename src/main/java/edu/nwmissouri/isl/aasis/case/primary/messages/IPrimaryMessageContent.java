package edu.nwmissouri.isl.aasis.case.primary.messages;

import java.io.IOException;

/**
 {@code IPrimaryMessageContent} provides an interface for defining the content in an {@code IPrimaryMessage}.
 */
public interface IPrimaryMessageContent {

  void add(IPrimaryMessageContent item);

  Object deserialize(byte[] bytes) throws Exception;

  long getTimeSlice();

  void setTimeSlice(long timeSlice);

  boolean isEmpty();

  byte[] serialize() throws IOException;
}
