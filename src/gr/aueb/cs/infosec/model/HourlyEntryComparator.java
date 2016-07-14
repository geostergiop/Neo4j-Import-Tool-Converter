package gr.aueb.cs.infosec.model;

import java.util.Comparator;

public class HourlyEntryComparator implements Comparator<HourlyEntry> {

  @Override
  public int compare(HourlyEntry o1, HourlyEntry o2) {
    Double averageFlow1 = new Double(o1.getAverageFlow());
    Double averageFlow2 = new Double(o2.getAverageFlow());
    return averageFlow1.compareTo(averageFlow2);
  }

}
