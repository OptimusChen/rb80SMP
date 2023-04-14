package com.optimus.rb80SMP.aztec.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;

@Getter
@AllArgsConstructor
public class VillagerLevelUpEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private Villager villager;
    private int level;

    public static HandlerList getHandler(){
        return HANDLERS;
    }

    public static HandlerList getHandlerList(){
        return HANDLERS;
    }

    public static HandlerList getHandlersList(){
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
