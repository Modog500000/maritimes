package xyz.volcanobay.maritimes.systems.input;

public interface Clickable {
    default void leftClick() {};
    default void middleClick() {};
    default void rightClick() {};

    Bounds getBounds();
    default boolean flash() { return false; }
}
