import java.util.*;
public class Matrix {
    double[][] distances;
    int size;

    public Matrix(int size) {
        this.distances = new double[size][size];
        this.size = size;
        for(int i = 0; i < size; i++){
            distances[i][i] = 0;
        }
    }

    public void printMatrix(){
        System.out.println("\n\n");
        for(int i = 0; i < size; i++){
            System.out.println(Arrays.toString(distances[i]));
        }
        System.out.println("\n\n");
    }

    public double getDistance(int i, int j) {
        return distances[i][j];
    }

    public void setDistance(int i, int j, double distance) {
        distances[i][j] = distance;
        distances[j][i] = distance; // Matrix is symmetric
    }

    public int size() {
        return distances.length;
    }
}