package entity.invoice;

import entity.order.Order;
import entity.order.RushOrder;

public class InvoiceRushOrder extends Invoice {
	private Order bigOldOrder;
	
	public Order getBigOldOrder() {
		return bigOldOrder;
	}

	public void setBigOldOrder(Order bigOldOrder) {
		this.bigOldOrder = bigOldOrder;
	}

	protected RushOrder rushOrder;
	
	public InvoiceRushOrder(Order order, RushOrder rushOrder){
        this.order = order;
        this.rushOrder = rushOrder;
    }
	
	public InvoiceRushOrder(Order order, RushOrder rushOrder, Order bigOldOrder){
        this.order = order;
        this.rushOrder = rushOrder;
        this.bigOldOrder = bigOldOrder;
    }
	
	public RushOrder getRushOrder() {
        return rushOrder;
    }
}
