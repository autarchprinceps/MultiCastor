package multicastor.view;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Klasse fuer das Snake-Gimmick. Es wird ein 90x90px gro�es Feld gezeichnet.
 * Jedes Snake-"Feld" ist 3x3px gro� <br>
 * <br>
 * Behandlung im viewController: <br>
 * 
 * <pre>
 * {@code 
 * +++++++++ Update der Snake mit moveSnake(snakeDir). Gibt Status als int zurueck +++++
 *  
 * 
 *  switch(mySnakeGimmick.moveSnake(snakeDir)){
 * case  1: System.out.println("Found Apple!");
 * 		   break;
 * case -1: System.out.println("CRASH!");
 * 		   break;
 * case  0: System.out.println("nix passiert");
 * }
 * 
 * ++++++++++ Update der Snake-Richtung: +++++++++++++
 * public void keyPressed(KeyEvent e){
 * 	switch(e.getKeyCode()){
 * 	case 38: snakeDir = SnakeGimmick.SNAKE_DIRECTION.N;
 * 			 break;
 * 	case 40: snakeDir = SnakeGimmick.SNAKE_DIRECTION.S;
 * 			 break;
 * 	case 37: snakeDir = SnakeGimmick.SNAKE_DIRECTION.W;
 * 			 break;
 * 	case 39: snakeDir = SnakeGimmick.SNAKE_DIRECTION.E;
 * 	default:
 * 	}
 * 	
 * }
 * }
 * </pre>
 */
public class SnakeGimmick {
	public static enum SNAKE_DIRECTION {
		E, N, S, W
	}

	private static final int APPLE = Integer.MAX_VALUE;

	public Boolean gameIsRunning = false;;

	Graphics g;
	private SNAKE_DIRECTION lastD = SNAKE_DIRECTION.E;
	private final int snakeField[][] = new int[30][30];
	private int snakeLength, headX, headY, appleX, appleY, score;

	public SnakeGimmick() {
		initSnake();
	}

	/**
	 * Zeichnet das Snake-Spiel
	 * 
	 * @param g
	 *            das Graphics-Objekt, auf dem gezeichnet werden soll
	 */
	public void drawSnake(final Graphics g) {
		int bodyCoord[] = new int[2];
		g.setColor(Color.LIGHT_GRAY);
		g.fill3DRect(5, 5, 90, 90, true);

		// Zeichne Apfel
		g.setColor(Color.RED);
		g.fillRect(5 + (appleX * 3), 5 + (appleY * 3), 3, 3);

		// Schlange vom head aus bis zum schwanz zeichnen
		g.setColor(Color.BLACK);
		bodyCoord[0] = headX;
		bodyCoord[1] = headY;
		for (int i = snakeLength; i > -1; i--) {
			bodyCoord = findSnakeBodyPart(i, bodyCoord[0], bodyCoord[1]);
			g.fillRect((bodyCoord[0] * 3) + 5, (bodyCoord[1] * 3) + 5, 3, 3);
		}

		// Score schreiben
		g.setColor(Color.WHITE);
		g.drawString(score + "", 100, 95);
	}

	/**
	 * Apfel "zieht um". Neue Location random. Sollte es ein Spieler geschafft
	 * haben, alle Felder mit der Schlange zu fuellen, wuerde das Game hier in
	 * eine Endlosschleife laufen
	 */
	public void dropNewApple() {
		int newAppleX, newAppleY;
		while (true) {
			newAppleX = (int) ((Math.random() * 100) % 30);
			newAppleY = (int) ((Math.random() * 100) % 30);
			if (snakeField[newAppleX][newAppleY] == -1) {
				snakeField[newAppleX][newAppleY] = APPLE;
				appleX = newAppleX;
				appleY = newAppleY;
				break;
			}
		}
	}

	/**
	 * Setzt das Snake-Feld auf die Anfangswerte (zurueck)
	 */
	public void initSnake() {
		// setze alle Felder -1 (unbesetzt)
		for (int i = 0; i < 30; i++) {
			for (int j = 0; j < 30; j++) {
				snakeField[i][j] = -1;
			}
		}

		snakeLength = 1;
		snakeField[22][23] = 0;
		snakeField[23][23] = 1;
		headX = 23;
		headY = 23;
		dropNewApple();
		score = 0;
		gameIsRunning = true;
	}

	/**
	 * Setzt die Snake ein Feld weiter und prueft die Folgen (Apfel gefunden,
	 * Crash)
	 * 
	 * @param d
	 *            die Richtung in die die Snake bewegt werden soll
	 * @return einen Status als int-Wert<br>
	 *         1: Apfel gefunden<br>
	 *         0: nichts passiert<br>
	 *         -1: Crash
	 */
	public int moveSnake(SNAKE_DIRECTION d) {
		Boolean foundApple = false;
		// Die Richtung aus die die Snake kommt, ist nicht erlaubt!
		// Sollte diese Richtung uebergeben werden,
		// laeuft die Snake weiter in die vorige Richtung
		if ((lastD == SNAKE_DIRECTION.N) && (d == SNAKE_DIRECTION.S)) {
			d = SNAKE_DIRECTION.N;
		}
		if ((lastD == SNAKE_DIRECTION.S) && (d == SNAKE_DIRECTION.N)) {
			d = SNAKE_DIRECTION.S;
		}
		if ((lastD == SNAKE_DIRECTION.W) && (d == SNAKE_DIRECTION.E)) {
			d = SNAKE_DIRECTION.W;
		}
		if ((lastD == SNAKE_DIRECTION.E) && (d == SNAKE_DIRECTION.W)) {
			d = SNAKE_DIRECTION.E;
		}

		lastD = d;

		switch (d) {
		case N:
			headY--;
			break;
		case S:
			headY++;
			break;
		case W:
			headX--;
			break;
		case E:
			headX++;
			break;
		}

		// "Umbrueche" an den Raendern beruecksichtigen
		if (headY < 0) {
			headY = 29;
		}
		if (headY > 29) {
			headY = 0;
		}
		if (headX < 0) {
			headX = 29;
		}
		if (headX > 29) {
			headX = 0;
		}

		// Ist das Feld an der neuen Head-Position leer?
		if (snakeField[headX][headY] != -1) {
			// APFEL
			if (snakeField[headX][headY] == APPLE) {
				foundApple = true;
				snakeLength++;
				score += 10;
				dropNewApple();
			} else {
				// Kollision
				gameIsRunning = false;
				return -1;
			}
		}

		// Wird nur erreicht, wenn es keine Kollision gab

		// Wenn kein Apfel gefunden wurde,
		// muessen die anderen Indizes umgeaendert werden
		if (!foundApple) {
			int[] temp = new int[2];
			temp[0] = headX;
			temp[1] = headY;
			for (int i = snakeLength; i >= 0; i--) {
				temp = findSnakeBodyPart(i, temp[0], temp[1]);
				snakeField[temp[0]][temp[1]]--;
			}
		}

		// Index des neuen Kopfs setzen
		snakeField[headX][headY] = snakeLength;
		if (foundApple) {
			foundApple = false;
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Sucht ein Teil der Snake innerhalb des Spielfelds.
	 * 
	 * @param index
	 *            index des gesuchten Snake-Koerperteil
	 * @param lastX
	 *            x-Position des letzten Koerperteils (die "Ausgangsposition")
	 * @param lastY
	 *            y-Position des letzten Koerperteils (die "Ausgangsposition")
	 * @return int[2] mit [0] = x-Position, [1] = y-Position
	 */
	private int[] findSnakeBodyPart(final int index, final int lastX,
			final int lastY) {
		// �stliches Feld ueberpruefen
		if (lastX < 29) {
			if (snakeField[lastX + 1][lastY] == index) {
				return new int[] { (lastX + 1), lastY };
			}
		}
		if (lastX == 29) {
			if (snakeField[0][lastY] == index) {
				return new int[] { 0, lastY };
			}
		}
		// Westliches Feld
		if (lastX > 0) {
			if (snakeField[lastX - 1][lastY] == index) {
				return new int[] { (lastX - 1), lastY };
			}
		}
		if (lastX == 0) {
			if (snakeField[29][lastY] == index) {
				return new int[] { 29, lastY };
			}
		}
		// Noerdliches Feld
		if (lastY < 29) {
			if (snakeField[lastX][lastY + 1] == index) {
				return new int[] { lastX, (lastY + 1) };
			}
		}
		if (lastY == 29) {
			if (snakeField[lastX][0] == index) {
				return new int[] { lastX, 0 };
			}
		}
		// Suedliches Feld
		if (lastY > 0) {
			if (snakeField[lastX][lastY - 1] == index) {
				return new int[] { lastX, (lastY - 1) };
			}
		}
		if (lastY == 0) {
			if (snakeField[lastX][29] == index) {
				return new int[] { lastX, 29 };
			}
		}
		return new int[] { lastX, lastY };
	}
}
