import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.universe.SimpleUniverse;
import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import java.awt.*;

public class iluminacion extends JPanel {
    public iluminacion() {
        setLayout(new BorderLayout());
        Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        add(canvas, BorderLayout.CENTER);

        SimpleUniverse universe = new SimpleUniverse(canvas);
        BranchGroup group = createScene();

        universe.getViewingPlatform().setNominalViewingTransform();
        universe.addBranchGraph(group);
        TransformGroup viewTransformGroup = universe.getViewingPlatform().getViewPlatformTransform();
        Transform3D viewTransform = new Transform3D();
        viewTransform.setTranslation(new Vector3f(0.0f, 0.0f, 5.0f)); // Aleja la cámara
        viewTransformGroup.setTransform(viewTransform);
    }

    private BranchGroup createScene() {
        BranchGroup group = new BranchGroup();

        // Fondo
        Background background = new Background(new Color3f(0.5f, 0.8f, 1.0f)); // Azul claro
        background.setApplicationBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 10.0));
        group.addChild(background);

        // -------------------- Configuración del Cilindro --------------------
        TransformGroup cylinderTransformGroup = createTransformGroup();
        group.addChild(cylinderTransformGroup);

        // Apariencia del cilindro
        Appearance aparienciaCilindro = new Appearance();
        Material materialCilindro = new Material();
        materialCilindro.setAmbientColor(new Color3f(0.2f, 0.1f, 0.0f)); // Color ambiental
        materialCilindro.setDiffuseColor(new Color3f(0.984f, 0.522f, 0.0f)); // Color difuso (naranja)
        materialCilindro.setSpecularColor(new Color3f(1.0f, 0.8f, 0.5f)); // Reflejo especular
        materialCilindro.setShininess(64.0f); // Brillo
        aparienciaCilindro.setMaterial(materialCilindro);

        // Crear cilindro y añadirlo al TransformGroup
        Cylinder cilindro = new Cylinder(0.1f, 0.3f, aparienciaCilindro);
        cylinderTransformGroup.addChild(cilindro);

        // Añadir movimiento circular al cilindro
        addCircularMotion(cylinderTransformGroup, 0.3f);

        // -------------------- Configuración del Cubo --------------------
        TransformGroup cubeTransformGroup = createTransformGroup();
        group.addChild(cubeTransformGroup);

        // Apariencia del cubo
        Appearance aparienciaCubo = createAppearance(new Color3f(0.0f, 1.0f, 0.0f)); // Verde
        Box cubo = new Box(0.1f, 0.1f, 0.1f, Box.GENERATE_NORMALS, aparienciaCubo);
        cubeTransformGroup.addChild(cubo);

        // Añadir movimiento circular al cubo (en sentido opuesto al cilindro)
        addCircularMotion(cubeTransformGroup, -0.3f);

        // -------------------- Luces --------------------
        addLights(group);

        return group;
    }

    private void addCircularMotion(TransformGroup tg, float radius) {
        Transform3D axis = new Transform3D();
        Alpha alpha = new Alpha(-1, 5000); // Movimiento infinito con duración de 5 segundos
        Transform3D circularPath = new Transform3D();
        circularPath.setTranslation(new Vector3f(radius, 0.0f, 0.0f)); // Radio del círculo

        // Animación circular
        RotationInterpolator rotator = new RotationInterpolator(alpha, tg, circularPath, 0.0f, (float) Math.PI * 2);
        rotator.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        tg.addChild(rotator);
    }

    private TransformGroup createTransformGroup() {
        TransformGroup tg = new TransformGroup();
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        return tg;
    }
    private void addAxisRotation(TransformGroup tg) {
        Transform3D rotationAxis = new Transform3D();
        rotationAxis.setRotation(new AxisAngle4d(0.0, 1.0, 0.0, Math.PI / 2));
        Alpha alpha = new Alpha(-1, 4000);
        RotationInterpolator rotator = new RotationInterpolator(alpha, tg, rotationAxis, 0.0f, (float) Math.PI * 2);
        rotator.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        tg.addChild(rotator);
    }

    private void addCyclicTranslation(TransformGroup tg, Vector3f start, Vector3f end) {
        Transform3D axis = new Transform3D();
        Alpha alpha = new Alpha(-1, 4000);
        PositionInterpolator interpolator = new PositionInterpolator(alpha, tg, axis, start.x, end.x);
        interpolator.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        tg.addChild(interpolator);
    }

    private void addRotation(TransformGroup tg, float direction) {
        Transform3D axis = new Transform3D();
        axis.setRotation(new AxisAngle4d(0.0, 1.0, 0.0, Math.PI / 2));
        Alpha alpha = new Alpha(-1, 3000);
        RotationInterpolator interpolator = new RotationInterpolator(alpha, tg, axis, 0.0f, direction * (float) Math.PI * 2);
        interpolator.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        tg.addChild(interpolator);
    }

    private void addLights(BranchGroup group) {

        DirectionalLight dirLight = new DirectionalLight(new Color3f(1.0f, 1.0f, 1.0f), new Vector3f(-1.0f, -1.0f, -1.0f));
        dirLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        group.addChild(dirLight);

        AmbientLight ambientLight = new AmbientLight(new Color3f(0.2f, 0.2f, 0.2f));
        ambientLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        group.addChild(ambientLight);
    }
    private Appearance createAppearance(Color3f color) {
        Appearance appearance = new Appearance();
        Material material = new Material();
        material.setDiffuseColor(color);
        material.setSpecularColor(new Color3f(1.0f, 1.0f, 1.0f));
        material.setShininess(64.0f);
        appearance.setMaterial(material);
        return appearance;
    }
}