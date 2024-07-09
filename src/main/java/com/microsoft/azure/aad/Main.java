package com.microsoft.azure.aad;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.util.logging.ClientLogger;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.resources.ResourceManager;
import com.azure.resourcemanager.resources.models.ParameterDefinitionsValue;
import com.azure.resourcemanager.resources.models.ParameterType;
import com.azure.resourcemanager.resources.models.PolicyDefinition;
import com.azure.resourcemanager.resources.models.PolicyType;

import java.util.Map;

public class Main {

    private static final ClientLogger LOGGER = new ClientLogger(Main.class);

    public static void main(String ...args) {

        // see https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/resourcemanager#authentication for environment variables

        // define env variable AZURE_SUBSCRIPTION_ID for subscription ID

        // DO NOT CODE SECRET
        TokenCredential credential = new DefaultAzureCredentialBuilder().build();
        AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);

        final ResourceManager resourceManager = ResourceManager.configure()
                .withLogLevel(HttpLogDetailLevel.BODY_AND_HEADERS)
                .authenticate(credential, profile)
                .withDefaultSubscription();

        final PolicyDefinition.DefinitionStages.WithCreate policyDefinition = resourceManager.policyDefinitions()
                .define("policyName")
                .withPolicyRuleJson("{\"if\":{\"not\":{\"field\":\"name\",\"like\":\"[concat(parameters('prefix'),'*',parameters('suffix'))]\"}},\"then\":{\"effect\":\"deny\"}}")
                .withDisplayName("displayName")
                .withDescription("description")
                .withPolicyType(PolicyType.CUSTOM)
                .withMode("All")
                .withMetadata(Map.of("category", "Compute"));

        policyDefinition.withParameter("prefix", ParameterType.STRING, "dept");

        policyDefinition.withParameter("suffix", new ParameterDefinitionsValue()
                .withType(ParameterType.STRING)
                .withDefaultValue("-US"));

        PolicyDefinition policyDefinitionCreated = policyDefinition.create();

        LOGGER.info("PolicyDefinition created, rule: " + policyDefinitionCreated.policyRule().toString());

        resourceManager.policyDefinitions().deleteByName("policyName");

        LOGGER.info("PolicyDefinition deleted");
    }
}
