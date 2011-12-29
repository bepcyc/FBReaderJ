/*
 * Copyright (C) 2007-2011 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.fbreader.formats;

import java.util.*;

import org.geometerplus.zlibrary.core.options.*;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;

import org.geometerplus.fbreader.formats.fb2.FB2Plugin;
import org.geometerplus.fbreader.formats.oeb.OEBPlugin;
import org.geometerplus.fbreader.formats.oeb.OPFPlugin;
import org.geometerplus.fbreader.formats.oeb.EpubPlugin;
import org.geometerplus.fbreader.formats.pdb.MobipocketPlugin;

public class PluginCollection {
	private static PluginCollection ourInstance;

	public ZLStringOption DefaultLanguageOption;
	public ZLStringOption DefaultEncodingOption;
	public ZLBooleanOption LanguageAutoDetectOption;

	private final HashMap<String, FormatPlugin> myNativePlugins = new HashMap<String, FormatPlugin>();
	private final HashMap<String, FormatPlugin> myExternalPlugins = new HashMap<String, FormatPlugin>();

	public static PluginCollection Instance() {
		if (ourInstance == null) {

			ourInstance = new PluginCollection();
			ourInstance.myNativePlugins.put("fb2", new FB2Plugin());
			ourInstance.myNativePlugins.put("mobi", new MobipocketPlugin());
			ourInstance.myNativePlugins.put("oeb", new OEBPlugin());
			ourInstance.myNativePlugins.put("epub", new EpubPlugin());
			ourInstance.myNativePlugins.put("opf", new OPFPlugin());

		}
		return ourInstance;
	}
	
//	public static void deleteInstance() {
//		if (ourInstance != null) {
//			ourInstance = null;
//		}
//	}

	private PluginCollection() {
		LanguageAutoDetectOption = new ZLBooleanOption("Format", "AutoDetect", true);
		DefaultLanguageOption = new ZLStringOption("Format", "DefaultLanguage", "en"); 
		DefaultEncodingOption = new ZLStringOption("Format", "DefaultEncoding", "windows-1252");
	}

	private FormatPlugin getOrCreateCustomPlugin(String extension) {
		if (!myExternalPlugins.containsKey(extension)) {
			myExternalPlugins.put(extension, new CustomPlugin(extension));
		}
		return myExternalPlugins.get(extension);
	}

	public FormatPlugin getPlugin(ZLFile file) {
		String extension = file.getExtension();
		switch (Formats.getStatus(extension)) {
			case Formats.NATIVE:
				return myNativePlugins.get(extension);
			case Formats.EXTERNAL:
				return getOrCreateCustomPlugin(extension);
			default:
				return null;
		}
	}

	public boolean acceptsBookPath(String path) {
		String extension = path.substring(path.lastIndexOf('.') + 1);
		return Formats.getStatus(extension) != Formats.UNDEFINED;
	}

}
