package flowershop;

import de.olivergierke.moduliths.model.Modules;
import org.junit.jupiter.api.Test;

class FlowershopModularityTests {

	@Test
	void assertModularity() {
		Modules.of(Application.class).verify();
	}

}
