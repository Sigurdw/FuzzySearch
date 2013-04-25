package Clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

/**
 * User: Sigurd Wien
 * Date: 11.04.13
 * Time: 14:28
 */
public final class KMeansClustering {

    public static Cluster[] performClustering(int numberOfClusters, int numberOfDocuments, final Map<String, TermDocumentVector> freqMap) throws InterruptedException {
        List<TermDocumentVector> terms = new ArrayList<TermDocumentVector>();
        List<String> termList = new ArrayList<String>();
        for(TermDocumentVector term : freqMap.values()){
            terms.add(term);
        }

        for(String term : freqMap.keySet()){
            termList.add(term);
        }

        float[][] clusterCoordinates = initializeRandomSeeds(numberOfClusters, numberOfDocuments, terms);
        final Cluster[] clusters = new Cluster[numberOfClusters];
        for(int i = 0; i < numberOfClusters; i++){
            Cluster cluster = new Cluster(i);
            cluster.vectorCoordinate = clusterCoordinates[i];
            clusters[i] = cluster;
        }

        //ForkJoinPool forkJoinPool = new ForkJoinPool();

        for(int i = 0; i < 10; i++){
            System.out.println("Iteration " + i);
            //ExecutorService executorService = Executors.newFixedThreadPool(4);
            for(int j = 0; j < numberOfClusters; j++){
                clusters[j].termList.clear();
                clusters[j].calculateDefaultDistance();
            }

            for(String term : termList){
                TermDocumentVector termDocumentVector = freqMap.get(term);
                int closestClusterIndex = findClosestCluster(termDocumentVector, clusters);
                clusters[closestClusterIndex].termList.add(term);
            }

            //forkJoinPool.invoke(new IterationTask(freqMap, termList, clusters, 0, terms.size()));

            for(Cluster cluster : clusters){
                cluster.recalculateCoordinate(freqMap);
            }
        }

        return clusters;
    }

    private static float[][] initializeRandomSeeds(final int numberOfClusters, final int numberOfDocuments, List<TermDocumentVector> terms){
        float[][] seeds = new float[numberOfClusters][numberOfDocuments];
        for(int i = 0; i < numberOfClusters; i++){
            Random random = new Random();
            int numberOfDocAdds = 10;
            for(int j = 0; j < numberOfDocAdds; j++){
                int termId = random.nextInt(terms.size());
                TermDocumentVector term = terms.get(termId);
                for(int k = 0; k < term.documentIds.length; k++){
                    seeds[i][term.documentIds[k]] += term.scores[k];
                }
            }

            for(int j = 0; j < numberOfDocuments; j++){
                seeds[i][j] /= numberOfDocAdds;
            }
        }

        return seeds;
    }

    private static int findClosestCluster(TermDocumentVector termCoordinates, Cluster[] clusters){
        int index = 0;
        float minDistance = clusters[index].getDistance(termCoordinates);
        for(int i = 1; i < clusters.length; i++){
            float difference = clusters[i].getDistance(termCoordinates);
            if(difference < minDistance){
                index = i;
                minDistance = difference;
            }
        }

        return index;
    }

    private static final class IterationTask extends RecursiveAction {
        private final List<String> terms;
        private final Map<String, TermDocumentVector> vector;
        private final Cluster[] clusters;
        private final int start;
        private final int end;
        private final static int MaxWorkUnit = 64;

        private IterationTask(Map<String, TermDocumentVector> vector, List<String> terms, Cluster[] clusters, int start, int end) {
            this.vector = vector;
            this.terms = terms;
            this.clusters = clusters;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            if(end - start < MaxWorkUnit){
                for(int i = start; i < end; i++){
                    String term = terms.get(i);
                    TermDocumentVector termDocumentVector = vector.get(term);
                    int closestClusterIndex = findClosestCluster(termDocumentVector, clusters);
                    clusters[closestClusterIndex].termList.add(term);
                }
            }
            else{
                int middle = (start + end) / 2;
                invokeAll(new IterationTask(vector, terms, clusters, start, middle), new IterationTask(vector, terms, clusters, middle, end));
            }
        }
    }
}
