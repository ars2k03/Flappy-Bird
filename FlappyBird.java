import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    int birdX = boardWidth/8;
    int birdY = boardHeight/2;

    int birdWidth = 34;
    int birdHeight = 24;


    class Bird{
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img){
            this.img = img;
        }
    }

    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img){
            this.img = img;
        }
    }

    Bird bird;
    int velocityX = -4;
    int velocityY = 0;
    double gravity = 1;

    ArrayList <Pipe> pipes;
    Random random = new Random();

    Timer gameloop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 0;
    boolean gameStart = false;
    String playerName;

    FlappyBird(){
        setPreferredSize(new Dimension(boardWidth,boardHeight));
//        setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        playerName = JOptionPane.showInputDialog("Enter your First name:");
        if(playerName == null || playerName.trim().equals("")) {
            playerName = "Player";
        }

        backgroundImg = new ImageIcon(getClass().getResource("/assets/bg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("/assets/flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("/assets/toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("/assets/bottompipe.png")).getImage();

        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        gameloop = new Timer(1000/60, this);
        gameloop.start();
    }

    public void placePipes(){
        int randompipeY = (int)(pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openningSpace = boardHeight/4;


        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randompipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openningSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void drawCenteredString(Graphics g, String text, int y, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = (boardWidth - metrics.stringWidth(text)) / 2;
        g.setFont(font);
        g.setColor(Color.BLACK);
        g.drawString(text, x, y);
    }


    public void draw(Graphics g){



        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        if(!gameStart){
            drawCenteredString(g, "Welcome to " + playerName + "!", boardHeight/2 - 150,
                    new Font("Arial", Font.BOLD, 32));
            drawCenteredString(g, "Press SPACE to Start", boardHeight/2,
                    new Font("Arial", Font.BOLD, 20));
        }

        else if(gameOver){
            drawCenteredString(g, "Game Over: " + (int)score, boardHeight/2 - 150, new Font("Arial", Font.BOLD, 32));

            drawCenteredString(g, "Press Space to Restart", boardHeight/3, new Font("Arial", Font.BOLD, 30));
        }


        else{
            g.drawString(String.valueOf((int) score),10, 35);
        }
    }

    public void move(){
        velocityY+=gravity;
        bird.y+=velocityY;
        bird.y = Math.max(bird.y,0);


        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score+=0.5;
            }

            if(collision(bird, pipe)){
                gameOver = true;

            }
        }

        if(bird.y > boardHeight){
            gameOver = true;
        }

    }

    public boolean collision (Bird a, Pipe b){
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            if(!gameStart){
                gameStart = true;
            }

            velocityY = -10;
            if(gameOver){
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameStart = false;
                gameloop.start();
                placePipesTimer.start();
            }
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }


    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver){
            placePipesTimer.stop();
            gameloop.stop();
        }
    }
}
