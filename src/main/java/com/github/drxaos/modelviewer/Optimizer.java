package com.github.drxaos.modelviewer;


import com.jme3.material.Material;
import com.jme3.math.Transform;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import jme3tools.optimize.GeometryBatchFactory;

import java.util.*;

public class Optimizer {


    public static Node optimize(Node scene, boolean useLods) {
        Transform transform = scene.getLocalTransform();
        scene.setLocalTransform(Transform.IDENTITY);

        ArrayList<Geometry> geoms = new ArrayList<>();

        GeometryBatchFactory.gatherGeoms(scene, geoms);

        List<Geometry> batchedGeoms = makeBatches(geoms, useLods);
        for (Geometry geom : batchedGeoms) {
            scene.attachChild(geom);
        }

        for (Iterator<Geometry> it = geoms.iterator(); it.hasNext(); ) {
            Geometry geometry = it.next();
            geometry.removeFromParent();
        }

        scene.setLocalTransform(transform);
        return scene;
    }

    public static List<Geometry> makeBatches(Collection<Geometry> geometries, boolean useLods) {
        ArrayList<Geometry> retVal = new ArrayList<>();
        HashMap<Material, List<Geometry>> matToGeom = new HashMap<Material, List<Geometry>>();

        for (Geometry geom : geometries) {
            List<Geometry> outList = matToGeom.get(geom.getMaterial());
//            if (outList == null) {
//                //trying to compare materials with the contentEquals method
//                for (Material mat : matToGeom.keySet()) {
//                    if (geom.getMaterial().contentEquals(mat)) {
//                        outList = matToGeom.get(mat);
//                    }
//                }
//            }
            if (outList == null) {
                outList = new ArrayList<Geometry>();
                matToGeom.put(geom.getMaterial(), outList);
            }
            outList.add(geom);
        }

        int batchNum = 0;
        for (Map.Entry<Material, List<Geometry>> entry : matToGeom.entrySet()) {
            Material mat = entry.getKey();
            List<Geometry> geomsForMat = entry.getValue();
            Mesh mesh = new Mesh();
            GeometryBatchFactory.mergeGeometries(geomsForMat, mesh);
            // lods
            if (useLods) {
                GeometryBatchFactory.makeLods(geomsForMat, mesh);
            }
            mesh.updateCounts();

            Geometry out = new Geometry("batch[" + (batchNum++) + "]", mesh);
            out.setMaterial(mat);
            out.updateModelBound();
            out.setShadowMode(geomsForMat.get(0).getShadowMode());
            out.setCullHint(geomsForMat.get(0).getCullHint());
            retVal.add(out);
        }

        return retVal;
    }
}
