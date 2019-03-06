package com.answer.bdframework.sqlcontainer;

import static com.answer.bdframework.common.BDCommon.*;
import static com.answer.bdframework.common.LoggerCommon.*;

import com.answer.bdframework.entity.LogLevel;
import com.answer.bdframework.sqlcontainer.entity.SqlRefer;
import com.answer.bdframework.sqlcontainer.entity.SqlText;
import com.answer.bdframework.sqlcontainer.entity.SqlTexts;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.JAXB;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by L.Answer on 2018-07-13 17:12
 *
 * HIVE SQL语句配置化容器
 *
 * Usage:
 *      1: 初始化容器:
 *          // 初始化单个配置文件
 *          SQLContainers.instance().init("src/main/resources/sqlText/template-mapper.xml;src/main/resources/sqlText/mapper/business/order-mapper.xml");
 *          // 初始化某个路径下的所有配置文件
 *          SQLContainers.instance().init("src/main/resources/sqlText");
 *
 *      2: 根据sqlId获取对应SQL语句
 *          // 多个参数情况使用案例
 *          String sqlText = SQLContainers.getSqlText("cardScoreTask.findStockDeptReportSource", map);
 *          // 单个参数情况使用案例
 *          String sqlText = SQLContainers.getSqlText("cardScoreTask.findDeptReportHis", key, value);
 *          // 无参数情况使用案例
 *          String sqlText = SQLContainers.getSqlText("cardScoreTask.findDeptReportHis");
 *
 *      ***.xml文件中需要使用<sql id="XXX"></sql>标签时, <sqlText referId="XXX">...</sqlText>中使用时需要用 @{PLACEHOLDER} 作为占位符
 *      注意: <sql>...</sql>中有 ${...} 变量时, 请使用 \${...}
 *
 *      ***.xml配置请参见模板: template-mapper.xml
 */
public class SQLContainer {
    private static final String SUFFIX_NAME = ".xml";
    private static final String PLACEHOLDER = "@\\{PLACEHOLDER}";
    /** 容器初始化大小 */
    private static final int CONTAINER_DEFAULT_CAPACITY = 16;
    private Map<String, SqlText> sqls;
    private Boolean isInit;
    private LogLevel logLevel = LogLevel.INFO;

    private SQLContainer() {
        isInit = false;
    }


    /**
     * 对指定路径下的数据库文件进行初始化
     * @param path  路径地址
     * */
    public void init(String path) {
        logLevel = LogLevel.INFO;
        init(path, CONTAINER_DEFAULT_CAPACITY);
    }

    /**
     * 对指定路径下的数据库文件进行初始化
     * @param path  路径地址
     * @param logLevel 日志输出级别
     * */
    public void init(String path, LogLevel logLevel) {
        this.logLevel = logLevel;
        init(path, CONTAINER_DEFAULT_CAPACITY);
    }


    /**
     * 对指定路径下的数据库文件进行初始化
     * @param filePath          路径地址, 多个路径以封号(;)隔开
     * @param initialCapacity   初始化容器大小, 默认大小为16
     * */
    public void init(String filePath, int initialCapacity) {
        if (logLevel.getLevel() <= INFO_LEVEL) {
            info(Thread.currentThread().getStackTrace()[1], "ready to init sql container...");
            info(Thread.currentThread().getStackTrace()[1], "filePath: " + filePath + ", initialCapacity: " + initialCapacity);
        }
        if (isInit) {
            throw new SQLContainersException("the sql container has been initialized two times.");
        }
        List<String> paths = new ArrayList<>();
        String[] fpArr = filePath.split(";");
        for (String path: fpArr) {
            if (logLevel.getLevel() <= INFO_LEVEL) {
                info(Thread.currentThread().getStackTrace()[1], "get valid file path: " + path);
            }
            getFilePaths(paths, path);
        }
        sqls = new HashMap<>(initialCapacity);

        Map<String, String> sqlRefersMap = null;
        for (String fp: paths) {
            List<String> lists = analysisFile(fp);
            SqlTexts sqlTexts = JAXB.unmarshal(new StringReader(String.join(NEW_LINE, lists)), SqlTexts.class);
            String name = sqlTexts.getName();
            List<SqlRefer> sqlRefers = sqlTexts.getSqlRefers();

            if (sqlRefers != null && sqlRefers.size() > 0) {
                sqlRefersMap = new HashMap<>(sqlRefers.size());
                for (SqlRefer sqlRefer: sqlRefers) {
                    String sqlReferId = sqlRefer.getId();
                    if (sqlRefersMap.containsKey(sqlReferId)) {
                        throw new SQLContainersException("sqlReferId[id=\"" + sqlReferId + "\"] has been initialized two times in file: " + fp);
                    } else {
                        sqlRefersMap.put(sqlReferId, sqlRefer.getSqlRefer().replaceAll("\\s+|\r|\n|\t", " "));
                    }


                }
            }

            for(SqlText sqlText: sqlTexts.getSqlTexts()) {
                String key = (name + "." + sqlText.getId());
                if (sqls.containsKey(key.toUpperCase())) {
                    throw new SQLContainersException("id =  " + key + " has already exists.");
                }
                if (StringUtils.isNotEmpty(sqlText.getReferId())) {
                    String referId = sqlText.getReferId();
                    if (sqlRefersMap != null && !sqlRefersMap.containsKey(referId)) {
                        throw new SQLContainersException("refer[id=\"" + referId + "\"] sql is not defined in file: " + fp);
                    }
                    String sqlTxt = sqlText.getSqlText();
                    // 替换占位符(@{PLACEHOLDER})为对应的转换字符串
                    sqlTxt = sqlTxt.replaceAll(PLACEHOLDER, sqlRefersMap.get(referId));
                    sqlText.setSqlText(sqlTxt);
                }
                sqls.put(key.toUpperCase(), sqlText);
            }
        }
        isInit = true;
        if (logLevel.getLevel() <= INFO_LEVEL) {
            info(Thread.currentThread().getStackTrace()[1],"init sql container success. size of container: " + sqls.size());
        }

    }

    /**
     * 销毁SQL容器
     * */
    public void destroy() {
        isInit = false;
        sqls = null;
    }

    /**
     * 递归获取指定路径下的所有文件信息
     * @param path  路径地址
     * */
    @SuppressWarnings("unchecked")
    private void getFilePaths(List paths, String path) {
        File f = new File(path);
        if (f.isFile()) {
            if (path.endsWith(SUFFIX_NAME)) {
                paths.add(path);
            }
        } else {
            File[] files = f.listFiles();
            if (files != null && files.length > 0) {
                for (File file: files) {
                    if (file.isFile()) {
                        String p = file.getPath();
                        if (p.endsWith(SUFFIX_NAME)) {
                            paths.add(p);
                        }
                    } else {
                        getFilePaths(paths, file.getPath());
                    }
                }
            } else {
                throw new SQLContainersException("path: [" + path + "] is unvalid path.");
            }
        }
    }

    /**
     * 根据文件路径信息解析文件内容
     * @param filePath  SQL配置文件路径
     * @return          List<String>
     * */
    private List<String> analysisFile(String filePath) {
        if (logLevel.getLevel() <= INFO_LEVEL) {
            info(Thread.currentThread().getStackTrace()[1],"read file content from file and file path 【" + filePath + "】");
        }

        List<String> lists = null;
        try {
            lists = FileUtils.readLines(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lists;
    }


    /**
     * 根据指定sqlId获取对应的sql语句(无参数)
     * @param sqlId     sql语句对应的sqlId
     * @return          String
     * */
    public static String getSqlText(String sqlId) {
        sqlId = sqlId.toUpperCase();
        if (checkInit(sqlId)) {
            return instance().map().get(sqlId).getSqlText();
        }
        return "";
    }

    /**
     * 根据指定sqlId获取对应的sql语句(多个动态参数)
     * @param sqlId     sql语句对应的sqlId
     * @param name      sql中单参数名称
     * @param value     sql中单参数值
     * @return          String
     * */
    public static String getSqlText(String sqlId, String name, String value) {
        sqlId = sqlId.toUpperCase();
        if (checkInit(sqlId)) {
            String rltSQL = instance().map().get(sqlId).getSqlText();
            return transSQLText(rltSQL, name, value).trim();
        }
        return "";
    }

    /**
     * 根据指定sqlId获取对应的sql语句(多个动态参数)
     * @param sqlId     sql语句对应的sqlId
     * @param params    sql中的动态参数
     * @return          String
     * */
    public static String getSqlText(String sqlId, Map<String, String> params) {
        sqlId = sqlId.toUpperCase();
        if (checkInit(sqlId)) {
            String rltSQL = instance().map().get(sqlId).getSqlText();
            for (String key : params.keySet()) {
                rltSQL = transSQLText(rltSQL, key, params.get(key));
            }
            return rltSQL.trim();
        }
        return "";
    }

    /**
     * 创建容器实例
     * */
    private static class ContainersHolder {
        private static SQLContainer containers = new SQLContainer();
    }


    /**
     * 获取容器实例
     * */
    public static SQLContainer instance() {
        return ContainersHolder.containers;
    }

    private Map<String, SqlText> map() {
        return sqls;
    }


    /**
     * 转换配置文件中原生SQL中的动态参数变量为值
     * @param rltSQL    配置中原生SQL语句
     * @param name      动态参数名
     * @param value     动态参数值
     * @return          String
     * */
    private static String transSQLText(String rltSQL, String name, String value) {
        if (rltSQL.contains("${" + name + "}"))
            rltSQL = rltSQL.replaceAll("\\$\\{" +  name + "}", value);
        if (rltSQL.contains("#{" + name + "}"))
            rltSQL = rltSQL.replaceAll("#\\{" + name + "}", "'" + value + "'");
        return rltSQL;
    }

    /**
     * 校验容器是否异常
     * @param sqlId     sql语句对应的sqlId
     * @return          boolean
     * */
    private static boolean checkInit(String sqlId) {
        if (!instance().isInit) {
            throw new SQLContainersException("the sql container has not been initialized.");
        }

        if (instance().sqls == null || instance().sqls.size() == 0) {
            throw new SQLContainersException("the sql container has not initialized, but size is 0.");
        }

        if (!instance().map().containsKey(sqlId)) {
            return false;
        }
        return true;
    }

}