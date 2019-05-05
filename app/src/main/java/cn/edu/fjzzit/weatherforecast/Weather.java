package cn.edu.fjzzit.weatherforecast;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

public class Weather extends LitePalSupport {
    private String cityId;
    private String tmpMin;
    private String tmpMax;
    private String date;
    private String condTxtD;

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    private String condTxtN;

    public String getTmpMin() {
        return tmpMin;
    }

    public void setTmpMin(String tmpMin) {
        this.tmpMin = tmpMin;
    }

    public String getTmpMax() {
        return tmpMax;
    }

    public void setTmpMax(String tmpMax) {
        this.tmpMax = tmpMax;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCondTxtD() {
        return condTxtD;
    }

    public void setCondTxtD(String condTxtD) {
        this.condTxtD = condTxtD;
    }

    public String getCondTxtN() {
        return condTxtN;
    }

    public void setCondTxtN(String condTxtN) {
        this.condTxtN = condTxtN;
    }

    public Weather() {
    }

    public Weather(String cityId, String tmpMin, String tmpMax, String date, String condTxtD, String condTxtN) {
        this.cityId = cityId;
        this.tmpMin = tmpMin;
        this.tmpMax = tmpMax;
        this.date = date;
        this.condTxtD = condTxtD;
        this.condTxtN = condTxtN;
    }
}
