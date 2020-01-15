package com.android.hbind;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * Created by huangbei on 20-1-15.
 */

public class ClassElementsInfo {

    //类
    public TypeElement mTypeElement;
    public int value;
    public String packageName;

    //成员,key为id
    public Map<Integer,VariableElement> mVariableElements = new HashMap<>();

    //方法,key为id
    public Map<Integer,ExecutableElement> mExecutableElements = new HashMap<>();

    //前缀
    public static final String classSuffix = "proxy";

    public String getProxyClassFullName() {
        return mTypeElement.getQualifiedName().toString() + classSuffix;
    }
    public String getClassName() {
        return mTypeElement.getSimpleName().toString() + classSuffix;
    }

    public String generateJavaCode() {
        ClassName viewClass = ClassName.get("android.view","View");
        ClassName clickClass = ClassName.get("android.view","View.OnClickListener");
        ClassName keepClass = ClassName.get("android.support.annotation","Keep");
        ClassName typeClass = ClassName.get(mTypeElement.getQualifiedName().toString().replace("."+mTypeElement.getSimpleName().toString(),""),mTypeElement.getSimpleName().toString());

        //构造方法
        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(typeClass,"host",Modifier.FINAL);
        if(value > 0){
            builder.addStatement("host.setContentView($L)",value);
        }

        //成员
        Iterator<Map.Entry<Integer,VariableElement>> iterator = mVariableElements.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<Integer,VariableElement> entry = iterator.next();
            Integer key = entry.getKey();
            VariableElement value = entry.getValue();
            String name = value.getSimpleName().toString();
            String type = value.asType().toString();
            builder.addStatement("host.$L=($L)host.findViewById($L)",name,type,key);
        }

        //方法
        Iterator<Map.Entry<Integer,ExecutableElement>> iterator1 = mExecutableElements.entrySet().iterator();
        while(iterator1.hasNext()){
            Map.Entry<Integer,ExecutableElement> entry = iterator1.next();
            Integer key = entry.getKey();
            ExecutableElement value = entry.getValue();
            String name = value.getSimpleName().toString();
            MethodSpec onClick = MethodSpec.methodBuilder("onClick")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(viewClass,"view")
                    .addStatement("host.$L(host.findViewById($L))",value.getSimpleName().toString(),key)
                    .returns(void.class)
                    .build();
            //构造匿名内部类
            TypeSpec clickListener = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(clickClass)
                    .addMethod(onClick)
                    .build();
            builder.addStatement("host.findViewById($L).setOnClickListener($L)",key,clickListener);
        }

        TypeSpec typeSpec = TypeSpec.classBuilder(getClassName())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(keepClass)
                .addMethod(builder.build())
                .build();
        JavaFile javaFile = JavaFile.builder(packageName,typeSpec).build();
        try {
            javaFile.writeTo(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return javaFile.toString();
    }

}
