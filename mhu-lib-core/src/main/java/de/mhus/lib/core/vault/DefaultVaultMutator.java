package de.mhus.lib.core.vault;

import de.mhus.lib.core.crypt.AsyncKey;
import de.mhus.lib.core.crypt.MCrypt;
import de.mhus.lib.core.crypt.pem.PemPriv;
import de.mhus.lib.core.crypt.pem.PemPub;
import de.mhus.lib.core.crypt.pem.PemUtil;
import de.mhus.lib.core.parser.ParseException;
import de.mhus.lib.errors.NotSupportedException;

public class DefaultVaultMutator implements VaultMutator {

	/**
	 * Try to adapt the entry to the given class or interface.
	 * @param entry 
	 * @param ifc
	 * @return The requested interface or class.
	 * @throws NotSupportedException Thrown if the entry can't be adapted to the interface.
	 * @throws ParseException 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T adaptTo(VaultEntry entry, Class<? extends T> ifc) throws ParseException, NotSupportedException {
		if (entry.getType() != null) {
			try {
				if (ifc == AsyncKey.class && MVault.TYPE_RSA_PRIVATE_KEY.equals(entry.getType())) {
					return (T) MCrypt.loadPrivateRsaKey(entry.getValue().value());
				}
				if (ifc == AsyncKey.class && MVault.TYPE_RSA_PUBLIC_KEY.equals(entry.getType())) {
					return (T) MCrypt.loadPrivateRsaKey(entry.getValue().value());
				}
				if (ifc == PemPriv.class && entry.getType().endsWith(MVault.SUFFIX_CIPHER_PRIVATE_KEY)) {
					return (T) PemUtil.cipherPrivFromString(entry.getValue().value());
				}
				if (ifc == PemPub.class && entry.getType().endsWith(MVault.SUFFIX_CIPHER_PUBLIC_KEY)) {
					return (T) PemUtil.cipherPubFromString(entry.getValue().value());
				}
				if (ifc == PemPriv.class && entry.getType().endsWith(MVault.SUFFIX_SIGN_PRIVATE_KEY)) {
					return (T) PemUtil.signPrivFromString(entry.getValue().value());
				}
				if (ifc == PemPub.class && entry.getType().endsWith(MVault.SUFFIX_SIGN_PUBLIC_KEY)) {
					return (T) PemUtil.signPubFromString(entry.getValue().value());
				}
			} catch (Exception e) {
				throw new ParseException(e);
			}
		}
		throw new NotSupportedException(entry,ifc);
	}

}
