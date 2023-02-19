package main;


//java swing components
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.imageio.ImageIO;


// awt import
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
// for enemies and trees
import java.util.LinkedList;


public class Main {
		
	public static void main(String args[]) {
		
			//setting up window			
			JFrame window = new JFrame();
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.setResizable(false);
			window.setTitle("Wally's Walk");
						
			GamePanel gp = new GamePanel();
						
			//adding Gamepanel
			window.add(gp);
			window.pack();
						
			window.setLocationRelativeTo(null);
			window.setVisible(true);
						
			//main loop of the game
			gp.startgamethread();
		}

}





class GamePanel extends JPanel implements Runnable {

	
	//  game tick speed
	int fps = 60;
	
	// width and height of screen
	final int screenwidth = 500;
	final int screenheight = 750;
	
	// thread for game loop and a boolean variable to check if the game is over
	static Thread gameThread;
	static boolean isgameover = false;
	
	
	// object declaration 
	KeyHandler keyhandle = new KeyHandler();
	Player player = new Player(this,keyhandle);
	Grass leftgrass = new Grass(this,0,0);
	Grass rightgrass = new Grass(this,400,0);
	LinkedList<TreeRan> t = new LinkedList<TreeRan>();
	LinkedList<TreeRan> rt = new LinkedList<TreeRan>();
	static LinkedList<Enemy> en = new LinkedList<Enemy>();
	
	
	//Constructor of the game panel
	public GamePanel() {
		this.setPreferredSize(new Dimension(screenwidth,screenheight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyhandle);
		this.setFocusable(true);
		t.add(new TreeRan(this));
		rt.add(new TreeRan(this,true));
		en.add(new Enemy(this));
	}
	
	/* methods of the game panel*/
	
	public void startgamethread() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	// game over is called on collision
	public static void gameover() {
		isgameover =true;
	}

	// stop game by making thread null and ending the while loop
	public static void stopgame() {
		gameThread=null;
	}

	
	// thread of the game with maths for the fps
	@Override
	public void run() {
		
		double di = 1000000000/fps;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		long timer =0;
		int drawCount=0;
		
		
		while(gameThread !=null) {
			
			currentTime = System.nanoTime();
			delta+=(currentTime-lastTime)/di;
			timer +=(currentTime-lastTime);
			lastTime=currentTime;
			
			if(delta>=1) {
				
				//update state
				
				update();
				
				// rerender state
				
				repaint();
				
				delta --;
				drawCount++;}
			
			if(timer>=1000000000) {
				drawCount=0;
				timer=0;}
			
			} }
	
	
	// This will return the enemy list which will be used in player class to check for collision with enemy
	public static LinkedList<Enemy> enemylist(){
		return en;
	}
	
	
	// update player movement , tree and enemy spawning
	public void update() {
		
	
		player.update();
		
		for(int i=0;i<t.size();i++) {
			TreeRan tree = t.get(i);
			if(tree.y==300) {
				t.add(new TreeRan(this));
			}
			else if(tree.y>750){
				t.remove(tree);
			}
			tree.update();
		}
		
		for(int i=0;i<rt.size();i++) {
			TreeRan tree = rt.get(i);
			if(tree.y==300) {
				rt.add(new TreeRan(this,true));
			}
			else if(tree.y>750){
				rt.remove(tree);
			}
			tree.update();
		}
		
		for(int i=0;i<en.size();i++) {
			Enemy ene = en.get(i);
			if(ene.y==300) {
				en.add(new Enemy(this));
			}
			else if(ene.y>750){
				en.remove(ene);
			}
			ene.update();
		}
		
	}
	
	// drawing the entity(enemy,player,grass) to the screen
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		
		Graphics2D players = (Graphics2D)g;
		Graphics2D path = (Graphics2D)g;
		Graphics2D border1 = (Graphics2D)g;
		Graphics2D border2 = (Graphics2D)g;
		Graphics2D tree =  (Graphics2D)g;
		Graphics2D treeright =  (Graphics2D)g;
		Graphics2D enem =  (Graphics2D)g;
		
		
		
		
		
		leftgrass.draw(border1);
		rightgrass.draw(border2);
		
		for(int i=0;i<t.size();i++) {
			TreeRan treed = t.get(i);
			treed.draw(tree);
		}
		for(int i=0;i<rt.size();i++) {
			TreeRan treed = rt.get(i);
			treed.draw(treeright);
		}
		
		
		path.setColor(Color.gray);
		
		path.fillRect(100, 0, 300, 750);
		for(int i=0;i<en.size();i++) {
			Enemy ene = en.get(i);
			ene.draw(treeright);
		}
		player.draw(players);
		
		if(isgameover) {
			g.setColor(Color.BLACK);
			g.setFont(new Font("MV Boli",Font.PLAIN,45));
			g.drawString("Game Over",150 , 330);
		}
		
		
		
		border1.dispose();
		border2.dispose();
		players.dispose();
		path.dispose();
	}
	
}


/*
 * Key Handler class which will listen to the keyboard input
 * the keys are set (a,d) to move left and right 
*/
class KeyHandler implements KeyListener {

	public boolean leftPressed,rightPressed;
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
	
		int code = e.getKeyCode();
		
		if(code == KeyEvent.VK_A) {
			leftPressed =true;}
		
		else if(code == KeyEvent.VK_D) {
			rightPressed = true; }
			}

	@Override
	public void keyReleased(KeyEvent e) {
		
		int code = e.getKeyCode();
		
		if(code == KeyEvent.VK_A) {
			leftPressed =false;}
		
		else if(code == KeyEvent.VK_D) {
			rightPressed = false;}
				
	}

}

// Entity class will be extended by player, enemy ,grass,tree
class Entity {

	public int x,y;
	public int speed;
	
	public BufferedImage pe;
}


/* 
 * 			Player class
 */

class Player extends Entity {
	
	GamePanel gp;
	KeyHandler keyhandle;
	private LinkedList<Enemy> en = GamePanel.enemylist();
	
	public Player(GamePanel gp,KeyHandler kh) {
		this.gp =gp;
		this.keyhandle= kh;
		setDefaultValues();
		getPlayerImage();
	}
	
	public void setDefaultValues() {
		 x = 298;
		 y = 670;
		 speed = 5;
	}
	
	
	public void getPlayerImage() {
		try {
			pe = ImageIO.read(getClass().getResourceAsStream("/waley/cs16.png"));
		}
		catch(IOException error) {
			error.printStackTrace();
		}
	}
	
	public Rectangle getrect() {
		return new Rectangle(x,y,48,48);
	}
	
	public void update() {
		if(keyhandle.leftPressed==true & x>105) {
			x-=speed;
		}
		else if(keyhandle.rightPressed==true & x<350){
			x+=speed;
		}
		collisionCheck();
	}
	public void collisionCheck() {
		
		for(int i=0;i<en.size();i++) {
			if(getrect().intersects(en.get(i).getreact())) {
				GamePanel.gameover();
				GamePanel.stopgame();
			}
		}
		
	}
	public void draw(Graphics2D players) {
		
		BufferedImage image=pe;
		players.drawImage(image, x,y,48,48,null);
	}

}

		/// Grass Class

class Grass extends Entity {
	GamePanel gp;
	
	
	public Grass(GamePanel gp,int x,int y) {
		this.gp =gp;
		this.x=x;
		this.y=y;
		this.speed=0;
	}
	
	
	public void draw(Graphics2D grass) {
		grass.setColor(Color.green);
		grass.fillRect(x, y, 100, 750);

	}

}


//// Enemy class (walking people and car)
class Enemy extends Entity {
	
	GamePanel gp;
	KeyHandler keyhandle;
	
	public Enemy(GamePanel gp) {
		this.gp =gp;
		setDefaultValues();
		getPlayerImage();
	}
	
	public void setDefaultValues() {
		 x = (int) (Math.random() * (300 - 110 + 1) + 110) ;
		 y = 0;
		 speed = (int) (Math.random() * (5 - 3 + 1) + 3);
	}
	
	
	public void getPlayerImage() {
		try {
			int randint = (int) (Math.random() * (4 - 0 + 1) + 0) ;
			String[] img = {"ecar.png","egreen.png","eorange.png","ered.png","eyellow.png"};
			pe = ImageIO.read(getClass().getResourceAsStream("/enemy/"+img[randint]));
		}
		catch(IOException error) {
			error.printStackTrace();
		}
	}
	public void update() {
		y+=speed;
	}
	public Rectangle getreact() {
		return new Rectangle(x,y,50,50);
	}
	public void draw(Graphics2D players) {
		
		BufferedImage image=pe;
		players.drawImage(image, x,y,50,50,null);

	}

}


			// Random Tree class

class TreeRan extends Entity{
	
	GamePanel gp;
	KeyHandler keyhandle;
	
	public TreeRan(GamePanel gp) {
		this.gp =gp;
		setDefaultValues();
		getPlayerImage();
	}
	public TreeRan(GamePanel gp,boolean isright) {
		this.gp =gp;
		setDefaultValues(true);
		getPlayerImage();
	}
	
	public void setDefaultValues() {
		 x = 20;
		 y = 0;
		 speed = 3;
	}
	
	public void setDefaultValues(boolean istrue) {
		x=420;
		y=0;
		speed=3;
	}
	
	
	public void getPlayerImage() {
		try {
			int randint = (int) (Math.random() * (3 - 0 + 1) + 0) ;
			String[] img = {"t1.png","t2.png","t3.png","t4.png"};
			pe = ImageIO.read(getClass().getResourceAsStream("/Trees/"+img[randint]));
		}
		catch(IOException error) {
			error.printStackTrace();
		}
	}
	public void update() {
		y+=speed;
	}
	
	public void draw(Graphics2D players) {
		
		BufferedImage image=pe;
		players.drawImage(image, x,y,64,64,null);
	}


}




