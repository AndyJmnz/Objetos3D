import javax.media.j3d.*;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.vecmath.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


public class GamePanel extends JPanel implements ActionListener, KeyListener, MouseMotionListener {
    private SimpleUniverse universe;
    private Canvas3D canvas;
    private BranchGroup scene;
    private TransformGroup shipTG;
    private Transform3D shipTransform;
    private ArrayList<TransformGroup> stars;
    private ArrayList<TransformGroup> asteroids;
    private Timer timer;
    private boolean gameRunning;
    private JLabel statusLabel;
    private int score = 0;
    private float shipX = 0.0f;
    private float shipY = -0.3f;
    private TransformGroup scoreTG;
    private Text3D scoreText3D;
    private Clip backgroundMusic;
    private Clip gameOverMusic;

    public GamePanel() {
        setLayout(new BorderLayout());
        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        universe = new SimpleUniverse(canvas);
        scene = createSceneGraph();
        scene.compile();
        universe.getViewingPlatform().setNominalViewingTransform();
        universe.addBranchGraph(scene);
        add(canvas, BorderLayout.CENTER);

        timer = new Timer(200, this);
        timer.start();
        gameRunning = true;

        setFocusable(true);
        addKeyListener(this);
        addMouseMotionListener(this);
        setCustomCursor();

        statusLabel = new JLabel("Puntos: 0");
        add(statusLabel, BorderLayout.SOUTH);

        customizeOptionPane();

        String message = "Instrucciones:\n" +
                "1. Usa las flechas izquierda y derecha para mover la canasta.\n" +
                "2. Recolecta las pelotas de basket para ganar puntos.\n" +
                "3. Si recoges una lata de Coca Cola, pierdes.\n\n" +
                "¡Buena suerte!";
        JOptionPane.showMessageDialog(this, message, "Instrucciones", JOptionPane.INFORMATION_MESSAGE);
    }

    private void customizeOptionPane() {
        UIManager.put("OptionPane.background", new Color(255, 228, 225));
        UIManager.put("Panel.background", new Color(255, 228, 225));
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.BOLD, 14));
        UIManager.put("OptionPane.messageForeground", new Color(102, 102, 153));
        UIManager.put("Button.background", new Color(204, 255, 229));
        UIManager.put("Button.foreground", new Color(102, 102, 153));
    }

    private void playMusic(String filepath) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(filepath).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
            backgroundMusic = clip;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopMusic(Clip clip) {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    private void playGameOverMusic(String filepath) {
        stopMusic(backgroundMusic);
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(filepath).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
            gameOverMusic = clip;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setCustomCursor() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image cursorImage = toolkit.getImage("src/img/cursor.png");
        Cursor customCursor = toolkit.createCustomCursor(cursorImage, new Point(0, 0), "customCursor");
        setCursor(customCursor);
    }

    private BranchGroup createSceneGraph() {
        BranchGroup root = new BranchGroup();

        TextureLoader bgTextureLoader = new TextureLoader("src/img/estadio.jpg", this);
        ImageComponent2D bgImage = bgTextureLoader.getImage();
        Background bg = new Background(bgImage);
        bg.setApplicationBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0)); root.addChild(bg);

        shipTG = createShip();
        root.addChild(shipTG);

        stars = new ArrayList<>();
        asteroids = new ArrayList<>();
        generateObjects();

        for (TransformGroup tg : stars) {
            root.addChild(tg);
        }
        for (TransformGroup tg : asteroids) {
            root.addChild(tg);
        }
        root.addChild(createScoreText());

        playMusic("src/sounds/crowd.wav");

        addLight(root);

        return root;
    }

    private TransformGroup createShip() {
        TransformGroup tg = new TransformGroup();
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        Appearance appearance = new Appearance();
        TextureLoader loader = new TextureLoader("src/img/canaasta.png", null);
        Texture texture = loader.getTexture();
        appearance.setTexture(texture);
        Box ship = new Box(0.08f, 0.08f, 0.08f, Box.GENERATE_TEXTURE_COORDS, appearance);
        tg.addChild(ship);

        shipTransform = new Transform3D();
        shipTransform.setTranslation(new Vector3f(shipX, shipY, 0.0f));
        tg.setTransform(shipTransform);

        return tg;
    }



    private void generateObjects() {
        Random rand = new Random();

        for (int i = 0; i < 5; i++) {
            TransformGroup starTG = createStar();
            Transform3D transform = new Transform3D();
            float x = -0.75f + rand.nextFloat() * 1.5f;
            float y = 0.75f + rand.nextFloat() * 1.5f;
            transform.setTranslation(new Vector3f(x, y, 0.0f));
            starTG.setTransform(transform);
            stars.add(starTG);

            TransformGroup asteroidTG = createAsteroid();
            transform = new Transform3D();
            x = -0.75f + rand.nextFloat() * 1.5f;
            y = 0.75f + rand.nextFloat() * 1.5f;
            transform.setTranslation(new Vector3f(x, y, 0.0f));
            asteroidTG.setTransform(transform);
            asteroids.add(asteroidTG);
        }
    }

    private TransformGroup createStar() {
        TransformGroup tg = new TransformGroup();
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        Appearance appearance = new Appearance();
        TextureLoader loader = new TextureLoader("src/img/balon.jpg", null);
        Texture texture = loader.getTexture();
        appearance.setTexture(texture);
        Sphere star = new Sphere(0.05f, Sphere.GENERATE_TEXTURE_COORDS, 30, appearance);
        tg.addChild(star);
        return tg;
    }

    private TransformGroup createAsteroid() {
        TransformGroup tg = new TransformGroup();
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        Appearance appearance = new Appearance();
        TextureLoader loader = new TextureLoader("src/img/coca.png", null);
        Texture texture = loader.getTexture();
        appearance.setTexture(texture);
        Cylinder asteroid = new Cylinder(0.05f, 0.1f, Cylinder.GENERATE_TEXTURE_COORDS, appearance);
        tg.addChild(asteroid);
        return tg;
    }


    private TransformGroup createScoreText() {
        Font3D font3D = new Font3D(new Font("Helvetica", Font.PLAIN, 1), new FontExtrusion());
        scoreText3D = new Text3D(font3D, "Puntos: " + score, new Point3f(0.0f, 0.0f, 0.0f));
        scoreText3D.setCapability(Text3D.ALLOW_STRING_WRITE);
        Shape3D textShape = new Shape3D(scoreText3D);

        TransformGroup shadowTG = new TransformGroup();
        Transform3D shadowTransform = new Transform3D();
        shadowTransform.setTranslation(new Vector3f(0.02f, -0.02f, -0.1f));
        shadowTG.setTransform(shadowTransform);
        Appearance shadowAppearance = new Appearance();
        shadowAppearance.setMaterial(new Material(new Color3f(Color.DARK_GRAY), new Color3f(Color.DARK_GRAY), new Color3f(Color.DARK_GRAY), new Color3f(Color.DARK_GRAY), 1.0f));
        textShape.setAppearance(shadowAppearance);
        shadowTG.addChild(textShape);

        TransformGroup textTG = new TransformGroup();
        Appearance textAppearance = new Appearance();
        textAppearance.setMaterial(new Material(new Color3f(Color.WHITE), new Color3f(Color.WHITE), new Color3f(Color.WHITE), new Color3f(Color.WHITE), 1.0f));
        textShape.setAppearance(textAppearance);
        textTG.addChild(new Shape3D(scoreText3D, textAppearance));

        scoreTG = new TransformGroup();
        scoreTG.addChild(shadowTG);
        scoreTG.addChild(textTG);

        Transform3D scaleTransform = new Transform3D();
        scaleTransform.setScale(0.1);
        Transform3D scoreTransform = new Transform3D();
        scoreTransform.setTranslation(new Vector3f(-1.2f, 0.7f, -2.0f));
        scoreTransform.mul(scaleTransform);
        scoreTG.setTransform(scoreTransform);

        return scoreTG;
    }




    private void addLight(BranchGroup root) {
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

        Color3f light1Color = new Color3f(1.0f, 1.0f, 1.0f);
        Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
        DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
        light1.setInfluencingBounds(bounds);
        root.addChild(light1);

        AmbientLight ambientLight = new AmbientLight(new Color3f(0.2f, 0.2f, 0.2f));
        ambientLight.setInfluencingBounds(bounds);
        root.addChild(ambientLight);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameRunning) {
            updateObjects();
            checkCollisions();
            shipTransform.setTranslation(new Vector3f(shipX, shipY, 0.0f));
            shipTG.setTransform(shipTransform);
            universe.getCanvas().repaint();
        }
    }


    private void updateObjects() {
        Random rand = new Random();

        for (TransformGroup starTG : stars) {
            Transform3D starTransform = new Transform3D();
            starTG.getTransform(starTransform);
            Vector3f starPosition = new Vector3f();
            starTransform.get(starPosition);

            starPosition.y -= 0.05f;

            Transform3D rotation = new Transform3D();
            rotation.rotY(Math.PI / 60);
            starTransform.mul(rotation);

            if (starPosition.y < -0.8f) {
                starPosition.y = 0.8f;
                starPosition.x = -0.75f + rand.nextFloat() * 1.5f;
            }

            starTransform.setTranslation(starPosition);
            starTG.setTransform(starTransform);
        }

        for (TransformGroup asteroidTG : asteroids) {
            Transform3D asteroidTransform = new Transform3D();
            asteroidTG.getTransform(asteroidTransform);
            Vector3f asteroidPosition = new Vector3f();
            asteroidTransform.get(asteroidPosition);

            asteroidPosition.y -= 0.05f;

            Transform3D rotation = new Transform3D();
            rotation.rotX(Math.PI / 60);
            asteroidTransform.mul(rotation);

            if (asteroidPosition.y < -0.8f) {
                asteroidPosition.y = 0.8f;
                asteroidPosition.x = -0.75f + rand.nextFloat() * 1.5f;
            }

            asteroidTransform.setTranslation(asteroidPosition);
            asteroidTG.setTransform(asteroidTransform);
        }
    }


    private void checkCollisions() {
        Transform3D shipTransform = new Transform3D();
        shipTG.getTransform(shipTransform);
        Vector3f shipPosition = new Vector3f();
        shipTransform.get(shipPosition);

        for (TransformGroup starTG : stars) {
            Transform3D starTransform = new Transform3D();
            starTG.getTransform(starTransform);
            Vector3f starPosition = new Vector3f();
            starTransform.get(starPosition);

            if (shipPosition.epsilonEquals(starPosition, 0.1f)) {
                score++;
                statusLabel.setText("Puntos: " + score);
                repositionStar(starTG);

                scoreText3D.setString("Puntos: " + score);
            }
        }

        for (TransformGroup asteroidTG : asteroids) {
            Transform3D asteroidTransform = new Transform3D();
            asteroidTG.getTransform(asteroidTransform);
            Vector3f asteroidPosition = new Vector3f();
            asteroidTransform.get(asteroidPosition);

            if (shipPosition.epsilonEquals(asteroidPosition, 0.1f)) {

                gameRunning = false;
                timer.stop();
                playGameOverMusic("src/sounds/crowd2.wav");
                JOptionPane.showMessageDialog(this, "Game Over");
            }
        }
    }


    private void repositionStar(TransformGroup starTG) {
        Transform3D transform = new Transform3D();
        float x = -0.75f + new Random().nextFloat() * 1.5f;
        float y = 0.8f; // Reposition at the top
        transform.setTranslation(new Vector3f(x, y, 0.0f));
        starTG.setTransform(transform);
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            shipX -= 0.1f;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            shipX += 0.1f;
        }
        shipTransform.setTranslation(new Vector3f(shipX, shipY, 0.0f));
        shipTG.setTransform(shipTransform);
    }


    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}
}

