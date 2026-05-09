package com.travlog.travlog.report;

public record MonthlyTripStat(int year, int month, long count, int barWidth) {

    public String label() {
        return year + "년 " + month + "월";
    }
}
