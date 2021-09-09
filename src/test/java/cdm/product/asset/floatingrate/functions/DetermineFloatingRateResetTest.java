package cdm.product.asset.floatingrate.functions;

import cdm.base.datetime.BusinessCenterEnum;
import cdm.observable.asset.FloatingRateOption;
import cdm.product.asset.InterestRatePayout;
import cdm.product.asset.RateSpecification;
import cdm.product.asset.calculation.functions.LookupNotionalAmountTest;
import cdm.product.asset.floatingrate.FloatingRateSettingDetails;
import cdm.observable.asset.fro.functions.IndexValueObservationDataProvider;
import cdm.product.common.schedule.CalculationPeriodBase;
import cdm.product.common.schedule.ResetDates;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.rosetta.model.lib.records.Date;
import org.isda.cdm.functions.AbstractFunctionTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static cdm.observable.asset.calculatedrate.functions.CalculatedRateTestHelper.date;
import static cdm.observable.asset.calculatedrate.functions.CalculatedRateTestHelper.period;
import static cdm.product.asset.floatingrate.functions.FloatingRateTestHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DetermineFloatingRateResetTest extends AbstractFunctionTest {

	@Inject
	private DetermineFloatingRateReset func;

	@Override
	protected void bindTestingMocks(Binder binder) {
		binder.bind(IndexValueObservationDataProvider.class).toInstance(initIndexData(initFro()));
	}

	@Test
	void shouldEvaluateRate() {
		InterestRatePayout interestRatePayout = initInterestPayout(initFro());

		CalculationPeriodBase calcPeriod = period(date(2020, 12, 10), date(2021, 3, 10));
		check(func.evaluate(interestRatePayout, calcPeriod), 0.01, date(2021, 3, 8));

		calcPeriod = period(date(2021, 9, 29), date(2021, 12, 29));
		check(func.evaluate(interestRatePayout, calcPeriod), 0.01, date(2021, 12, 23));
	}

	private void check(FloatingRateSettingDetails result, double expectedRate, Date fixingDate) {
		assertEquals(BigDecimal.valueOf(expectedRate), result.getFloatingRate());
		assertEquals(fixingDate, result.getObservationDate());
	}

	private InterestRatePayout initInterestPayout(FloatingRateOption fro) {
		ResetDates resetDates = initResetDates(BusinessCenterEnum.GBLO, 3, 2, false);

		return InterestRatePayout.builder()
				.setResetDates(resetDates)
				.setPayoutQuantity(LookupNotionalAmountTest.initNotionalSchedule())
				.setRateSpecification(RateSpecification.builder()
						.setFloatingRate(initFloatingRate(fro)).build())
				.build();
	}
}
