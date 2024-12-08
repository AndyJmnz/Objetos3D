package Practicas;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Objetos3D extends JPanel implements KeyListener {
    private Image fondo;

    SimpleUniverse universoCubo;
    SimpleUniverse universoCilindro;
    TransformGroup cubeTransformGroup;
    TransformGroup cylinderTransformGroup;
    Transform3D cubeTransform = new Transform3D();
    Transform3D cylinderTransform = new Transform3D();

    public Objetos3D() {
        fondo = Toolkit.getDefaultToolkit().getImage("src/fondo.jpg");

        JLayeredPane layeredPane = new JLayeredPane();
        setLayout(new BorderLayout());
        add(layeredPane, BorderLayout.CENTER);

        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvasCubo = new Canvas3D(config);
        canvasCubo.addKeyListener(this);
        canvasCubo.setSize(400, 400);
        canvasCubo.setLocation(0, 0);
        layeredPane.add(canvasCubo, JLayeredPane.DEFAULT_LAYER);

        universoCubo = new SimpleUniverse(canvasCubo);
        universoCubo.getViewingPlatform().setNominalViewingTransform();

        BranchGroup escenaCubo = crearGrafoEscenaCubo();
        escenaCubo.compile();
        universoCubo.addBranchGraph(escenaCubo);

        Canvas3D canvasCilindro = new Canvas3D(config);
        canvasCilindro.addKeyListener(this);
        canvasCilindro.setSize(400, 400);
        canvasCilindro.setLocation(400, 0);
        layeredPane.add(canvasCilindro, JLayeredPane.DEFAULT_LAYER);

        universoCilindro = new SimpleUniverse(canvasCilindro);
        universoCilindro.getViewingPlatform().setNominalViewingTransform();

        BranchGroup escenaCilindro = crearGrafoEscenaCilindro();
        escenaCilindro.compile();
        universoCilindro.addBranchGraph(escenaCilindro);

        JLabel fondoLabel = new JLabel(new ImageIcon(fondo));
        fondoLabel.setBounds(0, 0, getWidth(), getHeight());
        layeredPane.add(fondoLabel, JLayeredPane.PALETTE_LAYER);
    }
    private Background crearFondo(String rutaImagen) {
        TextureLoader cargador = new TextureLoader(rutaImagen, this);
        ImageComponent2D imagen = cargador.getImage();

        if (imagen == null) {
            System.out.println("No se pudo cargar la imagen de fondo desde: " + rutaImagen);
            return null;
        }

        Background fondo = new Background();
        fondo.setImage(imagen);
        fondo.setApplicationBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));

        return fondo;
    }

    public BranchGroup crearGrafoEscenaCubo() {
        BranchGroup objetoRaiz = new BranchGroup();

        cubeTransformGroup = new TransformGroup();
        cubeTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        cubeTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objetoRaiz.addChild(cubeTransformGroup);

        ColorCube cube = new ColorCube(0.4f);
        cubeTransformGroup.addChild(cube);

        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        AmbientLight light = new AmbientLight(new Color3f(0.3f, 0.3f, 0.3f));
        light.setInfluencingBounds(bounds);
        objetoRaiz.addChild(light);

        Background fondo = crearFondo("src/fondo.jpg");
        if (fondo != null) {
            objetoRaiz.addChild(fondo);
        }


        return objetoRaiz;
    }

    public BranchGroup crearGrafoEscenaCilindro() {
        BranchGroup objetoRaiz = new BranchGroup();

        cylinderTransformGroup = new TransformGroup();
        cylinderTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        cylinderTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objetoRaiz.addChild(cylinderTransformGroup);

        Appearance aparienciaCilindro = new Appearance();
        Material material = new Material();

        material.setAmbientColor(new Color3f(0.2f, 0.1f, 0.0f));
        material.setDiffuseColor(new Color3f(0.984f, 0.522f, 0.0f));
        material.setSpecularColor(new Color3f(1.0f, 0.8f, 0.5f));
        material.setShininess(64.0f);
        aparienciaCilindro.setMaterial(material);

        Cylinder cilindro = new Cylinder(0.3f, 0.8f, aparienciaCilindro);
        cylinderTransformGroup.addChild(cilindro);

        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

        DirectionalLight luzDireccional = new DirectionalLight(
                new Color3f(1.0f, 1.0f, 1.0f),
                new Vector3f(-1.0f, -1.0f, -1.0f)
        );
        luzDireccional.setInfluencingBounds(bounds);
        objetoRaiz.addChild(luzDireccional);

        AmbientLight luzAmbiente = new AmbientLight(new Color3f(0.3f, 0.3f, 0.3f));
        luzAmbiente.setInfluencingBounds(bounds);
        objetoRaiz.addChild(luzAmbiente);
        Background fondo = crearFondo("src/fondo.jpg");
        if (fondo != null) {
            objetoRaiz.addChild(fondo);
        }

        return objetoRaiz;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        double angle = 0.1;
        Transform3D rotation = new Transform3D();

        switch (e.getKeyCode()) {
            case KeyEvent.VK_X:
                rotation.setRotation(new AxisAngle4d(1, 0, 0, angle));
                cubeTransform.mul(rotation);
                cubeTransformGroup.setTransform(cubeTransform);
                break;
            case KeyEvent.VK_Y:
                rotation.setRotation(new AxisAngle4d(0, 1, 0, angle));
                cubeTransform.mul(rotation);
                cubeTransformGroup.setTransform(cubeTransform);
                break;
            case KeyEvent.VK_Z:
                rotation.setRotation(new AxisAngle4d(0, 0, 1, angle));
                cubeTransform.mul(rotation);
                cubeTransformGroup.setTransform(cubeTransform);
                break;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
                rotation.setRotation(new AxisAngle4d(1, 0, 0, angle));
                cylinderTransform.mul(rotation);
                cylinderTransformGroup.setTransform(cylinderTransform);
                break;
            case KeyEvent.VK_B:
                rotation.setRotation(new AxisAngle4d(0, 1, 0, angle));
                cylinderTransform.mul(rotation);
                cylinderTransformGroup.setTransform(cylinderTransform);
                break;
            case KeyEvent.VK_C:
                rotation.setRotation(new AxisAngle4d(0, 0, 1, angle));
                cylinderTransform.mul(rotation);
                cylinderTransformGroup.setTransform(cylinderTransform);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}