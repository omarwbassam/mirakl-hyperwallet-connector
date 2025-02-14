package com.paypal.observability.testsupport;

import com.paypal.observability.testsupport.fixtures.AdditionalFieldsMockServerFixtures;
import com.paypal.observability.testsupport.fixtures.DocsMockServerFixtures;
import com.paypal.observability.testsupport.fixtures.HealthMockServerFixtures;
import com.paypal.observability.testsupport.fixtures.HyperwalletHealthMockServerFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.mockserver.client.MockServerClient;

public abstract class AbstractMockServerITTest {

	protected MockServerClient mockServerClient;

	protected AdditionalFieldsMockServerFixtures additionalFieldsMockServerFixtures;

	protected DocsMockServerFixtures docsMockServerFixtures;

	protected HealthMockServerFixtures healthMockServerFixtures;

	protected HyperwalletHealthMockServerFixtures hyperwalletHealthMockServerFixtures;

	@BeforeEach
	void setUpFixtures() {
		additionalFieldsMockServerFixtures = new AdditionalFieldsMockServerFixtures(mockServerClient);
		docsMockServerFixtures = new DocsMockServerFixtures(mockServerClient);
		healthMockServerFixtures = new HealthMockServerFixtures(mockServerClient);
		hyperwalletHealthMockServerFixtures = new HyperwalletHealthMockServerFixtures(mockServerClient);
	}

}
