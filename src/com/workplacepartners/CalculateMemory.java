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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CalculateMemory {
  /**
   * From Mike McCandless: Memory for sorting on a string field is
   * maxDocs * ceil(log_2(totalTermBytes))/8
   * due to the magic of packed Ints and the byte arrays used for storing strings. FSTs have nothing to do with this.
   */

  static StringBuilder _sb = new StringBuilder();

  static long calculate(EstProperties estProps, boolean toFile) {

    long total = 0;
    _fields.clear();
    _sb.setLength(0);
    try {

      _sb.append("Memory calculations:");
      _sb.append("\n");
      _sb.append("\n");
      dumpNum("                 Num docs: ", estProps.get_numDocs());
      dumpNum("              Deleted pct: ", estProps.get_delPercent());
      dumpNum("        Filter Cache Size: ", estProps.get_filterCacheSize());
      dumpNum("      Document Cache Size: ", estProps.get_documentCacheSize());
      dumpNum("  Query Result Cache Size: ", estProps.get_queryResultCacheSize());
      dumpNum("          User Cache Size: ", estProps.get_userCacheSize());
      dumpNum("Average Filter Query Size: ", estProps.get_filterCacheSize());
      dumpNum("       Average Query Size: ", estProps.get_avgQuerySize());
      dumpNum("          Max Window Size: ", estProps.get_maxWindowSize());
      _sb.append("\n");
      _sb.append("\n");
      _sb.append("-------------Dumping Fields-------------------------------------");

      _sb.append("\n");
      long maxDocs = (long) ((double) estProps.get_numDocs() * (1 + (((double) estProps.get_delPercent()) / 100)));
      //TODO: cross check the flags to figure out this junk.
      long maxBytesPerDoc = 0;
      for (FieldProperty prop : estProps.get_fieldProperties().values()) {
        dumpField(prop);


        // Add in sorting information
        if (prop.is_sorting()) {
          _sb.append("  Sort requirements:");
          _sb.append("\n");
        }
        if (prop.is_sorting() && prop.is_string()) {
          long tokenBytes = prop.get_tokenLen() * prop.get_uniqueVals();
          // Add packed array for indirection if a string field
          int ceil = (int) Math.ceil(Math.log((double) tokenBytes) / Math.log(2)) / 8;
          total += ceil * maxDocs;
          dumpNum("  Pointer array: ", ceil * maxDocs);

          // Add all the bytes for the actual string data
          total += tokenBytes;
          dumpNum(String.format("  String storage (%s * %s): ",
              Utils._decimalFormat.format(prop.get_tokenLen()), Utils._decimalFormat.format(prop.get_uniqueVals())),
              tokenBytes);
        } else if (prop.is_sorting()) {
          total += prop.get_tokenLen() * maxDocs;
          dumpNum(String.format("    Numeric storage (%s * %s): ",
              Utils._decimalFormat.format(prop.get_tokenLen()), Utils._decimalFormat.format(maxDocs)),
              prop.get_tokenLen() * maxDocs);
        }

        // Add in boosting/norms (same thing, user sees them as different)
        if (prop.is_lengthNorms() || prop.is_boosting()) { // One byte for every field with norms in every document.
          // NOTE: that user can do their own, but I think it still defaults to 1 byte if they don't
          total += maxDocs;
          dumpNum("  Norms/boost storage (1 byte/doc): ", maxDocs);
        }
        // TODO: what about stats? I don't think they matter

        // TODO: Phrases How to factor in term position information? (i.e. phrases?)


        // See: http://lucene.472066.n3.nabble.com/Faceting-memory-requirements-td2128023.html
        // Add in facets.

        if (prop.is_faceting()) {
          if (prop.is_facet_enum()) {
            total += (prop.get_uniqueVals()) * maxDocs / 8;
            dumpNum(String.format("  Faceting requirements ((uniqueVals * maxdocs) / 8) (%s * %s / 8): ",
                Utils._decimalFormat.format(prop.get_uniqueVals()), Utils._decimalFormat.format(maxDocs)),
                (prop.get_uniqueVals() * maxDocs) / 8);
          } else {
            total += prop.get_uniqueVals() * 4;
            dumpNum(String.format("  Faceting requirements (uniqueVals * sizeof(int) (%s * 4):",
                Utils._decimalFormat.format(prop.get_uniqueVals())), prop.get_uniqueVals() * 4);
          }
        }

        maxBytesPerDoc += prop.get_rawBytes();
      }

      _sb.append("\n");
      _sb.append("\n");
      _sb.append("-------------Dumping Caches-------------------------------------");
      _sb.append("\n");
      Map<String, CacheProperty> cacheProps = estProps.get_cacheProperties();

      // Filter cache
      _sb.append("\n");
      _sb.append("Filter cache:");
      _sb.append("\n");
      CacheProperty cp = cacheProps.get(FixedOptsPanel.CACHE_FILTER);
      total += cp.get_size() * estProps.get_avgFqSize();
      dumpNum(String.format("  Aggregate size of keys (%s * %s): ",
          Utils._decimalFormat.format(cp.get_size()), estProps.get_avgFqSize()), cp.get_size() * estProps.get_avgFqSize());
      total += cp.get_size() * maxDocs / 8;
      dumpNum(String.format("  Aggregate value for keys (%s * %s) / 8: ",
          Utils._decimalFormat.format(cp.get_size()), Utils._decimalFormat.format(maxDocs)), cp.get_size() * maxDocs / 8);


      // QueryResultsCache
      _sb.append("\n");
      _sb.append("Query Result Cache:");
      _sb.append("\n");
      cp = cacheProps.get(FixedOptsPanel.CACHE_QUERY_RESULT);
      total += cp.get_size() * estProps.get_avgQuerySize();
      dumpNum(String.format("  Aggregate size of keys (%s * %s): ",
          Utils._decimalFormat.format(cp.get_size()), Utils._decimalFormat.format(estProps.get_avgQuerySize())),
          cp.get_size() * estProps.get_avgQuerySize());
      total += cp.get_size() * estProps.get_maxWindowSize();
      dumpNum(String.format("  Aggregate size of values (%s * %s): ",
          Utils._decimalFormat.format(cp.get_size()), Utils._decimalFormat.format(estProps.get_maxWindowSize())),
          cp.get_size() * estProps.get_maxWindowSize());


      // Document cache
      _sb.append("\n");
      _sb.append("Document cache:");
      _sb.append("\n");
      cp = cacheProps.get(FixedOptsPanel.CACHE_DOCUMENT);
      total += cp.get_size() * maxBytesPerDoc;
      dumpNum(String.format("  Aggregate size of docs  %s * %s: ",
          Utils._decimalFormat.format(cp.get_size()), Utils._decimalFormat.format(maxBytesPerDoc)),
          cp.get_size() * maxBytesPerDoc);

      // User cache
      _sb.append("\n");
      _sb.append("User cache:");
      _sb.append("\n");
      cp = cacheProps.get(FixedOptsPanel.CACHE_USER);
      total += cp.get_size();
      dumpNum("  User entered: ", cp.get_size());

      //TODO: tlog
      // From Yonik, each doc is essentially 2 longs + average size of <uniqueKey>. It's cleared on softcommit.
      // There's some extra here for a Java MAP overhead of, I think, 36 bytes/entry (32 bit), maybe 40 bytes 64 bit?

      // Does this get cleared on hardcommit? I assume so.
      long tLogSize = estProps.get_unFlushed() * (16 + 40);
      total += tLogSize;
      dumpNum(String.format("Bytes taken up by the memory resident part of the transaction log %s: ", tLogSize),
          tLogSize);

      //TODO: Add in facet calculations

      _sb.append("\n");
      _sb.append("\n");
      _sb.append("-------------Total-------------------------------------");
      _sb.append("\n");
      _sb.append("\n");
      dumpNum("TOTAL (MB): ", total / (1024 * 1024));
      dumpFields();
      if (toFile) {
        BufferedWriter tmpWriter = new BufferedWriter(new FileWriter("estimate.txt"));
        tmpWriter.append(_sb.toString());
        tmpWriter.close();
        // create a JTextArea
        JTextArea textArea = new JTextArea(40, 80);
        textArea.setText(_sb.toString());
        textArea.setEditable(false);

        // wrap a scrollpane around it
        JScrollPane scrollPane = new JScrollPane(textArea);

        // display them in a message dialog
        JOptionPane.showMessageDialog(null, scrollPane);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return total;
  }

  static void dumpNum(String prefix, long num) throws IOException {
    _sb.append(String.format("%s [%s (B)]", prefix,
        Utils._decimalFormat.format(num)));
    if (num > (1024 * 1024)) {
      _sb.append(String.format(" [%s (MB)]", Utils._decimalFormat.format(num / (1024 * 1024))));
    }
    _sb.append("\n");
  }

  static long calculate(EstProperties estProps) {
    return calculate(estProps, false);
  }

  static void dumpField(FieldProperty prop) throws IOException {
    _sb.append("\n");
    _sb.append("Field: ").append(prop.get_name());
    _sb.append("\n");
    dumpBool("  Boosting: ", prop.is_boosting());
    dumpBool("  Faceting: ", prop.is_faceting());
    dumpBool("  Norms: ", prop.is_lengthNorms());
    dumpBool("  Phrases: ", prop.is_phrases());
    dumpBool("  Sorting: ", prop.is_sorting());
    dumpBool("  Stats: ", prop.is_statistics());
    dumpBool("  String: ", prop.is_string());
    dumpNum("  Unique Vals: ", prop.get_uniqueVals());
    dumpNum("  Avg Token Len: ", prop.get_tokenLen());
    dumpNum("  Raw (stored) bytes: ", prop.get_rawBytes());
    _sb.append("   ------");
    _sb.append("\n");
    addFieldDef(prop);

  }

  static void dumpBool(String tag, boolean val) throws IOException {
    _sb.append(tag).append((val) ? "true" : "false");
    _sb.append("\n");
  }

  static List<String> _fields = new ArrayList<String>();

  static void addFieldDef(FieldProperty prop) {
    //<field name="weight" type="float" indexed="true" stored="true"/>
    StringBuilder sb = new StringBuilder("<field name=\"");
    sb.append(prop.get_name()).append("\" ");
    if (prop.is_string()) {
      sb.append("type=\"string\" ");
    } else {
      sb.append("type=\"").append(prop.get_fieldType()).append("\" ");
    }
    if (!prop.is_boosting() && !prop.is_lengthNorms()) {
      sb.append("omitNorms=\"true\" ");
    } else {
      sb.append("omitNorms=\"false\" ");
    }
    if (prop.get_rawBytes() > 0) {
      sb.append("stored=\"true\" ");
    } else {
      sb.append("stored=\"true\" ");
    }

    sb.append("indexed=\"true\" ");

    //TODO: omittermfreqs and all that stuff?
    sb.append(" />");
    _fields.add(sb.toString());
  }

  static void dumpFields() throws IOException {
    _sb.append("\n");
    _sb.append("\n");
    _sb.append("----------------field definitions, MAY BE INCOMPLETE!-------");
    _sb.append("\n");
    _sb.append("\n");
    for (String s : _fields) {
      _sb.append(s);
      _sb.append("\n");
    }
  }

}
