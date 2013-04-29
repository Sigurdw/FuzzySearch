package DataStructure;

import Clustering.ClusteringVector;
import Clustering.TermDocumentVector;
import Query.SuggestionWrapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Index {
    public final IndexHeader indexHeader;
    private final List<TrieNode> clusteredIndexes;

    public Index(int version, int numberOfClusters){
        indexHeader = new IndexHeader(version, numberOfClusters);
        clusteredIndexes = new ArrayList<TrieNode>(numberOfClusters);
        for(int i = 0; i < numberOfClusters; i++){
            clusteredIndexes.add(new InternalTrieNode());
        }
    }

    private Index(IndexHeader indexHeader, List<TrieNode> clusteredIndexes){
        this.indexHeader = indexHeader;
        this.clusteredIndexes = clusteredIndexes;
    }

    public void addTerm(String term, TermDocumentVector termDocumentVector, ClusteringVector clusteringVector){
        int clusterId = clusteringVector.clusterId;
        clusteredIndexes.get(clusterId).addNewTerm(term, termDocumentVector, clusteringVector);
    }

    public TrieNode getIndexCluster(int clusterId){
        return clusteredIndexes.get(clusterId);
    }

    public float getMaxRank(){
        float maxRank = clusteredIndexes.get(0).getRank();
        for(int i = 1; i < indexHeader.numberOfClusters; i++){
            float candidateRank = clusteredIndexes.get(i).getRank();
            if(candidateRank > maxRank){
                maxRank = candidateRank;
            }
        }

        return maxRank;
    }

    public void write(DataOutputStream dataOutputStream) throws IOException{
        indexHeader.write(dataOutputStream);
        for(TrieNode trieNode : clusteredIndexes){
            trieNode.write(dataOutputStream);
        }
    }

    public static Index read(DataInputStream dataInputStream) throws IOException{
        IndexHeader indexHeader = IndexHeader.read(dataInputStream);
        System.out.println(indexHeader.version);
        System.out.println(indexHeader.numberOfClusters);
        List<TrieNode> clusteredIndexes = new ArrayList<TrieNode>(indexHeader.numberOfClusters);
        for(int i = 0; i < indexHeader.numberOfClusters; i++){
            TrieNode index = TrieNode.read(dataInputStream);
            clusteredIndexes.add(index);
        }

        return new Index(indexHeader, clusteredIndexes);
    }

    public ArrayList<SuggestionWrapper> getAllTerms() {
        ArrayList<SuggestionWrapper> terms = new ArrayList<SuggestionWrapper>();
        for(TrieNode clusterIndex : clusteredIndexes){
            terms.addAll(clusterIndex.getAllTerms());
        }

        return terms;
    }
}
