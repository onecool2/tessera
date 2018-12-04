package com.quorum.tessera.enclave.rest;

import com.quorum.tessera.encryption.Enclave;
import com.quorum.tessera.encryption.PublicKey;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.json.JsonArray;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import static org.assertj.core.api.Assertions.assertThat;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jsonp.JsonProcessingFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EnclaveApplicationIT extends JerseyTest {

    private Enclave enclave;

    @Override
    public void setUp() throws Exception {
        enclave = mock(Enclave.class);
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        Mockito.verifyNoMoreInteractions(enclave);

    }

    @Override
    protected Application configure() {

        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        
        ResourceConfig config = ResourceConfig.forApplicationClass(EnclaveApplication.class);
        config.property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_SERVER, "FINE");

        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(enclave).to(Enclave.class);
            }
        });
        config.register(JsonProcessingFeature.class);
        return config;
    }

    @Test
    public void ping() throws Exception {

        Response response = target("ping").request().get();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(String.class)).isNotEmpty();

    }

    @Test
    public void defaultKey() throws Exception {

        PublicKey publicKey = PublicKey.from("defaultKey".getBytes());

        when(enclave.defaultPublicKey()).thenReturn(publicKey);

        Response response = target("default").request().get();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(String.class)).isEqualTo("defaultKey");
        verify(enclave).defaultPublicKey();

    }

    @Test
    public void publicKeys() throws Exception {

        Set<PublicKey> keys = Stream.of("publicKey","publicKey2")
                .map(String::getBytes)
                .map(PublicKey::from)
                .collect(Collectors.toSet());
        
        when(enclave.getPublicKeys())
                .thenReturn(keys);

        Response response = target("public").request().get();
        assertThat(response.getStatus()).isEqualTo(200);

        JsonArray result = response.readEntity(JsonArray.class);

        assertThat(result).hasSize(2);

        verify(enclave).getPublicKeys();
    }
}
