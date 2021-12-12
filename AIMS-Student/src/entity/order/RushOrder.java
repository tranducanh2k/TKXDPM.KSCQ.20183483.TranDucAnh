package entity.order;

import java.sql.Timestamp;
import java.util.Date;

import utils.Configs;

public class RushOrder extends Order{
	
	private Timestamp d1, d2;
	
	public Timestamp getD1() {
		return d1;
	}

	public void setD1(Timestamp d1) {
		this.d1 = d1;
	}

	public Timestamp getD2() {
		return d2;
	}

	public void setD2(Timestamp d2) {
		this.d2 = d2;
	}

	@Override
	public int getAmountInRO(){
		double amount = 0;
        for (Object object : lstOrderMedia) {
            OrderMedia om = (OrderMedia) object;
            if(om.getSupportRushOrder())
            	amount += om.getPrice();
        }
        return (int) (amount + (Configs.PERCENT_VAT/100)*amount);
	}
	
}
