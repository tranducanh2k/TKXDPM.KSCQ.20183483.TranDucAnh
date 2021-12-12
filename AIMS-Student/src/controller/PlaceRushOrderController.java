package controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;

import common.exception.InvalidDeliveryInfoException;
import entity.invoice.Invoice;
import entity.invoice.InvoiceRushOrder;
import entity.order.Order;
import entity.order.OrderMedia;
import entity.order.RushOrder;
import utils.Configs;

public class PlaceRushOrderController extends BaseController {
	/**
     * Just for logging purpose
     */
    private static Logger LOGGER = utils.Utils.getLogger(PlaceRushOrderController.class.getName());
	/**
	 * convert date from datepicker to timestamp 
	 */
    public Timestamp convert(LocalDate d1, String h) {
    	int hour = 0;
    	for(int i = 0; i<Configs.HOURS.length; i++) {
    		if(h==Configs.HOURS[i])
    		{
    			hour = i;
    			break;
    		}
    	}
    	int year = d1.getYear();
    	int month = d1.getMonthValue();
    	int day = d1.getDayOfMonth();
    	//Timestamp x = new Timestamp(year, month, day, hour, 0, 0, 0);
    	Timestamp x = Timestamp.valueOf(String.format("%04d-%02d-%02d %02d:00:00", year, month, day, hour));
    	LOGGER.info("Timestamp:"+year+"-"+month+"-"+day+" "+hour+":00:00");
    	return x;
    }
    /**
	   * The method validates the info
	   * @param info, LocalDate
	   * @throws InterruptedException
	   * @throws IOException
	   */
	public void validateRushDeliveryInfo(HashMap<String, String> info, LocalDate d1, LocalDate d2, String h1, String h2) throws InterruptedException, IOException{
		if(!this.validateName(info.get("name")))
			throw new InvalidDeliveryInfoException("Invalid Name");
		else if(!this.validateAddress(info.get("address")))
			throw new InvalidDeliveryInfoException("Invalid Address");
		
		else if(!this.validatePhoneNumber(info.get("phone")))
			throw new InvalidDeliveryInfoException("Invalid Phone Number");
		
    	else if(info.get("province")!="Hà Nội")
    		throw new InvalidDeliveryInfoException("Rush Order only supports in Hà Nội");
    	else if(h1==null||h2==null||d1==null||d2==null)
    		throw new InvalidDeliveryInfoException("Missing time interval");
		
		Timestamp t1 = this.convert(d1, h1);
		Timestamp t2 = this.convert(d2, h2);
		if(!this.validateShippingInterval(t1, t2)) {
			throw new InvalidDeliveryInfoException("Invalid Shipping Interval");
		}
	    }
	
	/**
	 * shipping interval có 2 timestamps
	 * mỗi timestamp có 2 thành phần
	 * ngày tháng năm chọn ở date picker ở screen/boundary
	 * giờ chọn trong khoảng [00-23]
	 * phút và giây cho mặc định là 00 và 00, thời gian tối thiểu của interval là 1 giờ
	 * Timestamp.valueOf(String.format("%04d-%02d-%02d %02d:00:00", Year, Month, Day, Hour));
	 */
	public boolean validateShippingInterval(Timestamp T1, Timestamp T2) {
		// T1 has to be before than T2
		if(!T1.before(T2)) return false;
		// T1 has to be after current time
		Timestamp current = new Timestamp(System.currentTimeMillis());
		if(!T1.after(current)) return false;
		return true;
	}
	
	public boolean validatePhoneNumber(String phoneNumber) {
    	if(phoneNumber==null) return false;
    	phoneNumber=phoneNumber.trim();
    	if(phoneNumber.length() != 10) return false;
    	if(!phoneNumber.startsWith("0")) return false;
    	try {
    		Integer.parseInt(phoneNumber);
    	}catch (NumberFormatException e) {
			return false;
		}
    	return true;
    }
    
    public boolean validateName(String name) {
    	if(name==null) return false;
    	name = name.trim();
		if(name.length() == 0) return false;
		// check every character of name
		for(int i = 0; i<name.length(); i++){
			if(!Character.isLetter(name.charAt(i))&&name.charAt(i)!=' ')
				return false;
		}
    	return true;
    }
    
    public boolean validateAddress(String address) {
    	if(address == null) return false;
    	address = address.trim();
		if(address.length() == 0) return false;
		// check every character of name
		for(int i = 0; i<address.length(); i++){
			if(!Character.isLetterOrDigit(address.charAt(i))&&address.charAt(i)!=' ')
				return false;
		}
    	return true;
    }
    
    /**
     * create new normal order from old order (before rush order)
     * contains media that does not support rush order
     */
    public Order oldOrder(Order order) {
    	Order normal  = new Order();
    	normal.setDeliveryInfo(order.getDeliveryInfo());
    	// under construction
    	for (Object object : order.getlstOrderMedia()) {
            OrderMedia om = (OrderMedia) object;
            if(!om.getSupportRushOrder())
            	normal.addOrderMedia(om);
        }
    	normal.setShippingFees(order.getAmountInRO());
    	return normal;
    }
    
    /**
     * create new RO order from old order (before rush order)
     * contains media that does support rush order
     * add media
     */
    public RushOrder newOrder(Order order) {
    	RushOrder normal  = new RushOrder();
    	for (Object object : order.getlstOrderMedia()) {
            OrderMedia om = (OrderMedia) object;
            if(om.getSupportRushOrder())
            	normal.addOrderMedia(om);
        }
    	return normal;
    }
    /**
     * This method calculates the shipping fees of rush order
     * @param order
     * @return shippingFee
     */
    //under construction
    public int calculateShippingFee(RushOrder order){
        Random rand = new Random();
        int fees = (int)( ( (rand.nextFloat()*10)/100 ) * order.getAmount() );
        LOGGER.info("Order Amount: " + order.getAmount() + " -- Shipping Fees: " + fees);
        return fees;
    }
    
    /**
     * This method creates the new Invoice based on new normal order, new rush order, old order
     * @param order
     * @return Invoice
     */
    public InvoiceRushOrder createInvoice(Order order, RushOrder rushOrder, Order bigOldOrder) {
        return new InvoiceRushOrder(order, rushOrder, bigOldOrder);
    }
    
    /**
     * This method creates the new Invoice based on new normal order, new rush order
     * @param order
     * @return Invoice
     */
    public InvoiceRushOrder createInvoice(Order order, RushOrder rushOrder) {
        return new InvoiceRushOrder(order, rushOrder);
    }
    
}
