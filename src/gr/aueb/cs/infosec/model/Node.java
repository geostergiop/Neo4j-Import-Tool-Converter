package gr.aueb.cs.infosec.model;

public class Node {

  // node's name
  private String name;
  // first connected node
  private String connected_node1;
  // second connected node
  private String connected_node2;

  /**
   * Constructor
   * 
   * @param name
   * @param connected_node1
   * @param connected_node2
   */
  public Node(String name, String connected_node1, String connected_node2) {
    this.name = name;
    this.connected_node1 = connected_node1;
    this.connected_node2 = connected_node2;
  }

  /**
   * Get the node's name
   * 
   * @return
   */
  public String getName() {
    return this.name;
  }

  /**
   * Get the first connected Node
   * 
   * @return
   */
  public String getFirstConnectedNode() {
    return this.connected_node1;
  }

  /**
   * Get the second connected Node
   * 
   * @return
   */
  public String getSecondConnectedNode() {
    return this.connected_node2;
  }
}
