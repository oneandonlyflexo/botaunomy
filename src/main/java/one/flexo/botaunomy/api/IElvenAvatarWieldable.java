/*******************************************************************************
 * Copyright (C) 2017 Jeremy Grozavescu <oneandonlyflexo>
 * https://creativecommons.org/licenses/by-nc-nd/4.0/
 *
 * This file is part of Botaunomy, which is open source:
 * https://github.com/oneandonlyflexo/botaunomy
 ******************************************************************************/
package one.flexo.botaunomy.api;

import net.minecraft.item.ItemStack;
import vazkii.botania.api.item.IAvatarTile;
import vazkii.botania.api.item.IAvatarWieldable;

/**
 * This is for class of rods/staffs that are supposed to only be wielded by this mod's elven avatar.  For ease of
 * integration with Botania, this interface inherits from IAvatarWieldable, but any item that is intended to be
 * IElvenAvatarWieldable should only implement IAvatarWieldable#onAvatarUpdate if it's supposed to do something with
 * the original livingwood avatar that's different than for the elven avatar.
 *
 * @author "oneandonlyflexo"
 */
public interface IElvenAvatarWieldable extends IAvatarWieldable {

	/**
	 * This method comes from extending IAvatarWieldable.  Only implement this if this weildable also does something
	 * for the base livingwood avatar.
	 */
	@Override
	void onAvatarUpdate(IAvatarTile tileAvatar, ItemStack stack);

	/**
	 * The bread and butter of this wieldable!  Do awesome and amazing things with this method. Please ;)
	 */
	void onElvenAvatarUpdate(IElvenAvatarTile tileElvenAvatar, ItemStack stack);

}
