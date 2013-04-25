package Clustering;

import java.io.*;

/**
 * User: Sigurd Wien
 * Date: 10.04.13
 * Time: 14:12
 */
public final class TermDocumentVector {
    public final int[] documentIds;
    public final float[] scores;

    public TermDocumentVector(int numberOfDocumentsContainingTerm){
        this(new int[numberOfDocumentsContainingTerm], new float[numberOfDocumentsContainingTerm]);
    }

    private TermDocumentVector(final int[] documentIds, final float[] scores){
        this.documentIds = documentIds;
        this.scores = scores;
    }

    public void setDocument(int documentId, float score, int index){
        documentIds[index] = documentId;
        scores[index] = score;
    }

    public float getMaxDocRank(){
        float maxRank = 0;
        for(Float score : scores){
            if(score > maxRank){
                maxRank = score;
            }
        }

        return maxRank;
    }

    public void write(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(documentIds.length);
        for(Integer docId : documentIds){
            dataOutputStream.writeInt(docId);
        }

        for (Float score : scores){
            dataOutputStream.writeFloat(score);
        }
    }

    public static TermDocumentVector read(DataInputStream dataInputStream) throws IOException {
        int size = dataInputStream.readInt();
        int[] docIds = new int[size];
        float[] scores = new float[size];
        for(int i = 0; i < size; i++){
            docIds[i] = dataInputStream.readInt();
        }

        for(int i = 0; i < size; i++){
            scores[i] = dataInputStream.readFloat();
        }

        return new TermDocumentVector(docIds, scores);
    }

    @Override
    public String toString(){
        return "to be implemented";
    }
}
