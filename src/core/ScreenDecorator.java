package core;

public abstract class ScreenDecorator extends Screen {
    protected Screen decoratedScreen;

    public ScreenDecorator(Screen decoratedScreen) {
        super(decoratedScreen.getScreenControl());
        this.decoratedScreen = decoratedScreen;
        root = decoratedScreen.root;
        scene = decoratedScreen.scene;
        layers = decoratedScreen.layers;
        gcs = decoratedScreen.gcs;
    }

    @Override
    public void tick(int ticks) {
        decoratedScreen.tick(ticks);
    }

    @Override
    public void render() {
        decoratedScreen.render();
    }
}
