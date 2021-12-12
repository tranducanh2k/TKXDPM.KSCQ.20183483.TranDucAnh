package views.screen.invoice;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.logging.Logger;

import common.exception.ProcessInvoiceException;
import controller.PaymentController;
import controller.PlaceOrderController;
import controller.PlaceRushOrderController;
import entity.invoice.Invoice;
import entity.invoice.InvoiceRushOrder;
import entity.order.OrderMedia;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.Configs;
import utils.Utils;
import views.screen.BaseScreenHandler;
import views.screen.payment.PaymentScreenHandler;
import views.screen.rushorder.RushOrderScreenHandler;

public class InvoiceRushOrderScreenHandler extends BaseScreenHandler {
	
	private static Logger LOGGER = Utils.getLogger(InvoiceRushOrderScreenHandler.class.getName());
	
	// TODO: Back to previous screen
	private InvoiceRushOrder invoiceRushOrder;

	public InvoiceRushOrderScreenHandler(Stage stage, String screenPath, InvoiceRushOrder invoiceRushOrder) throws IOException {
		// TODO Auto-generated constructor stub
		super(stage, screenPath);
		this.invoiceRushOrder = invoiceRushOrder;
		setInvoiceInfo();
	}
	
    private void setInvoiceInfo() {
		// TODO Auto-generated method stub
    	HashMap<String, String> deliveryInfo = invoiceRushOrder.getOrder().getDeliveryInfo();
    	name1.setText(deliveryInfo.get("name"));
		province1.setText(deliveryInfo.get("province"));
		instructions1.setText(deliveryInfo.get("instructions"));
		address1.setText(deliveryInfo.get("address"));
		subtotalNormal.setText(Utils.getCurrencyFormat(invoiceRushOrder.getOrder().getAmount()));
		shippingFeesNormal.setText(Utils.getCurrencyFormat(invoiceRushOrder.getOrder().getShippingFees()));
		
		HashMap<String, String> deliveryInfoRO = invoiceRushOrder.getRushOrder().getDeliveryInfo();
    	nameRO.setText(deliveryInfoRO.get("name"));
		provinceRO.setText(deliveryInfoRO.get("province"));
		instructionsRO.setText(deliveryInfoRO.get("instructions"));
		addressRO.setText(deliveryInfoRO.get("address"));
		subtotalRO.setText(Utils.getCurrencyFormat(invoiceRushOrder.getRushOrder().getAmount()));
		shippingFeesRO.setText(Utils.getCurrencyFormat(invoiceRushOrder.getRushOrder().getShippingFees()));
		String date1 = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(invoiceRushOrder.getRushOrder().getD1());
		String date2 = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(invoiceRushOrder.getRushOrder().getD2());
		timeRO.setText(date1+" - "+date2);
		
		int amount = invoiceRushOrder.getOrder().getAmount() + invoiceRushOrder.getOrder().getShippingFees() + invoiceRushOrder.getRushOrder().getAmount() + invoiceRushOrder.getRushOrder().getShippingFees();
		totalRO.setText(Utils.getCurrencyFormat(amount));
		invoiceRushOrder.setAmount(amount);
		
		invoiceRushOrder.getOrder().getlstOrderMedia().forEach(orderMedia -> {
			try {
				MediaInvoiceScreenHandler mis = new MediaInvoiceScreenHandler(Configs.INVOICE_MEDIA_SCREEN_PATH);
				mis.setOrderMedia((OrderMedia) orderMedia);
				vboxMedia.getChildren().add(mis.getContent());
			} catch (IOException | SQLException e) {
				System.err.println("errors: " + e.getMessage());
				throw new ProcessInvoiceException(e.getMessage());
			}
			
		});
		
		invoiceRushOrder.getRushOrder().getlstOrderMedia().forEach(orderMedia -> {
			try {
				MediaInvoiceScreenHandler mis = new MediaInvoiceScreenHandler(Configs.INVOICE_MEDIA_SCREEN_PATH);
				mis.setOrderMedia((OrderMedia) orderMedia);
				vboxROmedia.getChildren().add(mis.getContent());
			} catch (IOException | SQLException e) {
				System.err.println("errors: " + e.getMessage());
				throw new ProcessInvoiceException(e.getMessage());
			}
			
		});
		
	}

	@FXML
    private Label pageTitle;

    @FXML
    private Label nameRO;

    @FXML
    private Label phoneRO;

    @FXML
    private Label provinceRO;

    @FXML
    private Label addressRO;

    @FXML
    private Label instructionsRO;

    @FXML
    private Label timeRO;

    @FXML
    private VBox vboxROmedia;

    @FXML
    private VBox vboxMedia;

    @FXML
    private Label name1;

    @FXML
    private Label phone1;

    @FXML
    private Label province1;

    @FXML
    private Label address1;

    @FXML
    private Label instructions1;

    @FXML
    private Label subtotalRO;

    @FXML
    private Label shippingFeesRO;

    @FXML
    private Label subtotalNormal;

    @FXML
    private Label shippingFeesNormal;

    @FXML
    private Label totalRO;

    @FXML
    void confirmInvoice(MouseEvent event) throws IOException {
    	BaseScreenHandler paymentScreen = new PaymentScreenHandler(this.stage, Configs.PAYMENT_SCREEN_PATH, invoiceRushOrder);
		paymentScreen.setBController(new PaymentController());
		paymentScreen.setPreviousScreen(this);
		paymentScreen.setHomeScreenHandler(homeScreenHandler);
		paymentScreen.setScreenTitle("Payment Screen");
		paymentScreen.show();
		LOGGER.info("Confirmed invoice");
    }
    
    @FXML
    void backToRushOrder(MouseEvent event) throws IOException, SQLException {
    	RushOrderScreenHandler rushOrderScreenHandler = new RushOrderScreenHandler(this.stage, Configs.RUSH_ORDER_SHIPPING_SCREEN_PATH, this.invoiceRushOrder.getBigOldOrder());
    	rushOrderScreenHandler.setHomeScreenHandler(homeScreenHandler);
    	rushOrderScreenHandler.setBController(new PlaceRushOrderController());
    	rushOrderScreenHandler.requestToViewRushOrder(this.getPreviousScreen());
    }
}
