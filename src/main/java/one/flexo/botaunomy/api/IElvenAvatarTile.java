package one.flexo.botaunomy.api;

import java.lang.ref.WeakReference;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import vazkii.botania.api.item.IAvatarTile;

public interface IElvenAvatarTile extends IAvatarTile {

	TileEntity asTileEntity();

	World getAvatarWorld();
	WeakReference<FakePlayer> getAvatarPlayer();

}
