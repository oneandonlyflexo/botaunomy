package botaunomy.registry;


/*******************************************************************************
 * Copyright (C) 2017 Jeremy Grozavescu <oneandonlyflexo>
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 * 
 * This file is part of Nibbler, which is open source:
 * https://github.com/oneandonlyflexo/nibbler
 ******************************************************************************/

import net.minecraft.tileentity.TileEntity;

public interface TileEntityRegisteredBlocked {

	Class<? extends TileEntity> getTileEntityClass();
}

