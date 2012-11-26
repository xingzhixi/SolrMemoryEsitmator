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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Freely available for non-commercial applications without restriction. If you charge for it, I expect to get a cut.
 */

public class EstProperties implements Serializable {
  private static final long serialVersionUID = 1L;

  private Map<String, FieldProperty> _fieldProperties = new TreeMap<String, FieldProperty>();

  private Map<String, CacheProperty> _cacheProperties = new TreeMap<String, CacheProperty>();

  private long _numDocs = 1000000;
  private int _delPercent = 0;
  private int _filterCacheSize = 512;
  private int _documentCacheSize = 512;
  private int _queryResultCacheSize = 512;
  private int _userCacheSize = 0;
  private int _avgFqSize = 16;
  private int _avgQuerySize = 128;
  private int _maxWindowSize = 40;

  public Object[] getFieldNames() {
    List<String> names = new ArrayList<String>();
    names.add("");
    for (String str : get_fieldProperties().keySet()) {
      names.add(str);
    }
    return names.toArray();
  }

  public FieldProperty getFieldProperty(String name) {
    return get_fieldProperties().get(name);
  }

  public void addFieldProperty(FieldProperty property) {
    get_fieldProperties().put(property.get_name(), property);
  }

  public void removeField(String name) {
    get_fieldProperties().remove(name);
  }

  public void addCacheProperty(CacheProperty property) {
    get_cacheProperties().put(property.get_name(), property);
  }

  public long get_numDocs() {
    return _numDocs;
  }

  public void set_numDocs(long _numDocs) {
    this._numDocs = _numDocs;
  }

  public int get_delPercent() {
    return _delPercent;
  }

  public void set_delPercent(int _delPercent) {
    this._delPercent = _delPercent;
  }

  public int get_filterCacheSize() {
    return _filterCacheSize;
  }

  public void set_filterCacheSize(int _filterCacheSize) {
    this._filterCacheSize = _filterCacheSize;
  }

  public int get_documentCacheSize() {
    return _documentCacheSize;
  }

  public void set_documentCacheSize(int _documentCacheSize) {
    this._documentCacheSize = _documentCacheSize;
  }

  public int get_queryResultCacheSize() {
    return _queryResultCacheSize;
  }

  public void set_queryResultCacheSize(int _queryResultCacheSize) {
    this._queryResultCacheSize = _queryResultCacheSize;
  }

  public int get_userCacheSize() {
    return _userCacheSize;
  }

  public void set_userCacheSize(int _userCacheSize) {
    this._userCacheSize = _userCacheSize;
  }

  public int get_avgFqSize() {
    return _avgFqSize;
  }

  public void set_avgFqSize(int _avgFqSize) {
    this._avgFqSize = _avgFqSize;
  }

  public int get_avgQuerySize() {
    return _avgQuerySize;
  }

  public void set_avgQuerySize(int _avgQuerySize) {
    this._avgQuerySize = _avgQuerySize;
  }

  public int get_maxWindowSize() {
    return _maxWindowSize;
  }

  public void set_maxWindowSize(int _maxWindowSize) {
    this._maxWindowSize = _maxWindowSize;
  }


  public Map<String, FieldProperty> get_fieldProperties() {
    return _fieldProperties;
  }

  public Map<String, CacheProperty> get_cacheProperties() {
    return _cacheProperties;
  }
}
