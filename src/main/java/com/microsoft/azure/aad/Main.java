package com.microsoft.azure.aad;

import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.util.logging.ClientLogger;
import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;

public class Main {

    private static final ClientLogger logger = new ClientLogger(Main.class);

    public static void main(String args[]) {
        String rgName = "rg-weidxu";
        String vaultName = "kvweidxu";

        AzureResourceManager azure = AzureResourceManager.authenticate(new ManagedIdentityCredentialBuilder().build(), new AzureProfile(AzureEnvironment.AZURE)).withDefaultSubscription();

        azure.accessManagement().servicePrincipals().list().stream().count();
    }
}
