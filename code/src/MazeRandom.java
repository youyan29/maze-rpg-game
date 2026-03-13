import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class GameUI extends Application {

    private static Stage primaryStage;

    private MazeAPI maze;
    private Character player;
    private int lastRandomSteps = 0;
    private int remainingSteps = 0; 
    private boolean cameFromTurnScene = false;
    private int recordRemainingSteps = 0;
    private Item.ItemType rewardItem;
    private int gameMode;
    private Stage mapStage;
    private MapController mapController;

    
    @FXML
    private Label classLabel;

    @FXML
    private TextArea infoLabel;
    
    @FXML
    private Label moveInfoLabel;
    
    @FXML
    private Label rewardLabel;
    
    @FXML
    private Label effectLabel;
    
    @FXML
    private Button backButton;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        switchScene("StartGUI.fxml");
        primaryStage.setTitle("Maze Game");
        primaryStage.show();
    }

    private void switchScene(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            loader.setController(this);  
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void buttonSample() {
    	gameMode = 0;
    	switchScene("SelectGUI.fxml");
    }
    
    public void buttonRandom() {
    	gameMode = 1;
    	switchScene("SelectGUI.fxml");
    }
    
    public void handleStart(ActionEvent e) {
        switchScene("ModeChoice.fxml");
    }
    
    public void handleSelectWarrior(ActionEvent e) {
        player = new Warrior();
        switchScene("DescriptionGUI.fxml");
    }

    public void handleSelectMage(ActionEvent e) {
        player = new Mage();
        switchScene("DescriptionGUI.fxml");
    }

    public void handleSelectRanger(ActionEvent e) {
        player = new Ranger();
        switchScene("DescriptionGUI.fxml");
    }

    public void handleSelectRogue(ActionEvent e) {
        player = new Rogue();
        switchScene("DescriptionGUI.fxml");
    }

    public void handleBackToSelect(ActionEvent e) {
        switchScene("SelectGUI.fxml");
    }

    public void handleStartGame(ActionEvent e) {
    	if (gameMode == 0) maze = new Maze_Sample();
    	else {
    		maze = new MazeRandom();
    		openMapWindow();
    	}
        updateDirectionScene();
    }
    
    public void handleBack(ActionEvent e) {
    	handleMove("back");
    }

    public void initialize() {
        if (classLabel != null && infoLabel != null && player != null) {
            classLabel.setText(player.getClassName());
            infoLabel.setText(player.getDescription());
        }
        
        if (moveInfoLabel != null) {
        	int rem = (maze != null ? maze.getRemainingSteps_Sample() : 0);

        	if (rem > 0) {
        	    moveInfoLabel.setText(rem + " steps remaining");  
        	} else if (cameFromTurnScene){
        		moveInfoLabel.setText("Remaining " + recordRemainingSteps + "-step completed");
        		recordRemainingSteps = 0;
                cameFromTurnScene = false;
        	}else if (lastRandomSteps > 0) {
        	    moveInfoLabel.setText("Random " + lastRandomSteps + "-step");  
        	} else {
        	    moveInfoLabel.setText("");
        	}
        }
        
        if (rewardLabel != null && rewardItem != null) {
            rewardLabel.setText("You obtained: " + rewardItem.toString());
            effectLabel.setText(rewardItem.getItemEffectDescription());
        }
        
        if (backButton != null) {
        	backButton.setVisible(canGoBack());
        }
        
        if (mapController != null) {
        	mapController.drawMaze();
        }
    }

    private void handleMove(String dir) {

    	MazeEvent event = maze.move_Sample(dir, player);
    	lastRandomSteps = maze.getLastStepsMoved_Sample();
    	remainingSteps = maze.getRemainingSteps_Sample(); 

        if (event == null) return;

        switch(event.type) {

            case "move":
                updateDirectionScene();
                break;

            case "junction":
                remainingSteps = event.getRemainingSteps();

                if (remainingSteps > 0) {
                	recordRemainingSteps = remainingSteps;
                    showTurnScene();
                } else {
                    updateDirectionScene();
                }
                break;

            case "trap":
            	lastRandomSteps = 0;
                switchScene("TrapGUI.fxml");
                break;

            case "reward":
            	lastRandomSteps = 0;
            	this.rewardItem = event.rewardItem.getType();
            	switchScene("RewardGUI.fxml");
                break;

            case "enemy":
                lastRandomSteps = 0;
                loadBattleScene(event.enemy); 
                break;

            case "boss":
                lastRandomSteps = 0;
                loadBattleScene(event.enemy); 
                break;

            case "dead":
                switchScene("LostGUI.fxml");
                break;
        }
    }
    
    public void handleForward(ActionEvent e) {
        handleMove("forward");
    }

    public void handleLeft(ActionEvent e){
        handleMove("left");
    }

    public void handleRight(ActionEvent e){
        handleMove("right");
    }

    private void updateDirectionScene() {
        int[] pos = maze.getPlayerPosition_Sample();
        Node_Sample np = new Node_Sample(pos[0], pos[1]);
        Map<String, Node_Sample> opts = maze.getDirectionOptions(np);

        boolean F = opts.containsKey("forward");
        boolean L = opts.containsKey("left");
        boolean R = opts.containsKey("right");

        // three direction
        if (F && L && R) {
            switchScene("ThreeFolk.fxml");
            return;
        }

        // left and right
        if (!F && L && R) {
            switchScene("LeftRight.fxml");
            return;
        }

        // forward + left
        if (F && L) {
            switchScene("LeftGUI.fxml");
            return;
        }

        // forward + right
        if (F && R) {
            switchScene("RightGUI.fxml");
            return;
        }

        // only forward
        switchScene("StraightGUI.fxml");
    }


    public void handleRandom(ActionEvent e) {
        int[] pos = maze.getPlayerPosition_Sample();
        Node_Sample cur = new Node_Sample(pos[0], pos[1]);

        Map<String, Node_Sample> opts = maze.getDirectionOptions(cur);

        String dir;
        if (opts == null || opts.isEmpty()) {
            dir = "forward";
        } else {
            List<String> keys = new ArrayList<>(opts.keySet());
            dir = keys.get(new java.util.Random().nextInt(keys.size()));
        }

        handleMove(dir);
    }

	public void handleRewardOK(ActionEvent e) {
        updateDirectionScene();
    }

	private void showTurnScene() {
		cameFromTurnScene = true;
	    int[] pos = maze.getPlayerPosition_Sample();
	    Node_Sample np = new Node_Sample(pos[0], pos[1]);
	    Map<String, Node_Sample> opts = maze.getDirectionOptions(np);

	    if (opts == null || opts.size() <= 1) {
	        switchScene("StraightGUI.fxml");
	        return;
	    }

	    if (opts.containsKey("left")) {
	        switchScene("TurnLeftGUI.fxml");
	    }else if (opts.containsKey("right")) {
	        switchScene("TurnRightGUI.fxml");
	    }else {
	        switchScene("StraightGUI.fxml");
	    }	   
	}
	
	private void loadBattleScene(Enemy enemy) {
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("BattleGUI.fxml"));
	        Parent root = loader.load();

	        BattleController controller = loader.getController();
	        controller.setupBattle(player, enemy, this);

	        primaryStage.setScene(new Scene(root));
	        primaryStage.show();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void onBattleWon() {
	    updateDirectionScene();
	}
	
	public void onBossDefeated() {
	    switchScene("WinGUI.fxml");
	}


	public void onBattleLost() {
	    switchScene("LostGUI.fxml");
	}
	
	public boolean canGoBack() {
		if (maze instanceof MazeRandom) {
			return ((MazeRandom)maze).hasFork();
		}
		return false;
	}
	
	private void openMapWindow() {
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("MapGUI.fxml"));
	        Parent root = loader.load();

	        mapController = loader.getController();
	        mapController.setMaze((MazeRandom) maze);

	        mapStage = new Stage();
	        mapStage.setTitle("Maze Map");
	        mapStage.setScene(new Scene(root));
	        mapStage.show();
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}

			
    public static void main(String[] args) {
        launch(args);
    }
}
