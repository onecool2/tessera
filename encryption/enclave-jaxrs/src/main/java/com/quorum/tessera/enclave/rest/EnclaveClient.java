package com.quorum.tessera.enclave.rest;

import com.quorum.tessera.encryption.Enclave;
import com.quorum.tessera.encryption.EncodedPayloadWithRecipients;
import com.quorum.tessera.encryption.PayloadEncoder;
import com.quorum.tessera.encryption.PublicKey;
import com.quorum.tessera.encryption.RawTransaction;
import com.quorum.tessera.nacl.Nonce;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
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

        Response response = client.target(uri)
                .path("default")
                .request()
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

        EnclavePayload enclavePayload = new EnclavePayload();
        enclavePayload.setData(message);
        enclavePayload.setSenderKey(senderPublicKey.getKeyBytes());
        enclavePayload.setRecipientPublicKeys(recipientPublicKeys.stream()
                .map(PublicKey::getKeyBytes)
                .collect(Collectors.toList()));

        Response response = client.target(uri)
                .path("encrypt")
                .request()
                .post(Entity.json(enclavePayload));

        byte[] result = response.readEntity(byte[].class);

        return PayloadEncoder.create().decodePayloadWithRecipients(result);
    }

    @Override
    public EncodedPayloadWithRecipients encryptPayload(RawTransaction rawTransaction, List<PublicKey> recipientPublicKeys) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RawTransaction encryptRawPayload(byte[] message, PublicKey sender) {

        EnclavePayload enclavePayload = new EnclavePayload();
        enclavePayload.setData(message);
        enclavePayload.setSenderKey(sender.getKeyBytes());

        Response response = client.target(uri)
                .path("encrypt")
                .path("raw")
                .request()
                .post(Entity.json(enclavePayload));

        EnclaveRawPayload enclaveRawPayload = response.readEntity(EnclaveRawPayload.class);

        byte[] encryptedPayload = enclaveRawPayload.getEncryptedPayload();
        byte[] encryptedKey = enclaveRawPayload.getEncryptedKey();
        Nonce nonce = new Nonce(enclaveRawPayload.getNonce());
        PublicKey senderKey = PublicKey.from(enclaveRawPayload.getFrom());
        return new RawTransaction(encryptedPayload, encryptedKey, nonce, senderKey);
    }

    @Override
    public byte[] unencryptTransaction(EncodedPayloadWithRecipients payloadWithRecipients, PublicKey providedKey) {

        EnclaveUnencryptPayload dto = new EnclaveUnencryptPayload();

        byte[] body = PayloadEncoder.create().encode(payloadWithRecipients);

        dto.setData(body);
        dto.setProvidedKey(providedKey.getKeyBytes());

        Response response = client.target(uri)
                .path("unencrypt")
                .request()
                .post(Entity.json(dto));
        
       
        return response.readEntity(byte[].class);
    }

}
