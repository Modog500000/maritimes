package xyz.volcanobay.maritimes.systems.city;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import xyz.volcanobay.maritimes.systems.DateSystem;
import xyz.volcanobay.maritimes.systems.MaterialSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CitySystem {
    public static HashMap<String,City> cities = new HashMap<>();

    public static City MOMBASA = addCity(City.CityConstructor.construct("mombasa",new Vector2(61,27))
        // Connections
        .addSeasonalConnection("calicut", DateSystem.Season.FALL)
        .addSeasonalConnection("calicut", DateSystem.Season.WINTER)
        .addSeasonalConnection("surat", DateSystem.Season.FALL)
        .addSeasonalConnection("surat", DateSystem.Season.WINTER)
        .build());

    public static City CALICUT = addCity(City.CityConstructor.construct("calicut",new Vector2(69,31))
        // Connections
        .addSeasonalConnection("mombasa", DateSystem.Season.SPRING)
        .addSeasonalConnection("mombasa", DateSystem.Season.SUMMER)
        .addSeasonalConnection("surat", DateSystem.Season.SPRING)
        .addSeasonalConnection("surat", DateSystem.Season.SUMMER)

        // Trades
        .addTrade(MaterialSystem.GOLD,250)
        .addTrade(MaterialSystem.SILVER,100)
        .addTrade(MaterialSystem.FOOD,4)
        .addTrade(MaterialSystem.SALT,1)
        .addTrade(MaterialSystem.PEPPER,10)
        .addTrade(MaterialSystem.CINNAMON, 8)
        .build());



    public static City SURAT = addCity(City.CityConstructor.construct("surat",new Vector2(68,34))
        // Connections
        .addSeasonalConnection("calicut", DateSystem.Season.FALL)
        .addSeasonalConnection("calicut", DateSystem.Season.WINTER)
        .addSeasonalConnection("mombasa", DateSystem.Season.FALL)
        .addSeasonalConnection("mombasa", DateSystem.Season.WINTER)
        .build());

    public static void register() {
        for (City city : cities.values()) {
            city.createConnections();
        }
    }

    public static City addCity(City city) {
        cities.put(city.getName(),city);
        return city;
    }
}
