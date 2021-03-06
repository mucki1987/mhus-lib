/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.lib.core.vault;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MArgs;
import de.mhus.lib.core.console.ConsoleTable;
import de.mhus.lib.core.parser.ParseException;
import de.mhus.lib.errors.NotSupportedException;

public class MVaultUtil {
	
	public static MVault loadDefault() {
		MVault vault = MApi.lookup(MVault.class);
		checkDefault(vault);
		return vault;
	}
	
	public static void checkDefault(MVault vault) {
		VaultSource def = vault.getSource(MVault.SOURCE_DEFAULT);
		if (def == null) {
			
			VaultPassphrase vaultPassphrase = MApi.lookup(VaultPassphrase.class);
			VaultSourceFactory factory = MApi.lookup(VaultSourceFactory.class);
			
			def = factory.create(MVault.SOURCE_DEFAULT, vaultPassphrase);
			if (def != null)
				vault.registerSource(def);
			
		}
	}

	public static void main(String[] in) throws IOException {
		MArgs args = new MArgs(in);
		
		MVault vault = loadDefault();
		
		VaultSource source = null;
		if (args.contains("file")) {
			String vp = args.getValue("passphrase", "setit", 0);
			File f = new File(args.getValue("file", 0));
			source = new FileVaultSource(f, vp);
			vault.registerSource(source);
		}
		if (source == null) source = vault.getSource(MVault.SOURCE_DEFAULT);
		
		String cmd = args.getValue(MArgs.DEFAULT, "help", 0);
		
		switch (cmd) {
		case "help": {
			System.out.println("Usage: <cmd> <args>");
			System.out.println("list - list all keys");
		} break;
		case "list": {
			ConsoleTable out = new ConsoleTable();
			out.setHeaderValues("Source","Id","Type","Description");
			for (String sourceName : vault.getSourceNames()) {
				source = vault.getSource(sourceName);
				for (UUID id : source.getEntryIds()) {
					VaultEntry entry = source.getEntry(id);
					out.addRowValues(sourceName,id,entry.getType(),entry.getDescription());
				}
			}
			out.print(System.out);
		} break;
		}
		
	}

	/**
	 * Try to adapt the entry to the given class or interface.
	 * @param entry 
	 * @param ifc
	 * @return The requested interface or class.
	 * @throws NotSupportedException Thrown if the entry can't be adapted to the interface.
	 * @throws ParseException 
	 */
	public static <T> T adaptTo(VaultEntry entry, Class<? extends T> ifc) throws ParseException, NotSupportedException {
		// delegate to service
		return MApi.lookup(VaultMutator.class).adaptTo(entry, ifc);
	}

	/**
	 * Try to adapt the source to the given class or interface.
	 * @param source 
	 * @param ifc
	 * @return The requested interface or class.
	 * @throws NotSupportedException Thrown if the source can't be adapted to the interface.
	 */
//	@SuppressWarnings("unchecked")
//	public static <T> T adaptTo(VaultSource source, Class<? extends T> ifc) throws NotSupportedException {
//		if (ifc.isInstance(source)) return (T) source;
//		throw new NotSupportedException(source,ifc);
//	}


}
