package cn.edu.fjzzit.weatherforecast;
import org.litepal.crud.LitePalSupport;

public class City extends LitePalSupport {
    private String cityId;
    private String location;//城市
    private String parent_city;//市
    private String admin_area;//省
    private String cnty;//国家

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getParent_city() {
        return parent_city;
    }

    public void setParent_city(String parent_city) {
        this.parent_city = parent_city;
    }

    public String getAdmin_area() {
        return admin_area;
    }

    public void setAdmin_area(String admin_area) {
        this.admin_area = admin_area;
    }

    public String getCnty() {
        return cnty;
    }

    public void setCnty(String cnty) {
        this.cnty = cnty;
    }
    public City(String cityId, String location, String parent_city, String admin_area, String cnty) {
        this.cityId = cityId;
        this.location = location;
        this.parent_city = parent_city;
        this.admin_area = admin_area;
        this.cnty = cnty;
    }

    public City() {
    }
}
