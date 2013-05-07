package TestSearch;

import Clustering.Cluster;
import Clustering.ClusteringVector;
import Clustering.KMeansClustering;
import Clustering.TermDocumentVector;
import DataStructure.Index;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class DummyIndex {

    public static void main(String[] args) throws Exception{
        Index index  = new Index(2, 2);
        TermDocumentVector vector = new TermDocumentVector(2);
        vector.setDocument(0, 1.0f, 0);
        vector.setDocument(1, 0.5f, 0);
        ClusteringVector clusteringVector = new ClusteringVector(2, 1);
        clusteringVector.Vector[0] = 1.0f;
        clusteringVector.Vector[1] = 0.5f;

        index.addTerm("protein", vector, clusteringVector);
        vector = new TermDocumentVector(2);
        vector.setDocument(0, 0, 0);
        vector.setDocument(1, 1.1f, 0);
        clusteringVector = new ClusteringVector(2, 0);
        clusteringVector.Vector[0] = 0.1f;
        clusteringVector.Vector[1] = 0.9f;

        index.addTerm("test", vector, clusteringVector);

        vector.setDocument(0, 0, 0);
        vector.setDocument(1, 1.1f, 0);
        clusteringVector = new ClusteringVector(2, 0);
        clusteringVector.Vector[0] = 0.1f;
        clusteringVector.Vector[1] = 0.9f;

        index.addTerm("rotein", vector, clusteringVector);

        vector.setDocument(0, 0, 0);
        vector.setDocument(1, 1.1f, 0);
        clusteringVector = new ClusteringVector(2, 0);
        clusteringVector.Vector[0] = 0.1f;
        clusteringVector.Vector[1] = 0.9f;

        index.addTerm("pro", vector, clusteringVector);



        index.write(new DataOutputStream(new FileOutputStream("testIndex.dat")));
    }
}
