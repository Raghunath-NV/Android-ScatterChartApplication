package mad.com.inclass06;

import org.json.JSONException;
import org.json.JSONObject;


public class Data {
    String id, cost, sales, item;
    static public Data createData(JSONObject js){
        Data prod=new Data();
        try {
            prod.setId(js.getString("id"));
            prod.setCost(js.getString("cost"));
            prod.setItem(js.getString("item"));
            prod.setSales(js.getString("sales"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return prod;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getSales() {
        return sales;
    }

    public void setSales(String sales) {
        this.sales = sales;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "Data{" +
                "id='" + id + '\'' +
                ", cost='" + cost + '\'' +
                ", sales='" + sales + '\'' +
                ", item='" + item + '\'' +
                '}';
    }
}
