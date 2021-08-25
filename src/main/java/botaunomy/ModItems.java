/*******************************************************************************
 * Copyright (C) 2017 Jeremy Grozavescu <oneandonlyflexo>
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * This file is part of Botaunomy, which is open source:
 * https://github.com/oneandonlyflexo/botaunomy
 ******************************************************************************/
package botaunomy;
import botaunomy.item.RodItem;



public class ModItems {

	public static final RodItem rod_will = new RodItem("rod_will","Right clicks a block");
	public static final RodItem rod_work = new RodItem("rod_work","Combined with a tool, right click whith that tool");

	public static void init() {
		//needed to force load statics members
	}
}
