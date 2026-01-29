public class MazeEvent {
    public String type;  
    public Enemy enemy; 
    public Item rewardItem; 
    private int remainingSteps;

    public MazeEvent(String type) {
        this.type = type;
    }

    public MazeEvent(String type, Enemy enemy) {
        this.type = type;
        this.enemy = enemy;
    }

    public MazeEvent(String type, Item rewardItem) {
        this.type = type;
        this.rewardItem = rewardItem;
    }
        
    public MazeEvent(String type, int remainingSteps) {
        this.type = type;
        this.remainingSteps = remainingSteps;
    }
        
    public int getRemainingSteps() {
        return remainingSteps;
    }
        
    public Enemy getEnemy() {
        return enemy;
    }
}