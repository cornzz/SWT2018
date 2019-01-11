package flowershop;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.UserAccount;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FlowerShopUserDetailServiceUnitTest extends AbstractIntegrationTests {

	FlowerShopUserDetailService.UserAccountDetails details;
	FlowerShopUserDetailService.UserAccountDetails details2;

	@BeforeAll
	void setup2() {
		System.out.println("Setup 2...");

		UserAccount user = userAccountManager.findByUsername("test").get();
		details = new FlowerShopUserDetailService.UserAccountDetails(user);
		details2 = new FlowerShopUserDetailService.UserAccountDetails(user);
	}

	@Test
	void toStringTest() {
		String result = details.toString();
		assertTrue(result.contains("username=test"));
	}

	@Test
	void equalsTest() {
		assertEquals(details, details2);
		assertTrue(details.canEqual(details2));
		assertEquals(details.hashCode(), details2.hashCode());
	}

}
