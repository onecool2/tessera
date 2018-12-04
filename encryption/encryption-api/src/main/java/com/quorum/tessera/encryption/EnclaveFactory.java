
package com.quorum.tessera.encryption;

import com.quorum.tessera.nacl.NaclFacadeFactory;
import java.util.Collection;


public interface EnclaveFactory {
    
    default Enclave create(Collection<KeyPair> keys, Collection<PublicKey> forwardKeys) {
        return new EnclaveImpl(NaclFacadeFactory.newFactory().create(), new KeyManagerImpl(keys, forwardKeys));
    }
    
    static EnclaveFactory create() {
        return new EnclaveFactory() {};
    }
    
}
