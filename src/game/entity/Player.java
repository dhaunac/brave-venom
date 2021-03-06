package game.entity;

import core.Context;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Rotate;

public class Player extends Entity {

	public int damage = 0;

	public Player(double x, double y, double dir, double speed) {
		super(x, y, dir, speed);
		initImage(Context.instance.getSceneMaster().getImage("playerShip1_blue"), 0.8);
		redraw();
	}

	@Override
	public void tick(int ticks) {
		moveInDir(Context.instance.eventMaster.getDir(), ticks);
		turnTowards(Context.instance.gameMaster.mouseX, Context.instance.gameMaster.mouseY, ticks);
		redraw();
	}

	@Override
	protected void redraw() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		if (valid) {
			gc.save();
			Rotate r = new Rotate(dirLooking + 90, xOffset, yOffset);
			gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());

			// TODO: only works for squares, must be adjusted for all rectangles
			double x = (canvasSize - imageWidth) / 2;
			double y = (canvasSize - imageHeight) / 2;

			gc.drawImage(image, x, y, imageWidth, imageHeight);

			if (damage >= 1 && damage <= 3) {
				gc.drawImage(Context.instance.getSceneMaster().getImage("playerShip1_damage" + damage), x, y,
						imageWidth, imageHeight);
			}

			gc.restore();
		}
	}

	public void spawnShot() {
		double xWeapon = getXCenter() + Math.cos(dirLooking / 180 * Math.PI) * imageWidth / 2.2d;
		double yWeapon = getYCenter() + Math.sin(dirLooking / 180 * Math.PI) * imageHeight / 2.2;
		Shot shot = new Shot(xWeapon, yWeapon, dirLooking, 20, "laserGreen03", this);
		Context.instance.gameMaster.addShot(shot);
	}

	@Override
	public void collided(Entity shot) {
		Context.instance.getGameMaster().damage(1);
	}

	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}

}
