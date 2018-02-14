package me.masonic.mc.Function;

import me.masonic.mc.Objects.Icons;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class Announce {
    public static void openAn(Player p) {
        final ChestMenu menu = new ChestMenu("    公告");
        Icons.addBaseIcon(menu, "back", 49);
        AnnounceRecord.setContents(menu);
        menu.open(p);
    }
}

enum AnnounceRecord {
    AN1("§8[ §6版本更新记录 v0.2.7 §8]", new ArrayList<>(Arrays.asList(
            "",
            "§7◇ 更新时间: §618.02.13",
            "",
            "§7▽ 基地的§8[ §2后勤仓库 §8]§7系统已上线",
            "§7○ 后勤仓库会自动收容特殊物品",
            "§7○ 如§6武器强化材料§7，§6粘液科技蓝图§7等",
            "",
            "§7○ 当前版本通过§6签到§7等方式获得的蓝图",
            "§7○ 将自动发放至§8[ §2后勤仓库 §8]",
            "§7○ 解锁科技时，背包和仓库内的蓝图均可使用",
            "§7○ 系统优先使用§6背包§7内的蓝图",
            "",
            "§7▽ 最后祝各位任务愉快",
            "§7○ ---- 基地指挥官 Masonic9")), 13),
    AN2("§8[ §6版本更新记录 v0.2.8 §8]", new ArrayList<>(Arrays.asList(
            "",
            "§7◇ 更新时间: §618.02.14",
            "",
            "§7▽ 基地的§8[ §2活跃度 §8]§7原型系统已上线",
            "§7○ 完整版功能将在约一周内开发完毕",
            "§7▽ 优化了部分图标",
            "",
            "§7▽ 最后祝各位新春快乐",
            "§7○ ---- 基地指挥官 Masonic9")), 22);

    String title;
    ArrayList<String> content;
    int slot;

    AnnounceRecord(String title, ArrayList<String> content, int slot) {
        this.title = title;
        this.content = content;
        this.slot = slot;
    }

    static void setContents(ChestMenu menu) {
        for (AnnounceRecord r : AnnounceRecord.values()) {
            menu.addItem(r.slot, r.getIcon());
            menu.addMenuClickHandler(r.slot, (a1, a2, a3, a4) -> false);
        }
    }

    private ItemStack getIcon() {
        ItemStack icon = new ItemStack(Material.ENCHANTED_BOOK).clone();
        ArrayList<String> lores = (ArrayList<String>) this.content.clone();

        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName(this.title);
        lores.add("");
        lores.add("§8[ ModernSky ] announcement");
        meta.setLore(lores);
        icon.setItemMeta(meta);
        return icon;
    }
}
