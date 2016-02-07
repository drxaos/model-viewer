package com.github.drxaos.modelviewer;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

import java.util.ArrayList;
import java.util.List;

public class JmeUtils {

    private static class BoxGeoPair {

        public Box box;
        public Geometry geo;
    }

    private static final List<BoxGeoPair> mBoxCache = new ArrayList<BoxGeoPair>();

    public static Material generateTransparentMaterial(AssetManager am) {
        Material transparent = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        transparent.setTransparent(true);
        transparent.setColor("Color", new ColorRGBA(1, 1, 1, 0.0f));
        transparent.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        return transparent;
    }

    public static List<Material> findMaterials(Node root, String name) {
        List<Material> list = new ArrayList<Material>();
        findMaterials(root, name, list);
        return list;
    }

    private static void findMaterials(Node root, String name, List<Material> list) {
        for (Spatial child : root.getChildren()) {
            if (child instanceof Geometry && (name == null || name.equals(((Geometry) child).getMaterial().getName()))) {
                list.add(((Geometry) child).getMaterial());
            } else if (child instanceof Node) {
                findMaterials((Node) child, name, list);
            }
        }
    }

    public static List<Geometry> findGeometryByMaterial(Node root, String materialName) {
        List<Geometry> list = new ArrayList<Geometry>();
        findGeometryByMaterial(root, materialName, list);
        return list;
    }

    private static void findGeometryByMaterial(Node root, String name, List<Geometry> list) {
        for (Spatial child : root.getChildren()) {
            if (child instanceof Geometry && (name == null || name.equals(((Geometry) child).getMaterial().getName()))) {
                list.add(((Geometry) child));
            } else if (child instanceof Node) {
                findGeometryByMaterial((Node) child, name, list);
            }
        }
    }

    public static Geometry generateInvisibleBox(AssetManager am, Box box) {
        BoxGeoPair cache = null;
        for (BoxGeoPair pair : mBoxCache) {
            if (pair.box.equals(box)) {
                cache = pair;
            }
        }

        Geometry output;
        if (cache == null) {
            output = new Geometry("Custom_Col_Box", box);
            output.setQueueBucket(RenderQueue.Bucket.Sky);
            output.setMaterial(generateTransparentMaterial(am));
            output.setShadowMode(RenderQueue.ShadowMode.Off);

            BoxGeoPair cachePair = new BoxGeoPair();
            cachePair.geo = output;
            cachePair.box = box;
            mBoxCache.add(cachePair);
        } else {
            output = cache.geo.clone();
        }

        return output;
    }

    public static float degreesToRad(float rad) {
        return rad * 3.14159f / 180.0f;
    }
}
