
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

abstract class GameObject {
	private int posX;
	private int posY;
	private String image;
	private int gameSpeed;

	GameObject(int posX, int posY, int gameSpeed, String image) {
		this.posX = posX;
		this.posY = posY;
		this.image = image;
		this.gameSpeed = gameSpeed;
	}

	int getX() {
		return posX;
	}

	int getY() {
		return posY;
	}

	String getImage() {
		return image;
	}
	int gameSpeed() {
		return gameSpeed;
	}

	abstract void move();
}

class BulletObject extends GameObject {
	int bulletLocation;

	BulletObject(int posX, int posY, int gameSpeed, String image) {
		super(posX, posY, gameSpeed, image);
		this.bulletLocation = posY;
	}

	void move() {
		bulletLocation--;
	}
}

class PlayObject extends GameObject {

	PlayObject(int posX, int posY, int gameSpeed, String image) {
		super(posX, posY, gameSpeed, image);
	}

	void move() {
	}
}

class EnemyObject extends GameObject {
	int enemyPlanePositionW;
	int enemyPlanePositionH;
	int enemyMovingNum;
	int enemyPlanesNum;
	boolean enemyMoving;

	EnemyObject(int posX, int posY, int enemyMovingNum, int enemyPlanesNum, boolean enemyMoving, int gameSpeed,
			String image) {
		super(posX, posY, gameSpeed, image);
		this.enemyPlanePositionW = posX;
		this.enemyPlanePositionH = posY;
		this.enemyMovingNum = enemyMovingNum;
		this.enemyPlanesNum = enemyPlanesNum;
		this.enemyMoving = enemyMoving;
	}

	void move() {
		if (0 == enemyPlanesNum) {
			return;
		} else {
			if (enemyMoving == true) {
				if (enemyMovingNum == 20) {
					enemyMoving = false;
					enemyPlanePositionH++;
				} else {
					enemyPlanePositionW++;
					enemyMovingNum++;
				}
			} else if (enemyMoving == false) {
				if (enemyMovingNum == -50) {
					enemyMoving = true;
					enemyPlanePositionW++;
				} else {
					enemyPlanePositionW--;
					enemyMovingNum--;
				}
			}
		}
		back: for (int h = 0; h < 1; h++) {
			for (int i = 0; i < enemyPlanesNum; i++) {
				if (0 == enemyPlanesNum) {
					break back;
				}
			}
		}
	}
}

class GameHandler extends JFrame implements KeyListener {
	private final int SCREEN_WIDTH = 300;
	private final int LEFT_PADDING = 2;
	private final int SCREEN_HEIGHT = 50;
	private final int FIELD_WIDTH = 160, FIELD_HEIGHT = 49;
	private char[][] buffer;
	private int field[][];
	private int enemyPlanesNum = 8;
	private final int limitedPlanesInLine = 4;
	private final int enemyPlaneSpace = 20;
	private int enemyPlanePositionW = 60;
	private int enemyPlanePositionH = 0;
	private int playPlanePositionW = 100;
	private int playPlanePositionH = 48;
	private int score = 0;
	private int rocketNum = 0;
	private int limitedRocketNum = 10;
	private int enemyMovingNum = 0;
	private int gameSpeed = 20;
	private boolean enemyMoving = false;
	private boolean keyUp = false;
	private boolean keyDown = false;
	private boolean keyLeft = false;
	private boolean keyRight = false;
	private boolean playerLive = true;
	Random random = new Random();
	ArrayList Rocket_List = new ArrayList();
	ArrayList Enemy_List = new ArrayList();
	GameObject play;
	EnemyObject enemy;
	BulletObject bull;

	JTextArea textArea;

	public void GameHandler(JTextArea ta) {
		textArea = ta;
		ta.addKeyListener(this);
		field = new int[FIELD_WIDTH][FIELD_HEIGHT];
		buffer = new char[SCREEN_WIDTH][SCREEN_HEIGHT];
		initData();
	}

	public void initData() {
		inputEnemy();
		for (int x = 0; x < FIELD_WIDTH; x++)
			for (int y = 0; y < FIELD_HEIGHT; y++)
				field[x][y] = (x == 0 || x == FIELD_WIDTH - 1) ? 1 : 0;
		clearBuffer();
	}

	private void clearBuffer() {
		for (int y = 0; y < SCREEN_HEIGHT; y++) {
			for (int x = 0; x < SCREEN_WIDTH; x++) {
				buffer[x][y] = '.';
			}
		}
	}

	private void drawToBuffer(int px, int py, String c) {
		for (int x = 0; x < c.length(); x++) {
			buffer[px + x + LEFT_PADDING][py] = c.charAt(x);
		}
	}

	private void drawToBuffer(int px, int py, char c) {
		buffer[px + LEFT_PADDING][py] = c;
	}

	private void drawScore() {
		drawToBuffer(FIELD_WIDTH + 2, 1, "┌───────────────┐");
		drawToBuffer(FIELD_WIDTH + 2, 2, "│               │");
		drawToBuffer(FIELD_WIDTH + 2, 3, "└───────────────┘");
		drawToBuffer(FIELD_WIDTH + 4, 2, "SCORE: " + score);
	}

	public void inputPlayer() {
		if (playerLive) {
			play = new PlayObject(playPlanePositionW, playPlanePositionH, gameSpeed, "ㅁㅁ^ㅁㅁ");
		} else {
			play = new PlayObject(playPlanePositionW, playPlanePositionH, gameSpeed, "GAME OVER");
		}
	}

	public void inputRocket() {
		bull = new BulletObject(playPlanePositionW + 2, playPlanePositionH, gameSpeed, "O");
		Rocket_List.add(bull);
	}

	public void inputEnemy() {
		for (int i = 0; i < enemyPlanesNum; i++) {
			if (i >= limitedPlanesInLine) {
				enemy = new EnemyObject(enemyPlanePositionW + 5 + ((i - limitedPlanesInLine) * enemyPlaneSpace),
						enemyPlanePositionH + 3, enemyMovingNum, enemyPlanesNum, enemyMoving, gameSpeed, "ㅁㅁ?ㅁㅁ");
				Enemy_List.add(enemy);
			} else {
				enemy = new EnemyObject(enemyPlanePositionW + (i * enemyPlaneSpace), enemyPlanePositionH,
						enemyMovingNum, enemyPlanesNum, enemyMoving, gameSpeed, "ㅁㅁ?ㅁㅁ");
				Enemy_List.add(enemy);
			}
		}
	}

	void moveEnemy() {
		for (int i = 0; i < Enemy_List.size(); i++) {
			enemy = (EnemyObject) Enemy_List.get(i);
			enemy.move();
		}
	}

	void collision() {
		if (Enemy_List.size() == 0) {
			gameSpeed--;
			inputEnemy();
		} else {
			for (int i = 0; i < Rocket_List.size(); i++) {
				for (int h = 0; h < Enemy_List.size(); h++) {
					for (int k = 0; k < enemy.getImage().length(); k++) {
						bull = (BulletObject) Rocket_List.get(i);
						enemy = (EnemyObject) Enemy_List.get(h);

						if (bull.getX() == enemy.enemyPlanePositionW + k && bull.bulletLocation == enemy.enemyPlanePositionH) {
							rocketNum--;
							Rocket_List.remove(i);
							Enemy_List.remove(h);
							break;
						}
						if (play.getX() == enemy.enemyPlanePositionW + k && play.getY() == enemy.enemyPlanePositionH) {
							playerLive = false;
						}
					}
				}
			}
		}
	}

	public void drawPlayer() {
		drawToBuffer(play.getX(), play.getY(), play.getImage());
	}

	public void drawEnemy() {
		for (int i = 0; i < Enemy_List.size(); i++) {
			enemy = (EnemyObject) Enemy_List.get(i);
			drawToBuffer(enemy.enemyPlanePositionW, enemy.enemyPlanePositionH, enemy.getImage());
		}
	}

	public void drawRocket() {
		for (int i = 0; i < Rocket_List.size(); i++) {
			bull = (BulletObject) Rocket_List.get(i);
			if (bull.bulletLocation == 0) {
				Rocket_List.remove(i);
				rocketNum--;
			} else {
				bull.move();
				drawToBuffer(bull.getX(), bull.bulletLocation, bull.getImage());
			}
		}
	}

	public void drawAll() {

		for (int x = 0; x < FIELD_WIDTH; x++) {
			for (int y = 0; y < FIELD_HEIGHT; y++) {
				drawToBuffer(x, y, " #".charAt(field[x][y]));
			}
		}

		drawToBuffer(180, 47, " by sung");
		drawPlayer();
		drawEnemy();
		drawRocket();
		drawScore();
		render();
	}

	private void render() {

		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < SCREEN_HEIGHT; y++) {
			for (int x = 0; x < SCREEN_WIDTH; x++) {
				sb.append(buffer[x][y]);
			}
			sb.append("\n");
		}
		textArea.setText(sb.toString());
	}

	public void keyProcess() {
		if (keyUp == true) {
			if (playPlanePositionH == SCREEN_HEIGHT/2)
				return;
			else
				playPlanePositionH--;
		}
		if (keyDown == true) {
			if (playPlanePositionH >= FIELD_HEIGHT - 1)
				return;
			else
				playPlanePositionH++;
		}
		if (keyLeft == true) {
			if (playPlanePositionW <= LEFT_PADDING - 1)
				return;
			else
				playPlanePositionW--;
		}
		if (keyRight == true) {
			if (playPlanePositionW >= FIELD_WIDTH - play.getImage().length() - 1)
				return;
			else
				playPlanePositionW++;
		}

		if (keyUp && keyRight)// 동시에 두개의 키를 누루면(대각선)
		{
			if (playPlanePositionH == SCREEN_HEIGHT/2 || playPlanePositionW >= FIELD_WIDTH - play.getImage().length() - 1)
				return;
			else
				playPlanePositionW++;
			playPlanePositionH--;
		}
		if (keyUp && keyLeft) {
			if (playPlanePositionH == SCREEN_HEIGHT/2 || playPlanePositionW <= LEFT_PADDING - 1)
				return;
			else
				playPlanePositionW--;
			playPlanePositionH--;
		}
		if (keyDown && keyRight) {
			if (playPlanePositionH >= FIELD_HEIGHT - 1
					|| playPlanePositionW >= FIELD_WIDTH - play.getImage().length() - 1)
				return;
			else
				playPlanePositionW++;
			playPlanePositionH++;

		}
		if (keyDown && keyLeft) {
			if (playPlanePositionH >= FIELD_HEIGHT - 1 || playPlanePositionW <= LEFT_PADDING - 1)
				return;
			else
				playPlanePositionH++;
			playPlanePositionW--;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {

		switch (e.getKeyCode()) {
		case KeyEvent.VK_RIGHT:
			keyRight = true;
			break;
		case KeyEvent.VK_LEFT:
			keyLeft = true;
			break;
		case KeyEvent.VK_DOWN:
			keyDown = true;
			break;
		case KeyEvent.VK_UP:
			keyUp = true;
			break;
		case KeyEvent.VK_SPACE:
			if (rocketNum < limitedRocketNum) {
				rocketNum++;
				inputRocket();
			}
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			keyUp = false;
			break;
		case KeyEvent.VK_DOWN:
			keyDown = false;
			break;
		case KeyEvent.VK_LEFT:
			keyLeft = false;
			break;
		case KeyEvent.VK_RIGHT:
			keyRight = false;
			break;
		}
	}
}

public class FlyingGame extends JFrame implements Runnable {

	private JTextArea textArea;
	GameHandler g = new GameHandler();

	FlyingGame() {

		setTitle("20130509 EunilSung");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1200, 650);
		setLocationRelativeTo(null);
		textArea = new JTextArea();
		textArea.setFont(new Font("Courier New", Font.PLAIN, 10));
		g.GameHandler(textArea);
		textArea.setEditable(false);
		add(textArea);
		setVisible(true);
		Thread th = new Thread(this);
		th.start();
	}

	public void run() {
		try {
			while (true) {
				g.inputPlayer();
				g.drawAll();
				g.moveEnemy();
				g.collision();
				g.keyProcess();
				Thread.sleep(g.play.gameSpeed());
			}
		} catch (Exception e) {
		}
	}

	public static void main(String[] args) {
		new FlyingGame();
	}
}
