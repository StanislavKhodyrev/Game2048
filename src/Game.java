import controllers.Controller;
import models.Model;

import javax.swing.*;


public class Game {
    public static void main(String[] args) {
        Model model = new Model();
        Controller controller = new Controller(model);

        JFrame frame = new JFrame();
        frame.setTitle("2048");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(450, 500);
        frame.setResizable(false);

        frame.add(controller.getView());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
