package org.isda.cdm.processor;

import com.regnosys.rosetta.common.translation.Mapping;
import com.regnosys.rosetta.common.translation.Path;
import com.rosetta.model.lib.path.RosettaPath;
import org.isda.cdm.CsdInitialMargin2018EnglishLaw.CsdInitialMargin2018EnglishLawBuilder;
import org.isda.cdm.ElectiveAmountElection;
import org.isda.cdm.MinimumTransferAmountAmendment;
import org.isda.cdm.MinimumTransferAmountAmendment.MinimumTransferAmountAmendmentBuilder;
import org.isda.cdm.Money;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

class MinimumTransferAmountAmendmentMappingProcessorTest {

	private static final String PARTY_A = "partyA";
	private static final String PARTY_B = "partyB";
	private static final String ZERO = "zero";

	@Test
	void shouldMapMtaa() {
		// set up
		RosettaPath rosettaPath = RosettaPath.valueOf("LegalAgreement.csdInitialMargin2018EnglishLaw.minimumTransferAmountAmendment");
		List<Mapping> mappings = new ArrayList<>();
		mappings.add(new Mapping(Path.parse("answers.partyA.amendment_to_minimum_transfer_amount.partyA_amendment_to_minimum_transfer_amount"), "specify", null, null, "no destination", false, false));
		mappings.add(new Mapping(Path.parse("answers.partyA.amendment_to_minimum_transfer_amount.partyA_amount"), "1000", null, null, "no destination", false, false));
		mappings.add(new Mapping(Path.parse("answers.partyA.amendment_to_minimum_transfer_amount.partyA_currency"), "Japanese Yen", null, null, "no destination", false, false));
		mappings.add(new Mapping(Path.parse("answers.partyA.amendment_to_minimum_transfer_amount.partyB_amendment_to_minimum_transfer_amount"), "other", null, null, "no destination", false, false));
		mappings.add(new Mapping(Path.parse("answers.partyA.amendment_to_minimum_transfer_amount.partyB_specify"), "foo", null, null, "no destination", false, false));

		MinimumTransferAmountAmendmentBuilder builder = MinimumTransferAmountAmendment.builder();
		CsdInitialMargin2018EnglishLawBuilder parent = mock(CsdInitialMargin2018EnglishLawBuilder.class);

		// test
		MinimumTransferAmountAmendmentMappingProcessor processor = new MinimumTransferAmountAmendmentMappingProcessor(rosettaPath, mappings);
		processor.map(builder, parent);
		MinimumTransferAmountAmendment minimumTransferAmountAmendment = builder.build();

		// assert

		ElectiveAmountElection partyA = getPartyElection(minimumTransferAmountAmendment, PARTY_A);
		assertNull(partyA.getCustomElection());
		Money partyAAmount = partyA.getAmount();
		assertEquals(1000, partyAAmount.getAmount().intValue());
		assertEquals("Japanese Yen", partyAAmount.getCurrency().getValue());

		ElectiveAmountElection partyB = getPartyElection(minimumTransferAmountAmendment, PARTY_B);
		assertEquals("foo", partyB.getCustomElection());
		Money partyBAmount = partyB.getAmount();
		assertNull(partyBAmount.getAmount());
		assertNull(partyBAmount.getCurrency());
	}

	private ElectiveAmountElection getPartyElection(MinimumTransferAmountAmendment minimumTransferAmountAmendment, String party) {
		return minimumTransferAmountAmendment.getPartyElections().stream()
				.filter(e -> party.equals(e.getParty()))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("No partyElection found for " + party));
	}
}