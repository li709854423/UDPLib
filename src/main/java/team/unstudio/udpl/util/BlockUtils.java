package team.unstudio.udpl.util;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;

import team.unstudio.udpl.core.UDPLib;

public interface BlockUtils {
	
	boolean debug = UDPLib.isDebug();
	ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
	
	/**
	 * 0–9 are the displayable destroy stages and each other number means that there is no animation on this coordinate.</br>
	 * Block break animations can still be applied on air; the animation will remain visible although there is no block being broken. However, if this is applied to a transparent block, odd graphical effects may happen, including water losing its transparency. (An effect similar to this can be seen in normal gameplay when breaking ice blocks)</br>
	 * If you need to display several break animations at the same time you have to give each of them a unique Entity ID.</br>
	 * @param player
	 * @param location
	 * @param state
	 * @return
	 */
	static boolean sendBlockBreakAnimation(Player player, Location location, byte state) {
		PacketContainer container = protocolManager.createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
		container.getIntegers().write(0, player.getEntityId());
		container.getBlockPositionModifier().write(0,
				new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
		container.getBytes().write(0, state);

		try {
			protocolManager.sendServerPacket(player, container);
			return true;
		} catch (InvocationTargetException e) {
			if (debug)
				e.printStackTrace();
		}
		return false;
	}
}
