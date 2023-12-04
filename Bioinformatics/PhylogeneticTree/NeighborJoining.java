import java.util.*;
public class NeighborJoining {
    private double[][] distanceMatrix;
    private List<String> taxaList;
    public HashMap<String, Node> nodes = new HashMap<>();

    public NeighborJoining(double[][] distanceMatrix, List<String> taxaList) {
        this.distanceMatrix = distanceMatrix;
        this.taxaList = taxaList;
    }



    public Node computeTree() {
        
        int cnt = 1;
        while (taxaList.size() > 2) {
            // Step 1: Compute the matrix of total branch lengths
            double[] totalDistances = new double[taxaList.size()];
            for (int i = 0; i < taxaList.size(); i++) {
                for (int j = 0; j < taxaList.size(); j++) {
                    totalDistances[i] += distanceMatrix[i][j];
                }
            }

            // Step 2: Find the pair of taxa that minimizes the NJ criterion
            int minI = -1, minJ = -1;
            double minCriterion = Double.MAX_VALUE;
            for (int i = 0; i < taxaList.size(); i++) {
                for (int j = i + 1; j < taxaList.size(); j++) {
                    double criterion = (taxaList.size() - 2) * distanceMatrix[i][j] - totalDistances[i] - totalDistances[j];
                    if (criterion < minCriterion) {
                        minCriterion = criterion;
                        minI = i;
                        minJ = j;
                    }
                }
            }

            // Step 3: Join the pair of taxa into a new taxon
            double dij = distanceMatrix[minI][minJ];
            double diu = 0.5 * dij + 0.5 * (totalDistances[minI] - totalDistances[minJ]) / (taxaList.size() - 2);
            double dju = dij - diu;

            Node nodeI; 
            Node nodeJ;
            if(nodes.containsKey(taxaList.get(minI))){
                nodeI = nodes.get(taxaList.get(minI));
            }else{
                nodeI = new Node(taxaList.get(minI), diu);
            }
            
            if(nodes.containsKey(taxaList.get(minJ))){
                nodeJ = nodes.get(taxaList.get(minJ));
            }else{
                nodeJ = new Node(taxaList.get(minJ), dju);
            }

            nodes.put(nodeI.label, nodeI);
            nodes.put(nodeJ.label, nodeJ);
            Node nodeU = new Node(String.format("%d", cnt++), 0.0, nodeI, nodeJ);
            nodes.put(nodeU.label, nodeU);

            // Step 4: Update the distance matrix and taxa list
            double[] distancesU = new double[taxaList.size()];
            for (int i = 0; i < taxaList.size(); i++) {
                if (i != minI && i != minJ) {
                    distancesU[i] = 0.5 * (distanceMatrix[minI][i] + distanceMatrix[minJ][i] - dij);
                }
            }
            taxaList.remove(minJ);  // Remove j first because j > i
            taxaList.remove(minI);
            taxaList.add(nodeU.getLabel());
            distanceMatrix = updateDistanceMatrix(distanceMatrix, minI, minJ, distancesU);
        }

        // When there are only two taxa left, join them into the root
        double dij = distanceMatrix[0][1];
        Node root = new Node(String.format("%d", cnt++), 0.0, nodes.get(taxaList.get(0)), nodes.get(taxaList.get(1)));
        nodes.put(root.label, root);
        computeHeight(root, 0);
        return root;

    }

    private void computeHeight(Node node, double parentHeight) {
        if (node == null) {
            return;
        }

        // Set the height of the current node
        node.height = parentHeight + node.length;

        // Recursively compute the height for the children
        computeHeight(node.leftChild, node.height);
        computeHeight(node.rightChild, node.height);
    }
    
    private double[][] updateDistanceMatrix(double[][] oldMatrix, int i, int j, double[] distancesU) {
        int n = oldMatrix.length;
        double[][] newMatrix = new double[n - 1][n - 1];
        int newRow = 0;
        for (int oldRow = 0; oldRow < n; oldRow++) {
            if (oldRow == i || oldRow == j) {
                continue;
            }
            int newCol = 0;
            for (int oldCol = 0; oldCol < n; oldCol++) {
                if (oldCol == i || oldCol == j) {
                    continue;
                }
                newMatrix[newRow][newCol] = oldMatrix[oldRow][oldCol];
                newCol++;
            }
            newMatrix[newRow][n - 2] = distancesU[oldRow];
            newMatrix[n - 2][newRow] = distancesU[oldRow];
            newRow++;
        }
        return newMatrix;
    }
}
    
