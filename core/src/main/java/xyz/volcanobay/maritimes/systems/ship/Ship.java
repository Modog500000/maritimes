package xyz.volcanobay.maritimes.systems.ship;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import xyz.volcanobay.maritimes.Maritimes;
import xyz.volcanobay.maritimes.core.RenderSystem;
import xyz.volcanobay.maritimes.menus.BoatMenu;
import xyz.volcanobay.maritimes.systems.MaterialSystem;
import xyz.volcanobay.maritimes.systems.ShipSystem;
import xyz.volcanobay.maritimes.systems.city.City;
import xyz.volcanobay.maritimes.systems.city.CitySystem;
import xyz.volcanobay.maritimes.systems.input.Bounds;
import xyz.volcanobay.maritimes.systems.input.Clickable;
import xyz.volcanobay.maritimes.systems.material.Material;
import xyz.volcanobay.maritimes.systems.material.Supplies;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public abstract class Ship implements Clickable {
    private String name;
    private Texture texture;
    private City lastCity = RenderSystem.viewCity;
    private List<City> orders = new ArrayList<>();
    private List<City> returnOrders = new ArrayList<>();
    private List<City> usedOrders = new ArrayList<>();
    private List<City> usedReturnOrders = new ArrayList<>();
    private float satisfaction = 200f;

    private boolean barterFreely = false;

    private List<Supplies> suppliesList = new ArrayList<>();

    private City targetCity = RenderSystem.viewCity;

    private Vector2 position = new Vector2(63, 26);
    private Vector2 velocity = new Vector2();
    private boolean continueOrders = true;
    private float tradeTimer = 0;
    private Random random = new Random();
    private int foodUsed = 0;
    private float enterTimer = 0;
    private float exitTimer = 0;
    private float viewX = 0;
    private float viewY = 0;
    private boolean dockedAtHome = false;
    private boolean waiting = false;


    public Ship(String name) {
        this.name = name;
        this.texture = RenderSystem.getTexture("assets/textures/ship/" + name + ".png");
        orders.add(RenderSystem.viewCity);

        // for now just retrieve all materials as supplies
        for (Material material : MaterialSystem.materialList) {
            suppliesList.add(new Supplies(material));
        }
    }

    public void draw(SpriteBatch batch, float x, float y, float scale, boolean forceLeft, boolean forceRight) {
        viewX = x;
        viewY = y;
        if ((velocity.x < 0 || forceLeft) && !forceRight) {
            batch.draw(texture, x + scale, y, -scale, scale * ((float) texture.getHeight() / texture.getWidth()));
        } else {
            batch.draw(texture, x, y, scale, scale * ((float) texture.getHeight() / texture.getWidth()));
        }
    }

    public void tick() {
        if (waiting) {
            return;
        }
        velocity.scl(0.9f);
        position.sub(new Vector2(velocity).scl(0.4f));
        if (enterTimer > 0) {
            enterTimer--;
            return;
        }
        if (exitTimer > 0) {
            exitTimer--;
            return;
        }
        if (satisfaction < 70f) {
            for (Supplies supplies : suppliesList) {
                if (supplies.equals(MaterialSystem.FOOD)) {
                    if (supplies.getCount() > 0) {
                        supplies.add(-1);
                        satisfaction += 100;
                        foodUsed++;
                        break;
                    }
                }
            }
        }
        if (tradeTimer > 0) {
            attemptTrade(lastCity);
        }

        if (targetCity != null) {
            Vector2 targetPos = targetCity.getPosition();

            if (position.dst(targetPos) > 0.1f && velocity.len() < getMaxSpeed()) {
                Vector2 accel = new Vector2(position.x - targetPos.x, position.y - targetPos.y);
                if (accel.len2() > 1)
                    accel.nor();
                accel.scl(getAcceleration());
                accel.scl(Math.min(1, 0.05f / velocity.len()));
                if (satisfaction < 0) {
                    velocity.add(accel.scl(0.01f));
                } else {
                    velocity.add(accel);
                    satisfaction -= .25f;
                }
            } else {
                if (targetCity == RenderSystem.viewCity && orders.isEmpty()) {
                    if (ShipSystem.shipPresent) {
                        queue();
                    } else {
                        enter();
                    }
                }
                if (targetCity != RenderSystem.viewCity) {
                    tradeTimer = 10;
                }
                lastCity = targetCity;
                targetCity = null;
            }
        } else if (continueOrders && tradeTimer <= 0) {
            if (!orders.isEmpty()) {
                boolean impossible = true;
                for (City.Connection connection : lastCity.getConnections()) {
                    if (connection.canTraverse(orders.getFirst())) {
                        targetCity = orders.getFirst();
                        usedOrders.add(targetCity);
                        orders.remove(0);
                        impossible = false;
                        break;
                    } else if (connection.possibleToTraverse(orders.getFirst())) {
                        impossible = false;
                    }
                }
                if (impossible) {
                    targetCity = lastCity.getConnections().get((int) Math.floor(Math.abs(Math.random() * (lastCity.getConnections().size() - 0.1f)))).city;
                }
            } else if (!returnOrders.isEmpty()) {
                orders = new ArrayList<>(returnOrders);
                usedReturnOrders = new ArrayList<>(returnOrders);
                returnOrders.clear();
            } else {
                targetCity = lastCity.getConnections().get((int) Math.floor(Math.abs(Math.random() * (lastCity.getConnections().size() - 0.1f)))).city;
            }
        }
    }

    public void attemptTrade(City city) {
        if (Math.random() > 0.8) {
            tradeTimer += 1.5f;
        }
        List<Supplies> buyable = new ArrayList<>();
        List<Supplies> sellable = new ArrayList<>();

        for (City.TradingInstructions instructions : lastCity.tradingInstructions) { // Collect All Possible Trades
            Supplies supplies = getSupplies(instructions.material);
            if (supplies != null) {
                if (supplies.buy && instructions.buy) {
                    buyable.add(supplies);
                }
                if (supplies.sell && instructions.sell && supplies.getCount() > 0) {
                    sellable.add(supplies);
                }
            }
        }

        // Sort based on value
        buyable.sort((o1, o2) -> {
            float value1 = 0;
            float value2 = 0;
            for (City.TradingInstructions instructions : lastCity.tradingInstructions) {
                if (instructions.material.equals(o1.getMaterial())) {
                    value1 = instructions.value;
                    o1.setCalculatedValue(value1);
                }
                if (instructions.material.equals(o2.getMaterial())) {
                    value2 = instructions.value;
                    o2.setCalculatedValue(value2);
                }
                if (value1 != 0 && value2 != 0) {
                    break;
                }
            }
            return Float.compare(value1, value2);
        });
        if (buyable.size() == 1) {
            for (City.TradingInstructions instructions : lastCity.tradingInstructions) {
                if (instructions.material.equals(buyable.getFirst().getMaterial())) {
                    buyable.getFirst().setCalculatedValue(instructions.value);
                }
            }
        }
        sellable.sort((o1, o2) -> {
            float value1 = 0;
            float value2 = 0;
            for (City.TradingInstructions instructions : lastCity.tradingInstructions) {
                if (instructions.material.equals(o1.getMaterial())) {
                    value1 = instructions.value * o1.getCount();
                    o1.setCalculatedValue(value1);
                }
                if (instructions.material.equals(o2.getMaterial())) {
                    value2 = instructions.value * o2.getCount();
                    o2.setCalculatedValue(value2);
                }
            }
            return Float.compare(value1, value2);
        });
        if (sellable.size() == 1) {
            for (City.TradingInstructions instructions : lastCity.tradingInstructions) {
                if (instructions.material.equals(sellable.getFirst().getMaterial())) {
                    sellable.getFirst().setCalculatedValue(instructions.value);
                }
            }
        }

        float sellableValue = 0;
        for (Supplies supplies : sellable) {
            sellableValue += supplies.calculatedValue;
        }
        if (!buyable.isEmpty() && !sellable.isEmpty()) {
            Supplies randomBuyable = buyable.get(random.nextInt(buyable.size()));
            Supplies selling = sellable.getLast();

            float costDifference = (selling.getCalculatedValue() * selling.getCount()) / randomBuyable.getCalculatedValue();
            float buyAmount = (float) Math.ceil(Math.max(1, costDifference));
            float sellAmount = (randomBuyable.getCalculatedValue() * buyAmount) / (selling.getCalculatedValue());

            if (sellAmount <= selling.getCount()) {

                float cancellation = getStorageSpace() / (getWeight() - (selling.getMaterial().getWeight() * sellAmount) + randomBuyable.getMaterial().getWeight() * buyAmount);
                buyAmount *= Math.min(1, cancellation);
                if (barterFreely) {
                    completeTrade(selling, (int) sellAmount, randomBuyable, (int) buyAmount);
                } else if (cancellation >= 0) {
                    completeTrade(selling, (int) sellAmount, randomBuyable, (int) buyAmount);
                }
            }
        }

        tradeTimer--;
    }

    public void setViewX(float viewX) {
        this.viewX = viewX;
    }

    public void setViewY(float viewY) {
        this.viewY = viewY;
    }

    public void enter() {
        continueOrders = false;
        enterTimer = 160f;
        satisfaction = 150f;
        dockedAtHome = true;
        ShipSystem.shipPresent = true;
    }

    public void queue() {
        ShipSystem.shipsWaiting.add(this);
        waiting = true;
    }

    private void completeTrade(Supplies sell, int sellAmount, Supplies buy, int buyAmount) {
        for (Supplies supplies : suppliesList) {
            if (sell.equals(supplies)) {
                supplies.add(-sellAmount);
            }
            if (buy.equals(supplies)) {
                buy.add(buyAmount);
            }
        }
    }

    public float getEnterTimer() {
        return enterTimer;
    }

    public float getExitTimer() {
        return exitTimer;
    }

    public Supplies getSupplies(Material material) {
        return MaterialSystem.findMatchingSupplies(suppliesList, material);
    }

    public void setTargetCity(City targetCity) {
        this.targetCity = targetCity;
    }

    public String getName() {
        return name;
    }

    public City getTargetCity() {
        return targetCity;
    }

    public City getLastCity() {
        return lastCity;
    }

    public Vector2 getPosition() {
        return position;
    }

    public boolean isDockedAtHome() {
        return dockedAtHome;
    }

    public Vector2 getPosition(float partialTick) {
        return (new Vector2(position)).lerp(new Vector2(position).add(velocity), partialTick);
    }

    public void setSail() {
        continueOrders = true;
        dockedAtHome = false;
        ShipSystem.shipPresent = false;
        this.exitTimer = 160f;
    }

    public void setExitTimer(float exitTimer) {
        this.exitTimer = exitTimer;
    }

    public void setContinueOrders(boolean continueOrders) {
        this.continueOrders = continueOrders;
    }

    public void resetFoodUsed() {
        foodUsed = 0;
    }

    public int getFoodUsed() {
        return foodUsed;
    }

    public abstract float getMaxSpeed();

    public abstract float getAcceleration();

    public abstract float getStorageSpace();

    public List<City> getOrders() {
        return orders;
    }

    public List<City> getReturnOrders() {
        return returnOrders;
    }

    public List<City> getUsedOrders() {
        return usedOrders;
    }

    public List<Supplies> getSuppliesList() {
        return suppliesList;
    }

    public boolean isBarterFreely() {
        return barterFreely;
    }

    public void setBarterFreely(boolean barterFreely) {
        this.barterFreely = barterFreely;
    }

    public float getWeight() {
        float weight = 0;
        for (Supplies supplies : suppliesList) {
            weight += supplies.getMaterial().getWeight() * supplies.getCount();
        }
        return weight;
    }

    @Override
    public Bounds getBounds() {
        if (RenderSystem.mapScreen) {
            return new Bounds(position.x, (position.x) + 10, position.y, (position.y) + 6);
        } else {
            return new Bounds(viewX, (viewX) + 10, viewY, viewY + 6);

        }
    }


    @Override
    public void leftClick() {
    }

    @Override
    public void rightClick() {
        Maritimes.INSTANCE.setScreen(new BoatMenu(this));
    }
}
