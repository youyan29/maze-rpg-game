import java.util.Map;

public interface MazeAPI {
    MazeEvent move_Sample(String dir, Character player);
    Map<String, Node_Sample> getDirectionOptions(Node_Sample pos);
    int[] getPlayerPosition_Sample();
    int getLastStepsMoved_Sample();
    int getRemainingSteps_Sample();
}
