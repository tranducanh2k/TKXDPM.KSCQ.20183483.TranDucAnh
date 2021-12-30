package views.screen.shipping;

import java.io.IOException;
import java.io.ObjectInputFilter.Config;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import controller.PlaceOrderController;
import controller.PlaceRushOrderController;
import controller.ViewCartController;
import common.exception.InvalidDeliveryInfoException;
import common.exception.MediaNotAvailableException;
import common.exception.PlaceOrderException;
import entity.invoice.Invoice;
import entity.order.Order;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import utils.Configs;
import utils.Utils;
import views.screen.BaseScreenHandler;
import views.screen.cart.CartScreenHandler;
import views.screen.invoice.InvoiceScreenHandler;
import views.screen.popup.PopupScreen;
import views.screen.rushorder.RushOrderScreenHandler;

public class ShippingScreenHandler extends BaseScreenHandler implements Initializable {
	
	// TODO: Back to previous screen
	private static Logger LOGGER = Utils.getLogger(ShippingScreenHandler.class.getName());


	@FXML
	private Label errorDisplay;
	
	@FXML
	private CheckBox rushOrderCheckBox;
	
	@FXML
	private Label screenTitle;

	@FXML
	private TextField name;

	@FXML
	private TextField phone;

	@FXML
	private TextField address;

	@FXML
	private TextField instructions;

	@FXML
	private ComboBox<String> province;

	private Order order;

	// order is create when calling the constructor in cartScreen
	public ShippingScreenHandler(Stage stage, String screenPath, Order order) throws IOException {
		super(stage, screenPath);
		this.order = order;
		if(order.checkNewOrder()) {
			this.name.setText("Nhat Linh");
			this.phone.setText("0123456789");
			this.address.setText("62 hoang cau");
			this.instructions.setText("none");
			this.province.setValue("Hà Nội");	
		}
		else {
			this.name.setText(order.getDeliveryInfo().get("name").toString());
			this.phone.setText(order.getDeliveryInfo().get("phone").toString());
			this.address.setText(order.getDeliveryInfo().get("address").toString());
			this.instructions.setText(order.getDeliveryInfo().get("instructions").toString());
			this.province.setValue(order.getDeliveryInfo().get("province").toString());	
		}
		
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		final BooleanProperty firstTime = new SimpleBooleanProperty(true); // Variable to store the focus on stage load
		name.focusedProperty().addListener((observable,  oldValue,  newValue) -> {
            if(newValue && firstTime.get()){
                content.requestFocus(); // Delegate the focus to container
                firstTime.setValue(false); // Variable value changed for future references
            }
        });
		this.province.getItems().addAll(Configs.PROVINCES);
	}

	@FXML
	void submitDeliveryInfo(MouseEvent event) throws IOException, InterruptedException, SQLException {

		// add info to messages
		HashMap messages = new HashMap<>();
		messages.put("name", name.getText());
		messages.put("phone", phone.getText());
		messages.put("address", address.getText());
		messages.put("instructions", instructions.getText());
		messages.put("province", province.getValue());
		try {
			// process and validate delivery info
			getBController().processDeliveryInfo(messages);
		} catch (InvalidDeliveryInfoException e) {
			notifyError(e.getMessage());
			throw new InvalidDeliveryInfoException(e.getMessage());
		}
		
		if(!rushOrderCheckBox.isSelected()){
			// calculate shipping fees
			int shippingFees = getBController().calculateShippingFee(order);
			order.setShippingFees(shippingFees);
			order.setDeliveryInfo(messages);
			
			// create invoice screen
			Invoice invoice = getBController().createInvoice(order);
			BaseScreenHandler InvoiceScreenHandler = new InvoiceScreenHandler(this.stage, Configs.INVOICE_SCREEN_PATH, invoice);
			InvoiceScreenHandler.setPreviousScreen(this);
			InvoiceScreenHandler.setHomeScreenHandler(homeScreenHandler);
			InvoiceScreenHandler.setScreenTitle("Invoice Screen");
			InvoiceScreenHandler.setBController(getBController());
			InvoiceScreenHandler.show();
		}
		else {
			try {
				// process and validate delivery info
				getBController().validateRushOrderAvailability(messages, order);
			} catch (InvalidDeliveryInfoException e) {
				notifyError(e.getMessage());
				throw new InvalidDeliveryInfoException(e.getMessage());
			}
			LOGGER.info("Place Rush Order");
			try {
				// calculate shipping fees
				int shippingFees = getBController().calculateShippingFee(order);
				order.setShippingFees(shippingFees);
				
				order.setDeliveryInfo(messages);	// delivery info for normal order
				requestToPlaceRushOrder(order);
			} catch (SQLException | IOException exp) {
				LOGGER.severe("Cannot place the order, see the logs");
				exp.printStackTrace();
				throw new PlaceOrderException(Arrays.toString(exp.getStackTrace()).replaceAll(", ", "\n"));
			}
		}
	}
	
	private void requestToPlaceRushOrder(Order order) throws SQLException, IOException {
		PlaceRushOrderController placeRushOrderController = new PlaceRushOrderController();
		// under construction
		RushOrderScreenHandler rushOrderScreenHandler = new RushOrderScreenHandler(this.stage, Configs.RUSH_ORDER_SHIPPING_SCREEN_PATH, order);
		rushOrderScreenHandler.setPreviousScreen(this);
		rushOrderScreenHandler.setHomeScreenHandler(homeScreenHandler);
		rushOrderScreenHandler.setScreenTitle("Rush Order Shipping Screen");
		rushOrderScreenHandler.setBController(placeRushOrderController);
		rushOrderScreenHandler.show();
	}

	public PlaceOrderController getBController(){
		return (PlaceOrderController) super.getBController();
	}
	
	public void notifyError(String s){
		errorDisplay.setText(s);
		// TODO: implement later on if we need
	}
	

    @FXML
    void backToCart(MouseEvent event) throws IOException, SQLException {
    	CartScreenHandler cartScreenHandler = new CartScreenHandler(this.stage, Configs.CART_SCREEN_PATH);
    	cartScreenHandler.setHomeScreenHandler(homeScreenHandler);
    	cartScreenHandler.setBController(new ViewCartController());
    	cartScreenHandler.requestToViewCart(this.getPreviousScreen());
    }
    
    public void requestToViewOrder(BaseScreenHandler prevScreen) throws SQLException {
		LOGGER.info("User Click Back To Order");
		setPreviousScreen(prevScreen);
		setScreenTitle("Order Screen");
		//getBController().checkAvailabilityOfProduct();
		//displayCartWithMediaAvailability();
		show();
	}
}
