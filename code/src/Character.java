import java.util.HashMap;
import java.util.Map;

public abstract class Character {
    private String name;
    private final int maxHp;
    private final int maxMp;
    private int hp;
    private int mp;
    private int def;
    private int dex;
    private int basicDamage;
    private String advSkillName;
    private String advSkillDesc;
    private boolean cloakReady = false;

    private Map<Item.ItemType, Integer> inventory = new HashMap<>();

    public Character(String name, int hp, int mp, int def, int dex, int basicDamage, String skillName, String skillDesc) {
        this.name = name;
        this.maxHp = hp;
        this.maxMp = mp;
        this.hp = hp;
        this.mp = mp;
        this.def = def;
        this.dex = dex;
        this.basicDamage = basicDamage;
        this.advSkillName = skillName;
        this.advSkillDesc = skillDesc;
    }

    public abstract int useSkill();

    public int basicAttack() {
        int damage = (int) (Math.random() * basicDamage + 1);
        return damage;
    }

    public void takeDamage(int damage) {
    	if (def > 0) {
            int remainingDamage = damage - def;
            def = Math.max(0, def - damage); 
            if (remainingDamage > 0) {
                hp = Math.max(0, hp - remainingDamage);
            }
        } else {
            hp = Math.max(0, hp - damage);
        }}

    public boolean isAlive() { return hp > 0; }

    public void addItem(Item.ItemType type) {
        if (type == Item.ItemType.HP_POTION || 
            type == Item.ItemType.MP_POTION || 
            type == Item.ItemType.CLOAK) {
            
            int count = inventory.getOrDefault(type, 0) + 1;
            inventory.put(type, count);
            System.out.println("Obtained 1" + type + "! (New you have" + count + type + "s)");
            return;
        }

        Map<Item.ItemType, Integer> effectValues = Map.of(
            Item.ItemType.SHIELD, 2,
            Item.ItemType.SPEED_SHOES, 2
        );

        if (effectValues.containsKey(type)) {
            Item item = new Item(type, effectValues.get(type));
            System.out.println("Found " + type + "! Using immediately...");
            item.use(this);
        } else {
            System.out.println("Unknown item type: " + type);
        }
    }

    public boolean useItem(Item.ItemType type) {

        int count = inventory.getOrDefault(type, 0);
        if (count <= 0) {
            System.out.println("No " + type + " left!");
            return false;
        }

        inventory.put(type, count - 1);

        switch (type) {

            case CLOAK:
                cloakReady = true;
                return true;

            case HP_POTION:
                int newHp = Math.min(maxHp, getHp() + 5);
                setHp(newHp);
                return true;

            case MP_POTION:
                int newMp = Math.min(maxMp, getMp() + 2);
                setMp(newMp);
                return true;

            default:
                return false;
        }
    }



    public void printInventory() {
    	boolean hasItem = false;
        System.out.println("Inventory:");

        for (Map.Entry<Item.ItemType, Integer> entry : inventory.entrySet()) {
            if (entry.getValue() > 0) {
                System.out.println(" - " + entry.getKey() + " x" + entry.getValue());
                hasItem = true;
            }
        }

        if (!hasItem) {
            System.out.println(" (empty)");
        }
    }

    public int getItemCount(Item.ItemType type) {
        return inventory.getOrDefault(type, 0);
    }

    public int getInventoryCount() {
        return inventory.values().stream().mapToInt(i -> i).sum();
    }

    
    public boolean isCloakReady() {
        return cloakReady;
    }

    public void consumeCloak() {
        cloakReady = false;
    }
    
    public String getClassName() {
        return this.getName();
    }

    public String getDescription() {
        return "HP: " + hp + "/" + maxHp + "\n"
             + "MP: " + mp + "/" + maxMp + "\n"
             + "DEF: " + def + "\n"
             + "DEX: " + dex + "\n"
             + "Basic Damage: " + basicDamage + "\n"
             + "Skill: " + advSkillName + "\n"
             + "Skill Description: " + advSkillDesc;
    }


    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMp() { return mp; }
    public int getDef() { return def; }
    public int getDex() { return dex; }
    public int getMaxHp() { return maxHp; }
    public int getMaxMp() { return maxMp; }

    public void setHp(int hp) { this.hp = hp; }
    public void setMp(int mp) { this.mp = mp; }
    public void setDef(int def) { this.def = def; }
    public void setDex(int dex) { this.dex = dex; }
    
    public int getBasicDamage() { return basicDamage; }
    public String getAdvSkillName() { return advSkillName; }
    public String getAdvSkillDesc() { return advSkillDesc; }
}



class Warrior extends Character {

    public Warrior() {
        super("Warrior", 30, 4, 8, 4, 5,
              "Power Slash", "Deliver a mighty slash that causes 4–8 damage (costs 2 MP).");
    }

    @Override
    public int useSkill() {
    	if (getMp() < 2) {
            //System.out.println(getName() + " tries to cast " + getAdvSkillName() + ", but not enough MP!");
            return 0;
        }
        
        setMp(getMp() - 2);
        
        int damage = (int)(Math.random() * 5 + 4); 
        //System.out.println(getName() + " uses " + getAdvSkillName() + "! Deals " + damage + " physical damage!");
        return damage;
    }
}


class Mage extends Character {

    public Mage() {
        super("Mage", 22, 10, 8, 6, 3, 
              "Fireball", "Cast a fireball that deals 1–10 magic damage (costs 2 MP).");
    }

    @Override
    public int useSkill() {    	
        if (getMp() < 2) {
            //System.out.println(getName() + " tries to cast " + getAdvSkillName() + ", but not enough MP!");
            return 0;
        }
        
        setMp(getMp() - 2);
        
        int damage = (int)(Math.random() * 6 + 5); 
        //System.out.println(getName() + " casts " + getAdvSkillName() + "! Deals " + damage + " magic damage!");
        return damage;
    }
}


class Ranger extends Character {

    public Ranger() {
        super("Ranger", 27, 6, 8, 8, 4,
              "Piercing Arrow", "Shoot an arrow that deals 2–8 damage (costs 2 MP).");
    }

    @Override
    public int useSkill() {
    	if (getMp() < 2) {
            //System.out.println(getName() + " tries to cast " + getAdvSkillName() + ", but not enough MP!");
            return 0;
        }
        
        setMp(getMp() - 2);
        
        int damage = (int)(Math.random() * 7 + 2);
        //System.out.println(getName() + " fires " + getAdvSkillName() + "! Deals " + damage + " damage!");
        return damage;
    }
}


class Rogue extends Character {

    public Rogue() {
        super("Rogue", 25, 6, 8, 10, 3, 
              "Backstab", "A sneaky attack that deals 2–6 damage (costs 2 MP)." + "\n" + "25% chance to double damage.");
    }

    @Override
    public int useSkill() {
    	if (getMp() < 2) {
            //System.out.println(getName() + " tries to cast " + getAdvSkillName() + ", but not enough MP!");
            return 0;
        }
        
        setMp(getMp() - 2);
        
        int damage = (int)(Math.random() * 5 + 2);
        if (Math.random() < 0.25) {
            damage *= 2;
            //System.out.println(getName() + " lands a CRITICAL " + getAdvSkillName() + "! Deals " + damage + " damage!");
        } else {
            System.out.println(getName() + " uses " + getAdvSkillName() + "! Deals " + damage + " damage!");
        }
        return damage;
    }
}
