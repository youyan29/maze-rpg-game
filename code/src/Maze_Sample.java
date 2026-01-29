import java.util.*;

public class Maze_Sample implements MazeAPI{
    private Node_Sample player_Sample;
    private final Node_Sample goal_Sample = new Node_Sample(9, 9);
    private int stepsSinceLastEnemy_Sample = 0;
    private int stepsSinceLastReward_Sample = 0;
    private final Random rand_Sample = new Random();
    private int lastStepsMoved_Sample = 0;
    private int remainingSteps_Sample = 0;  

    private final Set<Edge_Sample> allowedEdges_Sample   = new HashSet<>();
    private final Set<Edge_Sample> mainPathEdges_Sample  = new HashSet<>();
    private final Set<Edge_Sample> wrongPathEdges_Sample = new HashSet<>();
    private final List<Node_Sample> mainPathNodes_Sample = new ArrayList<>();

    private final Map<Node_Sample, Node_Sample> traps_Sample = new HashMap<>();

    private final Map<Node_Sample, Map<String, Node_Sample>> directionMap_Sample = new HashMap<>();

    public Maze_Sample() {
        buildGraphFromPolylines_Sample();
        initTraps_Sample();
        initDirectionMap_Sample(); 
        player_Sample = new Node_Sample(0, 0);
        
        System.out.println("(6,0) dirs = " + directionMap_Sample.get(new Node_Sample(6,0)));

    }

    public java.util.Set<String> getAvailableMoves_Sample() {
        java.util.Map<String, Node_Sample> opts = directionMap_Sample.get(player_Sample);
        return (opts == null) ? java.util.Collections.emptySet()
                              : java.util.Collections.unmodifiableSet(opts.keySet());
    }

    private void addPolyline_Sample(List<Node_Sample> points_Sample, boolean isMainPath_Sample) {
        if (isMainPath_Sample) mainPathNodes_Sample.addAll(points_Sample);

        for (int i = 0; i + 1 < points_Sample.size(); i++) {
            Node_Sample a = points_Sample.get(i);
            Node_Sample b = points_Sample.get(i + 1);

            Edge_Sample edge_Sample = new Edge_Sample(a, b);
            allowedEdges_Sample.add(edge_Sample);
            if (isMainPath_Sample) mainPathEdges_Sample.add(edge_Sample);
            else wrongPathEdges_Sample.add(edge_Sample);
            
            Map<String, Node_Sample> dirMap = directionMap_Sample.computeIfAbsent(a, k -> new HashMap<>());
            dirMap.putIfAbsent("forward", b);
        }
    }

    private void buildGraphFromPolylines_Sample() {
        addPolyline_Sample(List.of(
            new Node_Sample(0,0), new Node_Sample(0,1), new Node_Sample(0,2),
            new Node_Sample(0,3), new Node_Sample(0,4), new Node_Sample(0,5),
            new Node_Sample(1,5), new Node_Sample(2,5), new Node_Sample(2,4),
            new Node_Sample(2,3), new Node_Sample(2,2), new Node_Sample(2,1),
            new Node_Sample(3,1), new Node_Sample(3,2), new Node_Sample(3,3),
            new Node_Sample(3,4), new Node_Sample(3,5), new Node_Sample(3,6),
            new Node_Sample(2,6), new Node_Sample(1,6), new Node_Sample(1,7),
            new Node_Sample(1,8), new Node_Sample(2,8), new Node_Sample(3,8),
            new Node_Sample(4,8), new Node_Sample(4,7), new Node_Sample(4,6),
            new Node_Sample(4,5), new Node_Sample(5,5), new Node_Sample(5,4),
            new Node_Sample(5,3), new Node_Sample(5,2), new Node_Sample(5,1),
            new Node_Sample(5,0), new Node_Sample(6,0), new Node_Sample(7,0),
            new Node_Sample(7,1), new Node_Sample(7,2), new Node_Sample(7,3),
            new Node_Sample(7,4), new Node_Sample(7,5), new Node_Sample(7,6),
            new Node_Sample(8,6), new Node_Sample(8,5), new Node_Sample(8,4),
            new Node_Sample(9,4), new Node_Sample(9,5), new Node_Sample(9,6),
            new Node_Sample(9,7), new Node_Sample(8,7), new Node_Sample(7,7),
            new Node_Sample(6,7), new Node_Sample(5,7), new Node_Sample(5,8),
            new Node_Sample(5,9), new Node_Sample(6,9), new Node_Sample(7,9),
            new Node_Sample(8,9), new Node_Sample(9,9)
        ), true);

        addPolyline_Sample(List.of(
            new Node_Sample(0,6), new Node_Sample(0,7), new Node_Sample(0,8),
            new Node_Sample(0,9), new Node_Sample(1,9), new Node_Sample(2,9),
            new Node_Sample(3,9), new Node_Sample(4,9)
        ), false);

        addPolyline_Sample(List.of(
            new Node_Sample(4,4), new Node_Sample(4,3), new Node_Sample(4,2),
            new Node_Sample(4,1), new Node_Sample(4,0), new Node_Sample(3,0),
            new Node_Sample(2,0), new Node_Sample(1,0), new Node_Sample(1,1),
            new Node_Sample(1,2), new Node_Sample(1,3), new Node_Sample(1,4)
        ), false);

        addPolyline_Sample(List.of(new Node_Sample(3,7), new Node_Sample(2,7)), false);

        addPolyline_Sample(List.of(
            new Node_Sample(6,1), new Node_Sample(6,2), new Node_Sample(6,3),
            new Node_Sample(6,4), new Node_Sample(6,5), new Node_Sample(6,6),
            new Node_Sample(5,6)
        ), false);

        addPolyline_Sample(List.of(
            new Node_Sample(8,3), new Node_Sample(9,3), new Node_Sample(9,2),
            new Node_Sample(9,1), new Node_Sample(9,0), new Node_Sample(8,0),
            new Node_Sample(8,1), new Node_Sample(8,2)
        ), false);

        addPolyline_Sample(List.of(
            new Node_Sample(9,8), new Node_Sample(8,8), new Node_Sample(7,8),
            new Node_Sample(6,8)
        ), false);
    }

    private void initTraps_Sample() {
        traps_Sample.put(new Node_Sample(4,9), new Node_Sample(1,5));
        traps_Sample.put(new Node_Sample(1,4), new Node_Sample(5,5));
        traps_Sample.put(new Node_Sample(2,7), new Node_Sample(4,6));
        traps_Sample.put(new Node_Sample(5,6), new Node_Sample(7,0));
        traps_Sample.put(new Node_Sample(8,2), new Node_Sample(7,4));
        traps_Sample.put(new Node_Sample(6,8), new Node_Sample(8,7));
    }

    private void initDirectionMap_Sample() {
        directionMap_Sample.put(new Node_Sample(0,5), Map.of(
            "forward", new Node_Sample(0,6),
            "left", new Node_Sample(1,5)
        ));
        directionMap_Sample.put(new Node_Sample(4,7), Map.of(
            "forward", new Node_Sample(4,6),
            "left", new Node_Sample(3,7)
        ));
        directionMap_Sample.put(new Node_Sample(4,5), Map.of(
            "forward", new Node_Sample(4,4),
            "right", new Node_Sample(5,5)
        ));
        directionMap_Sample.put(new Node_Sample(6,0), Map.of(
            "forward", new Node_Sample(7,0),
            "right", new Node_Sample(6,1)
        ));
        directionMap_Sample.put(new Node_Sample(7,3), Map.of(
            "forward", new Node_Sample(7,4),
            "left", new Node_Sample(8,3)
        ));
        directionMap_Sample.put(new Node_Sample(9,7), Map.of(
            "forward", new Node_Sample(9,8),
            "right", new Node_Sample(8,7)
        ));
    }

    
    public MazeEvent move_Sample(String direction_Sample, Character playerObj_Sample) {
        String dir = direction_Sample.toLowerCase();
        Random rand = new Random();

        int stepsToMove;
        if (remainingSteps_Sample > 0) {
            stepsToMove = remainingSteps_Sample;
        } else {
            int maxSteps = Math.max(1, playerObj_Sample.getDex());
            stepsToMove = rand.nextInt(maxSteps) + 1;
        }

        lastStepsMoved_Sample = stepsToMove;
        remainingSteps_Sample = stepsToMove;
        
        for (int step = 1; step <= stepsToMove; step++) {
        	remainingSteps_Sample--;

            Map<String, Node_Sample> options = directionMap_Sample.get(player_Sample);
            Node_Sample next_Sample = null;

            if (options != null && options.containsKey(dir)) {
                next_Sample = options.get(dir);
            } else {
                break;
            }

            player_Sample = next_Sample;
            stepsSinceLastEnemy_Sample++;

            System.out.println("Step " + step + ": moved " + dir + " to " + player_Sample);

            if (player_Sample.equals(goal_Sample)) {
            	remainingSteps_Sample = 0;
            	return new MazeEvent("boss", Enemy.createBoss());
            }
            if (traps_Sample.containsKey(player_Sample)) {
                Node_Sample tp_Sample = traps_Sample.get(player_Sample);
                int newHp = Math.max(0, playerObj_Sample.getHp() - 1);
                playerObj_Sample.setHp(newHp);                
                player_Sample = tp_Sample;
                
                if (playerObj_Sample.getHp() <= 0) {
                	return new MazeEvent("dead");
                }
                remainingSteps_Sample = 0;
                return new MazeEvent("trap");
            }
            
            
            if (checkForReward_Sample()) {
                Item rewardItem = Item.getRandomItem();
                Item.ItemType type = rewardItem.getType();

                if (type == Item.ItemType.SHIELD || type == Item.ItemType.SPEED_SHOES) {
                    rewardItem.use(playerObj_Sample);
                } else {
                    playerObj_Sample.addItem(type);
                }

                stepsSinceLastReward_Sample = 0;
                remainingSteps_Sample = 0;
                return new MazeEvent("reward", rewardItem);
            }                                  

            if (checkForEnemy_Sample()) {
                Enemy enemy = generateRandomEnemy_Sample();
                stepsSinceLastEnemy_Sample = 0;
                remainingSteps_Sample = 0;
                return new MazeEvent("enemy", enemy);
            }
            
            Map<String, Node_Sample> nextOptions = directionMap_Sample.get(player_Sample);
            if (nextOptions != null && nextOptions.size() > 1) {
            	return new MazeEvent("junction", remainingSteps_Sample);
            }
        }
        remainingSteps_Sample = 0;
        return new MazeEvent("move");
    }


    
    private boolean checkForEnemy_Sample() {
        if (stepsSinceLastEnemy_Sample < 10) {
            return false;
        }

        if (stepsSinceLastEnemy_Sample > 15) {
            return true; 
        }

        return rand_Sample.nextInt(15) == 0;
    }

    private boolean checkForReward_Sample() {
        stepsSinceLastReward_Sample++;
        
        if (stepsSinceLastReward_Sample < 10) {
            return false;
        }
        if (stepsSinceLastReward_Sample > 20) {
            return true;
        }
        return rand_Sample.nextInt(10) == 0;
    }


    private Enemy generateRandomEnemy_Sample() {
        double r_Sample = Math.random();
        if (r_Sample <= 0.5) return new Goblin();
        if (r_Sample <= 0.8 & r_Sample > 0.5) return new Skeleton();
        return new Ghost();
    }

    public Map<String, Node_Sample> getDirectionOptions(Node_Sample pos) {
        return directionMap_Sample.get(pos);
    }
    
    public int[] getPlayerPosition_Sample() {
        return new int[]{player_Sample.getX_Sample(), player_Sample.getY_Sample()};
    }
    
    public int getLastStepsMoved_Sample() {
        return lastStepsMoved_Sample;
    }
    
    public int getRemainingSteps_Sample() {
        return remainingSteps_Sample;
    }
}
