import java.util.*;
public class Cluster{
    List<Integer> indices;
    Cluster leftChild;
    Cluster rightChild;
    double height;

    public Cluster(int index) {
        this.indices = new ArrayList<>();
        this.indices.add(index);
        this.height = 0;
    }

    public Cluster(Cluster c1, Cluster c2, double distance) {
        this.indices = new ArrayList<>();
        this.indices.addAll(c1.indices);
        this.indices.addAll(c2.indices);
    
        this.leftChild = c1;
        this.rightChild = c2;
    
        // Set the height of the new cluster to be half the distance
        this.height = distance / 2;
    
        // Update the heights of the child clusters to reflect the new distance
        if (c1.leftChild == null && c1.rightChild == null) {
            c1.height = this.height - c1.height;
        } else {
            c1.height = this.height - c1.height;
        }
    
        if (c2.leftChild == null && c2.rightChild == null) {
            c2.height = this.height - c2.height;
        } else {
            c2.height = this.height - c2.height;
        }
    }
    

    public void printCluster(){
        System.out.println(Arrays.toString(indices.toArray()));
    }

}