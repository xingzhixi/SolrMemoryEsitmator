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
import java.text.DecimalFormat;

public class Utils {

  static DecimalFormat _decimalFormat = new DecimalFormat("###,###,###");

  static GridBagConstraints _below = null;


  static void addTwoCheckBoxes(JPanel panel, JCheckBox first, boolean firstChecked, JCheckBox second, boolean secondChecked) {
    first.setSelected(firstChecked);
    panel.add(first);
    if (second != null) {
      second.setSelected(secondChecked);
      panel.add(second);
    }
  }

  static GridBagConstraints getBelow() {
    if (_below == null) {
      _below = new GridBagConstraints();
      _below.gridx = 0;
    }
    return _below;
  }

  static void setupTextField(JPanel panel, JLabel label, JTextField field, long val) {
    setupTextField(panel, label, field, _decimalFormat.format(val));
  }

  static void setupTextField(JPanel panel, JLabel label, JTextField field, String val) {
//    label.setLabelFor(field);
    field.setColumns(10);
    field.setText(val);

    panel.add(label);
    panel.add(field);
  }

  // FORM SHOULD HAVE BEEN VALIDATED AT THIS POINT!!!!
  static long getLong(String num) {
    return Long.parseLong(num.trim().replaceAll("[,\\.]", ""));
  }

  static boolean validateLong(String num, String fieldName) {
    return validateLong(num, fieldName, Long.MAX_VALUE);
  }

  static boolean validateLong(String num, String fieldName, long max) {
    try {
      Long.parseLong(num.trim().replaceAll("[,\\.]", ""));
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null,
          String.format("Field entry %s must be a valid number between 0 and %s inclusive  (commas or periods allowed).",
              fieldName, Utils._decimalFormat.format(max)),
          "user error",
          JOptionPane.ERROR_MESSAGE);
      return false;
    }
    return true;
  }
}
