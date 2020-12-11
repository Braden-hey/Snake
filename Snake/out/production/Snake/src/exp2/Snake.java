package exp2;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Snake {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setBounds(450,160,900,758);
        frame.setResizable(false);
        //点击关闭能真正关闭
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //添加画布
        frame.add(new exp2.MyPanel());
        //设置标题和icon
        frame.setTitle("贪吃蛇小游戏");
        URL url = frame.getClass().getResource(".\\..\\..\\images\\snakeIcon.png");
        //System.out.println(url);
        Image img = Toolkit.getDefaultToolkit().getImage(url);
        frame.setIconImage(img);
        frame.setVisible(true);
    }
}

