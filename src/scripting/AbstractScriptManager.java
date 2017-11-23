
package scripting;

import client.MapleClient;
import java.io.*;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import tools.FileoutputUtil;

/**
 *
 * @author Matze
 */
public abstract class AbstractScriptManager {

    private static final ScriptEngineManager sem = new ScriptEngineManager();

    /**
     *
     * @param path
     * @param c
     * @return
     */
    protected Invocable getInvocable(String path, MapleClient c) {
        return getInvocable(path, c, false);
    }

    /**
     *
     * @param path
     * @param c
     * @param npc
     * @return
     */
    protected Invocable getInvocable(String path, MapleClient c, boolean npc) {
        InputStream fr = null;
        try {

            path = "scripts/" + path;
            ScriptEngine engine = null;

            if (c != null) {
                engine = c.getScriptEngine(path);
            }
            if (engine == null) {
                File scriptFile = new File(path);
                if (!scriptFile.exists()) {
                    return null;
                }
                engine = sem.getEngineByName("javascript");

                if (c != null) {
                    c.setScriptEngine(path, engine);
                }
                fr = new FileInputStream(scriptFile);
                BufferedReader bf = new BufferedReader(new InputStreamReader(fr, EncodingDetect.getJavaEncode(scriptFile)));

                engine.eval(bf);
            } else if (c != null && npc) {
                c.getPlayer().dropMessage(5, "你现在已经假死请使用@ea");
            }
            return (Invocable) engine;
        } catch (FileNotFoundException | UnsupportedEncodingException | ScriptException e) {
            System.err.println("Error executing script. Path: " + path + "\nException " + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing script. Path: " + path + "\nException " + e);
            return null;
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException ignore) {
            }
        }
    }
}
