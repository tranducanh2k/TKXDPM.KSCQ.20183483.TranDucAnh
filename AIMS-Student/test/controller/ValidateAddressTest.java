package controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ValidateAddressTest {
	
	private PlaceOrderController placeOrderController;

	@BeforeEach
	void setUp() throws Exception {
		placeOrderController = new PlaceOrderController();
	}

	@ParameterizedTest
	@CsvSource({
		"Ha Noi,true",
		"so 15 khuong dinh, true",
		"@hello, false",
		"'',false"
	})
	void test(String address, boolean expected) {
		boolean isValided = placeOrderController.validateAddress(address);
		assertEquals(expected, isValided);
	}

}
