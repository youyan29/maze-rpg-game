import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BattleController {

    @FXML private ImageView enemyImageView;

    @FXML private TextArea playerStatusLabel;
    @FXML private TextArea enemyStatusLabel;

    @FXML private Button hpPotionBtn;
    @FXML private Button mpPotionBtn;
    @FXML private Button cloakBtn;
    @FXML private Button basicAttackBtn;
    @FXML private Button skillBtn;

    private Character player;
    private Enemy enemy;
    private GameUI parent;  
    private Battle battleLogic;


    public void setupBattle(Character player, Enemy enemy, GameUI parent) {
        this.player = player;
        this.enemy = enemy;
        this.parent = parent;
        this.battleLogic = new Battle(player, enemy);

        updateEnemyImage();
        updateStatusPanels();
        updateButtons();
    }

    private void updateStatusPanels() {
        if (player != null) {
            playerStatusLabel.setText(
                "Name: " + player.getName() +
                "\nHP: " + player.getHp() + "/" + player.getMaxHp() +
                "\nMP: " + player.getMp() + "/" + player.getMaxMp() +
                "\nDEF: " + player.getDef() +
                "\nDEX: " + player.getDex() +
                "\nBasicDamage: " + player.getBasicDamage() +
                "\nSkill: " + player.getAdvSkillName() +
                "\n" + player.getAdvSkillDesc()
            );
        }

        if (enemy != null) {
            enemyStatusLabel.setText(
                "Name: " + enemy.getName() +
                "\nHP: " + enemy.getHp() + "/" + enemy.getMaxHp() +
                "\nDEX: " + enemy.getDex() +
                "\nSkill: " + enemy.getSkillName()
            );
        }
    }

    private void updateEnemyImage() {
        if (enemy == null || enemyImageView == null) return;

        String imgPath;
        if (enemy instanceof Boss) {
            imgPath = "/images/Boss.png";
        } else if (enemy.getName().contains("Skeleton")) {
            imgPath = "/images/Skeleton.png";
        } else if (enemy.getName().contains("Ghost")) {
            imgPath = "/images/Ghost.png";
        } else {
            imgPath = "/images/Goblin.png";
        }

        Image img = new Image(getClass().getResourceAsStream(imgPath));
        enemyImageView.setImage(img);
    }

    @FXML
    private void handleHpPotion() {
    	player.useItem(Item.ItemType.HP_POTION);
        updateStatusPanels();
        updateButtons();
    }

    @FXML
    private void handleMpPotion() {
    	player.useItem(Item.ItemType.MP_POTION);
        updateStatusPanels();
        updateButtons();
    }

    @FXML
    private void handleCloak() {
        if (enemy instanceof Boss) {
            return;
        }
        if (player.getItemCount(Item.ItemType.CLOAK) > 0) {
            player.useItem(Item.ItemType.CLOAK);
            updateButtons();
            parent.onBattleWon(); 
        }
    }

    @FXML
    private void handleBasicAttack() {
        performAttackTurn(ActionType.BASIC);
    }

    @FXML
    private void handleSkill() {
        performAttackTurn(ActionType.SKILL);
    }

    private enum ActionType {
        BASIC, SKILL
    }

    private void performAttackTurn(ActionType playerAction) {

        boolean playerFirst = player.getDex() >= enemy.getDex();

        if (playerFirst) {
            playerAttackAction(playerAction);
            updateStatusPanels();
            updateButtons();

            if (!enemy.isAlive()) {

                if (enemy.getName().equals("Dragon Boss")) {
                    System.out.println("Boss defeated!");        
                    parent.onBossDefeated();
                } else {
                    System.out.println("Enemy defeated!");
                    parent.onBattleWon();
                }
                return;
            }

            enemyAttackAction();
            updateStatusPanels();
            updateButtons();

            if (!player.isAlive()) {
                parent.onBattleLost();
            }

        } else {
            enemyAttackAction();
            updateStatusPanels();
            updateButtons();

            if (!player.isAlive()) {
                parent.onBattleLost();
                return;
            }

            playerAttackAction(playerAction);
            updateStatusPanels();
            updateButtons();

            if (!enemy.isAlive()) {

                if (enemy.getName().equals("Dragon Boss")) {
                    System.out.println("Boss defeated!");        
                    parent.onBossDefeated();
                } else {
                    System.out.println("Enemy defeated!");
                    parent.onBattleWon();
                }
                return;
            }

        }
    }

    private void playerAttackAction(ActionType type) {
        if (type == ActionType.BASIC) {
            battleLogic.normalAttack();
        } else if (type == ActionType.SKILL) {
            battleLogic.useSkill();
        }
    }

    private void enemyAttackAction() {
        int dmg = enemy.attack(); 
        int def = player.getDef();
        if (def > 0) {
            int newDef = def - dmg;

            if (newDef >= 0) {
                player.setDef(newDef);
                dmg = 0;
            } else {
                player.setDef(0);
                dmg = -newDef; 
            }
        }
        if (dmg > 0) {
            player.setHp(Math.max(0, player.getHp() - dmg));
        }
    }

    private void updateButtons() {

        int hpCount = player.getItemCount(Item.ItemType.HP_POTION);
        int mpCount = player.getItemCount(Item.ItemType.MP_POTION);
        int cloakCount = player.getItemCount(Item.ItemType.CLOAK);

        hpPotionBtn.setText("HP_POTION (x" + hpCount + ")");
        mpPotionBtn.setText("MP_POTION (x" + mpCount + ")");
        cloakBtn.setText("CLOAK (x" + cloakCount + ")");

        hpPotionBtn.setDisable(hpCount == 0);
        mpPotionBtn.setDisable(mpCount == 0);
        
        if (enemy instanceof Boss) {
            cloakBtn.setDisable(true);
        } else {
            cloakBtn.setDisable(cloakCount == 0);
        }
        
        skillBtn.setDisable(player.getMp() <= 0);
    }
}
