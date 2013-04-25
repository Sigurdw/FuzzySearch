package Query.PriorityInteractiveSearch.Links;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import DataStructure.QueryString;
import Query.PriorityInteractiveSearch.PriorityActiveNode;
import Query.PriorityInteractiveSearch.SuggestionNode;

import java.util.PriorityQueue;

public class SuggestionLink extends Link {

    private final PriorityActiveNode destinationNode;


    public SuggestionLink(PriorityActiveNode destinationNode) {
        this.destinationNode = destinationNode;
    }

    @Override
    public float getRank() {
        return destinationNode.getNextSuggestionRank();
    }

    @Override
    public PriorityActiveNode useLink(PriorityQueue<Link> linkQueue) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected int getDepth() {
        return destinationNode.getDepth();
    }

    @Override
    protected int getPriority() {
        return 3;
    }

    @Override
    public boolean isOnSuggestionFrontier() {
        return true;
    }

    @Override
    public boolean isValid(QueryString queryString) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
