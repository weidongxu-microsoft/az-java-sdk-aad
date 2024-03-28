package com.microsoft.azure.aad;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.util.logging.ClientLogger;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import reactor.core.observability.micrometer.Micrometer;

public class Main {

    private static final ClientLogger LOGGER = new ClientLogger(Main.class);

    public static void main(String ...args) {

        MeterRegistry meterRegistry = new LoggingMeterRegistry();

        // see https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/resourcemanager#authentication for environment variables

        TokenCredential credential = new DefaultAzureCredentialBuilder().build();
        AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);

        AzureResourceManager azureResourceManager = AzureResourceManager
                .configure()
                .withLogLevel(HttpLogDetailLevel.BASIC)
                .authenticate(credential, profile)
                .withDefaultSubscription();

        var rg = azureResourceManager.resourceGroups().getByNameAsync("rg-weidxu")
                .name("getByNameAsync")
                .tap(Micrometer.metrics(meterRegistry))
                .log()
                .block();

        meterRegistry.close();
    }
}
