package app.anshuman.lastshop.Model;

public class MyOrders {
    String orderId,orderTime,deliveryTime,orderBy,sellBy,itemId,status;
    int price;

    public MyOrders(String orderId, String orderTime, String deliveryTime, String orderBy, String sellBy, String itemId, String status, int price) {
        this.orderId = orderId;
        this.orderTime = orderTime;
        this.deliveryTime = deliveryTime;
        this.orderBy = orderBy;
        this.sellBy = sellBy;
        this.itemId = itemId;
        this.status = status;
        this.price = price;
    }

    public MyOrders(){}

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getSellBy() {
        return sellBy;
    }

    public void setSellBy(String sellBy) {
        this.sellBy = sellBy;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
