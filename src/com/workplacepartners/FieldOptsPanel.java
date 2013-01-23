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

public class FieldOptsPanel extends JPanel implements ActionListener {

  JCheckBox _searching = new JCheckBox("Use for seaching?");
  JCheckBox _sorting = new JCheckBox("Use for sorting?");
  JCheckBox _string = new JCheckBox("String field?");
  JCheckBox _boosting = new JCheckBox("Boosting?");
  JCheckBox _lengthNorms = new JCheckBox("Length Normalization?");
  JCheckBox _statistics = new JCheckBox("Function Queries?");
  JCheckBox _phrases = new JCheckBox("Phrase Queries?");
  JCheckBox _faceting = new JCheckBox("Faceting?");
  JCheckBox _facet_enum = new JCheckBox("Facet Method Enum?");
  JCheckBox _facet_fc = new JCheckBox("Facet method FC?");
  JCheckBox _invisible = new JCheckBox(""); // because of stupid grid layout. You probably should have used a builder

  JTextField _avgTokensPerDoc = new JTextField("1,000");
  JTextField _fieldName = new JTextField("");
  JTextField _uniqueTokens = new JTextField("100");
  JTextField _tokenLen = new JTextField("4");
  JTextField _rawBytes = new JTextField("0");
  JTextField _fieldType = new JTextField("");

  JButton _save = new JButton("Add/Save field");
  JButton _cancel = new JButton("Cancel");
  JButton _delete = new JButton("Delete field");

  JComboBox _comboList;

  Estimator _estimator;
  EstProperties _properties;

  JPanel _buttonsL = new JPanel();
  JPanel _buttonsR = new JPanel();

  JPanel _radios = new JPanel();

  FieldOptsPanel(EstProperties properties, Estimator estimator) {
    //Create the check boxes.
    super();
    _properties = properties;
    _estimator = estimator;
    this.setLayout(new GridLayout(0, 2));
    setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
        "Individual Field Options (will appear in 'Fields' combo box when added)."));

    _comboList = new JComboBox(_properties.getFieldNames());
    _comboList.setSelectedIndex(0);
    _comboList.addActionListener(this);

    add(new JLabel("Fields:   ", JLabel.RIGHT));
    add(_comboList);
    _invisible.setVisible(false);
    FieldProperty prop = new FieldProperty("new"); // Just to get defaults
    Utils.setupTextField(this, new JLabel("name:    ", JLabel.RIGHT),
        _fieldName, _fieldName.getText(),
        "Enter a name in this field to define a new field.");
    Utils.setupTextField(this, new JLabel("Tokens/doc:    ", JLabel.RIGHT),
        _avgTokensPerDoc, _avgTokensPerDoc.getText(),
        "Enter the average number of tokens per document");
    Utils.setupTextField(this, new JLabel("Unique tokens in field:    ", JLabel.RIGHT),
        _uniqueTokens, _uniqueTokens.getText(),
        "Enter the number of unique tokens in the entire corpus that will be in this field");
    Utils.setupTextField(this, new JLabel("Average token length (bytes):    ", JLabel.RIGHT),
        _tokenLen, _tokenLen.getText(),
        "Enter the average token length, e.g. 4 for ints, 8 for longs, word length for text)");
    Utils.setupTextField(this, new JLabel("Average text bytes ONLY if stored:    ", JLabel.RIGHT),
        _rawBytes, _rawBytes.getText(),
        "Enter the input field averages size if stored. Used for computing document cache size");
    Utils.setupTextField(this, new JLabel("Field type (optional):    ", JLabel.RIGHT),
        _fieldType, _fieldType.getText(),
        "Informational only for including in the dump file.");
    Utils.addTwoCheckBoxes(this, _searching, prop.is_searching(), _sorting, prop.is_sorting());
    Utils.addTwoCheckBoxes(this, _string, prop.is_string(), _boosting, prop.is_boosting());
    Utils.addTwoCheckBoxes(this, _lengthNorms, prop.is_lengthNorms(), _statistics, prop.is_statistics());
    Utils.addTwoCheckBoxes(this,  _phrases, prop.is_phrases(), _faceting, prop.is_faceting());
    //Utils.addTwoCheckBoxes(this, _invisible, false);
    // TODO: figure out how to do this. _radios.setLayout(new GroupLayout());
    _radios.add(_facet_enum);
    _radios.add(_facet_fc);
    //this.add(_radios);
    Utils.addTwoCheckBoxes(this, _facet_enum, prop.is_facet_enum(), _facet_fc, prop.is_facet_fc());

    _buttonsL.setLayout(new GridLayout(0, 2));
    _buttonsR.setLayout(new GridLayout(0, 2));

    _save.addActionListener(this);
    _cancel.addActionListener(this);
    _delete.addActionListener(this);
    _buttonsL.add(_save);
    _buttonsL.add(_cancel);
    _buttonsR.add(_delete);

    this.add(_buttonsL, Utils.getBelow());
    this.add(_buttonsR, Utils.getBelow());
    this.add(new JLabel("You must click the Add/Save button to affect calculations!!!"), Utils.getBelow());
  }


  void showFieldOpts(String fieldName) {
    FieldProperty thisOne = _properties.getFieldProperty(fieldName);

    if (thisOne == null) {
      thisOne = new FieldProperty(fieldName);
    }
    _searching.setSelected(thisOne.is_searching());
    _sorting.setSelected(thisOne.is_sorting());
    _string.setSelected(thisOne.is_string());
    _faceting.setSelected(thisOne.is_faceting());
    _facet_enum.setSelected(thisOne.is_facet_enum());
    _facet_fc.setSelected(thisOne.is_facet_fc());
    _boosting.setSelected(thisOne.is_boosting());
    _lengthNorms.setSelected(thisOne.is_lengthNorms());
    _statistics.setSelected(thisOne.is_statistics());
    _phrases.setSelected(thisOne.is_phrases());
    _fieldName.setText(thisOne.get_name());


    _uniqueTokens.setText(Utils._decimalFormat.format(thisOne.get_uniqueVals()));
    _tokenLen.setText(Utils._decimalFormat.format(thisOne.get_tokenLen()));
    _rawBytes.setText(Utils._decimalFormat.format(thisOne.get_rawBytes()));
    _fieldType.setText(thisOne.get_fieldType());
    _avgTokensPerDoc.setText(Utils._decimalFormat.format(thisOne.get_avgTokensPerDoc()));
  }

  public void addToCombo(FieldProperty newProp) {
    _comboList.removeItem(newProp.get_name());
    _comboList.addItem(newProp.get_name());
    for (int idx = 0; idx < _comboList.getItemCount(); ++idx) {
      if (newProp.get_name().equals(_comboList.getItemAt(idx))) {
        _comboList.setSelectedIndex(idx);
        break;
      }
    }
  }

  public void removeFromCombo(String name) {
    _comboList.removeItem(name);
  }


  @Override
  public void actionPerformed(ActionEvent actionEvent) {
    String fieldName = _fieldName.getText();

    if (actionEvent.getSource() == _save) {
      if (fieldName != null) fieldName = fieldName.trim();
      if (fieldName == null || fieldName.length() == 0) {
        JOptionPane.showMessageDialog(null,
            "Field name may not be empty!",
            "user error",
            JOptionPane.ERROR_MESSAGE);
        return;
      }
      if (!validateForm()) return;
      FieldProperty newProp = new FieldProperty(fieldName);
      newProp.set_uniqueVals(Utils.getLong(_uniqueTokens.getText()));
      newProp.set_tokenLen(Utils.getLong(_tokenLen.getText()));
      newProp.set_rawBytes(Utils.getLong(_rawBytes.getText()));
      newProp.set_avgTokensPerDoc(Utils.getLong(_avgTokensPerDoc.getText()));
      newProp.set_name((_fieldName.getText()));
      newProp.set_boosting(_boosting.isSelected());
      newProp.set_lengthNorms(_lengthNorms.isSelected());
      newProp.set_phrases(_phrases.isSelected());
      newProp.set_sorting(_sorting.isSelected());
      newProp.set_searching(_searching.isSelected());
      newProp.set_string((_string.isSelected()));
      newProp.set_faceting(_faceting.isSelected());
      newProp.set_facet_enum(_facet_enum.isSelected());
      newProp.set_facet_fc(_facet_fc.isSelected());
      newProp.set_statistics(_statistics.isSelected());
      newProp.set_fieldType((_fieldType.getText()));
      _properties.addFieldProperty(newProp);
      addToCombo(newProp);
    } else if (actionEvent.getSource() == _delete) {
      if (!"new".equals(fieldName)) {
        _properties.removeField(fieldName);
        removeFromCombo(fieldName);
      }
    } else if (actionEvent.getSource() == _comboList) {
      JComboBox cb = (JComboBox) actionEvent.getSource();
      showFieldOpts((String) cb.getSelectedItem());
      //_frame.pack();
    }
    _estimator.saveProps();
  }

  boolean validateForm() {
    if (Utils.validateLong(_uniqueTokens.getText(), "unique tokens") &&
        Utils.validateLong(_tokenLen.getText(), "token length") &&
        Utils.validateLong(_rawBytes.getText(), "raw bytes")) {

      if (_faceting.isSelected()) {
        if (_facet_enum.isSelected() && _facet_fc.isSelected()) {
          JOptionPane.showMessageDialog(null,
              "It is invalid to have both enum and fc selected for faceting",
              "user error",
              JOptionPane.ERROR_MESSAGE);
          return false;
        }

        if (!_facet_enum.isSelected() && !_facet_fc.isSelected()) {
          JOptionPane.showMessageDialog(null,
              "One of enum or fc must be selected selected for faceting",
              "user error",
              JOptionPane.ERROR_MESSAGE);
          return false;
        }
      }
    }
    return true;
  }
}
