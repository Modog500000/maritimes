package xyz.volcanobay.maritimes.systems.city;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import xyz.volcanobay.maritimes.systems.DateSystem;
import xyz.volcanobay.maritimes.systems.material.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class City {
    private String name;
    private Vector2 position;
    private List<Connection> connections = new ArrayList<>();
    private List<Supplier<Connection>> futureConnections = new ArrayList<>();
    public boolean hidden = false;
    public List<TradingInstructions> tradingInstructions = new ArrayList<>();

    private City(String name, Vector2 position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    private void setPosition(Vector2 position) {
        this.position = position;
    }

    public Vector2 getPosition() {
        return position;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void createConnections() {
        for (Supplier<Connection> connectionSupplier : futureConnections) {
            connections.add(connectionSupplier.get());
        }
    }

    public static class Connection {
        public DateSystem.Season season = null;
        public City self;
        public City city;

        public Connection(City self,String city) {
            this.self = self;
            this.city = CitySystem.cities.get(city);
        }

        public Connection setSeason(DateSystem.Season season) {
            this.season = season;
            return this;
        }

        public boolean possibleToTraverse(City city) {
            if (city == self) return true;
            return city == this.city;
        }

        public boolean canTraverse(City city) {
            if (city == self) return true;
            if (city != this.city) return false;
            if (season == null) {
                return true;
            } else {
                return season == DateSystem.season;
            }
        }
    }

    public static class CityConstructor {
        private City city;

        private CityConstructor(City city) {
            this.city = city;
        }

        public static CityConstructor construct(String cityName, Vector2 position) {
            return new CityConstructor(new City(cityName, position));
        }

        public CityConstructor addSeasonalConnection(String city, DateSystem.Season season) {
            this.city.futureConnections.add(() -> new Connection(this.city,city).setSeason(season));
            return this;
        }

        public CityConstructor addConnection(String city) {
            this.city.futureConnections.add(() -> new Connection(this.city,city));
            return this;
        }

        public CityConstructor addTrade(Material material, float value) {
            city.tradingInstructions.add(new TradingInstructions(material,value,true,true));
            return this;
        }

        public CityConstructor addTrade(Material material, float value, boolean buy, boolean sell) {
            city.tradingInstructions.add(new TradingInstructions(material,value,buy,sell));
            return this;
        }

        public CityConstructor addBuyTrade(Material material, float value) {
            city.tradingInstructions.add(new TradingInstructions(material,value,true,false));
            return this;
        }

        public CityConstructor addSellTrade(Material material, float value) {
            city.tradingInstructions.add(new TradingInstructions(material,value,false,true));
            return this;
        }

        public City build() {
            return city;
        }
    }

    public static class TradingInstructions {
        public Material material;
        public boolean buy;
        public boolean sell;
        public float value;

        public TradingInstructions(Material material, float value, boolean buy, boolean sell) {
            this.material = material;
            this.value = value;
            this.buy = buy;
            this.sell = sell;
        }
    }

    @Override
    public String toString() {
        return getName();
    }
}
