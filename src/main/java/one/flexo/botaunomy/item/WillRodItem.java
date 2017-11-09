package one.flexo.botaunomy.item;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import one.flexo.botaunomy.ModResources;
import one.flexo.botaunomy.api.IElvenAvatarTile;
import one.flexo.botaunomy.api.IElvenAvatarWieldable;
import one.flexo.botaunomy.item.base.ItemBase;
import one.flexo.botaunomy.util.FlexoUtil;
import vazkii.botania.api.item.IAvatarTile;

public class WillRodItem extends ItemBase implements IElvenAvatarWieldable {

	static final ResourceLocation avatarOverlay = new ResourceLocation(ModResources.MODEL_AVATAR_WILL_WORK);

	protected boolean useRightClick;

	public WillRodItem(String name, boolean useRightClick) {
		super(name);
		this.useRightClick = useRightClick;
	}

	@Override
	public ResourceLocation getOverlayResource(IAvatarTile tileAvatar, ItemStack stack) {
		return WillRodItem.avatarOverlay;
	}

	@Override
	public void onAvatarUpdate(IAvatarTile tileAvatar, ItemStack stack) {
		//Do nothing for livingwood avatars.
	}

	@Override
	public void onElvenAvatarUpdate(IElvenAvatarTile tileElvenAvatar, ItemStack stack) {
		if (!tileElvenAvatar.isEnabled()) {
			return;
		}
		TileEntity tile = tileElvenAvatar.asTileEntity();
		WeakReference<FakePlayer> avatarPlayer = tileElvenAvatar.getAvatarPlayer();
		if (avatarPlayer == null) {
			return;
		}

		avatarPlayer.get().rotationYaw = FlexoUtil.getYaw(tileElvenAvatar.getAvatarFacing());

		//TODO: have fake player try to equip the linked item in the wand.

		boolean rightClick = useRightClick();
		try {
			BlockPos targetPos = tile.getPos().offset(tileElvenAvatar.getAvatarFacing());
			interactBlock(tileElvenAvatar, avatarPlayer, targetPos, true);
			interactEntities(tileElvenAvatar, avatarPlayer, targetPos, true);
		}
		catch (Exception e) {
			//TODO: Print error
		}

	}

	protected boolean useRightClick() {
		return true;
	}

	private boolean interactBlock(
			IElvenAvatarTile tileElvenAvatar,
			WeakReference<FakePlayer> fakePlayer,
			BlockPos targetPos,
			boolean rightClick)
	{
		if(interactFluid(tileElvenAvatar, fakePlayer, targetPos, true)) {
			return true;
		}
		if(Block.getBlockFromItem(fakePlayer.get().getHeldItemMainhand().getItem()) != Blocks.AIR) {
			//This ain't no block placer!
			return false;
		}
		World world = tileElvenAvatar.getAvatarWorld();
		EnumActionResult r;
		r = fakePlayer.get().interactionManager.processRightClickBlock(fakePlayer.get(), world, fakePlayer.get().getHeldItemMainhand(), EnumHand.MAIN_HAND, targetPos, EnumFacing.UP, .5F, .5F, .5F);
		if (r == EnumActionResult.SUCCESS) {
			return true; //Yay!
		}
		r = fakePlayer.get().interactionManager.processRightClick(fakePlayer.get(), world, fakePlayer.get().getHeldItemMainhand(), EnumHand.MAIN_HAND);
		if (r == EnumActionResult.SUCCESS) {
			return true; //Yay!
		}
		ActionResult<ItemStack> res = fakePlayer.get().getHeldItemMainhand().getItem().onItemRightClick(world, fakePlayer.get(), EnumHand.MAIN_HAND);
		if (res != null || res.getType() == EnumActionResult.SUCCESS) {
			return true; //Yay!
		}
		return false;
	}

	private boolean interactFluid(
			IElvenAvatarTile tileElvenAvatar,
			WeakReference<FakePlayer> avatarPlayer,
			BlockPos targetPos,
			boolean rightClick)
	{
		//TODO: deal with dat fluid
		return false;
	}

	private void interactEntities(
			IElvenAvatarTile tileElvenAvatar,
			WeakReference<FakePlayer> avatarPlayer,
			BlockPos targetPos,
			boolean rightClick)
	{
		AxisAlignedBB entityRange = new AxisAlignedBB(targetPos).offset(0.5, 0, 0.5);

		World world = tileElvenAvatar.getAvatarWorld();
		List<EntityLivingBase> living = world.getEntitiesWithinAABB(EntityLivingBase.class, entityRange);
		List<EntityMinecart> carts = world.getEntitiesWithinAABB(EntityMinecart.class, entityRange);
		List<Entity> all = new ArrayList<Entity>(living);
		all.addAll(carts);//works since  they share a base class but no overlap

		if (rightClick) {
			world.markChunkDirty(targetPos, tileElvenAvatar.asTileEntity());
			if(world.isRemote == false) {
				for(Entity entity : all) {//both living and minecarts
					if (entity != null && entity.isDead == false) {
						//TODO: add a check in for tool/itemstack
						if (EnumActionResult.FAIL != avatarPlayer.get().interactOn(entity, EnumHand.MAIN_HAND)) {
							dropInventory(world, avatarPlayer, targetPos);
							break;
						}
					}
				}
			}
		}
		else {
			ItemStack mainHand = avatarPlayer.get().getHeldItemMainhand();
			avatarPlayer.get().onGround = true;
			for (EntityLivingBase entity : living) {
				avatarPlayer.get().attackTargetEntityWithCurrentItem(entity);
				float damage = FlexoUtil.getAttackDamage(mainHand, entity);
				entity.attackEntityFrom(DamageSource.causePlayerDamage(avatarPlayer.get()), damage);
			}
		}
	}

	private void dropInventory(World world, WeakReference<FakePlayer> avatarPlayer, BlockPos pos) {
		ItemStack mainHand = avatarPlayer.get().getHeldItemMainhand();
		for (ItemStack s : avatarPlayer.get().inventory.mainInventory) {
			if (!s.isEmpty() && !s.equals(mainHand)) {
				EntityItem entityItem = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, s.copy());
				if (world.isRemote == false) {
					world.spawnEntity(entityItem);
				}
				entityItem.setVelocity(0, 0, 0);
				s.setCount(0);
			}
		}
	}

}
