package gr.aueb.cs.infosec.model;

import java.util.ArrayList;
import java.util.List;

public class HourlyEntry {

  private String date;
  private String hour;
  private String link;
  private double flowLevel;
  private boolean type;
  private List<Double> flows;
  private String[] nodes;

  /**
   * Constructor
   */
  public HourlyEntry() {
    this.flows = new ArrayList<Double>();
    this.nodes = new String[3];
  }

  /**
   * Set the entry's type
   *
   * @param type
   */
  public void setType(boolean type) {
    this.type = type;
  }

  /**
   * Get the String representation of this entry's type
   * 
   * @return
   */
  public String getTypeStr() {
    return (this.type) ? "good" : "bad";
  }

  /**
   * Get this entry's type
   *
   * @return
   */
  public boolean getType() {
    return this.type;
  }

  /**
   * Set this entry's flow level
   *
   * @param d
   */
  public void setFlowLevel(double d) {
    this.flowLevel = d;
  }

  /**
   * Get this entry's flow level
   *
   * @return
   */
  public double getFlowLevel() {
    return this.flowLevel;
  }

  /**
   * Set this entry's hour. Entries vary from 1 to 24
   *
   * @param hour
   */
  public void setHour(String hour) {
    this.hour = hour;
  }

  /**
   * Get this entry's hour
   *
   * @return
   */
  public String getHour() {
    return this.hour;
  }

  /**
   * Set the current link
   *
   * @param link
   */
  public void setLink(String link) {
    this.link = link;
  }

  /**
   * Get the current link
   *
   * @return
   */
  public String getLink() {
    return this.link;
  }

  /**
   * Set the date
   *
   * @param date
   */
  public void setDate(String date) {
    this.date = date;
  }

  /**
   * Get the date
   *
   * @return
   */
  public String getDate() {
    return this.date;
  }

  /**
   * Add a specified flow rate to the list
   *
   * @param flow
   */
  public void addFlow(double flow) {
    this.flows.add(flow);
  }

  /**
   * Get the average hourly flow from the quarter entries.
   *
   * @return
   */
  public double getAverageFlow() {
    if (this.flows.size() == 0)
      return 0;
    double sum = 0;
    for (double flow : this.flows) {
      sum += flow;
    }
    return sum / this.flows.size();
  }

  /**
   * Set the nodes
   *
   * @param split_nodes
   */
  public void setNodes(Node[] split_nodes) {
    this.nodes[0] = split_nodes[0].getName();
    this.nodes[1] = split_nodes[0].getFirstConnectedNode();
    this.nodes[2] = split_nodes[0].getSecondConnectedNode();
  }

  /**
   * Get the nodes
   *
   * @return
   */
  public String[] getNodes() {
    return this.nodes;
  }
}
