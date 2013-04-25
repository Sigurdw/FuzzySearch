package DataStructure.Tests;

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

import Clustering.TermDocumentVector;
import DataStructure.InternalTrieNode;
import DataStructure.TrieNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TrieNodeTest {

    TrieNode root;

    @Before
    public void setUp(){
        char rootChar = 1;
        root = new InternalTrieNode(rootChar);
    }

    @Test
    public void addTermTest(){
        /*String testTerm1 = "aa";
        String testTerm2 = "bb";
        String testTerm3 = "ab";
        TermDocumentVector documentVector1 = new TermDocumentVector();
        documentVector1.setDocument(0, 0.5f);
        documentVector1.setDocument(1, 0.7f);

        TermDocumentVector documentVector2 = new TermDocumentVector();
        documentVector1.setDocument(0, 0.7f);
        documentVector1.setDocument(1, 0.5f);

        root.addNewTerm(testTerm1, freq1, docId);
        root.addNewTerm(testTerm2, freq2, docId);
        assert root.getRank() == 2;
        assert root.getNumberOfChildren() == 2;
        assert root.getMatchChildIndex('a') == 1;
        assert root.getMatchChildIndex('b') == 0;

        root.addNewTerm(testTerm3, 3, docId);

        assert root.getRank() == 3;
        assert root.getNumberOfChildren() == 2;
        assert root.getMatchChildIndex('a') == 0;
        assert root.getMatchChildIndex('b') == 1; */
    }

    /*@Test
    public void serializationTest() throws IOException, FileNotFoundException{
        root.addNewTerm("abcd", 3, 0);
        root.addNewTerm("dcba", 2, 0);

        File indexFile = new File("FuzzyTestIndex.txt");
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;

        try{
            dataOutputStream = new DataOutputStream(new FileOutputStream(indexFile));
            root.write(dataOutputStream);
            dataOutputStream.close();

            dataInputStream = new DataInputStream(new FileInputStream(indexFile));
            root = TrieNode.read(dataInputStream);
            dataInputStream.close();

            Assert.assertEquals(2, root.getNumberOfChildren());
            Assert.assertEquals(0, root.getMatchChildIndex('a'));
            Assert.assertEquals(1, root.getMatchChildIndex('d'));
        }
        finally {
            if(dataOutputStream != null){
                dataOutputStream.close();
            }

            if(dataInputStream != null){
                dataInputStream.close();
            }

            if(indexFile.exists()){
                indexFile.delete();
            }
        }


    }

    @Test
    public void nodeOrderTest(){
        root.addNewTerm("aa", 1, 0);
        root.addNewTerm("bb", 1, 0);
        root.addNewTerm("cc", 1, 0);
        root.addNewTerm("bb", 2, 0);
        root.addNewTerm("cc", 1, 0);
        root.addNewTerm("bb", 1, 1);
        root.addNewTerm("cc", 1, 0);

        Assert.assertEquals('c', root.getSortedChild(0).label);
        Assert.assertEquals('b', root.getSortedChild(1).label);
        Assert.assertEquals('a', root.getSortedChild(2).label);
    }*/
}
