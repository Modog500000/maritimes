package xyz.volcanobay.maritimes.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import xyz.volcanobay.maritimes.core.RenderSystem;
import xyz.volcanobay.maritimes.systems.input.Bounds;

public class InputSystem {
    public static Vector2 mouse = new Vector2();

    public static void input() {
        Vector3 vector3Mouse = RenderSystem.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        mouse.set(vector3Mouse.x, vector3Mouse.y);
    }

    public static Vector2 getMouse() {
        return new Vector2(mouse);
    }

    public static boolean mouseInBound(Bounds bounds) {
        return InputSystem.mouse.x > bounds.left && InputSystem.mouse.x < bounds.right && InputSystem.mouse.y < bounds.top && InputSystem.mouse.y > bounds.bottom;
    }
}
