package me.masonic.mc.Function;

import me.masonic.mc.Objects.Icons;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.text.MessageFormat;
import java.util.HashMap;
// - CODENAME
//   - Progress1 进度
//   - Progress2 进度
//   - Progress3 进度
public class Vitality {

    private final static String COL_USER_NAME = "uesr_name";
    private final static String COL_USER_UUID = "user_uuid";
    private final static String COL_VITALITY = "vitality";
    private final static String COL_PROGRESS = "progress";
    private final static String SHEET = "vitality";
    private final static String INIT_QUERY = MessageFormat.format("CREATE TABLE IF NOT EXISTS `{0}` (`{1}` VARCHAR(32) NOT NULL,`{2}` VARCHAR(40) NOT NULL, `{3}` JSON NOT NULL, `{4}` JSON NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8",
            SHEET, COL_USER_NAME, COL_USER_UUID, COL_VITALITY, COL_PROGRESS);

    public static String getColUserName() {
        return COL_USER_NAME;
    }

    public static String getColUserUuid() {
        return COL_USER_UUID;
    }

    public static String getColVitality() {
        return COL_VITALITY;
    }

    public static String getColProgress() {
        return COL_PROGRESS;
    }

    public static String getSheetName() {
        return SHEET;
    }

    public static String getInitQuery() {
        return INIT_QUERY;
    }

//    public void getRawMap() {
//        String sql = "SELECT {0} FROM {1} WHERE {2} = ''{3}'';";
//        String json = SqlUtil.getResults(MessageFormat.format(sql, COL_PROGRESS));
//    }

    public static void openvi(Player p) {
        final ChestMenu menu = new ChestMenu("  日常-活跃度");
        Icons.addBaseIcon(menu, "back", 49);

    }
}

enum VitalityQuest {
    LOGIN1("LOGIN1", "登入游戏", 5, new Reward(50, 0, new HashMap<String, Integer>() {{
    }}), false),

    PLAY1("PLAY1", "在线 1 小时", 10, new Reward(100, 20, new HashMap<String, Integer>() {{
    }}), false),
    PLAY2("PLAY2", "11:00-13:00 期间在线", 5, new Reward(50, 20, new HashMap<String, Integer>() {{
    }}), false),
    PLAY3("PLAY3", "17:00-19:00 期间在线", 5, new Reward(100, 20, new HashMap<String, Integer>() {{
    }}), false),

    SLIMEFUN1("SLIMEFUN1", "制造任意一种太阳能发电机", 15, new Reward(120, 0, new HashMap<String, Integer>() {{
        put("sf3", 1);
    }}), false),
    SLIMEFUN2("SLIMEFUN2", "制造任意一种蓄电池", 15, new Reward(120, 0, new HashMap<String, Integer>() {{
        put("sf13", 4);
    }}), false),
    SLIMEFUN3("SLIMEFUN3", "制造 末影之尘IIIx3", 20, new Reward(80, 20, new HashMap<String, Integer>() {{
        put("sf8", 10);
    }}), false),

    ADVANCEDAB1("ADVANCEDAB1", "解锁任意被动天赋", 20, new Reward(150, 0, new HashMap<String, Integer>() {{
    }}), false),

    KILL1("KILL1", "无伤击杀 20 只僵尸", 15, new Reward(120, 0, new HashMap<String, Integer>() {{
    }}), false),
    POTION1("POTION1", "酿造 3 种药水", 15, new Reward(60, 20, new HashMap<String, Integer>(), new HashMap<Material, Integer>() {{
        put(Material.SAND, 10);
    }}),false);


    String desc;
    String codename;
    int vitality;
    boolean completed;
    Reward reward;

    VitalityQuest(String codename, String desc, int vitality, Reward reward, boolean completed) {
        this.desc = desc;
        this.codename = codename;
        this.vitality = vitality;
        this.reward = reward;
        this.completed = completed;
    }
}

class VitalityListener implements Listener {
    @EventHandler
    private void onJoin(PlayerInteractEvent e) {

    }
}


