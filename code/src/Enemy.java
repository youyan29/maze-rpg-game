
public abstract class Enemy {
    private String name;
    private int hp;
    private final int maxHp;
    private int dex; 
    private String skillName;

    public Enemy(String name, int hp, int dex, String skillName) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.dex = dex;
        this.skillName = skillName;
    }

    public abstract int attack();

    public void takeDamage(int damage) {
    	hp = Math.max(0, hp - damage);
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public static Enemy createBoss() {
        return new Boss();
    }

    
    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getDex() { return dex; }
    public String getSkillName() { return skillName; }
    public int getMaxHp() { return maxHp; }

    public void setHp(int hp) { this.hp = hp; }   
}


class Goblin extends Enemy {
    public Goblin() {
        super("Goblin", 5, 5, "Club Smash");
    }

    @Override
    public int attack() {
        int damage = (int)(Math.random() * 1 + 1);
        System.out.println(getName() + " uses " + getSkillName() + "! Deals " + damage + " damage!");
        return damage;
    }
}


class Ghost extends Enemy {
    public Ghost() {
        super("Ghost", 7, 7, "Shadow Blast");
    }

    @Override
    public int attack() {
        int damage = (int)(Math.random() * 3 + 1); 
        System.out.println(getName() + " uses " + getSkillName() + "! Deals " + damage + " damage!");
        return damage;
    }
}


class Skeleton extends Enemy {
    public Skeleton() {
        super("Skeleton", 8, 5, "Bone Strike");
    }

    @Override
    public int attack() {
        int damage = (int)(Math.random() * 5 + 1); 
        System.out.println(getName() + " uses " + getSkillName() + "! Deals " + damage + " damage!");
        return damage;
    }
}


class Boss extends Enemy {
    public Boss() {
        super("Dragon Boss", 15, 2, "Flame Breath");
    }

    @Override
    public int attack() {
        int damage = (int)(Math.random() * 5 + 3); 
        System.out.println(getName() + " uses " + getSkillName() + "! Deals " + damage + " massive damage!");
        return damage;
    }
}

