package me.masonic.mc.Function;

import me.masonic.mc.Objects.Icons;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

public class Vitality {

    private final static String COL_USER_NAME = "uesr_name";
    private final static String COL_USER_UUID = "user_uuid";
    private final static String COL_VITALITY = "vitality";
    private final static String COL_RECORD = "record";
    private final static String SHEET = "vitality";
    private final static String INIT_QUERY = MessageFormat.format("CREATE TABLE IF NOT EXISTS `{0}` (`{1}` VARCHAR(32) NOT NULL,`{2}` VARCHAR(40) NOT NULL, `{3}` JSON NOT NULL), `{4}` JSON NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8",
            SHEET, COL_USER_NAME, COL_USER_UUID, COL_VITALITY, COL_RECORD);

    public static String getColUserName() {
        return COL_USER_NAME;
    }

    public static String getColUserUuid() {
        return COL_USER_UUID;
    }

    public static String getColVitality() {
        return COL_VITALITY;
    }

    public static String getColRecord() {
        return COL_RECORD;
    }

    public static String getSheetName() {
        return SHEET;
    }

    public static String getInitQuery() {
        return INIT_QUERY;
    }

//    public Vitality(String codename, String desc, int vitality, boolean completed) {
//        this.codename = codename;
//        this.desc = desc;
//        this.vitality = vitality;
//        this.completed = completed;
//    }

    public static void openvi(Player p) {
        final ChestMenu menu = new ChestMenu("  日常-活跃度");
        Icons.addBaseIcon(menu, "back", 49);

    }

}

enum VitalityQuest {
    LOGIN1("LOGIN1", "登入游戏", 10, false),
    PLAY1("PLAY1", "在线 1 小时", 10, false)
    KILL1("KILL1");

    String desc;
    String codename;
    int vitality;
    boolean completed;

    VitalityQuest(String codename, String desc, int vitality, boolean completed) {
        this.desc = desc;
        this.codename = codename;
        this.vitality = vitality;
        this.completed = completed;
    }
    }

