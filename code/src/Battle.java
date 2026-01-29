public class Battle {
    private Character player;
    private Enemy enemy;
    public boolean escapeThisBattle = false;

    public Battle(Character player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
    }
    
    public void normalAttack() {
        int dmg = player.basicAttack();
        enemy.takeDamage(dmg);
    }

    public void useSkill() {
        int dmg = player.useSkill();
        if (dmg > 0) enemy.takeDamage(dmg);
    }
}    
