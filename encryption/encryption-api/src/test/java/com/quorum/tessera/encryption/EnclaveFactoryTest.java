package com.quorum.tessera.encryption;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import java.util.Collections;
import static org.mockito.Mockito.mock;

public class EnclaveFactoryTest {

    private EnclaveFactory enclaveFactory;

    @Before
    public void onSetUp() {
        enclaveFactory = EnclaveFactory.create();
    }

    @Test
    public void create() {
        assertThat(enclaveFactory).isNotNull();
    }

    @Test(expected = NullPointerException.class)
    public void createEnclaveWithNullArgs() {
        enclaveFactory.create(null, null);
    }

    @Test(expected = java.util.NoSuchElementException.class)
    public void createEnclaveWithEmptyArgs() {
        enclaveFactory.create(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
    }

    @Test
    public void createEnclave() {
        KeyPair keyPair = mock(KeyPair.class);
        PublicKey publicKey = mock(PublicKey.class);
        Enclave enclave = enclaveFactory.create(Collections.singletonList(keyPair), Collections.singletonList(publicKey));
        assertThat(enclave).isNotNull();
        assertThat(enclave.getForwardingKeys()).containsExactly(publicKey);
    }

}
