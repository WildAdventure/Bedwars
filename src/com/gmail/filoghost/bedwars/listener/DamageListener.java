/*
 * Copyright (c) 2020, Wild Adventure
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 4. Redistribution of this software in source or binary forms shall be free
 *    of all charges or fees to the recipient of this software.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gmail.filoghost.bedwars.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.gmail.filoghost.bedwars.Bedwars;
import com.gmail.filoghost.bedwars.arena.Arena;
import com.gmail.filoghost.bedwars.utils.Utils;

public class DamageListener implements Listener {
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntityType() != EntityType.PLAYER) {
			return;
		}
		
		Player damaged = (Player) event.getEntity();
		Arena arena = Bedwars.getArenaByPlayer(damaged);
		
		if (event.getCause() == DamageCause.VOID) {
			event.setDamage(10000);
			return;
		}
		
		if (arena != null) {
			arena.getEvents().onDamage(event, damaged, null);
		} else {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
		if (event.getEntityType() != EntityType.PLAYER) {
			return;
		}
		
		Player attacker = Utils.getRealPlayerDamager(event.getDamager());
		if (attacker == null) {
			return;
		}
		
		Player defender = (Player) event.getEntity();
		Arena defenderArena = Bedwars.getArenaByPlayer(defender);
		if (defenderArena != null) {
			Arena attackerArena = Bedwars.getArenaByPlayer(attacker);
			if (defenderArena == attackerArena) {
				DeathListener.setLastDamager(defender, attacker);
				defenderArena.getEvents().onDamage(event, defender, attacker);
			} else {
				event.setCancelled(true);
			}
		} else {
			event.setCancelled(true);
		}
	}

}
