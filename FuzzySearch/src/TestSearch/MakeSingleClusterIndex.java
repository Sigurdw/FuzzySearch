package TestSearch;
import Clustering.ClusteringVector;
import Clustering.TermDocumentVector;
import DataStructure.Index;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
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
 * Date: 08.06.13
 * Time: 17:41
 */
public class MakeSingleClusterIndex {

    public static void main(String[] args) throws Exception{
        String indexPath = "D:/Index/";
        Directory indexDirectory = FSDirectory.open(new File(indexPath));
        IndexReader indexReader = IndexReader.open(indexDirectory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        Map<String, TermDocumentVector> freqMap = new HashMap<String, TermDocumentVector>();
        int numberOfDocs = indexReader.numDocs();

        TermEnum terms = indexReader.terms();
        int counter = 0;
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

            counter++;
            if(counter % 20000 == 0){
                System.out.println(counter);
            }
        }

        //Map<String, ClusteringVector> clusterMap = new HashMap<String, ClusteringVector>(freqMap.size());

        /*float dimenstionalityNormalizer = (float)Math.sqrt(numberOfDocs);
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
        }*/

        //Create index
        ClusteringVector clusteringVector = new ClusteringVector(1, 0);
        DataStructure.Index index = new Index(1, 1);
        for(String term : freqMap.keySet()){
            TermDocumentVector documentWeights = freqMap.get(term);
            index.addTerm(term, documentWeights, clusteringVector);
        }

        String indexOutputPath = "D:/TermIndex/singleClusterIndex.dat";

        index.write(new DataOutputStream(new FileOutputStream(indexOutputPath)));
    }
}
