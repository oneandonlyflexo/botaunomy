/*******************************************************************************
 * Copyright (C) 2017 Jeremy Grozavescu <oneandonlyflexo>
 * https://creativecommons.org/licenses/by-nc-nd/4.0/
 *
 * This file is part of Botaunomy, which is open source:
 * https://github.com/oneandonlyflexo/botaunomy
 ******************************************************************************/
package one.flexo.botaunomy.block.tile;

import java.lang.ref.WeakReference;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.mojang.authlib.GameProfile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.IItemHandler;
import one.flexo.botaunomy.Botaunomy;
import one.flexo.botaunomy.api.IElvenAvatarTile;
import one.flexo.botaunomy.api.IElvenAvatarWieldable;
import vazkii.botania.api.item.IAvatarWieldable;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.common.block.tile.TileSimpleInventory;

/**
 * The key bit to this class is the increase in max mana and the ability to use IElvenAvatarWieldable items.  It also
 * Offers some methods to the wieldables as the elven weildables are more powerful and can do more
 *
 * @author "oneandonlyflexo"
 */
public class TileElvenAvatar extends TileSimpleInventory implements IElvenAvatarTile {

	private static final int MAX_MANA = 25600;

	protected static final String TAG_ENABLED = "enabled";
	protected static final String TAG_TICKS_ELAPSED = "ticksElapsed";
	protected static final String TAG_MANA = "ticksElapsed";

	protected boolean enabled;
	protected int ticksElapsed;
	protected int mana;

	public final UUID uuid;
	protected WeakReference<FakePlayer> avatarPlayer;


	public TileElvenAvatar() {
		uuid = UUID.randomUUID();
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
		if(world instanceof WorldServer) {
			if (avatarPlayer == null) {
				avatarPlayer = initFakePlayer((WorldServer) world, uuid);
				if (avatarPlayer == null) {
					//TODO: Log error
					//TODO: create flag to stop trying to make fake player
					return null;
				}
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

	@Override
	public void writePacketNBT(NBTTagCompound par1nbtTagCompound) {
		super.writePacketNBT(par1nbtTagCompound);
		par1nbtTagCompound.setBoolean(TAG_ENABLED, enabled);
		par1nbtTagCompound.setInteger(TAG_TICKS_ELAPSED, ticksElapsed);
		par1nbtTagCompound.setInteger(TAG_MANA, mana);
	}

	@Override
	public void readPacketNBT(NBTTagCompound par1nbtTagCompound) {
		super.readPacketNBT(par1nbtTagCompound);
		enabled = par1nbtTagCompound.getBoolean(TAG_ENABLED);
		ticksElapsed = par1nbtTagCompound.getInteger(TAG_TICKS_ELAPSED);
		mana = par1nbtTagCompound.getInteger(TAG_MANA);
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	protected SimpleItemStackHandler createItemHandler() {
		return new SimpleItemStackHandler(this, false) {
			@Override
			protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
				return 1;
			}
		};
	}

	@Override
	public boolean isFull() {
		return mana >= MAX_MANA;
	}

	@Override
	public void recieveMana(int mana) {
		this.mana = Math.max(0, Math.min(MAX_MANA, this.mana + mana));
	}

	@Override
	public boolean canRecieveManaFromBursts() {
		return !itemHandler.getStackInSlot(0).isEmpty();
	}

	@Override
	public int getCurrentMana() {
		return mana;
	}

	@Override
	public IItemHandler getInventory() {
		return getItemHandler();
	}

	@Override
	public EnumFacing getAvatarFacing() {
		return world.getBlockState(getPos()).getValue(BotaniaStateProps.CARDINALS);
	}

	@Override
	public int getElapsedFunctionalTicks() {
		return ticksElapsed;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}
