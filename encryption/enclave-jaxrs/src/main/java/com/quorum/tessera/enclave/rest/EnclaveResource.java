package com.quorum.tessera.enclave.rest;

import com.quorum.tessera.encryption.Enclave;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class EnclaveResource {

    private final Enclave enclave;

    @Inject
    public EnclaveResource(Enclave enclave) {
        this.enclave = Objects.requireNonNull(enclave);
    }

    @GET
    @Path("ping")
    public String ping() {
        return LocalDateTime.now().toString();
    }

//    @GET
//    @Path("/default")
//    public String defaultPublicKey() {
//        return enclave.defaultPublicKey().encodeToBase64();
//    }
//
//    @GET
//    @Path("/forwarding")
//    public List<String> getForwardingKeys() {
//        return enclave.getForwardingKeys().stream()
//                .map(PublicKey::encodeToBase64)
//                .collect(Collectors.toList());
//    }
//
//    @GET
//    @Path("/public")
//    public Set<PublicKey> getPublicKeys() {
//        return enclave.getPublicKeys();
//    }
//
//    public EncodedPayloadWithRecipients encryptPayload(byte[] message, PublicKey senderPublicKey, List<PublicKey> recipientPublicKeys) {
//        return enclave.encryptPayload(message, senderPublicKey, recipientPublicKeys);
//    }
//
//    public EncodedPayloadWithRecipients encryptPayload(RawTransaction rawTransaction, List<PublicKey> recipientPublicKeys) {
//        return enclave.encryptPayload(rawTransaction, recipientPublicKeys);
//    }
//
//    public RawTransaction encryptRawPayload(byte[] message, PublicKey sender) {
//        return enclave.encryptRawPayload(message, sender);
//    }
//
//    public byte[] unencryptTransaction(EncodedPayloadWithRecipients payloadWithRecipients, PublicKey providedKey) {
//        return enclave.unencryptTransaction(payloadWithRecipients, providedKey);
//    }

}
