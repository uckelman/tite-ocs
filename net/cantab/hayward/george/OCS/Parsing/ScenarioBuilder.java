/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.cantab.hayward.george.OCS.Parsing;

import VASSAL.Info;
import VASSAL.build.GameModule;
import VASSAL.command.Command;
import VASSAL.launch.BasicModule;
import VASSAL.launch.EditorWindow;
import VASSAL.launch.Launcher;
import VASSAL.launch.PlayerWindow;
import VASSAL.tools.ArchiveWriter;
import VASSAL.tools.icon.IconFactory;
import VASSAL.tools.menu.MacOSXMenuManager;
import VASSAL.tools.menu.MenuBarProxy;
import VASSAL.tools.menu.MenuManager;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import net.cantab.hayward.george.OCS.Statics;

/**
 *
 * @author george
 */
public class ScenarioBuilder extends Launcher {

    static File load;
    static File text;
    static File save;

    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("Bad number of arguments " + args.length);
            System.exit(0);
        }
        load = new File(args[1]);
        text = new File(args[2]);
        save = new File(args[3]);
        new ScenarioBuilder(new String[]{args[0], "--standalone"});
    }

    protected ScenarioBuilder(String[] args) {
        // the ctor is protected to enforce that it's called via main()
        super(args);
    }

    protected MenuManager createMenuManager() {
        return Info.isMacOSX() ? new MacOSXMenuManager() : new MyMenuManager();
    }

    protected void launch()
            throws IOException {
        new IconFactory(); // initialsie the icon factory
        final ArchiveWriter archive = new ArchiveWriter(new ZipFile(
                lr.module.getPath()));
        GameModule.init(new BasicModule(archive));
        Command c = GameModule.getGameModule().getGameState().decodeSavedGame(
                load);
        c.execute();
        Statics.readingTextFile = true;
        ParseText p = new ParseText(text);
        p.parse();
        Statics.readingTextFile = false;
        try {
            GameModule.getGameModule().getGameState().saveGame(save);
        } catch (IOException e) {
        }

        System.exit(0);
    }

    private static class MyMenuManager extends MenuManager {

        private final MenuBarProxy editorBar = new MenuBarProxy();
        private final MenuBarProxy playerBar = new MenuBarProxy();

        @Override
        public JMenuBar getMenuBarFor(JFrame fc) {
            if (fc instanceof PlayerWindow) {
                return playerBar.createPeer();
            } else if (fc instanceof EditorWindow) {
                return editorBar.createPeer();
            } else {
                return null;
            }
        }

        @Override
        public MenuBarProxy getMenuBarProxyFor(JFrame fc) {
            if (fc instanceof PlayerWindow) {
                return playerBar;
            } else if (fc instanceof EditorWindow) {
                return editorBar;
            } else {
                return null;
            }
        }
    }
}
