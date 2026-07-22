package com.example.hyunjiinserver.core.global.geo;

public final class GeoDistance {

    private static final double EARTH_RADIUS_METERS = 6_371_000;

    private GeoDistance() {
    }

    public static int distanceMeters(double fromLatitude, double fromLongitude, double toLatitude, double toLongitude) {
        double fromLatRad = Math.toRadians(fromLatitude);
        double toLatRad = Math.toRadians(toLatitude);
        double deltaLatRad = Math.toRadians(toLatitude - fromLatitude);
        double deltaLngRad = Math.toRadians(toLongitude - fromLongitude);

        double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2)
                + Math.cos(fromLatRad) * Math.cos(toLatRad)
                * Math.sin(deltaLngRad / 2) * Math.sin(deltaLngRad / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) Math.round(EARTH_RADIUS_METERS * c);
    }
}
