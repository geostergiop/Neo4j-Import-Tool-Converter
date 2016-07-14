package gr.aueb.cs.infosec.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gr.aueb.cs.infosec.model.Node;
import gr.aueb.infosec.creators.NodeCreator;

public class NodeFileTests {

  private BufferedReader reader;
  private String input;
  private String output;
  private List<String> nodes;
  private List<String> missingNodes;

  /**
   * Constructor
   *
   * @param input
   * @param output
   */
  public NodeFileTests(String input, String output) {
    this.input = input;
    this.output = output;
    this.nodes = new ArrayList<String>();
    this.missingNodes = new ArrayList<String>();
    initialize();
  }

  /**
   * Initialize the testing background
   */
  private void initialize() {
    NodeCreator ncreator = new NodeCreator(input, output);
    ncreator.process();
    ncreator.finalize();

    System.out.println("Finished node file creation from original file  : " + this.input);
    // fill the node storage
    String nextLine = null;
    try {
      this.reader = new BufferedReader(new FileReader(new File(this.input)));
      // do not read the header
      this.reader.readLine();
      while ((nextLine = reader.readLine()) != null) {
        Node[] nodes = this.splitNodeNames(nextLine);
        for (Node n : nodes) {
          this.nodes.add(n.getName());
        }
      }
    } catch (IOException io) {
      System.out.println(io.getMessage());
    }

    System.out.println("Finished filling storage list");
  }

  /**
   * Given an input line from the csv dataset file, this method processes the line and returns the
   * three nodes participating in the corresponding link.
   *
   * @param inputLine
   * @return a Node array containing the nodes
   */
  private Node[] splitNodeNames(String inputLine) {
    Node[] results = new Node[3];
    // first node
    String first = inputLine.split(",")[1].split("between")[0].replaceAll("^\\s+|\\s+$", "");
    // 2nd node
    String second =
        inputLine.split(",")[1].split("and")[0].split("between")[1].replaceAll("^\\s+|\\s+$", "");
    // 3rd node
    String temp = inputLine.split(",")[1].split("and")[1];
    String third = temp.substring(0, temp.indexOf("(" + this.getLinkName(inputLine) + ")"))
        .replaceAll("^\\s+|\\s+$", "");
    results[0] = new Node(first, second, third);
    results[1] = new Node(second, first, third);
    results[2] = new Node(third, first, second);
    return results;
  }

  /**
   * Get the link's name
   *
   * @param inputLine
   * @return
   */
  private String getLinkName(String inputLine) {
    return inputLine.split(",")[0];
  }

  /**
   * Test for missing nodes
   */
  public void testNodeMissing() {
    System.out.println("Starting node missing test");
    String nextLine = null;
    try {
      this.reader = new BufferedReader(new FileReader(new File(this.output)));
      // do not read the header
      this.reader.readLine();
      while ((nextLine = reader.readLine()) != null) {
        String nextNode = nextLine.split(",")[0];
        if (!this.nodes.contains(nextNode)) {
          this.missingNodes.add(nextNode);
        }
      }
    } catch (IOException io) {
      System.out.println(io.getMessage());
    }
    if (this.missingNodes.size() == 0) {
      System.out.println("Node test passed, all nodes included");
    } else {
      System.out.println("Node test failed. Missing nodes : \n");
      for (String s : this.missingNodes) {
        System.out.println(s);
      }
    }
  }

  public static void main(String[] args) {
    String in = "C:\\Users\\Evan\\Desktop\\Aug 2014.csv";
    String out = "C:\\Users\\Evan\\Desktop\\Aug2014(nodes).csv";
    NodeFileTests nft = new NodeFileTests(in, out);
    nft.testNodeMissing();
  }
}
