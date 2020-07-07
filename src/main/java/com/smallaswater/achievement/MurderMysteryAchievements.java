package com.smallaswater.achievement;

import cn.lanink.murdermystery.addons.AddonsBase;
import cn.lanink.murdermystery.addons.manager.autoregister.RegisterListener;
import cn.lanink.murdermystery.event.MurderPlayerDamageEvent;
import cn.lanink.murdermystery.event.MurderRoomEndEvent;
import cn.lanink.murdermystery.room.GameMode;
import cn.lanink.murdermystery.room.Room;
import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author lt_name
 */
@RegisterListener
public class MurderMysteryAchievements extends AddonsBase implements Listener {

    private final HashMap<Player, HashSet<String>> achievementsCache = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("已启用");
    }

    @Override
    public void onDisable() {

    }

    /**
     * 缓存成就，房间结束后统一发放
     * @param player 玩家
     * @param achievements 成就
     */
    private void addAwardCache(Player player, String achievements) {
        if (!this.achievementsCache.containsKey(player)) {
            this.achievementsCache.put(player, new HashSet<>());
        }
        this.achievementsCache.get(player).add(achievements);
    }

    @EventHandler
    public void onRoomEnd(MurderRoomEndEvent event) {
        Room room = event.getRoom();
        if (room == null) return;
        if (room.getGameMode() == GameMode.INFECTED) {
            room.getPlayers().keySet().forEach(player -> player.awardAchievement("murderMysteryFirstPlayInfected"));
        }else {
            if (room.killKillerPlayer != null && room.getPlayerMode(room.killKillerPlayer) == 2) {
                room.killKillerPlayer.awardAchievement("murderMysteryQualifiedDetective");
            }
        }
        event.getVictoryPlayers().forEach(player -> player.awardAchievement("murderMysteryFirstVictory"));
        event.getDefeatPlayers().forEach(player -> player.awardAchievement("murderMysteryFirstFailure"));
        for (Player player : room.getPlayers().keySet()) {
            if (this.achievementsCache.containsKey(player)) {
                Iterator<String> it = this.achievementsCache.get(player).iterator();
                while (it.hasNext()) {
                    String s = it.next();
                    player.awardAchievement(s);
                    it.remove();
                }
            }
        }
    }

    @EventHandler
    public void onDamage(MurderPlayerDamageEvent event) {
        Room room = event.getRoom();
        Player player = event.getPlayer();
        Player damage = event.getDamage();
        if (room == null || player == null || damage == null) return;
        if (room.getPlayerMode(damage) != 3 && room.getPlayerMode(player) != 3 && room.getPlayerMode(player) != 0) {
            this.addAwardCache(damage, "murderMysteryTeamKill");
        }
    }

}
