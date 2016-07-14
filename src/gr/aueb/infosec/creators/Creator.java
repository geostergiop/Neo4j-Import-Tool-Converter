package gr.aueb.infosec.creators;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.aueb.cs.infosec.model.HourlyEntry;
import gr.aueb.cs.infosec.model.HourlyEntryComparator;
import gr.aueb.cs.infosec.model.Node;
import gr.aueb.cs.infosec.util.Scaling;
import gr.aueb.cs.infosec.util.Util;

public abstract class Creator {

  // input file
  private String input;
  // output file
  private String output;
  // reader
  private BufferedReader in;
  // writer
  private BufferedWriter out;
  // storage, to check for duplicate entries
  private Map<String, String> storage;
  // storage, for the flow rates
  private Map<String, List<Double>> flowRates;
  // storage for the quarter flow rates
  private Map<String, List<HourlyEntry>> hourlyFlowRates;

  /**
   * Constructor
   *
   * @param input
   * @param output
   */
  public Creator(String input, String output) {
    this.input = input;
    this.output = output;
    this.storage = new HashMap<String, String>();
    this.flowRates = new HashMap<String, List<Double>>();
    this.hourlyFlowRates = new HashMap<String, List<HourlyEntry>>();
    this.initialize();
  }

  /**
   * Initializes the reader, the writer and the storage
   */
  public void initialize() {
    try {
      this.in = new BufferedReader(new FileReader(new File(this.input)));
      this.out = new BufferedWriter(new FileWriter(new File(this.output)));
    } catch (IOException io) {
      io.printStackTrace();
    }
  }

  /**
   * Closes the reader and the writer
   */
  @Override
  public void finalize() {
    try {
      if (this.out != null) {
        out.close();
      }
      if (this.in != null) {
        in.close();
      }
    } catch (IOException io) {
      io.printStackTrace();
    }
    this.storage.clear();
    this.storage = null;
    this.flowRates.clear();
    this.flowRates = null;
    this.hourlyFlowRates.clear();
    this.hourlyFlowRates = null;
  }

  /**
   * This method will be implemented by the child classes Determines how the input file is going to
   * be processed in order to create the output file.
   */
  public abstract void process();

  /**
   * Get the input file
   *
   * @return
   */
  public String getInput() {
    return this.input;
  }

  /**
   * Get the output file
   *
   * @return
   */
  public String getOutput() {
    return this.output;
  }

  /**
   * Get the reader
   *
   * @return
   */
  public BufferedReader getReader() {
    return this.in;
  }

  /**
   * Get the writer
   *
   * @return
   */
  public BufferedWriter getWriter() {
    return this.out;
  }

  /**
   * Get the storage
   *
   * @return
   */
  public Map<String, String> getStorage() {
    return this.storage;
  }

  /**
   * Get the flow storage
   *
   * @return
   */
  public Map<String, List<Double>> getFlowRateStorage() {
    return this.flowRates;
  }

  /**
   * Get the hourly flow storage
   *
   * @return
   */
  public Map<String, List<HourlyEntry>> getHourlyFlowRateStorage() {
    return this.hourlyFlowRates;
  }

  /**
   * Given an input line from the csv dataset file, this method processes the line and returns the
   * three nodes participating in the corresponding link.
   *
   * @param inputLine
   * @return a Node array containing the nodes
   */
  public Node[] splitNodeNames(String inputLine) {
    // Node[] results = new Node[3];
    Node[] results = new Node[2];
    // first node
    String first = inputLine.split(",")[1].split("between")[0].replaceAll("^\\s+|\\s+$", "");
    // 2nd node
    String second =
        inputLine.split(",")[1].split("and")[0].split("between")[1].replaceAll("^\\s+|\\s+$", "");
    // 3rd node
    String temp = inputLine.split(",")[1].split("and")[1];
    String third = temp.substring(0, temp.indexOf("(" + Util.getLinkName(inputLine) + ")"))
        .replaceAll("^\\s+|\\s+$", "");
    // Testing : first entry is not needed
    // TODO : Test again
    // results[0] = new Node(first, second, third);
    results[0] = new Node(second, first, third);
    results[1] = new Node(third, first, second);
    return results;
  }

  /**
   * Returns the impact for the given link. Impact = max flow - min flow
   *
   * @param link
   * @return
   */
  public double getImpact(String link) {
    List<Double> flowz = this.flowRates.get(link);
    if (flowz == null)
      return -1;
    return Collections.max(flowz, null) - Collections.min(flowz, null);
  }

  /**
   * This method sets the flow level for all the current entries
   */
  public void setLevelingForEachHourlyEntry() {
    // for each link entered
    for (String nextLink : this.hourlyFlowRates.keySet()) {
      try {
        double maxFlow =
            (Collections.max(this.hourlyFlowRates.get(nextLink), new HourlyEntryComparator()))
                .getAverageFlow();
        double minFlow =
            (Collections.min(this.hourlyFlowRates.get(nextLink), new HourlyEntryComparator()))
                .getAverageFlow();
        // scale on a [1, 5] scale with 5 being the highest level
        Scaling scaling = new Scaling(minFlow, maxFlow, 1, 5);

        // Set the flow level for each hourly entry about the specific link
        for (HourlyEntry he : this.hourlyFlowRates.get(nextLink)) {
          he.setFlowLevel(Math.floor(scaling.rescale(he.getAverageFlow())));
        }
      } catch (Exception e) {
        System.out.println(nextLink);
        System.out.println(this.hourlyFlowRates.get(nextLink) == null);
      }
    }
  }

  /**
   * Determine whether each entry is "good" or bad. Every entry is feasible since the flows are > 0
   */
  public void determineEntries() {
    for (String nextLink : this.hourlyFlowRates.keySet()) {
      for (HourlyEntry he : this.hourlyFlowRates.get(nextLink)) {
        double x_r = he.getAverageFlow();
        he.setType(this.calculateSum(x_r, this.hourlyFlowRates.get(nextLink)));
      }
    }
  }

  /**
   * Calculate the sum determining whether an entry is good or bad
   *
   * @param x_r
   * @param list
   * @return
   */
  private boolean calculateSum(double x_r, List<HourlyEntry> list) {
    double sum = 0;
    for (HourlyEntry he : list) {
      sum += (he.getAverageFlow() - x_r) / x_r;
    }
    return sum < 0;
  }
}
