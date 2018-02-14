//package me.masonic.mc.Function.Vitality;
//
//import me.masonic.mc.Core;
//import me.masonic.mc.Function.Reward;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.player.PlayerInteractEvent;
//
//import java.text.MessageFormat;
//
//public class VitalityListener implements Listener {
//
//    @EventHandler
//    private void onJoin(PlayerInteractEvent e) {
//        Player p = e.getPlayer();
//        VitalityRecord vr = VitalityRecord.getInstance(p.getUniqueId());
//
//        if (vr.getProgress(VitalityQuest.LOGIN1.codename) < 100) {
//            vr.setProgress(VitalityQuest.LOGIN1.codename, 100);
//            Reward.send(p, VitalityQuest.LOGIN1.reward);
//            p.sendMessage(Core.getPrefix() + MessageFormat.format("日常活跃度任务§8[ §6{0} §8]奖励已发放", VitalityQuest.LOGIN1.desc));
//        }
//    }
//}
