package com.jason.datacollection.aspect;

import com.jason.datacollection.annotation.ApplyRepositoryPool;
import com.jason.datacollection.annotation.RepositoryId;
import com.jason.datacollection.annotation.RepositoryType;
import com.jason.datacollection.core.pool.ObjectPool;
import com.jason.datacollection.core.pool.RepositoryObject;
import com.jason.datacollection.core.pool.RepositoryObjectPool;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @ClassName RepositoryObjectPoolAspect
 * @Description
 * @Author Leslie Hwang
 * @Email hwangxiaosi@gmail.com
 * @Date 2021/12/10 15:04
 **/
@Aspect
@Component
public class RepositoryObjectPoolAspect {

    @Autowired
    private RepositoryObjectPool pool;
    
    /**
    * @description: 对标注@ApplyRepositoryPool方法进行资源库池获取及释放
    * @param: [pjp, applyRepositoryPool]
    * @return: java.lang.Object
    * @author: leslie
    * @email: hwangxiaosi@gmail.com        
    * @date: 2021/12/13
    */
    @Around("@annotation(applyRepositoryPool))")
    public Object repositoryPoolAround(ProceedingJoinPoint pjp,
                                       ApplyRepositoryPool applyRepositoryPool) throws Throwable {
        Object result = null;
        ThreadLocal<RepositoryObject> repositoryObject = new ThreadLocal<>();
        String repositoryId = null;
        try {
            Object[] args = pjp.getArgs();

            //获取方法，此处可将signature强转为MethodSignature
            MethodSignature signature = (MethodSignature) pjp.getSignature();
            Method method = signature.getMethod();

            //参数注解，1维是参数，2维是注解
            Annotation[][] annotations = method.getParameterAnnotations();

            for (int i = 0; i < annotations.length; i++) {
                //当前方法参数
                Object param = args[i];
                //注解列表
                Annotation[] paramAnn = annotations[i];
                //参数为空，直接下一个参数
                if(param == null || paramAnn.length == 0){
                    continue;
                }
                //遍历参数注解
                for (Annotation annotation : paramAnn) {
                    //注解类型
                    Class<? extends Annotation> type = annotation.annotationType();
                    if (type.equals(RepositoryType.class)){ //是否@Repository注解
                        repositoryId = getIdFromRepositoryAnn(param);
                    } else if (type.equals(RepositoryId.class)){ //是否@RepositoryId注解
                        repositoryId = getIdFromRepository(param);
                    }
                }
            }

            //根据资源库ID从资源库对象池获取Repository
            repositoryObject.set(pool.borrowObject(repositoryId));
            //赋值
            ObjectPool objectPool = (ObjectPool) pjp.getTarget();

            objectPool.setObject(repositoryObject);
            result = pjp.proceed(args);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放资源
            pool.returnObject(repositoryId, repositoryObject.get());
        }
        return result;
    }

    
    /**
    * @description: 通过@Repository注解获取资源库ID
    * @param: [args]
    * @return: java.lang.Integer
    * @author: leslie
    * @email: hwangxiaosi@gmail.com        
    * @date: 2021/12/13
    */
    private String getIdFromRepositoryAnn(Object param) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String repositoryId = null;
        Class clazz = param.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            //判断当前属性下是否存在@RepositoryId注解
            boolean fieldHasAnno = field.isAnnotationPresent(RepositoryId.class);
            if (fieldHasAnno){
                // 获取属性名
                String key = field.getName();
                // 将属性首字符大写，方便get & set 方法
                String method = key.substring(0,1).toUpperCase()+key.substring(1);
                // 反射获取方法
                Method getMethod = clazz.getMethod("get"+method);
                //反射执行方法并赋值资源库id
                repositoryId = (String) getMethod.invoke(param);
                continue;
            }
        }
        return repositoryId;
    }


    /**
    * @description: 通过@RepositoryID注解获取资源库ID
    * @param: [param]
    * @return: java.lang.Integer
    * @author: leslie
    * @email: hwangxiaosi@gmail.com
    * @date: 2021/12/13
    */
    private String getIdFromRepository(Object param){
        String repositoryId = null;
        if (param instanceof String){
            repositoryId = param.toString();
        }
        return repositoryId;
    }
}
