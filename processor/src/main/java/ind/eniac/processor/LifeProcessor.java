package ind.eniac.processor;

import com.google.auto.service.AutoService;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import ind.eniac.annotation.onCreate;
import ind.eniac.annotation.onDestroy;
import ind.eniac.annotation.onPause;
import ind.eniac.annotation.onResume;
import ind.eniac.annotation.onStart;
import ind.eniac.annotation.onStop;

@AutoService(Processor.class)
public class LifeProcessor extends AbstractProcessor {

    private Filer mFiler;
    private Messager mMessager;

    private HashMap<String, AnnoInfo> annoMap;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
        annoMap = new HashMap<>();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        //支持的java版本
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(onCreate.class.getCanonicalName());
        annotations.add(onStart.class.getCanonicalName());
        annotations.add(onResume.class.getCanonicalName());
        annotations.add(onPause.class.getCanonicalName());
        annotations.add(onStop.class.getCanonicalName());
        annotations.add(onDestroy.class.getCanonicalName());
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        parsePair(roundEnvironment, AnnoInfo.ONCREATE, onCreate.class);
        parsePair(roundEnvironment, AnnoInfo.ONCREATE, onStart.class);
        parsePair(roundEnvironment, AnnoInfo.ONCREATE, onResume.class);
        parsePair(roundEnvironment, AnnoInfo.ONCREATE, onPause.class);
        parsePair(roundEnvironment, AnnoInfo.ONCREATE, onStop.class);
        parsePair(roundEnvironment, AnnoInfo.ONDESTROY, onDestroy.class);

        for (AnnoInfo anno : annoMap.values()) {
            anno.adjust();
            anno.generate(mFiler);
        }
        return false;
    }

    private void parsePair(RoundEnvironment roundEnvironment, int according, Class<? extends Annotation> clazz) {
        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(clazz)) {
            if (annotatedElement.getKind() != ElementKind.METHOD) {
                mMessager.printMessage(
                        Diagnostic.Kind.ERROR, annotatedElement.getSimpleName());
                return;
            }
            analysisAnnotated(according, annotatedElement);
        }
    }

    private int returnPos(int according, Element classElement) {
        switch (according) {
            case AnnoInfo.ONCREATE:
                return classElement.getAnnotation(onCreate.class).pos();
            case AnnoInfo.ONSTART:
                return classElement.getAnnotation(onStart.class).pos();
            case AnnoInfo.ONRESUME:
                return classElement.getAnnotation(onResume.class).pos();
            case AnnoInfo.ONPAUSE:
                return classElement.getAnnotation(onPause.class).pos();
            case AnnoInfo.ONSTOP:
                return classElement.getAnnotation(onStop.class).pos();
            case AnnoInfo.ONDESTROY:
                return classElement.getAnnotation(onDestroy.class).pos();
            default:
                return 0;
        }
    }

    private void analysisAnnotated(int according, Element classElement) {
        String fullName = ((TypeElement) classElement.getEnclosingElement()).getQualifiedName().toString();
        int pos = returnPos(according,classElement);

        AnnoInfo anno = annoMap.get(fullName);
        if (anno == null) {
            anno = new AnnoInfo(classElement);
            annoMap.put(fullName, anno);
        }
        anno.getList(according).add(anno.new Meth(classElement.getSimpleName().toString(), pos));
    }

}
