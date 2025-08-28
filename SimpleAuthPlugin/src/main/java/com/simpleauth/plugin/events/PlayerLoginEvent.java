package com.simpleauth.plugin.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a player logs in
 */
public class PlayerLoginEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;

    /**
     * Constructor for PlayerLoginEvent
     * 
     * @param player The player
     */
    public PlayerLoginEvent(Player player) {
        this.player = player;
    }

    /**
     * Get the player
     * 
     * @return The player
     */
    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Get the handler list
     * 
     * @return The handler list
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}

