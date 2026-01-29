import java.util.Random;

public class Item {
    public enum ItemType {
        HP_POTION, MP_POTION, SHIELD, SPEED_SHOES, CLOAK;

		String getItemEffectDescription() {
			switch(this) {
            	case HP_POTION:
            		return "Restore 5 HP";
            	case MP_POTION:
            		return "Restore 2 MP";
            	case CLOAK:
            		return "Skip one battle" + "\n" + "(Useless for boss)";
            	case SHIELD:
            		return "DEF + 2";
            	case SPEED_SHOES:
            		return "DEX + 2";
			};		
			return null;
		}
    }

    private ItemType type;
    private int value; 

    public Item(ItemType type, int value) {
        this.type = type;
        this.value = value;
    }

    public ItemType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }
    
    public String getEffectDescription() {
        return switch (type) {
            case HP_POTION -> "Restore 5 HP";
            case MP_POTION -> "Restore 2 MP";
            case CLOAK -> "Skip a battle (useless on boss)";
		    default -> throw new IllegalArgumentException("Unexpected value: " + type);
        };
    }

    public static Item getRandomItem() {
        Random r = new Random();
        double roll = r.nextDouble() * 100;

        if (roll <= 40) {
            return new Item(ItemType.HP_POTION, 5);
        } else if (roll <= 65) {
            return new Item(ItemType.MP_POTION, 5);
        } else if (roll <= 80) {
            return new Item(ItemType.CLOAK, 1); 
        } else if (roll <= 90) {
            return new Item(ItemType.SHIELD, 2);
        } else {
            return new Item(ItemType.SPEED_SHOES, 2); 
        }
    }


    public void use(Character player) {
        switch (type) {
            case HP_POTION:
            	int newHp = Math.min(player.getHp() + value, player.getMaxHp());
                int healedHp = newHp - player.getHp();
                player.setHp(newHp);
                System.out.println(player.getName() + " used a health potion. HP +" + healedHp + " → " + player.getHp() + "/" + player.getMaxHp());
                break;

            case MP_POTION:
            	int newMp = Math.min(player.getMp() + value, player.getMaxMp());
                int healedMp = newMp - player.getMp();
                player.setMp(newMp);
                System.out.println(player.getName() + " used a mana potion. MP +" + healedMp + " → " + player.getMp() + "/" + player.getMaxMp());
                break;

            case SHIELD:
                player.setDef(player.getDef() + value);
                System.out.println(player.getName() + " equipped a shield. DEF +" + value);
                break;

            case SPEED_SHOES:
                player.setDex(player.getDex() + value);
                System.out.println(player.getName() + " equipped speed shoes. DEX +" + value);
                break;

            case CLOAK:
                System.out.println(player.getName() + " used a cloak and skipped one battle!");
                break;
        }
    }
}
