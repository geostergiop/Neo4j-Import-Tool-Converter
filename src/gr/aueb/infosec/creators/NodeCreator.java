package gr.aueb.infosec.creators;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import gr.aueb.cs.infosec.model.Node;

public class NodeCreator extends Creator {

  // output csv header
  private final String CSV_HEADER = "node_name:ID,connected_node1,connected_node2,:LABEL";
  // label used for the neo4j database
  private final String LABEL = "Traffic node";

  /**
   * Constructor
   * 
   * @param input
   * @param output
   */
  public NodeCreator(String input, String output) {
    super(input, output);
  }

  @Override
  public void process() {
    // add execution time
    long startTime = System.currentTimeMillis();
    String nextLine = null;
    BufferedReader in = this.getReader();
    BufferedWriter out = this.getWriter();
    try {
      // do not read the header
      in.readLine();
      // write the corresponding header to the output
      out.write(CSV_HEADER);
      out.write("\n");
      while ((nextLine = in.readLine()) != null) {
        Node[] split_nodes = this.splitNodeNames(nextLine);
        for (Node node : split_nodes) {
          if (this.getStorage().get(node.getName()) != null) {
            // we don't want to write duplicate entries
            continue;
          }
          this.getStorage().put(node.getName(), node.getFirstConnectedNode());
          out.write(node.getName());
          out.write(",");
          out.write(node.getFirstConnectedNode());
          out.write(",");
          out.write(node.getSecondConnectedNode());
          out.write(",");
          out.write(LABEL);
          out.write("\n");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println(
        "Parsed file : " + this.getInput() + " in " + (System.currentTimeMillis() - startTime));
  }
}
