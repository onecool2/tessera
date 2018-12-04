
package com.quorum.tessera.enclave.rest;

import com.quorum.tessera.encryption.Enclave;
import com.quorum.tessera.encryption.EncodedPayloadWithRecipients;
import com.quorum.tessera.encryption.PublicKey;
import com.quorum.tessera.encryption.RawTransaction;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


public class EnclaveClient implements Enclave {
    
    private final Client client;
    
    private final URI uri;

    public EnclaveClient(URI uri) {
        this.uri = Objects.requireNonNull(uri);
        this.client = ClientBuilder.newClient();
    }

    @Override
    public PublicKey defaultPublicKey() {
        
      Response response =   client.target(uri)
                .path("default")
                .request(MediaType.TEXT_PLAIN_TYPE)
                .get();
        
      byte[] data = response.readEntity(byte[].class);
      
      return PublicKey.from(data);
    }

    @Override
    public Set<PublicKey> getForwardingKeys() {
        
        Response response = client.target(uri)
                .path("forwarding")
                .request()
                .get();
        
        
        
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<PublicKey> getPublicKeys() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EncodedPayloadWithRecipients encryptPayload(byte[] message, PublicKey senderPublicKey, List<PublicKey> recipientPublicKeys) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EncodedPayloadWithRecipients encryptPayload(RawTransaction rawTransaction, List<PublicKey> recipientPublicKeys) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RawTransaction encryptRawPayload(byte[] message, PublicKey sender) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte[] unencryptTransaction(EncodedPayloadWithRecipients payloadWithRecipients, PublicKey providedKey) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
