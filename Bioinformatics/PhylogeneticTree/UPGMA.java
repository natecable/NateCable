import java.util.*;
public class UPGMA {
    Matrix matrix;
    ArrayList<Cluster> clusters;
    List<String> names;

    public UPGMA(Matrix matrix, List<String> seq) {
        this.matrix = matrix;
        this.clusters = new ArrayList<>();
        this.names = seq;
        for (int i = 0; i < matrix.size(); i++) {
            clusters.add(new Cluster(i));
        }
    }


    public String run() {
        while (clusters.size() > 1) {
            try{
                clusters.sort(Comparator.comparingDouble(c -> clusters.stream().filter(other -> other != c).mapToDouble(other -> matrix.getDistance(clusters.indexOf(c), clusters.indexOf(other))).min().orElse(Double.MAX_VALUE)));
            }catch(Exception e){
                continue;
            }
            Cluster c1 = clusters.get(0);
            Cluster c2 = clusters.stream()
                    .filter(c -> c != c1)
                    .min(Comparator.comparingDouble(c -> matrix.getDistance(clusters.indexOf(c1), clusters.indexOf(c))))
                    .orElse(null);
            if (c2 == null) {
                throw new IllegalStateException("Could not find a cluster to merge with " + c1);
            }
    
            double distance = matrix.getDistance(clusters.indexOf(c1), clusters.indexOf(c2));
    
            // Merge clusters
            Cluster newCluster = new Cluster(c1, c2, distance);
            clusters.add(newCluster);
            clusters.remove(c1);
            clusters.remove(c2);

            // Update distance matrix
            for (Cluster cluster : clusters) {
                if (cluster != newCluster) {
                    double newDistance = calculateAverageDistance(newCluster, cluster);
                    matrix.setDistance(clusters.indexOf(newCluster), clusters.indexOf(cluster), newDistance);
                }
            }
        }
        
        return toNewick(clusters.get(0))+ ";";

    }

    private double calculateAverageDistance(Cluster c1, Cluster c2) {
        double totalDistance = 0;
        for (int i : c1.indices) {
            for (int j : c2.indices) {
                totalDistance += matrix.getDistance(i, j);
            }
        }

        return totalDistance / (c1.indices.size() * c2.indices.size());
    }

    

    private String toNewick(Cluster cluster) {
        if (cluster.leftChild == null && cluster.rightChild == null) {
            return names.get(cluster.indices.get(0));
        } else {
            String leftChildNewick = toNewick(cluster.leftChild);
            String rightChildNewick = toNewick(cluster.rightChild);
        
            double leftChildBranchLength = cluster.height - cluster.leftChild.height;
            double rightChildBranchLength = cluster.height - cluster.rightChild.height;
        
            return "(" + leftChildNewick + ":" + String.format("%.5f", leftChildBranchLength) + "," + rightChildNewick + ":" + String.format("%.5f", rightChildBranchLength) + ")";
        }
    }
    
    
}
