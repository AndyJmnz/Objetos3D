package Practicas;

import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.image.TextureLoader;

import javax.media.j3d.*;
import javax.swing.*;
import java.awt.*;
import javax.vecmath.*;


public class SistemaSolar extends JPanel {
    private Canvas3D canvas;

    public SistemaSolar() {
        setLayout(new BorderLayout());
        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        add(canvas, BorderLayout.CENTER);

        SimpleUniverse universe = new SimpleUniverse(canvas);

        TransformGroup viewTransform = universe.getViewingPlatform().getViewPlatformTransform();
        Transform3D cameraPosition = new Transform3D();
        cameraPosition.setTranslation(new Vector3d(0.0, 0.0, 25.0));
        viewTransform.setTransform(cameraPosition);

        BranchGroup scene = createSceneGraph();
        universe.getViewingPlatform().setNominalViewingTransform();
        universe.addBranchGraph(scene);
    }

    private BranchGroup createSceneGraph() {
        BranchGroup group = new BranchGroup();

        TextureLoader bgTexture = new TextureLoader("src/img/universe.jpg", null);
        Background background = new Background(bgTexture.getImage());
        background.setApplicationBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        group.addChild(background);

        TransformGroup sunTransform = createRotatingSphere("src/img/sun.jpg", 0.1f, 5000);
        group.addChild(sunTransform);

        TransformGroup planet1Orbit = createOrbit(
                createRotatingSphere("src/img/eart.jpg", 0.05f, 2000),
                0.5,
                5000
        );
        group.addChild(planet1Orbit);

        TransformGroup planet2Orbit = createOrbit(
                createRotatingSphere("src/img/marts.jpg", 0.04f, 3000),
                0.75,
                8000
        );
        group.addChild(planet2Orbit);

        group.addChild(create3DText("Solar System"));

        addLighting(group);

        return group;
    }

    private Node create3DText(String text) {
        Font3D font3D = new Font3D(new Font("Arial", Font.BOLD, 1), new FontExtrusion());
        Text3D textGeom = new Text3D(font3D, text, new Point3f(0.0f, 0.0f, 0.0f));
        textGeom.setAlignment(Text3D.ALIGN_CENTER);

        Appearance textAppearance = new Appearance();
        ColoringAttributes textColor = new ColoringAttributes();
        textColor.setColor(new Color3f(0.721f, 0.745f, 0.867f));
        textAppearance.setColoringAttributes(textColor);

        Shape3D textShape = new Shape3D(textGeom, textAppearance);

        Transform3D scaleTransform = new Transform3D();
        scaleTransform.setScale(0.08);
        Transform3D positionTransform = new Transform3D();
        positionTransform.setTranslation(new Vector3d(0.0, 1.5, 0.0));

        scaleTransform.mul(positionTransform);
        TransformGroup textGroup = new TransformGroup(scaleTransform);

        textGroup.addChild(textShape);

        DirectionalLight textLight = new DirectionalLight(
                new Color3f(1.0f, 1.0f, 1.0f),
                new Vector3f(0.0f, -1.0f, -1.0f)
        );
        textLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 50.0));
        textGroup.addChild(textLight);

        return textGroup;
    }

    private TransformGroup createRotatingSphere(String texturePath, float radius, long duration) {
        Appearance appearance = new Appearance();
        Texture texture = new TextureLoader(texturePath, null).getTexture();
        appearance.setTexture(texture);

        Sphere sphere = new Sphere(radius, Sphere.GENERATE_TEXTURE_COORDS, 100, appearance);

        TransformGroup rotationGroup = new TransformGroup();
        rotationGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Alpha rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, duration, 0, 0, 0, 0, 0);
        RotationInterpolator rotator = new RotationInterpolator(rotationAlpha, rotationGroup);
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 50.0);
        rotator.setSchedulingBounds(bounds);

        rotationGroup.addChild(sphere);
        rotationGroup.addChild(rotator);

        return rotationGroup;
    }

    private TransformGroup createOrbit(TransformGroup rotatingSphere, double radius, long duration) {
        TransformGroup orbitGroup = new TransformGroup();
        orbitGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Alpha orbitAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, duration, 0, 0, 0, 0, 0);
        RotationInterpolator orbitRotator = new RotationInterpolator(orbitAlpha, orbitGroup);
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 50.0);
        orbitRotator.setSchedulingBounds(bounds);

        Transform3D translation = new Transform3D();
        translation.setTranslation(new Vector3d(radius, 0.0, 0.0));
        TransformGroup translationGroup = new TransformGroup(translation);

        translationGroup.addChild(rotatingSphere);
        orbitGroup.addChild(translationGroup);
        orbitGroup.addChild(orbitRotator);

        return orbitGroup;
    }

    private void addLighting(BranchGroup group) {
        DirectionalLight light = new DirectionalLight(new Color3f(1.0f, 1.0f, 1.0f), new Vector3f(-1.0f, -1.0f, -1.0f));
        light.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 50.0));
        group.addChild(light);

        AmbientLight ambientLight = new AmbientLight(new Color3f(0.3f, 0.3f, 0.3f));
        ambientLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 50.0));
        group.addChild(ambientLight);
    }
}