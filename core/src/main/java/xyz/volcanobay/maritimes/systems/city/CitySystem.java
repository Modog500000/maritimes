package xyz.volcanobay.maritimes.systems.city;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import xyz.volcanobay.maritimes.systems.DateSystem;
import xyz.volcanobay.maritimes.systems.MaterialSystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class CitySystem {
    public static HashMap<String, City> cities = new HashMap<>();

    public static City MOMBASA = addCity(City.CityConstructor.construct("mombasa", new Vector2(61, 27))
        // Connections
        .addSeasonalConnection("calicut", DateSystem.Season.FALL)
        .addSeasonalConnection("calicut", DateSystem.Season.WINTER)
        .addSeasonalConnection("surat", DateSystem.Season.FALL)
        .addSeasonalConnection("surat", DateSystem.Season.WINTER)
        .build());

    public static City CALICUT = addCity(City.CityConstructor.construct("calicut", new Vector2(69, 31))
        // Connections
        .addSeasonalConnection("mombasa", DateSystem.Season.SPRING)
        .addSeasonalConnection("mombasa", DateSystem.Season.SUMMER)
        .addSeasonalConnection("surat", DateSystem.Season.SPRING)
        .addSeasonalConnection("surat", DateSystem.Season.SUMMER)
            .addConnection("colombo")

        // Trades
        .addTrade(MaterialSystem.GOLD, 300)
        .addTrade(MaterialSystem.SILVER, 100)
        .addTrade(MaterialSystem.FOOD, 1)
        .addTrade(MaterialSystem.SALT, 4)
        .addTrade(MaterialSystem.PEPPER, 80)
        .addTrade(MaterialSystem.CINNAMON, 50)
        .addTrade(MaterialSystem.PORCELAIN, 150)
        .build());


    public static City SURAT = addCity(City.CityConstructor.construct("surat", new Vector2(68, 34))
        // Connections
        .addSeasonalConnection("calicut", DateSystem.Season.FALL)
        .addSeasonalConnection("calicut", DateSystem.Season.WINTER)
        .addSeasonalConnection("mombasa", DateSystem.Season.FALL)
        .addSeasonalConnection("mombasa", DateSystem.Season.WINTER)
        // Trades
        .addTrade(MaterialSystem.GOLD, 250)
        .addTrade(MaterialSystem.SILVER, 150)
        .addTrade(MaterialSystem.FOOD, 1)
        .addTrade(MaterialSystem.SALT, 4)
        .addTrade(MaterialSystem.PEPPER, 85)
        .addTrade(MaterialSystem.CINNAMON, 50)
        .addTrade(MaterialSystem.PORCELAIN, 160)
        .build());

    public static City BENCOOLEN = addCity(City.CityConstructor.construct("bencoolen", new Vector2(79, 24))
        // Connections
        .addConnection("macao")
        .addSeasonalConnection("colombo", DateSystem.Season.SPRING)
        .addSeasonalConnection("colombo", DateSystem.Season.SUMMER)
        .addSeasonalConnection("petapoli", DateSystem.Season.SPRING)
        .addSeasonalConnection("petapoli", DateSystem.Season.SUMMER)
        // Trades
        .addTrade(MaterialSystem.GOLD, 350)
        .addTrade(MaterialSystem.SILVER, 120)
        .addTrade(MaterialSystem.FOOD, 1)
        .addTrade(MaterialSystem.SALT, 2)
        .addTrade(MaterialSystem.PEPPER, 50)
        .addTrade(MaterialSystem.CINNAMON, 10)
        .addTrade(MaterialSystem.PORCELAIN, 130)
        .build());

    public static City MACAO = addCity(City.CityConstructor.construct("macao", new Vector2(79.5f, 34.5f))
        // Connections
        .addSeasonalConnection("bencoolen", DateSystem.Season.FALL)
        .addSeasonalConnection("bencoolen", DateSystem.Season.WINTER)
        // Trades
        .addTrade(MaterialSystem.GOLD, 280)
        .addTrade(MaterialSystem.SILVER, 80)
        .addTrade(MaterialSystem.FOOD, 2)
        .addTrade(MaterialSystem.SALT, 3)
        .addTrade(MaterialSystem.PEPPER, 110)
        .addTrade(MaterialSystem.CINNAMON, 40)
        .addTrade(MaterialSystem.PORCELAIN, 50)
        .build());
    public static City COLOMBO = addCity(City.CityConstructor.construct("colombo", new Vector2(72, 28))
        .addConnection("petapoli")
        .addConnection("calicut")
        .addSeasonalConnection("bencoolen", DateSystem.Season.FALL)
        .addSeasonalConnection("bencoolen", DateSystem.Season.WINTER)
        // Trades
        .addTrade(MaterialSystem.GOLD, 300)
        .addTrade(MaterialSystem.SILVER, 200)
        .addTrade(MaterialSystem.FOOD, 8)
        .addTrade(MaterialSystem.SALT, 1)
        .addTrade(MaterialSystem.PEPPER, 40)
        .addTrade(MaterialSystem.CINNAMON, 25)
        .addTrade(MaterialSystem.PORCELAIN, 145)
        // Connections
        .build());

    public static City PETAPOLI = addCity(City.CityConstructor.construct("petapoli", new Vector2(72, 32))
        // Connections
        .addConnection("colombo")
        .addSeasonalConnection("bencoolen", DateSystem.Season.FALL)
        .addSeasonalConnection("bencoolen", DateSystem.Season.WINTER)
        // Trades
        .addTrade(MaterialSystem.GOLD, 400)
        .addTrade(MaterialSystem.SILVER, 40)
        .addTrade(MaterialSystem.FOOD, 1)
        .addTrade(MaterialSystem.SALT, 4)
        .addTrade(MaterialSystem.PEPPER, 90)
        .addTrade(MaterialSystem.CINNAMON, 20)
        .addTrade(MaterialSystem.PORCELAIN, 140)
        .build());

    public static void register() {
        for (City city : cities.values()) {
            city.createConnections();
            city.hidden = true;
        }
        MOMBASA.hidden = false;
        CALICUT.hidden = false;
    }

    public static Collection<City> getCities() {
        return cities.values();
    }

    public static City addCity(City city) {
        cities.put(city.getName(), city);
        return city;
    }
}
