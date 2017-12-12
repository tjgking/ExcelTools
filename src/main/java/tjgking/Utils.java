package tjgking;

/**
 * Created by tjg_k on 2017/11/16.
 */
public class Utils {
    public static String formatNum(String str, String repeat) {
        int len = str.length();
        if (len < 8) {
            int a = 8 - len;
            for (int i = 0; i < a; i++) {
                str = str + repeat;
            }
        }

        return str;
    }
}
