package com.paypal.observability.hyperwalletapichecks;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.paypal.observability.hyperwalletapichecks.model.HyperwalletAPICheck;
import com.paypal.observability.hyperwalletapichecks.model.HyperwalletAPICheckStatus;
import com.paypal.observability.hyperwalletapichecks.services.HyperwalletHealthCheckService;
import com.paypal.observability.testsupport.AbstractMockServerITTest;
import com.paypal.observability.testsupport.ObservabilityWebIntegrationContext;

import static org.assertj.core.api.Assertions.assertThat;

@ObservabilityWebIntegrationContext
class HyperwalletAPIChecksITTest extends AbstractMockServerITTest {

	@Autowired
	private HyperwalletHealthCheckService hyperwalletHealthCheckService;

	@Test
	void shouldCheckWhenHyperwalletIsUp() {
		hyperwalletHealthMockServerFixtures.mockGetHealth_up();

		final HyperwalletAPICheck hyperwalletAPICheck = hyperwalletHealthCheckService.check();

		assertThat(hyperwalletAPICheck.getHyperwalletAPICheckStatus()).isEqualTo(HyperwalletAPICheckStatus.UP);
		assertThat(hyperwalletAPICheck.getLocation()).contains("http://localhost");
		assertThat(hyperwalletAPICheck.getError()).isNull();
	}

	@Test
	void shouldCheckWhenHyperwalletIsDown() {
		hyperwalletHealthMockServerFixtures.mockGetHealth_down();

		final HyperwalletAPICheck hyperwalletAPICheck = hyperwalletHealthCheckService.check();

		assertThat(hyperwalletAPICheck.getHyperwalletAPICheckStatus()).isEqualTo(HyperwalletAPICheckStatus.DOWN);
		assertThat(hyperwalletAPICheck.getLocation()).contains("http://localhost");
		assertThat(hyperwalletAPICheck.getError()).isEqualTo("Error message");
	}

}
