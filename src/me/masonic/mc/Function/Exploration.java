package me.masonic.mc.Function;

import me.masonic.mc.Utility.SqlUtil;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;


// 关于MessageFormat的用法
// http://blog.csdn.net/zhiweianran/article/details/8666992
public class Exploration {
    private final static String COL_USER_NAME = "user_name";
    private final static String COL_USER_UUID = "user_uuid";
    private final static String COL_EXPLORE = "exploration";
    private final static String SHEET = "package";

    private String tagname = "";
    private int explore_value = 0;
    private double slimefun_discount = 0;

    public String getTagname() {
        return tagname;
    }

    public int getExplore_value() {
        return explore_value;
    }

    public double getSlimefun_discount() {
        return slimefun_discount;
    }

    public static String getColUserName() {
        return COL_USER_NAME;
    }

    public static String getColUserUuid() {
        return COL_USER_UUID;
    }

    public static String getColExplore() {
        return COL_EXPLORE;
    }

    public static String getSheet() {
        return SHEET;
    }

    /**
     * 探索等级设定
     *
     * @param tagname           称号名称
     * @param explore_value     需要的探索值
     * @param slimefun_discount 粘液科技研究折扣
     */

    public Exploration(String tagname, int explore_value, double slimefun_discount) {
        this.tagname = tagname;
        this.explore_value = explore_value;
        this.slimefun_discount = slimefun_discount;
    }

    public static Exploration EXP_RANK0 = new Exploration("§7无", 0, 0.00);
    public static Exploration EXP_RANK1 = new Exploration("§2探索者", 10, 0.05);
    public static Exploration EXP_RANK2 = new Exploration("§3探索先导", 60, 0.07);
    public static Exploration EXP_RANK3 = new Exploration("§6狩魔猎人", 350, 0.09);
    public static Exploration EXP_RANK4 = new Exploration("§d狩魔宗师", 750, 0.11);
    public static Exploration EXP_RANK5 = new Exploration("§c魔物杀手", 1200, 0.15);

    public static String getExploreTag(Player p) {
        return (getExploreValue(p) > 10 ?
                (getExploreValue(p) > 60 ?
                        (getExploreValue(p) > 350 ?
                                (getExploreValue(p) > 750 ?
                                        (getExploreValue(p) > 1200 ? "§c魔物杀手" : "§d狩魔宗师") : "§6狩魔猎人") : "§3探索先导") : "§2探索者") : "§7无");
    }

    public static int getExploreValue(Player p) {

        try {
            Boolean exist = SqlUtil.ifExist(p.getUniqueId(), SHEET, COL_USER_UUID);
            if (exist) {
                String sql = "SELECT {0} FROM {1} WHERE {2} = ''{3}'' LIMIT 1;";
                ResultSet value = SqlUtil.getResults(MessageFormat.format(sql, COL_EXPLORE, SHEET, COL_USER_UUID, p.getUniqueId().toString()));
                Boolean empty = true;
                if (value == null) {
                    return 0;
                }
                while (value.next()) {
                    // ResultSet processing here
                    empty = false;
                    return value.getInt(1);
                }
                if (empty) {
                    return 0;
                }
            } else {
                String sql = "INSERT INTO {0}(`{1}`, `{2}`, `{3}`) VALUE(''{4}'', ''{5}'', {6})";
                sql = MessageFormat.format(sql, SHEET, COL_USER_UUID, COL_USER_NAME, COL_EXPLORE, p.getUniqueId().toString(), p.getPlayerListName(), 0);
                SqlUtil.update(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void setExploreValue(Player p, int value) {
        String sql = "UPDATE {0} SET {1} = {2} WHERE {3} = ''{4}'';";
        try {
            Boolean exist = SqlUtil.ifExist(p.getUniqueId(), SHEET, COL_USER_UUID);
            if (exist) {
                SqlUtil.update(MessageFormat.format(sql, SHEET, COL_EXPLORE, value, COL_USER_UUID, p.getUniqueId().toString()));
            } else {
                sql = "INSERT INTO {0}({1}, {2}, {3}) VALUE(''{4}'', ''{5}'', {6})";
                SqlUtil.update(MessageFormat.format(sql, SHEET, COL_USER_UUID, COL_USER_NAME, COL_EXPLORE, p.getUniqueId().toString(), p.getPlayerListName(), value));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
