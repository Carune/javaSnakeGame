import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;


import javax.swing.JPanel;

public class GamePanel extends JPanel implements ActionListener{     // JPanel 을 상속받아 개발자 임의의 모양을 가지는 패널로 사용
	
	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 25;
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT)/UNIT_SIZE;
	static final int DELAY = 75;  // 숫자가 높아질수록 게임이 느려짐
	// 뱀의 신체 좌표
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	int bodyParts = 6;
	int applesEaten;
	int appleX;
	int appleY;
	char direction = 'R';  // 방향 R : 오른쪽, L : 왼쪽, U : 위, D : 아래 
	boolean running = false;
	Timer timer;
	Random random;
	
	
	
	// 생성자
	GamePanel(){
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black); // 배경색
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		startGame();
		
		
	}
	
	public void startGame() {
		newApple();
		running = true;
		timer = new Timer(DELAY, this); // 지연값을 전달하여 게임 속도 결정하기 , this = 액션리스너 인터페이스
		timer.start();
		
	}
	
	public void paintComponent(Graphics g) { // 스윙 컴포넌트가 자신의 모양을 그리는 메소드 , 오버라이딩
		super.paintComponent(g);
		draw(g);
		
	}
	
	
	public void draw(Graphics g) {
		
		if(running) {
			/*
			// 그리드 생성
			for(int i=0;i<SCREEN_HEIGHT/UNIT_SIZE;i++) {
				g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);  // (x1, y1) 에서 (x2, y2) 까지 선 긋기
				g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);			
			}
			*/
			g.setColor(Color.red);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);    // (x, y) 에서 (w x h) 크기의 사각형에 내접하는 타원 채우기
			
			for(int i = 0;i<bodyParts;i++) {
				if(i == 0) {
					g.setColor(Color.green);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
				else {
//					g.setColor(new Color(45, 180, 0));  // RGB
					g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			g.setColor(Color.red);
			g.setFont(new Font("Ink Free", Font.BOLD, 40));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Score : "+applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score : "+applesEaten))/2, g.getFont().getSize());
		}
		else {
			gameOver(g);
		}
		
	}
	
	public void newApple() {
		appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE)) * UNIT_SIZE;
		appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE)) * UNIT_SIZE;
	}
	
	public void move() {
		for(int i = bodyParts;i>0;i--) {
			x[i] = x[i-1];
			y[i] = y[i-1];
		}
		switch(direction) {
		case 'U' :
			y[0] = y[0] - UNIT_SIZE;
			break;
		case 'D' :
			y[0] = y[0] + UNIT_SIZE;
			break;
		case 'L' :
			x[0] = x[0] - UNIT_SIZE;
			break;
		case 'R' :
			x[0] = x[0] + UNIT_SIZE;
			break;
		}
		
	}
	
	public void checkApple() {
		if((x[0] == appleX) && (y[0] == appleY)) {
			bodyParts++;
			applesEaten++;
			newApple();
			
		}
	}
	
	public void checkCollisions() {
		//머리와 몸통 부분이 충돌하는지 확인
		for(int i = bodyParts;i>0;i--) {
			if((x[0] == x[i]) && (y[0] == y[i])) {
				running = false;
			}
		}
		
		//머리가 왼쪽 테두리에 닿는지 확인
		if(x[0] < 0) {
			running = false;
		}
		
		//머리가 오른쪽 테두리에 닿는지 확인
		if(x[0] > SCREEN_WIDTH) {
			running = false;
		}
		
		//머리가 위 테두리에 닿는지 확인
		if(y[0] < 0) {
			running = false;
		}
		
		//머리가 아래 테두리에 닿는지 확인
		if(y[0] > SCREEN_HEIGHT) {
			running = false;
		}
		
		if(!running) {
			timer.stop();
		}
	}
	
	public void gameOver(Graphics g) {
		// Score
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 40));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("Score : "+applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score : "+applesEaten))/2, g.getFont().getSize());
		
		// Game Over 텍스트
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 75));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
		
	} 
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(running) {
			move();
			checkApple();
			checkCollisions();
		}
		repaint();
		
	}
	
	// 내부클래스
	public class MyKeyAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if(direction != 'R') {   // 180도 전환 불가
					direction = 'L';
				}
				break;
			case KeyEvent.VK_RIGHT:
				if(direction != 'L') {
					direction = 'R';
				}
				break;
			case KeyEvent.VK_UP:
				if(direction != 'D') {
					direction = 'U';
				}
				break;
			case KeyEvent.VK_DOWN:
				if(direction != 'U') {
					direction = 'D';
				}
				break;
			}	
		}
		
	}
	
	
}
