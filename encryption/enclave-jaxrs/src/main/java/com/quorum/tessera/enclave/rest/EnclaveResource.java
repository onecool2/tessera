package com.quorum.tessera.enclave.rest;

import com.quorum.tessera.encryption.Enclave;
import com.quorum.tessera.encryption.EncodedPayloadWithRecipients;
import com.quorum.tessera.encryption.PublicKey;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.json.Json;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

    @GET
    @Path("default")
    public Response defaultPublicKey() {
        return Response.ok(enclave.defaultPublicKey().getKeyBytes()).build();
    }

    @GET
    @Path("forwarding")
    public List<String> getForwardingKeys() {
        return enclave.getForwardingKeys().stream()
                .map(PublicKey::encodeToBase64)
                .collect(Collectors.toList());
    }

    @GET
    @Produces("application/json")
    @Path("public")
    public Response getPublicKeys() {

        List<String> body = enclave.getPublicKeys()
                .stream()
                .map(PublicKey::encodeToBase64)
                .collect(Collectors.toList());

        return Response.ok(Json.createArrayBuilder(body).build().toString(), MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response encryptPayload(EnclavePayload payload) {

        PublicKey senderKey = PublicKey.from(payload.getSenderKey());
        List<PublicKey> recipientPublicKeys = payload.getRecipientPublicKeys().stream().map(PublicKey::from).collect(Collectors.toList());
       
        
        EncodedPayloadWithRecipients outcome = enclave.encryptPayload(payload.getData(), senderKey, recipientPublicKeys);
        
        
        
       // return enclave.encryptPayload(message, senderPublicKey, recipientPublicKeys);
        
       
       
        return Response.ok().build();
    }
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
