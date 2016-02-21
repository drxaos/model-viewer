package com.github.drxaos.modelviewer;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.util.JmeFormatter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Callable;
import java.util.logging.*;

public class Main {

    private static JmeCanvasContext context;
    private static Canvas canvas;
    private static App app;
    private static JFrame frame;
    private static Container generalCanvasPanel, controlsPanel;
    private static JSplitPane splitPane;
    private static JTextArea textArea;
    static TextAreaOutputStream logStream;

    private static void createTabs() {
        generalCanvasPanel = new JPanel();
        generalCanvasPanel.setLayout(new BorderLayout());

        controlsPanel = new JPanel();
        controlsPanel.setLayout(new BorderLayout());

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, generalCanvasPanel, controlsPanel);
        splitPane.setPreferredSize(new Dimension(1024, 768));
        splitPane.setOneTouchExpandable(false);
        splitPane.setDividerLocation(768);

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        controlsPanel.add(scrollPane, BorderLayout.CENTER);
        logStream.setTextArea(textArea);

        Dimension minimumSize = new Dimension(100, 50);
        generalCanvasPanel.setMinimumSize(minimumSize);
        controlsPanel.setMinimumSize(minimumSize);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(splitPane, BorderLayout.CENTER);
    }

    private static void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        final JMenuItem itemOpenModel = new JMenuItem("Open model");
        fileMenu.add(itemOpenModel);
        itemOpenModel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final FileDialog fdlg = new FileDialog(frame, "Open file", FileDialog.LOAD);
                fdlg.setVisible(true);
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            if (fdlg.getFile() == null) {
                                return;
                            }
                            String modelName = fdlg.getDirectory() + fdlg.getFile();
                            Logger.getLogger("App").info("Loading model: " + modelName);
                            app.loadModel(modelName);
                            Logger.getLogger("App").info("Model loaded");
                        } catch (Throwable t) {
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            t.printStackTrace(pw);

                            Logger.getLogger("App").severe(sw.toString());

                            JTextArea textArea = new JTextArea(6, 25);
                            textArea.setText(sw.toString());
                            textArea.setEditable(false);
                            JScrollPane scrollPane = new JScrollPane(textArea);
                            JOptionPane.showMessageDialog(frame, scrollPane);
                        }
                    }
                }.start();
            }
        });

        final JMenuItem itemExit = new JMenuItem("Exit");
        fileMenu.add(itemExit);
        itemExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private static void createFrame() {
        frame = new JFrame("Model Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                app.stop();
            }
        });

        createTabs();
        createMenu();
    }

    public static void createCanvas() {
        AppSettings settings = new AppSettings(true);
        settings.setWidth(768);
        settings.setHeight(768);
        settings.setFrameRate(30);
        settings.setDepthBits(24);
        settings.setBitsPerPixel(24);
        settings.setSamples(8);

        app = new App();
        app.setPauseOnLostFocus(false);
        app.setSettings(settings);
        app.createCanvas();
        app.startCanvas();

        context = (JmeCanvasContext) app.getContext();
        canvas = context.getCanvas();
        canvas.setSize(settings.getWidth(), settings.getHeight());
    }

    public static void startApp() {
        app.startCanvas();
        app.enqueue(new Callable<Void>() {
            public Void call() {
                if (app instanceof SimpleApplication) {
                    SimpleApplication simpleApp = (SimpleApplication) app;
                    //simpleApp.getFlyByCamera().setDragToRotate(true);
                }
                return null;
            }
        });

    }

    public static void main(String[] args) {
        JmeFormatter formatter = new JmeFormatter();
        logStream = new TextAreaOutputStream();
        Handler textAreaHandler = new StreamHandler(logStream, formatter) {
            @Override
            public void publish(LogRecord record) {
                super.publish(record);
                flush();
            }
        };
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        Logger.getLogger("").removeHandler(Logger.getLogger("").getHandlers()[0]);
        Logger.getLogger("").addHandler(textAreaHandler);
        Logger.getLogger("").addHandler(consoleHandler);

        createCanvas();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JPopupMenu.setDefaultLightWeightPopupEnabled(false);

                createFrame();

                generalCanvasPanel.add(canvas, BorderLayout.CENTER);

                frame.pack();
                startApp();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

}