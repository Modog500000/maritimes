package xyz.volcanobay.maritimes.systems.input;

public class Button extends Bounds implements Clickable {

    private Runnable leftAction = null;
    private Runnable middleAction = null;
    private Runnable rightAction = null;

    public Button(float left, float right, float bottom, float top) {
        super(left, right, bottom, top);
    }

    @Override
    public void leftClick() {
        if (leftAction != null) {
            leftAction.run();
        }
    }

    @Override
    public void middleClick() {
        if (middleAction != null) {
            middleAction.run();
        }
    }

    @Override
    public void rightClick() {
        if (rightAction != null) {
            rightAction.run();
        }
    }

    public void setLeftAction(Runnable leftAction) {
        this.leftAction = leftAction;
    }

    public void setRightAction(Runnable rightAction) {
        this.rightAction = rightAction;
    }

    public void setMiddleAction(Runnable middleAction) {
        this.middleAction = middleAction;
    }

    public void setAnyAction(Runnable anyAction) {
        this.leftAction = anyAction;
        this.middleAction = anyAction;
        this.rightAction = anyAction;
    }

    @Override
    public Bounds getBounds() {
        return this;
    }
}
