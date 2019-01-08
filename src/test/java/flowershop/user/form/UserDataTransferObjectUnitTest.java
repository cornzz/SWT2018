package flowershop.user.form;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDataTransferObjectUnitTest {

	private final UserDataTransferObject form = new UserDataTransferObject();

	@Test
	void toStringTest() {
		assertEquals(form.toString(), "UserDataTransferObject{firstName='null', lastName='null', username='null'," +
				" email='null', phone='null', password='null', passwordRepeat='null', oldPassword='null', isEmpty=false}");
	}

}
