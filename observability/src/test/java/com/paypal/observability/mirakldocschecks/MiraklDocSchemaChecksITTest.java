package com.paypal.observability.mirakldocschecks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import com.paypal.observability.mirakldocschecks.services.MiraklDocSchemaCheckerService;
import com.paypal.observability.miraklschemadiffs.model.diff.MiraklSchemaDiffEntryType;
import com.paypal.observability.miraklschemadiffs.model.report.MiraklSchemaDiffReport;
import com.paypal.observability.testsupport.AbstractSchemaChecksITTest;
import com.paypal.observability.testsupport.ObservabilityWebIntegrationContext;

import static org.assertj.core.api.Assertions.assertThat;

@ObservabilityWebIntegrationContext
class MiraklDocSchemaChecksITTest extends AbstractSchemaChecksITTest {

	@Value("${hyperwallet.kycAutomated}")
	private boolean originalIsKycAutomated;

	@Autowired
	private MiraklDocSchemaCheckerService miraklDocSchemaCheckerService;

	@BeforeEach
	void setupKycAutomatedStatus() {
		setKycAutomated(true);
	}

	@AfterEach
	void resetKycAutomatedStatus() {
		setKycAutomated(originalIsKycAutomated);
	}

	@Test
	void shouldCheckAllDocuments() {
		docsMockServerFixtures.mockGetDocsConfiguration_correctSchemaResponse();

		final MiraklSchemaDiffReport diffReport = miraklDocSchemaCheckerService.checkMiraklDocs();
		assertThat(diffReport.hasEntries()).isFalse();
	}

	@Test
	void shouldNotCheckDocumentsWhenKycIsDeactivated() {
		docsMockServerFixtures.mockGetDocsConfiguration_emptyResponse();
		setKycAutomated(false);

		final MiraklSchemaDiffReport diffReport = miraklDocSchemaCheckerService.checkMiraklDocs();
		assertThat(diffReport.hasEntries()).isFalse();
	}

	@Test
	void shouldDetectAdditionalDocs() {
		docsMockServerFixtures.mockGetDocsConfiguration_additionalDoc();

		final MiraklSchemaDiffReport diffReport = miraklDocSchemaCheckerService.checkMiraklDocs();
		assertThat(diffReport.getDiffs()).hasSize(1);
		assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.UNEXPECTED_ITEM, "hw-unexpected-doc-1");
	}

	@Test
	void shouldDetectNotFoundDocs() {
		docsMockServerFixtures.mockGetDocsConfiguration_notFoundDoc();

		final MiraklSchemaDiffReport diffReport = miraklDocSchemaCheckerService.checkMiraklDocs();
		assertThat(diffReport.getDiffs()).hasSize(1);
		assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.ITEM_NOT_FOUND, "hw-bsh5-proof-identity-back");
	}

	@Test
	void shouldDetectIncorrectLabel() {
		docsMockServerFixtures.mockGetDocsConfiguration_incorrectLabel();

		final MiraklSchemaDiffReport diffReport = miraklDocSchemaCheckerService.checkMiraklDocs();
		assertThat(diffReport.getDiffs()).hasSize(1);
		assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE,
				"hw-bsh5-proof-identity-back");
	}

	@Test
	void shouldDetectIncorrectDescription() {
		docsMockServerFixtures.mockGetDocsConfiguration_incorrectDescription();

		final MiraklSchemaDiffReport diffReport = miraklDocSchemaCheckerService.checkMiraklDocs();
		assertThat(diffReport.getDiffs()).hasSize(1);
		assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE,
				"hw-bsh5-proof-identity-back");
	}

	@Test
	void shouldDetectMultipleDiffs() {
		docsMockServerFixtures.mockGetDocsConfiguration_mutipleDiffs();

		final MiraklSchemaDiffReport diffReport = miraklDocSchemaCheckerService.checkMiraklDocs();
		assertThat(diffReport.getDiffs()).hasSize(4);
		assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.UNEXPECTED_ITEM,
				"hw-bsh5-proof-identity-back-CHANGED");
		assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.ITEM_NOT_FOUND, "hw-bsh5-proof-identity-back");
		assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE,
				"hw-bsh5-proof-identity-front");
		assertThatContainsEntry(diffReport, MiraklSchemaDiffEntryType.INCORRECT_ATTRIBUTE_VALUE,
				"hw-bsh5-proof-identity-front");
	}

	private void setKycAutomated(final boolean isKycAutomated) {
		ReflectionTestUtils.setField(miraklDocSchemaCheckerService, "isKycAutomated", isKycAutomated);
	}

}
