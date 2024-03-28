package com.microsoft.azure.aad;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.HttpPipelineCallContext;
import com.azure.core.http.HttpPipelineNextPolicy;
import com.azure.core.http.HttpResponse;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.http.policy.HttpPipelinePolicy;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.util.logging.ClientLogger;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import reactor.core.observability.micrometer.Micrometer;
import reactor.core.publisher.Mono;

public class Main {

    private static final ClientLogger LOGGER = new ClientLogger(Main.class);

    public static void main(String ...args) {

        MeterRegistry meterRegistry = new LoggingMeterRegistry();

        // see https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/resourcemanager#authentication for environment variables

        TokenCredential credential = new DefaultAzureCredentialBuilder().build();
        AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);

        AzureResourceManager azureResourceManager = AzureResourceManager
                .configure()
                .withPolicy(new HttpPipelinePolicy() {
                    @Log
                    @Override
                    public Mono<HttpResponse> process(HttpPipelineCallContext context, HttpPipelineNextPolicy next) {
                        return next.process();
                    }
                })
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
