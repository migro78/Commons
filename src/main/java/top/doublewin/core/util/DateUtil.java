package top.doublewin.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import top.doublewin.core.support.context.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * 日期操作辅助类
 *
 * @author ShenHuaJie
 * @version $Id: DateUtil.java, v 0.1 2014年3月28日 上午8:58:11 ShenHuaJie Exp $
 */
public final class DateUtil {
    /**
     * 周一为一周开始模式
     */
    public static final int WEEK_START_MONDAY_MODE = 1;
    /**
     * 周日为一周开始模式
     */
    public static final int WEEK_START_SUNDAY_MODE = 2;

    private DateUtil() {
    }

    /**
     * 日期格式
     **/
    public interface DATE_PATTERN {
        String HHMMSS = "HHmmss";
        String HH_MM_SS = "HH:mm:ss";
        String YYYYMMDD = "yyyyMMdd";
        String YYYY_MM_DD = "yyyy-MM-dd";
        String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
        String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
        String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
        String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    }

    /**
     * 格式化日期
     *
     * @param date
     * @return
     */
    public static final String format(Object date) {
        return format(date, DATE_PATTERN.YYYY_MM_DD);
    }

    /**
     * 格式化日期
     *
     * @param date
     * @param pattern
     * @return
     */
    public static final String format(Object date, String pattern) {
        if (date == null) {
            return null;
        }
        if (pattern == null) {
            return format(date);
        }
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 获取当前时间的一周开始时间
     *
     * @param curDate 指定时间所在周
     * @param mode    显示模式 DateUtil.WEEK_START_MONDAY_MODE周一为一周开始，DateUtil.WEEK_START_SUNDAY_MODE周日为一周开始
     * @return
     */
    public static final Date getWeekStartDate(Date curDate, int mode) {
        if (DataUtil.isEmpty(curDate)) {
            curDate = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(curDate);
        // 去当前日期是周几
        if (mode == DateUtil.WEEK_START_MONDAY_MODE) {
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
        }
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DATE, calendar.getFirstDayOfWeek() - weekday);

        return calendar.getTime();
    }


    /**
     * 获取日期
     *
     * @return
     */
    public static final String getDate() {
        return format(new Date());
    }

    /**
     * 获取日期时间
     *
     * @return
     */
    public static final String getDateTime() {
        return format(new Date(), DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 获取日期
     *
     * @param pattern
     * @return
     */
    public static final String getDateTime(String pattern) {
        return format(new Date(), pattern);
    }

    /**
     * 日期计算
     *
     * @param date
     * @param field
     * @param amount
     * @return
     */
    public static final Date addDate(Date date, int field, int amount) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, amount);
        return calendar.getTime();
    }

    /**
     * 字符串转换为日期:不支持yyM[M]d[d]格式
     *
     * @param date
     * @return
     */
    public static final Date stringToDate(String date) {
        if (date == null) {
            return null;
        }
        String separator = String.valueOf(date.charAt(4));
        String pattern = "yyyyMMdd";
        if (!separator.matches("\\d*")) {
            pattern = "yyyy" + separator + "MM" + separator + "dd";
            if (date.length() < 10) {
                pattern = "yyyy" + separator + "M" + separator + "d";
            }
            pattern += " HH:mm:ss.SSS";
        } else if (date.length() < 8) {
            pattern = "yyyyMd";
        } else {
            pattern += "HHmmss.SSS";
        }
        pattern = pattern.substring(0, Math.min(pattern.length(), date.length()));
        try {
            return new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 间隔天数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static final Integer getDayBetween(Date startDate, Date endDate) {
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        end.set(Calendar.HOUR_OF_DAY, 0);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);

        long n = end.getTimeInMillis() - start.getTimeInMillis();
        return (int) (n / (60 * 60 * 24 * 1000L));
    }

    /**
     * 间隔月
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static final Integer getMonthBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null || !startDate.before(endDate)) {
            return null;
        }
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        int year1 = start.get(Calendar.YEAR);
        int year2 = end.get(Calendar.YEAR);
        int month1 = start.get(Calendar.MONTH);
        int month2 = end.get(Calendar.MONTH);
        int n = (year2 - year1) * 12;
        n = n + month2 - month1;
        return n;
    }

    /**
     * 间隔月，多一天就多算一个月
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static final Integer getMonthBetweenWithDay(Date startDate, Date endDate) {
        if (startDate == null || endDate == null || !startDate.before(endDate)) {
            return null;
        }
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        int year1 = start.get(Calendar.YEAR);
        int year2 = end.get(Calendar.YEAR);
        int month1 = start.get(Calendar.MONTH);
        int month2 = end.get(Calendar.MONTH);
        int n = (year2 - year1) * 12;
        n = n + month2 - month1;
        int day1 = start.get(Calendar.DAY_OF_MONTH);
        int day2 = end.get(Calendar.DAY_OF_MONTH);
        if (day1 <= day2) {
            n++;
        }
        return n;
    }

    /**
     * 对时间段参数进行自动拆分，拆分后参数名为：参数名+Start,参数名+End 的值保存到Map集合中
     * 例如：createTime:["2018-07-01","2018-07-02"]
     *
     * @param param 参数集合
     * @param key   需要拆分的参数名
     */
    public static final void splitDate(Map<String, Object> param, String key) {
        if (DataUtil.isNotEmpty(param.get(key))) {
            String s = param.get(key).toString();
            JSONArray ja = JSON.parseArray(s);
            if (DataUtil.isNotEmpty(ja)) {
                if (ja.size() > 0) {
                    Date start = DateUtil.stringToDate(ja.getString(0).trim());
                    param.put(key + "Start", start);
                }
                if (ja.size() > 1) {
                    Date end = DateUtil.stringToDate(ja.getString(1).trim().length() < 11 ? ja.getString(1).trim() + " 23:59:59" : ja.getString(1).trim());
                    param.put(key + "End", end);
                }
            }
        }
    }

    public static final void splitDate2Int(Map<String, Object> param, String key) {
        if (DataUtil.isNotEmpty(param.get(key))) {
            String s = param.get(key).toString();
            JSONArray ja = JSON.parseArray(s);
            if (DataUtil.isNotEmpty(ja)) {
                if (ja.size() > 0) {
                    Integer start = Integer.valueOf(ja.getString(0).trim());
                    param.put(key + "Start", start);
                }
                if (ja.size() > 1) {
                    Integer end = Integer.valueOf(ja.getString(1).trim());
                    param.put(key + "End", end);
                }
            }
        }
    }

    /**
     * @return String[]
     * @Author rxf113
     * @Description 根据Date取当前周星期一和当前的时间,"yyyy-MM-dd"格式
     * String[0]:星期一日期 ,String[1]:当前日期
     * @Date 15:54 2018/8/10
     * @Param [date]
     **/
    public static String[] getWeekStrArray(Date date) {
        SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int week = c.get(Calendar.DAY_OF_WEEK);
        Integer a = 0;
        switch (week) {
            case 1:
                a = -6;
                break;
            case 2:
                break;
            default:
                a = -(week - 2);
        }
        c.add(Calendar.DAY_OF_MONTH, a);
        String mondayStr = dateFm.format(c.getTime());
        String nowStr = dateFm.format(date);
        String[] resultArray = {mondayStr, nowStr};
        return resultArray;
    }

    public static final boolean isOverTime(Date date) {
        if (date.getTime() > System.currentTimeMillis()) {
            return false;
        }
        return true;
    }

    /**
     * 获取友好型与当前时间的差
     *
     * @param millis 毫秒时间戳
     * @return 友好型与当前时间的差
     * <ul>
     * <li>如果小于1秒钟内，显示刚刚</li>
     * <li>如果在1分钟内，显示XXX秒前</li>
     * <li>如果在1小时内，显示XXX分钟前</li>
     * <li>如果在1小时外的今天内，显示今天15:32</li>
     * <li>如果是昨天的，显示昨天15:32</li>
     * <li>如果是当年的，显示10-15</li>
     * <li>其余显示，2016-10-15</li>
     * <li>时间不合法的情况全部日期和时间信息，如2018-05-13 14:21:20</li>
     * </ul>
     */
    public static String getFriendly(long millis) {
        long now = System.currentTimeMillis();
        long span = now - millis;
        if (span < 0) {
            return String.format("%tF %tT", millis, millis);
        }
        if (span < 1000) {
            return "刚刚";
        } else if (span < Constants.TIMES.MINUTE) {
            return String.format("%d秒前", span / Constants.TIMES.SECOND);
        } else if (span < Constants.TIMES.HOUR) {
            return String.format("%d分钟前", span / Constants.TIMES.MINUTE);
        }
        // 获取当天00:00
        long wee = now / Constants.TIMES.DAY * Constants.TIMES.DAY;
        if (millis >= wee) {
            return String.format("今天%tR", millis);
        } else if (millis >= wee - Constants.TIMES.DAY) {
            return String.format("昨天%tR", millis);
        } else {
            wee = now / Constants.TIMES.YEAR * Constants.TIMES.YEAR;
            if (millis >= wee) {
                return String.format("%tm-%te", millis, millis);
            }
            return String.format("%tF", millis);
        }
    }

    /**
     * 获取某年某月第一天
     *
     * @return
     */
    public static String getFirstDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month - 1);
        //获取某月最小天数
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String firstDayOfMonth = sdf.format(cal.getTime());
        return firstDayOfMonth;
    }

    /**
     * 获取传入时间的年月日时间范围数组
     *
     * @param
     * @return
     */
    public static String[] getDay(Date firstDay, Date lastDay) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String first = format.format(firstDay.getTime());
        String last = format.format(lastDay.getTime());
        String[] zero = new String[2];
        zero[0] = first;
        zero[1] = last;
        return zero;
    }

    public static String[] getDay(String firstDay, String lastDay) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String first = format.format(firstDay);
        String last = format.format(lastDay);
        String[] zero = new String[2];
        zero[0] = first;
        zero[1] = last;
        return zero;
    }

    /**
     * 获取当月第一天 年月日
     *
     * @return
     */
    public static String getFirstday() {
        // 获取当前年份、月份、日期
        Calendar cale = null;
        cale = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String firstDay;
        // 获取前月的第一天
        cale.add(Calendar.MONTH, 0);
        cale.set(Calendar.DAY_OF_MONTH, 1);
        firstDay = format.format(cale.getTime());
        return firstDay;
    }

    /**
     * 获取当月最后一天的时间 年月日
     *
     * @return
     */
    public static String getLastday() {
        // 获取当前年份、月份、日期
        Calendar cale = null;
        cale = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String lastDay;
        cale.add(Calendar.MONTH, 1);
        cale.set(Calendar.DAY_OF_MONTH, 0);
        lastDay = format.format(cale.getTime());
        return lastDay;
    }

    /**
     * 通过生日计算年龄（小于1岁，显示月和天）
     *
     * @param
     * @return
     */
    public static String getAgeFromBirthday(Date birthday) {
        String age = null;
        int year = 0, month = 0, day = 0;
        if (DataUtil.isEmpty(birthday)) {
            return age;
        }

        Calendar start = Calendar.getInstance();
        start.setTime(birthday);
        Calendar end = Calendar.getInstance();
        int year1 = start.get(Calendar.YEAR);
        int year2 = end.get(Calendar.YEAR);
        int month1 = start.get(Calendar.MONTH);
        int month2 = end.get(Calendar.MONTH);
        int day1 = start.get(Calendar.DAY_OF_MONTH);
        int day2 = end.get(Calendar.DAY_OF_MONTH);
        year = year2 - year1;
        month = month2 - month1;
        day = day2 - day1;
        // 辅算
        if (day < 0) {
            day = 30 + day;
            month = month - 1;
        }
        if (month < 0) {
            month = 12 + month;
            year = year - 1;
        }


        System.out.println("year=" + year + ",month=" + month + ",day=" + day);

        if (year > 2) {
            // 只显示年
            age = year + "岁";
        } else {
            // 显示年月日
            age = (year == 0 ? "" : year + "岁") + (month == 0 ? "" : month + "月") + (day == 0 ? "" : day + "天");
        }

        return age;
    }

    public static void main(String[] args) {

        //System.out.println(DateUtil.getWeekStartDate(new Date(), DateUtil.WEEK_START_MONDAY_MODE));
        System.out.println(DateUtil.getAgeFromBirthday(DateUtil.stringToDate("2020-05-18")));

    }

}
