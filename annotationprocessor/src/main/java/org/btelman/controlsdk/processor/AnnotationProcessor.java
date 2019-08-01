package org.btelman.controlsdk.processor;

import com.google.auto.service.AutoService;
import org.btelman.controlsdk.annotations.HardwareTranslator;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {
    private JavaFileObject obj;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>() {{
            add(HardwareTranslator.class.getCanonicalName());
        }};
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public void init(ProcessingEnvironment processingEnvironment) {
        try {
            obj = processingEnvironment.getFiler().createClassFile("org.btelman.controlsdk.bindings");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //TODO put all annotations found in a class
        return true;
    }
}
