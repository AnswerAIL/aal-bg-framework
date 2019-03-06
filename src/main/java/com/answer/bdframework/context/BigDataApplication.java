package com.answer.bdframework.context;

import com.alibaba.fastjson.JSON;
import com.answer.bdframework.algorithm.AlgorithmAbs;
import com.answer.bdframework.annotation.AutoAssembly;
import com.answer.bdframework.annotation.BDValue;
import com.answer.bdframework.annotation.BDComponent;
import com.answer.bdframework.annotation.BigDataBootApplication;
import com.answer.bdframework.entity.BDType;
import com.answer.bdframework.entity.LogLevel;
import com.answer.bdframework.entity.ST;
import com.answer.bdframework.entity.STS;
import com.answer.bdframework.exception.BigDataException;
import com.answer.bdframework.sqlcontainer.SQLContainer;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.JAXB;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.answer.bdframework.common.LoggerCommon.*;
import static com.answer.bdframework.common.BDCommon.*;

/**
 * Created by L.Answer on 2018-07-30 08:56
 *
 * Big Data Application
 */
public class BigDataApplication implements BDApplicationContext {
    private Map<String, Object> settingsConfs;
    private Map<String, Object> injectObjects;
    private boolean sqlInitial;
    private boolean initialize;
    private LogLevel logLevel;
    private boolean jarFile;
    private List<Class> cacheObject;

    private BigDataApplication() {
        settingsConfs = new HashMap<>();
        injectObjects = new HashMap<>();
        sqlInitial = false;
        initialize = false;
        logLevel = LogLevel.INFO;
        jarFile = true;
        cacheObject = new ArrayList<>();
    }

    public static BDApplicationContext run(Class clz, String ... args) {
        return new BigDataApplication().run1(clz, args);
    }

    private BDApplicationContext run1(Class clz, String ... args) {
        try {
            if (clz.isAnnotationPresent(BigDataBootApplication.class)) {
                BigDataBootApplication bigDataBootApplication = (BigDataBootApplication) clz.getAnnotation(BigDataBootApplication.class);
                logLevel = bigDataBootApplication.logLevel();
                if (logLevel.getLevel() <= INFO_LEVEL) {
                    info(Thread.currentThread().getStackTrace()[1], "start run big data application, application name【"+ clz.getSimpleName() +"】");
                }

                this.loadConfiguration(bigDataBootApplication);

                if (bigDataBootApplication.scanSQLXmlPath().length > 0) {
                    sqlInitial = true;
                    if (logLevel.getLevel() <= INFO_LEVEL) {
                        info(Thread.currentThread().getStackTrace()[1], "\r\n" +
                                "-------------------------------------------------------------------------------------------------\r\n" +
                                "【SQL CONTAINER INIT START】. ready to initialize the SQL container. config path Length: " + bigDataBootApplication.scanSQLXmlPath().length + "\r\n" +
                                "-------------------------------------------------------------------------------------------------");
                    }

                    SQLContainer.instance().init(String.join(SEMICOLON, bigDataBootApplication.scanSQLXmlPath()), logLevel);
                    if (logLevel.getLevel() <= INFO_LEVEL) {
                        info(Thread.currentThread().getStackTrace()[1], "\r\n" +
                                "-------------------------------------------------------------------------------------------------\r\n" +
                                "【SQL CONTAINER INIT END】. SQL container has been initialized successfully.\r\n" +
                                "-------------------------------------------------------------------------------------------------");
                    }

                } else {
                    warn(Thread.currentThread().getStackTrace()[1], "annotation BigDataBootApplication's length of scanSQLXmlPath is not gt 0, scanSQLXmlPath's length is: " + bigDataBootApplication.scanSQLXmlPath().length);
                }

                String[] scanJavaPaths = bigDataBootApplication.scanJavaPath();
                jarFile = bigDataBootApplication.jarFile();
                for (String javaPath: scanJavaPaths) {
                    cacheObject.clear();
                    if (jarFile) {
                        String[] javaPaths = javaPath.split(DELIMITER);
                        if (javaPaths.length != 2) {
                            throw new BigDataException("javaPath must container jar path and package path, the delimiters of both are #.");
                        }

                        JarFile jarFile = new JarFile(javaPaths[0]);
                        Enumeration entries = jarFile.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry jarEntry = (JarEntry) entries.nextElement();
                            String namePath = jarEntry.getName();
                            if (namePath.contains(javaPaths[1].replaceAll("\\.", "/")) &&
                                    namePath.endsWith(".class") && !namePath.contains("$")) {
                                debug(Thread.currentThread().getStackTrace()[1], "first filtration read namePath from jar file: " + namePath);
                                String className = namePath.replaceAll("/", ".").replace(".class", "");
                                Class clazz = Class.forName(className);
                                injectPropValue(clazz);
                            }
                        }
                    } else {
                        if (logLevel.getLevel() <= INFO_LEVEL) {
                            info(Thread.currentThread().getStackTrace()[1], "scan java package name 【" + javaPath + "】");
                        }

                        ClassLoader loader = Thread.currentThread().getContextClassLoader();
                        URI uri  = loader.getResource(javaPath.replaceAll("\\.", "/")).toURI();
                        File file = new File(uri);

                        if (file.isDirectory()) {
                            File[] files = file.listFiles();
                            if (files != null && files.length > 0) {
                                for (File f: files) {
                                    optFile(javaPath, f);
                                }
                            }
                        } else {
                            optFile(javaPath, file);
                        }
                    }
                }
                initialize = true;
                if (logLevel.getLevel() <= INFO_LEVEL) {
                    info(Thread.currentThread().getStackTrace()[1], "big data application has been initialized successfully.");
                    info(Thread.currentThread().getStackTrace()[1], "initialized settings parameter's count: 【" + settingsConfs.size() + "】, inject object's count: 【" + injectObjects.size() + "】");
                }
                if (logLevel.getLevel() == DEBUG_LEVEL) {
                    debug(Thread.currentThread().getStackTrace()[1], "\r\n" +
                            "settingsConfs: 【" + settingsConfs + "】, \r\n" +
                            "injectObjects's keys: 【"+ injectObjects.keySet() +"】");
                }
                return this;
            } else {
                error(Thread.currentThread().getStackTrace()[1], "annotation BigDataBootApplication must be neccessary if you want to run a big data application.");
            }
        } catch (Exception e) {
            if (logLevel.getLevel() == ERROR_LEVEL) {
                error(Thread.currentThread().getStackTrace()[1], e.getMessage());
            }

            e.printStackTrace();
        }
        return null;
    }

    /**
     * inject the Object who is modified by annotation BDComponent
     *  <code>
     *      // class object
     *      @AutoAssembly
     *      private UserService userService;
     *      // parameter
     *      @BDValue
     *      private String userName;
     *  </code>
     * */
    private void injectPropValue(Class clz) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        // if class has been injected, return
        if (injectObjects.containsKey(clz.getName())) {
            return;
        }
        if (clz.isAnnotationPresent(BDComponent.class) && clz.getModifiers() == Modifier.PUBLIC) {
            Object instance = clz.newInstance();
            if (logLevel.getLevel() <= INFO_LEVEL) {
                info(Thread.currentThread().getStackTrace()[1], "ready to analysis java class name 【" + clz.getName() + "】");
            }

            Field[] fields = clz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                if (field.isAnnotationPresent(BDValue.class) || field.isAnnotationPresent(AutoAssembly.class)) {
                    // the injected field must be defined as private
                    if (field.getModifiers() == Modifier.PRIVATE) {
                        Set<Class>basicDataType = new HashSet<Class>(8) {{
                            add(byte.class); add(short.class); add(int.class); add(long.class);
                            add(float.class); add(double.class);
                            add(char.class);
                            add(boolean.class);
                        }};
                        // inject class. if class has been injected, use it, if not, please priority inject this class.
                        // here why use basicDataType to judge? basic Data Type has no field.getType().getName()
                        if (field.isAnnotationPresent(AutoAssembly.class) && !basicDataType.contains(field.getType()) &&
                                Class.forName(field.getType().getName()).isAnnotationPresent(BDComponent.class)) {
                            if (!injectObjects.containsKey(field.getType().getName())) {
                                if (cacheObject.contains(clz)) {
                                    throw new BigDataException("exists mutual reference in " + cacheObject);
                                }
                                cacheObject.add(clz);
                                injectPropValue(field.getType());
                                i--;
                                cacheObject.remove(clz);
                            } else {
                                field.set(instance, injectObjects.get(field.getType().getName()));
                            }
                            // inject parameter
                        } else if (field.isAnnotationPresent(BDValue.class)) {
                            BDValue bdValue = field.getAnnotation(BDValue.class);
                            String name = bdValue.name();
                            String defaultVal = bdValue.defaultVal();
                            if (StringUtils.isEmpty(name)) {
                                name = field.getName();
                            }
                            if (settingsConfs.containsKey(name)) {
                                field.set(instance, settingsConfs.get(name));
                            } else {
                                if (StringUtils.isNotEmpty(defaultVal)) {
                                    field.set(instance, defaultVal);
                                } else {
                                    throw new BigDataException("there is not property that named【"+ name +"】 in the xml file and "+ name +"'s annotation BDValue not set defaultVal.");
                                }
                            }
                        }
                    } else {
                        warn(Thread.currentThread().getStackTrace()[1], clz.getName() + "#"+ field.getName() +" must be set to private, modifier: "+ field.getModifiers() +".");
                    }
                }
            }
            injectObjects.put(clz.getName(), instance);
            if (logLevel.getLevel() <= INFO_LEVEL)
                info(Thread.currentThread().getStackTrace()[1], "【" + clz.getName() + "】 has been injected into container.");
        }
    }

    /**
     * loading settings from settings config file, default file name: bigdata-settings.xml
     * */
    private void loadConfiguration(BigDataBootApplication bdba) throws IOException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        String path = bdba.settingPath();
        Class<? extends AlgorithmAbs> algorithm = bdba.encry();
        Object instance = algorithm.newInstance();
        if (logLevel.getLevel() <= INFO_LEVEL) {
            info(Thread.currentThread().getStackTrace()[1], "loading and analysis the settings file, file path: 【"+ path +"】");
        }
        InputStream is = BigDataApplication.class.getClassLoader().getResourceAsStream(path);
        if (is == null) {
            error(Thread.currentThread().getStackTrace()[1], "loading settings file【"+ path +"】 failed.");
        }
        List<String> lists = IOUtils.readLines(is, Charsets.toCharset(CHARSET));

        STS sts = JAXB.unmarshal(new StringReader(String.join(NEW_LINE, lists)), STS.class);
        List<ST> settings = sts.getSts();
        if (logLevel.getLevel() <= INFO_LEVEL) {
            info(Thread.currentThread().getStackTrace()[1], "read settings parameter count 【" + settings.size() + "】 from file: " + path);
        }
        for (ST st: settings) {
            String type = st.getType();
            if (StringUtils.isEmpty(type)) {
                type = BDType.String.getType();
            }
            String name = st.getName();
            Object value = st.getValue();
            String salt = st.getSalt();
            if (StringUtils.isNotEmpty(salt) && type.equals(BDType.String.getType())) {
                if (salt.length() < 24) {
                    throw new BigDataException("name 【"+ name +"】 salt's length must be egt 24.");
                }
                Method method = algorithm.getDeclaredMethod("decrypt", String.class, String.class);
                value = method.invoke(instance, value.toString(), salt);
            }
            value = transVal(name, value, type);
            settingsConfs.put(name, value);
        }
    }


    /**
     *  according to type to trans value{@link Object} to defined type
     * */
    private Object transVal(String name, Object value, String type) {
        if (logLevel.getLevel() == DEBUG_LEVEL) {
            debug(Thread.currentThread().getStackTrace()[1], "trans name 【"+ name +"】 value 【"+ value +"】 to " + type);
        }

        String val = (String) value;
        if (type.equals(BDType.String.getType())) {
            return val;
        } else if (type.equals(BDType.Integer.getType())) {
            return Integer.parseInt(val);
        } else if (type.equals(BDType.Long.getType())) {
            return Long.parseLong(val);
        } else if (type.equals(BDType.Float.getType())) {
            return Float.parseFloat(val);
        } else if (type.equals(BDType.Double.getType())) {
            return Double.parseDouble((String) value);
        } else if (type.equals(BDType.List.getType())) {
            return Arrays.asList(val.split(","));
        } else if (type.equals(BDType.Map.getType())) {
            return JSON.parseObject(val);
        } else {
            throw new BigDataException("not match bigtype["+ type +"] type exception.");
        }
    }


    private void optFile(String javaPath, File file) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String className = file.getName();
        className = className.substring(0, className.length() - 6);
        className = javaPath + "." + className;
        Class cls = Class.forName(className);
        injectPropValue(cls);
    }


    @Override
    public Map<String, Object> getBeans() {
        checkInitStatus();
        if (injectObjects == null || injectObjects.size() == 0) {
            throw new BigDataException("the container is empty or inject object's count eq 0.");
        }
        return this.injectObjects;
    }

    @Override
    public Object getBean(Class clz) {
        checkInitStatus();
        String beanName = clz.getName();
        if (this.injectObjects.containsKey(beanName)) {
            return this.injectObjects.get(beanName);
        } else {
                throw new BigDataException("the bean["+ beanName +"] has not been injected into the container.");
        }
    }

    @Override
    public void close() {
        checkInitStatus();
        settingsConfs = null;
        injectObjects = null;
        if (sqlInitial) {
            SQLContainer.instance().destroy();
        }
        if (logLevel.getLevel() <= INFO_LEVEL) {
            info(Thread.currentThread().getStackTrace()[1], "big data application has been closed.");
        }

    }

    /**
     * check application has been initialized or not
     * */
    private void checkInitStatus() {
        if (!initialize) {
            throw new BigDataException("big data application has not been initialized, initialize: " + initialize);
        }
    }

}