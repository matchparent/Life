package ind.eniac.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

public class AnnoInfo {

    private static final String SUFFIX = "_Life";

    public static final int ONCREATE = 1;
    public static final int ONSTART = 2;
    public static final int ONRESUME = 3;
    public static final int ONPAUSE = 4;
    public static final int ONSTOP = 5;
    public static final int ONDESTROY = 6;

    private String packName;
    private String fullName;
    private String className;

    private List<Meth> createList;
    private List<Meth> startList;
    private List<Meth> resumeList;
    private List<Meth> pauseList;
    private List<Meth> stopList;
    private List<Meth> destroyList;

    private Comparator<Meth> comparator;

    AnnoInfo(Element classElement) {
        this.fullName = ((TypeElement) classElement.getEnclosingElement()).getQualifiedName().toString();
        this.packName = fullName.substring(0, fullName.lastIndexOf("."));
        this.className = fullName.substring(fullName.lastIndexOf(".") + 1);
        this.createList = new ArrayList<>();
        this.startList = new ArrayList<>();
        this.resumeList = new ArrayList<>();
        this.pauseList = new ArrayList<>();
        this.stopList = new ArrayList<>();
        this.destroyList = new ArrayList<>();
        comparator = new Comparator<Meth>() {
            @Override
            public int compare(Meth meth, Meth t1) {
                return meth.position - t1.position;
            }
        };
    }

    public List<Meth> getList(int according) {
        switch (according) {
            case ONCREATE:
                return createList;
            case ONSTART:
                return startList;
            case ONRESUME:
                return resumeList;
            case ONPAUSE:
                return pauseList;
            case ONSTOP:
                return stopList;
            case ONDESTROY:
                return destroyList;
            default:
                return null;
        }
    }

    public void adjust() {
        Collections.sort(createList, comparator);
        Collections.sort(destroyList, comparator);
    }

    public void generate(Filer mFiler) {
        StringBuilder builder = new StringBuilder()
                .append("package ").append(packName).append(";\n\n")
                .append("import android.util.Log;\n")
                .append("import android.view.View;\n")
                .append("import android.arch.lifecycle.Lifecycle;\n")
                .append("import android.arch.lifecycle.LifecycleObserver;\n")
                .append("import android.arch.lifecycle.OnLifecycleEvent;\n")
                .append("import ").append(fullName).append(";\n\n")
                .append("public class ")
                .append(className).append(SUFFIX)
                .append(" {\n\n") // open class
                .append("\tpublic ").append(className).append(SUFFIX)// open method
                .append("(final ").append(className).append(" target) {\n")
                .append("\t\ttarget.getLifecycle().addObserver(new LifecycleObserver() {\n");

        if (createList != null && createList.size() > 0) {
            builder.append("\t\t\t@OnLifecycleEvent(Lifecycle.Event.ON_CREATE)\n")//oncreate
                    .append("\t\t\tpublic void onCreate() {\n");
            publish(builder, ONCREATE);
            builder.append("\t\t\t}\n");
        }
        if (startList != null && startList.size() > 0) {
            builder.append("\t\t\t@OnLifecycleEvent(Lifecycle.Event.ON_START)\n")//oncreate
                    .append("\t\t\tpublic void onStart() {\n");
            publish(builder, ONSTART);
            builder.append("\t\t\t}\n");
        }
        if (resumeList != null && resumeList.size() > 0) {
            builder.append("\t\t\t@OnLifecycleEvent(Lifecycle.Event.ON_RESUME)\n")//oncreate
                    .append("\t\t\tpublic void onResume() {\n");
            publish(builder, ONRESUME);
            builder.append("\t\t\t}\n");
        }
        if (pauseList != null && pauseList.size() > 0) {
            builder.append("\t\t\t@OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)\n")//oncreate
                    .append("\t\t\tpublic void onPause() {\n");
            publish(builder, ONPAUSE);
            builder.append("\t\t\t}\n");
        }
        if (stopList != null && stopList.size() > 0) {
            builder.append("\t\t\t@OnLifecycleEvent(Lifecycle.Event.ON_STOP)\n")//oncreate
                    .append("\t\t\tpublic void onStop() {\n");
            publish(builder, ONSTOP);
            builder.append("\t\t\t}\n");
        }
        if (destroyList != null && destroyList.size() > 0) {
            builder.append("\t\t\t@OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)\n")
                    .append("\t\t\tpublic void onDestroy() {\n");
            publish(builder, ONDESTROY);
            builder.append("\t\t\t}\n");
        }

        builder.append("\t\t});\n");

        builder.append("\t}\n") // close method
                .append("}\n"); // close class

        try { // write the file
            JavaFileObject source = mFiler.createSourceFile(fullName + SUFFIX);
            Writer writer = source.openWriter();
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // Note: calling e.printStackTrace() will print IO errors
            // that occur from the file already existing after its first run, this is normal
        }
    }

    private void publish(StringBuilder builder, int according) {
        for (Meth method : getList(according)) {
            builder.append("\t\t\t\ttarget.").append(method.methodName).append("();").append("\n");
        }
    }

    class Meth {
        private String methodName;
        private int position;

        Meth(String methodName, int position) {
            this.methodName = methodName;
            this.position = position;
        }
    }
}
