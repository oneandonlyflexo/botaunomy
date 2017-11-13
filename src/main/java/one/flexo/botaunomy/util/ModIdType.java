/*******************************************************************************
 * Copyright (C) 2017 Jeremy Grozavescu <oneandonlyflexo>
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * This file is part of Botaunomy, which is open source:
 * https://github.com/oneandonlyflexo/botaunomy
 ******************************************************************************/
package one.flexo.botaunomy.util;

import one.flexo.botaunomy.ModInfo;

/**
 * A utility enum for getting the different types of ids that need the modid as a prefix.
 *
 * @author "oneandonlyflexo"
 */
public enum ModIdType {

	DEFAULT(":"),
	UNLOCALIZED_NAME("."),
	DIMENSION("_"),

	;

	ModIdType(String delimiter) {
		this.delimiter = delimiter;
	}

	private String delimiter;

	public String getId(String name) {
		return ModInfo.modid + getSuffix(name);
	}

	public String getSuffix(String name) {
		return this.delimiter + name;
	}
}
