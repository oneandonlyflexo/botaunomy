/*******************************************************************************
 * Copyright (C) 2017 Jeremy Grozavescu <oneandonlyflexo>
 * https://creativecommons.org/licenses/by-nc-nd/4.0/
 *
 * This file is part of Botaunomy, which is open source:
 * https://github.com/oneandonlyflexo/botaunomy
 ******************************************************************************/
package vazkii.botania.common.block.tile;

import java.lang.ref.WeakReference;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.item.ItemStack;
import net.minecraft.network.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;
import one.flexo.botaunomy.Botaunomy;
import one.flexo.botaunomy.api.IElvenAvatarTile;
import one.flexo.botaunomy.api.IElvenAvatarWieldable;
import vazkii.botania.api.item.IAvatarWieldable;

/**
 * @author "oneandonlyflexo"
 *
 * This class is in a botania package to access package level member variables in TileAvatar.  I'm sure there's
 * AT or Reflection ways around that, but this was a super simple method around that.
 *
 * The key bit to this class is the increase in max mana and the ability to use IElvenAvatarWieldable items.
 */
public class TileElvenAvatar extends TileAvatar implements IElvenAvatarTile {

	private static final int MAX_MANA = 25600;

	public final UUID uuid;
	protected WeakReference<FakePlayer> avatarPlayer;

	public TileElvenAvatar() {
		uuid = UUID.randomUUID();
	}

	@Override
	public boolean isFull() {
		return mana >= MAX_MANA;
	}

	@Override
	public void recieveMana(int mana) {
		this.mana = Math.min(MAX_MANA, this.mana + mana);
	}

	@Override
	public void update() {
		enabled = true;
		for(EnumFacing dir : EnumFacing.VALUES) {
			int redstoneSide = world.getRedstonePower(pos.offset(dir), dir);
			if(redstoneSide > 0) {
				enabled = false;
				break;
			}
		}

		ItemStack stack = itemHandler.getStackInSlot(0);
		if(!stack.isEmpty()) {
			if(stack.getItem() instanceof IElvenAvatarWieldable) {
				IElvenAvatarWieldable wieldable = (IElvenAvatarWieldable) stack.getItem();
				wieldable.onElvenAvatarUpdate(this, stack);
			}
			if(stack.getItem() instanceof IAvatarWieldable) {
				IAvatarWieldable wieldable = (IAvatarWieldable) stack.getItem();
				wieldable.onAvatarUpdate(this, stack);
			}
		}

		if(enabled) {
			ticksElapsed++;
		}
	}

	@Override
	public TileEntity asTileEntity() {
		return this;
	}

	@Override
	public World getAvatarWorld() {
		return world;
	}

	@Override
	public WeakReference<FakePlayer> getAvatarPlayer() {
		if (avatarPlayer == null) {
			avatarPlayer = initFakePlayer((WorldServer) world, uuid);
			if (avatarPlayer == null) {
				//TODO: Log error
				//TODO: create flag to stop trying to make fake player
				return null;
			}
		}
		return avatarPlayer;
	}

	private static WeakReference<FakePlayer> initFakePlayer(WorldServer ws, UUID uname) {
		GameProfile profile = new GameProfile(uname, uname.toString());
		WeakReference<FakePlayer> fakePlayer;
		try {
			fakePlayer = new WeakReference<FakePlayer>(FakePlayerFactory.get(ws, profile));
			if (fakePlayer == null || fakePlayer.get() == null) {
				return null;
			}
			fakePlayer.get().onGround = true;
			fakePlayer.get().connection = new NetHandlerPlayServer(FMLCommonHandler.instance().getMinecraftServerInstance(), new NetworkManager(EnumPacketDirection.SERVERBOUND), fakePlayer.get()) {
				@SuppressWarnings("rawtypes")
				@Override
				public void sendPacket(Packet packetIn) {}
			};
			fakePlayer.get().setSilent(true);
			return fakePlayer;
		}
		catch (Exception e) {
			Botaunomy.logger.error("Exception thrown trying to create fake player : " + e.getMessage());
			return null;
		}
	}
}
