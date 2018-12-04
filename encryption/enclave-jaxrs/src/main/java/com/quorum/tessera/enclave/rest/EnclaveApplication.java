package com.quorum.tessera.enclave.rest;

import java.util.Collections;
import java.util.Set;
import javax.ws.rs.core.Application;

public class EnclaveApplication extends Application implements com.quorum.tessera.config.apps.EnclaveApp {

    @Override
    public Set<Class<?>> getClasses() {
        return Collections.singleton(EnclaveResource.class);
    }

}
