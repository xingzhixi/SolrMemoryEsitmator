/**
 *   Copyright 2012 Workplace Partners LLC
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.workplacepartners;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

/**
 * Freely available for non-commercial applications without restriction. If you charge for it, I expect to get a cut.
 */
public class Estimator extends JPanel implements ActionListener {

  Frame _frame;

  EstProperties _estProperties;

  private static String MB_LABEL = "Total memory MB:   ";

  JLabel _MBLabel;
  JButton _dump = new JButton("Dump calcs (estimate.txt)");
  JButton _save = new JButton("Save and Recalculate");
  JButton _exit = new JButton("Exit");

  FieldOptsPanel _fieldOptsPanel;

  FixedOptsPanel _fixedOptsPanel;

  JPanel _allOfIt = new JPanel(new GridBagLayout());

  public Estimator(Frame frame, EstProperties props) {
    super(new BorderLayout());
    _frame = frame;
    _estProperties = props;

    _MBLabel = new JLabel(MB_LABEL);
    _MBLabel.setFont(new Font("Serif", Font.BOLD, 18));
    _MBLabel.setForeground(Color.RED);

    _allOfIt.add(_MBLabel, Utils.getBelow());

    _fixedOptsPanel = new FixedOptsPanel(_estProperties);
    _allOfIt.add(_fixedOptsPanel, Utils.getBelow());

    _fieldOptsPanel = new FieldOptsPanel(_estProperties, this);
    _allOfIt.add(_fieldOptsPanel, Utils.getBelow());

    JPanel buttons = new JPanel(new GridLayout(0, 3));
    _dump.addActionListener(this);
    _save.addActionListener(this);
    _exit.addActionListener(this);
    buttons.add(_dump);
    buttons.add(_save);
    buttons.add(_exit);

    _allOfIt.add(buttons, Utils.getBelow());
    add(_allOfIt);
    _MBLabel.setText("Memory (MB): " + Utils._decimalFormat.format(CalculateMemory.calculate(_estProperties) / (1024 * 1024)));

    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
  }


  /**
   * Create the GUI and show it.  For thread safety,
   * this method should be invoked from the
   * event-dispatching thread.
   */
  private static void createAndShowGUI() {

    //Create and set up the window.
    JFrame frame = new JFrame("Solr Memory Estimator");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //Create and set up the content pane.

    JComponent newContentPane = new Estimator(frame, initProps());
    newContentPane.setOpaque(true); //content panes must be opaque
    frame.setContentPane(newContentPane);

    //Display the window.
    frame.pack();
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createAndShowGUI();
      }
    });
  }

  private static EstProperties initProps() {
    EstProperties ret = null;
    File saved = new File("estimator.ser");
    if (saved.exists()) {
      try {

        FileInputStream fileIn =
            new FileInputStream("estimator.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        ret = (EstProperties) in.readObject();
        in.close();
        fileIn.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (ret == null) ret = new EstProperties();
    return ret;
  }

  public void saveProps() {
    try {
      FileOutputStream fileOut =
          new FileOutputStream("estimator.ser");
      ObjectOutputStream out =
          new ObjectOutputStream(fileOut);
      out.writeObject(_estProperties);
      out.close();
      fileOut.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    long total = CalculateMemory.calculate(_estProperties);
    _MBLabel.setText("Memory (MB): " + Utils._decimalFormat.format(total / (1024 * 1024)));
  }

  @Override
  public void actionPerformed(ActionEvent actionEvent) {
    if (actionEvent.getSource() == _save) {
      if (_fixedOptsPanel.validateForm()) {
        saveProps();
      }
    } else if (actionEvent.getSource() == _exit) {
      System.exit(0);
    } else if (actionEvent.getSource() == _dump) {
      CalculateMemory.calculate(_estProperties, true);
    }
  }

}