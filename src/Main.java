import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.Canvas3D;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args){
        System.setProperty("sun.awt.noerasebackgroung", "true");
        JFrame ventana = new JFrame("Objetos3D");
        //Clase.App3D_5 panel = new Clase.App3D_5();
        Objetos3D panel = new Objetos3D();
        ventana.add(panel);
        ventana.setVisible(true);
        ventana.setSize(800,400);
        ventana.setLocationRelativeTo(null);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
