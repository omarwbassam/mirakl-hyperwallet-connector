package com.paypal.observability.loggingcontext.batchjobs.listeners;

import com.callibrity.logging.test.LogTracker;
import com.callibrity.logging.test.LogTrackerStub;
import com.paypal.infrastructure.batchjob.BatchJobContext;
import com.paypal.infrastructure.batchjob.BatchJobItem;
import com.paypal.infrastructure.batchjob.BatchJobItemValidationResult;
import com.paypal.observability.batchjoblogging.listeners.BatchJobLoggingListener;
import com.paypal.observability.batchjoblogging.service.BatchJobLoggingContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;

import static org.apache.commons.lang3.ArrayUtils.EMPTY_THROWABLE_ARRAY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BatchJobLoggingListenerTest {

	public static final String JOB_NAME = "jobName";

	public static final int NUMBER_OF_ITEMS_TO_BE_PROCESSED = 22;

	public static final String ITEM_TYPE = "itemType";

	public static final String ITEM_ID = "itemId";

	public static final int NUMBER_OF_ITEMS_PROCESSED = 20;

	public static final int NUMBER_OF_ITEMS_FAILED = 2;

	public static final int NUMBER_OF_ITEMS_REMAINING = 0;

	@InjectMocks
	private BatchJobLoggingListener testObj;

	@Mock
	private BatchJobLoggingContextService batchJobLoggingContextServiceMock;

	@RegisterExtension
	final LogTrackerStub logTrackerStub = LogTrackerStub.create().recordForType(BatchJobLoggingListener.class);

	@Mock
	private BatchJobContext batchJobContextMock;

	@Mock
	private BatchJobItem<?> batchJobItemMock;

	@Mock
	private BatchJobItemValidationResult batchJobItemValidationResultMock;

	@Mock
	private Collection<BatchJobItem<?>> extractedItemsMock;

	@Mock
	private Exception exceptionMock;

	@BeforeEach
	public void setUp() {
		lenient().when(batchJobContextMock.getJobName()).thenReturn(JOB_NAME);
		lenient().when(batchJobContextMock.getNumberOfItemsToBeProcessed()).thenReturn(NUMBER_OF_ITEMS_TO_BE_PROCESSED);
		lenient().when(batchJobContextMock.getNumberOfItemsProcessed()).thenReturn(NUMBER_OF_ITEMS_PROCESSED);
		lenient().when(batchJobContextMock.getNumberOfItemsFailed()).thenReturn(NUMBER_OF_ITEMS_FAILED);
		lenient().when(batchJobContextMock.getNumberOfItemsRemaining()).thenReturn(NUMBER_OF_ITEMS_REMAINING);
		lenient().when(batchJobItemMock.getItemType()).thenReturn(ITEM_TYPE);
		lenient().when(batchJobItemMock.getItemId()).thenReturn(ITEM_ID);
	}

	@Test
	void beforeItemExtraction_ShouldLogAnInfoMessage() {

		testObj.beforeItemExtraction(batchJobContextMock);

		assertThat(logTrackerStub.contains("Starting extraction of items to be processed")).isTrue();
	}

	@Test
	void onItemExtractionSuccessful_ShouldLogAnInfoMessage() {

		testObj.onItemExtractionSuccessful(batchJobContextMock, extractedItemsMock);

		assertThat(logTrackerStub.contains(
				"Retrieved the following number of items to be processed: " + NUMBER_OF_ITEMS_TO_BE_PROCESSED))
						.isTrue();
	}

	@Test
	void onItemExtractionFailure_ShouldLogAnErrorMessage() {
		when(exceptionMock.getSuppressed()).thenReturn(EMPTY_THROWABLE_ARRAY);

		logTrackerStub.recordForLevel(LogTracker.LogLevel.ERROR);

		testObj.onItemExtractionFailure(batchJobContextMock, exceptionMock);

		assertThat(logTrackerStub.contains("Failed retrieval of items")).isTrue();
	}

	@Test
	void beforeProcessingItem_ShouldLogAnInfoMessage() {
		testObj.beforeProcessingItem(batchJobContextMock, batchJobItemMock);

		assertThat(logTrackerStub.contains("Processing item of type " + ITEM_TYPE + " with id: " + ITEM_ID)).isTrue();
	}

	@Test
	void onItemProcessingFailure_ShouldLogAnInfoAndAnErrorMessage() {
		when(exceptionMock.getSuppressed()).thenReturn(EMPTY_THROWABLE_ARRAY);

		logTrackerStub.recordForLevel(LogTracker.LogLevel.ERROR).recordForLevel(LogTracker.LogLevel.INFO);

		testObj.onItemProcessingFailure(batchJobContextMock, batchJobItemMock, exceptionMock);

		assertThat(logTrackerStub.contains("Failed processing item of type " + ITEM_TYPE + " with id: " + ITEM_ID))
				.isTrue();
		assertThat(logTrackerStub.contains(NUMBER_OF_ITEMS_PROCESSED + " items processed successfully. "
				+ NUMBER_OF_ITEMS_FAILED + " items failed. " + NUMBER_OF_ITEMS_REMAINING + " items remaining"))
						.isTrue();

	}

	@Test
	void onItemProcessingSuccess_ShouldLogInfoMessages() {

		testObj.onItemProcessingSuccess(batchJobContextMock, batchJobItemMock);

		assertThat(logTrackerStub.contains("Processed successfully item of type " + ITEM_TYPE + " with id: " + ITEM_ID))
				.isTrue();
		assertThat(logTrackerStub.contains(NUMBER_OF_ITEMS_PROCESSED + " items processed successfully. "
				+ NUMBER_OF_ITEMS_FAILED + " items failed. " + NUMBER_OF_ITEMS_REMAINING + " items remaining"))
						.isTrue();

	}

	@Test
	void onItemProcessingValidationFailure_ShouldLogWarnMessage() {
		testObj.onItemProcessingValidationFailure(batchJobContextMock, batchJobItemMock,
				batchJobItemValidationResultMock);

		assertThat(logTrackerStub
				.contains("Validation of item of type " + ITEM_TYPE + " with id: " + ITEM_ID + " has failed")).isTrue();
	}

}
