package com.android.hbind;

import com.google.auto.service.AutoService;

import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

/**
 * Created by huangbei on 20-1-15.
 */

@AutoService(Processor.class)
public class CompilerBindProcessor extends AbstractProcessor{

    private Filer mFileUtils;//文件相关的辅助类，负责生成java代码
    private Elements mElementUtils;//元素相关的辅助类，获取元素相关的信息
    private Messager messager;//日志相关的辅助类
    private Map<String,ClassElementsInfo> classElementsInfoMap;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFileUtils = processingEnvironment.getFiler();
        mElementUtils = processingEnvironment.getElementUtils();
        messager = processingEnvironment.getMessager();
        classElementsInfoMap = new HashMap<>();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new LinkedHashSet<>();
        set.add(CompilerBindClick.class.getCanonicalName());
        set.add(CompilerBindView.class.getCanonicalName());
        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        classElementsInfoMap.clear();
        //1.搜集所需要的信息
        collection(roundEnvironment);
        //2.生成具体的代码
        generateClass();
        return true;
    }

    private void collection(RoundEnvironment roundEnvironment){
        //1.搜集compileBindView注解
        Set<? extends Element> set = roundEnvironment.getElementsAnnotatedWith(CompilerBindView.class);
        for(Element element : set){
            //1.1搜集类的注解
            if(element.getKind() == ElementKind.CLASS){
                TypeElement typeElement = (TypeElement)element;
                String classPath = typeElement.getQualifiedName().toString();
                String className = typeElement.getSimpleName().toString();
                String packageName = mElementUtils.getPackageOf(typeElement).getQualifiedName().toString();
                CompilerBindView bindView = element.getAnnotation(CompilerBindView.class);
                if(bindView != null){
                    ClassElementsInfo info = classElementsInfoMap.get(classPath);
                    if(info == null){
                        info = new ClassElementsInfo();
                        classElementsInfoMap.put(classPath,info);
                    }
                    info.packageName = packageName;
                    info.value = bindView.value();
                    info.mTypeElement = typeElement;
                }
            }
            //1.2搜集成员的注解
            else if(element.getKind() == ElementKind.FIELD){
                VariableElement variableElement = (VariableElement) element;
                String classPath = ((TypeElement)element.getEnclosingElement()).getQualifiedName().toString();
                CompilerBindView bindView = variableElement.getAnnotation(CompilerBindView.class);
                if(bindView != null){
                    ClassElementsInfo info = classElementsInfoMap.get(classPath);
                    if(info == null){
                        info = new ClassElementsInfo();
                        classElementsInfoMap.put(classPath,info);
                    }
                    info.mVariableElements.put(bindView.value(),variableElement);
                }
            }
        }

        //2.搜集compileBindClick注解
        Set<? extends Element> set1 = roundEnvironment.getElementsAnnotatedWith(CompilerBindClick.class);
        for(Element element : set1){
            if(element.getKind() == ElementKind.METHOD){
                ExecutableElement executableElement = (ExecutableElement) element;
                String classPath = ((TypeElement)element.getEnclosingElement()).getQualifiedName().toString();
                CompilerBindClick bindClick = executableElement.getAnnotation(CompilerBindClick.class);
                if(bindClick != null){
                    ClassElementsInfo info = classElementsInfoMap.get(classPath);
                    if(info == null){
                        info = new ClassElementsInfo();
                        classElementsInfoMap.put(classPath,info);
                    }
                    int[] values = bindClick.value();
                    for(int value : values) {
                        info.mExecutableElements.put(value,executableElement);
                    }
                }
            }
        }
    }

    private void generateClass(){
        Iterator<Map.Entry<String,ClassElementsInfo>> iterator = classElementsInfoMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,ClassElementsInfo> entry = iterator.next();
            String key = entry.getKey();
            ClassElementsInfo info = entry.getValue();
            JavaFileObject sourceFile = null;
            try{
                sourceFile = mFileUtils.createSourceFile(info.getProxyClassFullName(),info.mTypeElement);
                Writer writer = sourceFile.openWriter();
                writer.write(info.generateJavaCode());
                writer.flush();
                writer.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
