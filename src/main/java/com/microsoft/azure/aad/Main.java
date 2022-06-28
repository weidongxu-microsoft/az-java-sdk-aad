package com.microsoft.azure.aad;

import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.util.logging.ClientLogger;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static final ClientLogger logger = new ClientLogger(Main.class);

    public static void main(String args[]) {

        // see https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/resourcemanager#authentication for environment variables

//        AzureProfile profile = new AzureProfile(null, "ec0aa5f7-9e78-40c9-85cd-535c6305b380", AzureEnvironment.AZURE);
//
//        AzureResourceManager azure = AzureResourceManager
//                .authenticate(new ManagedIdentityCredentialBuilder().build(), profile)
//                .withDefaultSubscription();

        AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);

        AzureResourceManager azureResourceManager = AzureResourceManager
                .authenticate(new DefaultAzureCredentialBuilder().build(), profile)
                .withDefaultSubscription();

        List<Flux<?>> fluxList = Arrays.asList(
                azureResourceManager.storageAccounts().listAsync(),
                azureResourceManager.networkSecurityGroups().listAsync(),
                azureResourceManager.applicationSecurityGroups().listAsync(),
                azureResourceManager.routeTables().listAsync(),
                azureResourceManager.networks().listAsync(),
                azureResourceManager.localNetworkGateways().listAsync(),
                azureResourceManager.virtualNetworkGateways().listAsync(),
                azureResourceManager.networkInterfaces().listAsync(),
                azureResourceManager.virtualMachines().listAsync(),
                azureResourceManager.applicationGateways().listAsync(),
                azureResourceManager.loadBalancers().listAsync(),
                // I don't have permission on this
//                azureResourceManager.accessManagement().activeDirectoryUsers().listAsync(),
                azureResourceManager.policyAssignments().listAsync(),
                azureResourceManager.publicIpAddresses().listAsync(),
                azureResourceManager.networks().manager().serviceClient().getAzureFirewalls().listAsync(),
                azureResourceManager.networks().manager().serviceClient().getIpGroups().listAsync(),
                azureResourceManager.sqlServers().manager().serviceClient().getManagedInstances().listAsync(),
                azureResourceManager.sqlServers().listAsync()
                        .flatMap(it -> azureResourceManager.sqlServers().firewallRules().listBySqlServerAsync(it)),
                azureResourceManager.cosmosDBAccounts().listAsync()
        );

        List<Flux<?>> publisherList = fluxList.stream()
                // I don't see why collectList which partly defeats the purpose of Flux, but use it anyway
                .map(it -> it.collectList().flatMapIterable(it1 -> it1))
                .collect(Collectors.toList());

        var retrieve = Flux.merge(publisherList.toArray(new Flux[0]));

//        // follow code does not call SDK at all, but it hangs
//        fluxList = new ArrayList<>();
//        for (int i = 0; i < 18; ++i) {
//            fluxList.add(Flux.defer(() -> Flux.just(1, 2, 3).delayElements(Duration.ofMillis(1))));
//        }
//        publisherList = fluxList.stream()
//                // I don't see why collectList which partly defeats the purpose of Flux, but use it anyway
//                .map(it -> it.collectList().flatMapIterable(it1 -> it1))
//                .collect(Collectors.toList());
//        var retrieve2 = Flux.merge(publisherList.toArray(new Flux[0]));

        Flux.just(1, 2, 3, 4, 5, 6).flatMap(n -> {
            Mono<List<Object>> asyncList = retrieve.collectList();
            return asyncList
                    .doOnNext(it -> System.out.println("size " + it.size()))
                    .doOnSubscribe(it -> System.out.println("start " + n))
                    .subscribeOn(Schedulers.newParallel("AzureWork", 10, true));
        }).repeat(100).blockLast();
    }
}
