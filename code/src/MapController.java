import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class MapController {

    @FXML
    private Canvas mapCanvas;

    private MazeRandom maze;
    private boolean fogEnabled = true;

    public void setMaze(MazeRandom maze) {
        this.maze = maze;
        drawMaze();
    }
    

    @FXML
    private void toggleFog() {
        fogEnabled = !fogEnabled;
        drawMaze();
    }


	public void drawMaze() {
	    GraphicsContext gc = mapCanvas.getGraphicsContext2D();
	    gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
	
	    MazeRandom.Block[][] grid = maze.getGrid();
	    int[] pos = maze.getPlayerPosition_Sample();
	
	    double cell = 40;
	
	    for (int row = 0; row < 10; row++) {
	        for (int col = 0; col < 10; col++) {
	
	            MazeRandom.Block b = grid[row][col];
	            double px = col * cell;
	            double py = row * cell;
	
	            gc.setFill(Color.LIGHTGRAY);
	            gc.fillRect(px, py, cell, cell);
	
	            if (fogEnabled && !b.isVisitedByPlayer()) {
	                gc.setFill(Color.rgb(120, 120, 120, 0.7)); 
	                gc.fillRect(px, py, cell, cell);
	                continue; 
	            }
	
	            gc.setStroke(Color.BLACK);
	            if (b.getTop())    gc.strokeLine(px, py, px + cell, py);
	            if (b.getBottom()) gc.strokeLine(px, py + cell, px + cell, py + cell);
	            if (b.getLeft())   gc.strokeLine(px, py, px, py + cell);
	            if (b.getRight())  gc.strokeLine(px + cell, py, px + cell, py + cell);
	
	            if (pos[0] == row && pos[1] == col) {
	                gc.setFill(Color.RED);
	                gc.fillOval(px + 10, py + 10, 20, 20);
	            }
	        }
	    }
	}


}
