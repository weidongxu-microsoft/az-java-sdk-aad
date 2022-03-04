package com.microsoft.azure.aad;

import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.util.logging.ClientLogger;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;

public class Main {

    private static final ClientLogger logger = new ClientLogger(Main.class);

    public static void main(String args[]) {

        AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);

        AzureResourceManager azure = AzureResourceManager
                .configure().withLogOptions(new HttpLogOptions().setLogLevel(HttpLogDetailLevel.BODY_AND_HEADERS))
                .authenticate(new DefaultAzureCredentialBuilder().build(), profile)
                .withDefaultSubscription();

        azure.resourceGroups().list().stream().count();

//        long count = azure.accessManagement().servicePrincipals().list().stream().count();
//        logger.info("Number of service principals: {}", count);
//
//        AvsManager manager = AvsManager
//                .authenticate(new ManagedIdentityCredentialBuilder().build(), profile);
//
//        long count = manager.privateClouds().list().stream().count();
//        logger.info("Number of privateClouds: {}", count);
    }
}
