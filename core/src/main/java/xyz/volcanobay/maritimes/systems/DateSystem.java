package xyz.volcanobay.maritimes.systems;

public class DateSystem {
    public static Season season = Season.WINTER;
    private static float timeUntilSeason = 0;

    public static void tick() {
        timeUntilSeason -= 0.1f;
        if (timeUntilSeason <= 0) {
            if (season.ordinal() < 3) {
                season = Season.values()[season.ordinal() + 1];
            } else {
                season = Season.WINTER;
            }
            timeUntilSeason = 31;
        }
    }

    public static float getTimeUntilSeason() {
        return timeUntilSeason;
    }

    public enum Season {
        WINTER,
        SPRING,
        SUMMER,
        FALL
    }
}
