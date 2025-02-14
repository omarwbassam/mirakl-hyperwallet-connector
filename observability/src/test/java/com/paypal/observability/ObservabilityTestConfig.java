package com.paypal.observability;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.paypal.observability.hyperwalletapichecks.HyperwalletAPIHealthCheckerConfig;
import com.paypal.observability.miraklapichecks.MiraklAPIHealthCheckerConfig;
import com.paypal.observability.mirakldocschecks.MiraklDocsCheckerConfig;
import com.paypal.observability.miraklfieldschecks.MiraklFieldsCheckerConfig;
import com.paypal.observability.miraklschemadiffs.MiraklSchemaDiffsConfig;
import com.paypal.observability.startupchecks.StartupCheckConfig;

@Configuration
//@formatter:off
@Import({StartupCheckConfig.class,
		MiraklFieldsCheckerConfig.class,
		MiraklDocsCheckerConfig.class,
		MiraklSchemaDiffsConfig.class,
		MiraklAPIHealthCheckerConfig.class,
		HyperwalletAPIHealthCheckerConfig.class,
		MockserverTestConfig.class
})
//@formatter:on
public class ObservabilityTestConfig {

}
