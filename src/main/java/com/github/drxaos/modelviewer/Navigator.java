package com.github.drxaos.modelviewer;

import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.*;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

public class Navigator implements RawInputListener {
    App app;
    InputManager inputManager;
    Camera cam;
    Node rootNode;

    boolean ldown = false;
    boolean rdown = false;
    boolean dragging = false;
    boolean rotating = false;
    long clickTime = 0;
    private Vector2f clickPos = new Vector2f();

    private Vector3f camLookAt = new Vector3f();
    private float camDistance = 1;
    private float camYaw = 0;
    private float camRoll = 0;

    private Vector3f toCamLookAt = new Vector3f();
    private float toCamDistance = 1;
    private float toCamYaw = 0;
    private float toCamRoll = 0;

    private float boundX = 100;
    private float boundZ = 100;

    private Vector3f camLocation = new Vector3f();
    private Quaternion camRotation = new Quaternion();
    private Vector3f camForward = new Vector3f(0, 0, 0);
    private Vector2f drag = new Vector2f();
    private boolean enabled = true;

    public Navigator(App app) {
        this.app = app;
        this.inputManager = app.getInputManager();
        this.cam = app.getCamera();
        this.rootNode = app.getRootNode();
    }

    public void move(float x, float z, float roll, float yaw, float distance) {
        toCamLookAt.setX(x);
        toCamLookAt.setZ(z);
        toCamRoll = roll;
        toCamYaw = yaw;
        toCamDistance = distance;
    }

    public void setBounds(float boundX, float boundZ) {
        this.boundX = boundX;
        this.boundZ = boundZ;
    }

    public void lookAt(float x, float z) {
        camLookAt.setX(x);
        camLookAt.setZ(z);
    }

    public void setCamRoll(float camRoll) {
        this.camRoll = camRoll;
    }

    public void setCamYaw(float camYaw) {
        this.camYaw = camYaw;
    }

    public void setCamDistance(float camDistance) {
        this.camDistance = camDistance;
    }

    public void updateCam() {
        if (toCamYaw < -FastMath.PI / 2) {
            toCamYaw = -FastMath.PI / 2;
        }
        if (toCamYaw > FastMath.PI / 2) {
            toCamYaw = FastMath.PI / 2;
        }
//        if (toCamDistance > (boundX + boundZ) * 3) {
//            toCamDistance = (boundX + boundZ) * 3;
//        }
        if (toCamDistance < 0.3f) {
            toCamDistance = 0.3f;
        }
//        if (toCamDistance > 900) {
//            toCamDistance = 900;
//        }
//        if (toCamLookAt.getX() < 0) {
//            toCamLookAt.setX(0);
//        }
//        if (toCamLookAt.getZ() < 0) {
//            toCamLookAt.setZ(0);
//        }
//        if (toCamLookAt.getX() > boundX) {
//            toCamLookAt.setX(boundX);
//        }
//        if (toCamLookAt.getZ() > boundZ) {
//            toCamLookAt.setZ(boundZ);
//        }
        toCamLookAt.set(0, 0, 0);

        camDistance = (camDistance * 2 + toCamDistance) / 3;
        camRoll = (camRoll * 2 + toCamRoll) / 3;
        camYaw = (camYaw * 2 + toCamYaw) / 3;
        camLookAt.addLocal(camLookAt).addLocal(toCamLookAt).multLocal(1f / 3);

        camForward.setZ(camDistance);
        camRotation.fromAngles(-camYaw, camRoll, 0);
        camLocation.set(camLookAt).addLocal(camRotation.mult(camForward));
        cam.setLocation(camLocation);
        cam.lookAt(camLookAt, Vector3f.UNIT_Y);
    }

    @Override
    public void beginInput() {

    }

    @Override
    public void endInput() {

    }

    @Override
    public void onJoyAxisEvent(JoyAxisEvent evt) {

    }

    @Override
    public void onJoyButtonEvent(JoyButtonEvent evt) {

    }

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt) {
        if (!enabled) {
            return;
        }

        if (ldown) {
            dragging = true;
            inputManager.setCursorVisible(false);
            drag.set(-evt.getDX(), evt.getDY()).multLocal(camDistance / 1500);
            drag.rotateAroundOrigin(camRoll, true);
            toCamLookAt.addLocal(drag.x, 0, drag.y);
        } else if (rdown) {
            rotating = true;
            inputManager.setCursorVisible(false);
            toCamRoll += -0.008f * evt.getDX();
            toCamYaw += -0.002f * evt.getDY();
        }


        if (evt.getDeltaWheel() != 0) {
            float delta = 1f * evt.getDeltaWheel() / 120f;
            toCamDistance *= 1 - delta * 0.05;
        }
    }

    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt) {
        if (!enabled) {
            return;
        }
        if (evt.isPressed()) {
            if (evt.getButtonIndex() == MouseInput.BUTTON_RIGHT && !rdown) {
                ldown = true;
            }
            if (evt.getButtonIndex() == MouseInput.BUTTON_LEFT && !ldown) {
                rdown = true;
            }
        }
        if (evt.isReleased()) {
            if (evt.getButtonIndex() == MouseInput.BUTTON_RIGHT) {
                ldown = false;
            }
            if (evt.getButtonIndex() == MouseInput.BUTTON_LEFT) {
                rdown = false;
            }
            dragging = false;
            rotating = false;
            inputManager.setCursorVisible(true);
        }
    }

    @Override
    public void onKeyEvent(KeyInputEvent evt) {

    }

    @Override
    public void onTouchEvent(TouchEvent evt) {

    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
