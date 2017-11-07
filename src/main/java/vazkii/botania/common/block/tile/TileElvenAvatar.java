/*******************************************************************************
 * Copyright (C) 2017 Jeremy Grozavescu <oneandonlyflexo>
 * https://creativecommons.org/licenses/by-nc-nd/4.0/
 *
 * This file is part of Botaunomy, which is open source:
 * https://github.com/oneandonlyflexo/botaunomy
 ******************************************************************************/
package vazkii.botania.common.block.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
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
public class TileElvenAvatar extends TileAvatar {

	private static final int MAX_MANA = 25600;

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

		if(enabled)
			ticksElapsed++;
	}
}
