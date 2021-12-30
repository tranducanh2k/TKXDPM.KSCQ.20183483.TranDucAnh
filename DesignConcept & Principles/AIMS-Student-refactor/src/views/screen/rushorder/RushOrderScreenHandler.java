package views.screen.rushorder;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import common.exception.InvalidDeliveryInfoException;
import controller.BaseController;
import controller.PlaceOrderController;
import controller.PlaceRushOrderController;
import controller.ViewCartController;
import entity.invoice.Invoice;
import entity.invoice.InvoiceRushOrder;
import entity.order.Order;
import entity.order.RushOrder;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.Configs;
import utils.Utils;
import views.screen.BaseScreenHandler;
import views.screen.home.HomeScreenHandler;
import views.screen.invoice.InvoiceRushOrderScreenHandler;
import views.screen.invoice.InvoiceScreenHandler;
import views.screen.shipping.ShippingScreenHandler;

public class RushOrderScreenHandler extends BaseScreenHandler implements Initializable{
	// TODO: Back to previous screen
	private static Logger LOGGER = Utils.getLogger(RushOrderScreenHandler.class.getName());
	
	@FXML
	private ComboBox Hour1, Hour2;
	
	@FXML
	private DatePicker Date1, Date2;
	
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
	private ComboBox<String> province, hour1, hour2;
	
	@FXML
	private DatePicker date1, date2;
	
	@FXML
    private Button btnConfirmDelivery;
	
	@FXML
    private Label errorDisplay;

	private Order order;
	
	public RushOrderScreenHandler(Stage stage, String screenPath, Order order) throws IOException{
		super(stage, screenPath);
		this.order = order;
		this.name.setText(order.getDeliveryInfo().get("name").toString());
		this.phone.setText(order.getDeliveryInfo().get("phone").toString());
		this.address.setText(order.getDeliveryInfo().get("address").toString());
		this.instructions.setText(order.getDeliveryInfo().get("instructions").toString());
		this.province.setValue(order.getDeliveryInfo().get("province").toString());	
	}
	
	 @FXML
	void submitDeliveryInfo(MouseEvent event) throws IOException, InterruptedException, SQLException{
		// add info to messages
			HashMap messages = new HashMap<>();
			messages.put("name", name.getText());
			messages.put("phone", phone.getText());
			messages.put("address", address.getText());
			messages.put("instructions", instructions.getText());
			messages.put("province", province.getValue());
			try {
				// process and validate delivery info
				getBController().validateRushDeliveryInfo(messages, date1.getValue(), date2.getValue(), hour1.getValue(), hour2.getValue());
			} catch (InvalidDeliveryInfoException e) {
				notifyError(e.getMessage());
				throw new InvalidDeliveryInfoException(e.getMessage());
			}
			Timestamp T1 = getBController().convert(date1.getValue(), hour1.getValue());
			Timestamp T2 = getBController().convert(date2.getValue(), hour2.getValue());
			// create normal order
			Order old = getBController().oldOrder(order);
			// create new rush order
			RushOrder now = getBController().newOrder(order);
			int rushOrderShippingFees = getBController().calculateShippingFee(now);
			now.setD1(T1);
			now.setD2(T2);
			now.setDeliveryInfo(messages);
			now.setShippingFees(rushOrderShippingFees);
			
			// create invoice screen
			InvoiceRushOrder invoiceRushOrder = getBController().createInvoice(old, now, this.order);
			BaseScreenHandler invoiceBaseScreenHandler = new InvoiceRushOrderScreenHandler(this.stage, Configs.INVOICE_RUSH_ORDER_SCREEN_PATH, invoiceRushOrder);
			invoiceBaseScreenHandler.setPreviousScreen(this);
			invoiceBaseScreenHandler.setHomeScreenHandler(homeScreenHandler);
			invoiceBaseScreenHandler.setScreenTitle("Rush Order Invoice");
			invoiceBaseScreenHandler.setBController(getBController());
			invoiceBaseScreenHandler.show();
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
		this.hour1.getItems().addAll(Configs.HOURS);
		this.hour2.getItems().addAll(Configs.HOURS);
	}

	public PlaceRushOrderController getBController(){
		return (PlaceRushOrderController) super.getBController();
	}
	public void notifyError(String s){
		errorDisplay.setText(s);
		// TODO: implement later on if we need
	}
	
	@FXML
    void backToOrderScreen(MouseEvent event) throws IOException, SQLException {
		ShippingScreenHandler shippingScreenHandler = new ShippingScreenHandler(this.stage, Configs.SHIPPING_SCREEN_PATH, this.order);
		shippingScreenHandler.setHomeScreenHandler(homeScreenHandler);
    	shippingScreenHandler.setBController(new PlaceOrderController());
    	shippingScreenHandler.requestToViewOrder(this.getPreviousScreen());
    }
	
	public void requestToViewRushOrder(BaseScreenHandler prevScreen) throws SQLException {
		LOGGER.info("User Click Back To Rush Order");
		setPreviousScreen(prevScreen);
		setScreenTitle("Rush Order Screen");
		//getBController().checkAvailabilityOfProduct();
		//displayCartWithMediaAvailability();
		show();
	}

}
