package xyz.volcanobay.maritimes.menus;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import xyz.volcanobay.maritimes.Maritimes;
import xyz.volcanobay.maritimes.core.RenderSystem;
import xyz.volcanobay.maritimes.systems.InputSystem;
import xyz.volcanobay.maritimes.systems.TradingSystem;
import xyz.volcanobay.maritimes.systems.city.City;
import xyz.volcanobay.maritimes.systems.city.CitySystem;
import xyz.volcanobay.maritimes.systems.input.Bounds;
import xyz.volcanobay.maritimes.systems.input.Button;
import xyz.volcanobay.maritimes.systems.input.Clickable;
import xyz.volcanobay.maritimes.systems.material.Supplies;
import xyz.volcanobay.maritimes.systems.ship.Ship;
import xyz.volcanobay.maritimes.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class BoatMenu implements Screen, Clickable {

    private Ship ship;
    private List<Button> boundsList = new ArrayList<>();
    private Page page = Page.TRIP;

    public static Texture background = new Texture("assets/textures/screen/boat_background.png");
    public static Texture selector = new Texture("assets/textures/screen/selector.png");

    public static Texture trade = new Texture("assets/textures/screen/trade.png");
    public static Texture no_trade = new Texture("assets/textures/screen/no_trade.png");

    public BoatMenu(Ship ship) {
        this.ship = ship;
    }

    @Override
    public void show() {
        // Prepare your screen here.
    }

    @Override
    public void render(float delta) {
//        RenderSystem.drawBounds(getBounds());
        RenderSystem.batch.draw(background, 8, 2, 14, 14 * ((float) background.getHeight() / background.getWidth()));
        float y = 190;
        boundsList.clear();

        boundsList.add(new Button(9, 12, 12.5f, 13.1f));
        boundsList.getLast().setAnyAction(() -> {
            page = Page.TRIP;
        });

        boundsList.add(new Button(12, 15, 12.5f, 13.1f));
        boundsList.getLast().setAnyAction(() -> {
            page = Page.RETURN;
        });

        boundsList.add(new Button(15, 18, 12.5f, 13.1f));
        boundsList.getLast().setAnyAction(() -> {
            page = Page.STOCK;
        });

        boundsList.add(new Button(18, 21, 12.5f, 13.1f));
        boundsList.getLast().setAnyAction(() -> {
            ship.setSail();
            Maritimes.INSTANCE.setScreen(null);
        });

        RenderSystem.drawText("trip     return  stock", -200f, 220);
        RenderSystem.drawText("Sail!", 160f, 220, new Color(0.4f, 1, 0.4f, 1));

        if (page == Page.TRIP) {
            renderTrip();
        }
        if (page == Page.RETURN) {
            renderReturnTrip();
        }
        if (page == Page.STOCK) {
            renderStock();
        }

        RenderSystem.batch.draw(selector, 10 + (page.ordinal() * 2.95f), (float) (13.4f + (Math.sin(Maritimes.time / 10f) / 5f)), 1, 1 * ((float) selector.getHeight() / selector.getWidth()));
        if (Maritimes.debug) {
            for (Bounds bounds : boundsList) {
                RenderSystem.drawBounds(bounds);
            }
        }
        // Draw your screen here. "delta" is the time since last render in seconds.
    }

    private void renderTrip() {
        float y = 190;
        for (City city : CitySystem.cities.values()) {
            if (city.hidden)
                continue;
            float mouseY = Mth.map(y, -178, 190, 3.3f, 12.4f);
            float bottom = Mth.map(y - 30f, -178, 190, 3.3f, 12.4f);
            boundsList.add(new Button(8, 22, bottom, mouseY));
            boundsList.getLast().setLeftAction(() -> {
                if (ship.getOrders().contains(city)) {
                    ship.getOrders().remove(city);
                } else {
                    ship.getOrders().add(city);
                }
            });
            boundsList.getLast().setRightAction(() -> {
                if (!ship.getTradeBlock().contains(city)) {
                    ship.getTradeBlock().add(city);
                } else {
                    ship.getTradeBlock().remove(city);
                }
            });
            Color color = new Color(65 / 255f, 30 / 255f, 5 / 255f, 255 / 255f);
            String prefix = "X";
            if (ship.getOrders().contains(city)) {
                ship.setContinueOrders(false);
                int index = ship.getOrders().indexOf(city);
                prefix = String.valueOf(index + 1);
                if (index == 0 && !ship.getOrders().isEmpty()) {
                    City nextOrder = ship.getOrders().getFirst();
                    boolean foundConnection = false;
                    for (City.Connection connection : RenderSystem.viewCity.getConnections()) {
                        if (connection.possibleToTraverse(nextOrder)) {
                            foundConnection = true;
                            break;
                        }
                    }
                    if (!foundConnection) {
                        color = Color.RED;
                    }
                }
                if (ship.getOrders().size() > index + 1) {
                    City nextOrder = ship.getOrders().get(index + 1);
                    boolean foundConnection = false;
                    for (City.Connection connection : city.getConnections()) {
                        if (connection.possibleToTraverse(nextOrder)) {
                            foundConnection = true;
                            break;
                        }
                    }
                    if (!foundConnection) {
                        color = Color.RED;
                    }
                }
            }

            if (ship.getTradeBlock().contains(city)) {
                RenderSystem.batch.draw(no_trade, 20, bottom - 0.2f, 0.8f, 0.8f);
            } else {
                RenderSystem.batch.draw(trade, 20, bottom - 0.2f, 0.8f, 0.8f);
            }

            RenderSystem.drawText(prefix + " " + city.getName(), -250f, y, color);
            y -= 25;
        }

    }

    private void renderReturnTrip() {
        float y = 190;
        if (!ship.getOrders().isEmpty()) {
            if (!ship.getReturnOrders().isEmpty()) {
                ship.getReturnOrders().set(0, ship.getOrders().getLast());
            } else {
                ship.getReturnOrders().add(ship.getOrders().getLast());
            }
        }
        for (City city : CitySystem.cities.values()) {
            if (city.hidden)
                continue;
            float mouseY = Mth.map(y, -178, 190, 3.3f, 12.4f);
            float bottom = Mth.map(y - 30f, -178, 190, 3.3f, 12.4f);
            boundsList.add(new Button(8, 22, bottom, mouseY));
            boundsList.getLast().setLeftAction(() -> {
                if (ship.getReturnOrders().contains(city)) {
                    ship.getReturnOrders().remove(city);
                } else {
                    ship.getReturnOrders().add(city);
                }
            });
            boundsList.getLast().setRightAction(() -> {
                if (!ship.getTradeBlock().contains(city)) {
                    ship.getTradeBlock().add(city);
                } else {
                    ship.getTradeBlock().remove(city);
                }
            });

            Color color = new Color(65 / 255f, 30 / 255f, 5 / 255f, 255 / 255f);
            String prefix = "X";
            if (ship.getReturnOrders().contains(city)) {
                int index = ship.getReturnOrders().indexOf(city);
                prefix = String.valueOf(index + 1);
                if (ship.getReturnOrders().size() > index + 1) {
                    City nextOrder = ship.getReturnOrders().get(index + 1);
                    boolean foundConnection = false;
                    for (City.Connection connection : city.getConnections()) {
                        if (connection.possibleToTraverse(nextOrder)) {
                            foundConnection = true;
                            break;
                        }
                    }
                    if (!foundConnection) {
                        color = Color.RED;
                    }
                }
            }
            if (ship.getTradeBlock().contains(city)) {
                RenderSystem.batch.draw(no_trade, 20, bottom - 0.2f, 0.8f, 0.8f);
            } else {
                RenderSystem.batch.draw(trade, 20, bottom - 0.2f, 0.8f, 0.8f);
            }
            RenderSystem.drawText(prefix + " " + city.getName(), -250f, y, color);
            y -= 25;
        }
    }

    public void renderStock() {
        float y = 11.3f;
        for (Supplies supplies : ship.getSuppliesList()) {
            float textY = Mth.map(y + 0.9f, 3.3f, 12.4f, -178, 190);

            boundsList.add(new Button(8.5f, 9.5f, y, y + 1));
            boundsList.getLast().setLeftAction(() -> {
                if (supplies.getCount() > 0) {
                    if (TradingSystem.getHeldMaterial() == null) {
                        supplies.add(-1);
                        TradingSystem.setHeldMaterial(supplies.getMaterial());
                    }
                }
            });
            boundsList.getLast().setRightAction(() -> {
                if (supplies.getCount() > 0) {
                    if (TradingSystem.getHeldMaterial() == null) {
                        TradingSystem.sellMaterial(supplies.getMaterial(), supplies.getCount());
                        supplies.add(-supplies.getCount());
                    }
                }
            });
            boundsList.add(new Button(9.5f, 13, y, y + 1));
            boundsList.getLast().setAnyAction(() -> {
                supplies.buy = !supplies.buy;
            });
            boundsList.add(new Button(13, 18, y, y + 1));
            boundsList.getLast().setAnyAction(() -> {
                supplies.sell = !supplies.sell;
            });
            RenderSystem.drawText("[" + (supplies.buy ? "Y" : "N") + "] BUY", -200f, textY);
            RenderSystem.drawText("[" + (supplies.sell ? "Y" : "N") + "] SELL", -40, textY);
            RenderSystem.drawText(String.valueOf(supplies.getCount()), 180, textY);
            RenderSystem.batch.draw(supplies.getMaterial().getItemTexture(), 8.5f, y, 1f, 1f);
            y -= 1;
        }
        RenderSystem.drawText(ship.getWeight() + "/" + ship.getStorageSpace(), -240, -180);
        boundsList.add(new Button(15f, 22f, 2.2f, 3.5f));
        boundsList.getLast().setAnyAction(() -> {
            ship.setBarterFreely(!ship.isBarterFreely());
        });
        Color color = InputSystem.mouseInBound(boundsList.getLast()) ? new Color(95 / 255f, 60 / 255f, 35 / 255f, 255 / 255f) : new Color(65 / 255f, 30 / 255f, 5 / 255f, 255 / 255f);
        if (ship.isBarterFreely()) {
            RenderSystem.drawText("Barter Freely", 60, -180, color);
        } else {
            RenderSystem.drawText("Barter Strictly", 25, -180, color);
        }
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        background.dispose();
        // Destroy screen's assets here.
    }

    @Override
    public Bounds getBounds() {
        return new Bounds(8, 22, 2, 13);
    }

    @Override
    public void leftClick() {
        for (Button bounds : boundsList) {
            if (InputSystem.mouseInBound(bounds)) {
                bounds.leftClick();
                return;
            }
        }
    }

    @Override
    public void rightClick() {
        for (Button bounds : boundsList) {
            if (InputSystem.mouseInBound(bounds)) {
                bounds.rightClick();
                return;
            }
        }
    }

    private enum Page {
        TRIP,
        RETURN,
        STOCK
    }
}
