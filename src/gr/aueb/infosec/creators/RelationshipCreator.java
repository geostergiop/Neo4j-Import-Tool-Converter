package gr.aueb.infosec.creators;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import gr.aueb.cs.infosec.model.HourlyEntry;
import gr.aueb.cs.infosec.model.Node;
import gr.aueb.cs.infosec.util.Util;

public class RelationshipCreator extends Creator {

  // output csv header
  // TODO : Put this in properties file
  private final String CSV_HEADER =
      ":START_ID,edge_name,road,:END_ID,:TYPE,date,hour,flow,impactLevel,type";
  // relationship type for the neo4j database
  private final String RELATIONSHIP_TYPE = "Road_Congestion";
  // temporary variable for current hourly entry
  private HourlyEntry currentHourlyEntry;
  // hour counter
  private int hour = 0;
  // quarter counter / counter for the read lines
  private int counter = 0;

  /**
   * Constructor
   *
   * @param input
   * @param output
   */
  public RelationshipCreator(String input, String output) {
    super(input, output);
  }

  @Override
  public void process() {
    // add execution time
    long startTime = System.currentTimeMillis();
    String nextLine = null;
    BufferedReader in = this.getReader();
    int skipped = 0;
    try {
      // do not read the header
      in.readLine();
      while ((nextLine = in.readLine()) != null) {
        this.counter++;
        // we wanna keep only 1 and 2 quality data and
        // skip all the data having data quality 1, which do not have a flow value
        if (Util.getDataQuality(nextLine) > 2 || Util.getFlowRate(nextLine) == -1) {
          skipped++;
          continue;
        }

        Node[] split_nodes = this.splitNodeNames(nextLine);
        String link_name = Util.getLinkName(nextLine);
        String date = Util.getDate(nextLine);
        double flow = Util.getFlowRate(nextLine);

        if (this.getStorage().containsKey(link_name) && this.counter != 4
            && this.currentHourlyEntry != null) {
          this.getFlowRateStorage().get(link_name).add(flow);
          this.currentHourlyEntry.addFlow(flow);
          continue;
        } else {

          // do not overwrite
          if (this.getStorage().get(link_name) == null) {
            this.getStorage().put(link_name, split_nodes[0].getName());
          }
          // do not overwrite the current list
          if (this.getFlowRateStorage().get(link_name) == null) {
            this.getFlowRateStorage().put(link_name, new ArrayList<Double>());
          }
          this.getFlowRateStorage().get(link_name).add(flow);
          // TODO : Check this one again
          if (this.getHourlyFlowRateStorage().get(link_name) == null) {
            this.getHourlyFlowRateStorage().put(link_name, new ArrayList<HourlyEntry>());
          }
          if (this.currentHourlyEntry != null) {
            this.getHourlyFlowRateStorage().get(link_name).add(this.currentHourlyEntry);
            this.counter = 0;
            this.currentHourlyEntry = null;
          } else {
            this.hour++;
            if (hour == 25)
              hour = 1;
            this.currentHourlyEntry = new HourlyEntry();
            this.currentHourlyEntry.setHour(Integer.toString(this.hour));
            this.currentHourlyEntry.addFlow(flow);
            this.currentHourlyEntry.setDate(date);
            this.currentHourlyEntry.setNodes(split_nodes);
            this.currentHourlyEntry.setLink(link_name);
          }
        }
        // right here all the lines referring to the link have been read so we write the results
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    // set the flow levels to all entries
    this.setLevelingForEachHourlyEntry();
    // determine whether each entry is "bad" or "good"
    this.determineEntries();
    // write the output file
    this.write();
    System.out.println(
        "Parsed file : " + this.getInput() + " in " + (System.currentTimeMillis() - startTime));
    System.out.println("Skipped : " + skipped);
  }

  /***
   * Write the output file
   *
   * @param currentLink
   * @param currentHourlyEntry
   * @throws IOException
   */

  private void write() {
    BufferedWriter out = this.getWriter();
    try {
      // write the corresponding header to the output
      out.write(CSV_HEADER);
      out.write("\n");

      for (String nextLink : this.getHourlyFlowRateStorage().keySet()) {
        for (HourlyEntry he : this.getHourlyFlowRateStorage().get(nextLink)) {
          out.write(he.getNodes()[0]);
          out.write(",");
          out.write(he.getLink());
          out.write(",");
          out.write(he.getNodes()[1]);
          out.write(",");
          out.write(he.getNodes()[2]);
          out.write(",");
          out.write(RELATIONSHIP_TYPE);
          out.write(",");
          out.write(he.getDate());
          out.write(",");
          out.write(he.getHour());
          out.write(",");
          out.write(Double.toString(he.getAverageFlow()));
          out.write(",");
          out.write(Double.toString(he.getFlowLevel()));
          out.write(",");
          out.write(he.getTypeStr());
          out.write("\n");
        }
      }
    } catch (IOException io) {
      System.out.println(io.getMessage());
    }
  }

}
