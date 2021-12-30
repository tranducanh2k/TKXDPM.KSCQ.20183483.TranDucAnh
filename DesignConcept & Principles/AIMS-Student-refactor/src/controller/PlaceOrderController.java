package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entity.cart.Cart;
import entity.cart.CartMedia;
import common.exception.InvalidDeliveryInfoException;
import entity.invoice.Invoice;
import entity.order.Order;
import entity.order.OrderMedia;
import views.screen.popup.PopupScreen;

/**
 * This class controls the flow of place order usecase in our AIMS project
 * @author nguyenlm
 */
public class PlaceOrderController extends BaseController{

    /**
     * Just for logging purpose
     */
    private static Logger LOGGER = utils.Utils.getLogger(PlaceOrderController.class.getName());

    /**
     * This method checks the avalibility of product when user click PlaceOrder button
     * @throws SQLException
     */
    public void placeOrder() throws SQLException{
        Cart.getCart().checkAvailabilityOfProduct();
    }

    /**
     * This method creates the new Order based on the Cart
     * @return Order
     * @throws SQLException
     */
    public Order createOrder() throws SQLException{
        Order order = new Order();
        for (Object object : Cart.getCart().getListMedia()) {
            CartMedia cartMedia = (CartMedia) object;
            OrderMedia orderMedia = new OrderMedia(cartMedia.getMedia(), 
                                                   cartMedia.getQuantity(), 
                                                   cartMedia.getPrice());    
            order.getlstOrderMedia().add(orderMedia);
        }
        return order;
    }

    /**
     * This method creates the new Invoice based on order
     * @param order
     * @return Invoice
     */
    public Invoice createInvoice(Order order) {
        return new Invoice(order);
    }

    /**
     * This method takes responsibility for processing the shipping info from user
     * @param info
     * @throws InterruptedException
     * @throws IOException
     */
    public void processDeliveryInfo(HashMap info) throws InterruptedException, IOException{
        LOGGER.info("Process Delivery Info");
        LOGGER.info(info.toString());
        validateDeliveryInfo(info);
    }
    
    /**
   * The method validates the info
   * @param info
   * @throws InterruptedException
   * @throws IOException
   */
    public void validateDeliveryInfo(HashMap<String, String> info) throws InterruptedException, IOException{
    	if(!this.validateAddress(info.get("address")))
    		throw new InvalidDeliveryInfoException("Invalid Address");
    	else if(!this.validateName(info.get("name")))
    		throw new InvalidDeliveryInfoException("Invalid Name");
    	else if(!this.validatePhoneNumber(info.get("phone")))
    		throw new InvalidDeliveryInfoException("Invalid Phone");
    	else if(info.get("province")==null)
    		throw new InvalidDeliveryInfoException("Blank Province");
    }
    
    public void validateRushOrderAvailability(HashMap<String, String> info, Order order) throws InterruptedException, IOException{
    	//check = 1 de test chuc nang rush order vi khong co san pham nao ho tro, check = 0 de ve mac dinh
    	int check = 1;	// 0 is invalid/throw; 1 is return;
    		for (Object object : order.getlstOrderMedia()) {
                OrderMedia om = (OrderMedia) object;
                if(om.getSupportRushOrder() == true) {
                	check = 1;
                	break;
                }
            }
    	if(check == 1) return;
    	else throw new InvalidDeliveryInfoException("No Media supports Rush Order");
    }
    
    public boolean validatePhoneNumber(String phoneNumber) {
    	// TODO: your work
    	if(phoneNumber.length() != 10) 	return false;
    	
    	if(!phoneNumber.startsWith("0")) return false;
    	
    	try {
    		Integer.parseInt(phoneNumber);
    	}catch (NumberFormatException e) {
    		return false;
    	}
    	
    	return true;
    }
    
    public boolean validateName(String name) {
    	// TODO: your work
    	Pattern p = Pattern.compile("[^a-z A-Z]", Pattern.CASE_INSENSITIVE);
    	Matcher m = p.matcher(name);
    	boolean b = m.find();

    	if(name == null || name.isEmpty() || b) return false;
    	
    	
    	return true;
    }
    
    public boolean validateAddress(String address) {
    	// TODO: your work
    	Pattern p = Pattern.compile("[^a-zA-Z 0-9]", Pattern.CASE_INSENSITIVE);
    	Matcher m = p.matcher(address);
    	boolean b = m.find();

    	if(address == null || address.isEmpty() || b) return false;
    	
    	return true;
    }
    

    /**
     * This method calculates the shipping fees of order
     * @param order
     * @return shippingFee
     */
    public int calculateShippingFee(Order order){
        Random rand = new Random();
        int fees = (int)( ( (rand.nextFloat()*10)/100 ) * order.getAmount() );
        LOGGER.info("Order Amount: " + order.getAmount() + " -- Shipping Fees: " + fees);
        return fees;
    }
}
