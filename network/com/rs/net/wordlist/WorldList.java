package com.rs.net.wordlist;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

public class WorldList {

	public static final Object2ObjectArrayMap<Integer, WorldEntry> WORLDS = new Object2ObjectArrayMap<Integer, WorldEntry>();

	public static void init() {
		WORLDS.put(1, new WorldEntry("Local Development", "127.0.0.1", 37, "Main", true)); //Local development world
		WORLDS.put(2, new WorldEntry("Live Server", "127.0.0.1", 37, "Main", true)); //Live world
		WORLDS.put(3, new WorldEntry("PVP", "127.0.0.1", 37, "Main", true)); //PVP world
		for (int i = 4; i <= 199; i++) {
			WORLDS.put(i, new WorldEntry("World " + i, "127.0.0.1", i, "n/a", (i % 2 == 0))); //WORLD randomizing kek
		}
	}

	public static WorldEntry getWorld(int worldId) {
		return WORLDS.get(worldId);
	}
	
	public static final int LOCAL = 1, LIVE = 2, PVP = 3;
}