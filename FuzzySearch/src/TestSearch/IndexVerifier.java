package TestSearch;

import Clustering.*;
import Clustering.Cluster;
import DataStructure.Index;
import org.apache.lucene.index.*;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Sigurd Wien
 * Date: 03.04.13
 * Time: 18:40
 */
public class IndexVerifier {

    public static void verifyIndex(String indexPath) throws Exception{
        Directory indexDirectory = FSDirectory.open(new File(indexPath));
        IndexReader indexReader = IndexReader.open(indexDirectory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        Map<String, TermDocumentVector> freqMap = new HashMap<String, TermDocumentVector>();
        int numberOfDocs = indexReader.numDocs();

        TermEnum terms = indexReader.terms();
        while (terms.next()){
            Term term = terms.term();
            TermDocs termDocs = indexReader.termDocs(term);
            ArrayList<Integer> docIds = new ArrayList<Integer>();
            while(termDocs.next()){
                int docId = termDocs.doc();
                docIds.add(docId);
            }

            TermDocumentVector termDocumentVector = new TermDocumentVector(docIds.size());
            for(int i = 0; i < docIds.size(); i++){
                int docId = docIds.get(i);
                Explanation explanation = indexSearcher.explain(new TermQuery(term), docId);
                termDocumentVector.setDocument(docId, explanation.getValue(), i);
            }

            freqMap.put(term.text(), termDocumentVector);
        }

        long startTime = System.nanoTime();
        Cluster[] clusters = KMeansClustering.performClustering(128, numberOfDocs, freqMap);
        long endTime = System.nanoTime();
        System.out.println("Time used: " + ((endTime - startTime) / 1000));

        for (Cluster cluster : clusters){
            System.out.println(cluster.termList.size());
        }

        Map<String, ClusteringVector> clusterMap = new HashMap<String, ClusteringVector>(freqMap.size());

        float dimenstionalityNormalizer = (float)Math.sqrt(numberOfDocs);
        for (Cluster cluster : clusters){
            for(int i = 0; i < cluster.termList.size(); i++){
                String term = cluster.termList.get(i);
                TermDocumentVector termVector = freqMap.get(term);
                ClusteringVector clusteringVector = new ClusteringVector(clusters.length, cluster.clusterId);
                for (int j = 0; j < clusters.length; j++){
                    float distanceToCluster = clusters[j].getDistance(termVector);
                    float clusterScore = (float)Math.pow(Math.E, -(Math.sqrt(distanceToCluster)/ dimenstionalityNormalizer));
                    System.out.println("Distance to cluter " + j + ": " + distanceToCluster + ", cluster score: " + clusterScore);
                    clusteringVector.Vector[j] = clusterScore;
                }

                clusterMap.put(term, clusteringVector);
            }
        }

        //Create index
        DataStructure.Index index = new Index(1, clusters.length);
        for(String term : clusterMap.keySet()){
            TermDocumentVector documentWeights = freqMap.get(term);
            ClusteringVector clusteringVector = clusterMap.get(term);
            index.addTerm(term, documentWeights, clusteringVector);
        }

        String indexOutputPath = "C:/Index/clusteredIndex.dat";

        index.write(new DataOutputStream(new FileOutputStream(indexOutputPath)));
    }

    public static void main(String args[]) throws Exception {
        String indexPath = "C:/Index";
        verifyIndex(indexPath);
    }
}
