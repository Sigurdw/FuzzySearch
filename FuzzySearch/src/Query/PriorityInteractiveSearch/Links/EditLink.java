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
import DocumentModel.EditOperation;
import Query.PriorityInteractiveSearch.PriorityActiveNode;

import java.util.PriorityQueue;

public class EditLink extends Link{

    private final PriorityActiveNode sourceNode;
    private final PriorityActiveNode destinationNode;

    public EditLink(
        PriorityActiveNode sourceNode,
        PriorityActiveNode destinationNode
    )
    {
        this.sourceNode = sourceNode;
        this.destinationNode = destinationNode;
    }

    @Override
    public float getRank() {
        return destinationNode.getRank();
    }

    @Override
    public PriorityActiveNode useLink(PriorityQueue<Link> linkQueue) {
        EditOperation performedOperation = destinationNode.getLastEditOperation();
        if(performedOperation == EditOperation.Insert){
            sourceNode.maybeAddNextLink(linkQueue);
        }

        return destinationNode;
    }

    @Override
    protected int getDepth() {
        return destinationNode.getDepth();
    }

    @Override
    protected int getPriority() {
        int priority = 0;
        switch (destinationNode.getLastEditOperation()){
            case Delete:
                priority = 0;
                break;
            case Match:
                priority = 1;
                break;
            case Insert:
                priority = 2;
                break;
        }

        return priority;
    }

    @Override
    public boolean isOnSuggestionFrontier() {
        EditOperation destinationOperation = destinationNode.getLastEditOperation();
        EditOperation sourceOperation = sourceNode.getLastEditOperation();

        return (destinationOperation == EditOperation.Match
            && (sourceOperation == EditOperation.Match
            || (sourceOperation == EditOperation.Delete
            && sourceNode.isSubstitution())
            || sourceOperation == EditOperation.Insert))
            || destinationNode.isSubstitution();
    }


    @Override
    public String toString(){
        return "Link to: " + destinationNode + ", " + super.toString();
    }

    @Override
    public boolean isValid(QueryString queryString) {
        return true;
    }
}
