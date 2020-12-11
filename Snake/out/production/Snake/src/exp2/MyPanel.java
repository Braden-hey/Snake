package exp2;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

//画布
public class MyPanel extends JPanel implements KeyListener, ActionListener {
    ImageIcon title;
    ImageIcon body;
    ImageIcon up;
    ImageIcon down;
    ImageIcon left;
    ImageIcon right;
    ImageIcon food;
    ImageIcon bigFood;

    //蛇的长度及分数
    int len = 0;
    int score = 0;

    //模式选择 1为不碰壁模式 2为碰壁模式
    int model = 2;
    //难度选择 difficulty数组越大速度越快
    int easy = 1;
    int medium = 2;
    int hard = 3;
    int difficulty = easy;
    String direction = "R";
    String tempDir;
    Boolean isStarted = false;
    SnakeNode p = null;
    SnakeNode first = null;
    SnakeNode end = null;
    SnakeNode toEnd;
    int speed = 25;
    int foodx;
    int foody;
    int bigFoodNum = 1;
    int bigFoodx;
    int bigFoody;
    int slowTimes = 0;
    Random rand = new Random();
    Boolean isFailed = false;
    Boolean hasBigFood = false;
    int timeDely = 100;
    private final JMenuBar jMenuBar = new JMenuBar();
    private final JMenu choose = new JMenu("选项");
    private final JMenuItem stop = new JMenuItem("暂停");
    private final JMenuItem start = new JMenuItem("开始");
    private final JMenu difChoose = new JMenu("难度选择");
    private final JMenuItem dif1 = new JMenuItem("难度1");
    private final JMenuItem dif2 = new JMenuItem("难度2");
    private final JMenuItem dif3 = new JMenuItem("难度3");
    private final JMenu modelChoose = new JMenu("模式选择");
    private final JMenuItem model1 = new JMenuItem("不碰壁模式");
    private final JMenuItem model2 = new JMenuItem("碰壁模式");
    private final JMenuItem exit = new JMenuItem("退出");
    //背景音乐
    Clip bgm;
    //时间快慢及速度
    Timer time = new Timer(timeDely, this);

    public MyPanel() {
        loadImages();
        initItem();
        initSnake();
        //获取键盘事件
        this.setFocusable(true);
        this.addKeyListener(this);
        LoadBGM();
        setBigFoodXY();

    }

    private void loadImages() {
        InputStream is;
        try {
            is = getClass().getClassLoader().getResourceAsStream("images\\title.jpg");
            assert is != null;
            title = new ImageIcon(ImageIO.read(is));

            is = getClass().getClassLoader().getResourceAsStream("images\\body.png");
            //assert is != null;
            body = new ImageIcon(ImageIO.read(is));

            is = getClass().getClassLoader().getResourceAsStream("images\\up.png");
            assert is != null;
            up = new ImageIcon(ImageIO.read(is));

            is = getClass().getClassLoader().getResourceAsStream("images\\down.png");
            assert is != null;
            down = new ImageIcon(ImageIO.read(is));

            is = getClass().getClassLoader().getResourceAsStream("images\\left.png");
            assert is != null;
            left = new ImageIcon(ImageIO.read(is));

            is = getClass().getClassLoader().getResourceAsStream("images\\right.png");
            assert is != null;
            right = new ImageIcon(ImageIO.read(is));

            is = getClass().getClassLoader().getResourceAsStream("images\\minFood.png");
            assert is != null;
            food = new ImageIcon(ImageIO.read(is));

            is = getClass().getClassLoader().getResourceAsStream("images\\bigFood.png");
            assert is != null;
            bigFood = new ImageIcon(ImageIO.read(is));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void LoadBGM() {
        try {
            //如果bgm文件比较大，使用SourseDataLine
            bgm = AudioSystem.getClip();
            InputStream is = this.getClass().getResourceAsStream("..\\music\\bgm.wav");
            AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            bgm.open(ais);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playBGM() {
        bgm.loop(Clip.LOOP_CONTINUOUSLY);
    }

    private void stopBGM() {
        bgm.stop();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //添加标题
        title.paintIcon(this,g,25,11);
        //绘制游戏区
        g.fillRect(25,110,850,600);
        //打印分数和长度
        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.PLAIN, 30));
        g.drawString("Length:" + len,700,50);
        g.drawString("Score:" + score,700,80);

        //打印蛇
        if (direction.equals("R")) {
            right.paintIcon(this, g, first.getX(), first.getY());
        } else if (direction.equals("L")) {
            left.paintIcon(this, g, first.getX(), first.getY());
        } else if (direction.equals("U")) {
            up.paintIcon(this, g, first.getX(), first.getY());
        } else if (direction.equals("D")) {
            down.paintIcon(this, g, first.getX(), first.getY());
        }
        p = first.next;
        while (p != null) {
            body.paintIcon(this, g, p.getX(), p.getY());
            p = p.next;
        }
        //打印食物
        food.paintIcon(this,g,foodx,foody);
        if (len < 150) {
            if (bigFoodNum % 6 == 0) {
                hasBigFood = true;
            }
            if (hasBigFood) {
                bigFood.paintIcon(this,g,bigFoodx,bigFoody);
            }
        }
        if (len == 10) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Consolas", Font.BOLD, 40));
            g.drawString("So nice!!!",100,200);
        }
        //显示背景
        if (slowTimes != 0) {
            setBackground(Color.GREEN);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Consolas", Font.BOLD, 40));
            g.drawString("Slow down!",600,600);
        } else {
            setBackground(Color.WHITE);
        }
        //打印开始
        if (!isStarted && !isFailed) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Consolas", Font.BOLD, 40));
            g.drawString("Press Space to Start",300,300);
        }
        //重新开始
        if (isFailed) {
            g.setColor(Color.RED);
            g.setFont(new Font("Consolas", Font.BOLD, 40));
            g.drawString("Failed! Press Space to Restart",200,300);
        }

    }

    private void setBigFoodXY() {
        Boolean setFood;
        do {
            setFood = true;
            bigFoodx = 25 + 25 * rand.nextInt(33);
            bigFoody = 110 + 25 * rand.nextInt(23);
            p = first;
            // 防止生成的食物在蛇和小食物身上
            while (p != null) {
                if ((p.getX() == bigFoodx && p.getY() == bigFoody)
                        || (p.getX() == bigFoodx -25 && p.getY() == bigFoody)
                        || (p.getX() == bigFoodx && p.getY() == bigFoody - 25)
                        || (p.getX() == bigFoodx -25 && p.getY() == bigFoody - 25)
                ) {
                    setFood = false;
                    break;
                }
                p = p.next;
            }
            if ((foodx == bigFoodx && foody == bigFoody)
                    || (foodx == bigFoodx -25 && foody == bigFoody)
                    || (foodx == bigFoodx && foody == bigFoody - 25)
                    || (foodx == bigFoodx -25 && foody == bigFoody - 25)) {
                setFood = false;
            }
        } while (!setFood);
    }
    private void initItem() {

        difChoose.add(dif1);
        difChoose.add(dif2);
        difChoose.add(dif3);
        modelChoose.add(model1);
        modelChoose.add(model2);
        jMenuBar.add(start);
        jMenuBar.add(stop);
        jMenuBar.add(difChoose);
        jMenuBar.add(modelChoose);
        jMenuBar.add(exit);
        //panel.add(popMenu);
        //设置字体
        choose.setFont(new Font("宋体",Font.BOLD,20));
        difChoose.setFont(new Font("宋体",Font.BOLD,20));
        modelChoose.setFont(new Font("宋体",Font.BOLD,20));
        setJMenuItemFont(dif1);
        setJMenuItemFont(dif2);
        setJMenuItemFont(dif3);
        setJMenuItemFont(model1);
        setJMenuItemFont(model2);
        setJMenuItemFont(start);
        setJMenuItemFont(stop);
        setJMenuItemFont(exit);
        this.add(jMenuBar);
        start.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isFailed) {
                    isFailed = false;
                    toRestart();
                } else {
                    isStarted = true;
                }
                playBGM();
                repaint();
            }
        });
        stop.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isStarted = false;
                stopBGM();
                repaint();
            }
        });
        dif1.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                difficulty = easy;
            }
        });
        dif2.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                difficulty = medium;
            }
        });
        dif3.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                difficulty = hard;
            }
        });
        model1.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                model = 1;
            }
        });
        model2.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                model = 2;
            }
        });
        exit.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.exit(0);
            }
        });
    }
    private void initSnake() {

        SnakeNode snake1 = new SnakeNode(125,135);
        initNode(snake1);
        SnakeNode snake2 = new SnakeNode(100,135);
        initNode(snake2);
        SnakeNode snake3 = new SnakeNode(75,135);
        initNode(snake3);
        setMinFood();
        bigFoodNum = 1;
        direction = "R";
        score = 0;
        setBigFoodXY();
        hasBigFood = false;
        time.start();
        if (difficulty == easy) {
            timeDely = 150;
            time.setDelay(timeDely);
        } else if (difficulty == medium) {
            timeDely = 100;
            time.setDelay(timeDely);
        } else if (difficulty == hard){
            timeDely = 60;
            time.setDelay(timeDely);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();//获取键盘背后对应的数字
        if (keyCode == KeyEvent.VK_SPACE) {
            if (isFailed) {
                isFailed = false;
                toRestart();
            } else {
                isStarted = !isStarted;
            }
            if (isStarted) {
                playBGM();
            } else {
                stopBGM();
            }
            repaint();
        } else if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
            tempDir = "L";
            direction = tempDir.equals(direction ) ? "L" : "R";
        } else if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
            tempDir = "R";
            direction = tempDir.equals(direction) ? "R" : "L";
        } else if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) {
            tempDir = "D";
            direction = tempDir.equals(direction) ? "D" : "U";
        } else if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) {
            tempDir = "U";
            direction = tempDir.equals(direction) ? "U" : "D";
        }
    }

    private void toRestart() {
        first = null;
        end = null;
        score = 0;
        len = 0;
        initSnake();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    //时钟时间到了就会调用这个移动方法
    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted && !isFailed) {
            //蛇进行移动
            //防止end结点改变，新建时比较简单
            toEnd = new SnakeNode(end.getX(),end.getY());
            p = end.prev;
            if (direction == "R") {
                end.setX(first.getX() + speed);
                end.setY(first.getY());
                if (end.getX() >= 875 && model == 1) {
                    end.setX(25);
                } else if (end.getX() >= 875 && model == 2) {
                    isFailed = true;
                }
            } else if (direction == "L") {
                end.setX(first.getX() - speed);
                end.setY(first.getY());
                if (end.getX() <= 0 && model == 1) {
                    end.setX(850);
                } else if (end.getX() <= 0 && model == 2) {
                    isFailed = true;
                }
            } else if (direction == "U") {
                end.setX(first.getX());
                end.setY(first.getY() - speed);
                if (end.getY() <= 85 && model == 1) {
                    end.setY(685);
                } else if (end.getY() <= 85 && model == 2) {
                    isFailed = true;
                }
            } else if (direction == "D") {
                end.setX(first.getX());
                end.setY(first.getY() + speed);
                if (end.getY() >= 710 && model == 1) {
                    end.setY(110);
                } else if (end.getY() >= 710 && model == 2) {
                    isFailed = true;
                }
            }
            p.next = null;
            end.next = first;
            first.prev = end;
            first = end;
            end = p;

            //蛇头与食物是否重合
            if (first.getX() == foodx && first.getY() == foody) {
                getMinFood();
                setMinFood();
            }
            if ((first.getX() >= bigFoodx)
                    && (first.getX() <= bigFoodx + 25)
                    && (first.getY() >= bigFoody)
                    && (first.getY() <= bigFoody + 25)) {
                getBigFood();
            }
            if (failedCheckOut()) {
                isFailed = true;
                stopBGM();
            }
            repaint();
        }
        //time.setDelay(timeDely);
        if (slowTimes != 0 && isStarted) {
            time.setDelay(200);
            setBackground(Color.BLUE);
            slowTimes --;
        } else if (difficulty == easy) {
            time.setDelay(150);
        } else if (difficulty == medium) {
            time.setDelay(100);
        } else if (difficulty == hard) {
            time.setDelay(60);
        }
        time.start();
    }

    private void getBigFood() {
        toEnd.next = null;
        toEnd.prev = end;
        end.next = toEnd;
        end = toEnd;
        score += 20 * difficulty;
        len ++;
        bigFoodNum ++;
        hasBigFood = false;
        slowTimes += 20;
        setBigFoodXY();
    }

    private boolean failedCheckOut() {
        p = first.next;
        while (p != null) {
            if (p.getX() == first.getX() && p.getY() == first.getY()) {
                return true;
            }
            p = p.next;
        }
        return false;
    }

    private void initNode(SnakeNode node) {
        if (first == null) {
            p = node;
            first = node;
            end = node;
            p.next = null;
            p.prev = null;
        } else {
            p.next = node;
            node.prev = p;
            p = p.next;
            end = p;
        }
        len ++;
    }

    private void setMinFood() {
        Boolean setFood;
        do {
            setFood = true;
            foodx = 25 + 25 * rand.nextInt(34);
            foody = 110 + 25 * rand.nextInt(24);
            p = first;
            // 防止生成的食物在蛇身上
            while (p != null) {
                if (p.getX() == foodx && p.getY() == foody) {
                    setFood = false;
                    break;
                }
                p = p.next;
            }
            if ((foodx == bigFoodx && foody == bigFoody)
                    || (foodx == bigFoodx -25 && foody == bigFoody)
                    || (foodx == bigFoodx && foody == bigFoody - 25)
                    || (foodx == bigFoodx -25 && foody == bigFoody - 25)) {
                setFood = false;
            }
        } while (!setFood);

    }

    private void getMinFood() {
        toEnd.next = null;
        toEnd.prev = end;
        end.next = toEnd;
        end = toEnd;
        score += 10 * difficulty;
        bigFoodNum++;
        len ++;
    }
    private void setJMenuItemFont(JMenuItem jMenuItem) {
        jMenuItem.setFont(new Font("宋体",Font.BOLD,20));
    }
}
