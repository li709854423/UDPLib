package team.unstudio.udpl.area;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import team.unstudio.udpl.util.Chunk;

public enum AreaManager {
	
	;
	
	private static final Map<World,WorldAreaManager> managers = new HashMap<>();
	
	public static WorldAreaManager getWorldAreaManager(World world){
		if(managers.containsKey(world)){
			return managers.get(world);
		}else{
			WorldAreaManager manager = new WorldAreaManager(world);
			managers.put(world, manager);
			return manager;
		}
	}
	
	public static WorldAreaManager getWorldAreaManager(String world){
		return getWorldAreaManager(Bukkit.getWorld(world));
	}
	
	public static void addArea(Area area){
		getWorldAreaManager(area.getWorld()).addArea(area);
	}
	
	public static void removeArea(Area area){
		getWorldAreaManager(area.getWorld()).removeArea(area);
	}
	
	public static boolean containArea(Area area){
		return getWorldAreaManager(area.getWorld()).containArea(area);
	}
	
	public static List<Area> getAreas(Location location){
		return getWorldAreaManager(location.getWorld()).getAreas(location);
	}
	
	public static List<Area> getAreas(Chunk chunk){
		return getWorldAreaManager(chunk.getWorld()).getAreas(chunk);
	}
	
	public static List<Area> getAreas(Area area){
		return getWorldAreaManager(area.getWorld()).getAreas(area);
	}
	
	public static void loadAll(){
		for(World world:Bukkit.getWorlds()){
			WorldAreaManager a = new WorldAreaManager(world);
			a.load();
			managers.put(world, a);
		}
	}
	
	public static void saveAll(){
		for(WorldAreaManager a:managers.values()) 
			a.save();
	}
}
