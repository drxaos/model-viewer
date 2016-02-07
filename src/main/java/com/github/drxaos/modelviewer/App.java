package com.github.drxaos.modelviewer;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.asset.AssetEventListener;
import com.jme3.asset.AssetKey;
import com.jme3.asset.TextureKey;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;

import java.io.File;

public class App extends SimpleApplication {

    private Node mSceneNode;
    private Lights mLights;
    private Navigator navigator;

    public App() {
        super(new AppState[0]);
        mLights = new Lights();
    }

    @Override
    public void simpleInitApp() {
        inputManager.clearMappings();
        setPauseOnLostFocus(false);

        AssetEventListener asl = new AssetEventListener() {
            public void assetLoaded(AssetKey key) {
            }

            public void assetRequested(AssetKey key) {
                if (key.getExtension().equals("png") || key.getExtension().equals("jpg") || key.getExtension().equals("dds")) {
                    TextureKey tkey = (TextureKey) key;
                    tkey.setAnisotropy(8);
                    tkey.setGenerateMips(true);
                }
            }

            public void assetDependencyNotFound(AssetKey parentKey, AssetKey dependentAssetKey) {
            }
        };
        assetManager.addAssetEventListener(asl);

        viewPort.setBackgroundColor(ColorRGBA.DarkGray);

        cam.setLocation(new Vector3f(15, 30, 100));
        cam.lookAt(new Vector3f(15, 0, 100), cam.getUp());
        inputManager.setCursorVisible(true);

        mSceneNode = new Node("Scene");
        rootNode.attachChild(mSceneNode);

        mLights.setLights(this);

        inputManager.addRawInputListener(navigator = new Navigator(this));
    }

    public void loadModel(String path) {
        ModelCache.getInstance().clear();
        mSceneNode.detachAllChildren();

        assetManager.registerLocator("/", FileSystemLocator.class);
        File f = new File(path);
        StaticModel model = new StaticModel(assetManager, f.getAbsolutePath().replace("\\", "/"), null, "model");
        getSceneNode().attachChild(model);
        BoundingVolume bound = model.getWorldBound();
        if (bound instanceof BoundingBox) {
            Vector3f b = new Vector3f();
            ((BoundingBox) bound).getExtent(b);
            navigator.move(0, 0, 0, 0, b.x + b.y + b.z);
        }
        assetManager.unregisterLocator("/", FileSystemLocator.class);
    }

    public Node getSceneNode() {
        return mSceneNode;
    }

    @Override
    public void simpleUpdate(float tpf) {
        navigator.updateCam();
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }
}
