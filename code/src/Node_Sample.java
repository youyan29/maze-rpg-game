import java.util.Objects;

public class Node_Sample {
    private final int x_Sample;
    private final int y_Sample;

    public Node_Sample(int x_Sample, int y_Sample) {
        this.x_Sample = x_Sample;
        this.y_Sample = y_Sample;
    }

    public int getX_Sample() { return x_Sample; }
    public int getY_Sample() { return y_Sample; }

    @Override
    public boolean equals(Object obj_Sample) {
        if (this == obj_Sample) return true;
        if (!(obj_Sample instanceof Node_Sample)) return false;
        Node_Sample other_Sample = (Node_Sample) obj_Sample;
        return x_Sample == other_Sample.x_Sample && y_Sample == other_Sample.y_Sample;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x_Sample, y_Sample);
    }

    @Override
    public String toString() {
        return "(" + x_Sample + "," + y_Sample + ")";
    }
}
