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

class FieldProperty implements Serializable {
  private String _name;
  private boolean _sorting = false;
  private boolean _string = false;
  private boolean _faceting = false;
  private boolean _boosting = true;
  private boolean _lengthNorms = true;
  private boolean _statistics = false;
  private boolean _phrases = true;
  private boolean _facet_enum = false;
  private boolean _facet_fc = true;

  private long _uniqueVals = 100;
  private long _tokenLen = 4;
  private long _rawBytes = 0;

  //TODO: go over things again. But the issue is that you need to go over all the opts and figure out what's right.

  FieldProperty(String name) {
    set_name(name);
  }

  public String get_name() {
    return _name;
  }

  public void set_name(String _name) {
    this._name = _name;
  }

  public boolean is_sorting() {
    return _sorting;
  }

  public void set_sorting(boolean sorting) {
    this._sorting = sorting;
  }

  public boolean is_boosting() {
    return _boosting;
  }

  public void set_boosting(boolean _boosting) {
    this._boosting = _boosting;
  }

  public boolean is_string() {
    return _string;
  }

  public void set_string(boolean string) {
    this._string = string;
  }

  public boolean is_lengthNorms() {
    return _lengthNorms;
  }

  public void set_lengthNorms(boolean _lengthNorms) {
    this._lengthNorms = _lengthNorms;
  }

  public boolean is_statistics() {
    return _statistics;
  }

  public void set_statistics(boolean _statistics) {
    this._statistics = _statistics;
  }

  public boolean is_phrases() {
    return _phrases;
  }

  public void set_phrases(boolean _phrases) {
    this._phrases = _phrases;
  }

  public long get_uniqueVals() {
    return _uniqueVals;
  }

  public void set_uniqueVals(long _uniqueVals) {
    this._uniqueVals = _uniqueVals;
  }

  public long get_tokenLen() {
    return _tokenLen;
  }

  public void set_tokenLen(long _tokenLen) {
    this._tokenLen = _tokenLen;
  }

  public long get_rawBytes() {
    return _rawBytes;
  }

  public void set_rawBytes(long _rawBytes) {
    this._rawBytes = _rawBytes;
  }

  public boolean is_faceting() {
    return _faceting;
  }

  public void set_faceting(boolean _faceting) {
    this._faceting = _faceting;
  }

  public boolean is_facet_enum() {
    return _facet_enum;
  }

  public void set_facet_enum(boolean _facet_enum) {
    this._facet_enum = _facet_enum;
  }

  public boolean is_facet_fc() {
    return _facet_fc;
  }

  public void set_facet_fc(boolean _facet_fc) {
    this._facet_fc = _facet_fc;
  }
}
