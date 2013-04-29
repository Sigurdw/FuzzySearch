package Clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Sigurd Wien
 * Date: 06.04.13
 * Time: 22:36
 */
public class DocumentToClusterMap {
    private Map<String, List<Cluster>> documentToClusterMap = new HashMap<String, List<Cluster>>();

    public void addDocument(String documentPath, Cluster cluster){
        List<Cluster> clusters = documentToClusterMap.get(documentPath);
        if(clusters == null){
            clusters = new ArrayList<Cluster>();
            documentToClusterMap.put(documentPath, clusters);
        }

        clusters.add(cluster);
    }

    public List<Cluster> getClusters(String documentPath){
        return documentToClusterMap.get(documentPath);
    }
}
