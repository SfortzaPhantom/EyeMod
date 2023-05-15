package com.podloot.eyemod.network;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

public class ServerWeather {

	int action;
	int ticks;

	public ServerWeather(int action, int ticks) {
		this.action = action;
		this.ticks = ticks;
	}

	public ServerWeather(FriendlyByteBuf buf) {
		this.action = buf.readInt();
		this.ticks = buf.readInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(action);
		buf.writeInt(ticks);
	}

	public boolean handle(Supplier<NetworkEvent.Context> ctx) {
		final var succes = new AtomicBoolean(false);
		ctx.get().enqueueWork(() -> {
			switch(action) {
			case 0:
				ctx.get().getSender().getLevel().setDayTime(ticks);
				break;
			case 1:
				ctx.get().getSender().getLevel().setWeatherParameters(ticks, 0, false, false);
				break;
			case 2:
				ctx.get().getSender().getLevel().setWeatherParameters(0, ticks, true, false);
				break;
			case 3:
				ctx.get().getSender().getLevel().setWeatherParameters(0, ticks, true, true);
				break;
			}
		});
		ctx.get().setPacketHandled(true);
		return succes.get();
	}

}