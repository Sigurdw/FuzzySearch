package Clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: Sigurd Wien
 * Date: 11.04.13
 * Time: 14:31
 */
public class Cluster {
    private float defaultDistance = 0;

    public List<String> termList = new ArrayList<String>();

    public float[] vectorCoordinate;

    public final int clusterId;

    public Cluster(int clusterId){
        this.clusterId = clusterId;
    }

    public void calculateDefaultDistance(){
        defaultDistance = 0;
        for(int i = 0; i < vectorCoordinate.length; i++){
            defaultDistance += Math.pow(vectorCoordinate[i], 2);
        }
    }

    public float getDistance(TermDocumentVector termDocumentVector){
        float squaredLengthDiff = 0;
        for(int i = 0; i < termDocumentVector.documentIds.length; i++){
            float clusterScore = vectorCoordinate[termDocumentVector.documentIds[i]];
            float termScore = termDocumentVector.scores[i];
            squaredLengthDiff += Math.pow(termScore, 2);
            squaredLengthDiff -= 2 * clusterScore * termScore;
        }

        return defaultDistance + squaredLengthDiff;
    }

    public void recalculateCoordinate(Map<String, TermDocumentVector> freqMap){
        if(termList.size() != 0){
            clearCurrentCoordinate();
            addVectors(freqMap);
            normalize();
        }
    }

    private void clearCurrentCoordinate(){
        for(int i = 0; i < vectorCoordinate.length; i++){
            vectorCoordinate[i] = 0;
        }
    }

    private void addVectors(Map<String, TermDocumentVector> freqMap) {
        for(String term : termList){
            TermDocumentVector termVector = freqMap.get(term);
            addVector(termVector);
        }
    }

    private void normalize() {
        for(int i = 0; i < vectorCoordinate.length; i++){
            vectorCoordinate[i] /= termList.size();
        }
    }

    public void addVector(TermDocumentVector termDocumentVector){
        for(int i = 0; i < termDocumentVector.documentIds.length; i++){
            vectorCoordinate[termDocumentVector.documentIds[i]] += termDocumentVector.scores[i];
        }
    }
}
