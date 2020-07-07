package com.smallaswater.achievement;

import cn.nukkit.Achievement;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSignPost;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.event.inventory.InventoryOpenEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerEatFoodEvent;
import cn.nukkit.item.food.Food;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.io.File;

/**
 * @author SmallasWater
 */
public class Achievements extends PluginBase implements Listener {

    private static Achievements INSTANCE;
    private Config language;

    @Override
    public void onEnable() {
        INSTANCE = this;
//        saveDefaultConfig();
//        reloadConfig();
        if(!new File(this.getDataFolder()+"/language.yml").exists()){
            this.saveResource("language.yml");
        }
        language = new Config(this.getDataFolder()+"/language.yml",2);
        //注册成就
        for(String s:getLanguage().getAll().keySet()){
            this.getLogger().info("正在注册"+getLanguage().getString(s)+"成就");
            if(Achievement.add(s,new Achievement(getLanguage().getString(s)))){
                this.getLogger().info(getLanguage().getString(s)+"成就注册成功");
            }
        }
        this.getServer().getCommandMap().register("achievement", new Command("ach") {
            @Override
            public boolean execute(CommandSender commandSender, String s, String[] strings) {
                if(commandSender.isOp()){
                    if(strings.length > 1){
                        String player = strings[0];
                        Player player1 = Server.getInstance().getPlayer(player);
                        if(player1 != null){
                            if(Achievement.achievements.containsKey(strings[1])){
                                player1.awardAchievement(strings[1]);
                            }
                        }else{
                            commandSender.sendMessage("该玩家不在线");
                        }
                    }else{
                        commandSender.sendMessage("/ach <玩家> <成就id> 让玩家完成某个成就");
                    }
                }
                return true;
            }
        });
        this.getServer().getPluginManager().registerEvents(this,this);
        try {
            Class.forName("cn.lanink.murdermystery.MurderMystery");
            cn.lanink.murdermystery.addons.manager.AddonsManager.registerAddons(
                    "MurderMysteryAchievements", MurderMysteryAchievements.class);
            getLogger().info("MurderMysteryAchievements已装载！等待MurderMystery加载...");
        } catch (Exception ignored) {

        }
    }

    @EventHandler
    public void killer(EntityDeathEvent event){
        Entity entity = event.getEntity();
        EntityDamageEvent d = entity.getLastDamageCause();
        if(d instanceof EntityDamageByEntityEvent) {
            Entity killer = ((EntityDamageByEntityEvent) d).getDamager();
            if(killer instanceof Player) {


                if (entity instanceof EntityMob){
                    if(!((Player) killer).hasAchievement("mosterKiller")){
                        ((Player) killer).awardAchievement("mosterKiller");
//                        Achievement.broadcast((Player) killer,"mosterKiller");
                    }
                }
                if(entity.getNetworkId() == 11){
                    if(!((Player) killer).hasAchievement("cowKiller")){
                        ((Player) killer).awardAchievement("cowKiller");
                    }
                }
            }
        }
    }
    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if(block instanceof BlockSignPost) {
            if (!player.hasAchievement("thisIsSign")) {
                player.awardAchievement( "thisIsSign");
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        EntityDamageEvent e = player.getLastDamageCause();
        if(e.getCause() == EntityDamageEvent.DamageCause.MAGIC){
            if (!player.hasAchievement("death")) {
                player.awardAchievement("death");
                return;
            }
        }

        if(e.getCause() == EntityDamageEvent.DamageCause.DROWNING){
            if (!player.hasAchievement("jumpWater")) {
                player.awardAchievement("jumpWater");
                return;
            }
        }

        if(e.getCause() == EntityDamageEvent.DamageCause.FIRE){
            if (!player.hasAchievement("frame")) {
                player.awardAchievement("frame");
            }
        }
    }


    @EventHandler
    public void eat(PlayerEatFoodEvent event){
        Food food = event.getFood();
        Player player = event.getPlayer();
        if(food.getRestoreFood() == 4){
            if(player.getFoodData().getLevel() == 0) {
                if (!player.hasAchievement("eat")) {
                    player.awardAchievement("eat");
                }
            }
        }
    }


    @EventHandler
    public void onOpenInventory(InventoryOpenEvent event){
        Player player = event.getPlayer();
        if(!player.hasAchievement("openInventory")){
            player.awardAchievement("openInventory");
        }
    }

    private Config getLanguage() {
        return language;
    }


    public static String getString(String s){
        return INSTANCE.getLanguage().getString(s,"");
    }



}
