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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class CalculateMemory {
  /**
   * From Mike McCandless: Memory for sorting on a string field is
   * maxDocs * ceil(log_2(totalTermBytes))/8
   * due to the magic of packed Ints and the byte arrays used for storing strings. FSTs have nothing to do with this.
   */

  static long calculate(EstProperties estProps, boolean dump) {
    BufferedWriter writer = null;
    long total = 0;
    try {
      if (dump) {
        writer = new BufferedWriter(new FileWriter("estimate.txt"));
        writer.write("Memory calculations:");
        writer.newLine();
        writer.newLine();
        dumpNum(writer, "                 Num docs: ", estProps.get_numDocs());
        dumpNum(writer, "              Deleted pct: ", estProps.get_delPercent());
        dumpNum(writer, "        Filter Cache Size: ", estProps.get_filterCacheSize());
        dumpNum(writer, "      Document Cache Size: ", estProps.get_documentCacheSize());
        dumpNum(writer, "  Query Result Cache Size: ", estProps.get_queryResultCacheSize());
        dumpNum(writer, "          User Cache Size: ", estProps.get_userCacheSize());
        dumpNum(writer, "Average Filter Query Size: ", estProps.get_filterCacheSize());
        dumpNum(writer, "       Average Query Size: ", estProps.get_avgQuerySize());
        dumpNum(writer, "          Max Window Size: ", estProps.get_maxWindowSize());
        writer.newLine();
        writer.newLine();
        writer.write("-------------Dumping Fields-------------------------------------");

        writer.newLine();
      }
      long maxDocs = (long) ((double) estProps.get_numDocs() * (1 + (((double) estProps.get_delPercent()) / 100)));
      //TODO: cross check the flags to figure out this junk.
      long maxBytesPerDoc = 0;
      for (FieldProperty prop : estProps.get_fieldProperties().values()) {
        if (dump) {
          dumpField(writer, prop);


          // Add in sorting information
          if (prop.is_sorting()) {
            writer.write("  Sort requirements:");
            writer.newLine();
          }
        }
        if (prop.is_sorting() && prop.is_string()) {
          long tokenBytes = prop.get_tokenLen() * prop.get_uniqueVals();
          // Add packed array for indirection if a string field
          int ceil = (int) Math.ceil(Math.log((double) tokenBytes) / Math.log(2)) / 8;
          total += ceil * maxDocs;
          dumpNum(writer, "  Pointer array: ", ceil * maxDocs);

          // Add all the bytes for the actual string data
          total += tokenBytes;
          dumpNum(writer, String.format("  String storage (%s * %s): ",
              Utils._decimalFormat.format(prop.get_tokenLen()), Utils._decimalFormat.format(prop.get_uniqueVals())),
              tokenBytes);
        } else if (prop.is_sorting()) {
          total += prop.get_tokenLen() * maxDocs;
          dumpNum(writer, String.format("    Numeric storage (%s * %s): ",
              Utils._decimalFormat.format(prop.get_tokenLen()), Utils._decimalFormat.format(maxDocs)),
              prop.get_tokenLen() * maxDocs);
        }

        // Add in boosting/norms (same thing, user sees them as different)
        if (prop.is_lengthNorms() || prop.is_boosting()) { // One byte for every field with norms in every document.
          // NOTE: that user can do their own, but I think it still defaults to 1 byte if they don't
          total += maxDocs;
          dumpNum(writer, "  Norms/boost storage (1 byte/doc): ", maxDocs);
        }
        // TODO: what about stats? I don't think they matter

        // TODO: Phrases How to factor in term position information? (i.e. phrases?)

        //TODO: tlog

        // See: http://lucene.472066.n3.nabble.com/Faceting-memory-requirements-td2128023.html
        // Add in facets.

        if (prop.is_faceting()) {
          if (prop.is_facet_enum()) {
            total += (prop.get_uniqueVals()) * maxDocs / 8;
            dumpNum(writer, String.format("  Faceting requirements ((uniqueVals * maxdocs) / 8) (%s * %s / 8): ",
                Utils._decimalFormat.format(prop.get_uniqueVals()), Utils._decimalFormat.format(maxDocs)),
                (prop.get_uniqueVals() * maxDocs) / 8);
          } else {
            total += prop.get_uniqueVals() * 4;
            dumpNum(writer, String.format("  Faceting requirements (uniqueVals * sizeof(int) (%s * 4):",
                Utils._decimalFormat.format(prop.get_uniqueVals())), prop.get_uniqueVals() * 4);

          }
        }

        maxBytesPerDoc += prop.get_rawBytes();
      }

      if (dump) {
        writer.newLine();
        writer.newLine();
        writer.write("-------------Dumping Caches-------------------------------------");
        writer.newLine();
      }
      Map<String, CacheProperty> cacheProps = estProps.get_cacheProperties();

      // Filter cache
      if (dump) {
        writer.newLine();
        writer.write("Filter cache:");
        writer.newLine();
      }
      CacheProperty cp = cacheProps.get(FixedOptsPanel.CACHE_FILTER);
      total += cp.get_size() * estProps.get_avgFqSize();
      dumpNum(writer, String.format("  Aggregate size of keys (%s * %s): ",
          Utils._decimalFormat.format(cp.get_size()), estProps.get_avgFqSize()), cp.get_size() * estProps.get_avgFqSize());
      total += cp.get_size() * maxDocs / 8;
      dumpNum(writer, String.format("  Aggregate value for keys (%s * %s) / 8: ",
          Utils._decimalFormat.format(cp.get_size()), Utils._decimalFormat.format(maxDocs)), cp.get_size() * maxDocs / 8);


      // QueryResultsCache
      if (dump) {
        writer.newLine();
        writer.write("Query Result Cache:");
        writer.newLine();
      }
      cp = cacheProps.get(FixedOptsPanel.CACHE_QUERY_RESULT);
      total += cp.get_size() * estProps.get_avgQuerySize();
      dumpNum(writer, String.format("  Aggregate size of keys (%s * %s): ",
          Utils._decimalFormat.format(cp.get_size()), Utils._decimalFormat.format(estProps.get_avgQuerySize())),
          cp.get_size() * estProps.get_avgQuerySize());
      total += cp.get_size() * estProps.get_maxWindowSize();
      dumpNum(writer, String.format("  Aggregate size of values (%s * %s): ",
          Utils._decimalFormat.format(cp.get_size()), Utils._decimalFormat.format(estProps.get_maxWindowSize())),
          cp.get_size() * estProps.get_maxWindowSize());


      // Document cache
      if (dump) {
        writer.newLine();
        writer.write("Document cache:");
        writer.newLine();
      }
      cp = cacheProps.get(FixedOptsPanel.CACHE_DOCUMENT);
      total += cp.get_size() * maxBytesPerDoc;
      dumpNum(writer, String.format("  Aggregate size of docs  %s * %s: ",
          Utils._decimalFormat.format(cp.get_size()), Utils._decimalFormat.format(maxBytesPerDoc)),
          cp.get_size() * maxBytesPerDoc);

      // User cache
      if (dump) {
        writer.newLine();
        writer.write("User cache:");
        writer.newLine();
      }
      cp = cacheProps.get(FixedOptsPanel.CACHE_USER);
      total += cp.get_size();
      dumpNum(writer, "  User entered: ", cp.get_size());

      //TODO: Add in facet calculations

      if (dump) {
        writer.newLine();
        writer.newLine();
        writer.write("-------------Total-------------------------------------");
        writer.newLine();
        dumpNum(writer, "TOTAL (MB): ", total / (1024 * 1024));
        writer.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return total;
  }

  static void dumpNum(BufferedWriter writer, String prefix, long num) throws IOException {
    if (writer != null) {
      writer.write(String.format("%s [%s (B)]", prefix,
          Utils._decimalFormat.format(num)));
      if (num > (1024 * 1024)) {
        writer.write(String.format(" [%s (MB)]", Utils._decimalFormat.format(num / (1024 * 1024))));
      }
      writer.newLine();
    }
  }

  static long calculate(EstProperties estProps) {
    return calculate(estProps, false);
  }

  static void dumpField(BufferedWriter writer, FieldProperty prop) throws IOException {
    writer.newLine();
    writer.write("Field: " + prop.get_name());
    writer.newLine();
    dumpBool(writer, "  Boosting: ", prop.is_boosting());
    dumpBool(writer, "  Faceting: ", prop.is_faceting());
    dumpBool(writer, "  Norms: ", prop.is_lengthNorms());
    dumpBool(writer, "  Phrases: ", prop.is_phrases());
    dumpBool(writer, "  Sorting: ", prop.is_sorting());
    dumpBool(writer, "  Stats: ", prop.is_statistics());
    dumpBool(writer, "  String: ", prop.is_string());
    dumpNum(writer, "  Unique Vals: ", prop.get_uniqueVals());
    dumpNum(writer, "  Avg Token Len: ", prop.get_tokenLen());
    dumpNum(writer, "  Raw (stored) bytes: ", prop.get_rawBytes());
    writer.write("   ------");
    writer.newLine();
  }

  static void dumpBool(BufferedWriter writer, String tag, boolean val) throws IOException {
    writer.write(tag + ((val) ? "true" : "false"));
    writer.newLine();
  }
}
