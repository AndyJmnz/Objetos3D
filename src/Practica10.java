import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.universe.SimpleUniverse;
import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import java.awt.*;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.*;

public class Practica10 extends JPanel {

    public Practica10() {
        setLayout(new GridLayout(1, 2));

        Canvas3D canvas1 = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        add(canvas1);
        SimpleUniverse universe1 = new SimpleUniverse(canvas1);
        BranchGroup group1 = createCylinderScene();
        universe1.getViewingPlatform().setNominalViewingTransform();
        universe1.addBranchGraph(group1);


        Canvas3D canvas2 = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        add(canvas2);
        SimpleUniverse universe2 = new SimpleUniverse(canvas2);
        BranchGroup group2 = createCubeScene();
        universe2.getViewingPlatform().setNominalViewingTransform();
        universe2.addBranchGraph(group2);
    }

    private BranchGroup createCylinderScene() {
        BranchGroup group = new BranchGroup();

        Background background = new Background(new Color3f(0.5f, 0.8f, 1.0f));
        background.setApplicationBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        group.addChild(background);

        TransformGroup circularTransformGroup = createTransformGroup();
        group.addChild(circularTransformGroup);

        Appearance aparienciaCilindro = new Appearance();
        Material materialCilindro = new Material();
        materialCilindro.setAmbientColor(new Color3f(0.2f, 0.1f, 0.0f));
        materialCilindro.setDiffuseColor(new Color3f(0.984f, 0.522f, 0.0f));
        materialCilindro.setSpecularColor(new Color3f(1.0f, 0.8f, 0.5f));
        materialCilindro.setShininess(64.0f);
        aparienciaCilindro.setMaterial(materialCilindro);

        Cylinder cilindro = new Cylinder(0.2f, 0.4f, aparienciaCilindro);

        TransformGroup rotationTransformGroup = createTransformGroup();
        rotationTransformGroup.addChild(cilindro);
        addSelfRotation(rotationTransformGroup);

        circularTransformGroup.addChild(rotationTransformGroup);
        addCircularMotion(circularTransformGroup, 0.5f, new Vector3f(0.0f, 0.0f, 0.0f));

        addLights(group);

        return group;
    }

    private BranchGroup createCubeScene() {
        BranchGroup group = new BranchGroup();

        Background background = new Background(new Color3f(0.5f, 0.8f, 1.0f)); // Azul claro
        background.setApplicationBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        group.addChild(background);

        TransformGroup circularTransformGroup = createTransformGroup();
        group.addChild(circularTransformGroup);

        Appearance aparienciaCubo = createAppearance(new Color3f(0.0f, 1.0f, 0.0f));
        Box cubo = new Box(0.2f, 0.2f, 0.2f, Box.GENERATE_NORMALS, aparienciaCubo);

        TransformGroup rotationTransformGroup = createTransformGroup();
        rotationTransformGroup.addChild(cubo);
        addSelfRotation(rotationTransformGroup);

        circularTransformGroup.addChild(rotationTransformGroup);
        addCircularMotion(circularTransformGroup, -0.5f, new Vector3f(0.0f, 0.0f, 0.0f));

        addLights(group);

        return group;
    }

    private void addCircularMotion(TransformGroup tg, float radius, Vector3f offset) {
        Alpha alpha = new Alpha(-1, 5000);
        CircularMotionBehavior motionBehavior = new CircularMotionBehavior(tg, radius, offset, alpha);
        motionBehavior.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        tg.addChild(motionBehavior);
    }

    private void addSelfRotation(TransformGroup tg) {
        Transform3D rotationAxis = new Transform3D();
        Alpha alpha = new Alpha(-1, 5000);
        RotationInterpolator rotator = new RotationInterpolator(alpha, tg, rotationAxis, 0.0f, (float) Math.PI * 2);
        rotator.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        tg.addChild(rotator);
    }

    private TransformGroup createTransformGroup() {
        TransformGroup tg = new TransformGroup();
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        return tg;
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

    private void addLights(BranchGroup group) {
        DirectionalLight dirLight = new DirectionalLight(new Color3f(1.0f, 1.0f, 1.0f), new Vector3f(-1.0f, -1.0f, -1.0f));
        dirLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        group.addChild(dirLight);

        AmbientLight ambientLight = new AmbientLight(new Color3f(0.2f, 0.2f, 0.2f));
        ambientLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        group.addChild(ambientLight);
    }

    class CircularMotionBehavior extends Behavior {
        private TransformGroup targetTG;
        private float radius;
        private Vector3f offset;
        private Alpha alpha;
        private Transform3D transform = new Transform3D();

        public CircularMotionBehavior(TransformGroup targetTG, float radius, Vector3f offset, Alpha alpha) {
            this.targetTG = targetTG;
            this.radius = radius;
            this.offset = offset;
            this.alpha = alpha;
        }

        @Override
        public void initialize() {
            wakeupOn(new WakeupOnElapsedFrames(0));
        }

        @Override
        public void processStimulus(java.util.Enumeration criteria) {
            double angle = alpha.value() * Math.PI * 2;
            float x = offset.x + (float) (radius * Math.cos(angle));
            float z = offset.z + (float) (radius * Math.sin(angle));

            transform.setTranslation(new Vector3f(x, offset.y, z));
            targetTG.setTransform(transform);

            wakeupOn(new WakeupOnElapsedFrames(0));
        }
    }
}
