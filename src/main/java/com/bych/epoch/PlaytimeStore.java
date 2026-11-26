package com.bych.epoch;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

/**
 * Odehraný čas a středověk všech hráčů v datech světa.
 * Počítá se na serveru, když je hráč online (i AFK).
 */
public final class PlaytimeStore extends SavedData {
	private static final String DATA_NAME = "kingdom_of_bych_playtime";

	private final Map<UUID, PlayerEpochData> byPlayer = new HashMap<>();

	public static PlaytimeStore get(MinecraftServer server) {
		ServerLevel overworld = server.overworld();
		DimensionDataStorage storage = overworld.getDataStorage();
		return storage.computeIfAbsent(new Factory<>(PlaytimeStore::new, PlaytimeStore::load, null), DATA_NAME);
	}

	public PlayerEpochData of(UUID playerId) {
		return this.byPlayer.computeIfAbsent(playerId, ignored -> new PlayerEpochData());
	}

	public void resetPlayer(UUID playerId) {
		PlayerEpochData data = this.of(playerId);
		data.setPlayTicks(0L);
		data.setMedieval(false);
		data.setLastAnnouncedEpoch(null);
		this.setDirty();
	}

	public void markChanged() {
		this.setDirty();
	}

	@Override
	public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
		ListTag list = new ListTag();
		for (Map.Entry<UUID, PlayerEpochData> entry : this.byPlayer.entrySet()) {
			CompoundTag row = new CompoundTag();
			row.putUUID("id", entry.getKey());
			row.putLong("playTicks", entry.getValue().playTicks());
			row.putBoolean("medieval", entry.getValue().medieval());
			list.add(row);
		}
		tag.put("players", list);
		return tag;
	}

	private static PlaytimeStore load(CompoundTag tag, HolderLookup.Provider registries) {
		PlaytimeStore store = new PlaytimeStore();
		ListTag list = tag.getList("players", Tag.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) {
			CompoundTag row = list.getCompound(i);
			if (!row.hasUUID("id")) {
				continue;
			}
			PlayerEpochData data = new PlayerEpochData();
			data.setPlayTicks(row.getLong("playTicks"));
			data.setMedieval(row.getBoolean("medieval"));
			store.byPlayer.put(row.getUUID("id"), data);
		}
		return store;
	}
}
