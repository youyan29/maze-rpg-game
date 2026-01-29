import java.util.*;

public class MazeRandom implements MazeAPI{

    private Block[][] grid = new Block[10][10];

    private final List<Block> history = new ArrayList<>();
    private int forkCount = 0;

    private int playerX;
    private int playerY;

    private Block startBlock;
    private Block endBlock;

    private int lastStepsMoved = 0;
    private int remainingSteps = 0;
    private final Random rand = new Random();
    
    private final int baseEnemyChance = 15;
    private final int baseRewardChance = 10;
    private int enemyChance = baseEnemyChance;
    private int rewardChance = baseRewardChance;

    public MazeRandom() {
        generateFullMaze();
        Block[] pts = randomStartEnd();
        startBlock = pts[0];
        endBlock = pts[1];

        playerX = startBlock.getX();
        playerY = startBlock.getY();

        history.clear();
        step(startBlock);
        startBlock.markVisitedByPlayer();
    }
    
    private void increaseEnemyRate() {
    	if (enemyChance > 3) enemyChance--;
    }
    private void increaseRewardRate() {
    	if (rewardChance > 3) rewardChance--;
    }
    private void resetEnemyRate() {
    	enemyChance = baseEnemyChance;
    }
    private void resetRewardRate() {
    	rewardChance = baseRewardChance;
    }

    public MazeEvent move_Sample(String direction, Character player) {

        direction = direction.toLowerCase();

        if (direction.equals("back")) {
            return handleBack();
        }

        int stepsToMove = (remainingSteps > 0) ? remainingSteps : generateRandomSteps(player);

        lastStepsMoved = stepsToMove;
        remainingSteps = stepsToMove;

        for (int i = 0; i < stepsToMove; i++) {
            Block now = grid[playerX][playerY];

            // make decision of direction
            Block next = resolveDirection(now, direction);
            
            if (next == null) {
                remainingSteps = 0;
                return new MazeEvent("move");
            }

            playerX = next.getX();
            playerY = next.getY();
            remainingSteps--;

            // back to start point
            if (isStart(playerX, playerY)) {
                history.clear();
                step(startBlock);
                remainingSteps = 0;
                return new MazeEvent("move");
            }

            step(next);
            // Boss
            if (isGoal(playerX, playerY)) {
                remainingSteps = 0;
                return new MazeEvent("boss", Enemy.createBoss());
            }

            // new block
            if (!next.isVisitedByPlayer() && !next.isEnd()) {
                next.markVisitedByPlayer();
                MazeEvent evt = tryRandomEvents(player);
                if (evt != null) {
                    remainingSteps = 0;
                    return evt;
                }
            }



            // dead end
            if (next.isEnd()) {
            	if (!samePos(next, startBlock) && !samePos(next,endBlock)) {
            		player.setHp(Math.max(0, player.getHp() - 1));
            		if (player.getHp() <= 0) {
            			remainingSteps = 0;
            			return new MazeEvent("dead");
            		}
            	}
                Block fork = findLastFork(true);
                playerX = fork.getX();
                playerY = fork.getY();
                remainingSteps = 0;
                return new MazeEvent("trap");
            }


            if (isFork(grid[playerX][playerY])) {
                remainingSteps = 0;
                return new MazeEvent("junction", 0);
            }
        }

        remainingSteps = 0;
        return new MazeEvent("move");
    }


    public Map<String, Node_Sample> getDirectionOptions(Node_Sample pos) {
        Map<String, Node_Sample> map = new HashMap<>();

        int x = pos.getX_Sample();
        int y = pos.getY_Sample();

        Block now = grid[x][y];

        Block prev = getPrevBlock();
        int dx = 0, dy = 0;

        // no facing at beginning
        if (prev != null) {
            dx = x - prev.getX();
            dy = y - prev.getY();
        } else {
            List<Block> open = openNeighbors(now);
            if (!open.isEmpty()) {
                Block first = open.get(0);
                dx = first.getX() - x;
                dy = first.getY() - y;
            }
        }

        // directions
        int fdx = dx,        fdy = dy;         // forward
        int ldx = -dy,       ldy = dx;         // left
        int rdx = dy,        rdy = -dx;        // right

        // test access
        if (canMove(now, fdx, fdy))
            map.put("forward", new Node_Sample(x + fdx, y + fdy));

        if (canMove(now, ldx, ldy))
            map.put("left", new Node_Sample(x + ldx, y + ldy));

        if (canMove(now, rdx, rdy))
            map.put("right", new Node_Sample(x + rdx, y + rdy));

        return map;
    }


    public Block[][] getGrid() {
    	return grid;
    }
    
    public int[] getPlayerPosition_Sample() {
        return new int[]{playerX, playerY};
    }

    public int getLastStepsMoved_Sample() {
        return lastStepsMoved;
    }

    public int getRemainingSteps_Sample() {
        return remainingSteps;
    }
    
    public boolean hasFork() {
    	if (forkCount <= 0) return false;
    	if (isFork(grid[playerX][playerY])) return forkCount > 1;
    	return forkCount > 0;
    }


    private MazeEvent handleBack() {
        // no fork (disable)
        if (forkCount <= 0) {
            return new MazeEvent("move");
        }

        Block fork = findLastFork(false);
        playerX = fork.getX();
        playerY = fork.getY();
        remainingSteps = 0;

        return new MazeEvent("move");
    }

    private int generateRandomSteps(Character player) {
        int dex = Math.max(1, player.getDex());
        return rand.nextInt(dex) + 1;
    }

    private boolean isStart(int x, int y) {
        return x == startBlock.getX() && y == startBlock.getY();
    }

    private boolean isGoal(int x, int y) {
        return x == endBlock.getX() && y == endBlock.getY();
    }


    private Block resolveDirection(Block now, String dir) {
        Block prev = getPrevBlock();

        int x = now.getX();
        int y = now.getY();

        int dx = 0, dy = 0;

        if (prev != null) {
            dx = x - prev.getX();
            dy = y - prev.getY();
        } else {
            List<Block> open = openNeighbors(now);
            if (!open.isEmpty()) {
                Block first = open.get(0);
                dx = first.getX() - x;
                dy = first.getY() - y;
            }
        }

        if (dir.equals("forward")) {} 
        else if (dir.equals("left")) {
            int t = dx;
            dx = -dy;
            dy = t;
        } else if (dir.equals("right")) {
            int t = dx;
            dx = dy;
            dy = -t;
        } else {
            return null;
        }

        int nx = x + dx;
        int ny = y + dy;

        if (nx < 0 || nx >= 10 || ny < 0 || ny >= 10) return null;
        if (!canMove(now, dx, dy)) return null;

        return grid[nx][ny];
    }


    private MazeEvent tryRandomEvents(Character player) {

        // enemy
        if (rand.nextInt(enemyChance) == 0) {
        	resetEnemyRate();
            return new MazeEvent("enemy", randomEnemy());
        }
        else increaseEnemyRate();

        // reward
        if (rand.nextInt(rewardChance) == 0) {
        	resetRewardRate();
        	Item reward = Item.getRandomItem();
            Item.ItemType type = reward.getType();

            if (type == Item.ItemType.SHIELD || type == Item.ItemType.SPEED_SHOES) {
                reward.use(player); 
            } 
            else {
                player.addItem(type);
            }
            return new MazeEvent("reward", reward);
        }
        else {
        	increaseRewardRate();
        }

        return null;
    }

    private Enemy randomEnemy() {
        double r = Math.random();
        if (r < 0.33) return new Goblin();
        if (r < 0.66) return new Skeleton();
        return new Ghost();
    }

    private void generateFullMaze() {
        for (int i = 0; i < 10; i++) {
            for (int k = 0; k < 10; k++) {
                grid[i][k] = new Block(i, k);
            }
        }

        Random r = new Random();
        Block start = grid[r.nextInt(10)][r.nextInt(10)];

        dfsGenerate(start);
    }

    private void dfsGenerate(Block cur) {
        cur.markGeneratedVisited();

        List<Block> ns = findNeighbors(cur);
        Collections.shuffle(ns);

        for (Block nxt : ns) {
            if (!nxt.isGeneratedVisited()) {
                removeWallBetween(cur, nxt);
                dfsGenerate(nxt);
            }
        }
    }

    private void removeWallBetween(Block a, Block b) {
        int dx = b.getX() - a.getX();
        int dy = b.getY() - a.getY();

        if (dx == 1) {
            a.removeBottom();
            b.removeTop();
        } else if (dx == -1) {
            a.removeTop();
            b.removeBottom();
        } else if (dy == 1) {
            a.removeRight();
            b.removeLeft();
        } else if (dy == -1) {
            a.removeLeft();
            b.removeRight();
        }
    }

    private void buildWallBetween(Block a, Block b) {
        int dx = b.getX() - a.getX();
        int dy = b.getY() - a.getY();

        if (dx == 1) {
            a.addBottom();
            b.addTop();
        } else if (dx == -1) {
            a.addTop();
            b.addBottom();
        } else if (dy == 1) {
            a.addRight();
            b.addLeft();
        } else if (dy == -1) {
            a.addLeft();
            b.addRight();
        }
    }

    private void step(Block b) {
        history.add(b);
        if (isFork(b)) forkCount++;
    }

    private Block getPrevBlock() {
        if (history.size() < 2) return null;
        return history.get(history.size() - 2);
    }

    private Block findLastFork(boolean trapBack) {

        int startIndex = history.size() - 1;

        if (!trapBack && isFork(history.get(startIndex))) {
            startIndex--;
        }

        int forkIndex = -1;
        for (int i = startIndex; i >= 0; i--) {
            if (isFork(history.get(i))) {
                forkIndex = i;
                break;
            }
        }

        if (forkIndex == -1) forkIndex = 0;

        Block forkBlock = history.get(forkIndex);

        if (trapBack && forkIndex + 1 < history.size()) {
            Block next = history.get(forkIndex + 1);
            buildWallBetween(forkBlock, next);
        }

        clearHistoryAfter(forkIndex);
        updateForkCount();

        return forkBlock;
    }


    private void clearHistoryAfter(int index) {
        while (history.size() > index + 1) {
            history.remove(history.size() - 1);
        }
    }

    private boolean canMove(Block now, int dx, int dy) {
        if (dx == -1) return !now.getTop();
        if (dx == 1)  return !now.getBottom();
        if (dy == -1) return !now.getLeft();
        if (dy == 1)  return !now.getRight();
        return false;
    }

    
    private void updateForkCount() {
        forkCount = 0;
        for (Block b : history) {
            if (isFork(b)) forkCount++;
        }
    }

    private List<Block> findNeighbors(Block b) {
        List<Block> list = new ArrayList<>();
        int x = b.getX();
        int y = b.getY();

        if (x > 0) list.add(grid[x - 1][y]);
        if (x < 9) list.add(grid[x + 1][y]);
        if (y > 0) list.add(grid[x][y - 1]);
        if (y < 9) list.add(grid[x][y + 1]);

        return list;
    }

    private List<Block> openNeighbors(Block b) {
        List<Block> list = new ArrayList<>();
        int x = b.getX();
        int y = b.getY();

        if (!b.getTop())    list.add(grid[x - 1][y]);
        if (!b.getBottom()) list.add(grid[x + 1][y]);
        if (!b.getLeft())   list.add(grid[x][y - 1]);
        if (!b.getRight())  list.add(grid[x][y + 1]);

        return list;
    }

    private boolean isFork(Block b) {
        int open = openNeighbors(b).size();
        return open >= 3;
    }

    private boolean samePos(Block a, Block b) {
        return a.getX() == b.getX() && a.getY() == b.getY();
    }

//    private Node_Sample toNode(Block b) {
//        return new Node_Sample(b.getX(), b.getY());
//    }

    private Block[] randomStartEnd() {
        int sx, sy, ex, ey;
        Random r = new Random();
        int v = r.nextInt(4);

        if (v == 0) {
            sx = 0; sy = 0; ex = 9; ey = 9;
        } else if (v == 1) {
            sx = 9; sy = 0; ex = 0; ey = 9;
        } else if (v == 2) {
            sx = 0; sy = 9; ex = 9; ey = 0;
        } else {
            sx = 9; sy = 9; ex = 0; ey = 0;
        }

        return new Block[]{grid[sx][sy], grid[ex][ey]};
    }


    public static class Block {
        private final int x;
        private final int y;

        private boolean top = true;
        private boolean bottom = true;
        private boolean left = true;
        private boolean right = true;

        private boolean generatedVisited = false;
        private boolean visitedByPlayer = false;

        Block(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int getX() { return x; }
        int getY() { return y; }

        void markGeneratedVisited() { generatedVisited = true; }
        boolean isGeneratedVisited() { return generatedVisited; }

        boolean isVisitedByPlayer() { return visitedByPlayer; }
        void markVisitedByPlayer() { visitedByPlayer = true; }

        void removeTop()    { top = false; }
        void removeBottom() { bottom = false; }
        void removeLeft()   { left = false; }
        void removeRight()  { right = false; }

        void addTop()       { top = true; }
        void addBottom()    { bottom = true; }
        void addLeft()      { left = true; }
        void addRight()     { right = true; }

        boolean getTop()    { return top; }
        boolean getBottom() { return bottom; }
        boolean getLeft()   { return left; }
        boolean getRight()  { return right; }

        boolean isEnd() {
            int walls = 0;
            if (top) walls++;
            if (bottom) walls++;
            if (left) walls++;
            if (right) walls++;
            return walls == 3;
        }
    }
}