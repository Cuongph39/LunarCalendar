package com.vanpt.lunarcalendar.utils;

import com.vanpt.lunarcalendar.models.DateObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by vanpt on 11/23/2016.
 */

public final class DateConverter {

    public static final double PI = Math.PI;
    public static final String[] CAN_AM_LICH = new String[]{
            "Giáp", "Ất", "Bính", "Đinh", "Mậu", "Kỷ", "Canh", "Tân", "Nhâm", "Quý"
    };
    public static final String[] CHI_AM_LICH = new String[] {
            "Tý", "Sửu", "Dần", "Mão", "Thìn", "Tị", "Ngọ", "Mùi", "Thân", "Dậu", "Tuất", "Hợi"
    };

    private static final int[][] CAN_GIO = new int[][] {
            {0, 2, 4, 6, 8, 0, 2, 4, 6, 8},
            {1, 3, 5, 7, 9, 1, 3, 5, 7, 9},
            {2, 4, 6, 8, 0, 2, 4, 6, 8, 0},
            {3, 5, 7, 9, 1, 3, 5, 7, 9, 1},
            {4, 6, 8, 0, 2, 4, 6, 8, 0, 2},
            {5, 7, 9, 1, 3, 5, 7, 9, 1, 3},
            {6, 8, 0, 2, 4, 6, 8, 0, 2, 4},
            {7, 9, 1, 3, 5, 7, 9, 1, 3, 5},
            {8, 0, 2, 4, 6, 8, 0, 2, 4, 6},
            {9, 1, 3, 5, 7, 9, 1, 3, 5, 7},
            {0, 2, 4, 6, 8, 0, 2, 4, 6, 8},
            {1, 3, 5, 7, 9, 1, 3, 5, 7, 9},
    };

    private static int[] NGAY_TIET_KHI = new int[]{
            6, 21, 4, 19, 5, 31, 5, 20, 6, 21, 6, 21, 7, 23, 7, 23, 8, 23, 8, 23, 7, 22, 7, 22
    };

    private static final String[] TIET_AM_LICH = new String[] {
            "Tiểu hàn", "Đại hàn", "Lập xuân", "Vũ thủy", "Kinh trập", "Xuân phân",
            "Thanh minh", "Cốc vũ", "Lập hạ", "Tiểu mãn", "Mang chủng", "Hạ chí",
            "Tiểu thử", "Đại thử", "Lập thu", "Xử thử", "Bạch lộ", "Thu phân",
            "Hàn lộ", "Sương giáng", "Lập đông", "Tiểu tuyết", "Đại tuyết", "Đông chí",
    };
    //"Tý" 0, "Sửu" 1, "Dần" 2, "Mão" 3, "Thìn" 4, "Tị" 5, "Ngọ" 6, "Mùi" 7, "Thân" 8, "Dậu" 9, "Tuất" 10, "Hợi" 11
    private static final int[][] GIO_HOANG_DAO = new int[][] {
            {0, 1, 3, 6, 8, 9},
            {2, 3, 4, 8, 10, 11},
            {0, 1, 4, 5, 6, 10},
            {1, 2, 3, 6, 7, 9},
            {2, 4, 5, 8, 9, 11},
            {1, 4, 6, 7, 10, 11},
            {0, 1, 3, 6, 8, 9},
            {2, 3, 5, 8, 10, 11},
            {0, 1, 4, 5, 7, 10},
            {0, 2, 3, 6, 7, 9},
            {2, 4, 5, 8, 9, 11},
            {1, 4, 6, 7, 10, 11}
    };

    public static final String[] VN_DAYS = new String[] {
            "Chủ Nhật", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7"
    };

    public static String convertToLunarYear(int year) {
        int can = (year + 6) % 10;
        int chi = (year + 8) % 12;
        return CAN_AM_LICH[can] + " " + CHI_AM_LICH[chi];
    }

    public static boolean isLeapYear(int year) {
        boolean isLeap = false;
        if ((year % 4) == 0) {
            if ((year % 100) == 0) {
                if ((year % 400) == 0) {
                    isLeap = true;
                } else {
                    isLeap = false;
                }
            } else {
                isLeap = true;
            }
        }
        return isLeap;
    }

    public static long getDateDiff(Date date1, Date date2) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        cal.set(Calendar.HOUR_OF_DAY, 1);
        cal.set(Calendar.MINUTE, 1);
        cal.set(Calendar.SECOND, 1);
        cal.set(Calendar.MILLISECOND, 1);
        Date d1 = cal.getTime();
        cal.setTime(date2);
        cal.set(Calendar.HOUR_OF_DAY, 1);
        cal.set(Calendar.MINUTE, 1);
        cal.set(Calendar.SECOND, 1);
        cal.set(Calendar.MILLISECOND, 1);
        Date d2 = cal.getTime();
        long diffInMillies = d2.getTime() - d1.getTime();
        long diff = (Math.abs(diffInMillies))/(1000*60*60*24);
        return diff;
    }

    public static String convertToCanChiDay(int day, int month, int year) {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(1997, 2, 1); // 1/3/1997 la ngay Nham Dan
        Calendar cal2 = Calendar.getInstance();
        cal2.set(year, month - 1, day);
        int diff = (int) (Math.abs(cal1.getTime().getTime() - cal2.getTime().getTime())/(1000*60*60*24));
        int canIndex = diff % 10 + 8;
        if (canIndex >= 10) {
            canIndex -= 10;
        }
        int chiIndex = diff % 12 + 2;
        if (chiIndex >= 12) {
            chiIndex -= 12;
        }
        return CAN_AM_LICH[canIndex] + " " + CHI_AM_LICH[chiIndex];
    }

    public static String convertToCanChiMonth(int month, int year) {
        // Thang 1/2016 la Ky Suu
        int diffYear;
        int diffMonth;
        if (year == 2016) {
            diffMonth = month - 1;
        } else if (year > 2016) {
            diffYear = year - 2016;
            diffMonth = diffYear * 12 + month - 1;
        } else {
            diffYear = 2016 - year;
            diffMonth = diffYear * 12 + 1 - month;
        }
        int canIndex = diffMonth % 10 + 5;
        if (canIndex >= 10) {
            canIndex -= 10;
        }
        return CAN_AM_LICH[canIndex] + " " + CHI_AM_LICH[month - 1];
    }

    public static String convertToCanChiHour(int day, int month, int year, int hour) {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(1997, 2, 1); // 1/3/1997 la ngay Nham Dan
        Calendar cal2 = Calendar.getInstance();
        cal2.set(year, month - 1, day);
        int diff = (int) (Math.abs(cal1.getTime().getTime() - cal2.getTime().getTime())/(1000*60*60*24));
        int canIndex = diff % 10 + 8;
        if (canIndex >= 10) {
            canIndex -= 10;
        }
        int chiHour = hour/2;
        int canHour = CAN_GIO[chiHour][canIndex];
        return CAN_AM_LICH[canHour] + " " + CHI_AM_LICH[chiHour];
    }

    public static String getTietAmLichName(int day, int month) throws Exception {
        int m = month - 1;
        m = m * 2;
        if (day >= NGAY_TIET_KHI[m]) {
            if (day < NGAY_TIET_KHI[m + 1]) {
                return TIET_AM_LICH[m];
            } else {
                return TIET_AM_LICH[m + 1];
            }
        } else {
            m -= 1;
            if (m < 0) {
                m = TIET_AM_LICH.length - 1;
            }
            return TIET_AM_LICH[m];
        }
    }

    public static int[] getGioHoangDao(int day, int month, int year) {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(1997, 2, 1); // 1/3/1997 la ngay Nham Dan
        Calendar cal2 = Calendar.getInstance();
        cal2.set(year, month - 1, day);
        int diff = (int) (Math.abs(cal1.getTime().getTime() - cal2.getTime().getTime())/(1000*60*60*24));
        int chiIndex = diff % 12 + 2;
        if (chiIndex >= 12) {
            chiIndex -= 12;
        }
        return GIO_HOANG_DAO[chiIndex];
    }

    /**
     *
     * @return the number of days since 1 January 4713 BC (Julian calendar)
     */
    public static int jdFromDate(DateObject date) {
        int dd = date.getDay();
        int mm = date.getMonth();
        int yy = date.getYear();
        int a = (14 - mm) / 12;
        int y = yy+4800-a;
        int m = mm+12*a-3;
        int jd = dd + (153*m+2)/5 + 365*y + y/4 - y/100 + y/400 - 32045;
        if (jd < 2299161) {
            jd = dd + (153*m+2)/5 + 365*y + y/4 - 32083;
        }
        //jd = jd - 1721425;
        return jd;
    }
    /**
     * http://www.tondering.dk/claus/calendar.html
     * Section: Is there a formula for calculating the Julian day number?
     * @param jd - the number of days since 1 January 4713 BC (Julian calendar)
     * @return
     */
    public static DateObject jdToDate(int jd) throws Exception {
        int a, b, c;
        if (jd > 2299160) { // After 5/10/1582, Gregorian calendar
            a = jd + 32044;
            b = (4*a+3)/146097;
            c = a - (b*146097)/4;
        } else {
            b = 0;
            c = jd + 32082;
        }
        int d = (4*c+3)/1461;
        int e = c - (1461*d)/4;
        int m = (5*e+2)/153;
        int day = e - (153*m+2)/5 + 1;
        int month = m + 3 - 12*(m/10);
        int year = b*100 + d - 4800 + m/10;
        return new DateObject(day, month, year, 0);
    }
    /**
     * Solar longitude in degrees
     * Algorithm from: Astronomical Algorithms, by Jean Meeus, 1998
     * @param jdn - number of days since noon UTC on 1 January 4713 BC
     * @return
     */
    public static double SunLongitude(double jdn) {
        //return CC2K.sunLongitude(jdn);
        return SunLongitudeAA98(jdn);
    }
    public static double SunLongitudeAA98(double jdn) {
        double T = (jdn - 2451545.0 ) / 36525; // Time in Julian centuries from 2000-01-01 12:00:00 GMT
        double T2 = T*T;
        double dr = PI/180; // degree to radian
        double M = 357.52910 + 35999.05030*T - 0.0001559*T2 - 0.00000048*T*T2; // mean anomaly, degree
        double L0 = 280.46645 + 36000.76983*T + 0.0003032*T2; // mean longitude, degree
        double DL = (1.914600 - 0.004817*T - 0.000014*T2)*Math.sin(dr*M);
        DL = DL + (0.019993 - 0.000101*T)*Math.sin(dr*2*M) + 0.000290*Math.sin(dr*3*M);
        double L = L0 + DL; // true longitude, degree
        L = L - 360*(INT(L/360)); // Normalize to (0, 360)
        return L;
    }
    public static double NewMoon(int k) {
        //return CC2K.newMoonTime(k);
        return NewMoonAA98(k);
    }
    /**
     * Julian day number of the kth new moon after (or before) the New Moon of 1900-01-01 13:51 GMT.
     * Accuracy: 2 minutes
     * Algorithm from: Astronomical Algorithms, by Jean Meeus, 1998
     * @param k
     * @return the Julian date number (number of days since noon UTC on 1 January 4713 BC) of the New Moon
     */

    public static double NewMoonAA98(int k) {
        double T = k/1236.85; // Time in Julian centuries from 1900 January 0.5
        double T2 = T * T;
        double T3 = T2 * T;
        double dr = PI/180;
        double Jd1 = 2415020.75933 + 29.53058868*k + 0.0001178*T2 - 0.000000155*T3;
        Jd1 = Jd1 + 0.00033*Math.sin((166.56 + 132.87*T - 0.009173*T2)*dr); // Mean new moon
        double M = 359.2242 + 29.10535608*k - 0.0000333*T2 - 0.00000347*T3; // Sun's mean anomaly
        double Mpr = 306.0253 + 385.81691806*k + 0.0107306*T2 + 0.00001236*T3; // Moon's mean anomaly
        double F = 21.2964 + 390.67050646*k - 0.0016528*T2 - 0.00000239*T3; // Moon's argument of latitude
        double C1=(0.1734 - 0.000393*T)*Math.sin(M*dr) + 0.0021*Math.sin(2*dr*M);
        C1 = C1 - 0.4068*Math.sin(Mpr*dr) + 0.0161*Math.sin(dr*2*Mpr);
        C1 = C1 - 0.0004*Math.sin(dr*3*Mpr);
        C1 = C1 + 0.0104*Math.sin(dr*2*F) - 0.0051*Math.sin(dr*(M+Mpr));
        C1 = C1 - 0.0074*Math.sin(dr*(M-Mpr)) + 0.0004*Math.sin(dr*(2*F+M));
        C1 = C1 - 0.0004*Math.sin(dr*(2*F-M)) - 0.0006*Math.sin(dr*(2*F+Mpr));
        C1 = C1 + 0.0010*Math.sin(dr*(2*F-Mpr)) + 0.0005*Math.sin(dr*(2*Mpr+M));
        double deltat;
        if (T < -11) {
            deltat= 0.001 + 0.000839*T + 0.0002261*T2 - 0.00000845*T3 - 0.000000081*T*T3;
        } else {
            deltat= -0.000278 + 0.000265*T + 0.000262*T2;
        };
        double JdNew = Jd1 + C1 - deltat;
        return JdNew;
    }
    public static int INT(double d) {
        return (int)Math.floor(d);
    }
    public static double getSunLongitude(int dayNumber, double timeZone) {
        return SunLongitude(dayNumber - 0.5 - timeZone/24);
    }
    public static int getNewMoonDay(int k, double timeZone) {
        double jd = NewMoon(k);
        return INT(jd + 0.5 + timeZone/24);
    }
    public static int getLunarMonth11(int yy, double timeZone) throws Exception {
        DateObject date = new DateObject(31, 12, yy, 0);
        double off = jdFromDate(date) - 2415021.076998695;
        int k = INT(off / 29.530588853);
        int nm = getNewMoonDay(k, timeZone);
        int sunLong = INT(getSunLongitude(nm, timeZone)/30);
        if (sunLong >= 9) {
            nm = getNewMoonDay(k-1, timeZone);
        }
        return nm;
    }
    public static int getLeapMonthOffset(int a11, double timeZone) {
        int k = INT(0.5 + (a11 - 2415021.076998695) / 29.530588853);
        int last; // Month 11 contains point of sun longutide 3*PI/2 (December solstice)
        int i = 1; // We start with the month following lunar month 11
        int arc = INT(getSunLongitude(getNewMoonDay(k+i, timeZone), timeZone)/30);
        do {
            last = arc;
            i++;
            arc = INT(getSunLongitude(getNewMoonDay(k+i, timeZone), timeZone)/30);
        } while (arc != last && i < 14);
        return i-1;
    }

    public static DateObject convertSolar2Lunar(DateObject date, double timeZone) throws Exception {
        int dd = date.getDay();
        int mm = date.getMonth();
        int yy = date.getYear();
        int lunarDay, lunarMonth, lunarYear, lunarLeap;
        int dayNumber = jdFromDate(date);
        int k = INT((dayNumber - 2415021.076998695) / 29.530588853);
        int monthStart = getNewMoonDay(k+1, timeZone);
        if (monthStart > dayNumber) {
            monthStart = getNewMoonDay(k, timeZone);
        }
        int a11 = getLunarMonth11(yy, timeZone);
        int b11 = a11;
        if (a11 >= monthStart) {
            lunarYear = yy;
            a11 = getLunarMonth11(yy-1, timeZone);
        } else {
            lunarYear = yy+1;
            b11 = getLunarMonth11(yy+1, timeZone);
        }
        lunarDay = dayNumber-monthStart+1;
        int diff = INT((monthStart - a11)/29);
        lunarLeap = 0;
        lunarMonth = diff+11;
        if (b11 - a11 > 365) {
            int leapMonthDiff = getLeapMonthOffset(a11, timeZone);
            if (diff >= leapMonthDiff) {
                lunarMonth = diff + 10;
                if (diff == leapMonthDiff) {
                    lunarLeap = 1;
                }
            }
        }
        if (lunarMonth > 12) {
            lunarMonth = lunarMonth - 12;
        }
        if (lunarMonth >= 11 && diff < 4) {
            lunarYear -= 1;
        }
        DateObject result = new DateObject(lunarDay, lunarMonth, lunarYear, lunarLeap);
        result.setHourOfDay(date.getHourOfDay());
        result.setMinute(date.getMinute());
        return result;
    }
    public static DateObject convertLunar2Solar(DateObject date, double timeZone) throws Exception {
        int a11, b11;
        int lunarDay = date.getDay();
        int lunarMonth = date.getMonth();
        int lunarYear = date.getYear();
        int lunarLeap = date.getLeap();
        if (lunarMonth < 11) {
            a11 = getLunarMonth11(lunarYear-1, timeZone);
            b11 = getLunarMonth11(lunarYear, timeZone);
        } else {
            a11 = getLunarMonth11(lunarYear, timeZone);
            b11 = getLunarMonth11(lunarYear+1, timeZone);
        }
        int k = INT(0.5 + (a11 - 2415021.076998695) / 29.530588853);
        int off = lunarMonth - 11;
        if (off < 0) {
            off += 12;
        }
        if (b11 - a11 > 365) {
            int leapOff = getLeapMonthOffset(a11, timeZone);
            int leapMonth = leapOff - 2;
            if (leapMonth < 0) {
                leapMonth += 12;
            }
            if (lunarLeap != 0 && lunarMonth != leapMonth) {
                System.out.println("Invalid input!");
                return new DateObject(1, 1, 1, 0);
            } else if (lunarLeap != 0 || off >= leapOff) {
                off += 1;
            }
        }
        int monthStart = getNewMoonDay(k+off, timeZone);
        DateObject result = jdToDate(monthStart+lunarDay-1);
        result.setHourOfDay(date.getHourOfDay());
        result.setMinute(date.getMinute());
        return result;
    }
}