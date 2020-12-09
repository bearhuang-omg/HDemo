package com.android.hutils;

import java.util.Calendar;

public class FastTimeFormat {

    int year;
    int month;
    int day;
    int hour = 0;
    int min = 0;
    int sec = 0;
    int ms = 0;
    private long lastTime;
    private long nextDay;
    private long lastDay;
    private int add;

    public Time cal(long time) {
        if (lastTime == 0 || time >= nextDay || time <= lastDay) {
            calFromTime(time);
        } else {
            calHms((int) (time - lastTime));
            lastTime = time;
        }
        return new Time(year, month, day, hour, min, sec, ms);
    }

    private void calFromTime(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        lastTime = time;
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);
        sec = c.get(Calendar.SECOND);
        ms = c.get(Calendar.MILLISECOND);

        c.set(year, month, day, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        lastDay = c.getTimeInMillis();
        c.add(Calendar.DAY_OF_MONTH, 1);

        nextDay = c.getTimeInMillis();
    }

    private void calHms(int inc) {
        ms = this.shift(inc, ms, 1000);
        if (add != 0) {
            sec = shift(add, sec, 60);
            if (add != 0) {
                min = shift(add, min, 60);
                if (add != 0) {
                    hour = shift(add, hour, 60);
                }
            }
        }
    }

    private int shift(int inc, int now, int scale) {
        now += inc;
        add = 0;
        if (now >= scale) {
            add = now / scale;
            now -= (add * scale);
        } else if (now < 0) {
            add = -now / scale;
            now += ((add + 1) * scale);
            if (now == scale) {
                now = 0;
            } else {
                add++;
            }
            add = -add;
        }
        return now;
    }

    public class Time {

        public int year;
        public int month;
        public int day;
        public int hour = 0;
        public int min = 0;
        public int sec = 0;
        public int ms = 0;

        public Time(int year, int month, int day, int hour, int min, int sec, int ms) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.min = min;
            this.sec = sec;
            this.ms = ms;
        }

        /**
         * 返回时间字符串，yyyy-MM-dd HH:mm:ss.SSS
         *
         * @return
         */
        public String toTime() {
            StringBuilder sb = new StringBuilder();
            sb.append(year).append("-");
            if (month < 9) {
                sb.append(0);
            }
            sb.append(month + 1).append("-");

            if (day < 10) {
                sb.append(0);
            }
            sb.append(day).append(" ");

            if (hour < 10) {
                sb.append(0);
            }
            sb.append(hour).append(":");
            if (min < 10) {
                sb.append(0);
            }
            sb.append(min).append(":");
            if (sec < 10) {
                sb.append(0);
            }
            sb.append(sec).append(".");
            if (ms < 10) {
                sb.append("00");
            } else if (ms < 100) {
                sb.append(0);
            }
            sb.append(ms);
            return sb.toString();
        }

        public String toDate() {
            StringBuilder sb = new StringBuilder();
            sb.append(year);
            if (month < 9) {
                sb.append(0);
            }
            sb.append(month + 1);
            if (day < 10) {
                sb.append(0);
            }
            sb.append(day);
            return sb.toString();
        }
    }
}

