import java.util.Objects;

public class Edge_Sample {
    private Node_Sample a_Sample;
    private Node_Sample b_Sample;

    public Edge_Sample(Node_Sample a_Sample, Node_Sample b_Sample) {
        if (b_Sample.getX_Sample() < a_Sample.getX_Sample() ||
            (b_Sample.getX_Sample() == a_Sample.getX_Sample() && b_Sample.getY_Sample() < a_Sample.getY_Sample())) {
            this.a_Sample = b_Sample;
            this.b_Sample = a_Sample;
        } else {
            this.a_Sample = a_Sample;
            this.b_Sample = b_Sample;
        }
    }

    public Node_Sample getA_Sample() { return a_Sample; }
    public Node_Sample getB_Sample() { return b_Sample; }

    @Override
    public boolean equals(Object obj_Sample) {
        if (this == obj_Sample) return true;
        if (!(obj_Sample instanceof Edge_Sample)) return false;
        Edge_Sample other_Sample = (Edge_Sample) obj_Sample;
        return a_Sample.equals(other_Sample.a_Sample) && b_Sample.equals(other_Sample.b_Sample);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a_Sample, b_Sample);
    }

    @Override
    public String toString() {
        return a_Sample + " <-> " + b_Sample;
    }
}
