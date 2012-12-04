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

public class FixedOptsPanel extends JPanel {
  //filter, queryresult, document. Fieldvalue???
  //TODO FieldValueCache
  public static final String CACHE_FILTER = "filter";
  public static final String CACHE_QUERY_RESULT = "queryresults";
  public static final String CACHE_DOCUMENT = "document";
  public static final String CACHE_USER = "user";


  JTextField _numDocs = new JTextField();

  JTextField _deletePercent = new JTextField();

  JTextField _filterCacheSize = new JTextField();
  JTextField _queryResultSize = new JTextField();
  JTextField _documentCacheSize = new JTextField();
  JTextField _userCacheSize = new JTextField();
  JTextField _avgFqSize = new JTextField();
  JTextField _avgQuerySize = new JTextField();
  JTextField _maxWindowSize = new JTextField();
  JTextField _unFlushed = new JTextField();

  EstProperties _estProps;

  FixedOptsPanel(EstProperties estProps) {
    _estProps = estProps;

    this.setLayout(new GridLayout(0, 4));

    setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Global Options"));


    Utils.setupTextField(this, new JLabel("Num docs:    ", JLabel.RIGHT),
        _numDocs, estProps.get_numDocs(),
        "Enter the number of live documents expected.");
    Utils.setupTextField(this, new JLabel("Deleted docs %:    ", JLabel.RIGHT),
        _deletePercent, estProps.get_delPercent(),
        "Enter the percentage of deleted documents you expect");


    Utils.setupTextField(this, new JLabel("Filter Cache Size:    ", JLabel.RIGHT),
        _filterCacheSize, estProps.get_filterCacheSize(),
        "Enter the filter cache size ('size' parameter in solrconfig.xml for filterCache)");
    Utils.setupTextField(this, new JLabel("Query Result Cache Size:    ", JLabel.RIGHT),
        _queryResultSize, estProps.get_queryResultCacheSize(),
        "Enter the query result cache ('size property in solrconfig.xml for queryResultCache)");
    Utils.setupTextField(this, new JLabel("Document Cache Size:    ", JLabel.RIGHT),
        _documentCacheSize, estProps.get_documentCacheSize(),
        "Enter the document cache size ('size' parameter in solrconfig.xml for documentCache)");
    Utils.setupTextField(this, new JLabel("User Cache Size (if any, total bytes):    ", JLabel.RIGHT),
        _userCacheSize, estProps.get_userCacheSize(),
        "Enter the user cache size. This will be the max size (in bytes) your user cache will hold. Enter 0 unless you're implementing custom caches");
    Utils.setupTextField(this, new JLabel("Average FQ size (bytes):    ", JLabel.RIGHT),
        _avgFqSize, estProps.get_avgFqSize(),
        "Enter the average string length of a filter query");
    Utils.setupTextField(this, new JLabel("Average Query Size (bytes):    ", JLabel.RIGHT),
        _avgQuerySize, estProps.get_avgQuerySize(),
        "Enter the average string length of a query");
    Utils.setupTextField(this, new JLabel("Query Result Window Size:    ", JLabel.RIGHT),
        _maxWindowSize, estProps.get_maxWindowSize(),
        "Enter the max window size (from 'queryResultWindowsize in solrconfig.xml)");
    Utils.setupTextField(this, new JLabel("Max unflushed docs (soft commit)", JLabel.RIGHT),
        _unFlushed, estProps.get_unFlushed(),
        "Enter the maximum number of documents you expect to index without performing a commit (soft or hard). " +
            "Used to calculate the in-memory portion of the transaction log.");

    _estProps.addCacheProperty(new CacheProperty(CACHE_FILTER, (int) Utils.getLong(_filterCacheSize.getText())));
    _estProps.addCacheProperty(new CacheProperty(CACHE_DOCUMENT, (int) Utils.getLong(_documentCacheSize.getText())));
    _estProps.addCacheProperty(new CacheProperty(CACHE_QUERY_RESULT, (int) Utils.getLong(_queryResultSize.getText())));
    _estProps.addCacheProperty(new CacheProperty(CACHE_USER, (int) Utils.getLong(_userCacheSize.getText())));
    setVisible(true);
  }

  boolean validateForm() {
    if (Utils.validateLong(_numDocs.getText(), "max documents must be a number >= 0") &&
        Utils.validateLong(_deletePercent.getText(), "Percentage must be 0-100", 100) &&
        Utils.validateLong(_filterCacheSize.getText(), "Filter cache size must be >= 0") &&
        Utils.validateLong(_documentCacheSize.getText(), "Document cache size must be >= 0") &&
        Utils.validateLong(_queryResultSize.getText(), "Query results cache size must be >= 0") &&
        Utils.validateLong(_userCacheSize.getText(), "User cache size must be >= 0") &&
        Utils.validateLong(_avgFqSize.getText(), "average fq size must be >= 0") &&
        Utils.validateLong(_avgQuerySize.getText(), "average query size must be >= 0") &&
        Utils.validateLong(_maxWindowSize.getText(), "Max window size must be >= 0") &&
        Utils.validateLong(_unFlushed.getText(), "Unflushed size must be >= 0")) {

      _estProps.set_numDocs(Utils.getLong(_numDocs.getText()));
      _estProps.set_delPercent((int) Utils.getLong(_deletePercent.getText()));
      _estProps.set_filterCacheSize((int) Utils.getLong(_filterCacheSize.getText()));
      _estProps.set_documentCacheSize((int) Utils.getLong(_documentCacheSize.getText()));
      _estProps.set_queryResultCacheSize((int) Utils.getLong(_queryResultSize.getText()));
      _estProps.set_userCacheSize((int) Utils.getLong(_userCacheSize.getText()));
      _estProps.set_avgFqSize((int) Utils.getLong(_avgFqSize.getText()));
      _estProps.set_avgQuerySize((int) Utils.getLong(_avgQuerySize.getText()));
      _estProps.set_maxWindowSize((int) Utils.getLong(_maxWindowSize.getText()));
      _estProps.set_unFlushed(((int) Utils.getLong(_unFlushed.getText())));
      return true;
    }
    return false;
  }
}
