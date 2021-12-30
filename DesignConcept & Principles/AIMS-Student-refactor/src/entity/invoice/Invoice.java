package entity.invoice;

import entity.order.Order;

public class Invoice {

    protected Order order;
    protected int amount;
    
    public Invoice(){

    }

    public Invoice(Order order){
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void saveInvoice(){
        
    }
}
