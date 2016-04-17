package core.masters;

import core.Context;
import game.entity.*;
import game.scenes.GameScene;
import javafx.animation.AnimationTimer;
import javafx.scene.shape.Circle;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GameMaster extends AnimationTimer {

	public final Context context;
	public final List<Enemy> enemies = new LinkedList<>();
	public final List<Shot> playerShots = new LinkedList<>();
	public final List<Shot> enemyShots = new LinkedList<>();
	public final List<Explosion> explosions = new LinkedList<>();
	public Player player;

	public int maxRoundTime = 100;
	public int currentRoundTime = maxRoundTime;
	public int currentLevel = 1;

	public double mouseX;
	public double mouseY;

	private double time = 0;
	private double lastNanoTime = System.nanoTime();

	public GameMaster(Context context) {
		this.context = context;

	}

	@Override
	public void handle(long currentNanoTime) {
		double fps = 60.0;

		// calculate time since last redraw.
		time += (currentNanoTime - lastNanoTime) / 1000000000.0;
		lastNanoTime = currentNanoTime;
		int passedTicks = (int) Math.floor(time * fps);
		time -= passedTicks / fps;

		// TODO: add ESC event handler for game scene

		tick(passedTicks);
		render();
	}

	private void tick(int ticks) {
		SceneMaster sceneMaster = context.getSceneMaster();

		// debug info
		GameScene gameScene = ((GameScene) sceneMaster.getContext().getSceneMaster().getScene("game"));
		gameScene.allticks += ticks;
		gameScene.tickLabel.setText(gameScene.allticks + " ticks");
		int nrEntities = enemies.size() + playerShots.size() + enemyShots.size() + 1;
		gameScene.entityLabel.setText(nrEntities + "enteties");

		sceneMaster.tick(ticks);
		List<List<? extends Entity>> lists = new LinkedList<>();
		lists.add(enemies);
		lists.add(playerShots);
		lists.add(enemyShots);
		lists.add(explosions);
		for (List<? extends Entity> list : lists) {
			Iterator<? extends Entity> i = list.iterator();
			while (i.hasNext()) {
				Entity entity = i.next();
				entity.tick(ticks);
				if (entity.valid)
					entity.tick(ticks);
				else
					i.remove();
			}
		}
		player.tick(ticks);

		checkCollide(player, enemies);
		checkCollide(player, enemyShots);
		for (Enemy enemy : enemies) {
			checkCollide(enemy, playerShots);
		}
	}

	private void checkCollide(Entity self, List<? extends Entity> others) {
		if (!self.valid)
			return;
		Circle selfC = self.collisionCircle();
		for (Entity other : others) {
			if (!other.valid)
				continue;
			Circle otherC = other.collisionCircle();
			if (Math.pow((otherC.getCenterX() - selfC.getCenterX()), 2) + Math
					.pow(otherC.getCenterY() - selfC.getCenterY(), 2) <= Math
					.pow(otherC.getRadius() - selfC.getRadius(), 2)) {
				self.collided(other);
				other.collided(self);
			}
		}
	}

	private void render() {

		SceneMaster sceneMaster = context.getSceneMaster();
		sceneMaster.render();
		GameScene gameScene = ((GameScene) sceneMaster.getContext().getSceneMaster().getScene("game"));
		gameScene.update();
	}

	@Override
	public void start() {
		super.start();
		player = new Player(200, 200, 0, 10);
		Enemy enemy1 = new ActiveEnemy(800, 800, 0, 1);
		Enemy enemy2 = new PassiveEnemy(400, 400, 0, 0);
		enemy2.radialSpeed = 0;
		Enemy enemy3 = new PassiveEnemy(800, 400, 50, 0);

		GameScene gamescene = ((GameScene) context.getSceneMaster().getScene("game"));
		gamescene.addEntitiy(EntityType.PLAYER, player);
		gamescene.addEntitiy(EntityType.ENEMY, enemy1);
		gamescene.addEntitiy(EntityType.ENEMY, enemy2);
		gamescene.addEntitiy(EntityType.ENEMY, enemy3);
		enemies.add(enemy1);
		enemies.add(enemy2);
		enemies.add(enemy3);
	}

	public void pause() {
		stop();
	}

	public void cotinue() {
		start();
	}

	@Override
	public void stop() {
		super.stop();
		// ...
	}

	public void restart() {
		stop();
		start();
	}

	public void addShot(Shot shot) {
		GameScene gamescene = ((GameScene) context.getSceneMaster().getScene("game"));
		if (shot.origin instanceof Enemy) {
			enemyShots.add(shot);
		} else {
			playerShots.add(shot);
		}
		gamescene.addEntitiy(EntityType.SHOT, shot);
	}

	public void addExplosion(Explosion explosion) {
		GameScene gamescene = ((GameScene) context.getSceneMaster().getScene("game"));
		gamescene.addEntitiy(EntityType.EXPLOSION, explosion);
	}

	public void mouseClicked(double x, double y) {
		player.spawnShot();
	}
}
