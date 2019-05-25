package cn.edu.fjzzit.weatherforecast;

public class WeatherNow {
    //当前天气(now)
    private String condCode;//实况天气状况代码
    private String condTxt;//实况天气状况
    private String tmp;//当前温度，默认单位：摄氏度
    private String fl;//体感温度，默认单位：摄氏度

    public String getCondCode() {
        return condCode;
    }

    public void setCondCode(String condCode) {
        this.condCode = condCode;
    }

    public String getCondTxt() {
        return condTxt;
    }

    public void setCondTxt(String condTxt) {
        this.condTxt = condTxt;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public String getFl() {
        return fl;
    }

    public void setFl(String fl) {
        this.fl = fl;
    }

    public WeatherNow() {
    }

    public WeatherNow(String condCode, String condTxt, String tmp, String fl) {
        this.condCode = condCode;
        this.condTxt = condTxt;
        this.tmp = tmp;
        this.fl = fl;
    }
}
