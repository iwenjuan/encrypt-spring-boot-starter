package cn.iwenjuan.encrypt.utils;

/**
 * @author li1244
 * @date 2023/3/29 13:00
 */
public class PatternUtils {

    /**
     * 将通配符表达式转化为正则表达式
     *
     * @param path
     * @return
     */
    public static String getPathRegStr(String path) {
        char[] chars = path.toCharArray();
        int len = chars.length;
        StringBuilder builder = new StringBuilder();
        boolean preX = false;
        for (int i = 0; i < len; i++) {
            if (chars[i] == '*') {
                // 遇到*字符
                if (preX) {
                    // 如果是第二次遇到*，则将**替换成.+
                    // .+的含义是匹配一个或多个任意字符
                    builder.append(".+");
                    preX = false;
                } else if (i + 1 == len) {
                    // 如果是遇到单星，且单星是最后一个字符，则直接将*转成[^/]+
                    // [^/]+的含义是匹配一个或多个非”/”字符
                    builder.append("[^/]+");
                } else {
                    // 否则单星后面还有字符，则不做任何动作，下一把再做动作
                    preX = true;
                    continue;
                }
            } else {
                // 遇到非*字符
                if (preX) {
                    // 如果上一把是*，则先把上一把的*对应的[^/]+添进来
                    // [^/]+的含义是匹配一个或多个非”/”字符
                    builder.append("[^/]+");
                    preX = false;
                }
                if (chars[i] == '?') {
                    // 接着判断当前字符是不是?，是的话替换成.
                    builder.append('.');
                } else {
                    // 不是?的话，则就是普通字符，直接添进来
                    builder.append(chars[i]);
                }
            }
        }
        return builder.toString();
    }
}
