package controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ValidatePhoneNumberTest {
	
	private PlaceOrderController placeOrderController;

	@BeforeEach
	void setUp() throws Exception {
		placeOrderController = new PlaceOrderController();
	}
	
	@ParameterizedTest
	@CsvSource({
		"0123456789,true",
		"012345,false",
		"abc123,false",
		"1234567890,false"
	})
	void test(String phone, boolean expected) {
		boolean isValided = placeOrderController.validatePhoneNumber(phone);
		assertEquals(expected, isValided);
	}

}
