package com.paypal.sellers.batchjobs.bankaccount;

import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.sellers.bankaccountextract.service.strategies.HyperWalletBankAccountStrategyExecutor;
import com.paypal.sellers.sellersextract.model.SellerModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BankAccountExtractBatchJobItemProcessorTest {

	@InjectMocks
	private BankAccountExtractBatchJobItemProcessor testObj;

	@Mock
	private HyperWalletBankAccountStrategyExecutor hyperWalletBankAccountStrategyExecutorMock;

	@Mock
	private BatchJobContext batchJobContextMock;

	@Test
	void processItem_ShouldExecuteHyperWalletBankAccountServiceExecutor() {

		final SellerModel sellerModel = SellerModel.builder().build();
		final BankAccountExtractJobItem bankAccountExtractJobItem = new BankAccountExtractJobItem(sellerModel);

		testObj.processItem(batchJobContextMock, bankAccountExtractJobItem);

		verify(hyperWalletBankAccountStrategyExecutorMock).execute(sellerModel);
	}

}
