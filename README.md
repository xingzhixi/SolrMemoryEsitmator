SolrMemoryEsitmator
===================

Solr memory estimator

This repository is form some code I'm writing to estimate Solr (4.0) memory requirements.
Most estimates will probably be high, but you MUST PROTOTYPE to ascertain for sure.

License info:

    Copyright 2012 Workplace Partners LLC
 
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 
 
 Code is a "living bit of code" meaning it isn't necessarily reliable at this point, but
 at least it's a starting point.
 

To build the project:

ant jar

To run the estimator:

java -jar dist/estimator.jar
