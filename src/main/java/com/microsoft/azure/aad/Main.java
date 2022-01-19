package com.microsoft.azure.aad;

import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.util.logging.ClientLogger;
import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.avs.AvsManager;

public class Main {

    private static final ClientLogger logger = new ClientLogger(Main.class);

    public static void main(String args[]) {

        AzureProfile profile = new AzureProfile(null, "ec0aa5f7-9e78-40c9-85cd-535c6305b380", AzureEnvironment.AZURE);

//        AzureResourceManager azure = AzureResourceManager
//                .authenticate(new ManagedIdentityCredentialBuilder().build(), profile)
//                .withDefaultSubscription();

//        long count = azure.accessManagement().servicePrincipals().list().stream().count();
//        logger.info("Number of service principals: {}", count);

        AvsManager manager = AvsManager
                .authenticate(new ManagedIdentityCredentialBuilder().build(), profile);

        long count = manager.privateClouds().list().stream().count();
        logger.info("Number of privateClouds: {}", count);
    }
}
