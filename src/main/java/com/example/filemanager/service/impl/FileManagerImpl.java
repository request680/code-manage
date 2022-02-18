package com.example.filemanager.service.impl;

import com.example.filemanager.service.FileManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

/**
 * @author mr.liu
 * @projectName file-manager
 * @package_name com.example.filemanager.service
 * @className FileManagerImpl
 * @description 文件管理
 * @date 2022/2/13 10:37
 */

/**
 * 需求:
 *      给定一个文本文件in.txt，文件可由不在引号内的换行符划分成多行，
 *      请将文件A中的内容按以下规则处理后输出至文件out.txt：
 *      A.每行行内字符要反转输出，
 *      B.有多行的话，行间次序要反转输出，
 *      C.引号内的内容视为整体，内部不需变化次序
 *
 * 理解的预期效果（ \r\n 为换行符）：
 *      原文本内容:  a"ss"df"gh" \r\n q"wer""ty"rty \r\n "nas"df"gh"jgd
 *      新文本内容： dgj"gh"fd"nas" \r\n ytr"ty""wer"qn \r\n "gh"fd"ss"a
 */
@Service
@Slf4j
public class FileManagerImpl implements FileManagerService {

    final String INPUT_FILE_PATH = "D:\\file\\input\\";        //输入文件路径
    final String OUTPUT_FILE_PATH = "D:\\file\\output\\";       //输出文件路径

    /**
     * 优化思路:
     * 将行内容转化为char字符压入栈中
     * 利用栈先进后出的特性反转
     * 引号数量为奇数抛异常，否则按照规则进行反转
     */
    public String fileReverse() throws IOException {
        String inTxt = "in.txt";
        File inFile = new File(INPUT_FILE_PATH.concat(inTxt));
        FileReader fileReader = new FileReader(inFile);

        int lines = 0;
        if (inFile.exists()) {
            lines = this.getFileCount(fileReader);
        }

        InputStreamReader inputReader = new InputStreamReader(new FileInputStream(inFile));
        BufferedReader bufferedReader = new BufferedReader(inputReader);
        int partCount = 1;
        if (lines > 10000) {
            partCount = lines / 10000 + 1;
        }

        if (Objects.equals(partCount, 1)) {
            //将源文件内容转为map存储
            List<String> newFileStringList = this.lineMapTransformList(lines, partCount, bufferedReader);
            this.writeNewFile(newFileStringList, Boolean.FALSE);
        } else {
            for (int i = 1; i < partCount; i++) {
                List<String> newFileStringList = this.lineMapTransformList(lines, partCount, bufferedReader);
                this.writeNewFile(newFileStringList, Boolean.TRUE);
            }
        }
        return null;
    }

    private List<String> lineMapTransformList(int lines, int partCount, BufferedReader bufferedReader) throws IOException {
        List<String> newFileStringList = new ArrayList<>();
        Map<Integer, String> originalFileMap = this.originalFileConvertMap(lines, partCount, bufferedReader);
        for (int i = 0; i < originalFileMap.size(); i++) {
            //文件内容处理
            String lineString = originalFileMap.get(i + 1);
            String newString = this.lineStringReverse(lineString.trim());
            newFileStringList.add(newString);
        }
        return newFileStringList;
    }

    /**
     * 将反转后的内容输入
     *
     * @param newFileStringList
     * @throws IOException
     */
    private void writeNewFile(List<String> newFileStringList, Boolean flag) throws IOException {
        String outTxt = "out.txt";
        File outFile = new File(OUTPUT_FILE_PATH.concat(outTxt));
        OutputStream outputStream = null;
        outputStream = new FileOutputStream(outFile, flag);
        for (int i = 0; i < newFileStringList.size(); i++) {
            byte[] bytes = (newFileStringList.get(i) + "\r\n").getBytes();
            outputStream.write(bytes, 0, bytes.length);
        }
        outputStream.flush();
        if (outputStream != null) {
            outputStream.close();
        }
    }

    /**
     * 行文本反转
     * @param lineString
     * @return
     */
    private String lineStringReverse(String lineString) {
        /*
         * 此处注释可解决引号不对称的问题
         * 如123"123"123"123可根据业务需求变换成321"123"321"123或321"123"321"321
            char[] chars = lineString.toCharArray();
            for (int i = 0; i < chars.length; i++){
                if (Objects.equals(chars[i], "\"")){
                    indexList.add(i);
                }
            }
        */
        StringBuilder sb = new StringBuilder();
        if (lineString.contains("\"")) {
            String[] strings = lineString.split("\"");
            //此处默认字符串中引号数量是对成的
            //每行字符均需反转,需判断最后是否是以引号结尾
            int index = lineString.lastIndexOf("\"");
            //获取第一个出现引号的位置,如在行首，倒序时需考虑
            int startIndex = lineString.indexOf("\"");

            //行尾以引号结尾
            if (Objects.equals(index, lineString.length() - 1)) {
                boolean flag = Boolean.TRUE;
                //行首为引号
                if (Objects.equals(startIndex, 0)) {
                    sb = this.startAndEndIsMarks(strings, flag, sb);
                } else {
                    sb = this.endIsMarks(strings, flag, sb);
                }
            } else {
                //行尾非引号，需反转
                boolean flag = Boolean.FALSE;
                //行首为引号
                if (Objects.equals(startIndex, 0)) {
                    sb = this.startIsMarks(strings, flag, sb);
                } else {
                    sb = this.startAndEndIsNotMarks(strings, flag, sb);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 首尾都不是引号
     *
     * @param strings
     * @param flag
     * @param sb
     * @return
     */
    private StringBuilder startAndEndIsNotMarks(String[] strings, Boolean flag, StringBuilder sb) {
        //非引号结尾，开头需要反转
        for (int i = strings.length - 1; i > 1; i--) {
            if (Objects.equals(strings.length - 1, i)) {
                sb = stringAppend(strings[i], flag, sb);
                flag = this.changeBooleanValue(flag);
            } else {
                if (i >= 1) {
                    //空字符串，代表两个引号相邻
                    if (Objects.equals(strings[i - 1], "")) {
                        sb = this.stringAppendByNullStr(strings[i], flag, sb);
                    } else {
                        if (StringUtils.isBlank(strings[i])) {
                            continue;
                        }
                        //字符串常规拼接
                        sb = stringAppend(strings[i], flag, sb);
                        flag = this.changeBooleanValue(flag);
                    }
                } else {
                    //最后需要反转
                    sb = stringAppend(strings[0], Boolean.FALSE, sb);
                }
            }
        }
        return sb;
    }

    /**
     * 行首是引号，行尾非引号
     *
     * @param strings
     * @param flag
     * @param sb
     * @return
     */
    private StringBuilder startIsMarks(String[] strings, Boolean flag, StringBuilder sb) {
        //非引号结尾，开头需要反转
        for (int i = strings.length - 1; i > 0; i--) {
            if (Objects.equals(strings.length - 1, i)) {
                sb = stringAppend(strings[i], flag, sb);
                flag = this.changeBooleanValue(flag);
            } else {
                //空字符串，代表两个引号相邻
                if (Objects.equals(strings[i - 1], "")) {
                    sb = this.stringAppendByNullStr(strings[i], flag, sb);
                } else {
                    if (StringUtils.isBlank(strings[i])) {
                        continue;
                    }
                    //字符串常规拼接
                    sb = stringAppend(strings[i], flag, sb);
                    flag = this.changeBooleanValue(flag);
                }
            }
        }
        return sb;
    }

    /**
     * 行尾以引号结尾,行首不是引号
     *
     * @param strings
     * @param flag
     * @param sb
     * @return
     */
    private StringBuilder endIsMarks(String[] strings, Boolean flag, StringBuilder sb) {
        //引号结尾，最后一组是字符串
        for (int i = strings.length - 1; i >= 0; i--) {
            if (Objects.equals(strings.length - 1, i)) {
                sb.append("\"").append(strings[i]).append("\"");
                flag = this.changeBooleanValue(flag);
            } else {
                if (i >= 1) {
                    //空字符串，代表两个引号相邻
                    if (Objects.equals(strings[i - 1], "")) {
                        sb = this.stringAppendByNullStr(strings[i], flag, sb);
                    } else {
                        if (StringUtils.isBlank(strings[i])) {
                            continue;
                        }
                        //字符串常规拼接
                        sb = stringAppend(strings[i], flag, sb);
                        flag = this.changeBooleanValue(flag);
                    }
                } else {
                    //最后需要反转
                    sb = stringAppend(strings[0], Boolean.FALSE, sb);
                }

            }
        }
        return sb;
    }

    /**
     * 首尾皆为引号
     *
     * @param strings
     * @param flag
     * @param sb
     * @return
     */
    private StringBuilder startAndEndIsMarks(String[] strings, Boolean flag, StringBuilder sb) {
        //引号结尾，最后一组是字符串
        for (int i = strings.length - 1; i > 0; i--) {
            if (Objects.equals(strings.length - 1, i)) {
                sb.append("\"").append(strings[i]).append("\"");
                flag = this.changeBooleanValue(flag);
            } else {
                //空字符串，代表两个引号相邻
                if (Objects.equals(strings[i - 1], "")) {
                    sb = this.stringAppendByNullStr(strings[i], flag, sb);
                } else {
                    if (StringUtils.isBlank(strings[i])) {
                        continue;
                    }
                    //字符串常规拼接
                    sb = stringAppend(strings[i], flag, sb);
                    flag = this.changeBooleanValue(flag);
                }
            }
        }
        return sb;
    }

    /**
     * 字符串常规拼接
     *
     * @param string
     * @param flag
     * @param sb
     * @return
     */
    private StringBuilder stringAppend(String string, Boolean flag, StringBuilder sb) {
        if (flag) {
            sb.append("\"").append(string).append("\"");
        } else {
            //非空字符串，代表不在引号范围内，需反转
            String newString = this.stringReverse(string);
            sb.append(newString);
        }
        return sb;
    }

    /**
     * 因空字符串拼接
     *
     * @param string
     * @param flag
     * @param sb
     * @return
     */
    private StringBuilder stringAppendByNullStr(String string, boolean flag, StringBuilder sb) {
        if (flag) {
            sb.append("\"").append(string).append("\"");
        } else {
            //非空字符串，代表不在引号范围内，需反转
            String newString = this.stringReverse(string);
            sb.append(newString);
        }
        return sb;
    }


    /**
     * 字符串反转
     *
     * @param value
     * @return
     */
    private String stringReverse(String value) {
        char[] chars = value.toCharArray();
        char[] newChars = new char[chars.length + 1];
        for (int j = 0; j < chars.length; j++) {
            newChars[j] = chars[chars.length - j - 1];
        }
        return String.valueOf(newChars);
    }

    /**
     * 获取文件总行数
     *
     * @param fileReader
     * @return
     * @throws IOException
     */
    private Integer getFileCount(FileReader fileReader) throws IOException {
        int lines = 0;
        //获取文件总行数
        LineNumberReader numberReader = new LineNumberReader(fileReader);
        numberReader.skip(Long.MAX_VALUE);
        lines = numberReader.getLineNumber() + 1;
        return lines;
    }

    /**
     * 文本内容转换为Map
     *
     * @param lines
     * @param partCount
     * @param bufferedReader
     * @return
     * @throws IOException
     */
    private Map<Integer, String> originalFileConvertMap(int lines, int partCount, BufferedReader bufferedReader) throws IOException {
        Map<Integer, String> inLineInfoMap = new HashMap<>();
        String lineInfo;
        if (lines > 10000) {
            partCount = lines / 10000 + 1;
        }
        int lineNumber = 0;
        while ((lineInfo = bufferedReader.readLine()) != null) {
            lineNumber++;
            if (Objects.equals(partCount, 1)) {
                inLineInfoMap.put(lineNumber, lineInfo);
            } else {
                int startIndex = partCount * 10000 + 1;
                int endIndex = (partCount + 1) * 10000;
                if ((lineNumber >= startIndex) && (lineNumber <= endIndex)) {
                    inLineInfoMap.put(lineNumber, lineInfo);
                }
            }
        }

        if (partCount > 1) {
            Set<Integer> lineNum = inLineInfoMap.keySet();
            TreeSet<Integer> treeSet = new TreeSet<>(lineNum);
            int number = 10000 - treeSet.size();
            int endIndex = partCount * 10000 + 1 + number;
            Map<Integer, String> resultMap = new HashMap<>();
            for (int i = endIndex; i > partCount * 10000; i--) {
                resultMap.put(i, inLineInfoMap.get(i));
            }
            return resultMap;
        }
        return inLineInfoMap;
    }

    /**
     * 更新布尔类型的值
     *
     * @param value
     * @return
     */
    private Boolean changeBooleanValue(Boolean value) {
        if (value) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }
}
