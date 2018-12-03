package com.quorum.tessera.enclave.rest;

import com.quorum.tessera.encryption.Enclave;
import com.quorum.tessera.encryption.EnclaveImpl;
import com.quorum.tessera.encryption.KeyManager;
import com.quorum.tessera.encryption.KeyManagerImpl;
import com.quorum.tessera.nacl.NaclFacade;
import com.quorum.tessera.nacl.NaclFacadeFactory;
import java.util.Collections;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import static org.assertj.core.api.Assertions.assertThat;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import static org.junit.Assert.assertTrue;

public class EnclaveApplicationIT extends JerseyTest {

    @Override
    protected Application configure() {

        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        ResourceConfig config = ResourceConfig.forApplicationClass(EnclaveApplication.class);
        
        NaclFacade nacl = NaclFacadeFactory.newFactory().create();
        
        
        
        KeyManager keyManager = new KeyManagerImpl(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        
        Enclave instance = new EnclaveImpl(nacl, keyManager);
        
        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(instance).to(Enclave.class);
            }
        });

        return config;
    }

    @Test
    public void doStuff() throws Exception {

        Response response = target("ping").request().get();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(String.class)).isNotEmpty();

        assertTrue(true);

    }
}
