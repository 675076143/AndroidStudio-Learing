package cn.edu.fjzzit.weatherforecast;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 工具类
 */
public class MyUtils {
    private String localTime;

    public String getLocalTime() {
        return localTime;
    }

    public void setLocalTime(String localTime) {
        this.localTime = localTime;
    }

    /**
     * 获取生活指数类型
     * @param type 生活指数类型代码
     * @return 生活指数类型
     */
    public String getLifeStyleType(String type)
    {
        switch (type)
        {
            case "comf":
                return "舒适度指数";
            case "cw":
                return "洗车指数";
            case "drsg":
                return "穿衣指数";
            case "flu":
                return "感冒指数";
            case "sport":
                return "运动指数";
            case "trav":
                return "旅游指数";
            case "uv":
                return "紫外线指数";
            case "air":
                return "空气污染扩散指数";
            default:
                return "其他";
        }
    }

    /**
     * 获取生活指数类型图片
     * @param type 生活指数类型代码
     * @return 生活指数类型图片
     */
    public int setLifeStyleTypeImg(String type)
    {
        switch (type)
        {
            case "comf":
                return R.drawable.hearts_48px;
            case "cw":
                return R.drawable.car_48px;
            case "drsg":
                return R.drawable.t_shirt_48px;
            case "flu":
                return R.drawable.sneeze_48px;
            case "sport":
                return R.drawable.exercise_48px;
            case "trav":
                return R.drawable.passenger_with_baggage_48px;
            case "uv":
                return R.drawable.summer_48px;
            case "air":
                return R.drawable.environment_48px;
            default:
                return R.drawable.ic_launcher_background;
        }
    }

    /**
     * 通过实况天气状况代码，判断返回的天气图片
     * @param CondCode 实况天气状况代码
     * @return 图片资源文件ID
     */
    public int setImageByCondCode(String CondCode)
    {
        boolean isDay = isDay(localTime);
        //如果时间是夜间，则在实况天气状况代码(cond_code)后加一个n
        if (!isDay)
        {
            CondCode = CondCode+"n";

        }
        switch (CondCode)
        {
            //白天
            case "100":
                return R.drawable.cond_icon_heweather_100;
            case "101":
                return R.drawable.cond_icon_heweather_101;
            case "102":
                return R.drawable.cond_icon_heweather_102;
            case "103":
                return R.drawable.cond_icon_heweather_103;
            case "104":
                return R.drawable.cond_icon_heweather_104;
            case "200":
                return R.drawable.cond_icon_heweather_200;
            case "201":
                return R.drawable.cond_icon_heweather_201;
            case "202":
                return R.drawable.cond_icon_heweather_202;
            case "203":
                return R.drawable.cond_icon_heweather_203;
            case "204":
                return R.drawable.cond_icon_heweather_204;
            case "205":
                return R.drawable.cond_icon_heweather_205;
            case "206":
                return R.drawable.cond_icon_heweather_206;
            case "207":
                return R.drawable.cond_icon_heweather_207;
            case "208":
                return R.drawable.cond_icon_heweather_208;
            case "209":
                return R.drawable.cond_icon_heweather_209;
            case "210":
                return R.drawable.cond_icon_heweather_210;
            case "211":
                return R.drawable.cond_icon_heweather_211;
            case "212":
                return R.drawable.cond_icon_heweather_212;
            case "213":
                return R.drawable.cond_icon_heweather_213;
            case "300":
                return R.drawable.cond_icon_heweather_300;
            case "301":
                return R.drawable.cond_icon_heweather_301;
            case "302":
                return R.drawable.cond_icon_heweather_302;
            case "303":
                return R.drawable.cond_icon_heweather_303;
            case "304":
                return R.drawable.cond_icon_heweather_304;
            case "305":
                return R.drawable.cond_icon_heweather_305;
            case "306":
                return R.drawable.cond_icon_heweather_306;
            case "307":
                return R.drawable.cond_icon_heweather_307;
            case "308":
                return R.drawable.cond_icon_heweather_308;
            case "309":
                return R.drawable.cond_icon_heweather_309;
            case "310":
                return R.drawable.cond_icon_heweather_310;
            case "311":
                return R.drawable.cond_icon_heweather_311;
            case "312":
                return R.drawable.cond_icon_heweather_312;
            case "313":
                return R.drawable.cond_icon_heweather_313;
            case "314":
                return R.drawable.cond_icon_heweather_314;
            case "315":
                return R.drawable.cond_icon_heweather_315;
            case "316":
                return R.drawable.cond_icon_heweather_316;
            case "317":
                return R.drawable.cond_icon_heweather_317;
            case "318":
                return R.drawable.cond_icon_heweather_318;
            case "399":
                return R.drawable.cond_icon_heweather_399;
            case "400":
                return R.drawable.cond_icon_heweather_400;
            case "401":
                return R.drawable.cond_icon_heweather_401;
            case "402":
                return R.drawable.cond_icon_heweather_402;
            case "403":
                return R.drawable.cond_icon_heweather_403;
            case "404":
                return R.drawable.cond_icon_heweather_404;
            case "405":
                return R.drawable.cond_icon_heweather_405;
            case "406":
                return R.drawable.cond_icon_heweather_406;
            case "407":
                return R.drawable.cond_icon_heweather_407;
            case "408":
                return R.drawable.cond_icon_heweather_408;
            case "409":
                return R.drawable.cond_icon_heweather_409;
            case "410":
                return R.drawable.cond_icon_heweather_410;
            case "499":
                return R.drawable.cond_icon_heweather_499;
            case "500":
                return R.drawable.cond_icon_heweather_500;
            case "501":
                return R.drawable.cond_icon_heweather_501;
            case "502":
                return R.drawable.cond_icon_heweather_502;
            case "503":
                return R.drawable.cond_icon_heweather_503;
            case "504":
                return R.drawable.cond_icon_heweather_504;
            case "507":
                return R.drawable.cond_icon_heweather_507;
            case "508":
                return R.drawable.cond_icon_heweather_508;
            case "509":
                return R.drawable.cond_icon_heweather_509;
            case "510":
                return R.drawable.cond_icon_heweather_510;
            case "511":
                return R.drawable.cond_icon_heweather_511;
            case "512":
                return R.drawable.cond_icon_heweather_512;
            case "513":
                return R.drawable.cond_icon_heweather_513;
            case "514":
                return R.drawable.cond_icon_heweather_514;
            case "515":
                return R.drawable.cond_icon_heweather_515;
            case "900":
                return R.drawable.cond_icon_heweather_900;
            case "901":
                return R.drawable.cond_icon_heweather_901;
            case "999":
                return R.drawable.cond_icon_heweather_999;
            //夜间
            case "100n":
                return R.drawable.cond_icon_heweather_100n;
            case "101n":
                return R.drawable.cond_icon_heweather_101;
            case "102n":
                return R.drawable.cond_icon_heweather_102;
            case "103n":
                return R.drawable.cond_icon_heweather_103n;
            case "104n":
                return R.drawable.cond_icon_heweather_104n;
            case "200n":
                return R.drawable.cond_icon_heweather_200;
            case "201n":
                return R.drawable.cond_icon_heweather_201;
            case "202n":
                return R.drawable.cond_icon_heweather_202;
            case "203n":
                return R.drawable.cond_icon_heweather_203;
            case "204n":
                return R.drawable.cond_icon_heweather_204;
            case "205n":
                return R.drawable.cond_icon_heweather_205;
            case "206n":
                return R.drawable.cond_icon_heweather_206;
            case "207n":
                return R.drawable.cond_icon_heweather_207;
            case "208n":
                return R.drawable.cond_icon_heweather_208;
            case "209n":
                return R.drawable.cond_icon_heweather_209;
            case "210n":
                return R.drawable.cond_icon_heweather_210;
            case "211n":
                return R.drawable.cond_icon_heweather_211;
            case "212n":
                return R.drawable.cond_icon_heweather_212;
            case "213n":
                return R.drawable.cond_icon_heweather_213;
            case "300n":
                return R.drawable.cond_icon_heweather_300n;
            case "301n":
                return R.drawable.cond_icon_heweather_301n;
            case "302n":
                return R.drawable.cond_icon_heweather_302;
            case "303n":
                return R.drawable.cond_icon_heweather_303;
            case "304n":
                return R.drawable.cond_icon_heweather_304;
            case "305n":
                return R.drawable.cond_icon_heweather_305;
            case "306n":
                return R.drawable.cond_icon_heweather_306;
            case "307n":
                return R.drawable.cond_icon_heweather_307;
            case "308n":
                return R.drawable.cond_icon_heweather_308;
            case "309n":
                return R.drawable.cond_icon_heweather_309;
            case "310n":
                return R.drawable.cond_icon_heweather_310;
            case "311n":
                return R.drawable.cond_icon_heweather_311;
            case "312n":
                return R.drawable.cond_icon_heweather_312;
            case "313n":
                return R.drawable.cond_icon_heweather_313;
            case "314n":
                return R.drawable.cond_icon_heweather_314;
            case "315n":
                return R.drawable.cond_icon_heweather_315;
            case "316n":
                return R.drawable.cond_icon_heweather_316;
            case "317n":
                return R.drawable.cond_icon_heweather_317;
            case "318n":
                return R.drawable.cond_icon_heweather_318;
            case "399n":
                return R.drawable.cond_icon_heweather_399;
            case "400n":
                return R.drawable.cond_icon_heweather_400;
            case "401n":
                return R.drawable.cond_icon_heweather_401;
            case "402n":
                return R.drawable.cond_icon_heweather_402;
            case "403n":
                return R.drawable.cond_icon_heweather_403;
            case "404n":
                return R.drawable.cond_icon_heweather_404;
            case "405n":
                return R.drawable.cond_icon_heweather_405;
            case "406n":
                return R.drawable.cond_icon_heweather_406n;
            case "407n":
                return R.drawable.cond_icon_heweather_407n;
            case "408n":
                return R.drawable.cond_icon_heweather_408;
            case "409n":
                return R.drawable.cond_icon_heweather_409;
            case "410n":
                return R.drawable.cond_icon_heweather_410;
            case "499n":
                return R.drawable.cond_icon_heweather_499;
            case "500n":
                return R.drawable.cond_icon_heweather_500;
            case "501n":
                return R.drawable.cond_icon_heweather_501;
            case "502n":
                return R.drawable.cond_icon_heweather_502;
            case "503n":
                return R.drawable.cond_icon_heweather_503;
            case "504n":
                return R.drawable.cond_icon_heweather_504;
            case "507n":
                return R.drawable.cond_icon_heweather_507;
            case "508n":
                return R.drawable.cond_icon_heweather_508;
            case "509n":
                return R.drawable.cond_icon_heweather_509;
            case "510n":
                return R.drawable.cond_icon_heweather_510;
            case "511n":
                return R.drawable.cond_icon_heweather_511;
            case "512n":
                return R.drawable.cond_icon_heweather_512;
            case "513n":
                return R.drawable.cond_icon_heweather_513;
            case "514n":
                return R.drawable.cond_icon_heweather_514;
            case "515n":
                return R.drawable.cond_icon_heweather_515;
            case "900n":
                return R.drawable.cond_icon_heweather_900;
            case "901n":
                return R.drawable.cond_icon_heweather_901;
            case "999n":
                return R.drawable.cond_icon_heweather_999;
            default:
                return R.drawable.cond_icon_heweather_999;

        }

    }

    /**
     * 判断是否为白天(6:00-18:00)
     * @param localTime 接口更新时间(当地时间loc)
     * @return 白天为True
     */
    private boolean isDay(String localTime)
    {
        String format = "HH:mm";
        Date nowTime = null;
        Date startTime = null;
        Date endTime = null;
        try {
            nowTime = new SimpleDateFormat(format).parse(localTime);
            startTime = new SimpleDateFormat(format).parse("06:00");
            endTime = new SimpleDateFormat(format).parse("18:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar now = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        now.setTime(nowTime);
        start.setTime(startTime);
        end.setTime(endTime);
        if (now.after(start) && now.before(end))
        {
            return true;
        }else {
            return false;
        }

    }
}
