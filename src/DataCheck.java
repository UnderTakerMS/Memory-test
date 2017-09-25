import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataCheck {
    String needToCheck,pattern;//needToCheck是需要被检测的字符串,pattern是正则表达式
    public DataCheck(String needToCheck,String pattern){
        this.needToCheck = needToCheck;
        this.pattern = pattern;
    }

    boolean check(){
        Pattern pt = Pattern.compile(pattern);
        Matcher condition = pt.matcher(needToCheck);
        if(!condition.matches()){
            return false;
        }
        return true;
    }

    public static boolean check(String needToCheck,String pattern){
        Pattern pt = Pattern.compile(pattern);
        Matcher condition = pt.matcher(needToCheck);
        if(!condition.matches()){
            return false;
        }
        return true;
    }

    public static String getStringByPattern(String needToUse,String pattern){
        Pattern pt = Pattern.compile(pattern);
        Matcher condition = pt.matcher(needToUse);
        if(condition.find()){
            return condition.group(0);
        }
        return null;
    }
}
