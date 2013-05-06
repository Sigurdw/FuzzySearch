package DataStructure;

import Clustering.ClusteringVector;
import Clustering.TermDocumentVector;
import Query.ISuggestionWrapper;

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

    public static Index read(DataInputStream dataInputStream, IIndexProgressListener progressListener) throws IOException{
        IndexHeader indexHeader = IndexHeader.read(dataInputStream);
        System.out.println(indexHeader.version);
        System.out.println(indexHeader.numberOfClusters);
        List<TrieNode> clusteredIndexes = new ArrayList<TrieNode>(indexHeader.numberOfClusters);
        for(int i = 0; i < indexHeader.numberOfClusters; i++){
            TrieNode index = TrieNode.read(dataInputStream);
            clusteredIndexes.add(index);
            progressListener.setReadProgress(i * 100 / indexHeader.numberOfClusters);
        }

        return new Index(indexHeader, clusteredIndexes);
    }

    public ArrayList<ISuggestionWrapper> getAllTerms() {
        ArrayList<ISuggestionWrapper> terms = new ArrayList<ISuggestionWrapper>();
        for(TrieNode clusterIndex : clusteredIndexes){
            terms.addAll(clusterIndex.getAllTerms());
        }

        return terms;
    }

    public ArrayList<String> getRandomIndexTerms(int maxNumberOfTerms){
        ArrayList<ISuggestionWrapper> suggestionWrappers = new ArrayList<ISuggestionWrapper>();
        for(TrieNode trieNode : clusteredIndexes){
            trieNode.getAllTerms(suggestionWrappers);
        }

        int actualNumberOfTerms = Math.min(maxNumberOfTerms, suggestionWrappers.size());
        ArrayList<String> randomTerms = new ArrayList<String>(actualNumberOfTerms);
        for(int i = 0; i < actualNumberOfTerms; i++){
            randomTerms.add(getRandomIndexTerm(suggestionWrappers));
        }

        return randomTerms;
    }

    private String getRandomIndexTerm(ArrayList<ISuggestionWrapper> indexTerms){
        int index = (int)(Math.random() * indexTerms.size());
        return indexTerms.get(index).getSuggestion();
    }
}
