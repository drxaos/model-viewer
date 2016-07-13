package com.github.drxaos.modelviewer;

import com.jme3.asset.AssetManager;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

public class StaticModel extends Node {
    protected AssetManager am;
    protected boolean fresh = false;
    protected String meshName, subname;

    public StaticModel(AssetManager am, String meshName, String subname, String objectName) {
        this.am = am;
        this.meshName = meshName;
        this.subname = subname;

        Spatial model = am.loadModel(meshName);
        fixLighting(model);
        this.attachChild(model);

        this.name = objectName;
    }

    protected void applyModel() {
        ModelCache.getInstance().putModel(subname == null ? meshName : meshName + "#" + subname, this.clone(false));
        fresh = false;
    }

    protected void fixLighting(Spatial spatial) {
        if (spatial instanceof Geometry) {
            spatial.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
            Material material = ((Geometry) spatial).getMaterial();
            if (material.getParam("UseMaterialColors") != null) {
                material.setBoolean("UseMaterialColors", true);
                material.setColor("Ambient", (ColorRGBA) material.getParam("Diffuse").getValue());
//                material.setColor("Diffuse", ColorRGBA.White.clone());
//                material.setColor("Specular", ColorRGBA.White.mult(0.1f));
                material.setFloat("Shininess", 0.1f);

                MatParamTexture diffuseMap = material.getTextureParam("DiffuseMap");
                if (diffuseMap != null) {
                    diffuseMap.getTextureValue().setMinFilter(Texture.MinFilter.Trilinear);
                    diffuseMap.getTextureValue().setMagFilter(Texture.MagFilter.Bilinear);
                }
            }
        }
        if (spatial instanceof Node) {
            for (Spatial child : ((Node) spatial).getChildren()) {
                fixLighting(child);
            }
        }
    }
}
