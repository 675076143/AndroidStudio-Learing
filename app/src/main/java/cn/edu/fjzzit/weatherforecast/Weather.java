package cn.edu.fjzzit.weatherforecast;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

public class Weather extends LitePalSupport {
    //基础信息(basic)
    private String cityId;//地区／城市ID
    //七天预报(daily_forecast)
    private String tmpMin;//最高温度
    private String tmpMax;//最低温度
    private String date;//预报日期
    private String condTxtD;//白天天气状况描述
    private String condTxtN;//晚间天气状况描述
    private String condCodeD;//	白天天气状况代码
    private String condCodeN;//	白天天气状况代码


    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

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

    public String getCondCodeD() {
        return condCodeD;
    }

    public void setCondCodeD(String condCodeD) {
        this.condCodeD = condCodeD;
    }

    public String getCondCodeN() {
        return condCodeN;
    }

    public void setCondCodeN(String condCodeN) {
        this.condCodeN = condCodeN;
    }



    public Weather() {
    }

    public Weather(String cityId,
                   String tmpMin, String tmpMax, String date,
                   String condTxtD, String condTxtN, String condCodeD, String condCodeN) {
        this.cityId = cityId;
        this.tmpMin = tmpMin;
        this.tmpMax = tmpMax;
        this.date = date;
        this.condTxtD = condTxtD;
        this.condTxtN = condTxtN;
        this.condCodeD = condCodeD;
        this.condCodeN = condCodeN;

    }
}
