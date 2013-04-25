package Clustering;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * User: Sigurd Wien
 * Date: 06.04.13
 * Time: 21:48
 */
public final class ClusteringVector {
    public final int clusterId;
    public final float[] Vector;

    public ClusteringVector(int numberOfClusters, int clusterId){
        this.clusterId = clusterId;
        Vector = new float[numberOfClusters];
    }

    private ClusteringVector(int clusterId, float[] vector){
        this.clusterId = clusterId;
        Vector = vector;
    }

    public static ClusteringVector defaultValue(int numberOfClusters){
        ClusteringVector clusteringVector = new ClusteringVector(numberOfClusters, 0);
        for (int i = 0; i < numberOfClusters; i++){
            clusteringVector.Vector[i] = 1.0f;
        }

        return clusteringVector;
    }

    public ClusteringVector pairwiseMultiply(ClusteringVector otherVector){
        if(otherVector.Vector.length != Vector.length){
            return null;
        }

        ClusteringVector result = new ClusteringVector(Vector.length, 0);
        for (int i = 0; i < Vector.length; i++){
            result.Vector[i] = Vector[i] * otherVector.Vector[i];
        }

        return result;
    }

    public void write(DataOutputStream dataOutputStream) throws IOException{
        dataOutputStream.writeInt(clusterId);
        dataOutputStream.writeInt(Vector.length);
        for(int i = 0; i < Vector.length; i++){
            dataOutputStream.writeFloat(Vector[i]);
        }
    }

    public static ClusteringVector read(DataInputStream dataInputStream) throws IOException{
        int clusterId = dataInputStream.readInt();
        int vectorLength = dataInputStream.readInt();
        float[] vector = new float[vectorLength];
        for(int i = 0; i < vectorLength; i++){
            vector[i] = dataInputStream.readFloat();

        }

        return new ClusteringVector(clusterId, vector);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder("");
        for(int i = 0; i < Vector.length; i++){
            sb.append(Vector[i] + ", ");
        }

        return "ClusterId: " + clusterId + ", " + sb.toString();
    }
}
